import {Component, EventEmitter, HostListener, OnChanges, OnInit, Output} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-phone-number-input-field',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipDisabled]="!isReadOnly(field) && !disabled">
      <div class="simple-form">
        <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
        <input #phoneNumber="ngModel" class="simple-field"
               [(ngModel)]=field.formFieldValue.defaultValue
               [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled" >
      </div>
    </div>
  `,
  styleUrls: ['./erin-phone-number-input-field.component.scss']
})
export class ErinPhoneNumberInputFieldComponent extends ErinBaseField implements OnInit, OnChanges {
  @Output() valueChange = new EventEmitter<number>();

  ngOnInit() {
    this.field.formFieldValue.defaultValue =
      this.field.formFieldValue.defaultValue ? this.field.formFieldValue.defaultValue : '';
  }
  ngOnChanges(): void {
    this.ngOnInit();
  }
  @HostListener('keypress', ['$event']) keyPress(event: KeyboardEvent) {
    this.setValidator(event);
  }
  setValidator(event): void {
    const regExpNumberFilter = /(\d)+/;
    const regSpaceFilter = /(\s)+/;
    const regCommaFilter = /(,)+/;
    if (!regExpNumberFilter.test(event.key) && !regSpaceFilter.test(event.key) && !regCommaFilter.test(event.key)) {
      event.preventDefault();
    }
  }
}
