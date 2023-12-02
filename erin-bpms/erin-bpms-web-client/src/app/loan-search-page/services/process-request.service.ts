import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { CommonSandboxService } from '../../common/common-sandbox.service';
import { FormsModel } from '../../models/app.model';
import { BRANCHBANKING, LOAN, LOANCONTRACT, ORGANIZATION } from '../models/process-constants.model';
import { ContractRequestModel, OrganizationRequestModel, ProcessTypeModel, RequestModel } from '../models/process.model';
import { FileDownloadUtil } from './fileDownloadUtil.service';

@Injectable({
  providedIn: 'root'
})
export class ProcessRequestService {

  constructor(private httpService: HttpClient, private snack: MatSnackBar,
              private commonService: CommonSandboxService) {
  }

  LDMS = 'LDMS';

  private static mapToProcessTypes(res: any) {
    const types: ProcessTypeModel[] = [];
    for (const item of res.entity) {
      const type: ProcessTypeModel = item;
      type.id = item.id;
      type.definitionKey = item.definitionKey;
      type.name = item.name;
      type.version = item.version;
      type.processDefinitionType = item.processDefinitionType;
      type.processTypeCategory = item.processTypeCategory;
      types.push(type);
    }
    return types;
  }

  private static mapToContractRequest(res: any) {
    const model: ContractRequestModel[] = [];
    for (const req of res) {
      const request: ContractRequestModel = {
        id: null, cif: null, loanAmount: null, loanAccount: null, product: null, date: null, assignee: null, processInstanceId: null
      };

      request.id = req['requestNumber'];
      request.product = req['productDescription'];
      request.cif = req['cifNumber'];
      const date = req['date'];
      request.date = this.getContractDate(date);
      request.assignee = req['userId'];
      request.loanAccount = req['account'];
      request.loanAmount = req['amount'];
      request.processInstanceId = req['processInstanceId'];
      model.push(request);
    }
    return model;
  }

  private static getContractDate(date: any): string {
    return this.getYear(date) + '-' + this.getMonth(date) + '-' + this.getDay(date) + ' ' + this.getHour(date) + ':' + this.getMinute(date);
  }

  private static getYear(date: any) {
    return (date['year'] != null ? date['year'] : date['0']);
  }

  private static getMonth(date: any) {
    const month = Number(date['monthValue'] ?? date['1']);
    return (month < 10 ? '0' + month : month);
  }

  private static getDay(date: any) {
    const day = Number(date['dayOfMonth'] != null ? date['dayOfMonth'] : date['2']);
    return (day < 10 ? '0' + day : day);
  }

  private static getHour(date: any) {
    const hour = Number(date['hour'] != null ? date['hour'] : date['3']);
    return (hour < 10 ? '0' + hour : hour);
  }

  private static getMinute(date: any) {
    const minute = Number(date['minute'] != null ? date['minute'] : date['4']);
    return (minute < 10 ? '0' + minute : minute);
  }

  checkErrorMessage(error) {
    return error.error.code && error.error.code !== '';
  }

  showErrorMessage(error, errorText?) {
    if (this.checkErrorMessage(error)) {
      this.snack.open(error.error.message, 'ХААХ');
    } else if (errorText) {
      this.snack.open(errorText, 'ХААХ');
    } else if (null != error.error) {
      this.snack.open(error.error, 'ХААХ');
    }
  }

  getProcessTypes(processTypeCategory: string): Observable<ProcessTypeModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/process-types/' + processTypeCategory).pipe(map(res => {
        return ProcessRequestService.mapToProcessTypes(res);
      }),
      catchError((err) => {
        this.showErrorMessage(err);
        return throwError('Процесс төрөл сонгоход алдаа гарлаа! ');
      }));
  }

  getAllRequests(startDate: string, endDate: string): Observable<RequestModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/loan-requests', {
      params: {
        startDate,
        endDate
      }
    }).pipe(map(res => {
      return this.mapToRequestModel(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  getRequests(groupId: string, startDate: string, endDate: string): Observable<RequestModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/loan-requests/group/' + groupId, {
      params: {
        startDate,
        endDate
      }
    }).pipe(map(res => {
      return this.mapToRequestModel(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }


  getMyLoanRequests(assignedUserId: string, startDate: string, endDate: string): Observable<RequestModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/loan-requests/users/' + assignedUserId, {
        params: {
          startDate,
          endDate
        }
      }
    ).pipe(map(res => {
      return this.mapToRequestModel(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  getEbankRequests(channelType: string, groupId: string, startDate: string, endDate: string): Observable<RequestModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/loan-requests/channel/' + channelType + '/group/' + groupId, {
      params: {
        startDate,
        endDate
      }
    }).pipe(
      map(res => {
        return this.mapToRequestModel(res);
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
  }

  getSubGroupRequests(groupId: string, startDate: string, endDate: string): Observable<RequestModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/loan-requests/sub-branches/' + groupId, {
      params: {
        startDate,
        endDate
      }
    }).pipe(map(res => {
      return this.mapToRequestModel(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  getRequestsByCustomerNumber(personNumber: string): Observable<RequestModel[]> {
    return this.httpService.post(environment.baseServerUrl + 'bpms/loan-requests/person-number/', personNumber).pipe(map((res: any) => {
      return this.mapToRequestModel(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  getRequestsByInstanceId(instanceId: string): Observable<RequestModel> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/loan-requests/processInstanceId/' + instanceId).pipe(map((res: any) => {
      return this.mapToRequestModel(res)[0];
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  getUserByRoleId(groupId: string): Observable<any> {
    let group = 'hr_specialist';
    if (groupId !== 'CHO') {
      group = 'branch_specialist';
    }
    return this.httpService.get(environment.baseServerUrl + 'bpms/aim/users/role/' + group).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  updateAssignedUser(userId: string, requestId: string): Observable<any> {
    return this.httpService.patch(environment.baseServerUrl + 'bpms/loan-requests/' + requestId + '/assigned-user/' + userId, {}).pipe(
      map((res: any) => res.entity.updated),
      catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
  }

  deleteTableRow(requestId: string, instanceId: string, state: string): Observable<any> {
    const body = {processRequestId: requestId, processInstanceId: instanceId, processState: state};
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/cases/delete', body).pipe(map((res: any) => {
      return res;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  startProcess(processRequestId: string, processCategory?, createdUser?: string): Observable<any> {
    const body = processCategory != null ? processCategory : {processRequestId};
    body.processRequestId = processRequestId;
    body.createdUser = createdUser;
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/cases/start', body).pipe(map((res: any) => {
        return res.entity.instanceId;
      }), catchError(err => {
        this.showErrorMessage(err, 'Кэйс эхлүүлэхэд алдаа гарлаа');
        return throwError(err);
      })
    );
  }

  getRequestForm(formId: string): Observable<FormsModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/task-forms/formId/' + formId).pipe(map((res: any) => {
      return res.entity.taskFormFields;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err.errorText);
    }));
  }

  getProductsByAppCategoryAndBorrowerType(appCategory: string, borrowerType: string): Observable<any[]> {
    borrowerType = this.commonService.encodeText(borrowerType);
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/products/applicationCategory/' + appCategory + '/borrowerType/' + borrowerType)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.errorText);
      }));
  }

  createRequest(processType: string, forms: FormsModel[], groupId: string, currentUser: string,
                currentUserBranch: string, processTypeCategory?: string, processInstanceId?: string, oldContractId?: string, registerNum?: string) {
    let body: any = this.extractForm(forms);
    body.channel = 'BPMS APP';
    body.branchNumber = currentUserBranch;
    body.userId = currentUser;
    body.state = 'NEW';
    if (null !== processInstanceId) {
      body.parameters = {register: registerNum};
      body.instanceId = processInstanceId;
    }
    if (null !== oldContractId) {
      body.oldContractNumber = oldContractId;
    }
    if (processTypeCategory === LOAN) {
      body.productCategory = processType;
      for (const field of forms) {
        if (field.id === 'loanProduct') {
          body.loanProductDescription = this.getOptionValueByDefaultVale(field, field.formFieldValue.defaultValue);
        }
      }
      return this.httpService.post(environment.baseServerUrl + 'bpms/loan-requests', body).pipe(map((res: any) => {
        return res.entity.processRequestId;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));

    } else if (processTypeCategory === LOANCONTRACT) {
      body = {processTypeId: processType, groupNumber: currentUserBranch, createdUserId: currentUser};
      const parameters = {};
      forms.forEach(field => parameters[field.id] = field.formFieldValue.defaultValue);
      body.parameters = parameters;
      return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/cases/loan-contract/create', body).pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
    } else if (processTypeCategory === ORGANIZATION) {
      body.processType = processType;
      return this.httpService.post(environment.baseServerUrl + 'bpms/organization-requests/organization-registration/create', body).pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
    } else if (processTypeCategory === BRANCHBANKING) {
      body.processTypeId = processType;
      body.createdUserId = currentUser;
      body.groupNumber = currentUserBranch;
      return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/process-requests', body).pipe(map((res: any) => {
        return res.entity.processRequestId;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
    }
  }

  public downloadExcelReport(urlHeader: string, topHeader: string, searchKey: string, groupId: string, loanRequests): Observable<any> {
    const key: string = encodeURI(searchKey);
    return this.httpService.post(environment.baseServerUrl + '/bpms/' + urlHeader + '/report/' + topHeader + '/' + key + '/' + groupId,
      loanRequests, {responseType: 'blob', observe: 'response'}).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }));
  }

  /* Loan Contract request */
  getContractRequests(type: string, id: string, startDate: string, endDate: string): Observable<ContractRequestModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/loan-contract', {
      params: {type, id, startDate, endDate}
    }).pipe(map(res => {
      if (res == null || res['entity'] == null) {
        return [];
      }
      return ProcessRequestService.mapToContractRequest(res['entity']);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  updateAssigneeUser(requestBody: {}): Observable<any> {
    return this.httpService.post(environment.baseServerUrl + 'bpms/loan-contract/update-user', requestBody).pipe(
      map((res: any) => res.entity),
      catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
  }

  /* organization registration requests service */

  getOrganizationRequests(topHeader: string, startDate: string, endDate: string, branchId: any): Observable<OrganizationRequestModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/organization-requests/' + topHeader, {
      params: {startDate, endDate, branchId}
    }).pipe(map(res => {
      return this.mapToOrganizationRequestModel(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  checkIsRegisteredOrganization(orgId: string, requestType: string): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/organization-requests/orgRegNumber/' + orgId + '/' + requestType).pipe(
      map((res: any) => {
          return res.entity;
        }
      ),
      catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      })
    );
  }

  getOptionValueByDefaultVale(form: FormsModel, value) {
    const result = form.options.find(option => option.id === value);
    if (result != null) {
      return form.options.find(option => option.id === value).value;
    }
  }


  formatNumber(input: string) {
    if (typeof input === 'string') {
      return Number(input.replace(/(,)/g, ''));
    }
  }

  private mapToRequestModel(res: any) {
    const models: RequestModel[] = [];
    for (const item of res.entity) {
      const model: RequestModel = item;
      if (item.id == null) {
        model.productCode = this.commonService.translate(item.productCategory);
      } else {
        model.id = item.id;
        model.fullName = item.fullName;
        model.registerNumber = item.registerNumber;
        model.instanceId = item.instanceId;
        model.cifNumber = item.cifNumber;
        model.phoneNumber = item.phoneNumber;
        model.email = item.email;
        model.createdDate = item.createdDate;
        model.assignee = item.userId;
        model.userName = item.userName;
        model.productCode = this.commonService.translate(item.productCategory);
        model.amount = item.amount;
        model.branchNumber = item.branchNumber;
        model.channel = item.channel;
        model.state = this.commonService.translate((item.state));
        model.processTypeId = item.processTypeId;
      }
      models.push(model);
    }
    return models;
  }

  private extractForm(forms: FormsModel[]) {
    const parameter = {};
    for (const field of forms) {
      if (field.type === 'BigDecimal' && field.options.length === 0) {
        parameter[field.id] = this.formatNumber(field.formFieldValue.defaultValue);
      } else {
        parameter[field.id] = field.formFieldValue.defaultValue;
      }
    }
    return parameter;
  }

  private mapToOrganizationRequestModel(res: any) {
    const models: OrganizationRequestModel[] = [];
    for (const item of res.entity) {
      const model: OrganizationRequestModel = item;
      model.id = item.id.id;
      model.branchId = item.branchId;
      model.organizationName = item.organizationName;
      model.organizationNumber = item.organizationNumber;
      model.cifNumber = item.cifNumber;
      const date = item.contractDate;
      model.contractDate = this.convertDateFromIso8001(date);
      model.assignee = item.assignee;
      model.state = this.commonService.translateOrgState((item.state));
      model.instanceId = item.instanceId.id;
      model.confirmedUser = item.confirmedUser;
      models.push(model);
    }
    return models;
  }

  private convertDateFromIso8001(date: any) {
    return this.getOrgYear(date) + '-' + this.getOrgMonth(date) + '-' + this.getOrgDay(date);
  }

  private getOrgYear(date: any) {
    return (date['year'] != null ? date['year'] : date['0']);
  }

  private getOrgMonth(date: any) {
    const month = Number(date['monthValue'] != null ? date['monthValue'] : date['1']);
    return (month < 10 ? '0' + month : month);
  }

  private getOrgDay(date: any) {
    const day = Number(date['dayOfMonth'] != null ? date['dayOfMonth'] : date['2']);
    return (day < 10 ? '0' + day : day);
  }
}
