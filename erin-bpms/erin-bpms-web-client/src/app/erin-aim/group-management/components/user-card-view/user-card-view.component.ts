import {AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild} from '@angular/core';
import {AddUserModel, DetailedUserInfo, Role, UserInfo} from '../../models/group-management.model';
import {MatTableDataSource} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import {Observable} from 'rxjs';
import {UserSearchFieldComponent} from '../search/user-search-field.component';
import {DropdownComponent} from '../../../component/dropdown/dropdown.component';


@Component({
  selector: 'user-card-view',
  template: `
    <mat-card class="card">
      <mat-card-header><h1 class="mat-h1">{{nameOfGroup}}</h1></mat-card-header>
      <mat-divider></mat-divider>
      <mat-card-content>
        <h4 class="mat-h4">{{title}}</h4>
        <user-search-field #searchField
                           [allUsers]="allUsers"
                           [roles]="roles"
                           (addUsers)="onUserAdded($event)">
        </user-search-field>
        <!-- TODO: card filter is not present update this-->
        <user-card-filter (change)="receivedValue($event)"></user-card-filter>
        <dropdown
          *ngIf="roleNames.length > 2"
          #dropdown [values]="roleNames"
          [style]="'role-filter'"
          (selectionChange)="onChangeRole($event)"></dropdown>
        <div class="user-card"
             cdkDropList
             [cdkDropListData]="users">
          <div cdkDrag
               *ngFor="let user of users | async"
               [ngClass]="user.moreOption ? OPTION_GRAY : OPTION_WHITE"
               class="each-user"
               (mouseenter)="mouseEnter(user)"
               (mouseleave)="mouseLeave(user)">
            <div class="card-icon"></div>
            <div class="card-detail">
              <h3 class="mat-h3" [matTooltip]="user.username">{{user.username}}</h3>
              <h5 class="mat-h5" [matTooltip]="user.email">{{user.email}}</h5>
              <h5 class="mat-h5" [matTooltip]="user.role">{{user.role}}</h5>
            </div>
            <button mat-icon-button [matMenuTriggerFor]="addOption" class="user-more-option">
              <mat-icon> more_vert </mat-icon>
              <mat-menu #addOption='matMenu'>
                <button mat-menu-item (click)="onUserRemoved($event, user)">
                  <!--move this style to css and wrap it with class-->
                  <mat-icon style="line-height: 0.8; margin-right: 10px">remove_circle</mat-icon>
                  <span>Хасах</span>
                </button>
              </mat-menu>
            </button>
          </div>
        </div>
      </mat-card-content>
    </mat-card>
  `,
  styleUrls: ['./user-card-view.component.scss']
})
export class UserCardViewComponent implements OnChanges, AfterViewInit {
  @Input() allUsers: DetailedUserInfo[];
  @Input() data: UserInfo[];
  @Input() roles: Role[];
  @Input() nameOfGroup = 'Групп';

  @Output() addUser: EventEmitter<AddUserModel> = new EventEmitter<AddUserModel>();
  @Output() removeUser: EventEmitter<UserInfo> = new EventEmitter<UserInfo>();

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('searchField') searchField: UserSearchFieldComponent;
  @ViewChild('dropdown') dropdown: DropdownComponent;

  title = 'Группд байгаа ажилчид';
  OPTION_GRAY = 'optionGray';
  OPTION_WHITE = 'optionWhite';
  dataSource = new MatTableDataSource<UserInfo>();
  users: Observable<any>;
  searchValue: string;
  selectedRole: string;

  userInfos: UserInfo[];
  originalUserData: UserInfo[];
  categorizedData: UserInfo[];

  roleNames: string[] = [];

  constructor(private cd: ChangeDetectorRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const prop in changes) {
      if (prop === 'data' && this.data != null) {
        this.userInfos = this.data;
        this.originalUserData = this.data;
        this.updateTable();
      }
      if (prop === 'roles' && this.roles != null) {
        this.roleNames = this.roles.map(role => role.roleName);
        this.roleNames.unshift('Бүгд');
      }
    }
  }

  ngAfterViewInit(): void {
    this.cd.detectChanges();
  }

  onChangeRole(value: string) {
    this.selectedRole = value;
    if (this.selectedRole === 'Бүгд') {
      this.userInfos = this.originalUserData;
      this.updateTable();
      return;
    }
    this.userInfos = this.originalUserData.filter(user => user.role === this.selectedRole);
    this.categorizedData = this.userInfos;
    this.updateTable();
  }

  onUserAdded(data: AddUserModel) {
    this.addUser.emit(data);
    this.dropdown.reset();
  }

  onUserRemoved(event, user: UserInfo) {
    this.removeUser.emit(user);
    this.dropdown.reset();
  }

  receivedValue(searchValue: string) {
    this.searchValue = searchValue;
    if (searchValue === null) {
      return;
    }
    if (this.searchValue !== undefined && this.searchValue.trim().length === 0) {
      if (this.selectedRole === undefined) {
        this.userInfos = this.originalUserData;
        this.updateTable();
        return;
      }
      if (this.selectedRole === 'Бүгд') {
        this.userInfos = this.originalUserData;
      } else {
        this.userInfos = this.categorizedData;
      }
      this.updateTable();
    }
    this.userInfos = this.originalUserData.filter(user => new RegExp(`^${this.searchValue}`, 'gi').test(user.username) ||
      user.username.toLocaleLowerCase().includes(this.searchValue.toLocaleLowerCase()));
    this.updateTable();
  }

  updateTable() {
    this.dataSource = new MatTableDataSource<UserInfo>(this.userInfos);
    this.users = this.dataSource.connect();
    this.cd.detectChanges();
  }

  reset(): void {
    this.userInfos = this.data;
    this.originalUserData = this.data;
    this.updateTable();
    this.searchField.reset();
  }

  mouseEnter(user) {
    user.moreOption = true;
  }

  mouseLeave(user) {
    setTimeout(() => {
      user.moreOption = false;
    }, 100);
  }
}
