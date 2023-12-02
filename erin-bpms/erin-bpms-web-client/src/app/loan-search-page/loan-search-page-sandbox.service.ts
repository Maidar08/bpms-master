import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthenticationSandboxService } from '../erin-aim/authentication/authentication-sandbox.service';
import { ApplicationState } from '../erin-aim/statemanagement/state/ApplicationState';
import { FormsModel } from '../models/app.model';
import { AuthModel } from '../models/auth.model';
import { ContractRequestModel, OrganizationRequestModel, ProcessTypeModel, RequestModel } from './models/process.model';
import { ProcessRequestService } from './services/process-request.service';

@Injectable({
  providedIn: 'root'
})
export class LoanSearchPageSandbox {
  auth$ = this.store.pipe(select(state => {
    return state.auth;
  }));
  private auth: AuthModel;
  private parentUrl = 'loan-page/';
  username: string;

  constructor(private processService: ProcessRequestService,
              private store: Store<ApplicationState>,
              private router: Router,
              private auths: AuthenticationSandboxService) {
    this.auth$.subscribe(res => this.auth = res);
    auths.getCurrentUserName().subscribe(res => this.username = res);
  }

  getUserName(): string {
    return this.username;
  }

  getProcessTypes(processTypeCategory: string): Observable<ProcessTypeModel[]> {
    return this.processService.getProcessTypes(processTypeCategory);
  }

  getAllRequests(startDate: string, endDate: string) {
    return this.processService.getAllRequests(startDate, endDate);
  }

  getRequests(startDate: string, endDate: string): Observable<RequestModel[]> {
    return this.processService.getRequests(this.auth.userGroup, startDate, endDate);
  }


  getSubGroupRequest(startDate: string, endDate: string): Observable<RequestModel[]> {
    return this.processService.getSubGroupRequests(this.auth.userGroup, startDate, endDate);
  }

  getMyLoanRequests(startDate: string, endDate: string) {
    return this.processService.getMyLoanRequests(this.auth.userName, startDate, endDate);
  }

  getRequestsByCustomerNumber(customerNumber: string) {
    return this.processService.getRequestsByCustomerNumber(customerNumber);
  }

  getEbankRequests(startDate: string, endDate: string) {
    return this.processService.getEbankRequests('Internet bank', this.auth.userGroup, startDate, endDate);
  }

  getRequestByInstanceId(instanceId: string): Observable<RequestModel> {
    return this.processService.getRequestsByInstanceId(instanceId);
  }

  getUsersById(groupId: string): Observable<any> {
    return this.processService.getUserByRoleId(groupId);
  }

  updateAssignedUser(userId: string, requestId: string): Observable<any> {
    return this.processService.updateAssignedUser(userId, requestId);
  }

  deleteTableRow(requestId: string, instanceId: string, state: string): Observable<any> {
    return this.processService.deleteTableRow(requestId, instanceId, state);
  }

  getRequestForm(formId: string): Observable<FormsModel[]> {
    return this.processService.getRequestForm(formId);
  }

  getProductsByAppCategoryAndBorrowerType(appCategory: string, borrowerType: string): Observable<any[]> {
    return this.processService.getProductsByAppCategoryAndBorrowerType(appCategory, borrowerType);
  }

  createRequest(formId: string, forms: FormsModel[], processTypeCategory?: string, processInstanceId?: string, oldContractId?: string, registerNumber?: string): Observable<string> {
    return this.processService.createRequest(formId, forms, this.auth.userGroup, this.auth.userName, this.auth.userGroup, processTypeCategory, processInstanceId, oldContractId, registerNumber);
  }

  startProcess(processRequestId: string, isReadOnlyReq: boolean, requestType?, noHeaderSection?: boolean): Observable<any> {
    return this.processService.startProcess(processRequestId).pipe(map(res => {
      this.routeToCaseView(res, isReadOnlyReq, requestType, noHeaderSection);
    }));
  }

  startBranchBankingProcess(processRequestId: string, processInfo, createdUser): Observable<any> {
    return this.processService.startProcess(processRequestId, processInfo, createdUser).pipe(map(res => {
      this.routeToBranchBanking(res);
    }));
  }

  routeToCaseView(id: string, isReadOnlyReq: boolean, requestType?: string, noHeaderSection?: boolean, productCode?: string, processRequestId?: string) {
    this.router.navigateByUrl(this.parentUrl + 'case-view/' + id + '/' + isReadOnlyReq + '/' + noHeaderSection, {state: {data: requestType, productCode, processRequestId}});
  }

  routeToBranchBanking(id: string) {
    this.router.navigateByUrl(this.parentUrl + 'branch-banking/' + id);
  }

  routeToContractCaseView(id: string, category: string, requestType: string) {
    this.router.navigateByUrl(this.parentUrl + 'loan-contract/case-view/' + id, {state: {data: category, requestType}});
  }

  routeToSalaryOrganizationCaseView(id: string, requestData: any, actionType?: string) {
    this.router.navigateByUrl(this.parentUrl + 'salary-organization/case-view/' + id, {state: {data: requestData, actionType}});
  }

  routeToLeasingOrganizationCaseView(id: string,  requestData: {}, actionType?: string) {
    this.router.navigateByUrl(this.parentUrl + 'leasing-organization/case-view/' + id, {state: {data: requestData, actionType}});
  }

  checkIsOrganizationRegistered(id: string, requestType: string) {
    return this.processService.checkIsRegisteredOrganization(id, requestType);
  }

  downloadExcelReport(urlHeader: string, topHeader: string, searchKey: string, loanRequests) {
    return this.processService.downloadExcelReport(urlHeader, topHeader, searchKey, this.auth.userGroup, loanRequests);
  }

  /* Loan Contract request */
  getContractRequests(type: string, startDate: string, endDate: string): Observable<ContractRequestModel[]> {
    let groupId = null;
    if (type.includes('group-request')) {
      groupId = this.auth.userGroup;
    }
    return this.processService.getContractRequests(type, groupId, startDate, endDate);
  }

  updateAssigneeUser(requestBody: {}) {
    return this.processService.updateAssigneeUser(requestBody);
  }

  /* organization registration requests sandbox service  */
  getOrganizationRequests(topHeader: string, startDate: string, endDate: string, branchId: any): Observable<OrganizationRequestModel[]> {
    return this.processService.getOrganizationRequests(topHeader, startDate, endDate, branchId);
  }
}

