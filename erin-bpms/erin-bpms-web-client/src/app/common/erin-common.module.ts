import {NgModule} from '@angular/core';
import {FileViewerComponent} from './file-viewer/file-viewer.component';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {CommonSandboxService} from './common-sandbox.service';
import {CommonService} from './service/common.service';
import {CommonModule} from '@angular/common';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {ErinAutocompleteFieldComponent} from './erin-fields/erin-autocomplete-field/erin-autocomplete-field.component';
import {ErinDatepickerFieldComponent} from './erin-fields/erin-datepicker-field/erin-datepicker-field.component';
import {ErinDropdownFieldComponent} from './erin-fields/erin-dropdown-field/erin-dropdown-field.component';
import {ErinEmailFieldComponent} from './erin-fields/erin-email-input-field/erin-email-field.component';
import {ErinNumberInputFieldComponent} from './erin-fields/erin-number-input-field/erin-number-input-field.component';
import {ErinPhoneNumberInputFieldComponent} from './erin-fields/erin-phone-number-input-field/erin-phone-number-input-field.component';
import {ErinRegisterNumberInputFieldComponent} from './erin-fields/erin-register-number-input-field/erin-register-number-input-field.component';
import {ErinSimpleInputFieldComponent} from './erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ErinStringButNumberFieldComponent} from './erin-fields/erin-string-but-number-field/erin-string-but-number-field.component';
import {ErinTextareaFieldComponent} from './erin-fields/erin-textarea-input-field/erin-textarea-field.component';
import {ErinWorkerCIFFieldComponent} from './erin-fields/erin-workerCIF-field/erin-workerCIF-field.component';
import {ScanFingerprintFieldComponent} from './erin-fields/scan-fingerprint-field/scan-fingerprint-field.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {SimpleDropdownComponent} from './erin-fields/erin-dropdown-field/simple-dropdown.component';
import {MaterialModule} from '../material.module';
import {ThousandSeparatorDirective} from './directive/thousand-separator.directive';
import {dateFormatValidation} from './directive/date-format.directive';
import {ConfirmDialogComponent} from './confirm-dialog/confirm-dialog.component';
import {ErinLoaderComponent} from './erin-loader/erin-loader.component';
import {TablePaginatorComponent} from './table-paginator/table-paginator.component';
import {ErinSearchFieldComponent} from './erin-search-field/erin-search-field.component';
import {ErinTitleComponent} from './erin-title/erin-title.component';
import {ShowPercentDirective} from './directive/show-percent.directive';
import {ErinPercentFieldComponent} from './erin-fields/erin-percent-field/erin-percent-field.component';
import {ErinYearFieldComponent} from './erin-fields/erin-year-field/erin-year-field.component';
import {ErinTableComponent} from './erin-table/erin-table/erin-table.component';
import {CdkTableModule} from '@angular/cdk/table';
import {AddRemoveBoxComponent} from './erin-fields/add-remove-box/add-remove-box.component';
import {ErinNumberMaxValueInputFieldComponent} from './erin-fields/erin-number-max-value-input-field/erin-number-max-value-input-field.component';
import {ErinTreeNode} from './erin-tree/erin-tree-node.component';
import {ErinSubtitleComponent} from './erin-subtitle/erin-subtitle.component';
import {ErinFieldHintComponent} from './erin-field-hint/erin-field-hint.component';
import {ErinButtonComponent} from './erin-fields/erin-button/erin-button.component';
import {ErinCustomDialogComponent} from './erin-custom-dialog/erin-custom-dialog.component';
import {ErinCheckboxFieldComponent} from './erin-fields/erin-checkbox-field/erin-checkbox-field.component';
import {ErinUploadFileButtonComponent} from './erin-fields/erin-uploadFile-button/erin-uploadFile-button.component';
import {ErinChipsComponent} from './erin-fields/erin-chips/erin-chips.component';


@NgModule({
  declarations: [
    FileViewerComponent,
    ErinAutocompleteFieldComponent,
    ErinDatepickerFieldComponent,
    ErinDropdownFieldComponent,
    ErinEmailFieldComponent,
    ErinNumberInputFieldComponent,
    ErinNumberMaxValueInputFieldComponent,
    ErinPhoneNumberInputFieldComponent,
    ErinRegisterNumberInputFieldComponent,
    ErinSimpleInputFieldComponent,
    ErinStringButNumberFieldComponent,
    ErinTextareaFieldComponent,
    ErinWorkerCIFFieldComponent,
    ScanFingerprintFieldComponent,
    SimpleDropdownComponent,
    ThousandSeparatorDirective,
    dateFormatValidation,
    ConfirmDialogComponent,
    ErinLoaderComponent,
    TablePaginatorComponent,
    ErinSearchFieldComponent,
    ErinTitleComponent,
    ShowPercentDirective,
    ErinPercentFieldComponent,
    ErinYearFieldComponent,
    ErinTableComponent,
    AddRemoveBoxComponent,
    ErinTreeNode,
    ErinButtonComponent,
    AddRemoveBoxComponent,
    ErinSubtitleComponent,
    ErinFieldHintComponent,
    ErinCustomDialogComponent,
    ErinCheckboxFieldComponent,
    ErinUploadFileButtonComponent,
    ErinChipsComponent
  ],
  exports: [
    FileViewerComponent,
    ErinAutocompleteFieldComponent,
    ErinDatepickerFieldComponent,
    ErinDropdownFieldComponent,
    ErinEmailFieldComponent,
    ErinNumberInputFieldComponent,
    ErinPhoneNumberInputFieldComponent,
    ErinRegisterNumberInputFieldComponent,
    ErinSimpleInputFieldComponent,
    ErinStringButNumberFieldComponent,
    ErinTextareaFieldComponent,
    ErinWorkerCIFFieldComponent,
    ScanFingerprintFieldComponent,
    SimpleDropdownComponent,
    ThousandSeparatorDirective,
    ShowPercentDirective,
    dateFormatValidation,
    ConfirmDialogComponent,
    ErinLoaderComponent,
    TablePaginatorComponent,
    ErinSearchFieldComponent,
    ErinTitleComponent,
    ErinPercentFieldComponent,
    ErinYearFieldComponent,
    ErinTableComponent,
    AddRemoveBoxComponent,
    ErinNumberMaxValueInputFieldComponent,
    ErinTreeNode,
    ErinNumberMaxValueInputFieldComponent,
    ErinSubtitleComponent,
    ErinButtonComponent,
    ErinCheckboxFieldComponent,
    ErinUploadFileButtonComponent,
    ErinChipsComponent
  ],
    imports: [
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        CommonModule,
        ReactiveFormsModule,
        MatAutocompleteModule,
        MaterialModule,
        FormsModule,
        CdkTableModule
    ],
  entryComponents: [
    ConfirmDialogComponent,
    ErinLoaderComponent,
  ],
  providers: [CommonSandboxService, CommonService]
})
export class ErinCommonModule {
}
