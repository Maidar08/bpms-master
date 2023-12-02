import {NgModule} from '@angular/core';
import {CoBorrowerDeleteComponent} from './component/coBorrower-delete/coBorrower-delete.component';
import {ConfirmRemoveCoBorrowerDialogComponent} from './component/confirm-remove-coBorrower-dialog/confirm-remove-coBorrower-dialog.component';
import {DocumentationTableComponent} from './component/documentation-table/documentation-table.component';
import {DynamicFieldsComponent} from './component/dynamic-fields/dynamic-fields.component';
import {LoanContractComponent} from './component/loan-contract/loan-contract.component';
import {SalaryCalculationTableComponent} from './component/salary-calculation-table/salary-calculation-table.component';
import {TaskListDashletComponent} from './component/task-list-dashlet/task-list-dashlet.component';
import {MaterialModule} from '../material.module';
import {CommonModule} from '@angular/common';
import {ErinCommonModule} from '../common/erin-common.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {TranslateModule} from '@ngx-translate/core';
import {CdkTableModule} from '@angular/cdk/table';
import {BasicFieldViewComponent} from './container/basic-field-view/basic-field-view.component';
import {CaseFolderComponent} from './container/case-folder/case-folder.component';
import {CaseViewComponent} from './container/case-view/case-view.component';
import {SalaryTableComponent} from './container/salary-table/salary-table.component';
import {TaskDashletsContainerComponent} from './container/task-dashlets-container/task-dashlets-container.component';
import {UploadFileDialogComponent} from './container/upload-file-dialog/upload-file-dialog.component';
import {NoteTableComponent} from './component/note-table/note-table.component';
import {CollateralTableComponent} from './component/collateral-table/collateral-table.component';
import {CollateralListPageComponent} from './container/collateral-list-page/collateral-list-page.component';
import {FlexibleFormComponent} from './component/flexible-form/flexible-form.component';
import {UpdateTaskFormComponent} from './component/update-task-form/update-task-form.component';
import {CreateAccountWithCollateralComponent} from './component/create-account-with-collateral/create-account-with-collateral.component';
import {MatDialogModule} from '@angular/material/dialog';
import {CollateralUdfDialogComponent} from './container/collateral-udf-dialog/collateral-udf-dialog.component';
import {DynamicColumnsComponent} from './component/dynamic-columns/dynamic-columns.component';
import {BalanceCalculationComponent} from './container/calculation/balance-calculation/balance-calculation.component';
import {DynamicFormContainerComponent} from './component/dynamic-form-container/dynamic-form-container.component';
import {MainWorkspaceNewComponent} from './container/main-workspace-new/main-workspace-new.component';
import {GroupedTaskListDashletComponent} from './component/sub-moduled-task-list-dashlet/grouped-task-list-dashlet.component';
import {ErinDropdownFieldWithCheckboxComponent} from '../common/erin-fields/erin-dropdown-field-with-checkbox/erin-dropdown-field-with-checkbox.component';
import {MainWorkspaceComponent} from './container/main-workspace/main-workspace.component';

@NgModule({
  declarations: [
    CoBorrowerDeleteComponent,
    ConfirmRemoveCoBorrowerDialogComponent,
    DocumentationTableComponent,
    DynamicFieldsComponent,
    LoanContractComponent,
    DynamicColumnsComponent,
    SalaryCalculationTableComponent,
    TaskListDashletComponent,
    BasicFieldViewComponent,
    CaseFolderComponent,
    CaseViewComponent,
    SalaryTableComponent,
    TaskDashletsContainerComponent,
    UploadFileDialogComponent,
    NoteTableComponent,
    CollateralTableComponent,
    CollateralListPageComponent,
    CollateralListPageComponent,
    FlexibleFormComponent,
    UpdateTaskFormComponent,
    FlexibleFormComponent,
    CreateAccountWithCollateralComponent,
    FlexibleFormComponent,
    CollateralUdfDialogComponent,
    BalanceCalculationComponent,
    DynamicFormContainerComponent,
    MainWorkspaceNewComponent,
    GroupedTaskListDashletComponent,
    ErinDropdownFieldWithCheckboxComponent,
    MainWorkspaceComponent
  ],
    exports: [
        DynamicFieldsComponent,
        LoanContractComponent,
        CoBorrowerDeleteComponent,
        TaskListDashletComponent,
        DocumentationTableComponent,
        SalaryCalculationTableComponent,
        TaskDashletsContainerComponent,
        DynamicFormContainerComponent,
        BasicFieldViewComponent,
        CaseFolderComponent,
        MainWorkspaceNewComponent,
        MainWorkspaceComponent,
        ErinDropdownFieldWithCheckboxComponent
    ],
  imports: [
    MaterialModule,
    CommonModule,
    MatDialogModule,
    ErinCommonModule,
    FormsModule,
    TranslateModule,
    CdkTableModule,
    ReactiveFormsModule,
  ],
  entryComponents: [
    UploadFileDialogComponent, CollateralUdfDialogComponent
  ],
})

export class CaseView {
}
