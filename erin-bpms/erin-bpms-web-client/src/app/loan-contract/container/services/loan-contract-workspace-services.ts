import {ErinCommonWorkspaceService} from '../../../common/model/erin-model';
import {ACCOUNT_INFO_FIELDS, TASK_DEF_PREPARE_MORTGAGE_LOAN_CONTRACT_INFO} from '../../../branch-banking/model/branch-banking-constant';
import {BranchBankingSandbox} from '../../../branch-banking/services/branch-banking-sandbox.service';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {FormsModel} from '../../../models/app.model';
import {Injectable} from '@angular/core';
import {CaseViewSandboxService} from '../../../case-view-page/case-view-sandbox.service';
import {TaskItem} from '../../../case-view-page/model/task.model';
import {LOANCONTRACT} from '../../../loan-search-page/models/process-constants.model';
import {CONTRACT_ACCOUNT_INFO_FIELDS} from '../../model/loan-contract-constant';
import {DialogService} from '../../../services/dialog.service';
import {MatDialogConfig} from '@angular/material/dialog';
import {LoanContractDialogComponent} from '../../component/loan-contract-dialog/loan-contract-dialog.component';
import {NavigationSandboxService} from '../../../app-navigation/navigation-sandbox.service';

const NON_CLEAR_VARIABLES = ['productName', 'productDescription', 'loanAmount', 'accountNumber'];
@Injectable()
export class LoanContractWorkspaceServices implements ErinCommonWorkspaceService {

  constructor(private commonService: CommonSandboxService, private sb: CaseViewSandboxService,
              private branchBankingService: BranchBankingSandbox, private dialogService: DialogService,
              private navSb: NavigationSandboxService) {
  }

  instanceId: string;
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
  form: FormsModel[] = [];
  typeOfCollateral;

  tableDataMap = {};
  checkedValue = {};

  // constant fields
  DIALOG = 'dialog';
  CHECKBOX = 'checkbox';
  CHECKBOXCONFIRM;

  private static setDialogConfig(buttons, message, width, form?: FormsModel[]): MatDialogConfig {
    const dialogData = {buttons, message, title: 'Хэвлэх гэрээ сонгох'};
    if (form) {
      dialogData['formField'] = form;
    }
    const config = new MatDialogConfig();
    config.width = width;
    config.data = dialogData;
    return config;
  }

  async handleFieldAction(functionName, form: FormsModel[], tableDataMap: any) {
    const overlayRef = this.commonService.setOverlay();
    return new Promise(async resolve => {
      await this.callFunctionByName(functionName, null, form, tableDataMap, overlayRef).then(state => {
        resolve(state);
      });
    });
  }

  async handleAction(event, form: FormsModel[], tableDataMap: any) {
    const overlayRef = this.commonService.setOverlay();
    return new Promise(async resolve => {
      await this.callFunctionByName(event.action, event.data, form, tableDataMap, overlayRef).then(state => {
        resolve(state);
      });

    });
  }

  setFormValue(value: {}, form: FormsModel[]) {
    return null;
  }

  setInstanceId(instanceId: string) {
    this.instanceId = instanceId;
  }

  setTask(task: TaskItem) {
    this.task = task;
  }

  private async callFunctionByName(functionName: string, data: any, form: FormsModel[], tableDataMap: any, overlayRef) {
    switch (functionName) {
      case 'getLoanAccountInfo' :
        this.getLoanAccountInfo(form, overlayRef);
        break;
      case 'getAccountInfo':
        this.getAccountInfo(form, overlayRef);
        break;
      case 'getInquireCollateralInfo':
        return new Promise(async resolve => {
          await this.getInquireCollateralInfo(form, tableDataMap, overlayRef);
          const response  = await this.filterSet(overlayRef, form);
          resolve(response);
        });
      case 'clearForm':
        this.commonService.clearForm(form, NON_CLEAR_VARIABLES);
        overlayRef.dispose();
        break;
      case 'save':
        return new Promise(async resolve => {
          const state = await this.save(overlayRef, true, form, tableDataMap, data.value);
          resolve(Boolean(state) ? {type: 'event', action: 'save'} : {type: 'error', message: state});
        });
      case 'continue':
      case 'createContract':
        return new Promise(async resolve => {
          const state = await this.submit(overlayRef, form, tableDataMap, null, false);
          resolve(Boolean(state) ? {type: 'event', action: 'submit'} : {type: 'error', message: state});
        });
      case 'createContractWithDialog':
        return new Promise(async resolve => {
          const output = await this.continueWithDialog(form, tableDataMap, overlayRef);
          resolve(output);
        });
      case 'filterCollateral':
        return this.filterSet(overlayRef, form);
      default:
        overlayRef.dispose();
        return new Promise(async resolve => {
          resolve({type: null});
        });
    }
  }

  async filterSet(overlayRef, form: FormsModel[]) {
    return new Promise(async resolve => {
      const filterValue = this.filterCollateral(form);
      overlayRef.dispose();
      resolve(filterValue == null ? null : {type: 'filter', value: filterValue, property: 'collateralType'});
    });
  }

  private getDialogFields(form: FormsModel[]): FormsModel[]  {
    const dialogForm: FormsModel[] = [];
    const dialogField: FormsModel = this.commonService.getFieldById(form, this.DIALOG);
    dialogField.formFieldValue.defaultValue = null;
    dialogForm.push(dialogField);

    if (this.task.definitionKey === TASK_DEF_PREPARE_MORTGAGE_LOAN_CONTRACT_INFO) {
      this.CHECKBOXCONFIRM = this.commonService.cloneObject(this.commonService.getFieldById(form, this.CHECKBOX));
    } else {
      this.CHECKBOXCONFIRM = this.commonService.getFieldById(form, this.CHECKBOX);
    }
    if (this.CHECKBOXCONFIRM != null) {
      this.CHECKBOXCONFIRM.optionsCheckbox = this.CHECKBOXCONFIRM.options;
      this.CHECKBOXCONFIRM.options = [];
      dialogForm.push(this.CHECKBOXCONFIRM);
    }
    return dialogForm;
  }

  private setDialogResponse(form: FormsModel[]) {
    const checkboxField = this.commonService.getFieldById(form, this.CHECKBOX);
    if (null != checkboxField) {
      if (checkboxField.options != null) {
        checkboxField.optionsCheckbox = checkboxField.options;
        checkboxField.options = [];
      }
      if (checkboxField.formFieldValue.defaultValue != null) {
        let valueArray = '';
        for (const value of checkboxField.formFieldValue.defaultValue) {
          const opId = checkboxField.optionsCheckbox.find(op => op.value === value).id;
          valueArray += opId + '###';
        }
        checkboxField.formFieldValue.defaultValue = valueArray;
      }
    }
    this.switchFieldIdAndValue(form, this.DIALOG, 'id');
  }

  async continueWithDialog(form: FormsModel[], tableDataMap: any, overlayRef) {
    return new Promise(async resolve => {
      const buttons = [
        {actionId: 'continue', actionName: 'ҮРГЭЛЖЛҮҮЛЭХ'},
        {actionId: 'cancel', actionName: 'БОЛИХ'},
      ];
      const config = LoanContractWorkspaceServices.setDialogConfig(buttons, null, '500px', this.getDialogFields(form));
      await this.dialogService.openCustomDialog(LoanContractDialogComponent, config).then(async (res: any) => {
        if (null != res.action && res.action.actionId === 'continue') {
          this.setDialogResponse(form);
          const state = await this.submit(overlayRef, form, tableDataMap, null, false);
          resolve(Boolean(state) ? {type: 'event', action: 'submit'} : {type: 'error', message: state});
        } else {
          overlayRef.dispose();
        }
      });
    });
  }

  async submit(overlayRef, form: FormsModel[], tableDataMap: any, formName?, formValue?, extraForm?) {
    let submitForm: FormsModel[];
    if (!!extraForm) {
      submitForm = extraForm;
    } else {
      submitForm = this.commonService.clone(form);
    }

    submitForm = this.commonService.clone(submitForm);
    // this.switchFieldIdAndValue(submitForm, this.LOAN_PRODUCT, this.ID);
    const tableParam = this.getParam(form, tableDataMap);
    return new Promise(resolve => {
      this.sb.submitForm(this.task.taskId, this.task.instanceId, null, submitForm,
        this.task.name, this.task.definitionKey, LOANCONTRACT, tableParam).subscribe(() => {
        overlayRef.dispose();
        resolve(true);
      }, () => {
        overlayRef.dispose();
        resolve(false);
      });
    });

  }

  private async save(overlayRef, isOnlySave, form: FormsModel[], tableDataMap: any, value?) {
    let saveForms;
    if (!!value) {
      saveForms = value;
    } else {
      saveForms = form;
    }

    // this.switchFieldIdAndValue(saveForms, this.LOAN_PRODUCT, this.ID);
    const parameters = this.getParam(form, tableDataMap);
    return new Promise(resolve => {
      this.sb.saveTasks(isOnlySave, this.commonService.clone(saveForms), this.instanceId,
        this.task.name, this.task.definitionKey, parameters, null).subscribe(() => {
        overlayRef.dispose();
        resolve(true);
      }, (err) => {
        overlayRef.dispose();
        resolve(err);
      });
    });
  }

  private getParam(form: FormsModel[], tableDataMap) {
    if (this.task.definitionKey === 'user_task_view_loan_collateral' && !this.isEmptyTable(tableDataMap)) {
      return  {table: tableDataMap[this.task.definitionKey], filterKey: this.filterCollateral(form), user: this.navSb.getUserName()};
    }
    return {user: this.navSb.getUserName()};
  }

  private isEmptyTable(tableDataMap): boolean {
    return tableDataMap[this.task.definitionKey] == null;
  }

  // it might be used in feature when form have dropdown field
  private switchFieldIdAndValue(form, id: string, setProperty: string) {
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

  private changeOption(field: FormsModel, id: string, setProperty: string) {
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

  private getAccountInfo(form: FormsModel[], overlayRef) {
    const accountNumber = this.commonService.getFormValue(form, 'accountNumber');
    if (accountNumber == null || accountNumber === '') {
      overlayRef.dispose();
      return;
    }
    this.branchBankingService.getAccountInfo(accountNumber, this.instanceId).subscribe((res: any) => {
      this.commonService.setFieldDefaultValue(form, res);
      overlayRef.dispose();
    }, () => {
      this.commonService.removeFieldsDefaultValue(form, ACCOUNT_INFO_FIELDS);
      overlayRef.dispose();
    });
  }


  private getLoanAccountInfo(form: FormsModel[], overlayRef) {
    const accountNumber = this.commonService.getFormValue(form, 'accountNumber');
    if (accountNumber == null || accountNumber === '') {
      overlayRef.dispose();
      return;
    }
    this.sb.getLoanAccountInfo(accountNumber).subscribe((res: any) => {
      res['productName'] = res['productDescription'];
      this.commonService.setFieldDefaultValue(form, res);
      overlayRef.dispose();
    }, () => {
      this.commonService.removeFieldsDefaultValue(form, CONTRACT_ACCOUNT_INFO_FIELDS);
      overlayRef.dispose();
    });
  }

  async getInquireCollateralInfo(form: FormsModel[], tableDataMap: any, overlayRef) {
    const accountNumber = this.commonService.getFormValue(form, 'accountNumber');

    if (accountNumber == null || accountNumber === '') {
      overlayRef.dispose();
      return;
    }

    return this.sb.getInquireCollateralInfo(accountNumber).toPromise().then((res: any) => {
      this.tableDataMap[this.task.definitionKey] = res;
      tableDataMap[this.task.definitionKey] = res;
      overlayRef.dispose();
    }).catch(() => {
      overlayRef.dispose();
    });

  }

  private filterCollateral(form: FormsModel[]): string {
    const colType = this.commonService.getFormValue(form, 'typeOfCollateral');
    if (colType == null || colType === '') {
      return null;
    }
    if (colType === 'Үл хөдлөх хөрөнгө') {
      return 'I';
    }
    if (colType === 'Хөдлөх хөрөнгө') {
      return 'OV';
    }
    if (colType === 'Тоног төхөөрөмж хөрөнгө') {
      return 'M';
    }
  }
}


