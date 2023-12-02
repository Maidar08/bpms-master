import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {catchError, map} from 'rxjs/operators';
import {Observable, throwError} from 'rxjs';
import {FieldOptions, FormsModel} from '../../models/app.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {
  CollateralListModel,
  DocumentModel,
  DocumentsModel,
  DocumentTypeModel,
  NoteModel,
  SalaryCalculationModel,
  SalaryModel,
  SaveSalaryModel,
  TaskItem,
  TaskModel
} from '../model/task.model';
import {FileDownloadUtil} from '../../loan-search-page/services/fileDownloadUtil.service';
import {CommonSandboxService} from '../../common/common-sandbox.service';
import {CREATE_COLLATERAL_LOAN_ACCOUNT, TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT, TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT} from '../model/task-constant';
import {isIterable} from 'rxjs/internal-compatibility';

@Injectable({
  providedIn: 'root'
})
export class ProcessService {
  constructor(
    private httpService: HttpClient,
    private snack: MatSnackBar,
    private commonSb: CommonSandboxService) {
  }

  LDMS = 'LDMS';
  BRANCH_BANKING = 'branch-banking';

  private static mapToDocumentModel(res: any) {
    const docs: DocumentsModel[] = [];
    for (const item of res.entity) {
      const file: DocumentsModel = {
        fileId: item.id,
        fileName: item.name,
        type: item.type,
        subType: item.subType,
        downloadable: item.isDownloadable,
        isSubDocument: item.isSubDocument,
        reference: item.reference,
        source: item.source
      };
      docs.push(file);
    }
    return docs;
  }

  private static mapToNotesModel(res: any) {
    const notesInfo: NoteModel[] = [];
    for (const item of res.entity.notes) {
      const note: NoteModel = {
        username: item.username,
        note: item.noteText,
        date: item.formattedDate,
        isReason: item.reason
      };
      notesInfo.push(note);
    }
    return notesInfo;
  }

  private static mapToDocumentType(res: any) {
    const documentTypes: DocumentTypeModel[] = [];
    for (const item of res.entity) {
      const documentType: DocumentTypeModel = item;
      documentType.id = item.id;
      documentType.name = item.name;
      documentType.parentId = item.parentId;
      documentType.type = item.type;
      documentTypes.push(documentType);
    }
    return documentTypes;
  }

  private static mapToCollateral(res: any) {
    const collateralList: CollateralListModel[] = [];
    for (const item of res.entity) {
      const collateral: CollateralListModel = item;
      collateral.checked = item.checked;
      collateral.collateralId = item.collateralId;
      collateral.description = item.description;
      collateral.amountOfAssessment = item.amountOfAssessment;
      collateral.hairCut = item.hairCut + '%';
      collateral.availableAmount = item.availableAmount;
      const startDate = new Date(item.startDate);
      collateral.startDate = item.startDate !== null ?
        startDate.getFullYear() + '/' + (startDate.getMonth() + 1) + '/' + startDate.getDate() : '';
      const reassessDate = new Date(item.revalueDate);
      collateral.revalueDate = item.revalueDate !== null ?
        `${reassessDate.getFullYear()}/${reassessDate.getMonth() + 1}/${reassessDate.getDate()}` : '';
      collateral.accountId = item.accountId;
      collateral.createdOnBpms = item.createdOnBpms === false ? 'Тийм' : 'Үгүй';
      collateralList.push(collateral);
    }
    return collateralList;
  }

  private static getIcon(activityType: string) {
    switch (activityType) {
      case 'processTask':
        return 'person_outline';
      case 'userTask':
        return 'person_outline';
      default:
        return 'notes';
    }
  }

  private static getTaskFormFields(res: any): FormsModel[] {
    const forms: FormsModel[] = [];
    for (const field of res) {
      if (field.id.toLowerCase().includes('fingerprint')) {
        field.type = 'fingerprint';
        forms.push({
          id: field.id, formFieldValue: {defaultValue: field.formFieldValue.defaultValue, valueInfo: field.formFieldValue.valueInfo},
          label: field.label, type: field.type,
          validations: field.validations, options: field.options, context: field.context, disabled: field.disabled,
          required: !!field.validations.find(value => value.name === 'required')
        });
      } else {
        forms.push({
          id: field.id, formFieldValue: field.formFieldValue, label: field.label, type: field.type,
          validations: field.validations, options: field.options, context: field.context, disabled: field.disabled,
          required: !!field.validations.find(value => value.name === 'required')
        });
      }
    }
    return forms;
  }

  private static mapToFormsModel(res: any): TaskModel {
    return {
      taskId: res.taskId,
      formId: res.formId,
      taskFormFields: ProcessService.getTaskFormFields(res.taskFormFields),
      tableData: res.tableData
    };
  }

  private static mapToBpmnFormsModel(res: any): TaskModel {
    return {
      taskId: res.taskId,
      formId: res.formId,
      taskFormFields: ProcessService.getTaskFormFields(res.taskFormFields),
      tableData: res.tableData
    };
  }

  private static getOptions(values): FieldOptions[] {
    const options: FieldOptions[] = [];
    for (const item of values) {
      options.push({id: item.itemId, value: item.itemDescription, default: item.default});
    }
    return options;
  }

  private static formatFormFieldValue(form: FormsModel[], parameter) {
    if (isIterable(form)) {
      for (const field of form) {
        field.formFieldValue.defaultValue = parameter[field.id];
      }
    }
    return form;
  }

  checkErrorMessage(error) {
    return error.error.code && error.error.code !== '';
  }

  showErrorMessage(error, errorText?) {
    if (this.checkErrorMessage(error)) {
      this.snack.open(error.error.message, 'ХААХ');
    } else if (errorText) {
      this.snack.open(errorText, 'ХААХ');
    }
  }

  getFromRelation(defKey: string) {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/form-relation/' + defKey)
      .pipe(map((res: any) => {
        if (res.entity == null) {
          return null;
        }
        return res.entity.fieldRelations;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
  }

  saveSalary(salaryModel: SaveSalaryModel, instanceId: string): Observable<SaveSalaryModel> {
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/salary/save/' + instanceId, salaryModel).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  setSalaryAverage(salary): Observable<SalaryModel> {
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/salary/calculate-tax-average', salary).pipe(map((res: any) => {
      const salaryModel: SalaryModel = {afterTaxSalaries: [], averageSalaryAfterTax: 0, averageSalaryBeforeTax: 0};
      let i = 0;
      const dates = [];
      const salaries = [];
      // tslint:disable-next-line:forin
      for (const date in salary.dateAndSalaries) {
        dates.push(new Date(Date.parse(date)));
        salaries.push(salary.dateAndSalaries[date]);
      }
      for (const date of dates) {
        i++;
        // tslint:disable-next-line:forin
        for (const props in res.entity.afterTaxSalaries) {
          const tmpDate = new Date(Date.parse(props));
          if (tmpDate.getFullYear() === date.getFullYear() && tmpDate.getMonth() === date.getMonth()) {
            const salaryCalculationModel: SalaryCalculationModel = {
              month: i,
              date: tmpDate,
              defaultValue: salaries[i - 1],
              socialInsuranceTax: res.entity.afterTaxSalaries[props].Ndsh,
              personalIncomeTax: res.entity.afterTaxSalaries[props].Hhoat,
              salaryAfterTax: res.entity.afterTaxSalaries[props].MonthSalaryAfterTax,
              checked: false, isXypSalary: false, isCompleted: false
            };
            salaryModel.afterTaxSalaries.push(salaryCalculationModel);
          }
        }
      }
      salaryModel.averageSalaryAfterTax = res.entity.averageSalaryAfterTax;
      salaryModel.averageSalaryBeforeTax = res.entity.averageSalaryBeforeTax;
      return salaryModel;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  getXypSalary(instacneId: string): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/xyp-salary/' + instacneId).pipe(map((res: any) => {
      return res.entity ? res.entity.XYP_SALARY : [];
    }));
  }

  getDefaultSalary(instanceId: string): Observable<SaveSalaryModel> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/salary/' + instanceId).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  getSalary(instanceId: string): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/process/parameters/salary-info/' + instanceId).pipe(map((res: any) => {
      const salaryModel = {
        niigmiinDaatgalExcluded: false,
        healthInsuranceExcluded: false,
        dateAndSalaries: {},
      };
      const salaries = {};
      let property;
      for (const entity of res.entity) {
        if (entity.month < 10) {
          property = entity.year + '-0' + entity.month + '-01';
        } else {
          property = entity.year + '-' + entity.month + '-01';
        }
        if (salaries[property] !== undefined) {
          salaries[property] += entity.amount;
        } else {
          salaries[property] = entity.amount;
        }
      }
      salaryModel.dateAndSalaries = salaries;
      return salaryModel;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  downloadInfo(taskId: string, caseInstanceId: string, processRequestId: string, entity, form: FormsModel[]): Observable<any> {

    const body = {taskId, caseInstanceId, processRequestId, properties: this.extractForm(form)};
    body.properties[entity] = true;
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/task-forms/submit',
      body, {
        headers: new HttpHeaders({
          'request-timeout': `${180000}`,
          'connection-timeout': `${180000}`
        })
      }).pipe(map(res => {
      this.snack.open('Мэдээлэл амжилттай татагдлаа.', null, {duration: 3000});
      return res;
    }), catchError(err => {
      this.showErrorMessage(err, 'Мэдээлэл татахад алдаа гарлаа!');
      return throwError(err.error.message);
    }));
  }

  calculateTask(taskId: string, taskType: string, entity: string, form: FormsModel[], instanceId: string): Observable<any> {
    this.commonSb.setFieldDefaultValue(form, {reCalculated: false});
    const body = {taskId, properties: this.extractForm(form)};
    body.properties[entity] = true;
    return this.httpService.post(environment.baseServerUrl + ' bpms/bpm/' + taskType + '/' + instanceId, body).pipe(map((res: any) => {
      return ProcessService.mapToFormsModel(res.entity);
    }), catchError(err => {
      this.showErrorMessage(err, 'Тооцоолоход алдаа гарлаа!');
      return throwError(err.errorText);
    }));
  }

  getBalanceFromProcessParameter(caseInstanceId: string): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/balance/' + caseInstanceId)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Тооцоолоход алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  calculateMicroBalance(caseInstanceId: string, form: []): Observable<any> {
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/micro-calculation/balance-calculation/' + caseInstanceId, form)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.errorText);
      }));
  }

  saveMicroBalance(caseInstanceId: string, form): Observable<any> {
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/save/balance/' + caseInstanceId, form)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Хадгалахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  createLoanAccount(taskId: string, form: any[], instanceId: string, taskName: string, defKey: string): Observable<any> {
    let properties;
    if (form.length > 1 && (taskName.includes(CREATE_COLLATERAL_LOAN_ACCOUNT) || defKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT ||
      defKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT)) {
      properties = this.extractForm(form[0]);
      properties.collateralList = form[1];
      properties.hasInsurance = form[2];
    } else {
      properties = this.extractForm(form);
    }
    const body = {taskId, properties};
    const isLoanAccountCreate = 'isLoanAccountCreate';
    body.properties[isLoanAccountCreate] = true;
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/loan-account/create/' + instanceId, body).pipe(map((res: any) => {
        return ProcessService.mapToFormsModel(res.entity);
      }), catchError(err => {
        this.showErrorMessage(err, 'Данс үүсгэхэд алдаа гарлаа!');
        return throwError(err.error.message);
      })
    );
  }

  submitForm(taskId: string, caseInstanceId: string, processRequestId: string,
    form: any[], taskName: string, defKey: string, entity: string, tableDataMap?): Observable<any> {
    let properties;
    if (form.length > 1 && (taskName.includes(CREATE_COLLATERAL_LOAN_ACCOUNT) ||
      defKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT || defKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT)) {
      properties = this.extractForm(form[0]);
      properties.collateralList = form[1];
      properties.hasInsurance = form[2];
    } else {
      properties = this.extractForm(form);
    }

    if (tableDataMap != null) {
      for (const prop in tableDataMap) {
        properties[prop] = tableDataMap[prop];
      }
    }

    const body = {taskId, caseInstanceId, processRequestId, properties};
    if (defKey) {
      body['defKey'] = defKey;
    }
    const isLoanAccountCreate = 'isLoanAccountCreate';

    body.properties[isLoanAccountCreate] = false;
    body.properties.calculateBalanceCalculation = false;

    if (entity != null) {
      body.properties[entity] = false;
    }
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/task-forms/submit', body).pipe(map((res: any) => {
        if (null != res && null != res.entity) {
          return res.entity;
        }
        return res;
      }), catchError(err => {
        this.showErrorMessage(err, 'Таск дуусгахад алдаа гарлаа !');
        return throwError(err.error.message);
      })
    );
  }

  submitThenCallUserTask(taskId: string, caseInstanceId: string, processRequestId: string, form: any[], props): Observable<any> {
    const properties = this.extractForm(form);
    if (props) {
      for (const prop in props) {
        if (props.hasOwnProperty(prop)) {
          properties[prop] = props[prop];
        }
      }
    }
    properties['caseInstanceId'] = caseInstanceId;
    const body = {taskId, caseInstanceId, processRequestId, properties};
    return this.httpService.post(environment.baseServerUrl + this.BRANCH_BANKING + '/bpm/task-forms/submit/call/user-task', body).pipe(map(res => {
        return res;
      }), catchError(err => {
        this.showErrorMessage(err, 'Таск дуусгахад алдаа гарлаа !');
        return throwError(err.error.message);
      })
    );
  }

  getParametersByName(instanceId: string, parameterName: string): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/process/instanceId/' + instanceId + '/parameterName/' + parameterName)
      .pipe(map((res: any) => {
        return res.entity;
      }));
  }

  saveCollateralUdf(instanceId: string, taskId: string, collateralId: string, udfForm: FormsModel[]): Observable<any> {
    const udfProperty = {};
    udfProperty[collateralId + '-UDF'] = JSON.stringify(this.extractForm(udfForm));
    const body = {taskId, caseInstanceId: instanceId, properties: udfProperty};
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/task-forms/save/execution-variables', body).pipe(map(res => {
      this.snack.open('Амжилттай хадгалагдлаа.', null, {duration: 3000});
      return res;
    }), catchError(err => {
      this.showErrorMessage(err, 'Форм хадгалахад алдаа гарлаа');
      return throwError(err.errorText);
    }));
  }

  saveUdfToProcessTable(instanceId: string, collateralId: string, entityType: string, udfForm: FormsModel[]): Observable<any> {
    const body = {parameterEntityType: entityType, parameters: this.extractForm(udfForm)};
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/process/parameters/save/' + instanceId + '/collateral/' + collateralId, body)
      .pipe(map(res => {
        return res;
      }), catchError(err => {
        this.showErrorMessage(err, 'Форм хадгалахад алдаа гарлаа');
        return throwError(err.errorText);
      }));
  }

  saveTasks(isOnlySave: boolean, form: any[], instanceId: string, taskName: string, defKey: string, extParam, entityName?: string): Observable<any> {
    const entityType = entityName ? entityName : 'FORM';
    let parameter;
    if (form.length > 1 && (taskName.includes(CREATE_COLLATERAL_LOAN_ACCOUNT) || defKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT ||
      defKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT)) {
      parameter = this.extractForm(form[0]);
      parameter.collateralAccount = form[1];
      form = ProcessService.formatFormFieldValue(form[0], parameter);
    } else {
      parameter = this.extractForm(form);
      // format form. form is used to save parameters in execution
      form = ProcessService.formatFormFieldValue(form, parameter);
    }
    if (extParam != null) {
      for (const prop in extParam) {
        parameter[prop] = extParam[prop];
      }
    }
    const body = {parameterEntityType: entityType, parameters: parameter, restFormFields: form, defKey};
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/process/parameters/save/' + instanceId, body)
      .pipe(map((res: any) => {
        if (isOnlySave) {
          this.snack.open('Бөглөсөн талбарууд амжилттай хадгалагдлаа! ', null, {duration: 3000});
        }
        return true;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.error.message);
      }));
  }

  printLoanContract(form: FormsModel[], instanceId: string, entityName?: string): Observable<any> {
    const entityType = entityName ? entityName : 'FORM';
    const body = {parameterEntityType: entityType, parameters: this.extractForm(form)};
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/documents/contract/loan/create/' + instanceId, body)
      .pipe(map((res: any) => {
        this.snack.open('Амжилттай хэвлэлээ!', null, {duration: 5000});
        return ProcessService.mapToFormsModel(res.entity);
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.error.message);
      }));
  }

  getInstanceForm(instanceId: string): Observable<TaskModel> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/task-forms/caseInstanceId/' + instanceId).pipe(map((res: any) => {
      return ProcessService.mapToFormsModel(res.entity);
    }), catchError((err) => {
      this.showErrorMessage(err);
      return throwError(err.message || 'error');
    }));
  }

  getCollateralUdfForm(savedValues) {
    const gedcollt = 'GEDCOLLT';
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/task-forms/ud-fields-by-function/' + gedcollt).pipe(map((res: any) => {
      return this.mapToUdfForms(res, savedValues);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  getCollateralUdfFromProcessTable(instanceId: string, collateralId: string) {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/process/parameters/' + instanceId + '/collateral/' + collateralId)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
  }

  calculateCollateralAmount(form) {
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/collateral/calculate-collateral-amount', form).pipe(map((res: any) => {
      if (res == null) {
        return null;
      }
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err);
    }));
  }

  getAccountList(instanceId: string): Observable<any> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/task-forms/accounts-list/' + instanceId).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err.message || 'error');
    }));
  }

  getSubModuledtask(instanceId: string, type: string): Observable<any[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/cases/executions/' + type + '/' + instanceId + '/filtered')
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.error.message || 'error');
      }));
  }

  getTask(instanceId: string, type: string, requestType: string): Observable<TaskItem[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/cases/executions/' + type + '/' + instanceId + '/' + requestType)
      .pipe(map((res: any) => {
        const taskItems: TaskItem[] = [];
        for (const item of res.entity) {
          taskItems.push({
            icon: ProcessService.getIcon(item.activityType),
            name: item.activityName,
            executionId: item.executionId,
            instanceId: item.instanceId,
            executionType: item.executionType,
            taskId: item.taskId,
            parentTaskId: item.parentTaskId,
            definitionKey: item.definitionKey
          });
        }
        return taskItems;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.error.message || 'error');
      }));
  }

  getActiveTasks(instanceId: string, requestType: string, processRequestId?: string, productType?: string): Observable<TaskItem[]> {
    if (productType === 'bnplLoan' || productType === 'instantLoan' || productType ==='onlineLeasing') {
      const definitionKeyValue = this.commonSb.getDefinitionKeyByProcessType(productType);
      const requestBody = {requestId: processRequestId, definitionKey: definitionKeyValue};
      return this.httpService.post(environment.baseServerUrl + 'bpms/bnpl/getActiveTasks', requestBody).pipe(map((res: any) => {
        return this.mapTaskVariablesBpmn(res);
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.error.message || 'error');
      }));
    } else {
      return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/tasks/active/' + instanceId + '/' + requestType).pipe(map((res: any) => {
        return this.mapTaskVariables(res);
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.error.message || 'error');
      }));
    }
  }

  mapTaskVariables(res: any) {
    const taskItems: TaskItem[] = [];
    for (const item of res.entity) {
      taskItems.push({
        icon: ProcessService.getIcon(item.type),
        name: item.name,
        executionId: item.executionId,
        instanceId: item.instanceId,
        executionType: item.type,
        taskId: item.taskId,
        parentTaskId: item.parentTaskId,
        definitionKey: item.definitionKey
      });
    }
    return taskItems;
  }

  mapTaskVariablesBpmn(res: any) {
    const taskItems: TaskItem[] = [];
    for (const item of res.entity) {
      taskItems.push({
        icon: ProcessService.getIcon(item.type),
        name: item.name,
        executionId: item.executionId,
        instanceId: item.executionId,
        executionType: item.type,
        taskId: item.taskId,
        parentTaskId: item.parentTaskId,
        definitionKey: item.definitionKey
      });
    }
    return taskItems;
  }

  getCustomerVariables(instanceId: string, type: string): Observable<FormsModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/cases/variables/' + type + '/' + instanceId).pipe(map((res: any) => {
      const entity = res.entity;
      const forms: FormsModel[] = [];
      for (const item of entity) {
        forms.push({
          id: item.id.id === undefined ? item.id : item.id.id,
          formFieldValue: {
            defaultValue: item.value ? item.type === 'Date' ? new Date(item.value).toISOString().substr(0, 10) : item.value : null,
            valueInfo: null
          },
          label: item.id.id === undefined ? item.label : item.id.id,
          type: item.type,
          validations: [],
          options: [],
          disabled: item.disabled,
          context: item.context,
          required: false
        });
      }
      return forms;
    }));
  }

  getDocuments(instanceId: string): Observable<DocumentsModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/documents/all/' + instanceId).pipe(map(res => {
      return ProcessService.mapToDocumentModel(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err.errorText);
    }));
  }

  getNotes(instanceId: string, type: string): Observable<NoteModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/cases/variables/notes-info/' + type + '/' + instanceId).pipe(map(res => {
      return ProcessService.mapToNotesModel(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err.errorText);
    }));
  }

  // collateral
  getCollateral(cifNumber: string, caseInstanceId: string): Observable<CollateralListModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/products/loan/collaterals/' + cifNumber + '/' + caseInstanceId)
      .pipe(map(res => {
        return ProcessService.mapToCollateral(res);
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.errorText);
      }));
  }

  refreshCollateral(cifNumber: string, caseInstanceId: string): Observable<CollateralListModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/products/loan/collaterals/refresh/' + cifNumber + '/' + caseInstanceId)
      .pipe(map(res => {
        return ProcessService.mapToCollateral(res);
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.errorText);
      }));
  }

  getUpdateCollateralForm(collateralId: string, collateralType: string) {
    return this.httpService
      .get(environment.baseServerUrl + 'bpms/bpm/products/collateral-task-form/collateralType/' + collateralType + '/collateralId/' + collateralId)
      .pipe(map((res: any) => {
          if (res == null) {
            return null;
          }
          return res.entity;
        }), catchError(err => {
          this.showErrorMessage(err);
          return throwError(err.errorText);
        })
      );
  }

  updateCollateral(instanceId: string, collateralId: string, collateralType: string, cif: string, form): Observable<boolean> {
    const submitForm = this.extractForm(form);
    return this.httpService
      .post(environment.baseServerUrl + 'bpms/bpm/products/update/collateral/instanceId/'
        + instanceId + '/collateralId/' + collateralId + '/type/' + collateralType + '/cif/' + cif, submitForm)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
  }

  saveCollateralFromAccount(collateralId: string, form): Observable<boolean> {
    return this.httpService
      .post(environment.baseServerUrl + 'bpms/bpm/products/save/collateral/account/caseInstanceId/' + collateralId, form)
      .pipe(map((res: any) => {
        return res;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
  }

  // document
  getBasicDocumentType(): Observable<DocumentTypeModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/documents/basic/all').pipe(map(res => {
      return ProcessService.mapToDocumentType(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err.errorText);
    }));
  }

  getSubDocumentType(id): Observable<DocumentTypeModel[]> {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/documents/sub/' + id).pipe(map(res => {
      return ProcessService.mapToDocumentType(res);
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err.errorText);
    }));
  }

  getRequestById(requestId: string) {
    return this.httpService.get(environment.baseServerUrl + '/bpms/loan-requests/' + requestId).pipe(map((res: any) => {
      return res;
    }));
  }

  getLoanProductById(productCode: string) {
    return this.httpService.get(environment.baseServerUrl + 'bpms/bpm/products/' + productCode).pipe(map((res: any) => {
      return res.entity[0];
    }));
  }

  saveDocuments(instanceId: string, type: string, subType: string, documents: DocumentModel[]) {
    const body = {type, subType, documents};
    return this.httpService.post('bpms/bpm/documents/upload/' + instanceId, body).pipe(map(() => undefined),
      catchError((err) => {
        this.showErrorMessage(err, 'Файл хадгалахад алдаа гарлаа!');
        return throwError('Файл хадгалахад алдаа гарлаа!');
      }));
  }

  switchActiveTask(taskId: string, instanceId: string, defKey: string): Observable<TaskModel> {
    return this.httpService.get(
      environment.baseServerUrl + 'bpms/bpm/task-forms/taskId/' + taskId + '/caseInstanceId/' + instanceId + '/defKey/' + defKey,
      {headers: new HttpHeaders({timeout: `${60000}`})})
      .pipe(map((res: any) => {
        return ProcessService.mapToFormsModel(res.entity);
      }), catchError(err => {
        this.showErrorMessage(err);
        // 'Таск хооронд шилжихэд алдаа гарлаа!'
        return throwError(err.errorText);
      }));

  }

  saveCompletedTask(completedTask): Observable<any> {
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/task-forms/save/completed-form', completedTask).pipe(
      catchError(() => {
        return throwError('Дууссан таск хадгалахад алдаа гарлаа.');
      })
    );
  }

  getCompletedForm(taskId: string, caseInstanceId: string): Observable<any> {
    return this.httpService
      .get(environment.baseServerUrl + 'bpms/bpm/task-forms/completed-form/taskId/' + taskId + '/caseInstanceId/' + caseInstanceId)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Дууссан таскын мэдээлэл татахад алдаа гарлаа');
        return throwError('Дууссан таскын мэдээлэл татахад алдаа гарлаа');
      }));
  }

  manualActivateTask(executionId: string) {
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/cases/executions/manual-activate', {
      executionId,
      variables: [],
      deletions: []
    }).pipe(map(res => {
      return res;
    }), catchError(err => {
      this.showErrorMessage(err, 'Таск эхлүүлэхэд алдаа гарлаа');
      return throwError(err.errorText);
    }));
  }

  private extractForm(forms: FormsModel[]) {
    const parameter = {};

    if (this.isIterable(forms)) {
      for (const field of forms) {
        if (field) {
          if (field.type === 'BigDecimal' && field.options.length === 0) {
            parameter[field.id] = this.commonSb.formatNumberService(field.formFieldValue.defaultValue);
          } else {
            parameter[field.id] = field.formFieldValue.defaultValue;
            this.checkLoanProduct(field, parameter);
          }
        }
      }
    }

    return parameter;
  }

  isIterable(obj) {
    if (obj == null) {
      return false;
    }
    return typeof obj[Symbol.iterator] === 'function';
  }

  checkLoanProduct(field: FormsModel, parameter) {
    if (field.id === 'loanProduct') {
      const loanProductDescriptionOption = field.options.find(option => option.id === field.formFieldValue.defaultValue);
      if (loanProductDescriptionOption) {
        parameter.loanProductDescription = loanProductDescriptionOption.value;
      }
    }
  }

  getCollateralAssets(caseInstanceId: string) {
    return this.httpService.get(environment.baseServerUrl + '/bpms/bpm/products/loan/selected-collaterals/' + caseInstanceId)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Барьцаа хөрөнгийн сонголтууд татахад алдаа гарлаа');
        return throwError(err.errorText);
      }));
  }

  getLoanProductByIdAndCategory(productCode: string, applicationCategory: string) {
    return this.httpService.get(environment.baseServerUrl + '/bpms/bpm/products/productId/' + productCode + '/applicationCategory/' + applicationCategory)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
  }

  getCollateralProducts() {
    return this.httpService.get(environment.baseServerUrl + '/bpms/bpm/products/collateral-products')
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err);
      }));
  }

  private mapToUdfForms(res: any, savedValues: any): FormsModel[] {
    const udfValues = savedValues;
    const forms: any[] = Object.values(res.entity);
    forms.sort((a, b) => {
      return Number(a.fieldNumber) - Number(b.fieldNumber);
    });
    const fields: FormsModel[] = [];
    for (const item of forms) {
      for (const entry of Object.entries(udfValues)) {
        if (item.id.id === entry[0]) {
          if (entry[1] !== 'empty') {
            item.defaultValue = entry[1];
          }
        }
      }
      fields.push({
        id: item.id.id, formFieldValue: {defaultValue: item.defaultValue}, label: item.fieldDescription, type: item.fieldType,
        validations: [], options: ProcessService.getOptions(item.values),
        context: '', disabled: false, required: item.mandatory, fieldNumber: Number(item.fieldNumber)
      });
    }
    return fields;
  }

  public downloadExcelReport(topHeader: string, searchKey: string, groupId: string, channel: string): Observable<any> {
    const key: string = encodeURI(searchKey);
    return this.httpService.get(environment.baseServerUrl + '/bpms/loan-requests/report/' + topHeader + '/' + key + '/' + groupId + '/' + channel, {
      responseType: 'blob',
      observe: 'response'
    }).pipe(map(res => {
      return FileDownloadUtil.downloadFile(res);
    }));
  }

  getBpmnForm(processInstanceId: string, defKey: string): Observable<any> {
    const reqBody = {processInstanceId, taskDefinitionKey: defKey};
    return this.httpService.post(environment.baseServerUrl + '/bpms/bnpl/getFormVariables', reqBody)
      .pipe(map((res: any) => {
        return ProcessService.mapToBpmnFormsModel(res.entity);
      }), catchError(err => {
        this.showErrorMessage(err);
        // 'Таск хооронд шилжихэд алдаа гарлаа!'
        return throwError(err.errorText);
      }));
  }

  submitBpmnForm(productType: string, instanceId: string): Observable<any> {
    const definitionKey = this.commonSb.getDefinitionKeyByProcessType(productType);
    const reqBody = {taskDefinitionKey: definitionKey, processInstanceId: instanceId};
    return this.httpService.post(environment.baseServerUrl + '/bpms/bnpl/submitForm', reqBody)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.errorText);
      }));
  }

  updateOrganizationRequest(orgContractId: string, instanceId: string, processType: string, action: string, makerId?: string): Observable<any> {
    const requestBody = {contractId: orgContractId, caseInstanceId: instanceId, type: processType, actionType: action, makerId: makerId};
    return this.httpService.post(environment.baseServerUrl + '/bpms/organization-requests/updateStateOrg', requestBody)
      .pipe(map((res: any) => {
        if (res.entity) {
          this.commonSb.showSnackBar('Хүсэлт амжилттай!', null, 5000);
        }
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err);
        return throwError(err.errorText);
      }));
  }

  printOrganizationContract(orgContractId: string, instanceId: string, processType: string): Observable<any> {
    const requestBody = {contractId: orgContractId, type: processType};
    return this.httpService.post(environment.baseServerUrl + '/bpms/bpm/documents/view/organization/' + instanceId, requestBody)
      .pipe(map((res: any) => {
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Баримт татахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  saveOrganizationInfo(contractNumber: any, typeId: any, taskId: any, definitionKey: any, instanceId, form: FormsModel[]) {
    const body = {requestId: contractNumber, processType: typeId, taskId: taskId, taskDefinitionKey: definitionKey, caseInstanceId: instanceId, parameters: this.extractForm(form)};
    return this.httpService.post(environment.baseServerUrl + '/bpms/organization-requests/save/creation-form', body)
      .pipe(map((res: any) => {
        if (res) {
          this.commonSb.showSnackBar('Амжилттай хадгалагдлаа!', null, 5000);
        }
        return res.entity;
      }), catchError(err => {
        this.showErrorMessage(err, 'Хадгалахад алдаа гарлаа!');
        return throwError(err.errorText);
      }));
  }

  getCompletedServiceTaskInfo(instanceId: string) {
    return this.httpService.get(environment.baseServerUrl + '/bpms/bpm/task-forms/getTaskState/processInstanceId/' + instanceId).pipe(map((res: any) => {
      return res.entity;
    }), catchError(err => {
      this.showErrorMessage(err);
      return throwError(err.errorText);
    }));
  }
}

