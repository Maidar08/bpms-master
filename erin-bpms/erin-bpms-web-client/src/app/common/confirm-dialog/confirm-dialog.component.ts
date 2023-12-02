import {Component, EventEmitter, Inject, Output} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {CREATE_COLLATERAL_TASK_NAME} from '../../case-view-page/model/task-constant';

@Component({
  selector: 'confirm-dialog',
  template: `
    <div class="dialog-container">
      <div mat-dialog-title>{{data.title}}</div>
      <hr *ngIf="data.hasDivider">
      <div mat-dialog-content>
        <span *ngIf="!isCreateCollateral()" class="taskName">{{data.taskName}}</span>&nbsp;<span class="message">{{data.message}}</span>
      </div>
      <div mat-dialog-actions>
        <button id="confirmButton" mat-flat-button mat-dialog-close="true" color="primary"
                (click)="confirm()">{{data.confirmButton}}</button>
        <button id="closeButton" mat-stroked-button mat-dialog-close color="primary"
                (click)="close()">{{data.closeButton}}</button>
      </div>
    </div>
  `,
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent {
  @Output() executeTask = new EventEmitter<any>();

  constructor(public dialogRef: MatDialogRef<ConfirmDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: {
      taskName: string, confirmButton: string, closeButton: string, message: string, executionType: string, title: string, hasDivider: boolean
    }) {
    dialogRef.disableClose = true;
  }

  confirm() {
    this.dialogRef.close(true);
  }

  close() {
    this.dialogRef.close(false);
  }

  isCreateCollateral() {
    return (this.data.taskName === CREATE_COLLATERAL_TASK_NAME && this.data.executionType === 'enabled');
  }
}
