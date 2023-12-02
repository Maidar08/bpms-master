import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';

@Component({
  selector: 'search-input',
  template: `
    <div class="error-message" *ngIf="error">Регистерийн дугаараа зөв оруулна уу!</div>
    <mat-form-field appearance="outline" class="margin-bottom">
      <input id="productSearch" (keyup.enter)="emitSearchValue()"
             matInput title="Search Product"
             #searchInput
             (input)="search($event)" placeholder="ХАЙХ">
      <button mat-icon-button matSuffix *ngIf="!searchInput.value || isSearchByRegister()" id="searchIcon" (click)="emitSearchValue()">
        <mat-icon>search</mat-icon>
      </button>
      <button mat-icon-button matSuffix *ngIf="searchInput.value && !isSearchByRegister()" (click)="clearInput()" id="clearIcon">
        <mat-icon>close</mat-icon>
      </button>
    </mat-form-field>
  `,
  styleUrls: ['./search-input.component.scss']
})
export class SearchInputComponent {
  @ViewChild('searchInput', {static: false}) searchInput;
  @Output() searchStr = new EventEmitter<string>();
  @Output() customerNumber = new EventEmitter<string>();
  @Input() type = 'loan-request';
  error = false;
  REGISTER_REG_EXP = '[ӨөҮүЁёА-Яа-я]{2}[0-9]{8}';

  search(e): void {
    this.error = false;
    this.searchStr.emit(e.target.value);
  }

  clearInput(): void {
    this.searchInput.nativeElement.value = '';
    this.searchStr.emit('');
  }

  isSearchByRegister() {
    return this.type !== 'loan-request';
  }

  emitSearchValue() {
    const inputField = document.getElementsByTagName('input')[0];
    const inputValue = this.trimValue(inputField.value);
    if (this.isSearchByRegister()) {
      this.customerNumber.emit(inputValue);
    }
  }

  trimValue(value: string): string {
    return value.replace(/\s/g, '');
  }
}
