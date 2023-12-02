import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-email-field',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipDisabled]="!isReadOnly(field) && !disabled">
      <div class="simple-form">
        <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
        <input type="email" #email="ngModel" class="simple-field"
               [ngModel]=field.formFieldValue.defaultValue
               (ngModelChange)="onChange($event)"
               [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
               pattern="^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,7}$" (keyup)= "checkEmail(email)">
      </div>
      <div *ngIf="email.errors && (email.invalid || email.touched)">
        <small class ="field-err-msg" *ngIf="email.errors.pattern">Зөв email оруулна уу!</small>
      </div>
    </div>
  `
})
export class ErinEmailFieldComponent extends ErinBaseField implements OnInit {
  @Output() emailValidation = new EventEmitter();
  isEmailWrong: boolean;
  ngOnInit(): void {
    const value = this.field.formFieldValue.defaultValue;
    let newVal;
    value === 'true' ? newVal = 'Тийм' : value === 'false' ? newVal = 'Үгүй' : newVal = value;
    this.field.formFieldValue.defaultValue = newVal;
  }

  onChange(changingValue) {
    this.field.formFieldValue.defaultValue = changingValue;
    if (changingValue === '') {
      this.field.formFieldValue.defaultValue = null;
    }
    return this.field.formFieldValue.defaultValue;
  }

  checkEmail(email) {
    this.isEmailWrong =  !!(email.errors && (email.invalid || email.touched));
    this.emailValidation.emit(this.isEmailWrong);
   }
}
