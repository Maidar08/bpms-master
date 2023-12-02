import {Injectable} from '@angular/core';
import {AuthenticationSandboxService} from '../erin-aim/authentication/authentication-sandbox.service';
import {PermissionService} from '../erin-aim/authorization/permission/permission.service';
import {DialogService} from '../services/dialog.service';
import {MenuNode} from './navigation.model';
import {NAV_ITEMS} from '../app.const';

@Injectable({
  providedIn: 'root'
})
export class NavigationSandboxService {
  username: string;
  userGroup: string;
  currentNavigations = [];

  constructor(
    private permissionService: PermissionService,
    private dialogService: DialogService,
    private auth: AuthenticationSandboxService) {
    auth.getCurrentUserName().subscribe(res => this.username = res);
    auth.getCurrentUserGroup().subscribe(res => this.userGroup = res);

  }

  getUserName(): string {
    return this.username;
  }
  getUserGroup(): string {
    return this.userGroup;
  }

  logout() {
    this.auth.logout();
  }

  getPermittedNavItems(): MenuNode[] {
    const permittedItems: MenuNode[] = [];
    for (const item of NAV_ITEMS) {
      if (this.permissionService.isPermittedAction(item.id) || item.id === undefined) {
        const navItem = {
          icon: item.icon, name: item.name, id: item.id, route: item.route, children: []
        };
        for (const child of item.children) {
          if (this.permissionService.isPermittedAction(child.id) || child.id === undefined) {
            navItem.children.push(child);
          }
        }
        if (navItem.children.length === 0 && item.id === undefined) {  } else { permittedItems.push(navItem); }
      }
    }
    this.currentNavigations = permittedItems;
    return permittedItems;
  }

  openRequestDialog() {
    this.dialogService.openProcessDialog();
  }

  getFirstPermittedRoute() {
    for (const item of NAV_ITEMS[0].children) {
      if (this.permissionService.isPermittedAction(item.id) || item.id === undefined) {
        return item.route;
      }
    }
    return undefined;
  }
}
