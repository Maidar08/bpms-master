import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {ApplicationState} from '../statemanagement/state/ApplicationState';
import {select, Store} from '@ngrx/store';
import {AuthenticationService} from './services/authentication.service';
import {SetAuth} from '../statemanagement/actions/auth/auth';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationSandboxService {
  authPermission$ = this.store.pipe(select(state => {
    return state.auth.permissions;
  }));
  authRole$ = this.store.pipe(select(state => {
    if (state.auth) {
      return state.auth.role;
    }
    return undefined;
  }));
  authName$ = this.store.pipe(select(state => {
    if (state.auth) {
      return state.auth.userName;
    }
  }));
  authGroup$ = this.store.pipe(select(state => {
    if (state.auth) {
      return state.auth.userGroup;
    }
  }));

  constructor(private authService: AuthenticationService, private store: Store<ApplicationState>) {
  }

  public login(username: string, password: string, url: string): Observable<any> {
    return this.authService.login(username, password, url).pipe(map(res => {
      this.store.dispatch(new SetAuth(res));
      return res;
    }));
  }

  getCurrentUserName() {
    return this.authName$;
  }
  getCurrentUserGroup() {
    return this.authGroup$;
  }

  getCurrentUserRole() {
    return this.authRole$;
  }

  getCurrentUserPermission() {
    return this.authPermission$;
  }

  public logout() {
    return this.authService.logout();
  }
}
