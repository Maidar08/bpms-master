import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {COLLATERAL_CONNECTION_RATE, FORM_FIELD_HINT} from '../../model/task-constant';
import {FORM_FIELDS_MAX_LENGTH, MORTGAGE_LOAN_MAX_LENGTH} from '../../model/form-field-max-length-constant';
import {MORTGAGE_LOAN} from '../../model/case-folder-constants';

@Component({
  selector: 'dynamic-fields',
  template: `
    <div class="form-field-container" [ngClass]="getCustomColumnStyle(numberOfColumns)">
      <div class="form-fields" *ngFor="let field of forms" [ngStyle]="getColumnStyle(field)">
        <!-- empty field is here-->
        <ng-container *ngIf="field.id.includes('dummy')">
          <div>
          </div>
        </ng-container>
        <!-- regular string input field is here-->
        <ng-container *ngIf="isRegularInput(field) && !field.id.includes('dummy')">
          <erin-simple-input-field [field]="field" [disabled]="inputState"
                                 [fieldHint] ="getFieldHint(field)" (changeEmitter)="detectChange($event)"></erin-simple-input-field>
        </ng-container>
        <!-- number input field is here-->
        <ng-container *ngIf="useSeparator(field)">
          <erin-number-input-field [field]="field" [disabled]="inputState" (changeEmitter)="detectChange($event)"
          [isRoundup]="isRoundup"></erin-number-input-field>
        </ng-container>
        <!-- register number input field is here-->
        <ng-container *ngIf="isRegisterField(field)">
          <erin-register-number-input-field [field]="field" [disabled]="inputState"
                                            [currentTaskName]="currentTaskName" [borrowerTypeCoBorrower]="borrowerTypeCoBorrower"
                                            (registerValidation)="validateInput($event)"
                                            [isOrganizationRegister]="isOrganization"></erin-register-number-input-field>
        </ng-container>
        <!-- phone number input field is here-->
        <ng-container *ngIf=isSimplePhoneNumberInput(field)>
          <erin-phone-number-input-field [field]="field" [disabled]="inputState"></erin-phone-number-input-field>
        </ng-container>
        <!-- email input field is here-->
        <!--        <ng-container *ngIf="field.id === 'email' || field.id === 'emailCoBorrower'">-->
        <ng-container *ngIf="null != field && null != field.id && field.id.includes('email')">
          <erin-email-field [field]="field" [disabled]="inputState"
                            (emailValidation)="validateInput($event)"></erin-email-field>
        </ng-container>
        <!-- organization_cif input field is here-->
        <ng-container *ngIf="field.id === 'organization_cif'">
          <erin-workerCIF-field [field]="field" [disabled]="inputState"></erin-workerCIF-field>
        </ng-container>
        <!-- Loop for 5 currentAccount input field is here-->
        <div *ngFor="let item of arrayTimes(5); let i = index;">
          <ng-container *ngIf="isSimpleNumberInputWithLoop(field, i)">
            <erin-string-but-number-field [field]="field" [disabled]="inputState" (changeEmitter)="detectChange($event)"
                                          [fieldHint] ="getFieldHint(field)" ></erin-string-but-number-field>
          </ng-container>
        </div>
        <!--  dropdown field is here-->
        <ng-container *ngIf="null != field.options && field.options.length>0 && udfFieldsExclude(field)">
          <erin-dropdown-field [field]="field" (changeEmitter)="detectChange($event)"
                               [disabled]="inputState"></erin-dropdown-field>
        </ng-container>
        <!--  dropdown field with checkbox is here-->
        <ng-container *ngIf="null != field.optionsCheckbox && field.optionsCheckbox.length>0 && udfFieldsExclude(field)">
          <erin-dropdown-field-with-checkbox [field]="field" (changeEmitter)="detectChange($event)"
                               [disabled]="inputState"></erin-dropdown-field-with-checkbox>
        </ng-container>
        <!--  autocomplete field is here-->
        <ng-container *ngIf="null != field.options && field.options.length>0 && (udfFieldsInclude(field))">
          <erin-autocomplete-field [field]="field"></erin-autocomplete-field>
        </ng-container>
        <!-- date input field is here-->
        <ng-container *ngIf="field.type == 'date' || field.type=='D'">
          <erin-datepicker-field [field]="field" [disabled]="inputState" (changeEmitter)="detectChange($event)" ></erin-datepicker-field>
        </ng-container>
        <!-- scan fingerprint field is here-->
<!--        <ng-container *ngIf=" field.type == 'fingerprint'">-->
<!--          <div>-->
<!--            <scan-fingerprint-field [field]="field"></scan-fingerprint-field>-->
<!--          </div>-->
<!--        </ng-container>-->
        <!-- textArea field is here-->
        <ng-container *ngIf="field.type == 'textArea'">
          <erin-textarea-input-field [field]="field"></erin-textarea-input-field>
        </ng-container>
        <!-- number field with percent symbol is here-->
        <ng-container *ngIf="isPercentField(field)">
          <erin-percent-field [field]="field" [disabled]="inputState" (changeEmitter)="detectChange($event)"
                              [inputMaxLength]="getFieldMaxLength(field)"></erin-percent-field>
        </ng-container>
        <!-- only year field is here -->
        <ng-container *ngIf="field.id ==='ASHIGLALTAND ORSON ON' || field.id ==='GERCHILGEENII DUUSAH ON'">
          <erin-year-field [field]="field" [disabled]="inputState"
                           (validateInput)="validateYearInput($event)"></erin-year-field>
        </ng-container>
        <!-- simple  number input without loop -->
        <ng-container *ngIf="isSimpleNumberInput(field)">
          <erin-string-but-number-field [field]="field" [disabled]="inputState" [inputMaxLength]="getFieldMaxLength(field)"
                                        (changeEmitter)="detectChange($event)" [fieldHint] ="getFieldHint(field)"></erin-string-but-number-field>
        </ng-container>

        <ng-container *ngIf = "field.type == 'checkbox'">
          <erin-checkbox-field [field]="field" [disabled]="field.disabled" (changeEmitter)="detectChange($event)"></erin-checkbox-field>
        </ng-container>

        <ng-container *ngIf="field.type == 'button'">
          <erin-button [field]="field" [disabled]="field.disabled" (actionEmitter)="handleAction($event)"></erin-button>
        </ng-container>

        <ng-container *ngIf="field.type == 'uploadFileButton'">
          <erin-uploadFile-button [field]="field" [disabled]="field.disabled" (actionEmitter)="handleAction($event)"></erin-uploadFile-button>
        </ng-container>

        <ng-container *ngIf="field.type == 'chipField'">
          <erin-chips [field]="field" [disabled]="field.disabled" (actionEmitter)="handleAction($event)"></erin-chips>
        </ng-container>
      </div>

      <div class="form-fields" *ngFor="let field of textareaForms">
        <!-- textArea field is here-->
        <ng-container *ngIf="field.type == 'textArea'">
          <erin-textarea-input-field [field]="field"></erin-textarea-input-field>
        </ng-container>
      </div>

    </div>    `,
  styleUrls: ['./dynamic-fields.component.scss'],
})
export class DynamicFieldsComponent {
  @Input() forms: FormsModel[];
  @Input() textareaForms: FormsModel[];
  @Input() inputState;
  @Input() isOrganization;
  @Input() loanType;
  @Input() numberOfColumns: number;
  @Input() currentTaskName: string;
  @Input() isRoundup = true;
  @Input() borrowerTypeCoBorrower;
  @Input() set setBorrowerTypeCoBorrower(value) {
    this.borrowerTypeCoBorrower = value;
  }
  @Output() changeDetector = new EventEmitter<string>();
  @Output() inputCheck = new EventEmitter<boolean>();
  @Output() actionEmitter = new EventEmitter<any>();

  private static isAccountField(field: FormsModel): boolean {
    return field.id.toLocaleLowerCase().includes('accountnumber') && field.options.length === 0;
  }

  useSeparator(field: FormsModel): boolean {
    if (field == null) {
      return false;
    }
    return (field.type === 'BigDecimal' || field.type === 'N')
      && field.options.length === 0 && field.id !== 'deductionRate' && field.id !== COLLATERAL_CONNECTION_RATE
      && field.id  !== 'accountId' && !DynamicFieldsComponent.isAccountField(field) && field.id !==  'attachmentNumber'
      && field.id !== 'customerId' && field.id !== 'phoneNumber' && field.id !== 'coreNumber' && field.id !== 'cifNumber'
      && field.id !== 'accountIdEnter' && field.id !== 'ussdPhoneNumber' && field.id  !== 'ussdSearchPhoneNumber'
      && field.id !=='salaryCalculationCount' && field.id !=='mortgageCalculationCount';   }

  isRegularInput(field: FormsModel): boolean {
    if (field == null || field.options == null || field.id == null || field.optionsCheckbox) {
      return false;
    }
    return field.type !== 'date' && field.type !== 'D' && (field.options.length === 0) && field.type !== 'BigDecimal' && field.type !== 'N' &&
      field.type !== 'fingerprint' && field.type !== 'textArea' && field.type !== 'checkbox' && field.type !== 'uploadFileButton' &&
      field.type !== 'chipField' && field.id !== 'phoneNumber' && field.id !== 'email' &&
      field.id !== 'emailCoBorrower' && field.id !== 'phoneNumberCoBorrower' && field.id !== 'organization_cif' &&
      field.id !== 'registerNumber' && field.id !== 'employeeRegisterNumber' && field.id !== 'borrowersIncomePercent'
      && field.id !== 'coBorrowersIncomePercent' && field.id !== 'transferIncomePercent' && field.id !== 'netIncomePercent'
      && field.id !== 'borrowerFinancesPercent' && field.id !== 'borrowerFinanceGaragePercent'
      && field.id !== this.isLoopInput(field.id) && !field.id.includes('email') && field.id !== 'deductionRate' && field.type !== 'button'
      && field.id !== 'registerNumberCoBorrower' && field.id !== 'spaceNull' && field.type !== 'checkbox' && field.id !== 'accountId'
      && field.id !=='reCalculated' && field.id !== 'contactPhoneNumber' && !DynamicFieldsComponent.isAccountField(field);
  }

  isRegisterField(field: FormsModel): boolean {
    return field.id === 'registerNumber' || field.id === 'employeeRegisterNumber' || field.id === 'registerNumberCoBorrower';
  }

  getColumnStyle(form: FormsModel) {
    if (this.forms.length === 1 && form.columnIndex != null) {
      return {'grid-column-start': form.columnIndex};
    }
  }

  isLoopInput(field): string {
    const fieldWithoutNumber = field.replace(/\d/g, '');
    if (fieldWithoutNumber === 'currentAccount' || fieldWithoutNumber === 'savingAccount' ||
      fieldWithoutNumber === 'cardAccount' || fieldWithoutNumber === 'otherCurrentAccount' ||
      fieldWithoutNumber === 'otherSavingAccount' || fieldWithoutNumber === 'otherCardAccount') {
      return field;
    }
  }

  arrayTimes(n: number): any[] {
    return Array(n);
  }

  udfFieldsExclude(field) {
    return field.id !== 'loanPurpose' && field.id !== 'businessTypeReason' && field.id !== 'worker' && field.id !== 'subType'
      && field.id !== 'insuranceCompanyInfo' && field.id !== 'supplier1' && field.id !== 'supplier2' && field.id !== 'supplier3'
      && field.id !== 'loanCycle' && field.id !== 'controlOfficer' && field.id !== 'lateReason' && field.id !== 'attentiveLoan'
      && field.id !== 'lateReasonAttention' && field.id !== 'nonRegulatoryLoanTerms' && field.id !== 'restructuredNumber'
      && field.id !== 'firstAccountNumber' && field.id !== 'typeOfFee' && field.id !== 'amountOfFee' &&
      field.id !== 'schoolNameAndInstitution' && field.id !== 'currentHeater'

      && field.id !== 'AccountFreeCode2' && field.id !== 'AccountFreeCode3' && field.id !== 'TypeOfAdvance' && field.id !== 'BorrowerCategoryCode'
      && field.id !== 'FREE_CODE_4' && field.id !== 'FREE_CODE_5' && field.id !== 'FREE_CODE_6'
      && field.id !== 'FREE_CODE_7' && field.id !== 'FREE_CODE_8' && field.id !== 'FREE_CODE_9' &&
      field.id !== 'FREE_CODE_10' && field.id !== 'NatureOfAdvance'
      && field.id !== 'CustomerIndustryType' && field.id !== 'PurposeOfAdvance' && field.id !== 'AccountFreeCode1';
  }

  udfFieldsInclude(field) {
    return field.id === 'loanPurpose' || field.id === 'businessTypeReason' || field.id === 'worker' || field.id === 'subType' ||
      field.id === 'insuranceCompanyInfo' || field.id === 'supplier1' || field.id === 'supplier2' || field.id === 'supplier3' ||
      field.id === 'loanCycle' || field.id === 'controlOfficer' || field.id === 'lateReason' || field.id === 'attentiveLoan' ||
      field.id === 'lateReasonAttention' || field.id === 'nonRegulatoryLoanTerms' || field.id === 'restructuredNumber' ||
      field.id === 'firstAccountNumber' || field.id === 'typeOfFee' || field.id === 'amountOfFee' ||
      field.id === 'schoolNameAndInstitution' || field.id === 'currentHeater'

      || field.id === 'AccountFreeCode2' || field.id === 'AccountFreeCode3' || field.id === 'TypeOfAdvance' || field.id === 'BorrowerCategoryCode' ||
      field.id === 'FREE_CODE_4' || field.id === 'FREE_CODE_5' || field.id === 'FREE_CODE_6' ||
      field.id === 'FREE_CODE_7' || field.id === 'FREE_CODE_8' || field.id === 'FREE_CODE_9' ||
      field.id === 'FREE_CODE_10' || field.id === 'NatureOfAdvance'
      || field.id === 'CustomerIndustryType' || field.id === 'PurposeOfAdvance' || field.id === 'AccountFreeCode1';
  }

  getCustomColumnStyle(numberOfColumns): string {
    switch (numberOfColumns) {
      case 1:
        return 'column-1-container';
      case 2:
        return 'column-2-container';
      case 4:
        return 'column-4-container';
      case 5:
        return 'column-5-container';
      default:
        return 'form-field-container';
    }
  }

  getFieldMaxLength(field): number {
    const maxLength = this.getMaxLengthConstraint(field);
    if (maxLength != null) {
      return maxLength - 1;
    }
    let input = FORM_FIELDS_MAX_LENGTH.find(inputField => inputField.id === field.id);
    if (this.loanType === MORTGAGE_LOAN) {
      input = MORTGAGE_LOAN_MAX_LENGTH.find(inputField => inputField.id === field.id);
    }
    if (input) {
      return input.maxLength;
    }
  }

  getMaxLengthConstraint(field: FormsModel) {
    const  validations = field.validations;
    if (validations == null) {
      return null;
    }
    const maxLengthValidation = validations.find(val => val.name === 'maxlength');
    return maxLengthValidation == null ? null : maxLengthValidation.configuration;
  }

  getFieldMaxValue(field): number {
    const input = MORTGAGE_LOAN_MAX_LENGTH.find(inputField => inputField.id === field.id);
    if (input) {
      return input.maxValue;
    }
  }

  detectChange(field: FormsModel) {
    this.changeDetector.emit(field.id);
  }

  validateInput(value: boolean) {
    this.inputCheck.emit(value);
  }

  isSimpleNumberInputWithLoop(field: FormsModel, i: number): boolean {
    return field.id === 'currentAccount' + i || field.id === 'savingAccount' + i || field.id === 'cardAccount' + i ||
      field.id === 'otherCurrentAccount' + i || field.id === 'otherSavingAccount' + i || field.id === 'otherCardAccount' + i;
  }

  isSimplePhoneNumberInput(field: FormsModel) {
    return (field.id === 'phoneNumber' || field.id === 'phoneNumberCoBorrower' || field.id === 'contactPhoneNumber');
  }

  isPercentField(field: FormsModel): boolean {
    return field.id === 'deductionRate' || field.id === 'DUUSAAGUI BARILGIIN GUITSETGEL HUVI' || field.id === COLLATERAL_CONNECTION_RATE
      || field.id === 'borrowersIncomePercent' || field.id === 'coBorrowersIncomePercent' || field.id === 'transferIncomePercent'
      || field.id === 'netIncomePercent' || field.id === 'borrowerFinancesPercent' || field.id === 'borrowerFinanceGaragePercent';
  }

  isSimpleNumberInput(field: FormsModel) {
    return (field.type === 'N' || field.type === 'BigDecimal') && (field.id === 'accountNumber' || field.id === 'cifNumber'
      || field.id === 'accountId' || field.id === 'customerId' || field.id === 'attachmentNumber' || field.id === 'accountIdEnter'
      || field.id === 'ussdPhoneNumber' ||  field.id === 'ussdSearchPhoneNumber') || DynamicFieldsComponent.isAccountField(field);
  }

  validateYearInput(value: boolean) {
    this.inputCheck.emit(value);
  }

  getFieldHint(field) {
    const input = FORM_FIELD_HINT.find(inputField => inputField.id === field.id);
    if (input) {
      return input.fieldHint;
    }
  }

  handleAction(value: string) {
    this.actionEmitter.emit(value);
  }
}
