import {NgModule} from '@angular/core';
import {MaterialModule} from '../material.module';
import {BrowserModule} from '@angular/platform-browser';
import {CommonModule} from '@angular/common';
import {ErinCommonModule} from '../common/erin-common.module';
import {RouterModule} from '@angular/router';
import {CaseView} from '../case-view-page/case-view.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LoanContractCaseViewComponent} from './container/loan-contract-case-view/loan-contract-case-view.component';
import {LoanContractWorkspaceServices} from './container/services/loan-contract-workspace-services';
import {LoanContractDialogComponent} from './component/loan-contract-dialog/loan-contract-dialog.component';
import {DateRangePickerComponent} from './component/date-range-picker/date-range-picker.component';
import {MatDatepickerModule} from '@angular/material/datepicker';

@NgModule({
  imports: [
    MaterialModule,
    BrowserModule,
    CommonModule,
    ErinCommonModule,
    RouterModule,
    CaseView,
    FormsModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatDatepickerModule,
    MatDatepickerModule,
    MatDatepickerModule
  ],

  declarations: [LoanContractCaseViewComponent, LoanContractDialogComponent, DateRangePickerComponent],
  providers: [LoanContractWorkspaceServices],
  exports: [
    DateRangePickerComponent
  ]
})

export class LoanContract {

}
