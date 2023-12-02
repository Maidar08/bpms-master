import {Component, DoCheck, OnInit} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'erin-autocomplete-field',
  template: `
    <div class="simple-form">
      <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>
      <input type="text"
             (blur)="updateValue($event.target)"
             [formControl]="myControl" class="simple-field" required
             [attr.disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
             [matAutocomplete]="auto">
      <mat-autocomplete #auto="matAutocomplete" [panelWidth]="auto">
        <mat-option *ngFor="let option of filteredOptions | async" [value]="option">
          {{option}}
        </mat-option>
      </mat-autocomplete>
    </div>
  `
})
export class ErinAutocompleteFieldComponent extends ErinBaseField implements OnInit, DoCheck {
  option;
  initialDefaultValue: string;
  myControl = new FormControl();
  options: string[] = [];
  filteredOptions: Observable<string[]>;

  ngOnInit() {
    for (const field of this.field.options) {
      if (field.value !== null) {
        this.options.push(field.value);
      }
    }
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value)),
    );
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.options.filter(option => option.toLowerCase().includes(filterValue));
  }

  updateValue(input: any): void {
    const inputValue = input.value;
    if (this.options.includes(inputValue)) {
      this.field.formFieldValue.defaultValue = inputValue;
    } else {
      this.field.formFieldValue.defaultValue = this.initialDefaultValue;
    }
  }

  ngDoCheck(): void {
    if (this.initialDefaultValue !== this.field.formFieldValue.defaultValue &&
      this.options.includes(this.field.formFieldValue.defaultValue)) {
      this.myControl.setValue(this.field.formFieldValue.defaultValue);
      this.initialDefaultValue = this.field.formFieldValue.defaultValue;
    }
  }

}
