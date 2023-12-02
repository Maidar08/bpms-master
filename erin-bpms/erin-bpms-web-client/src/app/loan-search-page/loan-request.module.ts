import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MaterialModule} from '../material.module';
import {TranslateModule} from '@ngx-translate/core';
import {MatRadioModule} from '@angular/material/radio';
import {_MatMenuDirectivesModule, MatMenuModule} from '@angular/material/menu';
import {CreateRequestDialogComponent} from './container/create-request-dialog/create-request-dialog.component';
import {OverlayModule} from '@angular/cdk/overlay';
import {CdkTableModule} from '@angular/cdk/table';
import {ResultTableComponent} from './component/result-table/result-table.component';
import {SearchInputComponent} from './component/search-input/search-input.component';
import {MyLoanRequestTableComponent} from './container/my-loan-request-table/my-loan-request-table.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {BranchRequestPageComponent} from './container/branch-request-page/branch-request-page.component';
import {AllRequestPageComponent} from './container/all-request-page/all-request-page.component';
import {EbankRequestPageComponent} from './container/ebank-request-page/ebank-request-page.component';
import {ChangeAssigneeDialogComponent} from './component/change-assignee-dialog/change-assignee-dialog.component';
import {DashboardComponent} from './component/dashboard/dashboard.component';
import {RouterModule} from '@angular/router';
import {SubGroupRequestPageComponent} from './container/sub-group-request-page/sub-group-request-page.component';
import {SearchCustomerComponent} from './container/search-customer/search-customer.component';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {DownloadExcelComponent} from './component/download-excel/download-excel.component';
import {ConfirmDeleteDialogComponent} from './component/confirm-delete-dialog/confirm-delete-dialog.component';
import {ErinCommonModule} from '../common/erin-common.module';
import {CaseView} from '../case-view-page/case-view.module';
import {OrganizationRegistrationComponent} from './container/organization-registration/organization-registration.component';
import {LoanContractPageComponent} from "./container/loan-contract-page/loan-contract-page.component";
import {LoanContract} from "../loan-contract/loan-contract.module";
import {OrganizationRegistration} from "../organization-registration/model/organization-registration.module";

@NgModule({
  declarations: [
    ConfirmDeleteDialogComponent,
    CreateRequestDialogComponent,
    ResultTableComponent,
    SearchInputComponent,
    MyLoanRequestTableComponent,
    BranchRequestPageComponent,
    AllRequestPageComponent,
    EbankRequestPageComponent,
    ChangeAssigneeDialogComponent,
    DashboardComponent,
    SubGroupRequestPageComponent,
    DownloadExcelComponent,
    SearchCustomerComponent,
    LoanContractPageComponent,
    OrganizationRegistrationComponent,
  ],
  imports: [
    BrowserAnimationsModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    TranslateModule,
    MatRadioModule,
    _MatMenuDirectivesModule,
    MatMenuModule,
    CdkTableModule,
    MatMenuModule,
    MatAutocompleteModule,
    OverlayModule,
    RouterModule,
    ErinCommonModule,
    CaseView,
    LoanContract,
    OrganizationRegistration,
  ],
  exports: [
    SearchInputComponent
  ],
  entryComponents: [
    CreateRequestDialogComponent,
  ],
  providers: [
    DatePipe
  ]
})
export class LoanRequest {

}
