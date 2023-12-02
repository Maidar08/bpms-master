import {NgModule} from '@angular/core';
import {MaterialModule} from '../material.module';
import {BrowserModule} from '@angular/platform-browser';
import {CommonModule} from '@angular/common';
import {ErinCommonModule} from '../common/erin-common.module';
import {CaseView} from '../case-view-page/case-view.module';
import {RouterModule} from '@angular/router';
import {BranchBankingWorkspaceComponent} from './contrainer/branch-banking-workspace/branch-banking-workspace.component';
import {BranchBankingCaseViewComponent} from './contrainer/branch-banking-case-view/branch-banking-case-view.component';
import {BranchBankingInitialViewComponent} from './contrainer/branch-banking-initial-view/branch-banking-initial-view.component';
import {VerifyOtpDialogComponent} from './contrainer/verify-otp-dialog/verify-otp-dialog.component';
import {FormsModule} from "@angular/forms";

@NgModule({
  imports: [
    MaterialModule,
    BrowserModule,
    CommonModule,
    ErinCommonModule,
    RouterModule,
    CaseView,
    FormsModule
  ],
  exports: [
  ],

  declarations: [BranchBankingWorkspaceComponent, BranchBankingCaseViewComponent, BranchBankingInitialViewComponent, VerifyOtpDialogComponent]
})

export class BranchBanking {

}
