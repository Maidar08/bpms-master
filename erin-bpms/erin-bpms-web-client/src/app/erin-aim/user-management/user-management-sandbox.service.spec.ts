import {TestBed} from '@angular/core/testing';

import {UserManagementSandboxService} from './user-management-sandbox.service';
import {UserManagementService} from "./services/user-management.service";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import {AIM_CONFIG} from "../aim.config.token";
import {AimConfig} from "../aim.config";

describe('UserManagementSandboxService', () => {
  let service: UserManagementSandboxService;

  beforeEach(() => {
    const aimConfig: AimConfig = {baseUrl: '', tenantId: ''};
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        MatSnackBarModule,
      ],
      providers: [
        HttpClient,
        MatSnackBar,
        {provide: AIM_CONFIG, useValue: aimConfig},
        UserManagementService
      ]
    }).compileComponents();
    service = TestBed.inject(UserManagementSandboxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
