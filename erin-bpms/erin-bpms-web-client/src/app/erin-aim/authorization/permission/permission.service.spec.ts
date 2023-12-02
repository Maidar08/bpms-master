import {TestBed} from '@angular/core/testing';
import {PermissionService} from './permission.service';
import {provideMockStore} from '@ngrx/store/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {AuthenticationSandboxService} from '../../authentication/authentication-sandbox.service';
import {RouterTestingModule} from '@angular/router/testing';
import {AIM_CONFIG} from "../../aim.config.token";

describe('PermissionService', () => {
  let service: PermissionService;
  let sb: any;
  const aimConfig = {baseUrl: 'base', tenantId: 'tenantId'};
  const initialState = {auth: {permissions: [{
        properties: 'app',
        id: 'app.service'
      }]}};
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        AuthenticationSandboxService,
        {provide: AIM_CONFIG, useValue: aimConfig},
        provideMockStore({initialState})]
    });
    service = TestBed.inject(PermissionService);
    sb = TestBed.inject(AuthenticationSandboxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return true when id does exist', () => {
    const result = service.isPermittedAction('app.service');
    expect(result).toBeTruthy();
  });

  it('should return false when id does not exist', () => {
    const result = service.isPermittedAction('app.filter');
    expect(result).toBeFalsy();
  });

  it('should get permission with existing ids on current permission',  () => {
    const actions = [
      {id: 'app.service', actionIcon: 'apple', action: undefined, actionName: 'service'},
      {id: 'app.delete', actionIcon: 'delete', action: undefined, actionName: 'Delete'}];
    const checkedActions  = service.getPermittedActions(actions);
    expect(checkedActions.length).toEqual(1);
    expect(checkedActions[0].actionName).toEqual('service');
  });
});
