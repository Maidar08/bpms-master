import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import {AddUserModel, DetailedUserInfo, Role} from '../../models/group-management.model';
import {FormControl, FormGroup} from '@angular/forms';
import {ErinChipField} from '../../../component/erin-chip-field/erin-chip-field.component';
import {Chip} from '../../../component/erin-chip-field/chip.model';
import {GREEN_CHIP_COLOR, WHITE_TEXT_COLOR} from '../../../statemanagement/app.constant';


@Component({
  selector: 'user-search-field',
  template: `
    <div class="user-search">
      <div class="chips">
        <erin-chip-field #chipField
          [icon]="'search'"
          [title]="'Хайх'"
          [autocomplete]="true"
          [allAutoCompleteOption]="chips"
          [formGroup]="formGroup"
          [formType]="'users'">
        </erin-chip-field>
      </div>
      <div class="add-membership">
        <dropdown *ngIf="roleNames.length > 1" [values]="roleNames"
          [style]="'role-assignment'"
          [icon]="'vpn_key'"
          [tooltipMsg]="'Хэрэглэгчийн эрх'"
          (selectionChange)="onRoleChange($event)"></dropdown>
        <div>
          <button mat-flat-button color="primary" class="add-membership-button" (click)="onAddClicked()">
            <mat-icon class="add-icon">group_add</mat-icon>
            НЭМЭХ
          </button>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./user-search-field.component.scss']
})
export class UserSearchFieldComponent implements OnChanges {
  @Input() allUsers: DetailedUserInfo[];
  @Input() roles: Role[];
  @Output() addUsers: EventEmitter<AddUserModel> = new EventEmitter<AddUserModel>();

  @ViewChild('chipField') chipField: ErinChipField;

  formGroup: FormGroup;
  chips: Chip[] = [];
  roleNames: string[] = [];
  selectedRole: Role;

  constructor() {
    this.formGroup = new FormGroup({
      users: new FormControl([])
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop === 'allUsers' && this.allUsers != null) {
        this.collectToChips();
      }
      if (prop === 'roles' && this.roles != null) {
        this.roleNames = this.roles.map(role => role.roleName);
        this.selectedRole = this.roles[0];
      }
    }
  }

  onAddClicked(): void {
    const users = [];

    for (const user of this.formGroup.controls.users.value) {
      users.push(user.value);
    }

    this.addUsers.emit({roleId: this.selectedRole.roleId, users});
  }

  onRoleChange(roleName: string) {
    this.selectedRole = this.roles.filter(role => role.roleName === roleName)[0];
  }

  reset(): void {
    this.disableChips();
    this.chipField.empty();
  }

  private collectToChips() {
    for(const user of this.allUsers) {
      this.chips.push({value: user.username, color: GREEN_CHIP_COLOR, textColor: WHITE_TEXT_COLOR})
    }
    this.disableChips();
  }

  private disableChips() {
    this.resetDisabledChips();
    for (const user of this.allUsers) {
      for (const chip of this.chips) {
        if (chip.value === user.username && user.membership != null) {
          chip.disabled = true;
        }
      }
    }
  }

  private resetDisabledChips() {
    for (const chip of this.chips) {
      chip.disabled = false;
    }
  }
}
