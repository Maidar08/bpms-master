import { OverlayRef } from '@angular/cdk/overlay';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewEncapsulation } from '@angular/core';
import { CaseViewSandboxService } from '../../../case-view-page/case-view-sandbox.service';
import { FormServiceSandboxService } from '../../../case-view-page/form-service-sandbox.service';
import { TaskItem, TaskModel } from '../../../case-view-page/model/task.model';
import { FormRelationService } from '../../../case-view-page/services/form-relation.service';
import { Document, FieldOptions, FormsModel } from '../../../models/app.model';
import { BranchBankingSandbox } from '../../services/branch-banking-sandbox.service';

import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Observable, forkJoin, from } from 'rxjs';
import { CLOSE_MN } from '../../../case-view-page/model/common.constants';
import { CLOSE } from '../../../case-view-page/model/task-constant';
import { CommonSandboxService } from '../../../common/common-sandbox.service';
import { ErinCustomDialogComponent } from '../../../common/erin-custom-dialog/erin-custom-dialog.component';
import { FileService } from '../../../common/service/file-service';
import { AuthenticationSandboxService } from '../../../erin-aim/authentication/authentication-sandbox.service';
import { DialogSandboxService } from '../../../services/sandbox/dialog-sandbox.service';
import {
  ACCOUNT_HAS_NOT_ACCESS_MESSAGE,
  ACCOUNT_INFO_FIELDS,
  ACCOUNT_REFERENCE,
  ACCOUNT_REFERENCE_FIELDS,
  CHECK_REQUIRED_FIELDS_ACTION,
  CONFIRM_DIALOG_BUTTONS,
  CONTRACT_TYPE,
  CUSTOM_PAY,
  FORGOT_PASSWORD,
  LIST_EMPTY_MESSAGE,
  LOAN_REPAYMENT_WARNING,
  LOAN_REPAYMENT_WARNING_ON_TIME,
  LOAN_REPAYMENT_WARNING_OVER_DUE,
  OTP_VERIFICATION_DIALOG_FIELD,
  RECOVER_RIGHTS,
  RECOVER_USER_RIGHT_CONFIRM_MESSAGE,
  REMOVE_RIGHTS,
  REMOVE_USER_RIGHT_CONFIRM_MESSAGE,
  SALARY_PACKAGE_TRANSACTION,
  SAVE_USSD_INFO,
  TASKS_WITH_NOT_TRANSACTION,
  TASK_DEF_ACCOUNT_REFERENCE,
  TASK_DEF_BILLING_CUSTOM_PAY,
  TASK_DEF_BILLING_CUSTOM_PAY_CHILD,
  TASK_DEF_BILLING_TAX_PAY_CHILD,
  TASK_DEF_DEPOSIT_CONTRACT,
  TASK_DEF_E_TRANSACTION_DOCUMENT,
  TASK_DEF_FUTURE_MILLIONARE,
  TASK_DEF_LOAN_REPAYMENT,
  TASK_DEF_SALARY_PACKAGE_TRANSACTION,
  TASK_DEF_TRANSACTION_DOCUMENT,
  TAX_PAY,
  TRANSACTION_FORM_TYPE,
  UNBLOCK,
  UNBLOCK_USER_CONFIRM_MESSAGE,
  UPDATE_PASSWORD_CONFIRM_MESSAGE,
  USSD_EMPTY_MAIN_ACCOUNT_WARNING,
  USSD_PHONE_NUMBER_EMPTY_MESSAGE,
} from '../../model/branch-banking-constant';
import { VerifyOtpDialogComponent } from '../verify-otp-dialog/verify-otp-dialog.component';
import {CONFIRM_PERMISSION_FOR_USSD} from "../../../loan-search-page/models/process-constants.model";


@Component({
  selector: 'branch-banking-workspace',
  template: `
    <div class="workspace-container">

      <div class="workspace">
        <!-- Sub task form section-->
        <div class="task-form" *ngIf="isDataFull">
          <dynamic-form-container [data]="fieldGroups" [task]="task" [form]="form" [tableData]="getTableValue()" [highlightRow]="highlightRow"
                                  [errorCheckHeaderCol]="errorHeaderCol" [errorCheckColVal]="errorColVal"
                                  (tableLength)="setWorkspaceStyle($event)" (actionEmitter)="handleAction($event)"
                                  (fieldActionListener)="handleFieldAction($event)"
                                  [isRoundup]="false"></dynamic-form-container>
        </div>
        <!-- Empty message section-->
        <p *ngIf="!isDataFull" class="mat-title error-table-text">АЖЛЫН ТАЛБАР ХООСОН БАЙНА</p>
        <iframe [src]="iframeURL" id="iframeElement" style="display: none"></iframe>
      </div>
    </div>
  `,
  styleUrls: ['./branch-banking-workspace.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class BranchBankingWorkspaceComponent implements OnInit, OnChanges {

  constructor(private branchBankingService: BranchBankingSandbox, private dialogService: DialogSandboxService,
              private dialog: MatDialog, private relationService: FormRelationService, private commonService: CommonSandboxService,
              private sb: CaseViewSandboxService, private formSb: FormServiceSandboxService, private fileService: FileService,
              private auth: AuthenticationSandboxService,
              private sanitizer: DomSanitizer) {
  }

  @Input() instanceId: string;
  @Output() saveActivated = new EventEmitter<any>();
  @Output() expandWorkspace = new EventEmitter();

  groupId: string = null;
  tableDataMap = {};
  tableHeader: string;
  errorHeaderCol;
  errorColVal: any[];
  selectedTableRow;
  highlightRow = false;
  isDataFull = false;
  parentTaskDefKey: string;
  form: FormsModel[] = [];
  loanRepaymentInfo;
  loanRepaymentIni;
  fieldGroups;
  successTransaction = false;
  iframeURL: SafeResourceUrl;
  task: TaskItem = {
    taskId: null,
    parentTaskId: null,
    name: null,
    executionId: null,
    executionType: null,
    icon: null,
    instanceId: null,
    definitionKey: null
  };
   ussdMainAccounts: any;
   ussdAllAccounts: any;
   previousTaskDefKey: string;
   IS_CHECKED_SALARY_ACCOUNTS = 'isCheckedSalaryAccounts';

  private static setSession(taskId: string, taskName: string) {
    sessionStorage.removeItem('taskId');
    sessionStorage.removeItem('taskName');
    sessionStorage.setItem('taskId', taskId);
    sessionStorage.setItem('taskName', taskName);
  }

  private static isCheckRequiredFieldAction(functionName: string): boolean {
    return !CHECK_REQUIRED_FIELDS_ACTION.includes(functionName);
  }

  ngOnChanges(changes: SimpleChanges): void {
    const idSession = sessionStorage.getItem('taskId');
    if (null != idSession) {
      sessionStorage.removeItem('taskId');
      this.getDynamicForms(idSession);
    } else {
      this.getDynamicForms();
    }
  }

  ngOnInit(): void {
    this.auth.getCurrentUserGroup().subscribe(res => this.groupId = res);
    if (null != localStorage.getItem('currentTaskData') && null != JSON.parse(localStorage.getItem('currentTaskData'))) {
      const currentTaskData = JSON.parse(localStorage.getItem('currentTaskData'));
      this.form = currentTaskData.form != null ? currentTaskData.form : this.form;
      this.tableDataMap = currentTaskData.tableData != null ? currentTaskData.tableData : this.tableDataMap;
    }
    localStorage.clear();
  }

  getDynamicForms(taskId?: string, fromEnable?: boolean) {
    this.isDataFull = false;
    this.previousTaskDefKey = this.task.definitionKey;
    this.sb.getActiveTasks(this.instanceId).subscribe(taskList => {
      if (taskList.length > 0) {
        this.setTask(taskId, taskList);
        if (this.task != null) {
          taskId = this.task.taskId;
          this.parentTaskDefKey = undefined;
        }
        const overlayRef = this.commonService.setOverlay();
        this.sb.switchActiveTasks(taskId ? taskId : taskList[0].taskId, this.instanceId, this.task.definitionKey).subscribe(res => {
          this.handleResponse(res, fromEnable, overlayRef);
        }, error => {
          if (error) {
            this.isDataFull = false;
          }
          overlayRef.dispose();
        });
      } else {
        localStorage.setItem('currentTaskData', null);
      }
    });
  }

  getTableValue() {
    if (null == this.task || null == this.task.definitionKey) {
      return [];
    }
    return this.tableDataMap[this.task.definitionKey];
  }

  setWorkspaceStyle(length: number) {
    this.expandWorkspace.emit(length > 8);
  }

  handleAction(event: any): void {
    const overlayRef = (event.action == null || event.action === 'selectTableRow') ? null : this.commonService.setOverlay();
    if ((BranchBankingWorkspaceComponent.isCheckRequiredFieldAction(event.action)
      && this.commonService.isMandatoryField(this.form, overlayRef))) {
      return;
    }
    this.callFunctionByName(event.action, event.data, overlayRef);
  }

  handleFieldAction(fieldId: string) {
    if (!TASKS_WITH_NOT_TRANSACTION.includes(this.task.definitionKey)) {
      if (this.task.definitionKey === TASK_DEF_SALARY_PACKAGE_TRANSACTION && fieldId === 'accountIdEnter') {
        this.fieldGroups.buttons['transaction'] = true;
      } else if (fieldId === 'transactionType' || fieldId === 'accountNumber') {
        const transactionType = this.commonService.getFormValue(this.form, 'transactionType');
        this.fieldGroups.buttons['transaction'] = (transactionType === 'Бэлэн бус');
      }
    }
    if (null != this.fieldGroups.fieldAction) {
      const functionName = this.fieldGroups.fieldAction[fieldId];
      if (null != functionName) {
        this.callFunctionByName(functionName, null, this.commonService.setOverlay());
      }
    }
  }

  setButtonInitialState() {
    this.fieldGroups.buttons['unblock'] = true;
    this.fieldGroups.buttons['removeRights'] = true;
    this.fieldGroups.buttons['recoveryRights'] = true;
  }

  changeButtonState() {
    this.setButtonInitialState();
    const stateValue = this.commonService.getFormValue(this.form, 'customerStatus');
    const isPrimary = this.commonService.getFormValue(this.form, 'ussdPhoneNumber');
    switch (stateValue) {
      case 'Блоклогдсон':
        this.fieldGroups.buttons['unblock'] = false;
        break;
      case 'Нээлттэй':
        this.fieldGroups.buttons['removeRights'] = false;
        break;
      case 'Хаалттай':
        this.fieldGroups.buttons['recoveryRights'] = false;
        break;
    }
    if (isPrimary === '') {
      this.fieldGroups.buttons['forgetPassword'] = true;
      this.fieldGroups.buttons['unblock'] = true;
      this.fieldGroups.buttons['removeRights'] = true;
      this.fieldGroups.buttons['recoveryRights'] = true;
    } else {
      this.fieldGroups.buttons['forgetPassword'] = false;
    }
  }

  printIframeElement(base64File: string, overlayRef: OverlayRef) {
    const iframeElement = document.getElementById('iframeElement') as HTMLIFrameElement;
    const data = this.fileService.base64ToArrayBuffer(base64File);
    const blob = new Blob([data], {type: 'application/pdf'});
    this.iframeURL = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));
    const blobUrl = window.URL.createObjectURL(blob);
    iframeElement.addEventListener('load', () => {
      iframeElement.contentWindow.print();
      window.URL.revokeObjectURL(blobUrl);
      overlayRef.dispose();
    });
  }

  private printDocument(overlayRef: OverlayRef) {
    const observables: Observable<any>[] = [];
    observables.push(from(this.branchBankingService.downloadDocumentByType(this.instanceId, this.getTransactionDocumentType(),
      this.getDocumentParameters(overlayRef))));
    forkJoin(...observables).subscribe(res => {
      this.printIframeElement(res[0].source, overlayRef);
    }, () => {
      overlayRef.dispose();
    });
  }

  private showWarningBeforeCalculate(field) {
    if (field.formFieldValue.defaultValue === 'Хуваарийн бус') {
      this.commonService.showSnackBar(LOAN_REPAYMENT_WARNING, CLOSE, 5000);
    }
  }

  private showWarningAfterCalculate(field) {
    if (field.formFieldValue.defaultValue === this.loanRepaymentIni) {
      return;
    } else {
      switch (field.formFieldValue.defaultValue) {
        case 'Хуваарийн бус':
          this.commonService.showSnackBar(LOAN_REPAYMENT_WARNING_ON_TIME, CLOSE, 5000);
          break;
        case 'Хуваарийн дагуу':
          this.commonService.showSnackBar(LOAN_REPAYMENT_WARNING_OVER_DUE, CLOSE, 5000);
          break;
        default:
          break;
      }
    }
  }

  private setScheduleLoanRepay() {
    const scheduled = this.loanRepaymentInfo['scheduled'];
    const scheduleOptions: FieldOptions[] = this.commonService.getFieldOptions(this.form, 'loanRepaymentType');

    const loanRepaymentType = scheduled > 0 ? scheduleOptions[0].value : scheduleOptions[1].value;
    this.commonService.setFieldDefaultValue(this.form, {loanRepaymentType});
    this.loanRepaymentIni = this.commonService.getFormValue(this.form, 'loanRepaymentType');
    this.relationService.updateForm('loanRepaymentType');
  }

  private handleLoanInfoOnRepayment(overlayRef) {
    const field = this.form.find(formField => formField.id === 'loanRepaymentType');
    if (this.loanRepaymentInfo == null) {
      this.showWarningBeforeCalculate(field);
    } else if (field.formFieldValue.defaultValue === 'Хуваарийн бус') {
      const nonScheduledPayment: any = this.loanRepaymentInfo['nonScheduledPayment'];
      this.commonService.setFieldDefaultValue(this.form, {
        totalAmount: nonScheduledPayment['totalAmount'], payLoanAmount: nonScheduledPayment['totalAmount'],
        penaltyAmount: nonScheduledPayment['penaltyAmount'],
        interestPayment: nonScheduledPayment['interestPayment'], basicPayment: '', feePayment: ''
      });
    } else if (field.formFieldValue.defaultValue === 'Хуваарийн дагуу') {
      const scheduledPayment: any = this.loanRepaymentInfo['scheduledPayment'];
      this.commonService.setFieldDefaultValue(this.form, {
        basicPayment: scheduledPayment['basicPayment'],
        interestPayment: scheduledPayment['interestPayment'],
        totalAmount: scheduledPayment['totalAmount'], payLoanAmount: scheduledPayment['totalAmount'],
        feePayment: '', penaltyAmount: ''
      });
    } else if (field.formFieldValue.defaultValue === 'Зээл хаах') {
      const closingLoanPayment: any = this.loanRepaymentInfo['closingLoanPayment'];
      this.commonService.setFieldDefaultValue(this.form, {
        feePayment: closingLoanPayment['feePayment'],
        interestPayment: closingLoanPayment['interestPayment'],
        totalAmount: closingLoanPayment['totalAmount'], payLoanAmount: closingLoanPayment['totalAmount'],
        penaltyAmount: closingLoanPayment['penaltyAmount'],
        basicPayment: ''
      });
    }
    const totalAmount = this.commonService.getFormValue(this.form, 'totalAmount');
    this.commonService.setFieldDefaultValue(this.form, {payLoanAmount: totalAmount});
    overlayRef.dispose();
  }

  private openFileViewer(documents: Document[], isDownloadableFile: boolean) {
    const parentURL = this.sb.getParentUrl();
    const componentUrl = parentURL + 'branch-banking/' + this.instanceId;
    this.fileService.showFileByPlainBase64(documents, parentURL, this.instanceId, isDownloadableFile, componentUrl, 'branch-banking');
  }

  private setDefaultData(fromEnable: boolean) {
    let sessionInstanceId = null;
    localStorage.setItem('showTable', String(false));
    if (localStorage.getItem('instanceId')) {
      sessionInstanceId = localStorage.getItem('instanceId');
    } else {
      localStorage.setItem('instanceId', this.instanceId);
    }
    const fromSubTask = Boolean(localStorage.getItem('fromSubTask'));
    const toSubTask = Boolean(localStorage.getItem('toSubTask'));
    const showTable = Boolean(localStorage.getItem('showTable'));
    if (!fromSubTask && toSubTask) {
      this.tableHeader = '';
      this.form = [];
    }
    if ((!showTable && this.isTransactionDocumentTask()) || ((fromSubTask || toSubTask) && sessionInstanceId !== this.instanceId)
      || fromEnable) {
      this.tableDataMap = {};
    }
  }

  private isTransactionDocumentTask(): boolean {
    return this.task.definitionKey.includes('transaction');
  }

  private setFormRelation(form) {
    this.relationService.setForm(form, this.task.definitionKey).then(() => {
      this.handleFormRelation();
    });
  }

  private handleResponse(res: TaskModel, fromEnable: boolean, overlayRef: OverlayRef, isCompletedTask?: boolean) {
    this.setDefaultData(fromEnable);
    this.getFormGroup(res, isCompletedTask).then(() => overlayRef.dispose());
    this.setFormRelation(res.taskFormFields);
    this.form = res.taskFormFields;
    if (this.previousTaskDefKey !== null && this.previousTaskDefKey.includes('custom') && this.task.definitionKey.includes('tax')
      || this.previousTaskDefKey != null && this.previousTaskDefKey.includes('tax') && this.task.definitionKey.includes('custom')) {
      this.clearPreviousTaskVariables();
    }
    this.clearPreviousTaskVariables();
    BranchBankingWorkspaceComponent.setSession(res.taskId, res.formId);
  }

  private clearPreviousTaskVariables(): void {
    if (this.previousTaskDefKey != null) {
      if (!this.task.definitionKey.includes('child')) {
        if (this.previousTaskDefKey !== this.task.definitionKey) {
          this.commonService.setFieldDefaultValue(this.form, {
            accountNumber: '', transactionNumber: '', accountId: '', accountIdEnter: '',
            registerId: '', registerNumber: '', customerName: '',
            accountName: null, accountCurrency: '', accountBalance: 0,
            phoneNumber: null, transactionCcy: '', transactionDescription: '', taxPayerName: ''
          });
        }
      }
    }
  }

  private handleFormRelation() {
    this.nonCashStateButtonRelation();
    if (this.task.definitionKey === TASK_DEF_BILLING_TAX_PAY_CHILD) {
      this.relationService.updateForm('payFull');
    }
  }

  private nonCashStateButtonRelation() {
    if (this.task.definitionKey === TASK_DEF_ACCOUNT_REFERENCE) {
      const accountName = this.commonService.getFormValue(this.form, 'accountName');
      const feesAmount = this.commonService.getFormValue(this.form, 'feesAmount');
      const convertedFeesAmount = this.commonService.formatNumberService(feesAmount);
      if (accountName && convertedFeesAmount > 0) {
          this.fieldGroups.buttons['transaction'] = false;
      }
    }
  }

  private async getFormGroup(res: TaskModel, isCompletedTask: boolean) {
    this.fieldGroups = await this.formSb.getForm(this.task, res.taskFormFields, 'branch-banking');
    this.isDataFull = null != this.fieldGroups;
    if (isCompletedTask) {
      for (const buttonId in this.fieldGroups.buttons) {
        if (this.fieldGroups.buttons.hasOwnProperty(buttonId)) {
          this.fieldGroups.buttons[buttonId] = true;
        }
      }
    }
    this.disableFormAndButtonByTaskDef(res.taskFormFields);
  }

  private getTtdOptions(overlayRef) {
    const searchType = this.commonService.getFormValue(this.form, 'searchType');
    const searchValue = this.commonService.getFormValue(this.form, 'searchValueTax');
    if (searchType === 'Регистрийн дугаар') {
      this.branchBankingService.getTtdOptions(encodeURI(searchValue), this.instanceId).subscribe(res => {
        if (null != res && res.length > 0) {
          this.commonService.setFieldDefaultValue(this.form, {taxNumber: res[0].id});
          this.commonService.setFieldOptions(this.form, res, 'taxNumber');
        }
        overlayRef.dispose();
      }, () => overlayRef.dispose(), () => null);
    } else {
      overlayRef.dispose();
    }
  }

  private getTaxInvoiceList(overlayRef) {
    this.branchBankingService.getTaxInvoiceList(this.form, this.instanceId).subscribe(res => {
      this.tableDataMap[this.task.definitionKey] = res;
      this.commonService.setFieldDefaultValue(this.form, res[0]);
      overlayRef.dispose();
    }, () => overlayRef.dispose());
  }

  private getCustomInvoiceList(overlayRef) {
    this.branchBankingService.getCustomInvoiceList(this.form, this.instanceId).subscribe(res => {
      this.tableDataMap[this.task.definitionKey] = res;
      overlayRef.dispose();
    }, () => overlayRef.dispose());
  }

  private submit(data, overlayRef, haveNextMethod: boolean) {
    const requiredFields = this.commonService.getEmptyRequiredFieldsOfForm(this.form);
    return new Promise(resolve => {
      if (data['action'] === 'complete') {
        if (requiredFields.length > 0) {
          requiredFields.forEach(field => {
            field.formFieldValue.defaultValue = ' ';
          });
        }
      }
      this.sb.submitThenCallUserTask(this.task.taskId, this.task.instanceId, null, this.form, data)
        .subscribe((res) => {
          if (data['action'] === 'complete') {
            this.form = [];
            this.tableDataMap[this.task.definitionKey] = [];
            overlayRef.dispose();
            resolve(res);
          }
          // this.saveActivated.emit({save: true, relatedTaskId: null});
          this.setSubTableData(data);
          if (data.value != null) {
            this.tableDataMap[this.task.definitionKey] = data.value;
          }
          this.successTransaction = true;
          if (!haveNextMethod) {
            this.callRelatedTask(overlayRef);
          }
          overlayRef.dispose();
          resolve(res);
        }, (error) => {
          if (null != error) {
            this.commonService.showSnackBar(error, 'ХААХ', null);
          }
          this.successTransaction = false;
          this.setErrorMessageOnFailedTransactionField(error);
          overlayRef.dispose();
          resolve(null);
        });
    });
  }

  private setErrorMessageOnFailedTransactionField(errorMessage: string): void {
    if (null == errorMessage) {
      return;
    }
    const msgString: string[] = errorMessage.split('message:');
    if (this.task.definitionKey === 'user_task_branch_banking_salary_package' && msgString.length > 1) {
      this.commonService.setFieldDefaultValue(this.form, {invalidAccounts: msgString[1]});
    }
  }

  private callRelatedTask(overlayRef: OverlayRef): void {
    if (this.task.parentTaskId != null) {
      this.saveActivated.emit({save: true, relatedTaskId: this.task.parentTaskId});
    } else {
      this.saveActivated.emit({save: true, relatedTaskId: null});
    }
    overlayRef.dispose();
  }

  private makeTransaction(data, overlayRef) {
    const config = this.setDialogConfig(CONFIRM_DIALOG_BUTTONS, 'Гүйлгээ хийхдээ итгэлтэй байна уу?', '500px');
    this.dialogService.openCustomDialog(ErinCustomDialogComponent, config).then((res: any) => {
      if (null != res && null != res.action && res.action.actionId === 'confirm') {
        const observables: Observable<any>[] = [];
        this.setGroupIdToFormField();
        if (null != this.tableDataMap[this.task.definitionKey]) {
          data['transactionTable'] = this.tableDataMap[this.task.definitionKey];
        }
        observables.push(from(this.submit(data, overlayRef, true)));
        forkJoin(...observables).subscribe(() => {
          if (this.successTransaction) {
            localStorage.removeItem(this.IS_CHECKED_SALARY_ACCOUNTS);
            this.commonService.showSnackBar('Гүйлгээ амжилттай хийгдлээ.', null, 3000);
            this.formButtonRelation();
          }
        });
      } else {
        overlayRef.dispose();
      }
    });
  }

  private formButtonRelation() {
    const taskDefKey = this.task.definitionKey;
    if (taskDefKey === TASK_DEF_ACCOUNT_REFERENCE) {
      this.fieldGroups.buttons['download'] = false;
    }
    if (taskDefKey === TASK_DEF_SALARY_PACKAGE_TRANSACTION) {
      this.form.forEach(field => {
        field.validations.push({name: 'readonly', configuration: null});
        field.disabled = true;
      });
      this.fieldGroups.buttons['transaction'] = true;
    }
    if (taskDefKey !== TASK_DEF_SALARY_PACKAGE_TRANSACTION && taskDefKey !== TASK_DEF_ACCOUNT_REFERENCE) {
      this.tableDataMap = {};
    }
    this.downloadDocumentByType(this.commonService.setOverlay(), true, 'transactionDocument');
  }

  private setGroupIdToFormField(): void {
    this.form
      .push({
        id: 'groupId', type: 'string', formFieldValue: {defaultValue: this.groupId}, options: [], label: null, validations: [], required: false
      });
  }

  private disableFormAndButtonByTaskDef(form: FormsModel[]): void {
    const action = this.commonService.getFormValue(form, 'action');
    if (this.task.definitionKey === TASK_DEF_SALARY_PACKAGE_TRANSACTION && action === 'continue' && !this.previousTaskDefKey === null) {
      this.form.forEach(field => {
        field.validations.push({name: 'readonly', configuration: null});
        field.disabled = true;
      });
      for (const button in this.fieldGroups.buttons) {
        if (button !== 'complete') {
          this.fieldGroups.buttons[button] = true;
        }
      }
    }
    if (this.task.definitionKey === TASK_DEF_SALARY_PACKAGE_TRANSACTION || this.task.definitionKey === TASK_DEF_LOAN_REPAYMENT) {
      this.fieldGroups.buttons['transaction'] = true;
    }
    // todo hide editButton
    if(this.task.definitionKey === 'user_task_branch_banking_ussd'){
      this.auth.authPermission$.subscribe(source => {
        const permission = source.find(value => value.id === CONFIRM_PERMISSION_FOR_USSD);
        if(permission != null){
          this.form.forEach(field => {
            if (field.id === 'editButton') {
              field.disabled = true;
              field.type = null;
              field.label = null;
              field.formFieldValue.defaultValue = null;
              field.id = 'spaceNull';
            }
          });
        }
      });
    }
  }

  private getTransactionDocumentType(documentType?: string): string {
    switch (this.task.definitionKey) {
      case TASK_DEF_BILLING_TAX_PAY_CHILD :
        return TAX_PAY;
      case TASK_DEF_BILLING_CUSTOM_PAY:
        return CUSTOM_PAY;
      case TASK_DEF_BILLING_CUSTOM_PAY_CHILD:
        return CUSTOM_PAY;
      case TASK_DEF_TRANSACTION_DOCUMENT:
      case TASK_DEF_E_TRANSACTION_DOCUMENT:
        return this.commonService.getSelectedFieldOptionIdByFieldId(this.form, TRANSACTION_FORM_TYPE);
      case TASK_DEF_FUTURE_MILLIONARE:
      case TASK_DEF_DEPOSIT_CONTRACT:
        return this.commonService.getSelectedFieldOptionIdByFieldId(this.form, CONTRACT_TYPE);
      case TASK_DEF_ACCOUNT_REFERENCE:
        if (documentType === 'transactionDocument') {
          return this.isCashTransaction(this.form) ? 'addCashTransactionReceipt' : 'nonCashTransactionReceipt';
        }
        return ACCOUNT_REFERENCE;
      case TASK_DEF_LOAN_REPAYMENT:
        return 'nonCashTransactionReceipt';
      case TASK_DEF_SALARY_PACKAGE_TRANSACTION:
        return SALARY_PACKAGE_TRANSACTION;
      default:
        return null;
    }
  }

  private isCashTransaction(form: FormsModel[]): boolean {
    return this.commonService.getFormValue(form, 'transactionType') === 'Бэлэн';
  }

  private downloadDocumentByType(overlayRef, isDownloadableFile?: boolean, documentType?: string) {
    this.branchBankingService.downloadDocumentByType(this.instanceId, this.getTransactionDocumentType(documentType),
      this.getDocumentParameters(overlayRef))
      .subscribe(resDocument => {
        localStorage.setItem('currentTaskData', JSON.stringify({form: this.form, tableData: this.tableDataMap}));
        if (Array.isArray(resDocument)) {
          this.openFileViewer(resDocument, isDownloadableFile);
        } else {
          const documents: Document[] = [];
          documents.push(resDocument);
          this.openFileViewer(documents, isDownloadableFile);
        }
        overlayRef.dispose();
      }, () => {
        overlayRef.dispose();
      });
  }

  private downloadDocument(overlayRef, action?: string) {
    const observables: Observable<any>[] = [];
    this.setGroupIdToFormField();
    const haveNextMethod = action === 'showTemplate' || action === 'download';
    observables.push(from(this.submit({action: 'back'}, overlayRef, haveNextMethod)));
    forkJoin(...observables).subscribe(() => {
      const overlay = this.commonService.setOverlay();
      this.downloadDocumentByType(overlay, action !== 'showTemplate');
    });
  }

  private getDocumentParameters(overlay: OverlayRef): any {
    let params = {};
    switch (this.task.definitionKey) {
      case TASK_DEF_TRANSACTION_DOCUMENT:
      case  TASK_DEF_E_TRANSACTION_DOCUMENT:
        const transactionNumber = this.commonService.getFormValue(this.form, 'transactionNo');
        if (this.getTableValue() == null) {
          if (transactionNumber == null || transactionNumber === '') {
            overlay.dispose();
            this.commonService.showSnackBar(LIST_EMPTY_MESSAGE, CLOSE_MN, 3000);
            return;
          } else {
            this.convertFormDataToObject(params);
          }
        } else {
          if (this.getTableValue().length === 0) {
            if (transactionNumber == null || transactionNumber === '') {
              overlay.dispose();
              this.commonService.showSnackBar(LIST_EMPTY_MESSAGE, CLOSE_MN, 3000);
              return;
            } else {
              this.convertFormDataToObject(params);
            }
          } else {
            if (this.selectedTableRow == null) {
              this.selectedTableRow = this.getTableValue()[0];
            }
            params = this.commonService.clone(this.selectedTableRow);
            this.convertFormDataToObject(params);
          }
        }
        break;
      case TASK_DEF_ACCOUNT_REFERENCE:
        const jointHoldersCif = [];
        if (this.tableDataMap.hasOwnProperty(TASK_DEF_ACCOUNT_REFERENCE)) {
          this.getTableValue().forEach(row => {
            if (row.checked) {
              jointHoldersCif.push(row.jointHolderId);
            }
          });
        }
        params = this.commonService.getFormFieldsAsMap(this.form, params);
        const jointHolders = 'jointHolders';
        params[jointHolders] = jointHoldersCif.join(',');
        break;
      case TASK_DEF_LOAN_REPAYMENT:
        params = this.commonService.getFormFieldsAsMap(this.form, params);
        params['loanRepaymentTask'] = 'loanRepayment';
        break;
      default:
        params = this.commonService.getFormFieldsAsMap(this.form, params);
    }
    return params;
  }

  private convertFormDataToObject(params) {
    for (const field of this.form) {
      if (!params.hasOwnProperty(field.id)) {
        params[field.id] = field.formFieldValue.defaultValue;
      }
    }
  }

  private setSubTableData(data: any) {
    if (data.action && data.action === 'back') {
      return;
    }
    for (const tableRowData of Object.values(data)) {
      if (Array.isArray(tableRowData) && null != this.task.parentTaskId) {
        this.tableDataMap[this.task.parentTaskId] = tableRowData;
      }
    }
  }

  private getAccountInfo(overlayRef) {
    let accountNumber = this.commonService.getFormValue(this.form, 'accountNumber');
    if (accountNumber == null && this.task.definitionKey === TASK_DEF_SALARY_PACKAGE_TRANSACTION) {
      accountNumber = this.commonService.getFormValue(this.form, 'accountIdEnter');
    }
    this.branchBankingService.getAccountInfo(accountNumber, this.instanceId).subscribe((res: any) => {
      this.commonService.setFieldDefaultValue(this.form, res);
      if (res) {
        if (res.accountHasAccess === '0') {
          this.commonService.showSnackBar(ACCOUNT_HAS_NOT_ACCESS_MESSAGE, CLOSE, 30000);
        }
        this.fieldGroups.buttons['transaction'] = false;
      }
      overlayRef.dispose();
    }, () => {
      this.commonService.removeFieldsDefaultValue(this.form, ACCOUNT_INFO_FIELDS);
      overlayRef.dispose();
    });
  }

  private getUssdInfo(overlayRef, form) {
    this.relationService.updateForm('mainAccounts');
    this.branchBankingService.getUssdInfo(form, this.instanceId).subscribe((res: any) => {
      if (res) {
        this.commonService.setFieldDefaultValue(this.form, res);
        this.ussdMainAccounts = res.mainAccounts;
        this.ussdAllAccounts = res.allAccounts;
        const mainAccount = this.form.find(f => f.id === 'mainAccount');
        const isRegistered = this.commonService.getFormValue(form, 'checkRegistration');
        const accountOptions: FieldOptions[] = [];
        for (const account of this.ussdMainAccounts) {
          if (account['isPrimary']) {
            mainAccount.formFieldValue.defaultValue = account.accountNumber;
          }
          if (!mainAccount.options.find(op => op.value === account.accountNumber)) {
            accountOptions.push({id: account.accountNumber, value: account.accountNumber});
          }
        }
        if (res.mainAccounts.length === 0) {
          this.commonService.setFieldDefaultValue(this.form, {
            mainAccount: ''});
          this.commonService.showSnackBar(USSD_EMPTY_MAIN_ACCOUNT_WARNING, CLOSE, 5000);
          mainAccount.validations.push({name: 'readonly', configuration: null});
        }
        mainAccount.options = accountOptions;
        // mainAccount.options = accountOptions;
        // this.commonService.setFieldOptions(this.form, accountOptions, 'mainAccount');
        if (isRegistered === false) {
          mainAccount.formFieldValue.defaultValue = null;
        }
        this.changeButtonState();
        this.relationService.updateForm('checkRegistration');
      }
      overlayRef.dispose();

    }, () => {
      overlayRef.dispose();
    });
  }

  private calculateLoanRepayment(overlayRef) {
    this.branchBankingService.calculateLoanRepayment(this.form, this.instanceId).subscribe((res: any) => {
      this.commonService.setFieldDefaultValue(this.form, res);
      this.loanRepaymentInfo = res;
      this.setScheduleLoanRepay();
      this.handleLoanInfoOnRepayment(overlayRef);
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });

  }


  private getAccountReference(overlayRef) {
    const accountId = this.commonService.getFormValue(this.form, 'accountIdEnter');
    this.branchBankingService.getAccountReference(accountId, this.instanceId).subscribe((res: any) => {
        this.commonService.setFieldDefaultValue(this.form, res);
        this.fieldGroups.buttons['download'] = this.commonService.getFormValue(this.form, 'feesAmount') > 0;
        this.tableDataMap[this.task.definitionKey] = res.accountJointHolders;
        this.commonService.setFieldDefaultValue(this.form, {fee: res.feesAmount});
        if (res.accountHasAccess === '0') {
          this.commonService.showSnackBar(ACCOUNT_HAS_NOT_ACCESS_MESSAGE, CLOSE, 30000);
        }
        overlayRef.dispose();
      }, () => {
        this.commonService.removeFieldsDefaultValue(this.form, ACCOUNT_REFERENCE_FIELDS);
        overlayRef.dispose();
      }
    );
  }

  private getCustomerTransactionList(overlayRef) {
    const transactionDate = this.commonService.getFormValue(this.form, 'transactionDate');
    this.branchBankingService.getCustomerTransactions(transactionDate, this.instanceId).subscribe((res: any) => {
      this.tableDataMap[this.task.definitionKey] = res;
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });
  }

  private getETransactionList(overlayRef) {
    this.branchBankingService.getETransactionList(this.form, this.instanceId).subscribe((res: any) => {
      this.tableDataMap[this.task.definitionKey] = res;
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });
  }

  private setFeesAmountByCheckbox(checkboxIds: string[], overlayRef) {
    const checkbox1 = this.commonService.getFormValue(this.form, checkboxIds[0]);
    const checkbox2 = this.commonService.getFormValue(this.form, checkboxIds[1]);
    const obj = {};
    obj[checkboxIds[1]] = false;
    let feesAmount;
    if (checkbox1 && checkbox2) {
      this.commonService.setFieldDefaultValue(this.form, obj);
      this.setDefaultFeesAmount();
    }
    if (checkbox1) {
      feesAmount = this.commonService.getFormValue(this.form, 'feesAmount');
      if (null != feesAmount) {
        localStorage.setItem('feesAmount', feesAmount);
        this.commonService.setFieldDefaultValue(this.form, {feesAmount: this.getFeesAmountByCheckbox(checkboxIds[0])});
      }
    } else {
      this.setDefaultFeesAmount();
    }
    this.fieldGroups.buttons['download'] = this.commonService.getFormValue(this.form, 'feesAmount') !== 0;
    overlayRef.dispose();
  }

  private setDefaultFeesAmount() {
    const storageFeeAmount = localStorage.getItem('feesAmount');
    const feesAmount = storageFeeAmount == null ? this.commonService.getFormValue(this.form, 'fee') : storageFeeAmount;
    this.commonService.setFieldDefaultValue(this.form, {feesAmount});
  }

  private getFeesAmountByCheckbox(checkboxId) {
    if (checkboxId === 'reduceFees') {
      return this.commonService.getFormValue(this.form, 'feeConstant');
    }
    return 0;
  }

  private setTask(taskId: string, taskList: TaskItem[]): void {
    if (taskId == null && this.parentTaskDefKey != null) {
      const filteredTaskName = taskList.find(task => task.parentTaskId === this.parentTaskDefKey);
      if (filteredTaskName) {
        this.task = filteredTaskName;
      } else {
        this.task = taskList[0];
      }
    } else if (taskId == null) {
      this.task = taskList[0];
    } else {
      let filteredTask = taskList.find(task => task.taskId === taskId);
      // if task is submitted then taskId/executionId has changed. So instead use def key to get previous task id
      if (filteredTask == null) {
        const defKey = sessionStorage.getItem('defKey');
        filteredTask = taskList.find(task => task.definitionKey === defKey);
      }
      if (filteredTask) {
        sessionStorage.removeItem('defKey');
        this.task = filteredTask;
      } else {
        sessionStorage.removeItem('defKey');
        this.task = taskList[0];
      }
    }
  }

  private uploadFile(data: any[], overlayRef: OverlayRef) {
    for (const file of data) {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        const base64String = reader.result.toString().split(',').pop();
        this.sendUploadedFile(base64String, overlayRef);
      };
    }
  }

  private sendUploadedFile(contentAsBase64: string, overlayRef: OverlayRef) {
    this.branchBankingService.uploadFile(contentAsBase64).subscribe(res => {
      this.commonService.setFieldDefaultValue(this.form, res);
      this.tableDataMap[this.task.definitionKey] = res.tableData;
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });
  }

  private setDialogConfig(buttons, message, width): MatDialogConfig {
    const dialogData = {buttons, message};
    const config = new MatDialogConfig();
    config.width = width;
    config.data = dialogData;
    return config;
  }

  private openUssdConfirmDialog(action: any, confirmMessage: string, overlayRef: OverlayRef) {
    const config = this.setDialogConfig(CONFIRM_DIALOG_BUTTONS, confirmMessage, '500px');
    this.dialogService.openCustomDialog(ErinCustomDialogComponent, config).then((res: any) => {
      if (null != res.action && res.action.actionId === 'confirm') {
        action.actionId === 'recoveryRights' ? this.sendOtpCode(action, overlayRef) : this.changeUssdUserState(action, overlayRef);
      } else {
        overlayRef.dispose();
      }
    });
  }

  private openOtpVerificationDialog(action: any, overlayRef: OverlayRef) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      otpField: OTP_VERIFICATION_DIALOG_FIELD,
      form: this.form,
      instanceId: this.instanceId,
      dialogAction: action,
      title: 'Баталгаажуулах код оруулна уу ',
      mainAccounts: this.ussdMainAccounts,
      allAccounts: this.ussdAllAccounts,
      changeUssdState: () => {
        return this.changeUssdUserState(action, overlayRef);
      },
      refreshUssdInfo: () => {
        const ussdPhoneNo = this.commonService.getFormValue(this.form, 'ussdPhoneNumber');
        this.commonService.setFieldDefaultValue(this.form, {phoneNumber: ussdPhoneNo});
        return this.getUssdInfo(overlayRef, this.form);
      }
    };
    this.dialog.open(VerifyOtpDialogComponent, dialogConfig).afterClosed().subscribe(() => {
      overlayRef.dispose();
    });
  }

  changeUssdUserState(action, overlayRef) {
    const phone = this.commonService.getFormValue(this.form, 'ussdPhoneNumber');
    this.branchBankingService.changeUssdStateService(action, phone, this.instanceId).subscribe(response => {
      if (response.code) {
        this.commonService.showSnackBar(action.actionName + ' хүсэлт амжилттай боллоо.', null, 3000);
        this.getUssdInfo(overlayRef, this.form);
      } else {
        this.commonService.showSnackBar(action.actionName + ' хүсэлт амжилтгүй боллоо!', null, 3000);
      }
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });
  }

  private sendOtpCode(action, overlayRef: OverlayRef) {
    const destination = this.commonService.getFormValue(this.form, 'ussdPhoneNumber');
    if (destination === '') {
      this.commonService.showSnackBar(USSD_PHONE_NUMBER_EMPTY_MESSAGE, CLOSE_MN, 5000);
      overlayRef.dispose();
      return;
    }
    this.branchBankingService.sendOtpCode(this.instanceId, destination).subscribe(res => {
      if (res) {
        this.openOtpVerificationDialog(action, overlayRef);
      }
    }, () => {
      overlayRef.dispose();
    });
  }

  private callFunctionByName(functionName: string, data: any, overlayRef) {
    sessionStorage.setItem('defKey', this.task.definitionKey);
    switch (functionName) {
      case 'getTaxInvoiceList':
        this.getTaxInvoiceList(overlayRef);
        break;
      case 'getCustomInvoiceList':
        this.getCustomInvoiceList(overlayRef);
        break;
      case 'getTtdOptions':
        this.getTtdOptions(overlayRef);
        break;
      case 'toChildTask':
        localStorage.setItem('toSubTask', String(false));
        const properties = data.row;
        properties.caseInstanceId = this.instanceId;
        properties['action'] = 'toChildTask';
        this.submit(properties, overlayRef, false).then(() => undefined);
        break;
      case 'cancel':
        localStorage.setItem('fromSubTask', String(true));
        this.submit({action: 'back', value: this.getTableValue()}, overlayRef, false).then(() => undefined);
        break;
      case 'continue':
        localStorage.setItem('fromSubTask', String(false));
        this.submit({action: 'continue'}, overlayRef, false).then(() => undefined);
        break;
      case 'transaction':
        localStorage.setItem('fromSubTask', String(false));
        const isChecked = localStorage.getItem(this.IS_CHECKED_SALARY_ACCOUNTS);
        if ( this.task.definitionKey === TASK_DEF_SALARY_PACKAGE_TRANSACTION && (isChecked == null || undefined)) {
          this.commonService.showSnackBar('Нэр данс шалгана уу!', CLOSE, 5000);
          overlayRef.dispose();
        } else {
          this.makeTransaction({action: 'continue'}, overlayRef);
        }
        break;
      case 'print':
        this.printDocument(overlayRef);
        break;
      case 'download':
      case 'showTemplate':
        this.downloadDocument(overlayRef, functionName);
        break;
      case 'getAccountInfo' :
        this.getAccountInfo(overlayRef);
        break;
      case 'getUssdInfo' :
        this.getUssdInfo(overlayRef, this.form);
        break;
      case 'editUssdInfo' :
        this.commonService.removeFieldsValidation(this.form, ['ussdPhoneNumber', 'mainAccount']);
        overlayRef.dispose();
        break;
      case 'getAccountReference':
        this.getAccountReference(overlayRef);
        break;
      case 'getCustomerTransactions':
        this.getCustomerTransactionList(overlayRef);
        break;
      case 'getEtransactionList':
        this.getETransactionList(overlayRef);
        break;
      case 'selectTableRow':
        this.selectedTableRow = data.row;
        this.highlightRow = true;
        if (this.task.definitionKey === TASK_DEF_TRANSACTION_DOCUMENT) {
          this.commonService.setFieldDefaultValue(this.form, {transactionNo: data.row.transactionId});
        }
        break;
      case 'exemptFromFees':
        this.setFeesAmountByCheckbox(['exemptFromFees', 'reduceFees'], overlayRef);
        break;
      case 'reduceFees':
        this.setFeesAmountByCheckbox(['reduceFees', 'exemptFromFees'], overlayRef);
        break;
      case 'complete':
        localStorage.setItem('showTable', String(false));
        this.submit({action: 'complete'}, overlayRef, false).then(() => undefined);
        if (TASK_DEF_LOAN_REPAYMENT) {
          this.loanRepaymentInfo = null;
        }
        break;
      // salary package:
      case 'uploadFile':
        if (data.length > 0) {
          this.commonService.setFieldDefaultValue(this.form, {fileNameChips: data[0].name});
          this.uploadFile(data, overlayRef);
        } else {
          overlayRef.dispose();
        }
        break;
      case 'checkNameAndAccount':
        if (!this.tableDataMap.hasOwnProperty(TASK_DEF_SALARY_PACKAGE_TRANSACTION)) {
          this.commonService.showSnackBar('Файл оруулна уу!', CLOSE_MN, 5000);
          overlayRef.dispose();
          return;
        }
        this.branchBankingService.checkAccountsInfo(this.instanceId, this.tableDataMap[this.task.definitionKey]).subscribe(res => {
          this.tableDataMap[this.task.definitionKey] = res.accountInfo;
          this.checkAccountError(res);
          localStorage.setItem(this.IS_CHECKED_SALARY_ACCOUNTS, 'true');
          overlayRef.dispose();
        }, () => {
          overlayRef.dispose();
        });
        break;
      case 'forgetPassword':
        this.openUssdConfirmDialog(FORGOT_PASSWORD, UPDATE_PASSWORD_CONFIRM_MESSAGE, overlayRef);
        break;
      case 'recoveryRights':
        this.openUssdConfirmDialog(RECOVER_RIGHTS, RECOVER_USER_RIGHT_CONFIRM_MESSAGE, overlayRef);
        break;
      case 'removeRights':
        this.openUssdConfirmDialog(REMOVE_RIGHTS, REMOVE_USER_RIGHT_CONFIRM_MESSAGE, overlayRef);
        break;
      case 'unblock':
        this.openUssdConfirmDialog(UNBLOCK, UNBLOCK_USER_CONFIRM_MESSAGE, overlayRef);
        break;
      case 'save':
        this.sendOtpCode(SAVE_USSD_INFO, overlayRef);
        break;
      // loan repayment
      case 'calculateLoanRepayment':
        this.calculateLoanRepayment(overlayRef);
        break;
      case 'handleLoanInfoOnRepayment':
        const formField = this.form.find(field => field.id === 'loanRepaymentType');
        this.handleLoanInfoOnRepayment(overlayRef);
        if (this.loanRepaymentInfo != null) {
          this.showWarningAfterCalculate(formField);
        }
        break;
      case 'removeFile':
        this.tableDataMap = {};
        this.commonService.setFieldDefaultValue(this.form, {transactionCount: 0, transactionTotalAmount: 0});
        overlayRef.dispose();
        break;
      case 'confirmUSSD':
        this.confirmUSSD(overlayRef);
        break;
      case 'cancelUSSD':
        this.cancelUSSD(overlayRef);
        break;
      default:
        if (null != overlayRef) {
          overlayRef.dispose();
        }
        break;
    }
  }

  private checkAccountError(res) {
    if (res.invalidAccounts.length > 0) {
      this.errorHeaderCol = 'accountId';
      this.errorColVal = res.invalidAccounts;
      this.fieldGroups.buttons.transaction = true;
    } else {
      this.errorHeaderCol = null;
      this.errorColVal = [];
      this.fieldGroups.buttons.transaction = false;
    }
  }

  private confirmUSSD(overlayRef) {
    const id = this.form.find(f => f.id === "id");
    if (id == null){
      this.commonService.showSnackBar('Хэрэглэгч USSD сервис дээр бүртгэлгүй байна!', CLOSE, 3000);
      overlayRef.dispose();
    } else {
      this.branchBankingService.confirmUSSD(this.form, this.instanceId).subscribe(response => {
        if (response) {
          this.commonService.showSnackBar('USSD хэрэглэгч батлах амжилттай боллоо.', null, 3000);
        } else {
          this.commonService.showSnackBar('USSD хэрэглэгч батлах амжилтгүй боллоо!', null, 3000);
        }
        overlayRef.dispose();
      }, () => {
        overlayRef.dispose();
      });
    }
  }

  private cancelUSSD(overlayRef) {
    const id = this.commonService.getFormValue(this.form, 'id');
    if (id == null){
      this.commonService.showSnackBar('Хэрэглэгч USSD сервис дээр бүртгэлгүй байна!', CLOSE, 3000);
      overlayRef.dispose();
    } else {
      this.branchBankingService.cancelUSSD(this.form, this.instanceId).subscribe(response => {
        if (response) {
          this.commonService.showSnackBar('USSD хэрэглэгч цуцлах хүсэлт амжилттай боллоо.', null, 3000);
        } else {
          this.commonService.showSnackBar('USSD хэрэглэгч цуцлах хүсэлт амжилтгүй боллоо!', null, 3000);
        }
        overlayRef.dispose();
      }, () => {
        overlayRef.dispose();
      });
    }
  }
}
