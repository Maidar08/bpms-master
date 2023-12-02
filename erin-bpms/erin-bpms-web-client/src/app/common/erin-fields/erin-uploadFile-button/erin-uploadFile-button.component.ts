import {Component, EventEmitter, Output} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-uploadFile-button',
  template: `
    <button mat-stroked-button color="primary" (click)="fileInput.click()" [disabled]="field.disabled">{{field.label}}</button>
    <input #fileInput type="file" style="visibility: hidden; width: 0" accept=".xls, .xlsx" (change)="makeAction($event)" (click)="clear($event)"/>
  `,
})
export class ErinUploadFileButtonComponent extends ErinBaseField {
  @Output() actionEmitter = new EventEmitter<any>();

  makeAction(event) {
    this.actionEmitter.emit({action: this.field.formFieldValue.defaultValue, data: event.target.files});
  }

  clear(event) {
    event.target.value = null;
  }
}
