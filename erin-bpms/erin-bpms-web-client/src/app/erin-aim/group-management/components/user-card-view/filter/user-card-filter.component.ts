import {Component, EventEmitter, Input, Output} from '@angular/core';
import {UserInfo} from '../../../models/group-management.model';

//TODO: This component is uncessary.
@Component({
  selector: 'user-card-filter',
  template: `
    <input class="form-control" type="text" placeholder="{{placeholderText}}" [ngModel]="searchValue" (ngModelChange)="onValueEntered($event)">
  `,
  styleUrls: ['./user-card-filter.component.scss']
})

export class UserCardFilter {
  @Input() userdata: UserInfo[] = [];
  @Output() change: EventEmitter<string> = new EventEmitter<string>();

  searchValue: string;
  placeholderText = 'Ажилчны нэрээр шүүх';

  constructor() {
  }
  onValueEntered(searchValue: string) {
    this.change.emit(searchValue);
  }
}
