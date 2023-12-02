import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormsModel} from '../../../models/app.model';

@Component({
  selector: 'app-change-assignee-dialog',
  template:  `
    <p> Хүсэлт шилжүүлэх </p>
    <hr>
    <div class="input-field-container">
<!--      <div class="simple-form">-->
<!--        <label>{{data.form.label}}</label>-->
<!--        <select [(ngModel)]="data.form.formFieldValue.defaultValue" class="simple-field">-->
<!--          <option *ngFor="let choice of data.form.options" [value]="choice.id">{{ choice.value }}</option>-->
<!--        </select>-->
<!--      </div>-->
      <erin-dropdown-field [field]="data.form"></erin-dropdown-field>
    </div>
    <div class="button-container">
      <button mat-button class="continue" [mat-dialog-close]="data.form">Үргэлжлүүлэх</button>
      <button mat-button class="cancel" (click)="cancel()">Болих</button>
    </div>

  `,
  styleUrls: ['./change-assignee-dialog.component.scss']
})
export class ChangeAssigneeDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ChangeAssigneeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      form: FormsModel
    }) {}

  cancel(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
  }

}
