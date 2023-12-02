import {ChangeDetectorRef, Component, Inject, OnChanges, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

export interface DialogData {
  title: string;
  message: string;
  alternativeMessage?: string;
  confirmButton?: string;
  hideCancelButton?: boolean;
}
@Component({
  selector: 'confirm-dialog',
  template: `
    <div *ngIf="data.title">
      <h1 mat-dialog-title>{{data.title}}</h1>
      <mat-divider></mat-divider>
    </div>
    <div mat-dialog-content>
      <pre class="mat-typography">{{data.message}}</pre>
      <div class="alternative mat-typography">{{data.alternativeMessage}}</div>
    </div>
    <div mat-dialog-actions class="dialog-buttons">
      <button *ngIf="hasCancelButton" mat-button [mat-dialog-close]="false" id="confirm-dialog-no-btn">Үгүй</button>
      <span class="margin-left"></span>
      <button mat-flat-button class="proceed-btn " color="primary" [mat-dialog-close]="true" cdkFocusInitial id="confirm-dialog-yes-btn">
        {{confirmButton}}
      </button>
    </div>
  `,
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent implements OnInit, OnChanges{
  confirmButton: string;
  hasCancelButton: boolean;

  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    private cd: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }

  ngOnInit(): void {
    this.confirmButton = this.data.confirmButton ? this.data.confirmButton : 'Тийм';
    this.hasCancelButton = !this.data.hideCancelButton;
    this.cd.detectChanges();
  }

  ngOnChanges(): void {
    this.cd.detectChanges();
  }


}
