import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormsModel} from '../../../models/app.model';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {CHANGE_COLLATERAL_LABELS, COLLATERAL_UDF_FIELDS_ID} from '../../model/task-constant';

@Component({
  selector: 'app-collateral-udf-dialog',
  template: `
    <div class="dialog-container">
      <div mat-dialog-title>{{data.title}}</div>
      <hr>
      <div mat-dialog-content>
        <dynamic-fields [forms]="collateralUdfForm" [numberOfColumns]="columnNumber" [inputState]="data.isCompleted" (inputCheck)="validateFieldInput($event)"></dynamic-fields>
        <p *ngIf="collateralUdfForm.length === 0" class="error-table-text">UDF ТАЛБАРУУД ХООСОН БАЙНА</p>
      </div>
      <div mat-dialog-actions>
        <button mat-flat-button color="primary" id="save-button" (click)="save()" [disabled]="data.isCompleted ||this.collateralUdfForm.length === 0
          || isWrongInput"> ХАДГАЛАХ </button>
        <button mat-stroked-button color="primary" id="close-button" (click)="cancel()">БУЦАХ</button>
      </div>
    </div>
  `,
  styleUrls: ['./collateral-udf-dialog.component.scss']
})
export class CollateralUdfDialogComponent implements OnInit {
  columnNumber: number;
  collateralUdfForm = [];
  parameterEntityType = 'UD_FIELD_COLLATERAL';
  isWrongInput: boolean;
  constructor(public dialogRef: MatDialogRef<CollateralUdfDialogComponent>, public sb: CaseViewSandboxService, public commonSb: CommonSandboxService,
              @Inject(MAT_DIALOG_DATA) public data: { title: string, form: FormsModel[], instanceId: string; taskId: string; collateralId: string, isCompleted: boolean }) {
  }

  ngOnInit(): void {
    this.columnNumber = 2;
    this.getCollateralUdf();
  }

  getCollateralUdf() {
    const overlayRef = this.commonSb.setOverlay();
    this.sb.getCollateralUdfFromProcessTable(this.data.instanceId, this.data.collateralId).subscribe(response => {
      this.getCollateralUdfForm(response, overlayRef);
    }, () => {
      overlayRef.dispose();
      this.dialogRef.close();
    });
  }

  getCollateralUdfForm(formValue: any, overlayRef: any ): void {
    this.sb.getCollateralUdf(formValue).subscribe(res => {
      this.collateralUdfForm = res;
      overlayRef.dispose();
      this.setFormValidationAndValue();
    }, () => {
      overlayRef.dispose();
    });
  }

  save() {
    const percentFieldValue = this.commonSb.getFormValue(this.collateralUdfForm, 'DUUSAAGUI BARILGIIN GUITSETGEL HUVI');
    let formattedFieldValue = String(percentFieldValue).replace(/(%)/g, '');
    if (formattedFieldValue === '' || formattedFieldValue == null) {formattedFieldValue = '0'; }
    const map = {'DUUSAAGUI BARILGIIN GUITSETGEL HUVI': formattedFieldValue};
    this.collateralUdfForm = this.commonSb.setFieldDefaultValue(this.collateralUdfForm, map);

    this.sb.saveCollateralUdf(this.data.instanceId, this.data.taskId, this.data.collateralId, this.collateralUdfForm).subscribe(() => {
      this.dialogRef.close(true);
    });
    this.sb.saveUdfToProcessTable(this.data.instanceId, this.data.collateralId, this.parameterEntityType, this.collateralUdfForm).subscribe(() => {
      this.dialogRef.close(true);
    });
  }

  cancel() {
    this.dialogRef.close(false);
  }

  setFormValidationAndValue(): void {
    this.commonSb.setFieldsValidation(this.collateralUdfForm, COLLATERAL_UDF_FIELDS_ID, {name: 'readonly', configuration: null});
    const newValue = {OWNED_TYPE: this.commonSb.getFormValue(this.data.form, 'formOfOwnership'),
                     'ULSIIN BURTGELIIN DUGAAR2': this.commonSb.getFormValue(this.data.form, 'stateRegistrationNumber')};
    this.collateralUdfForm = this.commonSb.setFieldDefaultValue(this.collateralUdfForm, newValue);

    this.commonSb.setFieldLabel(this.collateralUdfForm, CHANGE_COLLATERAL_LABELS);
  }

  validateFieldInput(value): void {
    this.isWrongInput = value;
  }
}
