import {Component, OnInit} from '@angular/core';
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {ProcessTypeModel} from '../../models/process.model';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {FieldOptions, FormsModel} from '../../../models/app.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {
  BORROWER_TYPE,
  BRANCH_BANKING,
  BRANCHBANKING,
  LOAN,
  LOAN_CONTRACT,
  LOAN_PRODUCT,
  LOANCONTRACT,
  ORGANIZATION,
  ORGANIZATION_BORROWER,
  ORGANIZATION_REQUEST,
  PRODUCT_BORROWER_TYPE_MAP,
  PRODUCT_CATEGORY_MAP,
} from '../../models/process-constants.model';
import {CLOSE, ORGANIZATION_ALREADY_EXIST} from '../../../case-view-page/model/task-constant';
import {Router} from '@angular/router';
import {MICRO_LOAN} from '../../../case-view-page/model/case-folder-constants';
import {CONFIRM_DIALOG_BUTTONS} from '../../../branch-banking/model/branch-banking-constant';
import {ErinCustomDialogComponent} from '../../../common/erin-custom-dialog/erin-custom-dialog.component';

const ACCOUNT_NUMBER_FIELD_CONSTANT: FormsModel = {
    id: 'accountNumber', formFieldValue: {valueInfo: null, defaultValue: null}, options: [],
    label: 'Зээлийн данс', validations: [{name: 'required', configuration: ''}, {name: 'maxlength', configuration: '11'}], type: '', required: true
  }
;

const ORGANIZATION_REGISTER_NUMBER_FIELD_CONSTANT: FormsModel = {
    id: 'registrationNumber', formFieldValue: {valueInfo: null, defaultValue: null}, options: [],
    label: 'Регистрийн дугаар', validations: [{name: 'required', configuration: ''}], type: '', required: true
  }
;

@Component({
  selector: 'process-types',
  template: `
    <div class="dialog-container">
      <div>
        <h1 mat-dialog-title #dialogTitle>{{dialogName}}</h1>
      </div>
      <div *ngIf="loanContractRequestForm != null">
        <dynamic-fields [forms]="loanContractRequestForm"></dynamic-fields>
      </div>
      <div *ngIf="organizationRequestForm != null">
        <dynamic-fields [forms]="organizationRequestForm"></dynamic-fields>
      </div>
      <div mat-dialog-content id="dialog-content" *ngIf="getType() !== loanProductType && getType() !== organizationType">
        <div id="requestType"><p>{{getType()}}</p></div>
        <simple-dropdown [items]="processTypes" (selectionChange)="changedType($event)"></simple-dropdown>
      </div>
      <div *ngIf="!selectedProcessType && isLoanRequest()" class="emptyMessage" id="emptyMassage">
        <span>{{getType().toUpperCase()}} СОНГООГҮЙ БАЙНА!</span>
      </div>
      <form (ngSubmit)="save()">
        <div *ngIf="selectedProcessType && getType() !== loanProductType && getType() !== organizationType">
          <dynamic-fields [forms]="forms" (inputCheck)="checkValidation($event)" [isOrganization]="isOrganization"
                          (keypress)="validateRegisterNumber()" (change)="validateRegisterNumber()"
                          (changeDetector)="setProductOptionByBorrowerType()"></dynamic-fields>
        </div>
        <mat-error *ngIf="fieldError">{{fieldError}}</mat-error>
        <div class="workspace-actions">
          <!--          <span class="spacer"></span>-->
          <button *ngIf="isLoanRequest()" id="saveButton" mat-flat-button color="primary" (click)="save()" type="button"
                  [disabled]="loading || isFieldValueWrong">ХАДГАЛАХ
          </button>
          <!--          <span class="margin-left"></span>-->
          <button id="continueButton" mat-flat-button color="primary" (click)="continue()" type="button" [disabled]="loading || isFieldValueWrong">
            ҮРГЭЛЖЛҮҮЛЭХ
          </button>
          <!--          <span class="margin-left"></span>-->
          <button id="closeButton" mat-stroked-button color="primary" (click)="close()" type="button">БОЛИХ</button>
        </div>
      </form>
    </div>
  `,
  styleUrls: ['./create-request-dialog.component.scss']
})

export class CreateRequestDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<CreateRequestDialogComponent>, private sb: LoanSearchPageSandbox,
              private commonSb: CommonSandboxService, private snackBar: MatSnackBar, private router: Router,
              private dialog: MatDialog) {
    this.currentUrl = this.router.url;
  }

  isFieldValueWrong: boolean;
  requestType = 'Хүсэлтийн төрөл';
  serviceType = 'Үйлчилгээний төрөл';
  loanProductType = 'Зээлийн бүтээгдэхүүний төрөл';
  organizationType = 'Бүртгэлийн төрөл';
  dialogName = 'Хүсэлт үүсгэх';
  processTypes: ProcessTypeModel[];
  errorMessage: string;
  selectedProcessType: ProcessTypeModel;
  forms: FormsModel[] = [];
  selectedBorrowerType: string;
  fieldError: string;
  loading: boolean;
  isOrganization = false;
  currentUrl: string;
  loanContractRequestForm: FormsModel[] = null;
  organizationRequestForm: FormsModel[] = null;

  private static getValidation(forms: FormsModel[]): string {
    for (const field of forms) {
      if (field.validations.length > 0) {
        if ((field.formFieldValue.defaultValue === null || field.formFieldValue.defaultValue === '' ||
          field.formFieldValue.defaultValue === '0' || this.checkBigDecimalNumber(field, 'BigDecimal')) && field.validations[0].name === 'required') {
          return field.label;
        }
      }
    }
  }

  private static checkBigDecimalNumber(field: FormsModel, type: string): boolean {
    if (field.type === type) {
      if (Number(field.formFieldValue.defaultValue.replace(/[^\d.-]/g, '')) === 0) {
        return true;
      }
    }
  }

  ngOnInit(): void {
    const overlayRef = this.commonSb.setOverlay();
    this.sb.getProcessTypes(this.getRequestCategory()).subscribe(res => {
      overlayRef.dispose();
      if (res) {
        this.processTypes = res;
        if (this.getRequestCategory() !== null) {
          this.dialogTypeChanger();
        }
      }
    }, error => {
      overlayRef.dispose();
      this.errorMessage = error;
      this.dialogRef.close();
    });
  }

  private dialogTypeChanger() {
    if (this.getRequestCategory() === LOANCONTRACT) {
      const options: FieldOptions[] = [];
      this.processTypes.forEach(process => options.push({id: process.id, value: process.name}));
      this.loanContractRequestForm = [
        {
          id: 'processType', formFieldValue: {valueInfo: null, defaultValue: null}, options, label: this.getType(),
          validations: [{name: 'required', configuration: ''}], type: '', required: true
        },
        this.commonSb.clone(ACCOUNT_NUMBER_FIELD_CONSTANT)
      ];
    } else if (this.getRequestCategory() === ORGANIZATION) {
      const options: FieldOptions[] = [];
      this.processTypes.forEach(process => options.push({id: process.id, value: process.name}));
      if (this.currentUrl.includes('salary')) {
        this.organizationRequestForm = [
          {
            id: 'processType', formFieldValue: {valueInfo: null, defaultValue: 'Цалингийн байгууллага'}, options, label: this.getType(),
            validations: [{name: 'required', configuration: ''}], type: '', required: true
          },
          this.commonSb.clone(ORGANIZATION_REGISTER_NUMBER_FIELD_CONSTANT)
        ];
      } else if (this.currentUrl.includes('leasing-organization')) {
        this.organizationRequestForm = [
          {
            id: 'processType', formFieldValue: {valueInfo: null, defaultValue: 'Лизингийн байгууллага'}, options, label: this.getType(),
            validations: [{name: 'required', configuration: ''}], type: '', required: true
          },
          this.commonSb.clone(ORGANIZATION_REGISTER_NUMBER_FIELD_CONSTANT)
        ];
      }
    } else {
      this.organizationRequestForm = null;
      this.loanContractRequestForm = null;
    }
  }

  getRequestCategory(): string {
    switch (true) {
      case this.currentUrl.includes(ORGANIZATION_REQUEST):
        return ORGANIZATION;
      case this.currentUrl.includes(BRANCH_BANKING):
        return BRANCHBANKING;
      case this.currentUrl.includes(LOAN_CONTRACT):
        return LOANCONTRACT;
      default:
        return LOAN;
    }
  }

  isLoanRequest(): boolean {
    return this.getRequestCategory() === 'LOAN';
  }

  getType(): string {
    if (this.currentUrl.includes(BRANCH_BANKING)) {
      return this.serviceType;
    } else if (this.currentUrl.includes(LOAN_CONTRACT)) {
      return this.loanProductType;
    } else if (this.currentUrl.includes(ORGANIZATION_REQUEST)) {
      return this.organizationType;
    } else {
      return this.requestType;
    }
  }

  changedType(selected: ProcessTypeModel): void {
    this.selectedProcessType = selected;
    this.fieldError = undefined;
    const overlayRef = this.commonSb.setOverlay();
    this.sb.getRequestForm(selected.id).subscribe(res => {
      this.forms = res;
      overlayRef.dispose();
      this.sortLoanProductOption(this.forms);
    }, error => {
      overlayRef.dispose();
      this.fieldError = error.error.message;
    });
  }


  continue(): void {
    if (this.checkError()) {
      return;
    }
    sessionStorage.clear();
    this.errorMessage = undefined;
    if (this.currentUrl.includes(ORGANIZATION_REQUEST)) {
      if (this.organizationRequestForm != null) {
        const processTypeField = this.organizationRequestForm.find(field => field.id === 'processType');
        const selectedType = processTypeField.formFieldValue.defaultValue;
        const selectedTypeId = processTypeField.options.find(op => op.value === selectedType).id;
        this.selectedProcessType = this.processTypes.find(process => process.id === selectedTypeId);
        this.forms = [this.organizationRequestForm.find(field => field.id === 'registrationNumber')];
      }
    } else {
      if (this.loanContractRequestForm != null) {
        const processTypeField = this.loanContractRequestForm.find(field => field.id === 'processType');
        const selectedType = processTypeField.formFieldValue.defaultValue;
        const selectedTypeId = processTypeField.options.find(op => op.value === selectedType).id;
        this.selectedProcessType = this.processTypes.find(process => process.id === selectedTypeId);
        this.forms = [this.loanContractRequestForm.find(field => field.id === 'accountNumber')];
      }
    }

    if (!this.selectedProcessType) {
      this.commonSb.showSnackBar(this.getType() + '  СОНГОНО УУ!', 'ХААХ', 3000);
      return;
    }

    if (this.getRequestCategory() === LOANCONTRACT || this.getRequestCategory() === ORGANIZATION || this.isValidAmount()) {
      const overlayRef = this.commonSb.setOverlay();
      if (this.getRequestCategory() === ORGANIZATION) {
        this.checkIsAlreadyRegistered(this.selectedProcessType.id, overlayRef);
      } else {
      this.setProductId();
      this.sb.createRequest(this.selectedProcessType.id, this.forms, this.getRequestCategory()).subscribe((res) => {
        if (this.getRequestCategory() === BRANCHBANKING) {
          const createdUser = this.sb.getUserName();
          this.sb.startBranchBankingProcess(res, {processCategory: BRANCHBANKING, processType: this.selectedProcessType.id}, createdUser)
            .subscribe(() => this.closeDialog(overlayRef), error => this.errorResponseHandler(overlayRef, error));
        } else if (this.getRequestCategory() === LOANCONTRACT) {
          this.closeDialog(overlayRef);
          this.sb.routeToContractCaseView(res, this.getRequestCategory(), 'group-request');
        } else {
          this.sb.startProcess(res, false).subscribe(() => this.closeDialog(overlayRef), error => this.errorResponseHandler(overlayRef, error));
        }
      }, error => {
        this.commonSb.showSnackBar(this.errorMessage = error.error.message, CLOSE, 3000);
        overlayRef.dispose();
        this.showErrorSnackBar(error);
        });
      }
    } else {
      this.commonSb.showSnackBar(this.errorMessage = 'Зээлийн хэмжээ 0 илүү оруулна уу.', CLOSE, 3000);
    }
  }

  private startOrganizationRequest(overlayRef, overlayRef1) {
    this.sb.createRequest(this.selectedProcessType.id, this.forms, this.getRequestCategory()).subscribe((res) => {
        this.routeToOrganizationCaseView(res, overlayRef1);
    }, error => {
      this.commonSb.showSnackBar(this.errorMessage = error.error.message, CLOSE, 3000);
      overlayRef1.dispose();
      this.showErrorSnackBar(error);
    });
    overlayRef.dispose();
  }

  private routeToOrganizationCaseView(res, overlayRef) {
    if (this.selectedProcessType.id === 'salaryOrganization') {
      this.sb.routeToSalaryOrganizationCaseView(res, null, 'salary-organization');
    } else {
      this.sb.routeToLeasingOrganizationCaseView(res, null, 'leasing-organization');
    }
    overlayRef.dispose();
  }

  private checkIsAlreadyRegistered(processType: string, overlayRef) {
    const config = this.setDialogConfig(CONFIRM_DIALOG_BUTTONS, ORGANIZATION_ALREADY_EXIST, '400px');
    const orgRegNumber = this.organizationRequestForm.find(field => field.id === 'registrationNumber');
    this.closeDialog(overlayRef);
    this.sb.checkIsOrganizationRegistered(orgRegNumber.formFieldValue.defaultValue, processType)
      .subscribe((res) => {
        if (res.length > 0) {
          this.openCustomDialog(ErinCustomDialogComponent, config).then((buttonRes: any) => {
            if (null != buttonRes && null != buttonRes.action && buttonRes.action.actionId === 'confirm') {
              const overlayRef1 = this.commonSb.setOverlay();
              this.startOrganizationRequest(overlayRef, overlayRef1);
            }
          });
        } else {
          const overlayRef1 = this.commonSb.setOverlay();
          this.startOrganizationRequest(overlayRef, overlayRef1);
        }
      }, () => {
        overlayRef.dispose();
      }
    );
  }

  save(): void {
    if (this.checkError()) {
      return;
    }
    sessionStorage.clear();
    this.errorMessage = undefined;
    if (this.selectedProcessType && this.isValidAmount()) {
      const overlayRef = this.commonSb.setOverlay();
      this.setProductId();
      this.sb.createRequest(this.selectedProcessType.id, this.forms, this.getRequestCategory()).subscribe(() => {
        overlayRef.dispose();
        this.dialogRef.close('success');
      }, error => {
        overlayRef.dispose();
        this.loading = false;
        this.commonSb.showSnackBar(this.errorMessage = error.error.message, CLOSE);
      });
    }
  }

  close(): void {
    this.dialogRef.close();
  }

  checkValidation(value: boolean): void {
    this.isFieldValueWrong = value;
  }

  validateRegisterNumber(): void {
    const borrowerType = this.commonSb.getFormValue(this.forms, BORROWER_TYPE);
    if (this.getRequestCategory() === ORGANIZATION ||
      (this.selectedProcessType.id === MICRO_LOAN && borrowerType === ORGANIZATION_BORROWER)) {
      this.isOrganization = true;
    }
    else {
       this.isOrganization = false;
    }
  }

  setProductOptionByBorrowerType(): void {
    const borrowerTypeId = this.getOptionId(BORROWER_TYPE);
    const borrowerType = borrowerTypeId != null ? PRODUCT_BORROWER_TYPE_MAP.get(borrowerTypeId) : null;
    if (this.selectedBorrowerType !== borrowerType) {
      const appCategory = PRODUCT_CATEGORY_MAP.get(this.selectedProcessType.id);
      if (null != appCategory) {
        const options: FieldOptions[] = [];
        const loanProduct = this.forms.find(field => field.id === LOAN_PRODUCT);
        this.sb.getProductsByAppCategoryAndBorrowerType(appCategory, borrowerType).subscribe(products => {
          loanProduct.formFieldValue.defaultValue = null;
          this.selectedBorrowerType = borrowerType;
          for (const product of products) {
            options.push({id: product.id, value: product.productDescription});
          }
          if (options.length > 0) {
            loanProduct.options = options;
          }
        });
      }
    }
  }

  private setProductId() {
    for (const field of this.forms) {
      if (field.id === LOAN_PRODUCT) {
        const product = field.options.find(option => option.value === field.formFieldValue.defaultValue);
        field.formFieldValue.defaultValue = product.id;
      }
    }
  }

  private isValidAmount() {
    if (this.forms != null) {
      const amountValue = this.commonSb.getFormValue(this.forms, 'amount');
      return amountValue !== '0';
    }
  }

  private checkError(): boolean {
    const errorText = CreateRequestDialogComponent.getValidation(this.forms);
    if (errorText) {
      this.snackBar.open(errorText + ' талбарыг бөглөнө үү!', CLOSE, {duration: 3000});
      return true;
    }
    return false;
  }

  private sortLoanProductOption(form: FormsModel[]) {
    const loanProduct: FormsModel = form.find(field => field.id === LOAN_PRODUCT);
    if (null != loanProduct) {
      loanProduct.options.sort((a, b) => {
        if (a.value > b.value) {
          return 1;
        }
        if (a.value < b.value) {
          return -1;
        }
        return 0;
      });
    }
  }

  private getOptionId(fieldId: string): string {
    const field = this.forms.find(f => f.id === fieldId);
    if (field != null) {
      for (const option of field.options) {
        if (option.value === field.formFieldValue.defaultValue) {
          return option.id;
        }
      }
    }
    return null;
  }

  private errorResponseHandler(overlayRef, error): void {
    this.closeDialog(overlayRef);
    this.showErrorSnackBar(error);
  }

  private closeDialog(overlayRef): void {
    overlayRef.dispose();
    this.dialogRef.close();
  }

  private showErrorSnackBar(error): void {
    this.commonSb.showSnackBar(this.errorMessage = error.error.message, CLOSE, 30000);
    this.loading = false;
  }

  private setDialogConfig(buttons, message, width): MatDialogConfig {
    const dialogData = {buttons, message};
    const config = new MatDialogConfig();
    config.width = width;
    config.data = dialogData;
    return config;
  }

  openCustomDialog(classRef, config) {
    return new Promise(resolve => {
        const dialogRef = this.dialog.open(classRef, config);
        dialogRef.afterClosed().subscribe((res) => {
          resolve(res);
        });
      }
    );
  }
}
