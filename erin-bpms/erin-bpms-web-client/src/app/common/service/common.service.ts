import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {catchError, map} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Overlay} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {ErinLoaderComponent} from '../erin-loader/erin-loader.component';
import {FieldOptions, FormsModel} from '../../models/app.model';
import {TranslateService} from '@ngx-translate/core';
import {CLOSE_MN} from '../../case-view-page/model/common.constants';
import {ORGANIZATION_STATES} from '../../loan-search-page/models/process-constants.model';

@Injectable({
  providedIn: 'root'
})
export class CommonService {
  private LDMS = 'LDMS';

  constructor(private httpService: HttpClient, private snack: MatSnackBar, public overlay: Overlay, private translate: TranslateService) {
  }

  showSnackBar(text: string, action, duration?: number): void {
    this.snack.open(text, action, {duration});
  }

  setOverlay() {
    const overlayRef = this.overlay.create({
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true
    });
    overlayRef.attach(new ComponentPortal(ErinLoaderComponent));
    return overlayRef;
  }

  getFile(id: string, instanceId: string, source: string) {
    const body = {id, source};
    return this.httpService.post(environment.baseServerUrl + 'bpms/bpm/documents/view/' + instanceId, body,
      {
        headers: new HttpHeaders({
          'request-timeout': `${180000}`,
          'connection-timeout': `${180000}`
        })
      }
    ).pipe(map((res: any) => {
      if (res == null && res.entity == null) {
        return null;
      }
      if (res.entity.documentAsBase64) {
        return res.entity.documentAsBase64;
      }
      return res.entity;
    }), catchError(err => {
      if (source !== this.LDMS) {
        let errorMessage = 'Файл ачааллахад алдаа гарлаа';
        if (err.error && err.error.code) {
          errorMessage = errorMessage + ' ' + err.error.code;
        }
        if (err.error && err.error.message) {
          errorMessage = errorMessage + ' ' + err.error.message;
        }
        this.snack.open(errorMessage, 'Хаах', {duration: 3000});
      }
      return throwError(err.errorText);
    }));
  }

  getUserManualFile() {
    return this.httpService.get(environment.baseServerUrl + '/bpms/bpm/documents/user-manual',
      {
        headers: new HttpHeaders({
          'request-timeout': `${180000}`,
          'connection-timeout': `${180000}`
        })
      }
    ).pipe(map((res: any) => {
      if (res == null && res.entity == null) {
        return null;
      }
      if (res.entity.documentAsBase64) {
        return res.entity.documentAsBase64;
      }
      return res.entity;
    }), catchError(err => {
      let errorMessage = 'Гарын авлага татхад алдаа гарлаа';
      if (err.error && err.error.code) {
        errorMessage = errorMessage + ' ' + err.error.code;
      }
      if (err.error && err.error.message) {
        errorMessage = errorMessage + ' ' + err.error.message;
      }
      this.snack.open(errorMessage, 'Хаах', {duration: 3000});
      return throwError(err.errorText);
    }));
  }

  formatNumberService(input: string) {
    if (input != null) {
      return (Number(input.toString().replace(/(,)/g, '')) || Number(input.toString().replace(/(%)/g, '')));
    }
  }

  setFieldDefaultValue(form: FormsModel[], newValue: {}) {
    for (const field of form) {
      for (const entry of Object.entries(newValue)) {
        if (field.id === entry[0]) {
          if (entry[1] != null && !String(entry[1]).includes('undefined')) {
            field.formFieldValue.defaultValue = entry[1];
          }
        }
      }
    }
    return form;
  }

  getForm(form): FormsModel[] {
    const finalForm = [];
    for (const subForm of form) {
      if (subForm.length !== undefined) {
        const tmp = this.getForm(subForm);
        tmp.forEach(field => finalForm.push(field));
      } else {
        finalForm.push(subForm);
      }
    }
    return finalForm;
  }

  setForm(source: any[], fieldNameList: any[]) {
    let tmpForm: FormsModel[] = [];
    const form = [];
    fieldNameList.forEach(nameList => {
      nameList.forEach(name => {
        const tmpField = source.find(field => field.id === name);
        if (tmpField) {
          tmpForm.push(tmpField);
        }
      });
      form.push(tmpForm);
      tmpForm = [];
    });
    return form;
  }

  sortArray(form: any[], attribute, type, order?) {
    form.sort((a, b) => {
      if (a[attribute] && b[attribute]) {
        if (type === 'string') {
          return a[attribute].localeCompare(b[attribute]);
        } else if (type === 'date') {
          if (order != null && order === 'desc') {
            return Number(b[attribute]) - Number((a[attribute]));
          }
          return Number(a[attribute]) - Number((b[attribute]));
        } else if (type === 'number') {
          return Number(a[attribute]) - Number((b[attribute]));
        }
      }
    });
  }

  extractProcessParameter(form, paramName: string[]) {
    if (null != form && null != form.processParameters && paramName.length === 2) {
      const processParam = form.processParameters;
      if (null != processParam[paramName[0]] && null != processParam[paramName[0]][paramName[1]]) {
        const params = processParam[paramName[0]][paramName[1]];
        return JSON.parse(params);
      }
    }
    return null;
  }

  getFieldById(form: FormsModel[], fieldId: string): FormsModel {
    if (form == null) {
      return null;
    }
    return form.find(f => f.id === fieldId);
  }

  getFormValue(form: FormsModel[], fieldId: string): any {
    const valueFromForm = form.find(field => field.id === fieldId);
    if (null != valueFromForm) {
      return valueFromForm.formFieldValue.defaultValue;
    }
    return null;
  }

  setFieldsValidation(form: FormsModel[], fieldsId: string[], validation: any, required?: boolean): void {
    if (null != form && null != fieldsId) {
      for (const fieldId of fieldsId) {
        const formField = form.find(field => field.id === fieldId);
        if (null != formField) {
          if (validation != null) {
            if (formField.validations.indexOf({name: 'readonly', configuration: null}) === -1) {
              formField.validations.push(validation);
              formField.required = required;
            }
          } else {
            formField.validations.splice(1, 4);
          }
        }
      }
    }
  }

  removeFieldsValidation(form: FormsModel[], fieldsId: string[]): void {
    if (null != form && null != fieldsId) {
      for (const fieldId of fieldsId) {
        const formField = form.find(field => field.id === fieldId);
        if (null != formField) {
          if (formField.validations.length > 0) {
            formField.validations.splice(0, 1);
          }
        }
      }
    }
  }

  removeFieldsDefaultValue(form: FormsModel[], fieldsId: string[]) {
    if (null != form && null != fieldsId) {
      for (const id of fieldsId) {
        const formField = form.find(field => field.id === id);
        if (null != formField) {
          formField.formFieldValue.defaultValue = '';
        }
      }
    }
  }

  getFieldLabel(forms: FormsModel[]): string {
    for (const field of forms) {
      if ((field.formFieldValue.defaultValue == null || field.formFieldValue.defaultValue === '') && field.required &&
        field.id !== 'monthPaymentActiveLoan' && field.id !== 'fixedAcceptedLoanAmount' ||
        (field.formFieldValue.defaultValue === '0' && field.id === 'term')) {
        return field.label;
      }
    }
  }

  setFieldLabel(form: FormsModel[], fields: {}): void {
    for (const value of Object.entries(fields)) {
      if (value != null) {
        const fieldToChange = form.find(field => field.id === value[0]);
        if (fieldToChange != null) {
          fieldToChange.label = String(value[1]);
        }
      }
    }
  }

  checkFieldValue(form: FormsModel[], fieldId, fieldValue): boolean {
    return this.getFormValue(form, fieldId) === fieldValue;
  }

  findMultiplication(input1: number, input2: number): any {
    if (input1 != null && input2 != null) {
      if (input1 === 0 || input2 === 0) {
        return 0;
      }
      return (input1 * input2 || (input1 * input2).toFixed());
    }
  }

  translateString(text: string): string {
    return this.translate.instant(text);
  }

  translateOrganizationState(text: string): string {
    return ORGANIZATION_STATES.has(text) ? ORGANIZATION_STATES.get(text) : text;
  }

  clone(data) {

    return JSON.parse(JSON.stringify(data));
  }

  cloneObject(data) {
    return Object.assign({}, data);
  }

  validateFieldValueLessThanZero(value: any, errorMessage: string): boolean {
    if (null !== value && undefined !== value) {
      if (value < 0) {
        this.showSnackBar(errorMessage, 'XААХ', 4000);
        return true;
      }
      return false;
    }
  }

  getEmptyFormModel() {
    return {
      id: 'monthField', formFieldValue: {defaultValue: null, valueInfo: null}, label: null, type: 'string',
      validations: [], options: [], required: false
    };
  }

  convertFormToJson(form): string {
    return JSON.stringify(form);
  }

  setFieldOptions(form: FormsModel[], options: FieldOptions[], fieldId: string): void {
    if (form != null && options != null) {
      form.forEach(field => {
        if (field.id.includes(fieldId)) {
          field.options = options;
        }
      });
    }
  }

  getFieldOptions(form: FormsModel[], fieldId: string): FieldOptions[] {
    for (const field of form) {
      if (fieldId === field.id) {
        return field.options;
      }
    }
    return null;
  }

  clearFieldsById(form: FormsModel[], ids: string[]): void {
    for (const field of form) {
      if (this.containId(field.id, ids)) {
        const prop = {};
        prop[field.id] = '';
        this.setFieldDefaultValue(form, prop);
      }
    }
  }

  private containId(fieldId: string, ids: string[]) {
    for (const id of ids) {
      if (id === fieldId) {
        return true;
      }
    }
    return false;
  }

  encodeText(text: string): string {
    return encodeURI(text);
  }

  isEqualNumbers(number1, number2): boolean {
    return Number(number1) === Number(number2);
  }

  isEqualDates(date1, date2): boolean {
    return new Date(date1).toDateString() === new Date(date2).toDateString();
  }

  isNull(field: FormsModel): boolean {
    if (field.formFieldValue.defaultValue == null) {
      return true;
    }
  }

  isMandatoryField(submitForm, overlayRef?): any {
    for (const field of submitForm) {
      if (field.required && (field.formFieldValue.defaultValue == null || field.formFieldValue.defaultValue === ''
        || field.formFieldValue.defaultValue === 0)) {
        this.snack.open(field.label + ' талбарыг бөглөнө үү', CLOSE_MN, {duration: 3000});
        if (null != overlayRef) {
          overlayRef.dispose();
        }
        return true;
      }
    }
    return false;
  }

  getSelectedFieldOptionIdByFieldId(formFields: FormsModel[], fieldId: string): any {
    const field = formFields.find(formField => formField.id === fieldId);
    if (field.options.length > 0) {
      for (const option of field.options) {
        if (option.value === field.formFieldValue.defaultValue) {
          return option.id;
        }
      }
    }
    return null;
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

  getFormFieldsAsMap(form: FormsModel[], params: {}): any {
    for (const field of form) {
      params[field.id] = field.formFieldValue.defaultValue;
    }
    return params;
  }

  getEmptyRequiredFieldsOfForm(form: FormsModel[]): any[] {
    const requiredFields = [];
    form.forEach(field => {
      if (field.required && (field.formFieldValue.defaultValue == null
        || field.formFieldValue.defaultValue === '' ||
        field.formFieldValue.defaultValue === 0)) {
        requiredFields.push(field);
      }
    });
    return requiredFields;
  }

  clearForm(form: FormsModel[], nonClearVariables: string[]) {
    if (nonClearVariables.length === 0) {
      form.forEach(this.clearDefaultValueByType);
    } else {
      form.filter(field => this.isClearField(field, nonClearVariables)).forEach(this.clearDefaultValueByType);
    }
  }

  isClearField(field: FormsModel, nonClearVariables: string[]): boolean {
    return null == nonClearVariables.find(variable => variable === field.id);
  }

  clearDefaultValueByType(field: FormsModel): void {
    if (field.type === 'string') {
      field.formFieldValue.defaultValue = null;
    }
    if (field.type === 'BigDecimal') {
      field.formFieldValue.defaultValue = 0;
    }
    if (field.type === 'date') {
      field.formFieldValue.defaultValue = null;
    }
  }
  getProcessType(processType: string): string {
    switch (processType) {
      case "bnplLoan":
        return "user_task_disbursement";
      case "instantLoan":
        return "user_task_instant_loan_disbursement"
      default:
        return "user_task_online_leasing_disbursement";
    }
  }
}
