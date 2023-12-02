import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router} from '@angular/router';
import {AuthenticationService} from './authentication.service';
import {Store} from '@ngrx/store';
import {AuthenticationSandboxService} from '../authentication-sandbox.service';
import {ApplicationState} from '../../statemanagement/state/ApplicationState';
import {ClearAuth, SetAuth} from '../../statemanagement/actions/auth/auth';


@Injectable()
export class RouteGuardService implements CanActivate {
  path: ActivatedRouteSnapshot[];
  protected authRole = this.sb.authRole$;
  protected role: string;

  //TODO: service shouldn't do statemanagement action. Move store action to sandbox.
  constructor(public router: Router, public auth: AuthenticationService,
              private sb: AuthenticationSandboxService, protected store: Store<ApplicationState>) {
    this.authRole.subscribe(res => this.role = res);
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: any): Promise<boolean> {
    return this.canActivate();
  }

  async canActivate(): Promise<boolean> {
    const isLoggedIn = this.auth.isLoggedIn();

    if (!isLoggedIn) {
      //Here it return the result of the session validation.
      //Validation session is labmda function, which means return value defined inside function will return value of function.
      return await this.auth.validateSession().toPromise().then((res) => {
        //TODO: move store action to part to sandbox.
        const action = new SetAuth(res);
        this.store.dispatch(action);
        return true;
      }).catch(() => {
        const action = new ClearAuth();
        this.store.dispatch(action);
        this.router.navigate(['/login']);
        return false;
      });
    }
    return true;
  }
}
