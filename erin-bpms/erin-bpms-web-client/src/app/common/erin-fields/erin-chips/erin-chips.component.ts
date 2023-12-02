import {Component, EventEmitter, Output} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-chips',
  template: `
    <mat-chip-list #matChipList>
      <mat-chip class="chip-field" *ngIf="field.formFieldValue.defaultValue" color="primary"
                [matTooltip]="field.formFieldValue.defaultValue"
                [selectable]="true"
                [removable]="removable"
                [disabled]="field.disabled"
                (removed)="remove()">
        <p class="file-name"> {{field.formFieldValue.defaultValue}}</p>
        <mat-icon matChipRemove *ngIf="removable"> cancel</mat-icon>
      </mat-chip>
    </mat-chip-list>
  `,
})
export class ErinChipsComponent extends ErinBaseField {
  removable = true;
  @Output()  actionEmitter = new EventEmitter<any>();

  remove() {
    this.field.formFieldValue.defaultValue = null;
    this.actionEmitter.emit('removeFile');
  }
}

