import {Injectable} from '@angular/core';
import {FieldOptions, FormsModel} from '../../models/app.model';
import {
  ADD,
  DEFAULT_VALUE,
  DIVIDE,
  DROPDOWN,
  FieldRelationModel,
  MATH,
  MULTIPLY,
  REQUIRED_VALIDATION,
  SUBTRACT,
  TEXT,
  VALIDATION,
  VALIDATION_TEXT_REFERENCE
} from '../model/form-relation-constant.model';
import {CommonService} from '../../common/service/common.service';
import {CaseViewSandboxService} from '../case-view-sandbox.service';
import {BIG_DECIMAL, DATE} from '../model/common.constants';


@Injectable({
  providedIn: 'root'
})
export class FormRelationService {
  constructor(private commonSb: CommonService, private sb: CaseViewSandboxService) {
  }
  private form: Map<string, FormsModel> = new Map<string, FormsModel>();
  private formRelation = new Map<string, FieldRelationModel[]>();

  private static isValidMathOperations(operations: string[], reference: string[]): boolean {
    return (operations === null || reference === null) || ( operations.length + 1 === reference.length );
  }

  private static convertJsonToMap(jsonArray, map: Map<string, any> ) {
    for (const key in jsonArray) {
      if (jsonArray.hasOwnProperty(key)) {
        for (const field of jsonArray[key]) {
          if (field.options !== null) {
            const options = new Map< string, FieldOptions[] >();
            for (const optionKey in field.options) {
              if ( field.options.hasOwnProperty(optionKey) ) {
                options.set( optionKey, field.options[optionKey] );
              }
            }
            field.options = options;
          }
        }
        map.set( key, jsonArray[key] );
      }
    }
  }

  private static setValidation( updatedField: FormsModel, validationState: boolean, validationName?) {
    const validationType = updatedField.validations.find( validation => validation.name === validationName);
    // when add validation
    if ( validationState && validationType === undefined ) {
      updatedField.validations.push( { name: validationName, configuration: null } );
      this.isRequiredValidation(validationName, updatedField, true);
    }
    // when remove field
    if ( !validationState && validationType ) {
      const index = updatedField.validations.indexOf( validationType );
      updatedField.validations.splice(index, 1);
      this.isRequiredValidation(validationName, updatedField, false);
    }
  }
  private static isRequiredValidation(validationName: string, updatedField: FormsModel, state: boolean): void {
    if (validationName === 'required') {
      updatedField.required = state;
    }
  }

  private static calculate(value1: number, value2: number, operation: string): number {
    switch (operation) {
      case ADD:
        return value1 + value2;
      case SUBTRACT:
        return value1 - value2;
      case MULTIPLY:
        return value1 * value2;
      case DIVIDE:
        return value1 / value2;
      default:
        return  0;
    }
  }

  private static hasSameOptions(option: FieldOptions[], newOption: FieldOptions[]): boolean {
    if (option.length !== newOption.length ) {
      return false;
    }

    for (let i = 0; i < option.length; i++) {
      if ( option[i].value !== newOption[i].value ) {
        return false;
      }
    }

    return true;
  }

  private compareValueAndOption(field: FieldRelationModel, reference: string): boolean {
    const selectedField: FormsModel = this.form.get(reference);
    const selectedOption = selectedField.options.find(op => op.value === selectedField.formFieldValue.defaultValue);
    if (null != selectedOption && null !=  field.options.get(reference).length && field.options.get(reference).length > 0) {
      const opId: string = selectedOption.id;
      const referenceOption = field.options.get(reference).find(op => op.id === opId);
      return null != referenceOption;
    }
    return false;
  }

  private checkReferenceValue(field: FieldRelationModel, reference: string) {
    const selectedField: FormsModel = this.form.get(reference);
    const referenceValue = field.options.get(reference);
    return String(selectedField.formFieldValue.defaultValue) === String(referenceValue);
  }

  private changeStateByReferenceDropdown(field: FieldRelationModel, reference: string[], type: string) {
    let state = false;
    if ( reference.length === 1 && this.form.has(reference[0]) ) {
      if (this.commonSb.isNull(this.form.get(reference[0]))) { return; }
      state = this.compareValueAndOption(field, reference[0]);
    } else if (this.form.has( reference[0] ) && this.form.has( reference[1] )) {
      const referenceValue1 = this.form.get( reference[0] ).formFieldValue.defaultValue;
      if (field.options.has( referenceValue1 )) {
        const referenceValue2 = this.form.get(reference[1]).formFieldValue.defaultValue;
        state = !!field.options.get(referenceValue1).find( option => option.value === referenceValue2 );
      }
    }
    this.updateField( field.updatedFieldId, null, type,  state );
  }

 private changeStateByReferenceText(field: FieldRelationModel, reference: string[]) {
    let state = false;
    if ( reference.length === 1 && this.form.has(reference[0]) ) {
      if (this.commonSb.isNull(this.form.get(reference[0]))) { return; }
      state = this.checkReferenceValue(field, reference[0]);
    }
    this.updateField( field.updatedFieldId, null, VALIDATION,  state );
  }

  private changeState(field: FieldRelationModel, reference: string[], type: string) {
    if (type === VALIDATION || type === REQUIRED_VALIDATION) {
      this.changeStateByReferenceDropdown(field, reference, type);
    } else if (type === VALIDATION_TEXT_REFERENCE) {
      this.changeStateByReferenceText(field, reference);
    }
  }

  private updateField(fieldId, value, updateType, validation?: boolean): void {
    const updatedField: FormsModel = this.form.get(fieldId);
    if (updatedField) {
      switch (updateType) {
        case DEFAULT_VALUE:
          updatedField.formFieldValue.defaultValue = value;
          break;
        case DROPDOWN:
          const hasDefaultValue = value.find( val => val.value === updatedField.formFieldValue.defaultValue );
          if (!FormRelationService.hasSameOptions(updatedField.options, value)) {
            updatedField.options = value;
            if (!hasDefaultValue) {
              updatedField.formFieldValue.defaultValue = undefined;
            }
          }
          this.updateForm(updatedField.id);
          break;
        case VALIDATION:
          FormRelationService.setValidation( updatedField, validation , 'readonly');
          break;
        case REQUIRED_VALIDATION:
          FormRelationService.setValidation(updatedField, validation, 'required');
          break;
        default:
          break;
      }
    }
  }

  private executeMathOperation(operation: string[], reference: string[], result: number): number {
    if (operation.length === 0) {
      return result;
    }
    const value1: number = this.getValue(reference.pop());
    const value2: number = this.getValue(reference.pop());
    result = FormRelationService.calculate(value2, value1, operation.pop());
    reference.push(String(result));
    return this.executeMathOperation( operation, reference, result );
  }

  private getValue(element): number {
    if (this.form.has(element)) {
      return this.commonSb.formatNumberService( this.form.get(element).formFieldValue.defaultValue );
    }
    return this.commonSb.formatNumberService(element);
  }

  private updateDropdownField(detectedFieldId: string, field: FieldRelationModel, updateType: string): void {
    const updateField = this.form.get(detectedFieldId);
    if (updateField != null) {
      const options = updateField.options;
      const selectedOption = options.find( opt => opt.value === updateField.formFieldValue.defaultValue );
      if (selectedOption != null) {
        if ( field.options.has(selectedOption.id)) {
          this.updateField( field.updatedFieldId, field.options.get(selectedOption.id), updateType );
        }
      }
    }
  }
  public checkRequiredFieldsValue(requiredCheckFields, form) {
    const updateForm = this.commonSb.getForm(form);
    const fieldIds: string[] = requiredCheckFields[0];
    const fieldValues = requiredCheckFields[1];
    if (null == fieldValues) {
      return true;
    }
    for (let i = 0; i < fieldIds.length; i++) {
      const field = updateForm.find( f => f.id === fieldIds[i] );
      if (null == field || null == field.formFieldValue) {
        return fieldIds[i];
      }
      switch (field.type) {
        case BIG_DECIMAL:
          if (!this.commonSb.isEqualNumbers(field.formFieldValue.defaultValue, fieldValues[i])) {
            return fieldIds[i];
          }
          break;
        case DATE:
          if (!this.commonSb.isEqualDates(field.formFieldValue.defaultValue, fieldValues[i])) {
            return fieldIds[i];
          }
          break;
        default:
          if (field.formFieldValue.defaultValue !== fieldValues[i]) {
            return fieldIds[i];
          }

      }
    }
    return true;
  }

  setForm(form: FormsModel[], defKey: string) {
    return new Promise(resolve => {
      form.forEach( field => this.form.set( field.id, field ) );
      this.sb.getFromRelation(defKey).subscribe( res => {
        FormRelationService.convertJsonToMap(res, this.formRelation);
        resolve(true);
      } );
    });
  }

  updateForm(detectedFieldId: string) {
    if (this.formRelation.has(detectedFieldId)) {
      const relatedFields: FieldRelationModel[] = this.formRelation.get( detectedFieldId );
      if (relatedFields) {
        for (const field of relatedFields) {
          const reference: string[] = Array.from(field.references);
          const operationType: string = field.operationType;
          switch (operationType) {
            case MATH:
              const operations: string[] = Array.from(field.operations);
              if (FormRelationService.isValidMathOperations(operations, reference)) {
                const updatedValue = this.executeMathOperation(operations, reference, 0);
                this.updateField(field.updatedFieldId, updatedValue, DEFAULT_VALUE);
              }
              break;
            case DROPDOWN:
              this.updateDropdownField(detectedFieldId, field, DROPDOWN);
              break;
            case VALIDATION:
              this.changeState(field, reference, VALIDATION);
              break;
            case REQUIRED_VALIDATION:
              this.changeState(field, reference, REQUIRED_VALIDATION);
              break;
            case VALIDATION_TEXT_REFERENCE:
              this.changeState(field, reference, VALIDATION_TEXT_REFERENCE);
              break;
            case TEXT:
              this.updateDropdownField(detectedFieldId, field, DEFAULT_VALUE);
              break;
            default:
              break;
          }
        }
      }
    }
    return this.form;
  }

  updateRequiredFields(requiredCheckFields, form): any[] {
    const updateForm = this.commonSb.getForm(form);
    const fieldIds: string[] = requiredCheckFields[0];
    const fieldValues = [];
    for (const id of fieldIds) {
      const field = updateForm.find( f => f.id === id );
      if (null != field && null != field.formFieldValue) {
        if (field.type === DATE) {
          fieldValues.push(new Date(field.formFieldValue.defaultValue));
        } else {
          fieldValues.push(field.formFieldValue.defaultValue);
        }
      } else {
        fieldValues.push(null);
      }
    }
    return [fieldIds, fieldValues];
  }

}
