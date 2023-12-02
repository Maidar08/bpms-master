import {Component, OnInit, ViewChild} from '@angular/core';
import {AddUserModel, DetailedUserInfo, Role, UserInfo} from '../../models/group-management.model';
import {GroupManagementSandboxService} from '../../group-management-sandbox.service';
import {UserCardViewComponent} from '../../components/user-card-view/user-card-view.component';

import {MatDialog} from '@angular/material/dialog';
import {CdkDragDrop} from '@angular/cdk/drag-drop';
import {Node} from '../../models/tree.model';
import {ConfirmDialogComponent} from '../../../component/confirm-dialog/confirm-dialog.component';
import {ComponentPortal} from '@angular/cdk/portal';
import {ErinLoaderComponent} from '../../../component/loader/loader.component';
import {Overlay} from '@angular/cdk/overlay';

@Component({
  selector: 'group-management-container',
  template: `
    <div [ngStyle]="{display: 'flex'}" cdkDropListGroup>
      <mat-card class="tree-view-card">
        <tree-view
          (dragAndDrop)="drop($event)"
          (loaded)="onLoaded($event)"
          (groupDeleted)="onGroupDeleted($event)"
          (selectionChange)="onSelectionChanged($event)"
          (treeLoad)="updateTreeLoad($event)">
        </tree-view>
      </mat-card>
      <div class="user-view-card" style="width: 100%">
        <user-card-view #users
                        [data]="this.selectedGroupUsers"
                        [allUsers]="this.allUsers"
                        [roles]="roles"
                        [nameOfGroup]="selectedGroupName"
                        (addUser)="onUserAdded($event)"
                        (removeUser)="onUserRemoved($event)">
        </user-card-view>
      </div>
    </div>
  `
})
export class GroupManagementContainerComponent implements OnInit {
  allUsers: DetailedUserInfo[] = [];
  allUsersMap: Map<string, DetailedUserInfo> = new Map<string, DetailedUserInfo>();
  selectedGroupUsers: UserInfo[];
  roles: Role[];
  newGroupId = '';
  userId = '';
  roleId = '';
  selectedGroupId = '';
  selectedGroupName = '';
  overlayRef;
  treeLoaded: boolean;
  groupManagementLoaded: boolean = false;

  @ViewChild('users') userCardView: UserCardViewComponent;

  constructor(public dialog: MatDialog, private sb: GroupManagementSandboxService, public overlay: Overlay) {
  }

  ngOnInit() {
    this.overlayRef = this.setOverlay();
    this.checkLoad();
    this.sb.getAllUsers().subscribe((res) => {
      this.allUsers = res;
      for (const user of this.allUsers) {
        this.allUsersMap.set(user.username, user);
      }
      this.selectedGroupUsers = this.getSelectedGroupUsers(this.selectedGroupId);
      this.groupManagementLoaded = true;
      this.checkLoad();
    });
    this.sb.getAllRoles().subscribe((res) => {
      this.roles = res;
    });
  }

  onUserAdded(data: AddUserModel) {
    this.sb.createMembership(this.selectedGroupId, data.roleId, data.users).subscribe((memberships) => {
      const newUsers: UserInfo[] = [];
      for (const membership of memberships) {
        const username = membership.userId;
        const newUserData: UserInfo = {
          username,
          email: this.allUsersMap.get(username).email,
          phoneNumber: this.allUsersMap.get(username).phoneNumber,
        };

        const role = this.roles.filter(r => r.roleId === membership.roleId)[0];
        if (role != null) {
          newUserData.role = role.roleName;
        }

        newUsers.push(newUserData);
        this.allUsers.forEach(user => {
          if (user.username === membership.userId) {
            user.membership = membership;
          }
        });
      }

      this.selectedGroupUsers.unshift(...newUsers);
      this.userCardView.reset();
    });
  }

  onUserRemoved(user: UserInfo) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '500px',
      data: {title: 'Анхааруулга', message: 'Та ' + user.username + '-г хасахдаа итгэлтэй байна уу?'}
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const membershipId = this.getMembershipId(user.username);
        this.sb.deleteMember(membershipId).subscribe((res) => {
          if (res) {
            for (let index = 0; index < this.selectedGroupUsers.length; index++) {
              if (this.selectedGroupUsers[index].username === user.username) {
                this.selectedGroupUsers.splice(index, 1);
              }
            }
            for (const currentUser of this.allUsers) {
              if (currentUser.username === user.username) {
                currentUser.membership = null;
              }
            }
            this.userCardView.reset();
          }
        });
      }
    });
  }

  getMembershipId(username: string): string {
    for (const user of this.allUsers) {
      if (username === user.username) {
        const membership = user.membership;
        return membership.membershipId;
      }
    }
  }

  onLoaded(groupId: string) {
    this.selectedGroupId = groupId;
    this.selectedGroupUsers = this.getSelectedGroupUsers(this.selectedGroupId);
  }

  onSelectionChanged(node: Node) {
    this.selectedGroupUsers = this.getSelectedGroupUsers(node.id);
    this.selectedGroupId = node.id;
    this.selectedGroupName = node.name;
  }

  getSelectedGroupUsers(selectedGroupId: string): UserInfo[] {
    const mappedUsers: UserInfo[] = [];

    for (const user of this.allUsers) {
      if (user.membership !== null && user.membership.groupId === selectedGroupId) {
        const roleId = this.roles.filter(role => role.roleId === user.membership.roleId)[0];
        if (roleId !== undefined) {
          mappedUsers.unshift({
            username: user.username,
            email: user.email,
            phoneNumber: user.phoneNumber,
            role: this.roles.filter(role => role.roleId === user.membership.roleId)[0].roleName
          });
        }
      }
    }
    return mappedUsers;
  }

  drop(event: CdkDragDrop<any[]>) {
    this.newGroupId = event.container.element.nativeElement.id;
    this.userId = this.selectedGroupUsers[event.previousIndex].username;
    const newGroupName = event.container.element.nativeElement.innerText;
    if (this.selectedGroupId !== this.newGroupId) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '600px',
        data: {
          title: 'Анхааруулга',
          message: 'Та ' + this.userId + '-г "' + newGroupName + '" группд шилжүүлэхдээ итгэлтэй байна уу?\n'
            + 'Энэ үйлдлийг хийснээр "' + this.selectedGroupName + '" группээс хасахыг анхаарна уу!'
        }
      });
      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          for (const i of this.roles) {
            if (i.roleName === this.selectedGroupUsers[event.previousIndex].role) {
              this.roleId = i.roleId;
            }
          }
          this.sb.moveUserGroup(this.newGroupId, this.userId, this.roleId).subscribe(res => {
            for (const i of this.allUsers) {
              if (i.membership !== null && i.membership.userId === this.userId) {
                i.membership.groupId = this.newGroupId;
                this.selectedGroupUsers.splice(event.previousIndex, 1);
                this.userCardView.reset();
              }
            }
          });
        }
      });
    }
  }
  onGroupDeleted(membershipIds: string[]) {
    for (const user of this.allUsers) {
      if (user.membership != null && membershipIds.includes(user.membership.membershipId)) {
        user.membership = null;
      }
    }
    this.userCardView.reset();
  }

  private setOverlay() {
    const overlayRef = this.overlay.create({
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true
    });
    overlayRef.attach(new ComponentPortal(ErinLoaderComponent));
    return overlayRef;
  }

  updateTreeLoad(Loading) {
    this.treeLoaded = Loading;
    this.checkLoad();
  }

  checkLoad() {
    if (this.treeLoaded === true && this.groupManagementLoaded === true) {
      this.overlayRef.dispose();
    }
  }
}
