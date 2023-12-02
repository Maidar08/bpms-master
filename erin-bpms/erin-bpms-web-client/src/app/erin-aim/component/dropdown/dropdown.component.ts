import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {MatSelect} from '@angular/material/select';

@Component({
  selector: 'dropdown',
  template: `
    <button mat-button [ngClass]="style" color="primary" [matTooltip]="tooltipMsg">
      <mat-select #matSelect [value]="values[0]" (selectionChange)="onSelectionChange($event)">
        <mat-option [value]="value" *ngFor="let value of values">
          {{value}}
        </mat-option>
      </mat-select>
    </button>
  `,
  styleUrls: ['./dropdown.component.scss']
})
export class DropdownComponent {
  @Input() values: string[];
  @Input() style: string;
  @Input() icon: string;
  @Input() tooltipMsg: string;
  @Output() selectionChange: EventEmitter<string> = new EventEmitter<string>();

  @ViewChild('matSelect') matSelect: MatSelect;

  constructor() {
  }

  onSelectionChange(event: any) {
    this.selectionChange.emit(event.value);
  }

  reset(): void {
    this.matSelect.value = this.values[0];
  }
}
