import {Component, EventEmitter, Output} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-button',
  template: `
    <div [matTooltip]="field.formFieldValue.defaultValue" [matTooltipDisabled]="!isReadOnly(field) && !disabled">
        <button  class="erin-button" (click)="makeAction()" [disabled]="field.disabled">{{field.label}}</button>
    </div>
  `,
})
export class ErinButtonComponent extends ErinBaseField {
  @Output() actionEmitter = new EventEmitter<any>();

  makeAction() {
    this.actionEmitter.emit(this.field.formFieldValue.defaultValue);
  }
}
