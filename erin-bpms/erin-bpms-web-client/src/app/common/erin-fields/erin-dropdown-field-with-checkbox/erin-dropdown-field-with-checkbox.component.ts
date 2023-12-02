import {Component, OnInit} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {ErinBaseField} from '../ErinBaseField';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'erin-dropdown-field-with-checkbox',
  template: `
    <div class="simple-form">
      <label><span *ngIf="isRequired(field)" class="error-label">*</span>{{field.label}}</label>
      <mat-form-field class="simple-dropdown-checkbox" appearance="outline">
        <mat-select [(ngModel)]="field.formFieldValue.defaultValue" multiple>
<!--          <mat-option *ngIf="showEmptyOption(field)" value=""></mat-option>-->
          <mat-option  *ngFor="let choice of field.optionsCheckbox" [value]="choice.value">{{ choice.value }}</mat-option>
        </mat-select>
      </mat-form-field>
    </div>
  `,
  styleUrls: ['./erin-dropdown-field-with-checkbox.component.scss']

})
export class ErinDropdownFieldWithCheckboxComponent extends ErinBaseField implements OnInit {
  selected = new FormControl();
  checkedIDs = [];

  ngOnInit() {
  }

  showEmptyOption(field: FormsModel): boolean {
    return field.formFieldValue.defaultValue == null;
  }
}
