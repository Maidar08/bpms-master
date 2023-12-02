import {Component, HostListener, Input} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';
import {FormsModel} from '../../../models/app.model';

@Component({
  selector: 'erin-string-but-number-field',
  template: `
    <div class="simple-form">
      <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
      <input class="simple-field" #workerCif="ngModel"
             [(ngModel)]=field.formFieldValue.defaultValue
             [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
             maxlength="{{inputMaxLength}}"  (change)="change()" (keyup.enter)="change()"  >
      <erin-field-hint *ngIf = "showFieldHint(field)" [hintText]="fieldHint"></erin-field-hint>
    </div>
  `
})
export class ErinStringButNumberFieldComponent extends ErinBaseField {
  @Input() inputMaxLength;
  @Input() fieldHint;

  @HostListener('keypress', ['$event']) keyPress(event: KeyboardEvent) {
    this.setValidator(event);
  }
  setValidator(event): void {
    const regExpNumberFilter = /([0-9])+/;
    if (!regExpNumberFilter.test(event.key)) {
      event.preventDefault();
    }
  }

  showFieldHint(field: FormsModel) {
    return field.label !== 'Зээлийн данс' && field.label !== 'Зээлийн дансны дугаар';
  }

}
