import {TestBed} from '@angular/core/testing';

import {UserManagementService} from './user-management.service';
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import {AIM_CONFIG} from "../../aim.config.token";
import {AimConfig} from "../../aim.config";

describe('UserManagementService', () => {
  let service: UserManagementService;

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
      ]
    }).compileComponents();
    service = TestBed.inject(UserManagementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
