import {BrowserModule} from '@angular/platform-browser';
import {CUSTOM_ELEMENTS_SCHEMA, ModuleWithProviders, NgModule} from '@angular/core';

import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {StoreModule} from '@ngrx/store';
import {AuthenticationService} from './authentication/services/authentication.service';
import {RouteGuardService} from './authentication/services/route-guard.service';
import {CustomHttpInterceptor} from './authentication/services/custom-http-interceptor.service';
import {rootReducer} from './statemanagement/rootReducer';
import {ConfirmDialogComponent} from './component/confirm-dialog/confirm-dialog.component';
import {PermissionService} from './authorization/permission/permission.service';
import {GroupManagementService} from './group-management/services/group-management.service';
import {UserCardViewComponent} from './group-management/components/user-card-view/user-card-view.component';
import {GroupManagementContainerComponent} from './group-management/containers/group-management-container/group-management-container.component';
import {GroupManagementSandboxService} from './group-management/group-management-sandbox.service';
import {AuthenticationSandboxService} from './authentication/authentication-sandbox.service';
import {CanAccessDirective} from './authorization/directives/can-access.directive';
import {CommonModule} from '@angular/common';
import {MatTreeModule} from '@angular/material/tree';
import {DragDropModule} from '@angular/cdk/drag-drop';
import {MatInputModule} from '@angular/material/input';
import {MatMenuModule} from '@angular/material/menu';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatSelectModule} from '@angular/material/select';
import {MatCardModule} from '@angular/material/card';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatDividerModule} from '@angular/material/divider';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {TreeViewContainerComponent} from './group-management/containers/tree-view-container/tree-view-container.component';
import {ErinLoaderComponent} from './component/loader/loader.component';
import {MatDialogModule} from '@angular/material/dialog';
import {UserSearchFieldComponent} from './group-management/components/search/user-search-field.component';
import {ErinChipField} from './component/erin-chip-field/erin-chip-field.component';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatChipsModule} from '@angular/material/chips';
import {DropdownComponent} from './component/dropdown/dropdown.component';
import {AIM_CONFIG} from './aim.config.token';
import {AimConfig} from './aim.config';
import {UserCardFilter} from './group-management/components/user-card-view/filter/user-card-filter.component';
import {UserManagementContainerComponent} from './user-management/containers/user-management-container/user-management-container.component';
import {UserDialogComponent} from './user-management/components/user-dialog/user-dialog.component';
import {MatRadioModule} from '@angular/material/radio';
import {UserManagementService} from './user-management/services/user-management.service';
import {ErinTableComponent} from './component/erin-table/erin-table.component';
import {MatTableModule} from '@angular/material/table';
import {CdkTableModule} from '@angular/cdk/table';
import {ErinPaginationComponent} from './component/erin-pagination/erin-pagination.component';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {ErinStatusComponent} from './component/erin-status/erin-status.component';
import {MatExpansionModule} from '@angular/material/expansion';
import {UserProfileContainerComponent} from './user-profile/containers/user-profile-container/user-profile-container.component';
import {MatGridListModule} from "@angular/material/grid-list";

@NgModule({
  declarations: [
    UserSearchFieldComponent,
    ConfirmDialogComponent,
    GroupManagementContainerComponent,
    CanAccessDirective,
    UserCardFilter,
    UserCardViewComponent,
    TreeViewContainerComponent,
    ErinLoaderComponent,
    ErinChipField,
    DropdownComponent,
    UserManagementContainerComponent,
    UserDialogComponent,
    ConfirmDialogComponent,
    ErinTableComponent,
    ErinPaginationComponent,
    ErinStatusComponent,
    UserProfileContainerComponent
  ],
  imports: [
    BrowserAnimationsModule,
    HttpClientModule,
    MatAutocompleteModule,
    MatChipsModule,
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatTreeModule,
    DragDropModule,
    MatDialogModule,
    MatInputModule,
    MatMenuModule,
    MatSidenavModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatSelectModule,
    MatCardModule,
    MatExpansionModule,
    MatDividerModule,
    BrowserModule,
    MatSnackBarModule,
    StoreModule.forRoot(rootReducer),
    MatRadioModule,
    MatProgressSpinnerModule,
    MatTableModule,
    CdkTableModule,
    MatPaginatorModule,
    MatCheckboxModule,
    MatExpansionModule,
    MatGridListModule,
  ],
  providers: [
    PermissionService,
    GroupManagementService,
    AuthenticationService,
    GroupManagementSandboxService,
    AuthenticationSandboxService,
    RouteGuardService,
    UserManagementService,
    {provide: HTTP_INTERCEPTORS, useClass: CustomHttpInterceptor, multi: true}
  ],
  entryComponents: [
    ConfirmDialogComponent,
    UserDialogComponent,
    ErinLoaderComponent
  ],
  exports: [
    GroupManagementContainerComponent,
    UserManagementContainerComponent,
    UserProfileContainerComponent,
    CanAccessDirective,
    ErinTableComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AimModule {
  static forRoot(aimConfig: AimConfig): ModuleWithProviders {
    return {
      ngModule: AimModule,
      providers: [
        {
          provide: AIM_CONFIG,
          useValue: aimConfig
        }
      ]
    };
  }
}

