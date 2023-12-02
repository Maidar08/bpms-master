import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {FormsModel} from '../models/app.model';
import {DocumentModel, DocumentsModel, DocumentTypeModel, NoteModel, SalaryModel, SaveSalaryModel, TaskItem, TaskModel} from './model/task.model';
import {ProcessService} from './services/process.service';
import {CommonService} from '../common/service/common.service';
import {LoanContractService} from './services/loan-contract.service';

@Injectable({
  providedIn: 'root'
})
export class CaseViewSandboxService {

  private parentUrl = 'loan-page/';

  constructor(
    private loanContractService: LoanContractService,
    private processService: ProcessService,
    private commonService: CommonService) {
  }

  getParentUrl(): string {
    return this.parentUrl;
  }

  getInstanceForm(instanceId: string): Observable<TaskModel> {
    return this.processService.getInstanceForm(instanceId);
  }

  getAccountList(instance: string): Observable<any> {
    return this.processService.getAccountList(instance);
  }

  getTask(instanceId: string, type: string, requestType: string): Observable<TaskItem[]> {
    return this.processService.getTask(instanceId, type, requestType);
  }

  getSubModuledTask(instanceId: string, type: string): Observable<any[]> {
    return this.processService.getSubModuledtask(instanceId, type);
  }

  getActiveTasks(instanceId: string, requestType?: string, processRequestId?: string, productType?: string): Observable<TaskItem[]> {
    return this.processService.getActiveTasks(instanceId, requestType, processRequestId, productType);
  }

  getCustomerVariables(instanceId: string, type: string): Observable<FormsModel[]> {
    return this.processService.getCustomerVariables(instanceId, type);
  }

  manualActivateTask(executionId: string) {
    return this.processService.manualActivateTask(executionId);
  }

  switchActiveTasks(taskId: string, instanceId: string, defKey: string): Observable<TaskModel> {
    return this.processService.switchActiveTask(taskId, instanceId, defKey);
  }

  getFromRelation(defKey: string) {
    return this.processService.getFromRelation(defKey);
  }

  getSalary(instanceId: string): Observable<SalaryModel> {
    return this.processService.getSalary(instanceId);
  }

  getDefaultSalary(instanceId: string): Observable<SaveSalaryModel> {
    return this.processService.getDefaultSalary(instanceId);
  }

  getXypSalary(instanceId: string): Observable<any> {
    return this.processService.getXypSalary(instanceId);
  }

  setSalaryAverage(salary): Observable<SalaryModel> {
    return this.processService.setSalaryAverage(salary);
  }

  saveSalary(salaryModel: SaveSalaryModel, instanceId: string): Observable<SaveSalaryModel> {
    return this.processService.saveSalary(salaryModel, instanceId);
  }

  calculateTask(taskId: string, taskType: string, entity: string, form: FormsModel[], instanceId: string): Observable<any> {
    return this.processService.calculateTask(taskId, taskType, entity, form, instanceId);
  }

  getBalanceFromProcessParameter(caseInstanceId: string): Observable<any> {
    return this.processService.getBalanceFromProcessParameter(caseInstanceId);
  }

  calculateMicroBalance(caseInstanceId: string, form: any): Observable<any> {
    return this.processService.calculateMicroBalance(caseInstanceId, form);
  }

  saveMicroBalance(caseInstanceId: string, form): Observable<any> {
    return this.processService.saveMicroBalance(caseInstanceId, form);
  }

  submitForm(currentTaskId: string, caseInstanceId: string, processRequestId: string,
    form: FormsModel[], taskName: string, defKey: string, entity: string, tableDataMap?) {
    return this.processService.submitForm(currentTaskId, caseInstanceId, processRequestId, form, taskName, defKey, entity, tableDataMap);
  }

  submitThenCallUserTask(currentTaskId: string, caseInstanceId: string, processRequestId: string, form: FormsModel[], properties) {
    return this.processService.submitThenCallUserTask(currentTaskId, caseInstanceId, processRequestId, form, properties);
  }

  getParametersByName(instanceId: string, parameterName: string): Observable<any> {
    return this.processService.getParametersByName(instanceId, parameterName);
  }

  createLoanAccount(currentTaskId: string, form: any[], instanceId: string, taskName: string, defKey: string): Observable<any> {
    return this.processService.createLoanAccount(currentTaskId, form, instanceId, taskName, defKey);
  }

  saveTasks(isOnlySave: boolean, form: FormsModel[], instanceId: string, taskName: string, defKey: string, parameter, entityName?: string): Observable<any> {
    return this.processService.saveTasks(isOnlySave, form, instanceId, taskName, defKey, parameter, entityName);
  }

  printLoanContract(form: FormsModel[], instanceId: string, entityName?: string): Observable<any> {
    return this.processService.printLoanContract(form, instanceId, entityName);
  }

  getDocuments(instanceId: string): Observable<DocumentsModel[]> {
    return this.processService.getDocuments(instanceId);
  }

  getBasicDocumentType(): Observable<DocumentTypeModel[]> {
    return this.processService.getBasicDocumentType();
  }

  getSubDocumentTypes(parentId: string): Observable<DocumentTypeModel[]> {
    return this.processService.getSubDocumentType(parentId);
  }

  saveDocuments(instanceId: string, type: string, subType: string, documents: DocumentModel[]): Observable<any> {
    return this.processService.saveDocuments(instanceId, type, subType, documents);
  }

  downloadInfo(taskId: string, caseInstanceId: string, processRequestId: string, entity, form: FormsModel[]): Observable<any> {
    return this.processService.downloadInfo(taskId, caseInstanceId, processRequestId, entity, form);
  }

  getNotes(instanceId: string, type: string): Observable<NoteModel[]> {
    return this.processService.getNotes(instanceId, type);
  }

  saveCompletedTask(completedTask): Observable<any> {
    return this.processService.saveCompletedTask(completedTask);
  }

  getCollateralAssets(caseInstanceId: string): Observable<any> {
    return this.processService.getCollateralAssets(caseInstanceId);
  }

  getCompletedForm(taskId: string, caseInstanceId: string): Observable<any> {
    return this.processService.getCompletedForm(taskId, caseInstanceId);
  }

  // collateral
  getCollateral(cifNumber: string, caseInstanceId: string): Observable<any> {
    return this.processService.getCollateral(cifNumber, caseInstanceId);
  }

  refreshCollateral(cifNumber: string, caseInstanceId: string): Observable<any> {
    return this.processService.refreshCollateral(cifNumber, caseInstanceId);
  }

  getUpdateCollateralForm(collateralId: string, collateralType: string): Observable<any> {
    return this.processService.getUpdateCollateralForm(collateralId, collateralType);
  }

  updateCollateral(instanceId: string, collateralId: string, collateralType: string, cif: string, form): Observable<boolean> {
    return this.processService.updateCollateral(instanceId, collateralId, collateralType, cif, form);
  }

  saveCollateralFromAccount(collateralId: string, form): Observable<boolean> {
    return this.processService.saveCollateralFromAccount(collateralId, form);
  }

  getLoanProductById(productCode: string): Observable<any> {
    return this.processService.getLoanProductById(productCode);
  }

  getRequestById(requestId: string): Observable<any> {
    return this.processService.getRequestById(requestId);
  }

  getCollateralUdf(savedValues: object): Observable<any> {
    return this.processService.getCollateralUdfForm(savedValues);
  }

  getCollateralUdfFromProcessTable(instanceId: string, collateralId: string): Observable<any> {
    return this.processService.getCollateralUdfFromProcessTable(instanceId, collateralId);
  }

  calculateCollateralAmount(form: any[]): Observable<any> {
    return this.processService.calculateCollateralAmount(form);
  }

  saveCollateralUdf(instanceId: string, taskId: string, collateralId: string, udfForm: FormsModel[]): Observable<any> {
    return this.processService.saveCollateralUdf(instanceId, taskId, collateralId, udfForm);
  }

  saveUdfToProcessTable(instanceId: string, collateralId: string, parameterEntityType: string, udfForm: FormsModel[]): Observable<any> {
    return this.processService.saveUdfToProcessTable(instanceId, collateralId, parameterEntityType, udfForm);
  }

  getLoanProductByIdAndCategory(productCode: string, applicationCategory: string): Observable<any> {
    return this.processService.getLoanProductByIdAndCategory(productCode, applicationCategory);
  }

  getCollateralProducts(): Observable<any> {
    return this.processService.getCollateralProducts();
  }

  setFieldDefaultValue(form: FormsModel[], newValue: {}): FormsModel[] {
    return this.commonService.setFieldDefaultValue(form, newValue);
  }

  getLoanAccountInfo(accountNumber: string): Observable<any> {
    return this.loanContractService.getLoanAccountInfo(accountNumber);
  }

  getInquireCollateralInfo(accountNumber: string): Observable<any> {
    return this.loanContractService.getInquireCollateralInfo(accountNumber);
  }

  getBpmnForm(processInstanceId: string, taskDefinitionKey: string): Observable<any> {
    return this.processService.getBpmnForm(processInstanceId, taskDefinitionKey);
  }

  submitBpmForm(productType: string, instanceId: string): Observable<any> {
    return this.processService.submitBpmnForm(productType, instanceId);
  }

  updateOrganizationRequest(contractId: string, instanceId: string, processType: string, action: string, makerId?: string): Observable<any> {
    return this.processService.updateOrganizationRequest(contractId, instanceId, processType, action, makerId);
  }

  printOrganizationContract(contractId: string, instanceId: string, processType: string): Observable<any> {
    return this.processService.printOrganizationContract(contractId, instanceId, processType);
  }

  saveOrganizationInfo(contractId: any, processType: any, taskId: any, definitionKey: any, instanceId: any, form: FormsModel[]) {
    return this.processService.saveOrganizationInfo(contractId, processType, taskId, definitionKey, instanceId , form);
  }

  getCompletedTaskInfo(instanceId: string) {
    return this.processService.getCompletedServiceTaskInfo(instanceId);
  }
}
