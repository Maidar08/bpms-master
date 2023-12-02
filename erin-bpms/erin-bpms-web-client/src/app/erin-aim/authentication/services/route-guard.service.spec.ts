import {fakeAsync, TestBed} from '@angular/core/testing';
import {RouteGuardService} from './route-guard.service';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {provideMockStore} from '@ngrx/store/testing';
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot} from '@angular/router';
import {AuthenticationSandboxService} from '../authentication-sandbox.service';
import {AIM_CONFIG} from '../../aim.config.token';
import {of, throwError} from 'rxjs';
import {AuthModel} from '../models/auth.model';

class RouterStub {
  url = '';

  navigate(commands: any[], extras?: any) {
  }
}

describe('RouteGuardService', () => {
  const initialState = {auth: {role: ['LMS_Admin']}};
  let service: RouteGuardService;
  let sb: AuthenticationSandboxService;
  let route: ActivatedRouteSnapshot;
  // TODO: update ngrx module v9.0+ and check store related action here by checking method is called or not.
  // let store: MockStore<any>;
  const testData: AuthModel = {
    userName: 'kakashi',
    role: 'shinobi',
    permissions: [],
    userGroup: 'Hatake'
  };
  const aimConfig = {baseUrl: 'base', tenantId: 'tenantId'};
  const mockSnapshot: any = jasmine.createSpyObj<RouterStateSnapshot>('RouterStateSnapshot', ['toString']);

  beforeEach(() => TestBed.configureTestingModule({
    imports: [RouterTestingModule, HttpClientTestingModule],
    providers: [RouteGuardService, AuthenticationSandboxService, provideMockStore({initialState}),
      {provide: ActivatedRouteSnapshot, useValue: {data: {role: ['Zombie']}}},
      {provide: Router, useClass: RouterStub},
      {provide: RouterStateSnapshot, useValue: mockSnapshot},
      {provide: AIM_CONFIG, useValue: aimConfig}]
  }));
  beforeEach(() => {
    service = TestBed.inject(RouteGuardService);
    sb = TestBed.inject(AuthenticationSandboxService);
    route = TestBed.inject(ActivatedRouteSnapshot);
    // store = TestBed.inject(MockStore);
  });
  it('should be created', fakeAsync(() => {
    expect(service).toBeTruthy();
  }));
  it('should navigate to the login page when user not logged in', fakeAsync(() => {
    spyOn(service.auth, 'isLoggedIn').and.returnValue(false);
    service.canActivate();
    expect(service.router.navigate(['/login']));
  }));
  it('should return true when the user already has logged in', fakeAsync(() => {
    spyOn(service.auth, 'isLoggedIn').and.returnValue(true);
    service.canActivate();
    expect(route.data.role).not.toEqual(initialState.auth.role);
    service.auth.isLoggedIn();
    service.canActivate().then(canActivate => expect(canActivate).toBeTruthy());
  }));

  it('should return true when the user try to log in', fakeAsync(() => {
    spyOn(service.auth, 'isLoggedIn').and.returnValue(false);
    spyOn(service.auth, 'validateSession').and.returnValue(of(testData));
    service.canActivate().then(canActivate => expect(canActivate).toBeTruthy());
  }));

  it('should return false when !isLoggedIn and validateSession is false', fakeAsync(() => {
    spyOn(service.auth, 'isLoggedIn').and.returnValue(false);
    spyOn(service.auth, 'validateSession').and.callFake(() => {
      return throwError(new Error('Fake error'));
    });
    service.canActivate().then(canActivate => {
      expect(canActivate).toBeFalsy();
    });
  }));
});
