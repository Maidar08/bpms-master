import {Component, OnInit} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'erin-checkbox-field',
  template: `
    <div class = "checkbox-field">
      <mat-checkbox color="primary"
                    [disabled]="isReadOnly(field)? true: field.disabled ? true: disabled"
                    [(ngModel)]=field.formFieldValue.defaultValue (change)="change()">{{field.label}}
      </mat-checkbox>
    </div>
  `,
})

export class ErinCheckboxFieldComponent extends ErinBaseField implements OnInit {
  ngOnInit(): void {
  }
}
