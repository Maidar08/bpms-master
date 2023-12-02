import {ComponentFixture, TestBed} from '@angular/core/testing';
import {UserManagementContainerComponent} from "./user-management-container.component";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {UserManagementSandboxService} from "../../user-management-sandbox.service";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import {AIM_CONFIG} from "../../../aim.config.token";
import {UserManagementService} from "../../services/user-management.service";
import {AimConfig} from "../../../aim.config";
import {ErinTableComponent} from "../../../component/erin-table/erin-table.component";
import {MatTableModule} from "@angular/material/table";
import {CdkTableModule} from "@angular/cdk/table";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatIconModule} from "@angular/material/icon";
import {ErinStatusComponent} from "../../../component/erin-status/erin-status.component";
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatPaginatorModule} from "@angular/material/paginator";

describe('UserManagementContainerComponent', () => {
  let component: UserManagementContainerComponent;
  let fixture: ComponentFixture<UserManagementContainerComponent>;

  beforeEach(() => {
    const aimConfig: AimConfig = {baseUrl: '', tenantId: ''};
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        MatSnackBarModule,
        MatDialogModule,
        MatTableModule,
        CdkTableModule,
        MatTooltipModule,
        MatPaginatorModule,
        MatCheckboxModule,
        MatIconModule
      ],
      providers: [
        HttpClient,
        MatSnackBar,
        {provide: AIM_CONFIG, useValue: aimConfig},
        UserManagementService,
        MatDialog,
        UserManagementSandboxService,
      ],
      declarations: [UserManagementContainerComponent, ErinTableComponent, ErinStatusComponent]
    }).compileComponents()
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserManagementContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    fixture.destroy();
    component = null;
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });

});
