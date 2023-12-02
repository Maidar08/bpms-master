import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';

@Component({
  selector: 'erin-search-field',
  template: `
    <div class="simple-form">
      <div class="search-field-container">
        <input  class="search-field-input" placeholder="ХАЙХ" #searchInput
               [ngModel]="searchValue" (ngModelChange)="search($event)">
        <button mat-icon-button class="search-button" *ngIf="!searchInput.value">
          <mat-icon class="search-field-icon" >search</mat-icon>
        </button>
        <button mat-icon-button class="search-button" *ngIf="searchInput.value" (click)="clearInput()">
          <mat-icon  class="search-field-icon" >clear</mat-icon>
        </button>
      </div>
    </div>
  `,

})
export class ErinSearchFieldComponent implements OnInit {
  @ViewChild('searchInput', {static: false}) searchInput;
  // tslint:disable-next-line:no-output-native
  @Output() change: EventEmitter<string> = new EventEmitter<string>();
  searchValue: string;

  constructor() {
  }

  ngOnInit(): void {
  }

  search(searchValue) {
    this.change.emit(searchValue);
  }

  clearInput() {
    this.searchInput.nativeElement.value = '';
    this.change.emit('');
  }
}
