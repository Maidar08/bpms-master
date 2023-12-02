import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {BANK_LIST} from "../../model/task-constant";

@Component({
  selector: 'loan-contract',
  template: `
    <mat-card class="card-container">
      <div class="title">
        <hr class="line">
        <span class="title-text">{{title}}</span>
        <hr class="line">
      </div>
      <ng-container>
        <dynamic-fields [forms]="forms[0]"></dynamic-fields>
      </ng-container>
      <ng-container class="contracts">
        <hr class="contract-line">
        <div class="column-container">
          <dynamic-fields [forms]="forms[1]"></dynamic-fields>
        </div>
        <div class="padding_right">
          <dynamic-fields class="form-fields" [forms]="forms[2]"></dynamic-fields>
        </div>
        <div class="padding_right">
          <dynamic-fields [forms]="forms[3]"></dynamic-fields>
        </div>
      </ng-container>
      <ng-container class="bank-account">
        <div class="sub-title"><span class="text">Хасбанкны данс</span>
          <hr class="sub-line">
          <span class="material-icons-outlined add-btn" *ngIf="!disableState" (click)="increaseNumber(1)">add_box</span>
          <span class="material-icons-outlined remove-btn" *ngIf="!disableState" (click)="decreaseNumber(1)">indeterminate_check_box</span>
        </div>
        <div *ngFor="let form of accountForm index as i">
          <dynamic-fields [forms]="form"></dynamic-fields>
        </div>
      </ng-container>
      <ng-container class="other-account">
        <div class="sub-title"><span class="text">Бусад банкны данс</span>
          <hr class="sub-line">
          <span class="material-icons-outlined add-btn" *ngIf="!disableState" (click)="increaseNumber(0)">add_box</span>
          <span
            class="material-icons-outlined remove-btn" *ngIf="!disableState"
            (click)="decreaseNumber(0)">indeterminate_check_box</span></div>
        <div *ngFor="let form of otherAccountForm index as i">
          <dynamic-fields class="other-bank" [forms]="form"></dynamic-fields>
        </div>
      </ng-container>
      <div class="loan-info">
        <div class="sub-title"><span class="text">Зээлийн мэдээллийн хуудас</span>
          <hr class="sub-line">
        </div>
        <dynamic-fields [forms]="forms[4]"></dynamic-fields>
      </div>
      <div class="button-container">
        <button mat-flat-button color="primary" (click)="submitContract()" [disabled]="disableState">ДУУСГАХ</button>
        <button mat-flat-button color="primary" (click)="saveContract()" [disabled]="disableState">ХАДГАЛАХ</button>
        <button mat-flat-button color="primary" (click)="printContract()" [disabled]="disableState">ХЭВЛЭХ</button>
      </div>
    </mat-card>    `,
  styleUrls: ['./loan-contract.component.scss']
})
export class LoanContractComponent implements OnInit, OnChanges {
  @Input() title: string;
  @Input() data: FormsModel[];
  @Input() disableState = false;
  @Output() printEmitter = new EventEmitter<FormsModel[]>();
  @Output() saveEmitter = new EventEmitter<FormsModel[]>();
  @Output() submitEmitter = new EventEmitter<FormsModel[]>();
  forms = [];
  accountForm = [];
  otherAccountForm = [];
  constructor() {}

  ngOnChanges(changes: SimpleChanges): void { this.ngOnInit(); /* It's called when switch between active and completed task */ }

  ngOnInit(): void {
    this.forms = [];
    this.accountForm = [];
    this.otherAccountForm = [];
    if (this.data) {
      const hashMap = new Map();
      for (const form of this.data) {
        hashMap.set(form.id, form);
      }
      let tmp: FormsModel[] = [];
      tmp.push(hashMap.get('loanProduct'));
      tmp.push(hashMap.get('fixedAcceptedLoanAmount'));
      tmp.push(hashMap.get('loanAccountNumber'));
      this.forms.push(tmp);

      tmp = [];
      tmp.push(hashMap.get('rateChange'));
      tmp.push(hashMap.get('applicationFee'));
      tmp.push(hashMap.get('mortgageContract'));
      tmp.push(hashMap.get('loanServiceFee'));
      tmp.push(hashMap.get('numberOfContract'));
      tmp.push(hashMap.get('fiduciaryContract'));
      this.forms.push(tmp);

      tmp = [];
      tmp.push(hashMap.get('collateralContract'));
      if (tmp[0]) { tmp[0].columnIndex = 3; }
      this.forms.push(tmp);

      tmp = [];
      tmp.push(hashMap.get('warrantyContract'));
      if (tmp[0]) { tmp[0].columnIndex = 3; }
      this.forms.push(tmp);

      tmp = [];
      tmp.push(hashMap.get('serviceFee'));
      tmp.push(hashMap.get('supplierFee'));
      tmp.push(hashMap.get('otherFee'));
      tmp.push(hashMap.get('propertyInsurance'));
      tmp.push(hashMap.get('accidentInsurance'));
      this.forms.push(tmp);

      for (let i = 0; i < 5; i++) {
        let currentCurrentAccount;
        let otherCurrentAccount;
        if (hashMap.get('currentAccount' + i)) { currentCurrentAccount = hashMap.get('currentAccount' + i).formFieldValue.defaultValue; }
        if (hashMap.get('otherCurrentAccount' + i)) { otherCurrentAccount = hashMap.get('otherCurrentAccount' + i).formFieldValue.defaultValue; }
        if (undefined  !== currentCurrentAccount && 'empty' !== currentCurrentAccount ) {
          const account = [];
          account.push(hashMap.get('currentAccount' + i));
          account.push(hashMap.get('savingAccount' + i));
          account.push(hashMap.get('cardAccount' + i));
          this.accountForm.push(account);
        }
        if (undefined !== otherCurrentAccount && 'empty' !== otherCurrentAccount) {
          const otherAccount = [];
          otherAccount.push(hashMap.get('otherBankName' + i));
          otherAccount.push(hashMap.get('otherCurrentAccount' + i));
          otherAccount.push(hashMap.get('otherSavingAccount' + i));
          otherAccount.push(hashMap.get('otherCardAccount' + i));
          this.otherAccountForm.push(otherAccount);
        }
      }
    }
  }

  increaseNumber(accountNumber: number) {
    if (accountNumber === 1 && this.accountForm.length < 5) {
      const account = [
        {id: 'currentAccount' + this.accountForm.length, label: 'Харилцах данс', required: false, options: [],
          formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
        {id: 'savingAccount' + this.accountForm.length, label: 'Хадгаламжийн данс', required: false, options: [],
          formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
        {id: 'cardAccount' + this.accountForm.length, label: 'Картын данс', required: false, options: [],
          formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
      ];
      this.accountForm.push(account);
    }
    if (accountNumber !== 1 && this.otherAccountForm.length < 5) {
      const otherAccount = [
        {id: 'otherBankName' + this.otherAccountForm.length, label: 'Банкны нэр', required: false, options: BANK_LIST,
          formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
        {id: 'otherCurrentAccount' + this.otherAccountForm.length, label: 'Харилцах данс', required: false, options: [],
          formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
        {id: 'otherSavingAccount' + this.otherAccountForm.length, label: 'Хадгаламжийн данс', required: false, options: [],
          formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
        {id: 'otherCardAccount' + this.otherAccountForm.length, label: 'Картын данс', required: false, options: [],
          formFieldValue: {defaultValue: '', valueInfo: ''}, validations: [], disabled: false, type: 'string', context: undefined},
      ];
      this.otherAccountForm.push(otherAccount);
    }
  }
  decreaseNumber(accountNumber: number) {
    if (accountNumber === 1 && this.accountForm.length > 1) {
      this.accountForm.pop();
    }
    if (accountNumber !== 1 && this.otherAccountForm.length > 1) {
      this.otherAccountForm.pop();
    }
  }

  printContract() {
    this.printEmitter.emit(this.mapForm());
  }

  saveContract() {
    this.saveEmitter.emit(this.mapForm());
  }

  submitContract() {
    this.submitEmitter.emit(this.mapForm());
  }

  mapForm() {
    const returnForm = [];
    this.mapFields(this.forms, returnForm);
    this.mapFields(this.accountForm, returnForm);
    this.mapFields(this.otherAccountForm, returnForm);
    return returnForm;
  }

  mapFields(forms: any[], returnForm: any[]) {
    forms.forEach((form: FormsModel[]) => {
      form.forEach(field => {
        const value = field.formFieldValue.defaultValue;
        if (undefined !== value) {
          returnForm.push(field); }
      });
    });
  }
}
