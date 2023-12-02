import {ErinCommonWorkspaceService} from '../../../common/model/erin-model';
import {BranchBankingSandbox} from '../../../branch-banking/services/branch-banking-sandbox.service';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {Document, FormsModel} from '../../../models/app.model';
import {Injectable} from '@angular/core';
import {CaseViewSandboxService} from '../../../case-view-page/case-view-sandbox.service';
import {TaskItem} from '../../../case-view-page/model/task.model';
import {EDIT_PERMISSION_ORGANIZATION_REGISTRATION, LOANCONTRACT} from '../../../loan-search-page/models/process-constants.model';
import {DialogService} from '../../../services/dialog.service';
import {NavigationSandboxService} from '../../../app-navigation/navigation-sandbox.service';
import {AuthenticationSandboxService} from 'src/app/erin-aim/authentication/authentication-sandbox.service';
import {OverlayRef} from '@angular/cdk/overlay';
import {forkJoin, from, Observable} from 'rxjs';
import {CONTRACT_NUMBER} from '../../model/organization-registrataion-constants';
import {FileService} from '../../../common/service/file-service';
import {
  LEASING_ORGANIZATION_CONTRACT,
  SALARY_ORGANIZATION_CONTRACT,
  TASK_DEF_SALARY_ORGANIZATION_REGISTRATION
} from '../../../case-view-page/model/task-constant';
import {PermissionItem} from '../../../models/auth.model';
import {Router} from '@angular/router';
import {LoanSearchPageSandbox} from '../../../loan-search-page/loan-search-page-sandbox.service';

@Injectable()
export class OrganizationRegistrationWorkspaceServices implements ErinCommonWorkspaceService {

  constructor(private commonService: CommonSandboxService, private sb: CaseViewSandboxService,
              private branchBankingService: BranchBankingSandbox, private dialogService: DialogService,
              private navSb: NavigationSandboxService, private auth: AuthenticationSandboxService,
              private fileService: FileService, private  router: Router, private loanPage: LoanSearchPageSandbox) {
  }

  instanceId: string;
  requestState: string;
  contextMenuAction: string;
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
  days;
  extendDays;
  userRole: string;
  userPermission: PermissionItem[];
  editPermission = null;
  userGroup;
  userName;


  async handleFieldAction(functionName, form: FormsModel[], tableDataMap: any, buttons: FormsModel[]) {
    this.disableFormAndButtonByRole(form, buttons);
    const overlayRef = this.commonService.setOverlay();
    return new Promise(async resolve => {
      await this.callFunctionByName(functionName, null, form, tableDataMap, overlayRef, buttons).then(state => {
        resolve(state);
      });
    });
  }

  async handleAction(event, form: FormsModel[], tableDataMap: any, buttons: FormsModel[]) {
    const overlayRef = this.commonService.setOverlay();
    return new Promise(async resolve => {
      await this.callFunctionByName(event.action, event.data, form, tableDataMap, overlayRef, buttons).then(state => {
        resolve(state);
      });

    });
  }

  private disableFormAndButtonByRole(form, buttons) {
    const branchId = this.commonService.getFormValue(form, 'branchId');
    const temporaryState = this.commonService.getFormValue(form, 'temporaryState');
    this.checkRoleAndPermission();
    if (this.editPermission === false || this.userGroup !== branchId) {
      form.forEach(field => {
        field.validations.push({name: 'readonly', configuration: null});
        field.disabled = true;
      });
      if (this.requestState !== 'БАТЛАГДААГҮЙ') {
        buttons['confirm'] = true;
        buttons['refuse'] = true;
        buttons['close'] = true;
        buttons['print'] = true;
      }
      buttons['confirm'] = true;
      buttons['refuse'] = true;
      buttons['confirmByDirector'] = true;
      buttons['saveOrg'] = true;
    }
    if (this.requestState === 'БАТЛАГДААГҮЙ' && this.userGroup === branchId) {
      buttons['print'] = true;
      buttons['confirm'] = false;
      buttons['refuse'] = false;
    }
    buttons['print'] = this.requestState !== 'БАТЛАГДСАН';
    if ((this.requestState === 'БАТЛАГДСАН' || this.requestState === 'ЦУЦЛАГДСАН') && (this.contextMenuAction !== 'edit' && this.contextMenuAction !== 'extend')) {
      form.forEach(field => {
        field.validations.push({name: 'readonly', configuration: null});
        field.disabled = true;
      })
      buttons['print'] = false;
      buttons['confirmByDirector'] = true;
      buttons['saveOrg'] = true;
    }
    if (this.requestState === 'ЦУЦЛАГДСАН') {
      buttons['print'] = true;
    }
    if (this.requestState === undefined && temporaryState === 'Батлагдсан') {
      form.forEach(field => {
        field.validations.push({name: 'readonly', configuration: null});
        field.disabled = true;
      })
      buttons['print'] = false;
      buttons['confirmByDirector'] = true;
      buttons['saveOrg'] = true;
      buttons['confirm'] = true;
      buttons['refuse'] = true;
    }
    if (this.requestState === undefined && temporaryState === 'Гэрээ цуцалсан') {
      form.forEach(field => {
        field.validations.push({name: 'readonly', configuration: null});
        field.disabled = true;
      })
      buttons['print'] = true;
      buttons['confirmByDirector'] = true;
      buttons['saveOrg'] = true;
      buttons['confirm'] = true;
      buttons['refuse'] = true;
    }
  }

  private checkRoleAndPermission() {
    this.auth.getCurrentUserRole().subscribe(res => this.userRole = res);
    this.auth.getCurrentUserPermission().subscribe(res => this.userPermission = res);
    this.auth.getCurrentUserGroup().subscribe(res => this.userGroup = res);
    this.auth.getCurrentUserName().subscribe(res => this.userName = res);
    this.editPermission = this.userPermission.find(value => value.id === EDIT_PERMISSION_ORGANIZATION_REGISTRATION);
    this.editPermission = this.editPermission != null;
  }

  setInstanceId(instanceId: string) {
    this.instanceId = instanceId;
  }

  setTask(task: TaskItem) {
    this.task = task;
  }

  setFormValue(value: {}, form: FormsModel[]) {
    this.contextMenuAction = value['actionValue'];
    if (value['requestData'] != null) {
      const requestData = value['requestData'];
      this.requestState = requestData['requestState'];
      if (requestData['requestState'] === 'БАТЛАГДААГҮЙ' || requestData['requestState'] === 'U') {
        form.forEach(field => {
          field.validations.push({name: 'readonly', configuration: null});
          field.disabled = true;
        });
      }
    }
    if (this.contextMenuAction === 'extend') {
      form.forEach(field => {
        if (field.id === 'contractDate' || field.id === 'contractPeriod') {
          field.validations.push({name: 'readonly', configuration: null});
          field.disabled = true;
        }
      })
    }
  }

  private async save(overlayRef, contractId: string, processType: string, form: FormsModel[]) {
    return new Promise(async resolve => {
      this.sb.saveOrganizationInfo(contractId, processType, this.task.taskId, this.task.definitionKey, this.instanceId, form).subscribe(() => {
        overlayRef.dispose();
        resolve(true);
      }, error => {
        overlayRef.dispose();
        resolve(error);
      });
    });
  }

  private async callFunctionByName(functionName: string, data: any, form: FormsModel[], tableDataMap: any, overlayRef, buttons?: FormsModel[]) {
    const processType = this.commonService.getFormValue(form, 'processTypeId');
    const contractId = this.commonService.getFormValue(form, 'contractNumber');
    switch (functionName) {
      case 'getEndDate' :
        this.getEndDate(form);
        overlayRef.dispose();
        break;
      case 'save':
        return new Promise(async resolve => {
          const state = await this.save(overlayRef, contractId, processType, form);
          resolve(Boolean(state) ? {type: 'event', action: 'save'} : {type: 'error', message: state});
        });
      case 'confirmByDirector':
        return new Promise(async resolve => {
          this.sb.updateOrganizationRequest(contractId, this.instanceId, processType, 'confirmByDirector', this.userName).subscribe(() => {
            form.forEach(field => {
              field.validations.push({name: 'readonly', configuration: null});
              field.disabled = true;
            });
            overlayRef.dispose();
            resolve(true);
          }, error => {
            overlayRef.dispose();
            resolve(error);
          });
        });
      case 'refuse':
        return new Promise(async resolve => {
          this.sb.updateOrganizationRequest(contractId, this.instanceId, processType, 'reject').subscribe(() => {
            buttons['refuse'] = true;
            buttons['confirm'] = true;
            overlayRef.dispose();
            resolve(true);
          }, error => {
            overlayRef.dispose();
            resolve(error);
          });
        });
      case 'confirm':
        return new Promise(async resolve => {
          this.sb.updateOrganizationRequest(contractId, this.instanceId, processType, 'confirm').subscribe(() => {
            buttons['refuse'] = true;
            buttons['confirm'] = true;
            overlayRef.dispose();
            resolve(true);
          }, error => {
            overlayRef.dispose();
            resolve(error);
          });
        });
      case 'close':
        if (processType != null && processType === 'salaryOrganization') {
          this.router.navigateByUrl('/loan-page/dashboard/salary-organization-request').then(() => {
          });
        } else {
          this.router.navigateByUrl('/loan-page/dashboard/leasing-organization-request').then(() => {
          });
        }
        overlayRef.dispose();
        return;
      case 'print':
        this.printDocument(overlayRef, form);
        break;
      default:
        overlayRef.dispose();
        return new Promise(async resolve => {
          resolve({type: null});
        });
    }
  }

  async submit(overlayRef, form: FormsModel[], tableDataMap: any, formName?, formValue?, extraForm?) {
    let submitForm: FormsModel[];
    if (!!extraForm) {
      submitForm = extraForm;
    } else {
      submitForm = this.commonService.clone(form);
    }

    submitForm = this.commonService.clone(submitForm);
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

  private getParam(form: FormsModel[], tableDataMap) {
    if (this.task.definitionKey === 'user_task_view_loan_collateral' && !this.isEmptyTable(tableDataMap)) {
      return {table: tableDataMap[this.task.definitionKey], filterKey: this.filterCollateral(form), user: this.navSb.getUserName()};
    }
    return {user: this.navSb.getUserName()};
  }

  private isEmptyTable(tableDataMap): boolean {
    return tableDataMap[this.task.definitionKey] == null;
  }

  private getEndDate(form: FormsModel[]) {
    const currentDate = this.commonService.getFormValue(form, 'contractDate');
    const chosenYear = this.commonService.getFormValue(form, 'contractPeriod');
    const contractExtensionYear = this.commonService.getFormValue(form, 'contractExtensionYear');
    if (chosenYear != null) {
      this.days = 0;
    }
    this.convertYearToDays(chosenYear);
    const endDate = new Date(currentDate);
    if (contractExtensionYear !== null) {
      this.convertExtendYearToDays(contractExtensionYear);
      endDate.setDate(endDate.getDate() + this.days + this.extendDays);
      return this.commonService.setFieldDefaultValue(form, {contractEndDate: endDate.toLocaleDateString()});
    } else {
      if (currentDate !== null) {
        endDate.setDate(endDate.getDate() + this.days);
        return this.commonService.setFieldDefaultValue(form, {contractEndDate: endDate.toLocaleDateString()});
      }
    }
  }

  private convertYearToDays(year: string): number {
    switch (year) {
      case '1 жил':
        return this.days = 365;
      case '2 жил':
        return this.days = 730;
      case '3 жил':
        return this.days = 1095;
      case '4 жил':
        return this.days = 1460;
      case '5 жил':
        return this.days = 1825;
    }
  }

  private convertExtendYearToDays(year: string): number {
    switch (year) {
      case '1 жил':
        return this.extendDays = 365;
      case '2 жил':
        return this.extendDays = 730;
      case '3 жил':
        return this.extendDays = 1095;
      case '4 жил':
        return this.extendDays = 1460;
      case '5 жил':
        return this.extendDays = 1825;
    }
  }

  private filterCollateral(form: FormsModel[]) {

  }

  private printDocument(overlayRef: OverlayRef, form: FormsModel[]) {
    const observables: Observable<any>[] = [];
    const contractId = this.commonService.getFormValue(form, CONTRACT_NUMBER);
    const processType = this.task.definitionKey.includes(TASK_DEF_SALARY_ORGANIZATION_REGISTRATION) ? 'salaryOrganization' : 'leasingOrganization';
    observables.push(from(this.sb.printOrganizationContract(contractId, this.instanceId, processType)));
    forkJoin(...observables).subscribe(resDocument => {
      if (Array.isArray(resDocument)) {
        this.openFileViewer(resDocument, true);
      } else {
        const documents: Document[] = [];
        documents.push(resDocument);
        this.openFileViewer(documents, true);
      }
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });
  }

  private openFileViewer(documents: Document[], isDownloadableFile: boolean) {
    const parentURL = this.sb.getParentUrl();
    const productType = this.task.definitionKey.includes(TASK_DEF_SALARY_ORGANIZATION_REGISTRATION) ? 'salary-organization' : 'leasing-organization';
    const componentUrl = parentURL + productType + '/' + this.instanceId;
    const contractName = this.task.definitionKey.includes(TASK_DEF_SALARY_ORGANIZATION_REGISTRATION) ? SALARY_ORGANIZATION_CONTRACT
      : LEASING_ORGANIZATION_CONTRACT;
    documents.find(document => document.name = contractName);
    this.fileService.showFileByPlainBase64(documents, parentURL, this.instanceId, isDownloadableFile, componentUrl, productType);
  }
}


