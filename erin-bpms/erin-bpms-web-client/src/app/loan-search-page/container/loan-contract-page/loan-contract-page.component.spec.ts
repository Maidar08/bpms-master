import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {LoanContractPageComponent} from "./loan-contract-page.component";
import {CdkTableModule} from "@angular/cdk/table";
import {FormsModule} from "@angular/forms";
import {MatCardModule} from "@angular/material/card";
import {MaterialModule} from "../../../material.module";
import {MatIconModule} from "@angular/material/icon";
import {HttpClientModule} from "@angular/common/http";
import {BrowserAnimationsModule, NoopAnimationsModule} from "@angular/platform-browser/animations";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {StoreModule} from "@ngrx/store";
import {RouterModule} from "@angular/router";
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {DynamicFieldsComponent} from "../../../case-view-page/component/dynamic-fields/dynamic-fields.component";
import {ErinDatepickerFieldComponent} from "../../../common/erin-fields/erin-datepicker-field/erin-datepicker-field.component";
import {ErinDropdownFieldComponent} from "../../../common/erin-fields/erin-dropdown-field/erin-dropdown-field.component";
import {ErinNumberInputFieldComponent} from "../../../common/erin-fields/erin-number-input-field/erin-number-input-field.component";
import {ErinButtonComponent} from "../../../common/erin-fields/erin-button/erin-button.component";
import {ErinLoaderComponent} from "../../../common/erin-loader/erin-loader.component";
import {ErinSimpleInputFieldComponent} from "../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component";


describe('BranchBankingWorkspaceComponent', () => {
  let component: LoanContractPageComponent;
  let fixture: ComponentFixture<LoanContractPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        CdkTableModule,
        MaterialModule,
        FormsModule,
        MatIconModule,
        MatCardModule,
        HttpClientModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
        HttpClientTestingModule,
        StoreModule.forRoot({}),
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        }),
      ],
      declarations: [
        LoanContractPageComponent,
        DynamicFieldsComponent,
        ErinDatepickerFieldComponent,
        ErinDropdownFieldComponent,
        ErinNumberInputFieldComponent,
        ErinSimpleInputFieldComponent,
        ErinLoaderComponent,
        ErinButtonComponent,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoanContractPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
