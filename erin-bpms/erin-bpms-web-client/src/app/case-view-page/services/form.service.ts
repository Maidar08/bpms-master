import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {FieldGroups, FormGroupModel, FormsModel} from '../../models/app.model';
import {LOAN_CONTRACT, LOANCONTRACT, ORGANIZATION} from '../../loan-search-page/models/process-constants.model';

@Injectable({
  providedIn: 'root'
})
export class FormService {
  constructor(private http: HttpClient) {
  }

  jsonURL = 'assets/form-data-constants.json';
  LOAN_CONTRACT_JSON_URL = 'assets/loan-contract-form-data.json';
  BRANCH_BANKING_JSON_URL = 'assets/branch-banking-form-data.json';
  ORGANIZATION_REGISTRATION_JSON_URL = 'assets/organization-registration-form-data.json';

  private static getDefaultFormData(form): FormGroupModel {
    return {
      fieldGroups: [{fieldIds: [], fields: form, column: 3, title: null, type: 'default'}],
      entities: {entity: null, taskType: null}, buttons: {save: false, continue: false}
    };
  }

  private static findFields(fieldIds: string[], formFields: FormsModel[]): FormsModel[] {
    const tmp: FormsModel[] = [];
    for (const id of fieldIds) {
      for (const field of formFields) {
        if (field.id === id) {
          tmp.push(field);
        }
      }
    }
    return tmp;
  }

  private static setFieldGroup(fieldGroups: FieldGroups[], formFields: FormsModel[]) {
    if (fieldGroups.length === 0) {
      fieldGroups.push({fieldIds: [], fields: formFields, type: 'default', column: 3, title: null});
      return;
    }
    for (const fields of fieldGroups) {
      fields.fields = FormService.findFields(fields.fieldIds, formFields);
      if (null != fields.subFieldIds) {
        for (const subIds of fields.subFieldIds) {
          const tmp: FormsModel[] = FormService.findFields(subIds, formFields);
          fields.subFields.push(tmp);
        }
      }
      this.setCustomFieldsType(fields);
    }
  }

  private static setCustomFieldsType(fields: FieldGroups) {
    for (const field of fields.fields) {
      if (null != fields.buttonIds && fields.buttonIds.find(f => f === field.id)) {
        field.type = field.id === 'uploadFile' ? 'uploadFileButton' : 'button';
      } else if (null != fields.checkboxFieldIds && fields.checkboxFieldIds.find(f => f === field.id)) {
        field.type = 'checkbox';
      } else if (null != fields.chipFieldIds && fields.chipFieldIds.find(f => f === field.id)) {
        field.type = 'chipField';
      } else if ( null != fields.type && fields.type === 'textarea') {
        fields.fieldIds.forEach(() => {
          field.type = 'textArea';
        });
      }
    }
  }

  private static setForm(formData, formFields) {
    if (null != formData.fieldGroups && null != formData.fieldGroups.length) {
      FormService.setFieldGroup(formData.fieldGroups, formFields);
    } else {
      const defaultFieldGroup = this.getDefaultFormData(formFields);
      formData.fieldGroups = defaultFieldGroup.fieldGroups;
      formData.entities = defaultFieldGroup.entities;
      formData.buttons = defaultFieldGroup.buttons;
    }
  }

  private getJSON(type?: string): Observable<any> {
    if (type === 'branch-banking') {
      return this.http.get(this.BRANCH_BANKING_JSON_URL);
    }
    if (type === LOAN_CONTRACT || type === LOANCONTRACT) {
      return this.http.get(this.LOAN_CONTRACT_JSON_URL);
    }
    if (type === ORGANIZATION) {
      return this.http.get(this.ORGANIZATION_REGISTRATION_JSON_URL);
    }

    return this.http.get(this.jsonURL);
  }

  getForm(task, form, type?: string) {
    return new Promise(resolve => {
      this.getJSON(type).subscribe(data => {
        if (null != task.definitionKey && null != data[task.definitionKey]) {
          const formData: FormGroupModel = data[task.definitionKey];
          FormService.setForm(formData, form);
          resolve(formData);
        } else {
          resolve(FormService.getDefaultFormData(form));
        }
      });
    });
  }
}
