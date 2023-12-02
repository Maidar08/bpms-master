import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'simple-dropdown',
  template: `
    <div class="simple-form">
      <label>{{label}}</label>
      <select [(ngModel)]="selected" class="simple-dropdown simple-field" (ngModelChange)="changedSelectedItem()">
        <option *ngFor="let item of items" [ngValue]="item">{{ item.name }}</option>
      </select>
    </div>
  `
})
export class SimpleDropdownComponent {
  @Input() items: any[];
  @Input() label: string;
  @Input() selected: any;
  @Output() selectionChange = new EventEmitter();
  changedSelectedItem() {
    this.selectionChange.emit(this.selected);
  }
}
