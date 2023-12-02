import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormsModel} from '../../models/app.model';

@Component({
  selector: 'erin-custom-dialog',
  template: `
    <div class="dialog-container">
      <div mat-dialog-title>{{data.title}}</div>
      <hr *ngIf="data.hasDivider">
      <div mat-dialog-content>
        <p>{{data.message}}</p>
      </div>
      <div mat-dialog-actions>
        <div *ngFor="let button of data.buttons">
          <button id="confirmButton" mat-flat-button mat-dialog-close="true" color="primary" (click)="close({action: button})">
            {{button.actionName}}</button>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./erin-custom-dialog.component.scss']
})
export class ErinCustomDialogComponent {
  formField: FormsModel = this.data.formField != null ? this.data.formField : null;

  constructor(public dialogRef: MatDialogRef<ErinCustomDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data) {
    dialogRef.disableClose = true;
  }
  close(button) {
    this.dialogRef.close(button);
  }
}
