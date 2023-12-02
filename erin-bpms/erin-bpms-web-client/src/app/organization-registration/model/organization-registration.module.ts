import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {OrganizationRegistrationCaseViewComponent} from '../container/organization-registration-case-view/organization-registration-case-view.component';
import {OrganizationRegistrationWorkspaceServices} from '../container/services/organization-registration-workspace-service';
import {ErinCommonModule} from '../../common/erin-common.module';
import {CaseView} from '../../case-view-page/case-view.module';
import {MaterialModule} from '../../material.module';
import {DateRangePickerComponentWithBranch} from "../container/date-range-picker-with-branch/date-range-picker.component-with-branch";

@NgModule({
  imports: [
    MaterialModule,
    BrowserModule,
    CommonModule,
    ErinCommonModule,
    RouterModule,
    CaseView,
    FormsModule,
    ReactiveFormsModule
  ],

  declarations: [OrganizationRegistrationCaseViewComponent, DateRangePickerComponentWithBranch],
  providers: [OrganizationRegistrationWorkspaceServices],
  exports: [
    DateRangePickerComponentWithBranch
  ]
})

export class OrganizationRegistration {

}
