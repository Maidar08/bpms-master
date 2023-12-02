import {Component, EventEmitter, Inject, Output} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'confirm-dialog',
  template: `
    <div class="dialog-container">
      <div mat-dialog-title>Хамтран хасах</div>
      <hr>
      <div mat-dialog-content>
        <p>Та "{{data.name}}" хамтранг зээлийн хүсэлтээс хасахдаа итгэлтэй байна уу!</p>
      </div>
      <div mat-dialog-actions>
        <button id="confirmButton" mat-flat-button mat-dialog-close="true" color="primary"
                (click)="confirm()">{{data.confirmButton}}</button>
        <button id="closeButton" mat-stroked-button mat-dialog-close color="primary"
                (click)="close()">{{data.closeButton}}</button>
      </div>
    </div>
  `,
  styleUrls: ['./confirm-remove-coBorrower-dialog.component.scss']
})
export class ConfirmRemoveCoBorrowerDialogComponent {
  @Output() executeTask = new EventEmitter<any>();

  constructor(public dialogRef: MatDialogRef<ConfirmRemoveCoBorrowerDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: {
              name: string, confirmButton: string, closeButton: string
    }) {
    dialogRef.disableClose = true;
  }

  confirm() {
    this.dialogRef.close(true);
  }

  close() {
    this.dialogRef.close(false);
  }
}
