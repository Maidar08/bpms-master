import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {FieldGroups, FieldOptions, FormsModel} from '../../../models/app.model';
import {TaskItem, TaskModel} from '../../model/task.model';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {OverlayRef} from '@angular/cdk/overlay';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {FormRelationService} from '../../services/form-relation.service';
import {FormServiceSandboxService} from '../../form-service-sandbox.service';
import {
  AMOUNT_OF_ASSESSMENT,
  CLOSE,
  CO_OWNER_INFORMATION,
  COL_REMOVE_DIALOG_MESSAGE,
  COLLATERAL_ACCOUNT,
  COLLATERAL_CONNECTION_RATE,
  COLLATERAL_FORMS,
  COLLATERAL_ID,
  COLLATERAL_LIST,
  COLLATERAL_REAL_ESTATE,
  CREATE_COLLATERAL,
  CREATE_COLLATERAL_LOAN_ACCOUNT,
  DOWNLOADKHUR,
  DOWNLOADKHUR_COBORROWER,
  FIDUCIARY_ASSET,
  FIRST_PAYMENT_DATE,
  FORM_FIELD_HINT,
  LOAN_ACCOUNT,
  LOAN_AMOUNT,
  LOAN_ATTACHMENT_CONTRACT,
  LOAN_CALCULATION,
  LOAN_PERMIT,
  LOAN_SEND,
  LOAN_TYPES,
  MONGOLBANK,
  MONGOLBANK_COBORROWER,
  ORDER,
  REMOVE_COBORROWER,
  SALARY_CALCULATION,
  SPECIAL_FORMS,
  STANDARD_HOUSING_VALUATION,
  TASK_DEF_COLLATERAL_LIST,
  TASK_DEF_CONSUMPTION_MONGOL_BANK,
  TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT,
  TASK_DEF_CREATE_LOAN_ACCOUNT,
  TASK_DEF_CREDIT_LINE_VIEW_COLLATERAL_LIST,
  TASK_DEF_MICRO_BALANCE_CALCULATION,
  TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT,
  TASK_DEF_MICRO_LOAN_CALCULATION,
  TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR,
  TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR_CO_BORROWER,
  TASK_DEF_MICRO_MONGOL_BANK,
  TASK_DEF_MICRO_MONGOL_BANK_EXTENDED,
  TASK_DEF_MICRO_MONGOL_BANK_NEW_CORE,
  TASK_DEF_MICRO_SIMPLE_CALCULATION,
  TASK_DEF_MORTGAGE_LOAN_CALCULATION,
  TASK_DEF_MORTGAGE_SALARY_CALCULATION,
  TASK_DEF_VIEW_COLLATERAL_LIST,
  TERM,
  WRONG_COLLATERAL_CONNECTION_RATE_VALUE_MESSAGE,
  WRONG_COLLATERAL_LOAN_AMOUNT_MESSAGE,
  DIGITAL_LOANS_PROCESS_TYPE_ID
} from '../../model/task-constant';
import {CLOSE_MN, YES_MN} from '../../model/common.constants';
import {ConfirmDialogComponent} from '../../../common/confirm-dialog/confirm-dialog.component';
import {CollateralUdfDialogComponent} from '../collateral-udf-dialog/collateral-udf-dialog.component';
import {LOAN_CONTRACT, LOAN_PRODUCT} from 'src/app/loan-search-page/models/process-constants.model';
import {DialogSandboxService} from "../../../services/sandbox/dialog-sandbox.service";
import {ErinCustomDialogComponent} from "../../../common/erin-custom-dialog/erin-custom-dialog.component";

@Component({
  selector: 'main-workspace-new',
  template: `
    <ng-container *ngIf="showTestForm()" class="media1">
      <dynamic-form-container [task]="task" [data]="formGroup" [isValidColConnectionRate]="isValidColConnectionRate"
                              [hasInsurance]="hasInsurance" [form]="form" [accountList]="accountList"
                              [borrowerTypeCoBorrower]="borrowerTypeCoBorrower" [tableData]="tableData"
                              (actionEmitter)="handleAction($event)" (fieldActionListener)="fieldActionHandler($event)"></dynamic-form-container>
    </ng-container>

    <ng-container *ngIf="showSalaryTable">
      <salary-table [instanceId]="instanceId" [completedForm]="completedSalaryForm" [requestId]="requestId"
                    (submitFormEmitter)="submitSalaryForm($event)"></salary-table>
    </ng-container>
    <ng-container *ngIf="showCollateralList">
      <collateral-list-page [instanceId]="instanceId" [setCif]="cifNumber" [processRequestId]="requestId"
                            [setIsCompletedCollateral]="completedCollateral"
                            [taskName]="task.name"
                            [data]="completedCollateralForm" [taskId]="task.taskId"
                            (collateralListEmitter)="submitCollateralList($event)"></collateral-list-page>
    </ng-container>
    <ng-container *ngIf="isLoanContract">
      <loan-contract [title]="task.name" [data]="form" [disableState]="completedContract"
                     (printEmitter)="printContractForm($event)" (saveEmitter)="saveLoanContractForm($event)"
                     (submitEmitter)="submitLoanContractForm($event)"></loan-contract>
    </ng-container>

    <ng-container *ngIf="isRemoveCoBorrower">
      <remove_coborrower [instanceId]="instanceId" [title]="task.name" [data]="form"
                         (submitEmitter)="submitRemoveCoBorrowerForm($event)"
                         [buttonState]="buttonState"></remove_coborrower>
    </ng-container>

    <balance-calculation *ngIf="isMicroBalanceCalculation" [taskName]="task.name" [form]="form"
                         [completedState]="buttonState" [instanceId]="instanceId"
                         (saveEmitter)="save(true)" (submitEmitter)="continueProcess()"
                         (calculateEmitter)="calculate()">
    </balance-calculation>
  `,
  styleUrls: ['./main-workspace-new.component.scss']
})
export class MainWorkspaceNewComponent implements OnChanges {
  @Input() instanceId: string;
  @Input() isContinueTask: boolean;
  @Input() field: FormsModel;
  @Input() salaryForm: FormsModel[];
  @Input() isCalculation;
  @Input() cifNumber;
  @Input() requestId;
  @Input() requestType;
  @Input() isInstantLoan;
  @Output() saveActivated = new EventEmitter<any>();
  @Output() salaryTaskId = new EventEmitter<boolean>();
  @Output() calculateTasks = new EventEmitter<string>();
  @Output() hide = new EventEmitter<boolean>();
  @Output() saved = new EventEmitter<boolean>();
  @Output() taskIdEmitter = new EventEmitter<string>();
  @Output() refresh = new EventEmitter<boolean>();
  @Output() continueProc = new EventEmitter<boolean>();
  isReadOnly: boolean;
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
  formGroup;
  isValidColConnectionRate = true;
  form: FormsModel[] = [];
  subtitledForm: FormsModel[][] = [];
  completedSalaryForm = [];
  completedCollateralForm = [];
  accountList = [];
  requiredCheckFields: any[] = [];
  previousTaskId: string;
  parentTaskDefKey: string;
  borrowerTypeCoBorrower = null;
  isCalculated = false;

  isCreateCollateralAccount = false;
  isMicroCreateCollateralAccount = false;
  isMicroSimpleCalculation = false;
  isDownloadKhur = false;
  isDownloadKhurCoBorrower = false;
  isScrollable = false;
  showSalaryTable = false;
  showCollateralList = false;
  hideMainWorkspace = false;
  isLoanAccount = false;
  isCollateralLoanAccount = false;
  isLoanContract = false;
  isRemoveCoBorrower = false;
  isLoanSendForm = false;
  isLoanPermitForm = false;

  // Loan Type
  loanType: string;

  // Micro
  isMicroLoanDownloadKhur = false;
  isMicroLoanDownloadKhurCoBorrower = false;
  isMicroBalanceCalculation = false;
  isMicroLoanCalculation = false;

  // Button
  buttonState = false;
  completedContract = false;
  loanAttachmentContract: boolean;
  showPrintButton: boolean;
  showSeeContractButton: boolean;
  completedCollateral: boolean;

  // create account with collateral
  hasInsurance: boolean;
  collateralAmount = [];
  productType: string;

  LOAN_PRODUCT = 'loanProduct';
  DECISION_RECEIVERS = 'receivers';
  DECISION_RETURNED_USERS = 'receivedUser';
  ID = 'id';
  VALUE = 'value';

  LOAN_CONTRACT = 'LOAN_CONTRACT';
  LOAN_ATTACHMENT_CONTRACT = 'LOAN_ATTACHMENT_CONTRACT';
  CO_OWNER_CONTRACT = 'CO_OWNER_CONTRACT';
  COLLATERAL_REAL_ESTATE_CONTRACT = 'COLLATERAL_REAL_ESTATE_CONTRACT';
  FIDUCIARY_CONTRACT = 'FIDUCIARY_CONTRACT';

  // isLoanAmountCalculated = false;

  // instant loan
  tableData;
  calculatedCount: any;

  constructor(private sb: CaseViewSandboxService, public dialog: MatDialog,
              private router: Router, private commonSb: CommonSandboxService,
              private relationService: FormRelationService,
              private formSb: FormServiceSandboxService,
              private route: ActivatedRoute,
              private dialogService: DialogSandboxService
  ) {
    this.route.paramMap.subscribe(params => {
      this.isReadOnly = params.get('isReadOnlyReq') === 'true';
    });
    const currentNavigationExtras = this.router.getCurrentNavigation().extras.state;
    if (null != currentNavigationExtras && null != currentNavigationExtras.data) {
      this.productType = this.router.getCurrentNavigation().extras.state.productCode;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop === 'instanceId') {
        const idSession = sessionStorage.getItem('taskId');
        sessionStorage.removeItem('taskId');
        if (DIGITAL_LOANS_PROCESS_TYPE_ID.includes(this.productType)) {
          this.getBpmnDynamicForms(this.instanceId, this.productType);
        } else {
          this.getDynamicForms(idSession);
        }
      }
    }
  }

  showTestForm() {
    return !this.isRemoveCoBorrower && !this.showSalaryTable &&
      !this.showCollateralList && !this.isLoanContract && !this.isMicroBalanceCalculation && !this.hideMainWorkspace;
  }

  getRequestById() {
    if (null != this.requestId) {
      this.getRequestInfo(this.requestId);
    }
  }

  setFormRelation(form) {
    this.relationService.setForm(form, this.task.definitionKey).then(() => {
      if (this.task.name.includes(LOAN_CALCULATION)) {
        this.relationService.updateForm(LOAN_PRODUCT);
      }
    });
  }

  updateField(fieldId: string) {
    this.relationService.updateForm(fieldId);
  }

  getBpmnDynamicForms(processInstanceId: string, processType: string, taskId?: string) {
    const overlayRef = this.commonSb.setOverlay();
    let definitionKey: string;
    definitionKey = this.commonSb.getDefinitionKeyByProcessType(processType);
    this.sb.getBpmnForm(processInstanceId, definitionKey).subscribe(res => {
      res['definitionKey'] = definitionKey;
      this.task['definitionKey'] = definitionKey;
      this.handleResponse(res, overlayRef, taskId, null);
      this.tableData = this.getTableValue();
    }, error => {
      if (error) {
        this.setHideMainWorkspace(true);
        this.salaryTaskId.emit(false);
      }
      overlayRef.dispose();
    });
  }

  getDynamicForms(taskId?: string, processRequestId?: string, processType?: string) {
    this.hideCards();
    this.sb.getActiveTasks(this.instanceId, this.requestType, processRequestId, processType).subscribe(taskList => {
      if (taskList.length > 0 && !this.isReadOnly) {
        this.setTask(taskId, taskList);
        if (this.task != null) {
          taskId = this.task.taskId;
          this.parentTaskDefKey = undefined;
        }
        const overlayRef = this.commonSb.setOverlay();
        this.sb.switchActiveTasks(taskId ? taskId : taskList[0].taskId, this.instanceId, this.task.definitionKey).subscribe(res => {
          this.handleResponse(res, overlayRef, taskId, null);
          // if (this.isLoanAmountCalculated === true) {
          //   this.handleFormRelationByTaskDef();
          // }
        }, error => {
          if (error) {
            this.setHideMainWorkspace(true);
            this.salaryTaskId.emit(false);
          }
          overlayRef.dispose();
        });
      }
    });
  }

  private hideCards(): void {
    this.hideMainWorkspace = true;
    this.showSalaryTable = false;
    this.isLoanContract = false;
    this.isRemoveCoBorrower = false;
    this.isCreateCollateralAccount = false;
    this.isMicroCreateCollateralAccount = false;
    this.isMicroBalanceCalculation = false;
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
      const filteredTask = taskList.find(task => task.taskId === taskId);
      if (filteredTask) {
        this.task = filteredTask;
      }
    }
  }

  setSession(taskId: string, taskName: string) {
    sessionStorage.removeItem('taskId');
    sessionStorage.removeItem('taskName');
    sessionStorage.setItem('taskId', taskId);
    sessionStorage.setItem('taskName', taskName);
  }

  clearSession() {
    sessionStorage.clear();
  }

  calculate(calculateForm?) {
    // this.isLoanAmountCalculated = true;
    const overlayRef = this.commonSb.setOverlay();
    const submitForm: FormsModel[] = this.commonSb.clone(calculateForm ? calculateForm : this.form);
    this.switchFieldIdAndValue(submitForm, this.LOAN_PRODUCT, this.ID);
    if (null != this.formGroup.entities && null != this.formGroup.entities.taskType && null != this.formGroup.entities.entity) {
      this.sb.calculateTask(this.task.taskId, this.formGroup.entities.taskType, this.formGroup.entities.entity, submitForm, this.instanceId)
        .subscribe((res: TaskModel) => {
          this.handleCalculate(overlayRef, res);
        }, () => overlayRef.dispose(), () => null);
    } else {
      overlayRef.dispose();
    }
  }

  // private handleFormRelationByTaskDef() {
  //   if ((this.task.definitionKey === TASK_DEF_MORTGAGE_BUSINESS_CALCULATION) || (this.task.definitionKey === TASK_DEF_LOAN_AMOUNT_CALCULATION)
  //     || (this.task.definitionKey === TASK_DEF_MICRO_SIMPLE_CALCULATION) || (this.task.definitionKey === TASK_DEF_MICRO_LOAN_CALCULATION) || this.task.definitionKey === TASK_DEF_MORTGAGE_LOAN_CALCULATION) {
  //     this.form.forEach(field => {
  //       field.validations.push({name: 'readonly', configuration: null})
  //     })
  //   }
  // }

  // private editForm() {
  //
  //   if (this.task.definitionKey === TASK_DEF_MORTGAGE_BUSINESS_CALCULATION) {
  //     this.form.forEach(field => {
  //       if (field.id !== 'reportPeriod' && field.id !== 'netProfit') {
  //         field.validations = []
  //       }
  //       if (field.id !== 'reportPeriod' && field.id !== 'netProfit' && field.id !== 'activeLoanPayment') {
  //         field.validations.push({name: 'required', configuration: null})
  //       }
  //     })
  //   }
  //   if (this.task.definitionKey === TASK_DEF_LOAN_AMOUNT_CALCULATION) {
  //     this.form.forEach(field => {
  //       if (field.id !== 'repaymentType' && field.id !== 'debtIncomeBalanceString' && field.id !== 'hasCollateral'
  //         && field.id !== 'collateralAmount' && field.id !== 'loanApprovalAmount' && field.id !== 'salaryAmountString'
  //         && field.id !== 'totalIncomeAmountString' && field.id !== 'debtIncomeIssuanceString' && field.id !== 'grantLoanAmountString') {
  //         field.validations = []
  //       }
  //       if (field.id !== 'repaymentType' && field.id !== 'incomeAmountCoBorrower' && field.id !== 'debtIncomeBalanceString'
  //         && field.id !== 'hasCollateral' && field.id !== 'collateralAmount' && field.id !== 'loanApprovalAmount'
  //         && field.id !== 'salaryAmountString' && field.id !== 'totalIncomeAmountString' && field.id !== 'grantLoanAmountString'
  //         && field.id !== 'confirmLoanAmount') {
  //         field.validations.push({name: 'required', configuration: null})
  //       }
  //     })
  //   }
  //   if (this.task.definitionKey === TASK_DEF_MICRO_SIMPLE_CALCULATION) {
  //     this.form.forEach(field => {
  //       if (field.id !== 'totalAssetString' && field.id !== 'netProfitString') {
  //         field.validations = []
  //       }
  //       if (field.id === 'reportPeriod') {
  //         field.validations.push({name: 'required', configuration: null})
  //       }
  //     })
  //   }
  //   if (this.task.definitionKey === TASK_DEF_MICRO_LOAN_CALCULATION) {
  //     this.form.forEach(field => {
  //       if (field.id !== 'debtToSolvencyRatioString' && field.id !== 'debtToIncomeRatioString' && field.id !== 'debtToAssetsRatioString'
  //         && field.id !== 'currentAssetsRatioString' && field.id !== 'collateralProvidedAmountString' && field.id !== 'grantLoanAmount' && !field.id.includes('loanApprovalAmount')) {
  //         field.validations = []
  //       }
  //       if (field.id === 'loanTerm' || field.id === 'interestRate' || field.id === 'loanProductType' || field.id === 'areasOfActivity' || field.id === 'hasMortgage' || field.id === 'purposeOfLoan' || field.id === 'acceptedLoanAmount') {
  //         field.validations.push({name: 'required', configuration: null})
  //       }
  //     })
  //   }
  //   if (this.task.definitionKey === TASK_DEF_MORTGAGE_LOAN_CALCULATION) {
  //     this.form.forEach(field => {
  //       if (field.id === 'loanProduct' || field.id === 'amount' || field.id === 'loanTerm' || field.id === 'interestRate'
  //         || field.id.includes('typeOfIncome') || field.id.includes('season') || field.id === 'conditionsMet'
  //         || field.id === 'housingFinancing'  || field.id === 'propertyLocation' || field.id === 'borrowerFinances'
  //         || field.id === 'monthlyPayment' || field.id === 'autoGarage'  || field.id === 'borrowerFinanceGarage' || field.id === 'acceptedLoanAmount' || field.id === 'authorize') {
  //         field.validations = []
  //       }
  //       if (field.id === 'loanProduct' || field.id === 'amount' || field.id === 'loanTerm'
  //         || field.id === 'interestRate' || field.id === 'conditionsMet' || field.id === 'propertyLocation'
  //         || field.id === 'borrowerFinances' || field.id === 'acceptedLoanAmount') {
  //         field.validations.push({name: 'required', configuration: null})
  //       }
  //     })
  //   }
  // }

  save(isOnlySave, value?): void {
    const overlayRef = this.commonSb.setOverlay();
    let saveForms;
    if (!!value) {
      saveForms = value;
    } else {
      saveForms = this.form;
    }

    this.switchFieldIdAndValue(saveForms, this.LOAN_PRODUCT, this.ID);
    const loanContractEntity = this.isLoanContract ? 'LOAN_CONTRACT' : undefined;
    if (this.task.definitionKey !== 'user_task_micro_loan_update_coBorrower') {
      this.sb.saveTasks(isOnlySave, this.commonSb.clone(saveForms), this.instanceId,
        this.task.name, this.task.definitionKey, null, loanContractEntity).subscribe(() => {
        overlayRef.dispose();
      }, () => {
        overlayRef.dispose();
      });
    }
    overlayRef.dispose();
    this.saved.emit(true);
  }

  printContract() {
    const overlayRef = this.commonSb.setOverlay();
    this.form = this.getFinalForm();
    const loanContractEntity = this.getEntityType();
    this.sb.printLoanContract(this.form, this.instanceId, loanContractEntity).subscribe((res: TaskModel) => {
      overlayRef.dispose();
      this.form = res.taskFormFields;
    }, () => {
      overlayRef.dispose();
    });
    this.saved.emit(true);
  }

  private getEntityType() {
    if (this.isLoanContract) {
      return this.LOAN_CONTRACT;
    }
    if (this.task.name.includes(LOAN_ATTACHMENT_CONTRACT)) {
      return this.LOAN_ATTACHMENT_CONTRACT;
    }
    if (this.task.name.includes(CO_OWNER_INFORMATION)) {
      return this.CO_OWNER_CONTRACT;
    }
    if (this.task.name.includes(COLLATERAL_REAL_ESTATE)) {
      return this.COLLATERAL_REAL_ESTATE_CONTRACT;
    }
    if (this.task.name.includes(FIDUCIARY_ASSET)) {
      return this.FIDUCIARY_CONTRACT;
    }
    return undefined;
  }

  private validateCollateralOfLoanAccount(accountForm: FormsModel[]) {
    if (accountForm.length > 1) {
      const collateralList = accountForm[1];
      for (const colId in collateralList) {
        const collateralLoanAmount = collateralList[colId].loanAmount;
        if (collateralLoanAmount <= 0) {
          this.commonSb.showSnackBar(WRONG_COLLATERAL_LOAN_AMOUNT_MESSAGE, 'ХААХ');
          return false;
        }
      }
    }
    return true;
  }

  createLoanAccount(collateralForm?) {
    let accountTmpForm: FormsModel[];
    if (!!collateralForm) {
      accountTmpForm = collateralForm;
    } else {
      accountTmpForm = this.commonSb.getForm(this.form);
    }
    accountTmpForm = this.commonSb.clone(accountTmpForm);
    this.swapFieldValueAndOptionId(accountTmpForm, this.ID);
    const isValidCollateralInfo = this.validateCollateralOfLoanAccount(accountTmpForm);
    if (!isValidCollateralInfo) {
      return;
    }
    const overlayRef = this.commonSb.setOverlay();
    this.sb.createLoanAccount(this.task.taskId, accountTmpForm, this.instanceId, this.task.name, this.task.definitionKey)
      .subscribe((res: TaskModel) => {
        overlayRef.dispose();
        this.handleResponse(res, overlayRef, res.taskId, null);
        this.calculateTasks.emit(res.taskId);
        this.isDisabledLoanAccount();
      }, () => {
        overlayRef.dispose();
      });
  }

  download() {
    const overlayRef = this.commonSb.setOverlay();
    if (this.isDownloadButton()) {
      const submitForm = this.form;
      this.sb.downloadInfo(this.task.taskId, this.task.instanceId, this.requestId, this.formGroup.entities.entity, submitForm).subscribe(() => {
        this.saveCompletedForm('formFields', this.getFinalForm());
        this.handleSubmitForm(overlayRef);
      }, () => {
        overlayRef.dispose();
      });
    }
  }

  continueProcess(formName?, formValue?, extraForm?) {
    const overlayRef = this.commonSb.setOverlay();
    if (this.task.name.includes(LOAN_CALCULATION) && this.noCollateralOnLoanAmountCalculation()) {
      this.dialogHasCollateral(overlayRef);
    } else {
      this.submit(overlayRef, formName, formValue, extraForm);
      const definitionKeys = ['Task_1a5yfhu', 'UserTask_0r0le5y', 'user_task_micro_balance_calculation', 'user_task_micro_simple_calculation',
        'user_task_mortgage_salary_calculation', 'user_task_mortgage_business_calculation']
      if (definitionKeys.includes(this.task.definitionKey)) {
        let msg = '';
        let openDialog = false;
        switch (this.task.definitionKey) {
          case 'Task_1a5yfhu':
            msg = 'Холбогдох тооцооллуудыг хийнэ үү!'
            const salaryCalculationCount = this.commonSb.getFormValue(this.form, 'salaryCalculationCount');
           if(salaryCalculationCount != null && salaryCalculationCount > 0){
             openDialog = true;
           }
            break;
          case 'UserTask_0r0le5y':
            const amountCalculationCount = this.commonSb.getFormValue(this.form, 'amountCalculationCount');
            if(amountCalculationCount != null && amountCalculationCount > 0){
              openDialog = true;
            }
            msg = 'Холбогдох тооцооллуудыг хийнэ үү!'
            break;
          case 'user_task_micro_balance_calculation':
          case 'user_task_micro_simple_calculation':
          case 'user_task_mortgage_salary_calculation':
          case 'user_task_mortgage_business_calculation':
            const businessCalculationCount = this.commonSb.getFormValue(this.form, 'businessCalculationCount');
            const mortgageCalculationCount = this.commonSb.getFormValue(this.form, 'mortgageCalculationCount');
            if((businessCalculationCount != null && businessCalculationCount > 0) || (null!= mortgageCalculationCount && mortgageCalculationCount > 1)){
              openDialog = true;
            }
            msg = 'Холбогдох тооцооллуудыг хийнэ үү!'
            break;
          default:
            break;
        }
        const CONFIRM_DIALOG_BUTTONS = [{actionId: 'confirm', actionName: 'OK'}];
        const config = this.setDialogConfig(CONFIRM_DIALOG_BUTTONS, msg, '500px');

        if(openDialog){
          this.dialogService.openCustomDialog(ErinCustomDialogComponent, config).then((res: any) => {

          });
        }
      }
    }
  }

  private setDialogConfig(buttons, message, width): MatDialogConfig {
    const dialogData = {buttons, message};
    const config = new MatDialogConfig();
    config.width = width;
    config.data = dialogData;
    return config;
  }

  submit(overlayRef, formName?, formValue?, extraForm?) {
    let submitForm: FormsModel[];
    if (!!extraForm) {
      submitForm = extraForm;
    } else {
      submitForm = this.commonSb.clone(this.form);
    }

    if (!this.checkRequiredFieldsValue(overlayRef, submitForm)) {
      return;
    }

    submitForm = this.commonSb.clone(submitForm);
    this.switchFieldIdAndValue(submitForm, this.LOAN_PRODUCT, this.ID);
    if (this.task.definitionKey !== TASK_DEF_CREATE_LOAN_ACCOUNT && this.task.definitionKey !== TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT) {
      this.swapFieldValueAndOptionId(submitForm, this.ID);
    }

    if (this.task.definitionKey === 'Task_1a5yfhu') {
      submitForm['salaryCalculationCount'] = 2;
    }
    if (this.task.definitionKey === 'UserTask_0r0le5y') {
      let fieldValue = this.commonSb.getFormValue(submitForm, 'amountCalculationCount');
      this.commonSb.setFieldDefaultValue(submitForm, {amountCalculationCount: fieldValue + 1})
    }
    if(this.task.definitionKey ==='user_task_calculate_micro_loan_amount'){
      let fieldValue = this.commonSb.getFormValue(submitForm, 'microLoanCalculation');
      this.commonSb.setFieldDefaultValue(submitForm, {microLoanCalculation: fieldValue + 1})
    }
    if(this.task.definitionKey ==='user_task_calculate_mortgage_loan_amount'){
      let fieldValue = this.commonSb.getFormValue(submitForm, 'mortgageLoanCalculationCount');
      this.commonSb.setFieldDefaultValue(submitForm, {mortgageLoanCalculationCount: fieldValue + 1})
    }
    if (this.task.definitionKey === 'user_task_micro_balance_calculation' || this.task.definitionKey === 'user_task_micro_simple_calculation') {
      let fieldValue = this.commonSb.getFormValue(submitForm, 'businessCalculationCount');
      this.commonSb.setFieldDefaultValue(submitForm, {businessCalculationCount: fieldValue + 1})
    }
    if (this.task.definitionKey === 'user_task_mortgage_business_calculation') {
      let fieldValue = this.commonSb.getFormValue(submitForm, 'mortgageCalculationCount');
      this.commonSb.setFieldDefaultValue(submitForm, {mortgageCalculationCount: fieldValue + 1})
    }

    if(this.task.definitionKey === 'user_task_mortgage_salary_calculation'){
      submitForm['salaryCalculationCount'] = 2;
    }
    if (DIGITAL_LOANS_PROCESS_TYPE_ID.includes(this.productType)) {
      this.sb.submitBpmForm(this.productType, this.instanceId).subscribe(res => {
        if (res) {
          this.continueProc.emit();
          this.commonSb.showSnackBar('Амжилттай', CLOSE_MN, 6000);
          this.saveCompletedForm((formName ? formName : 'formFields'), formValue ? formValue : submitForm);
          this.formGroup = null;
          this.callRelatedTask(overlayRef, res.relatedUserTaskId != null ? res.relatedUserTaskId : null);
        }
      }, () => {
        overlayRef.dispose();
      });
    } else {
      this.sb.submitForm(this.task.taskId, this.task.instanceId, this.requestId, submitForm,
        this.task.name, this.task.definitionKey, this.formGroup.entities.entity).subscribe((res: any) => {
        this.saveCompletedForm((formName ? formName : 'formFields'), formValue ? formValue : submitForm);
        this.formGroup = null;
        this.callRelatedTask(overlayRef, res.relatedUserTaskId != null ? res.relatedUserTaskId : null);
      }, () => {
        overlayRef.dispose();
      });
    }
  }

  private checkRequiredFieldsValue(overlayRef, submitForm): any {
    if (this.requiredCheckFields.length > 0) {
      const hasRequiredValuesChanged = this.relationService.checkRequiredFieldsValue(this.requiredCheckFields, submitForm);
      if (hasRequiredValuesChanged !== true) {
        const field = this.getFinalForm().find(f => f.id === hasRequiredValuesChanged);
        if (null != field) {
          this.commonSb.showSnackBar(field.label + ' талбарын утга өөрчилөгдсөн тул дахин тооцоолно уу!', CLOSE_MN);
          overlayRef.dispose();
          return false;
        }
      }
    }
    return true;
  }

  private callRelatedTask(overlayRef: OverlayRef, relatedTaskRef: string): void {
    if (this.task.name.includes(LOAN_PERMIT) || this.task.name.includes(LOAN_SEND)) {
      this.navigateToProcessTable();
    } else if (null != relatedTaskRef && this.task.parentTaskId == null) {
      this.handleSubmitForm(overlayRef, relatedTaskRef);
    } else if (this.task.parentTaskId != null) {
      this.handleSubmitForm(overlayRef, this.task.parentTaskId);
    } else {
      this.handleSubmitForm(overlayRef);
    }
    overlayRef.dispose();
  }

  private navigateToProcessTable(): void {
    const parentURL = this.sb.getParentUrl();
    this.clearSession();
    this.router.navigateByUrl(parentURL + 'dashboard/my-loan-request').then(() => {
      this.commonSb.showSnackBar('Амжилттай илгээлээ!', null, 5000);
      this.refresh.emit(true);
    });
  }

  saveCompletedForm(formName, formValue) {
    if (formName === 'formFields') {
      formValue.forEach(field => {
        field.disabled = true;
      });
    }
    const completedTask = {
      taskId: this.task.taskId, caseInstanceId: this.instanceId,
      formId: this.task.name, taskDefinitionKey: this.task.definitionKey
    };
    completedTask[formName] = formValue;
    this.sb.saveCompletedTask(completedTask).subscribe(() => undefined);
  }

  getCompletedTaskForm(taskId: string) {
    this.hideCards();
    const overlayRef = this.commonSb.setOverlay();
    this.sb.getCompletedForm(taskId, this.instanceId).subscribe(res => {
      this.task.definitionKey = res.taskDefinitionKey;
      this.task.name = res.formId;
      this.task.taskId = res.taskId;
      this.handleResponse({taskFormFields: res.formFields, taskId, formId: res.formId}, overlayRef, taskId, null, true);
      if (res.specialForms.SALARY_TABLE || res.specialForms.COLLATERAL_TABLE) {
        this.completedSalaryForm = res.specialForms.SALARY_TABLE;
        this.completedCollateralForm = res.specialForms.COLLATERAL_TABLE;
      }
      if (res.specialForms.COLLATERAL_FORM && res.specialForms.FORM && (this.isCreateCollateralAccount || this.isMicroCreateCollateralAccount)) {
        this.hideMainWorkspace = true;
        const collateralList = [];
        for (const id in res.specialForms.COLLATERAL_FORM) {
          if (res.specialForms.COLLATERAL_FORM.hasOwnProperty(id)) {
            collateralList.push(res.specialForms.COLLATERAL_FORM[id]);
          }
        }
        this.handleResponse({taskFormFields: res.specialForms.FORM.field, taskId, formId: res.formId}, overlayRef, taskId, collateralList, true);
      }
    }, () => {
      overlayRef.dispose();
    });
  }

  private checkAndSetCompletedTaskForm(collateralList, isCompletedTask: boolean): void {
    if (isCompletedTask) {
      for (const buttonId in this.formGroup.buttons) {
        if (this.formGroup.buttons.hasOwnProperty(buttonId)) {
          this.formGroup.buttons[buttonId] = true;
        }
      }
      if (null != collateralList) {
        this.setCollateralForm(collateralList);
      }
    }
  }

  private checkAndSetLoanAccountTaskForm(res: TaskModel, isCompletedTask: boolean) {
    if ((this.isCreateCollateralAccount && res.taskFormFields.length > 0 && isCompletedTask == null) || this.isMicroCreateCollateralAccount) {
      this.setCollateralForm(null);
    }
    if (!isCompletedTask && (this.isLoanAccount ||
      (this.isCreateCollateralAccount && res.taskFormFields.length > 0) || this.isMicroCreateCollateralAccount)) {
      this.getRequestById();
      this.isDisabledLoanAccount();
      this.sb.getAccountList(this.instanceId).subscribe(accountList => {
        this.accountList = accountList;
        this.changeLoanAccount();
      });
    }
  }

  private async getFormGroup(res: TaskModel, collateralList, isCompletedTask: boolean) {
    this.formGroup = await this.formSb.getForm(this.task, res.taskFormFields);
    this.checkAndSetCompletedTaskForm(collateralList, isCompletedTask);
    this.swapFieldValueAndOptionId(res.taskFormFields, this.VALUE);
    this.checkAndSetLoanAccountTaskForm(res, isCompletedTask);
  }

  private setAddionalInfoOnField(): void {
    for (const field of this.form) {
      const fieldHint = FORM_FIELD_HINT.find(inputField => inputField.id === field.id);
      if (fieldHint) {
        field.fieldHint = fieldHint.fieldHint;
      }
    }
  }

  private handleResponse(res: TaskModel, overlayRef: OverlayRef, taskId: string, collateralList, isCompletedTask?: boolean) {
    this.getFormGroup(res, collateralList, isCompletedTask);
    this.setFormRelation(res.taskFormFields);
    if (isCompletedTask === undefined) {
      this.completedSalaryForm = [];
      this.completedContract = false;
    }
    this.switchFieldIdAndValue(res.taskFormFields, this.LOAN_PRODUCT, this.VALUE);
    if (this.task.name == '11. Зээлийн хэмжээ тооцох') {
      const fieldValue = this.commonSb.getFormValue(res.taskFormFields, 'loanProduct');
      this.hasCollateral(fieldValue);
    }
    this.setTaskState(res, isCompletedTask);
    this.form = res.taskFormFields;
    this.setAddionalInfoOnField();
    this.isValidColConnectionRate = true;
    if (this.isMicroLoanCalculation) {
      this.getCollateralProducts();
    }

    overlayRef.dispose();
    this.buttonState = isCompletedTask;
    this.isCalculated = false;
    this.hideMainWorkspace = false;
    this.taskIdEmitter.emit(taskId);
    this.setSession(res.taskId, res.formId);
  }

  private handleSubmitForm(overlayRef: OverlayRef, relatedTaskID?: string) {
    this.previousTaskId = this.task.taskId;
    this.parentTaskDefKey = this.task.definitionKey;
    overlayRef.dispose();
    this.saveActivated.emit({save: true, relatedTaskId: relatedTaskID});
  }

  private handleCalculate(overlayRef: OverlayRef, res) {
    this.task.taskId = res.taskId;
    if (this.task.name.includes(LOAN_CALCULATION) && this.task.definitionKey !== TASK_DEF_MICRO_LOAN_CALCULATION
      && this.task.definitionKey !== TASK_DEF_MORTGAGE_LOAN_CALCULATION) {
      this.setLoanCalculationForm(res);
    } else {
      this.form = res.taskFormFields;
    }
    // this.handleFormRelationByTaskDef();
    this.calculatedCount = Number(this.calculatedCount) + 1;
    this.calculateTasks.emit(this.task.taskId);
    overlayRef.dispose();
  }

  private removeField(index: number, subform: FieldGroups): void {
    subform.subFields.splice(index, 1);
  }

  private removeFieldFromForm(index: number, subform: FieldGroups, showDialog: boolean): void {
    if (showDialog) {
      const config = new MatDialogConfig();
      config.width = '500px';
      config.data = {
        name: '', confirmButton: 'Тийм', closeButton: 'Үгүй',
        message: subform.message
      };
      const dialogRef = this.dialog.open(ConfirmDialogComponent, config);
      dialogRef.afterClosed().subscribe(res => {
        if (res) {
          subform.subFields.splice(index, 1);
        }
      });
    } else {
      subform.subFields.splice(index, 1);
    }
  }

  isSalaryCalculation(): boolean {
    if (this.task.name == null) {
      return false;
    }
    if (this.task.name.includes(SALARY_CALCULATION) || this.task.definitionKey === TASK_DEF_MORTGAGE_SALARY_CALCULATION) {
      return true;
    }
  }

  isUdfButton() {
    if (this.task.name == null) {
      return false;
    }
    return this.task.name.includes(CREATE_COLLATERAL);
  }

  isSaveButton(): boolean {
    if (this.task.name == null) {
      return false;
    }
    return (!this.isDownloadKhur && !this.task.name.includes(MONGOLBANK)
      && !this.isMicroLoanDownloadKhur
      && !this.isDownloadKhurCoBorrower && !this.task.name.includes(MONGOLBANK_COBORROWER)
      && !this.task.name.includes(LOAN_ATTACHMENT_CONTRACT) && !this.isMicroLoanDownloadKhurCoBorrower);
  }

  isDownloadButton() {
    if (this.task.name == null) {
      return false;
    }
    return (this.isDownloadKhur || this.task.name.includes(MONGOLBANK)
      || this.isDownloadKhurCoBorrower || this.task.name.includes(MONGOLBANK_COBORROWER)
      || this.isMicroLoanDownloadKhur || this.isMicroLoanDownloadKhurCoBorrower);
  }

  isContinueButton() {
    if (this.task.name == null) {
      return false;
    }
    return (!this.isDownloadKhur && !this.task.name.includes(MONGOLBANK) &&
      !this.isDownloadKhurCoBorrower && !this.task.name.includes(MONGOLBANK_COBORROWER)
      && !this.isMicroLoanDownloadKhur && !this.isMicroLoanDownloadKhurCoBorrower);
  }

  isBackButton() {
    if (this.task.name == null) {
      return false;
    }
    return (this.task.name.includes(STANDARD_HOUSING_VALUATION));
  }

  isFlexibleShowContractButton(): boolean {
    if (this.task.name == null) {
      return false;
    }
    return this.task.name.includes(LOAN_ATTACHMENT_CONTRACT);
  }

  isDisabledLoanAccount() {
    const loanAccountField = this.form.find(field => field.id === 'loanAccountNumber');
    if (null != loanAccountField && null != loanAccountField.formFieldValue && loanAccountField.formFieldValue.defaultValue !== null) {
      this.formGroup.buttons.createAccount = true;
      this.formGroup.buttons.continue = false;
      this.form.forEach(accountField => accountField.validations.push({name: 'readonly', configuration: null}));
    } else {
      this.formGroup.buttons.createAccount = false;
      this.formGroup.buttons.continue = true;
    }
  }

  setHideMainWorkspace(state: boolean) {
    this.hideMainWorkspace = state;
    this.hide.emit(state);
  }

  handleAction(event: any): void {
    switch (event.action) {
      case 'calculateCollateralAccount':
        this.calculateCollateralAmount(event.data);
        break;
      case 'createColAccount':
        this.createCreateCollateralAccountForm(event.data);
        break;
      case 'continueAccount':
        this.submitCreateCollateralAccountForm(event.data);
        break;
      case 'udf':
        this.openUdfDialog();
        break;
      case 'createAccount':
        this.createLoanAccount();
        break;
      case 'download':
        if (this.isMongolBankRelatedTask()) {
          this.calculate();
        } else {
          this.download();
        }
        break;
      case 'calculate':
        this.calculate();
        break;
      case 'collateralAccountSave':
        this.saveCreateCollateralAccountForm(event.data);
        break;
      case 'save':
        this.save(true);
        break;
      case 'continue':
        this.continueProcess();
        break;
      case 'removeField':
        this.removeFieldFromForm(event.data.index, event.data.subform, event.showDialog);
        break;
      // case 'editForm':
      //   this.editForm();
        break;
      default:
        break;
    }
  }

  printContractForm($event: FormsModel[]) {
    this.form = $event;
    this.printContract();
  }

  saveLoanContractForm($event: FormsModel[]) {
    this.form = $event;
    this.save(true);
  }

  submitLoanContractForm($event: FormsModel[]) {
    this.form = $event;
    this.continueProcess();
    this.save(false);
  }

  submitSalaryForm(event) {
    this.form = event[1];
    this.continueProcess(SPECIAL_FORMS, {SALARY_TABLE: event[0]});
    this.showSalaryTable = false;
  }

  submitCollateralList(event) {
    this.form = event[1];
    this.continueProcess(SPECIAL_FORMS, {COLLATERAL_TABLE: event[0], form: event[1]});
    this.showCollateralList = !this.showCollateralList;
  }

  submitCreateCollateralAccountForm(form) {
    this.continueProcess(SPECIAL_FORMS, {
      FORM: {field: form[0]},
      COLLATERAL_FORM: form[1],
      hasInsurance: form[2]
    }, form);
    this.saveCollateralFromAccount(form[form.length - 1]);
  }

  saveCreateCollateralAccountForm($event) {
    this.saveCollateralFromAccount($event[$event.length - 1]);
    this.save(true, $event);
  }

  createCreateCollateralAccountForm($event: any[]) {
    this.saveCollateralFromAccount($event[$event.length - 1]);
    this.createLoanAccount($event);
  }

  calculateCollateralAmount(collateralForm: any[]) {
    this.sb.calculateCollateralAmount(collateralForm).subscribe(res => {
      if (res) {
        let sum = 0;
        for (const collateralField of res) {
          const fields = this.formGroup.fieldGroups[5].subFields;
          for (const subFields of fields) {
            if (subFields.find(f => f.id === COLLATERAL_ID).formFieldValue.defaultValue === collateralField.collateralId) {
              subFields.find(f => f.id === COLLATERAL_CONNECTION_RATE).formFieldValue.defaultValue = collateralField.collateralConnectionRate;
              sum = Number((sum + collateralField.collateralConnectionRate).toFixed(2));
            }
          }
        }
        if (sum > 100) {
          this.commonSb.showSnackBar(WRONG_COLLATERAL_CONNECTION_RATE_VALUE_MESSAGE, CLOSE, null);
          this.isValidColConnectionRate = false;
        } else {
          this.isValidColConnectionRate = true;
        }
        this.collateralAmount = [res, true];
      }
    });
  }

  getFinalForm(): FormsModel[] {
    return this.isRegularTask() ? this.form : this.commonSb.getForm(this.subtitledForm);
  }

  checkLoanCalculationMonth(overlayRef?): boolean {
    const monthField = this.subtitledForm[0].find(field => {
      if (field.id === 'term') {
        return field;
      }
    });
    if (monthField != null) {
      const isValidMonth = Number.isInteger(Number(monthField.formFieldValue.defaultValue)) && monthField.formFieldValue.defaultValue < 31;
      if (!isValidMonth) {
        this.commonSb.showSnackBar('Хугацаа(сар) -аа зөв оруулна уу.', null, 3000);
        overlayRef.dispose();
      }
      return isValidMonth;
    }
  }

  setTaskState(res: TaskModel, isCompletedTask?: boolean): void {
    this.isScrollable = res.taskFormFields.length > 12;
    this.task.name = res.formId;
    this.isMicroLoanCalculation = this.task.definitionKey === TASK_DEF_MICRO_LOAN_CALCULATION;
    this.isCollateralLoanAccount = this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT;
    this.isLoanAccount = this.task.name.includes(LOAN_ACCOUNT) && this.task.definitionKey !== TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
      && this.task.definitionKey !== TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT;
    // related to completed task
    this.completedContract = isCompletedTask;
    this.completedCollateral = isCompletedTask;

    this.isLoanSendForm = this.task.name.includes(LOAN_SEND);
    this.isLoanPermitForm = this.task.name.includes(LOAN_PERMIT);
    this.isLoanContract = this.task.name.includes(LOAN_CONTRACT);
    this.isRemoveCoBorrower = this.task.name.includes(REMOVE_COBORROWER);
    if (this.task.name.includes(SALARY_CALCULATION) || this.task.definitionKey === TASK_DEF_MORTGAGE_SALARY_CALCULATION) {
      this.showSalaryTable = true;
    }
    this.isMicroSimpleCalculation = this.task.definitionKey === TASK_DEF_MICRO_SIMPLE_CALCULATION;
    this.isDownloadKhur = this.task.name.includes(DOWNLOADKHUR) && this.task.definitionKey !== TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR;
    this.isDownloadKhurCoBorrower = this.task.name.includes(DOWNLOADKHUR_COBORROWER) && this.task.definitionKey !== TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR;
    this.isCreateCollateralAccount =
      this.task.name.includes(CREATE_COLLATERAL_LOAN_ACCOUNT) ||
      (this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT);
    this.showCollateralList = (this.task.name.includes(COLLATERAL_LIST) || this.task.definitionKey === TASK_DEF_COLLATERAL_LIST)
      && this.task.definitionKey !== TASK_DEF_VIEW_COLLATERAL_LIST && this.task.definitionKey !== TASK_DEF_CREDIT_LINE_VIEW_COLLATERAL_LIST;

    // micro
    this.isMicroBalanceCalculation = this.task.definitionKey === TASK_DEF_MICRO_BALANCE_CALCULATION;
    this.isMicroLoanCalculation = this.task.definitionKey === TASK_DEF_MICRO_LOAN_CALCULATION;
    this.isMicroSimpleCalculation = this.task.definitionKey === TASK_DEF_MICRO_SIMPLE_CALCULATION;
    this.isMicroLoanDownloadKhur = this.task.definitionKey === TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR;
    this.isMicroLoanDownloadKhurCoBorrower = this.task.definitionKey === TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR_CO_BORROWER;

    // Mortgage
    this.isMicroCreateCollateralAccount =
      this.task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT && this.task.name.includes('данс үүсгэх');

    this.showPrintButton = false;
    this.showSeeContractButton = false;
    this.salaryTaskId.emit(this.isSalaryCalculation());

    if (!this.isLoanContract && !this.isRemoveCoBorrower && !this.isCreateCollateralAccount
      && !this.isMicroCreateCollateralAccount && !this.isMicroBalanceCalculation) {
      this.hideMainWorkspace = false;
    }
  }

  setLoanCalculationForm(res): void {
    this.sortLoanProductOption(res.taskFormFields);
    const fieldValue = this.commonSb.getFormValue(res.taskFormFields, LOAN_PRODUCT);
    if (fieldValue != null && fieldValue.includes('EB71')) {
      this.commonSb.setFieldDefaultValue(res.taskFormFields, {term: 6});
    }
    this.requiredCheckFields[0] = [TERM, FIRST_PAYMENT_DATE];
    this.requiredCheckFields = this.relationService.updateRequiredFields(this.requiredCheckFields, this.getFinalForm());
  }

  swapFieldValueAndOptionId(submitForm: FormsModel[], type: string) {
    if (this.formGroup.hasOwnProperty('getPropertyId')) {
      const fieldIds: string[] = this.formGroup.getPropertyId;
      for (const id of fieldIds) {
        this.switchFieldIdAndValue(submitForm, id, type);
      }
    }
  }

  switchFieldIdAndValue(form, id: string, setProperty: string) {
    for (const subForm of form) {
      if (null != subForm && null != subForm.length) {
        for (const field of subForm) {
          this.changeOption(field, id, setProperty);
        }
      } else {
        this.changeOption(subForm, id, setProperty);
      }
    }
  }

  changeOption(field: FormsModel, id: string, setProperty: string) {
    if (null != field && id === field.id) {
      let value;
      if (setProperty === 'value') {
        value = field.options.find(option => option.id === field.formFieldValue.defaultValue);
        if (value !== undefined) {
          value = value.value;
        }
      } else if (setProperty === 'id') {
        value = field.options.find(option => option.value === field.formFieldValue.defaultValue);
        if (value !== undefined) {
          value = value.id;
        }
      }
      if (value !== undefined) {
        field.formFieldValue.defaultValue = value;
      }
    }
  }

  sortLoanProductOption(form: FormsModel[]) {
    const loanProduct: FormsModel = form.find(field => field.id === this.LOAN_PRODUCT);
    loanProduct.options.sort((a, b) => {
      if (a.value > b.value) {
        return 1;
      }
      if (a.value < b.value) {
        return -1;
      }
      return 0;
    });
  }

  changeLoanAccount() {
    if (null != this.form) {
      const loanRate = this.commonSb.getFormValue(this.form, 'yearlyInterestRateString');
      let increasedRate;
      if (loanRate != null) {
        increasedRate = this.commonSb.findMultiplication(Number(loanRate), 0.2).toFixed(2);
      }
      const startDate = this.commonSb.getFormValue(this.form, 'firstPaymentDate');
      let startDay;
      if (startDate != null && typeof startDate.getDate === 'function') {
        startDay = startDate.getDate();
      }
      const accountNumber = this.commonSb.getFormValue(this.form, 'currentAccountNumber');
      const accountForPayment = this.accountList.find(account => account.accountId === accountNumber);
      let branchId;
      if (null != accountForPayment) {
        branchId = accountForPayment.branchId;
      }
      const newMap = {
        depositInterestRateString: String(increasedRate) + '%',
        dayOfPayment: String(startDay),
        accountBranchNumber: String(branchId)
      };
      this.form = this.commonSb.setFieldDefaultValue(this.form, newMap);
    }
  }

  private transformCollateralListToFormField(collateralList) {
    const tmpForm = [];
    for (const collateralField of collateralList) {
      const fields = JSON.parse(JSON.stringify(COLLATERAL_FORMS));
      fields.find(f => f.id === COLLATERAL_ID).formFieldValue.defaultValue = collateralField.collateralId;
      fields.find(f => f.id === AMOUNT_OF_ASSESSMENT).formFieldValue.defaultValue = collateralField.amountOfAssessment;
      tmpForm.push(fields);
    }
    return tmpForm;
  }

  private isAccountCreated(): boolean {
    const loanAccountField = this.form.find(field => field.id === 'loanAccountNumber');
    return null != loanAccountField && null != loanAccountField.formFieldValue && loanAccountField.formFieldValue.defaultValue !== null;
  }

  private checkAccountAndDisableColForm(colFormList: FormsModel[][]) {
    if (this.isAccountCreated()) {
      for (const subForm of colFormList) {
        subForm.forEach(field => field.disabled = true);
      }
    }
  }

  private setCollateralForm(hasCompletedForm): void {
    const tmpForm = [];
    const collateralSubfieldTemplate: FieldGroups = {
      fieldIds: [], fields: [], title: 'Барьцаа хөрөнгө', column: 5, type: 'removable', subFields: tmpForm, message: COL_REMOVE_DIALOG_MESSAGE
    };
    if (hasCompletedForm == null) {
      this.sb.getCollateralAssets(this.instanceId).subscribe(collaterals => {
        collateralSubfieldTemplate.subFields = this.transformCollateralListToFormField(collaterals);
        this.checkAccountAndDisableColForm(collateralSubfieldTemplate.subFields);
        this.formGroup.fieldGroups.push(collateralSubfieldTemplate);
        this.getSavedCollateralFormAccountCreation();
      });
    } else {
      for (const collateralForm of hasCompletedForm) {
        const fields = JSON.parse(JSON.stringify(COLLATERAL_FORMS));
        for (const id in collateralForm) {
          if (collateralForm.hasOwnProperty(id)) {
            const tmpCol = fields.find(y => y.id === id);
            tmpCol.formFieldValue.defaultValue = collateralForm[id];
            tmpCol.disabled = true;
          }
        }
        tmpForm.push(fields);
      }
      collateralSubfieldTemplate.subFields = tmpForm;
      this.formGroup.fieldGroups.push(collateralSubfieldTemplate);
    }
  }

  private setSavedCollateralAccountForm(collateralList) {
    for (const colFields of this.formGroup.fieldGroups[5].subFields) {
      const colId = colFields.find(f => f.id === COLLATERAL_ID).formFieldValue.defaultValue;
      for (const id in collateralList) {
        if (collateralList.hasOwnProperty(id) && id === colId) {
          const tmpCol = collateralList[id];
          colFields.find(f => f.id === AMOUNT_OF_ASSESSMENT).formFieldValue.defaultValue = tmpCol.amountOfAssessment;
          colFields.find(f => f.id === COLLATERAL_CONNECTION_RATE).formFieldValue.defaultValue = tmpCol.collateralConnectionRate;
          colFields.find(f => f.id === ORDER).formFieldValue.defaultValue = tmpCol.order;
          colFields.find(f => f.id === LOAN_AMOUNT).formFieldValue.defaultValue = tmpCol.loanAmount;
          break;
        }
      }
    }
  }

  private getSavedCollateralFormAccountCreation() {
    this.sb.getParametersByName(this.instanceId, COLLATERAL_ACCOUNT).subscribe((res) => {
      const collateralList = this.commonSb.extractProcessParameter(res, ['FORM', COLLATERAL_ACCOUNT]);
      const tmpForm = [];
      for (const id in collateralList) {
        if (collateralList.hasOwnProperty(id)) {
          tmpForm.push(collateralList[id]);
        }
      }
      this.collateralAmount = [tmpForm, false];
      this.setSavedCollateralAccountForm(collateralList);
    });
  }

  dialogHasCollateral(overlayRef, formName?, formValue?) {
    const config = new MatDialogConfig();
    const dialogData = {taskName: '', confirmButton: 'Тийм', closeButton: 'Үгүй', message: ''};
    dialogData.message = 'Та зээлийн процессийг барьцаагүй үргэлжлүүлэхдээ итгэлтэй байна уу?';
    config.data = dialogData;
    const dialogRef = this.dialog.open(ConfirmDialogComponent, config);
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.submit(overlayRef, formName, formValue);
      } else {
        overlayRef.dispose();
      }
    });
  }

  submitRemoveCoBorrowerForm(event) {
    this.form = event[1];
    this.continueProcess('formFields', event[0]);
  }

  setDecisionForm() {
    if (this.isLoanSendForm || this.isLoanPermitForm) {
      let valueField = this.form.find(field => field.id === this.DECISION_RETURNED_USERS);
      if (valueField === undefined) {
        valueField = this.form.find(field => field.id === this.DECISION_RECEIVERS);
      }
      if (null != valueField) {
        const defaultValue = valueField.formFieldValue.defaultValue;
        valueField.formFieldValue.defaultValue = valueField.options.find(option => option.value === defaultValue).id;
      }
    }
  }

  isRelatedToDecision(): boolean {
    const ZSH = this.task.name.search('ZSH');
    const ZSI = this.task.name.search('ZSI');
    const LOAN_DECISION = this.task.name.search('Зээл шийдвэрлэх');
    return ZSH > -1 || ZSI > -1 || LOAN_DECISION > -1;
  }

  isRegularTask(): boolean {
    return !this.isLoanAccount &&
      !this.isLoanSendForm && !this.isLoanPermitForm && !this.isCollateralLoanAccount &&
      !this.isMicroSimpleCalculation && !this.isMicroLoanDownloadKhur &&
      !this.isMicroLoanDownloadKhurCoBorrower && !this.isMicroCreateCollateralAccount;
  }

  bigDecimalToNumber(getId): number {
    const checkForm: FormsModel[] = this.getFinalForm();
    return Number((checkForm.find(field => field.id === getId).formFieldValue.defaultValue).split(',').join(''));
  }

  noCollateralOnLoanAmountCalculation(): boolean {
    if (sessionStorage.getItem('loanProduct')) {
      return this.commonSb.checkFieldValue(this.form, 'hasCollateral', 'Үгүй');
    }
  }

  openUdfDialog() {
    const colId = this.commonSb.getFormValue(this.form, 'collateralId');
    const config = new MatDialogConfig();
    config.data = {
      title: 'UDF талбар',
      form: this.form,
      instanceId: this.instanceId,
      taskId: this.task.taskId,
      collateralId: colId,
      isCompleted: this.buttonState
    };
    this.dialog.open(CollateralUdfDialogComponent, {width: '620px', disableClose: true, data: config.data});
  }

  getRequestInfo(requestId: string): void {
    this.sb.getRequestById(requestId).subscribe(res => {
      if (null != res && null != res.entity) {
        this.loanType = res.entity.productCategory;
        if (null != res.entity.loanProductDescription) {
          const productId = res.entity.loanProductDescription.substring(0, 4);
          let applicationCategory = '';
          if (this.loanType === 'consumptionLoan') {
            applicationCategory = LOAN_TYPES[0];
          } else if (this.loanType === 'microLoan') {
            applicationCategory = LOAN_TYPES[1];
          }
          this.sb.getLoanProductByIdAndCategory(productId, applicationCategory).subscribe(response => {
            if (null != response) {
              this.hasInsurance = response.hasInsurance;
            }
          });
        }
      }
    });
  }

  fieldActionHandler(fieldId: string): void {
    if (null != this.formGroup.fieldAction) {
      const functionName = this.formGroup.fieldAction[fieldId];
      if (null != functionName) {
        this.callFunctionByName(functionName, this.commonSb.setOverlay(), fieldId);
      }
    }
    if (fieldId == 'loanProduct' && this.task.name === '11. Зээлийн хэмжээ тооцох') {
      const fieldValue = this.commonSb.getFormValue(this.form, fieldId);
      this.hasCollateral(fieldValue);
    }
  }

  private callFunctionByName(functionName: string, overlayRef: OverlayRef, fieldId?: string) {
    switch (functionName) {
      case 'getLoanAccountInfo':
        this.getLoanAccountInfo(overlayRef, fieldId);
        break;
      case 'setRegisterValidation':
        const borrowerTypeCoBorrowerField = this.commonSb.getSelectedFieldOptionIdByFieldId(this.form, 'borrowerTypeCoBorrower');
        this.borrowerTypeCoBorrower = borrowerTypeCoBorrowerField === 'organizationTypeCoBorrower' ? 'organizationTypeCoBorrower' : null;
        overlayRef.dispose();
        break;
      case 'calculate':
        this.calculate();
        overlayRef.dispose();
        break;
      default:
        if (null != overlayRef) {
          overlayRef.dispose();
        }
        break;
    }
  }

  private saveCollateralFromAccount(form): void {
    const submitForm = {caseInstanceId: this.instanceId, specialForms: {COLLATERAL_TABLE: form}};
    this.sb.saveCollateralFromAccount(this.instanceId, submitForm)
      .subscribe(() => undefined);
  }

  private getCollateralProducts(): void {
    const collateralOptions: FieldOptions[] = [];
    this.sb.getCollateralProducts().subscribe(res => {
      res.forEach(product => collateralOptions.push({id: product.id, value: product.description}));
      this.commonSb.setFieldOptions(this.formGroup.fieldGroups[2].fields, collateralOptions, 'collateralAsset');
      this.commonSb.setFieldOptions(this.formGroup.fieldGroups[2].flexibleForm[2], collateralOptions, 'collateralAsset');
    });
  }

  private isMongolBankRelatedTask(): boolean {
    return this.task.definitionKey === TASK_DEF_CONSUMPTION_MONGOL_BANK || this.task.definitionKey === TASK_DEF_MICRO_MONGOL_BANK
      || this.task.definitionKey === TASK_DEF_MICRO_MONGOL_BANK_NEW_CORE
      || this.task.definitionKey === TASK_DEF_MICRO_MONGOL_BANK_EXTENDED;
  }

  private getLoanAccountInfo(overlayRef: OverlayRef, fieldId: string): void {
    const field = this.form.find(form => form.id === fieldId);
    this.sb.getLoanAccountInfo(field.formFieldValue.defaultValue).subscribe(res => {
      this.sb.setFieldDefaultValue(this.form, res);
      overlayRef.dispose();
    }, () => overlayRef.dispose());
  }

  getTableValue() {
    this.sb.getCompletedTaskInfo(this.instanceId).subscribe(res => {
      this.tableData = res;
    });
    return this.tableData;
  }

  private hasCollateral(fieldValue: string) {
    const productId = fieldValue.substring(0, 4);
    let field = this.commonSb.getFieldById(this.form, "hasCollateral");
    this.sb.getLoanProductByIdAndCategory(productId, "CONSUMER").subscribe(res => {
      if (field != null) {
        if (res.hasCollateral) {
          field.formFieldValue.defaultValue = YES_MN;
          field.disabled = true;
        } else {
          field.disabled = false;
        }
      }
    });
  }
}
