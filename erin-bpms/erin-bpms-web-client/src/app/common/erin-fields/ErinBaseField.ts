import {FormsModel} from '../../models/app.model';
import {EventEmitter, Input, Output} from '@angular/core';
import {YES_MN} from '../../case-view-page/model/common.constants';

export abstract class ErinBaseField {
  @Input() field: FormsModel;
  @Input() disabled;
  @Output() changeEmitter = new EventEmitter<FormsModel>();
  @Output() enterEmitter = new EventEmitter<FormsModel>();

  isRequired(field: FormsModel): boolean {
    if (null == field.validations) {
      return false;
    }
    return !!field.validations.find(value => value.name === 'required');
  }

  isReadOnly(field: FormsModel): boolean {
    if (null == field.validations) {
      return false;
    }
    return !!field.validations.find(value => value.name === 'readonly');
  }

  getMaxValue(field: FormsModel): any {
    if (null == field.validations) {
      return false;
    }
    const validation = field.validations.find(value => value.name === 'max');
    if (validation != null) {
      return validation.configuration;
    }
  }

  getMaxLength(field: FormsModel): any {
    if (null == field.validations) {
      return false;
    }
    const validation = field.validations.find(value => value.name === 'maxlength');
    if (validation != null) {
      return validation.configuration - 1;
    }
  }


  change() {
    this.changeEmitter.emit(this.field);
  }

  enterEvent() {
    this.enterEmitter.emit(this.field);
  }
}
