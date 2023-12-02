import {Component, OnChanges} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-datepicker-field',
  template: `
    <div class="simple-form">
      <label><span *ngIf="isRequired(field)" class="error">*</span> {{field.label}}</label>
      <input class="simple-field"
             type="date" [ngModel]="field.formFieldValue.defaultValue | date:'yyyy-MM-dd'"
             (ngModelChange)="field.formFieldValue.defaultValue=parseDate($event)"
             [min]="disableBeforeDateValidation()" [max]="disableFutureDateValidation()"
             [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"  (change)="change()">
    </div>
  `
})
export class ErinDatepickerFieldComponent extends ErinBaseField implements OnChanges {
  ngOnChanges(): void {
    this.field.formFieldValue.defaultValue = this.parseDate(this.field.formFieldValue.defaultValue);
  }

  parseDate(dateString: string): Date {
    if (dateString) {
      return new Date(dateString);
    }
    return null;
  }

  disableBeforeDateValidation(): string {
    // store session
    if (this.field.id === 'loanGrantDate' && this.field.formFieldValue.defaultValue != null) {
      sessionStorage.setItem('loanGrantDate', this.field.formFieldValue.defaultValue);
    }
    // return disabled range
    if (this.field.id === 'firstPaymentDate' && sessionStorage.getItem('loanGrantDate') != null) {
      return new Date(sessionStorage.getItem('loanGrantDate')).toISOString().split('T')[0];
    }
    if (this.field.id === 'loanGrantDate' || this.field.id === 'firstPaymentDate') {
      return new Date().toISOString().split('T')[0];
    }
    return null;
  }

  disableFutureDateValidation() {
  }

}
