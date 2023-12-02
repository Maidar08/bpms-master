import {Component, HostListener} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-workerCIF-field',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipDisabled]="!isReadOnly(field) && !disabled">
      <div class="simple-form">
        <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
        <input class="simple-field" #workerCif="ngModel"
               [(ngModel)]=field.formFieldValue.defaultValue
               [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
               maxlength="8"
               pattern="^([0-9]){8}$">
      </div>
      <div *ngIf="workerCif.errors && workerCif.touched">
        <small *ngIf="workerCif.errors.pattern">Байгууллагын CIF зөв оруулна уу </small>
      </div>
    </div>
  `
})
export class ErinWorkerCIFFieldComponent extends ErinBaseField {
  @HostListener('keypress', ['$event']) keyPress(event: KeyboardEvent) {
    this.setValidator(event);
  }
  setValidator(event): void {
    const regExpNumberFilter = /([0-9])+/;
    if (!regExpNumberFilter.test(event.key)) {
      event.preventDefault();
    }
  }
}
