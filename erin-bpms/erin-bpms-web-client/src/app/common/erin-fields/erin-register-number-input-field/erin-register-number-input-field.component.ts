import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

const CITIZEN_REGISTER_PATTERN = '^([А-ЯЁӨҮа-яёөү]){2}([0-9]){8}$';
const ORGANIZATION_REGISTER_PATTERN = '^[0-9]+$';

@Component({
  selector: 'erin-register-number-input-field',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipDisabled]="!isReadOnly(field) && !disabled">
      <div class="simple-form">
        <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
        <input #register="ngModel" class="simple-field"
               [(ngModel)]=field.formFieldValue.defaultValue
               [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
               pattern="{{registerPattern}}" (keyup)="checkRegister(register)">
      </div>
      <div *ngIf="isWrongRegister && !isOrganizationCoBorrower">
        <small class="field-err-msg" *ngIf="register.errors.pattern">Регистрээ зөв оруулна уу!</small>
      </div>
    </div>
  `
})

export class ErinRegisterNumberInputFieldComponent extends ErinBaseField implements OnChanges {
  @Output() registerValidation = new EventEmitter();
  @Input() isOrganizationRegister;
  @Input() currentTaskName;
  @Input() set borrowerTypeCoBorrower(value) {
    this.isOrganizationCoBorrower = value === 'organizationTypeCoBorrower';
  }

  isWrongRegister: boolean;
  registerPattern = CITIZEN_REGISTER_PATTERN;
  isOrganizationCoBorrower;

  ngOnChanges() {
    if ((this.currentTaskName !== '02. Монгол банк лавлагаа' && this.isOrganizationRegister && !this.isOrganizationCoBorrower)
      && this.borrowerTypeCoBorrower !== 'organizationTypeCoBorrower') {
      this.registerPattern = ORGANIZATION_REGISTER_PATTERN;
    }
    else {
      this.registerPattern = CITIZEN_REGISTER_PATTERN;
    }
  }

  checkRegister(register) {
    if (!this.isOrganizationCoBorrower) {
      this.isWrongRegister = !!(register.errors && (register.invalid || register.touched));
      this.registerValidation.emit(this.isWrongRegister);
    }
  }

  getPattern() {
    if (this.isOrganizationCoBorrower) {
      return null;
    }
    return this.registerPattern;
  }
}
