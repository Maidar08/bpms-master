import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-number-max-value-input-field',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipDisabled]="!isReadOnly(field) && !disabled">
      <div class="simple-form">
        <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
        <input class="simple-field"
               [(ngModel)]="field.formFieldValue.defaultValue"
               [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
               [placeholder]="0"
               maxlength="{{inputMaxLength}}"
        (change)="change()">
      </div>
      <div *ngIf="checkMaxValue(field.formFieldValue.defaultValue)">
        <small class ="field-err-msg">{{inputMaxValue}} хүртэлх тоон утга оруулах боломжтой байна</small>
      </div>
    </div>
  `
})
export class ErinNumberMaxValueInputFieldComponent extends ErinBaseField implements OnInit, OnChanges {
  @Input() inputMaxLength;
  @Input() inputMaxValue;
  isMaxValue: boolean;
  @Output() valueChange = new EventEmitter<number>();
  @Output() maxValueValidation = new EventEmitter();

  ngOnInit() {
    this.field.formFieldValue.defaultValue =
      this.field.formFieldValue.defaultValue ? this.field.formFieldValue.defaultValue : '0';
  }
  ngOnChanges(): void {
    this.ngOnInit();
  }

  checkMaxValue(fieldValue) {
    this.isMaxValue = fieldValue > this.inputMaxValue;
    this.maxValueValidation.emit(this.isMaxValue);
    return this.isMaxValue;
  }
}
