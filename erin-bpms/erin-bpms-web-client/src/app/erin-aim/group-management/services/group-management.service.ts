import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {NewNode, Node} from '../models/tree.model';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {DetailedUserInfo, Membership, Role} from '../models/group-management.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {AIM_CONFIG} from '../../aim.config.token';
import {AimConfig} from '../../aim.config';


@Injectable({
  providedIn: 'root'
})
export class GroupManagementService {

  DELETE_NODE_SUCCESS_MSG = 'Групп амжилттай устгалаа';
  DELETE_NODE_FAILURE_MSG = 'Групп хадгалахад алдаа гарлаа';
  DELETE_USER_SUCCESS_MSG = 'Хэрэглэгч группээс хаслаа';
  DELETE_USER_FAILURE_MSG = 'Хэрэглэгч группээс хасахад алдаа гарлаа';

  SNACKBAR_ACTION_CLOSE = 'ХААХ';
  SNACKBAR_CLOSE_DURATION = {duration: 3297};

  constructor(
    private http: HttpClient, private snackbar: MatSnackBar,
    @Inject(AIM_CONFIG) private config: AimConfig ) {
  }

  public getAllUsers(): Observable<DetailedUserInfo[]> {
    return this.http.get(this.config.baseUrl + '/aim/users').pipe(map((res: any) => {
      const users: DetailedUserInfo[] = [];
      for (const user of res.entity) {
        const userInfo: DetailedUserInfo = {
          username: user.userId,
          email: user.email,
          phoneNumber: user.phoneNumber,
          membership: null
        };
        if (user.memberships && user.memberships.length > 0) {
          userInfo.membership = {
            userId: user.userId,
            groupId: user.memberships[0].groupId,
            roleId: user.memberships[0].roleId,
            membershipId: user.memberships[0].membershipId
          };
        }
        users.push(userInfo);
      }
      return users;
    }));
  }

  public getAllUsersGroups(groupId: string): Observable<Node[]> {
    return this.http.get(this.config.baseUrl +  '/aim/groups').pipe(map((res: any) => {
      let nodes: Node[] = [];
      if (res.entity.length === undefined) {
        this.pushNode([res.entity]);
      } else {
        nodes = this.pushNode(res.entity);
      }
      return nodes;
    }));
  }

  public getAllRoles(): Observable<Role[]> {
    let params = new HttpParams();
    params = params.append('tenantId', this.config.tenantId);
    return this.http.get(this.config.baseUrl + '/aim/roles', {params}).pipe(map((res: any) => {
      const roles: Role[] = [];

      for (const role of res.entity) {
        roles.push({
          roleId: role.roleId,
          roleName: role.roleName
        });
      }

      return roles;
    }));
  }

  public createMemberships(groupId: string, roleId: string, users: string[]): Observable<Membership[]> {
    const body = {
      groupId,
      roleId,
      users,
      tenantId: this.config.tenantId
    };

    return this.http.post(this.config.baseUrl + '/aim/memberships', body).pipe(map((res: any) => {
      const memberships: Membership[] = [];

      for (const membership of res.entity) {
        memberships.push({
          membershipId: membership.membershipId,
          groupId: membership.groupId,
          roleId: membership.roleId,
          userId: membership.userId
        });
      }
      return memberships;
    }));
  }

  public deleteMembership(membershipId: string): Observable<any> {
    return this.http.delete(this.config.baseUrl + '/aim/memberships/' + membershipId)
      .pipe(map((res: any) => {
        this.snackbar.open(this.DELETE_USER_SUCCESS_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return res.entity;
      }), catchError((error: any) => {
        this.snackbar.open(this.DELETE_USER_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return throwError(error);
      }));
  }

  public addGroup(newNode: NewNode): Observable<Node> {
    return this.http.post(this.config.baseUrl + '/aim/groups', {
      parentId: newNode.parentId,
      id: newNode.name.slice(0,3),
      name: newNode.name,
      description: ''
    }).pipe(map((res: any) => {
      const node = res.entity;
      return {
        parent: newNode.parentId,
        id: node.id,
        name: newNode.name,
        nthSibling: node.nthSibling
      };
    }), catchError((error: any) =>{
      return throwError(error)
    }));
  }

  public deleteGroup(id): Observable<string[]> {
    return this.http.delete(this.config.baseUrl + '/aim/groups/' + id)
      .pipe(map((res: any) => {
        this.snackbar.open(this.DELETE_NODE_SUCCESS_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return res.entity;
      }), catchError((error: any) => {
        this.snackbar.open(this.DELETE_NODE_FAILURE_MSG, this.SNACKBAR_ACTION_CLOSE, this.SNACKBAR_CLOSE_DURATION);
        return throwError(error);
      }));
  }

  public updateGroupName(id: string, newName: string): Observable<Node> {
    return this.http.patch(this.config.baseUrl + '/aim/groups/' + id, {name: newName})
      .pipe(map((res: any) => {
        return res;
      }));
  }

  public moveUserGroup(groupId: string, userId: string, roleId: string) {
    return this.http.patch(this.config.baseUrl + '/aim/memberships/', {groupId, userId, roleId}).pipe(map((res: any) => {
      return res;
    }));
  }

  private pushNode(node: any[]): Node[] {
    const nods: Node[] = [];
    for (const theNode of node) {
      nods.push({
        parent: theNode.parent,
        id: theNode.id,
        name: theNode.name,
        nthSibling: theNode.nthSibling,
        children: theNode.leaf ? [] : this.pushNode(theNode.children)
      });
    }
    return nods;
  }
}

