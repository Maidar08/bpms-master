import {Injectable} from '@angular/core';
import {ComponentAction} from '../../aim.model';
import {PermissionItem} from '../../authentication/models/auth.model';
import {AuthenticationSandboxService} from '../../authentication/authentication-sandbox.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private authPermission$ = this.sb.authPermission$;
  private permissions: PermissionItem[] = [];

  constructor(private sb: AuthenticationSandboxService) {
    this.authPermission$.subscribe(res => this.permissions = res);
  }

  public isPermittedAction(permId: string): boolean {
    const permissionItem = this.permissions.find(item => item.id === permId || item.id.includes('*'));
    return (permissionItem != null);
  }

  public isPermittedModule(permId: string): boolean {
    const permissionItem = this.permissions.find(item => item.id === permId || item.id.includes('*'));
    return (permissionItem != null);
  }

  public getPermittedModules(modules: any[]): any[] {
    const permittedModules = [];
    for (const module of modules) {
      if (this.isPermittedModule(module.id)) {
        permittedModules.push(module);
      }
    }
    return permittedModules;
  }

  public getPermittedActions(actions: ComponentAction[]): ComponentAction[] {
    const permittedActions: ComponentAction[] = [];
    for (const action of actions) {
      if (this.isPermittedAction(action.id)) {
        permittedActions.push(action);
      }
    }
    return permittedActions;
  }
}

