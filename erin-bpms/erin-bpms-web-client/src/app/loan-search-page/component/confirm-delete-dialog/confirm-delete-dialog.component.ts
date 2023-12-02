import {Component, EventEmitter, Inject, Output} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'confirm-dialog',
  template: `
    <div class="dialog-container">
      <div mat-dialog-title>Хүсэлт устгах</div>
      <hr>
      <div mat-dialog-content>
        <p>Та "{{data.state}}"  төлөвтэй, "{{data.id}}" дугаартай хүсэлтийг устгахад итгэлтэй байна уу? Хүсэлтэй холбоотой үүсгэгдсэн файлууд хамт устгах болохыг анхаарна уу!</p>
      </div>
      <div mat-dialog-actions>
        <button id="confirmButton" mat-flat-button mat-dialog-close="true" color="primary"
                (click)="confirm()">{{data.confirmButton}}</button>
        <button id="closeButton" mat-stroked-button mat-dialog-close color="primary"
                (click)="close()">{{data.closeButton}}</button>
      </div>
    </div>
  `,
  styleUrls: ['./confirm-delete-dialog.component.scss']
})
export class ConfirmDeleteDialogComponent {
  @Output() executeTask = new EventEmitter<any>();

  constructor(public dialogRef: MatDialogRef<ConfirmDeleteDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: {
      state: string, id: string, confirmButton: string, closeButton: string
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
