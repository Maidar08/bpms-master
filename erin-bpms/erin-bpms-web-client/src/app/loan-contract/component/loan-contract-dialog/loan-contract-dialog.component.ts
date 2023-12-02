import {Component, Inject} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-loan-contract-dialog',
  template: `
  <div class="dialog-container">
      <div mat-dialog-title>{{data.title}}</div>
      <hr *ngIf="data.hasDivider">
      <div mat-dialog-content>
        <p>{{data.message}}</p>
      </div>
    <dynamic-fields *ngIf="showForm" [forms]="formField"></dynamic-fields>
      <div class="workspace-actions">
        <div *ngFor="let button of data.buttons">
          <button id="confirmButton" mat-flat-button mat-dialog-close="true" color="primary" (click)="close({action: button})">
            {{button.actionName}}</button>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./loan-contract-dialog.component.scss']
})
export class LoanContractDialogComponent {
  formField: FormsModel[] = this.data.formField != null ? this.data.formField : null;
  title = 'Хэвлэх гэрээ сонгох';

  constructor(public dialogRef: MatDialogRef<LoanContractDialogComponent>, private snack: MatSnackBar,
              @Inject(MAT_DIALOG_DATA) public data) {
    dialogRef.disableClose = true;
  }
  close(button) {
    if (button.action != null && button.action.actionId === 'continue') {
      if (this.checkRequiredFieldsValue()) {
        this.dialogRef.close(button);
      }
    } else {
      this.dialogRef.close(button);
    }
  }

  showForm() {
    return !! this.formField;
  }

  private checkRequiredFieldsValue(): any {
    if (!this.showForm()) {
      return true;
    }
    for (const field of this.formField) {
      const isRequired = field.validations.find(val => val.name === 'required');
      const value = field.formFieldValue.defaultValue;
      if (isRequired && (value == null || value === '')) {
        this.snack.open(field.label + ' талбарыг бөглөнө үү!', 'ХААХ', {duration: 3000});
        return false;
      }
    }

    return true;
  }

}
