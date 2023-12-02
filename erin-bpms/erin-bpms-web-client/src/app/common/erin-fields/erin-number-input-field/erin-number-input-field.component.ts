import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-number-input-field',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipDisabled]="!isReadOnly(field) && !disabled">
      <div class="simple-form">
        <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
        <input class="simple-field"
               [thousandSeparator]="field.formFieldValue.defaultValue"
               [validations]="field.validations"
               [element]="field.formFieldValue"
               [(ngModel)]="field.formFieldValue.defaultValue"
               [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
               [placeholder]="0"
               [isRoundup]="isRoundup"
               [ngClass]="getSpecialStyle(field.id)"
               (change)="change()">
      </div>
    </div>
  `
})
export class ErinNumberInputFieldComponent extends ErinBaseField implements OnInit, OnChanges {
  @Input() isRoundup = true;
  @Output() valueChange = new EventEmitter<number>();
  ngOnInit() {
    this.field.formFieldValue.defaultValue =
      this.field.formFieldValue.defaultValue ? this.field.formFieldValue.defaultValue : '0';
  }
  ngOnChanges(): void {
    this.ngOnInit();
  }

  getSpecialStyle(id: string): string {
    return id === 'lackedAmountField' ?  'special-text' : '' ;
  }
}
