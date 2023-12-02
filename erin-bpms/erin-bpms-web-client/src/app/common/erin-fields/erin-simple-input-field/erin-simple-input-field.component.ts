import {Component, Input, OnInit} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';
import {FormsModel} from '../../../models/app.model';

@Component({
  selector: 'erin-simple-input-field',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipDisabled]="!isReadOnly(field) && !disabled">
      <div class="simple-form">
        <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
        <input class="simple-field"
               [(ngModel)]=field.formFieldValue.defaultValue
               [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
               maxLength="{{setMaxlength(field)}}"
               (change)="change()" (keyup.enter)="enterEvent()">
        <erin-field-hint *ngIf = "showFieldHint(field)"  [hintText]="fieldHint"></erin-field-hint>
      </div>
    </div>
  `
})
export class ErinSimpleInputFieldComponent extends ErinBaseField implements OnInit {
  @Input() inputMaxLength;
  @Input() fieldHint;

  ngOnInit(): void {
    const value = this.field.formFieldValue.defaultValue;
    let newVal;
    value === 'true' ? newVal = 'Тийм' : value === 'false' ? newVal = 'Үгүй' : newVal = value;
    if (this.field.type === 'Date' && value != null) {
      newVal = value.toString();
    }
    this.field.formFieldValue.defaultValue = newVal;
  }

  setMaxlength(field: FormsModel) {
    if (this.getMaxLength(field)) {
      return this.getMaxLength(field);
    } else {
      return 1000000;
    }
  }

  showFieldHint(field: FormsModel) {
    return field.label !== 'Зээлийн данс' && field.label !== 'Зээлийн дансны дугаар';
  }
}
