import {Component, OnInit} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-dropdown-field',
  template: `
    <div class="simple-form">
      <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
      <select [(ngModel)]="field.formFieldValue.defaultValue" class="simple-field simple-dropdown" required
              [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
              (change)="selectChange($event)">
        <option *ngIf="showEmptyOption(field)" value="" style="display:none;"></option>
        <option *ngFor="let choice of field.options" [value]="choice.value">{{ choice.value }}</option>
      </select>
    </div>
  `
})
export class ErinDropdownFieldComponent extends ErinBaseField implements OnInit {
  ngOnInit() {
    this.selectChange();
  }

  showEmptyOption(field: FormsModel): boolean {
    return field.formFieldValue.defaultValue == null;
  }

  selectChange(event?) {
    this.change();
    if (this.field.id === 'loanProduct' && this.field.formFieldValue.defaultValue != null) {
      sessionStorage.setItem('loanProduct', this.field.formFieldValue.defaultValue);
      if (event != null) {
        sessionStorage.setItem('loanProduct', event.target.value);
      }
    }
    if (sessionStorage.getItem('loanProduct')) {
      if (this.totalPaymentEqual(sessionStorage.getItem('loanProduct'))) {
        if (this.field.id === 'repaymentType') {
          this.field.formFieldValue.defaultValue = 'Нийт төлбөр тэнцүү';
        }
      } else if (this.field.id === 'repaymentType') {
        this.field.formFieldValue.defaultValue = 'Үндсэн төлбөр тэнцүү';
      }
    }
  }

  totalPaymentEqual(sessionValue): boolean {
    const stringsId = ['EB51-365-Цалингийн зээл -Иргэн –EMI', 'EA51-365-Өрхийн зээл - Иргэн-EMI', 'EH54-365-Хэрэглээний ХАЗ-EMI',
      'EH89-EMI-Автомашины зээл-Иргэн', 'DJ87-EMI-Автомашины зээл-ААН', 'EA53-Эко хэрэглээний зээл-EMI /дулаалга/',
      'EH57-Эко хэрэглээний зээл-EMI/халаагуур/'];
    if (stringsId.indexOf(sessionValue) !== -1) {
      return true;
    }
  }
}
