import {Injectable} from '@angular/core';
import {GroupManagementService} from './services/group-management.service';
import {NewNode, Node} from './models/tree.model';
import {Observable} from 'rxjs';
import {DetailedUserInfo, Membership, Role} from './models/group-management.model';
import {select, Store} from '@ngrx/store';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ApplicationState} from '../statemanagement/state/ApplicationState';

@Injectable({
  providedIn: 'root'
})
export class GroupManagementSandboxService {
  auth$ = this.store.pipe(select(state => {
    return state.auth;
  }));
  currentGroupId;

  constructor(
    private gmService: GroupManagementService,
    private snackbar: MatSnackBar,
    private store: Store<ApplicationState>) {
    this.auth$.subscribe(res => {
      this.currentGroupId = res.userGroup;
    });
  }

  public getAllUsers(): Observable<DetailedUserInfo[]> {
    return this.gmService.getAllUsers();
  }

  public getAllGroups(): Observable<Node[]> {
    return this.gmService.getAllUsersGroups(this.currentGroupId);
  }

  public getAllRoles(): Observable<Role[]> {
    return this.gmService.getAllRoles();
  }

  public createMembership(groupId: string, roleId: string, users: string[]): Observable<Membership[]> {
    return this.gmService.createMemberships(groupId, roleId, users);
  }

  public addGroup(newNode: NewNode): Observable<Node> {
    return this.gmService.addGroup(newNode);
  }

  public deleteGroup(id): Observable<string[]> {
    return this.gmService.deleteGroup(id);
  }

  public updateGroupName(id: string, newName: string): Observable<Node> {
    return this.gmService.updateGroupName(id, newName);
  }

  public deleteMember(membershipId: string): Observable<any> {
    return this.gmService.deleteMembership(membershipId);
  }

  public moveUserGroup(groupId: string, userId: string, roleId: string) {
    return this.gmService.moveUserGroup(groupId, userId, roleId);
  }
}
