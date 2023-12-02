import {Component} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-textarea-input-field',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipClass]="'erinTextareaTooltip'"
         [matTooltipDisabled]="!isReadOnly(field) && !disabled">
      <div class="textarea-form">
        <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
        <textarea class="simple-textarea"
               [(ngModel)]=field.formFieldValue.defaultValue
               [matTooltip]="field.formFieldValue.defaultValue"
               [matTooltipClass]="'erinTextareaTooltip'"
               [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
               [maxlength]="getMaxLength(field)">
        </textarea>
      </div>
    </div>
  `,
  styleUrls: ['./erin-textarea-field.component.scss']
})
export class ErinTextareaFieldComponent extends ErinBaseField {
}
