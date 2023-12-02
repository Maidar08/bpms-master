import {TestBed} from '@angular/core/testing';
import {AuthenticationService} from './authentication.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {Router} from '@angular/router';
import {Component} from '@angular/core';
import {AuthModel} from '../models/auth.model';
import {AIM_CONFIG} from '../../aim.config.token';


@Component({
  template: ''
})
class BlankComponent {
}

describe('AuthenticationService', () => {
  let service: AuthenticationService;
  let httpMock;
  let router;
  let sessionSetSpy;
  const postMethod = 'POST';
  const testText = 'admin';
  const testTextSupervisor = 'supervisor';
  const testTextUser = 'user';
  const aimConfig = { baseUrl: 'base', tenantId: 'tenantId'};


  const authTestModelAdmin: AuthModel = {
    permissions: [{
      id: 'id',
      properties: 'id'
    }],
    userGroup: 'group',
    userName: 'admin',
    role: 'ADMIN'
  };
  const authTestModelSupervisor: AuthModel = {
    permissions: [{
      id: 'id',
      properties: 'id'
    }],
    userGroup: 'group',
    userName: 'supervisor',
    role: 'SUPERVISOR'
  };
  const authTestModelUser: AuthModel = {
    permissions: [{
      properties: 'id',
      id: 'id'
    }],
    userGroup: 'group',
    userName: 'user',
    role: 'User'
  };
  const responseModel = {
    entity: {
      userName: 'admin',
      group: 'group',
      role: 'ADMIN',
      permissions: [{
        id: 'id',
        properties: 'id'
      }]
    }
  };
  const responseModelSupervisor = {
    entity: {
      group: 'group',
      role: 'SUPERVISOR',
      permissions: [{
        id: 'id',
        properties: 'id'
      }]
    }
  };
  const responseModelUser = {
    entity: {
      group: 'group',
      role: 'User',
      permissions: [{
        id: 'id',
        properties: 'id'
      }]
    }
  };
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([{path: 'url', component: BlankComponent}])],
      declarations: [BlankComponent],
      providers: [{ provide: AIM_CONFIG, useValue: aimConfig}]
    });
    service = TestBed.inject(AuthenticationService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    sessionSetSpy = spyOn(sessionStorage, 'setItem');
    sessionSetSpy.and.callFake(() => {
    });
    spyOn(router, 'navigateByUrl').and.callFake(() => {
    });
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create body and change status on success when role is admin', () => {
    service.login(testText, testText, 'url').subscribe(res => {
      expect(res).toBeTruthy();
      expect(res).toEqual(authTestModelAdmin);
      expect(router.navigateByUrl).toHaveBeenCalledWith('url');
      const loggedIn = service.isLoggedIn();
      expect(loggedIn).toBeTruthy();
    });
    const req = httpMock.expectOne({method: postMethod});
    expect(req.request.url).toEqual('base/aim/login');
    expect(req.request.body).toEqual({userId: testText, password: testText, tenantId: 'tenantId'});
    req.flush(responseModel, {status: 200, statusText: 'ok'});
  });

  it('should create body and change status on success when role is supervisor', () => {
    service.login(testTextSupervisor, testTextSupervisor, 'url_s').subscribe(res => {
      expect(res).toBeTruthy();
      expect(res).toEqual(authTestModelSupervisor);
      expect(router.navigateByUrl).toHaveBeenCalledWith('url_s');
      const loggedIn = service.isLoggedIn();
      expect(loggedIn).toBeTruthy();
    });
    const req = httpMock.expectOne({method: postMethod});
    expect(req.request.url).toEqual('base/aim/login');
    expect(req.request.body).toEqual({userId: 'supervisor', password: 'supervisor', tenantId: 'tenantId'});
    req.flush(responseModelSupervisor, {status: 200, statusText: 'ok'});
  });

  it('should create body and change status on success when role is employee', () => {
    service.login(testTextUser, testTextUser, 'url').subscribe(res => {
      expect(res).toBeTruthy();
      expect(res).toEqual(authTestModelUser);
      expect(router.navigateByUrl).toHaveBeenCalledWith('url');
      const loggedIn = service.isLoggedIn();
      expect(loggedIn).toBeTruthy();
    });
    const req = httpMock.expectOne({method: postMethod});
    expect(req.request.url).toEqual('base/aim/login');
    expect(req.request.body).toEqual({userId: 'user', password: 'user', tenantId: 'tenantId'});
    req.flush(responseModelUser, {status: 200, statusText: 'ok'});
  });

  it('should change status to false on login fail and throw error message on not 400 or 401 status code', () => {
    service.login(testText, testText, 'url').subscribe((res) => {
    }, (error1: any) => {
      expect(error1).toEqual('Холболт амжилтгүй.');
      const loggedIn = service.isLoggedIn();
      expect(loggedIn).toBeFalsy();
    });
    const req = httpMock.expectOne({method: postMethod});
    req.flush({}, {status: 500, statusText: 'Bad Request'});
  });

  it('should change status to false on login fail and throw error message on 400 or 401 status code', () => {
    service.login(testText, testText, 'url').subscribe(() => {
    }, error1 => {
      expect(error1).toEqual('Хэрэглэгчийн нэр эсвэл нууц үг буруу байна.');
      const loggedIn = service.isLoggedIn();
      expect(loggedIn).toBeFalsy();
    });
    const req = httpMock.expectOne({method: 'POST'});
    req.flush({}, {status: 400, statusText: 'Bad Request'});
  });

  it('should return current login status', () => {
    const status = service.isLoggedIn();
    expect(status).toBeFalsy();
  });

  it('should set login status false and re route to login', () => {
    service.logout();
    const status = service.isLoggedIn();
    expect(status).toBeFalsy();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });

  it('should validate session', () => {
    service.validateSession().subscribe(res => {
      expect(res).toBeTruthy();
      expect(res.userName).toEqual('admin');
    });
    const req = httpMock.expectOne({method: 'GET'});
    expect(req.request.url).toEqual('base/aim/validate-session');
    req.flush(responseModel, {status: 200, statusText: 'ok'});
  });

  it('should have false logged in on check login status error', () => {
    service.validateSession().subscribe(() => {
    }, error1 => {
      expect(error1).toBeTruthy();
      const loggedIn = service.isLoggedIn();
      expect(loggedIn).toBeFalsy();
    });
    const req = httpMock.expectOne({method: 'GET'});
    req.flush({}, {status: 500, statusText: 'error'});
  });

  it('should logout', () => {
    service.logout();
    expect(service.isLoggedIn()).toBeFalsy();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
    const req = httpMock.expectOne({method: 'GET'});
    expect(req.request.url).toEqual('base/aim/logout');
    req.flush({}, {status: 200, statusText: 'ok'});
  });

  it('should navigate to redirect url', () => {
    service.login(testText, testText, '/123').subscribe(() => {
      expect(router.navigateByUrl).toHaveBeenCalledWith('/123');
    });
    const req = httpMock.expectOne({method: postMethod});
    expect(req.request.url).toEqual('base/aim/login');
    expect(req.request.body).toEqual({userId: 'admin', password: 'admin', tenantId: 'tenantId'});
    req.flush(responseModel, {status: 200, statusText: 'ok'});
  });
});
