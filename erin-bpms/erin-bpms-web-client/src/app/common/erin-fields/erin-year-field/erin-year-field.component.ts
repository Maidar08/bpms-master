import {Component, EventEmitter, HostListener, Input, OnChanges, OnInit, Output} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-year-field',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipDisabled]="!isReadOnly(field) && !disabled">
      <div class="simple-form">
        <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
        <input  #year = "ngModel" class="simple-field" [(ngModel)]="field.formFieldValue.defaultValue"
               [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
               minlength="4" maxlength="4"  pattern="^[1-9][0-9]*$" (keyup)="checkValue(year)">
      </div>
      <small class ="field-err-msg" *ngIf= "year.errors && (year.invalid || year.touched)">Зөв утга оруулна уу!</small>
    </div>
  `
})
export class ErinYearFieldComponent extends ErinBaseField implements OnInit, OnChanges {
  @Input() field: FormsModel;
  @Input() disabled;
  @Output() validateInput = new EventEmitter<boolean>();
  @HostListener('keyup', ['$event']) keyUp(event: KeyboardEvent) {
    this.setValidator(event);
  }
  ngOnInit() {
    const value = this.field.formFieldValue.defaultValue;
    if (value === 'empty') {
       this.field.formFieldValue.defaultValue  = '';
     }
  }
  ngOnChanges(): void {
    this.ngOnInit();
  }
  setValidator(event): void {
    const regExpNumberFilter = /([1-9])+/;
    if (!regExpNumberFilter.test(event.key)) {
      event.preventDefault();
    }
  }
  checkValue(year) {
    this.validateInput.emit(!!(year.errors && (year.invalid || year.touched)));
  }
}
