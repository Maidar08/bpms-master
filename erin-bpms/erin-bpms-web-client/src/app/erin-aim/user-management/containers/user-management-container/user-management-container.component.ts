import {Component, OnInit, ViewChild} from '@angular/core';
import {UserDialogComponent} from "../../components/user-dialog/user-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {UserManagementSandboxService} from "../../user-management-sandbox.service";
import {Observable} from "rxjs";
import {USER_TABLE_COLUMNS, UserModel, UserStatus} from "../../models/user-management.model";
import {CONFIRM_DIALOG_CONSTANTS} from "../../models/user-management.constant";
import {ConfirmDialogComponent} from "../../../component/confirm-dialog/confirm-dialog.component";
import {ErinTableComponent} from "../../../component/erin-table/erin-table.component";

@Component({
  selector: 'user-management-container',
  template: `
    <div class="user-management-header">
      <button mat-flat-button color="primary" class="create-user-button" (click)="openCreateUserDialog()">ҮҮСГЭХ</button>
      <input hidden type="file" accept=".csv" #file (change)="addUsers($event)">
      <button mat-button
              [matTooltip]="addBulkUsers"
              color="primary"
              class="add-users-button"
              (click)="file.click()">
        {{importFile}}
        <mat-icon class="add-icon">login</mat-icon>
      </button>
    </div>
    <div class="user-management-main-container">
      <erin-table
        [data]="dataSource"
        [columns]="columns"
        [hoverHighlight]="true"
        [paginator]="true"
        [defaultPageSize]="15"
        [select]="true"
        [action]="true"
        (onArchive)="openArchiveUserDialog($event)"
        (onDelete)="openDeleteUserDialog($event)"
        (onEdit)="openUpdateUserDialog($event)">
      </erin-table>
    </div>
    <erin-loader [loading]="loading"></erin-loader>
  `,
  styleUrls: ['./user-management-container.component.scss']
})
export class UserManagementContainerComponent implements OnInit {
  @ViewChild(ErinTableComponent) table: ErinTableComponent;
  addUsersId = "app.users.add";
  importFile = "ИМПОРТ";
  addBulkUsers = "Хэрэглэгч олноор нэмэх";
  userDialogInstance: UserDialogComponent;
  loading = false;
  columns = USER_TABLE_COLUMNS;
  dataSource: UserModel[];
  confirmConstants = CONFIRM_DIALOG_CONSTANTS;

  constructor(public dialog: MatDialog, public sb: UserManagementSandboxService) {
  }

  ngOnInit(): void {
    this.refreshTable();
  }

  openCreateUserDialog(): void {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      maxWidth: '600px',
      minWidth: '300px',
      disableClose: true,
      data: {title: 'Хэрэглэгч үүсгэх', type: 'create'},
    });

    this.userDialogInstance = dialogRef.componentInstance;

    this.userDialogInstance.onSubmit.subscribe(user => {
      this.userDialogInstance.loading = true;
      this.create(user).subscribe(res => {
        // Update table
        this.dataSource = [...this.dataSource, res];
        this.userDialogInstance.loading = false;
        dialogRef.close();
      }, error => {
        this.userDialogInstance.loading = false;
      });
    });

    this.userDialogInstance.onSearch.subscribe(username => {
      this.search(username).subscribe(res => {
        this.userDialogInstance.pattern = res ? '^(?!' + username + '$).*$' : '^\\S*$';
        this.userDialogInstance.isUsernameAlreadyExists = res;
        this.userDialogInstance.loading = false;
      });
    });

    dialogRef.afterClosed().subscribe(() => {
      this.userDialogInstance.onSubmit.unsubscribe();
      this.userDialogInstance.onSearch.unsubscribe();
    });
  }

  create(newUser): Observable<any> {
    return this.sb.createUser(newUser);
  }

  openUpdateUserDialog(updatingUser: UserModel) {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      maxWidth: '600px',
      minWidth: '400px',
      data: {title: 'Хэрэглэгчийн мэдээлэл солих', type: 'update', user: updatingUser},
    });
    this.userDialogInstance = dialogRef.componentInstance;

    this.userDialogInstance.onSubmit.subscribe((user: UserModel) => {
      this.userDialogInstance.loading = true;
      user.userId = updatingUser.userId;
      this.update(user).subscribe(res => {
        if (res) {
          this.userDialogInstance.username.subscribe((username: string) => {
            updatingUser.username = username;
          })
          updatingUser.firstName = user.firstName;
          updatingUser.lastName = user.lastName;
          updatingUser.email = user.email;
          updatingUser.phoneNumber = user.phoneNumber;
          updatingUser.gender = user.gender;
          updatingUser.message = user.message;
          updatingUser.deletable = user.deletable;
          this.userDialogInstance.loading = false;
          dialogRef.close();
        } else {
          this.userDialogInstance.loading = false;
        }
      }, error => {
        this.userDialogInstance.loading = false;
      });
    });

    this.userDialogInstance.onSearch.subscribe(username => {
      if (username === updatingUser.username) {
        this.userDialogInstance.loading = false;
      } else {
        this.search(username).subscribe(res => {
          this.userDialogInstance.pattern = res ? '^(?!' + username + '$).*$' : '^\\S*$';
          this.userDialogInstance.isUsernameAlreadyExists = res;
          this.userDialogInstance.loading = false;
        });
      }
    });

    dialogRef.afterClosed().subscribe(() => {
      this.userDialogInstance.username.unsubscribe();
      this.userDialogInstance.onSubmit.unsubscribe();
      this.userDialogInstance.onSearch.unsubscribe();
    });
  }

  update(updatingUser): Observable<any> {
    return this.sb.updateUser(updatingUser);
  }

  openDeleteUserDialog(rows: UserModel[]) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '600px',
      minWidth: '300px',
      data: rows.length === 1 ?
        this.confirmConstants.find(constant => constant.type === 'delete') :
        this.confirmConstants.find(constant => constant.type === 'bulk_delete'),
    });
    dialogRef.afterClosed().subscribe(isAccepted => {
      if (isAccepted) {
        if (rows.length === 1) {
          this.sb.deleteUser(rows[0].userId).subscribe(res => {
            if (res) {
              this.dataSource = this.dataSource.filter(user => user.userId !== rows[0].userId);
            }
          });
        } else {
          const userIds: string[] = [];

          for (const row of rows) {
            userIds.push(row.userId);
          }
          this.sb.deleteUsers(userIds).subscribe(res => {
            if (res) {
              this.dataSource = this.dataSource.filter(tableItem => userIds.indexOf(tableItem.userId) === -1);
            }
            this.table.unselectAll();
          });
        }
      }
    });
  }

  openArchiveUserDialog(rows: { data: UserModel[], isArchived: boolean }) {
    let type;
    if (rows.data.length === 1) {
      if (rows.isArchived) {
        type = 'unarchive';
      } else {
        type = 'archive';
      }
    } else {
      if (rows.isArchived) {
        type = 'bulk_unarchive';
      } else {
        type = 'bulk_archive';
      }
    }
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '600px',
      minWidth: '300px',
      data: this.confirmConstants.find(constant => constant.type === type),
    });
    dialogRef.afterClosed().subscribe(isAccepted => {
      if (isAccepted) {
        const users = rows.data;
        if (users.length === 1) {
          const user = users[0];
          this.sb.archiveUser(user.userId, rows.isArchived).subscribe(res => {
            if (res) {
              user.status = rows.isArchived ? UserStatus.ACTIVE : UserStatus.ARCHIVED;
            }
          });
        } else {
          const userIds: string[] = [];
          for (const user of rows.data) {
            userIds.push(user.userId);
          }

          this.sb.archiveUsers({userIds, archived: rows.isArchived}).subscribe(res => {
            if (res) {
              for (const user of rows.data) {
                user.status = rows.isArchived ? UserStatus.ACTIVE : UserStatus.ARCHIVED;
              }
              this.table.unselectAll();
            }
          });
        }
      }
    });
  }

  search(username: string): Observable<boolean> {
    return this.sb.isUserExists(username);
  }

  refreshTable(): void {
    this.sb.getUsers().subscribe(res => {
      this.dataSource = res;
    });
  }

  addUsers(event): void {
    // TODO: validate
    this.loading = true
    this.sb.addUsers(event.target.files[0]).subscribe(res => {
      this.refreshTable();
      this.loading = false;
    },   () => {
      this.loading = false;
    });
  }

}

