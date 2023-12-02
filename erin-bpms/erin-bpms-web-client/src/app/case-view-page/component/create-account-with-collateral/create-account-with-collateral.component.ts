import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '../../../common/confirm-dialog/confirm-dialog.component';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {
  BIG_DECIMAL,
  CLOSE,
  COLLATERAL_CONNECTION_RATE,
  COLLATERAL_ID,
  DUPLICATED_COLLATERAL_ORDER_MESSAGE,
  FIXED_ACCEPTED_LOAN_AMOUNT,
  ORDER,
  WRONG_COLLATERAL_CONNECTION_RATE_VALUE_MESSAGE,
  WRONG_COLLATERAL_ORDER_MESSAGE
} from '../../model/task-constant';

@Component({
  selector: 'create-account-with-collateral',
  template: `
    <mat-card class="card-container">
      <erin-title [titleName]="currentTaskName"></erin-title>
      <ng-container>
        <dynamic-fields [forms]="collateralAssetForm[0]"></dynamic-fields>
        <hr class="separator-line">
        <dynamic-fields [forms]="collateralAssetForm[1]" (change)="changeLoanAccount()"></dynamic-fields>
        <dynamic-fields *ngIf="hasInsurance" [forms]="collateralAssetForm[2]"></dynamic-fields>
        <erin-title [titleName]="'UDF талбар'"></erin-title>
        <dynamic-fields [forms]="collateralAssetForm[3]"></dynamic-fields>
        <dynamic-fields [forms]="collateralAssetForm[4]"></dynamic-fields>
      </ng-container>

      <ng-container class="collateralAssets">
        <erin-title [titleName]="'Барьцаа хөрөнгө'"></erin-title>
        <div *ngFor="let form of collateralAssetForm[COLLATERAL_ASSET_INDEX] index as i">
          <span *ngIf="collateralAssetForm[COLLATERAL_ASSET_INDEX].length > 1 && enableCreateAccount"
                class="material-icons-outlined remove-btn"
                (click)="collateralAssetsRemove(i)">indeterminate_check_box</span>
          <dynamic-fields [forms]="form" [numberOfColumns]="columnNumber"></dynamic-fields>
        </div>
      </ng-container>

      <div class="workspace-actions">
        <button mat-flat-button color="primary" id='submitButton' (click)="submit()" *ngIf="showContinueButton"
                [disabled]="!enableContinue">ҮРГЭЛЖЛҮҮЛЭХ
        </button>
        <button mat-flat-button color="primary" id='saveButton' (click)="save()" *ngIf="showSaveButton"
                [disabled]="disableState">ХАДГАЛАХ
        </button>
        <button mat-flat-button color="primary" id='calculateButton' (click)="calculate()" *ngIf="showCalculateButton"
                [disabled]="disableState">
          ТООЦООЛОХ
        </button>
        <button mat-flat-button color="primary" id='createButton' (click)="createAccount()"
                *ngIf="showCreateAccountButton" [disabled]="!enableCreateAccount">ДАНС ҮҮСГЭХ
        </button>
      </div>
    </mat-card>
  `,
  styleUrls: ['./create-account-with-collateral.component.scss']
})
export class CreateAccountWithCollateralComponent implements OnInit, OnChanges {
  @Input() set collateralAmount(collaterals: any[]) {
    if (collaterals) {
      this.setCollateral( collaterals[0], collaterals[1] );
    }
  }
  @Input() instanceId: string;
  @Input() currentTaskName: string;
  @Input() subtitlesInput;
  @Input() requestId;
  @Input() data: FormsModel[][];
  @Input() enableContinue: boolean;
  @Input() enableCreateAccount: boolean;
  @Input() disableState: boolean;
  @Input() showContinueButton: boolean;
  @Input() showSaveButton: boolean;
  @Input() showCalculateButton: boolean;
  @Input() showCreateAccountButton: boolean;
  @Input() accountList = [];
  @Input() hasInsurance;
  @Output() saveEmitter = new EventEmitter<any[]>();
  @Output() submitEmitter = new EventEmitter<any[]>();
  @Output() calculateEmitter = new EventEmitter<any[]>();
  @Output() createAccountEmitter = new EventEmitter<any[]>();
  @Output() getLoanInfoEmitter = new EventEmitter<any[]>();


  constructor(public dialog: MatDialog, private commonService: CommonSandboxService) {
    this.columnNumber = 5;
  }

  collateralAssetForm = [];
  subtitles: string[];
  readonly length = 4;
  dialogData = {name: '', confirmButton: 'Тийм', closeButton: 'Үгүй',
  message: 'Та сонгосон барьцааг дансанд холбох барьцаанаас хасахдаа итгэлтэй байна уу?' };
  COLLATERAL_ASSET_INDEX = 5;
  columnNumber: number;
  isValidCollateralRate = true;

  ngOnChanges(changes: SimpleChanges): void {
    /* It's called when switch between active and completed task */
    if (!changes.instanceId) { this.ngOnInit(); }
    if (changes.currentTaskName !== undefined && changes.currentTaskName.previousValue !== undefined) {this.ngOnInit(); } else {
      for (const prop in changes) {
        if (prop === 'accountList') {
          this.ngOnInit();
        }}}
  }

  ngOnInit(): void {
    this.getLoanInfoEmitter.emit();
    this.collateralAssetForm = this.data;
    this.subtitles = this.subtitlesInput;
    this.setDefaultValue(this.collateralAssetForm[2], 'insuranceCUDF');
  }

  collateralAssetsRemove(index) {
    const config = new MatDialogConfig();
    config.width = '500px';
    config.data = this.dialogData;
    const dialogRef = this.dialog.open(ConfirmDialogComponent, config);
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.collateralAssetForm[this.COLLATERAL_ASSET_INDEX].splice(index, 1);
      }
    });
  }

  changeLoanAccount() {
    if (this.collateralAssetForm.length > 1) {
      const loanRate = this.getFormValue(this.collateralAssetForm[1], 'yearlyInterestRateString');
      let increasedRate: any;
      if (loanRate != null) {increasedRate = this.commonService.findMultiplication(Number(loanRate), 0.2).toFixed(2); }
      const startDate = this.getFormValue(this.collateralAssetForm[1], 'firstPaymentDate');
      let startDay;
      if (startDate != null) {startDay = startDate.getDate(); }
      const accountNumber = this.getFormValue(this.collateralAssetForm[1], 'currentAccountNumber');
      let branchId;
      const accountForPayment = this.accountList.find(account => account.accountId === accountNumber);
      if (null != accountForPayment) {branchId = accountForPayment.branchId; }
      const newMap = {depositInterestRateString: String(increasedRate) + '%', dayOfPayment: String(startDay), accountBranchNumber: String(branchId)};
      this.collateralAssetForm[1] = this.commonService.setFieldDefaultValue(this.collateralAssetForm[1], newMap);
    }
  }

  calculate() {
    const overlayRef = this.commonService.setOverlay();
    const collateralForm = this.getCollateralForm();
    const acceptedLoanAmount = this.collateralAssetForm[0].find( field => field.id === FIXED_ACCEPTED_LOAN_AMOUNT );
    if (acceptedLoanAmount != null) {
      const value = this.commonService.formatNumberService(acceptedLoanAmount.formFieldValue.defaultValue);
      collateralForm.forEach( collateral => collateral[acceptedLoanAmount.id] = value );
    }
    this.calculateEmitter.emit(collateralForm);
    overlayRef.dispose();
  }

  save() {
    const collateralMap = this.getCollateralMap();
    const submitForm = this.commonService.getForm( this.getSubmitForm() );
    this.saveEmitter.emit([submitForm, JSON.stringify(collateralMap[0]), collateralMap[1]]);
  }

  createAccount() {
    if (!this.checkValidation()) { return; }
    const collateralMap = this.getCollateralMap();
    const submitForm = this.commonService.getForm( this.getSubmitForm() );
    this.setDefaultValue(submitForm, 'insuranceCUDF');
    this.createAccountEmitter.emit([submitForm, collateralMap[0], this.hasInsurance, collateralMap[1]]);
  }

  submit() {
    if (!this.checkValidation()) { return; }
    const collateralMap = this.getCollateralMap();
    const submitForm = this.commonService.getForm( this.getSubmitForm() );
    this.setDefaultValue(submitForm, 'insuranceCUDF');
    this.submitEmitter.emit([submitForm, collateralMap[0], this.hasInsurance, collateralMap[1]]);
  }

  private checkValidation(): boolean {
    if (!this.isValidCollateralRate) {
      this.commonService.showSnackBar(WRONG_COLLATERAL_CONNECTION_RATE_VALUE_MESSAGE, CLOSE, null);
      return false;
    }
    const collateralList = this.getCollateralForm();
    const orderSet = new Set();
    for (const collateral of collateralList) {
      const connectionRate = collateral[ORDER];
      orderSet.add(connectionRate);
      if (connectionRate != null && this.commonService.formatNumberService(connectionRate) === 0) {
        this.commonService.showSnackBar(WRONG_COLLATERAL_ORDER_MESSAGE, CLOSE, null);
        return false;
      }
    }
    if (collateralList.length !== orderSet.size) {
      this.commonService.showSnackBar(DUPLICATED_COLLATERAL_ORDER_MESSAGE, CLOSE, null);
      return false;
    }
    return true;
  }

  private setCollateral(collaterals: any[], showMessage: boolean): void {
    if (collaterals != null && collaterals.length > 0) {
      const overlayRef = this.commonService.setOverlay();
      for (let i = 0; i < collaterals.length; i++) {
        const selectedCollateral = this.getCollateralById(collaterals[i].collateralId);
        if (null != selectedCollateral) {
            for (const prop in collaterals[i]) {
              if (collaterals[i].hasOwnProperty( prop )) {
                const updateField = selectedCollateral.find( field => field.id === prop );
                if (updateField != null) {
                  updateField.formFieldValue.defaultValue = collaterals[i][prop];
                  if (!this.enableCreateAccount) { updateField.disabled = true; }
                }
              }
            }
        }
      }
      if (showMessage) {
        this.checkCollateralConnectionRate(collaterals);
      }
      overlayRef.dispose();
    }
  }

  private getCollateralById(id: string) {
    if (this.collateralAssetForm[this.COLLATERAL_ASSET_INDEX] == null) { return null; }
    for (const collateral of this.collateralAssetForm[this.COLLATERAL_ASSET_INDEX]) {
      const filteredCol = collateral.find(col => col.id === COLLATERAL_ID);
      if (filteredCol.formFieldValue.defaultValue === id) { return collateral; }
    }
  }


  private checkCollateralConnectionRate(collateralList: any[]): void {
    let sum = 0;
    for (const collateral of collateralList) {
      const connectionRate = this.commonService.formatNumberService(collateral[COLLATERAL_CONNECTION_RATE]);
      sum = Number( (sum + connectionRate).toFixed(2) );
    }

    if (sum > 100) {
      this.isValidCollateralRate = false;
      this.commonService.showSnackBar(WRONG_COLLATERAL_CONNECTION_RATE_VALUE_MESSAGE, CLOSE, null);
    } else { this.isValidCollateralRate = true; }
  }

  private getCollateralForm() {
    if (null == this.collateralAssetForm[this.COLLATERAL_ASSET_INDEX]) {
      return [];
    }
    const collateralForm = [];
    for (const form of this.collateralAssetForm[this.COLLATERAL_ASSET_INDEX]) {
      const collateralField = {};
      for (const field of form) {
        if (field.id === 'loanAmount' || field.id === 'amountOfAssessment') {
          const value = this.commonService.formatNumberService(field.formFieldValue.defaultValue);
          collateralField[field.id] = value.toString();
        } else {
          collateralField[field.id] = field.formFieldValue.defaultValue;
        }
      }
      collateralForm.push(collateralField);
    }
    return collateralForm;
  }

  private getSubmitForm() {
    const copyForm = [...this.collateralAssetForm];
    copyForm.splice(this.COLLATERAL_ASSET_INDEX, 1);
    return copyForm;
  }

  private getCollateralMap() {
    if (null == this.collateralAssetForm[this.COLLATERAL_ASSET_INDEX]) {
      return [{}, []];
    }
    const colObj = {};
    const colIdArray = [];
    for (const collateral of this.collateralAssetForm[this.COLLATERAL_ASSET_INDEX]) {
      let collateralId = null;
      const collateralMap = {};
      collateral.forEach( field => {
        if (field.type === BIG_DECIMAL) {
          field.formFieldValue.defaultValue = this.commonService.formatNumberService(field.formFieldValue.defaultValue);
        }
        if (field.id === COLLATERAL_ID) {
          collateralId = field.formFieldValue.defaultValue;
        }
        collateralMap[field.id] = field.formFieldValue.defaultValue;
      } );
      if (collateralId) {
        colIdArray.push(collateralId);
        colObj[collateralId] = collateralMap;
      }
    }
    return [colObj, colIdArray];
  }



  private  getFormValue(form: FormsModel[], fieldId: string): any {
    const valueFromForm = form.find(field => field.id === fieldId);
    return valueFromForm.formFieldValue.defaultValue;
  }

  private setDefaultValue(form: FormsModel[], id: string) {
    const formField = form.find(field => field.id === id).formFieldValue;
    if (formField.defaultValue === '' || null == formField.defaultValue || formField.defaultValue === 'empty') {
      formField.defaultValue = 0;
    }
  }
}
