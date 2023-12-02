import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CreateRequestDialogComponent} from './create-request-dialog.component';
import {MaterialModule} from '../../../material.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of, throwError} from 'rxjs';
import {By} from '@angular/platform-browser';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {DynamicFieldsComponent} from '../../../case-view-page/component/dynamic-fields/dynamic-fields.component';
import {ErinNumberInputFieldComponent} from '../../../common/erin-fields/erin-number-input-field/erin-number-input-field.component';
import {ErinSimpleInputFieldComponent} from '../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ErinDropdownFieldComponent} from '../../../common/erin-fields/erin-dropdown-field/erin-dropdown-field.component';
import {ErinDatepickerFieldComponent} from '../../../common/erin-fields/erin-datepicker-field/erin-datepicker-field.component';
import {TranslateModule} from '@ngx-translate/core';
import {StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {FieldOptions, FormsModel} from '../../../models/app.model';
import {ScanFingerprintFieldComponent} from '../../../common/erin-fields/scan-fingerprint-field/scan-fingerprint-field.component';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {BORROWER_TYPE, LOAN_PRODUCT} from "../../models/process-constants.model";

const microForm : FormsModel[] = [
  {id: BORROWER_TYPE, formFieldValue: {defaultValue: null, valueInfo: ''}, options: [], label: 'Зээлдэгчийн төрөл', type: 'string', validations: [], required: false},
  {id: LOAN_PRODUCT, formFieldValue: {defaultValue: null, valueInfo: ''}, options: [], label: 'Бүтээгдэхүүн', type: 'string', validations: [], required: false},
  {id: 'registerNumber', formFieldValue: {defaultValue: '', valueInfo: ''}, options: [], label: 'Регистр', type: 'string', validations: [], required: false},
  {id: 'phoneNumber', formFieldValue: {defaultValue: '', valueInfo: ''}, options: [], label: '', type: 'string', validations: [], required: false},
  {id: 'amount', formFieldValue: {defaultValue: '', valueInfo: ''}, options: [], label: 'Хүсч бүй...', type: 'BigDecimal', validations: [], required: false},
  {id: 'email', formFieldValue: {defaultValue: '', valueInfo: ''}, options: [], label: 'mail', type: 'string', validations: [], required: false}
];

const microCitizenProductOption : FieldOptions[] = [
  {id: 'prod1', value: 'prod1'},
  {id: 'prod2', value: 'prod2'},
  {id: 'prod3', value: 'prod3'}
];

describe('Create request dialog', () => {
  let component: CreateRequestDialogComponent;
  let fixture: ComponentFixture<CreateRequestDialogComponent>;
  let sb: LoanSearchPageSandbox;
  let getRequestSpy;
  let dialog;
  const error = {error: {message: 'error'}};
  const types = [{
    id: 'consumptionLoan',
    definitionKey: 'bpms_consumption_loan_case',
    name: 'Хэрэглээний зээл',
    version: '1.0',
    processDefinitionType: 'CASE',
    processTypeCategory: 'ORGANIZATION'
  }];
  const dialogMock = {
    close: () => {
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        MaterialModule,
        MatButtonModule,
        MatDialogModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatSelectModule,
        TranslateModule.forRoot(),
        StoreModule.forRoot({}),
        RouterModule.forRoot([])
      ],
      declarations: [
        CreateRequestDialogComponent,
        DynamicFieldsComponent,
        ErinNumberInputFieldComponent,
        ErinSimpleInputFieldComponent,
        ErinDropdownFieldComponent,
        ErinDatepickerFieldComponent,
        ScanFingerprintFieldComponent,
        ThousandSeparatorDirective,
        ErinLoaderComponent
      ],
      providers: [
        LoanSearchPageSandbox,
        {provide: MatDialogRef, useValue: dialogMock}
      ]
    }).overrideModule(BrowserDynamicTestingModule, {
      set: {
        entryComponents: [ErinLoaderComponent],
      }
    }).compileComponents();
    sb = TestBed.inject(LoanSearchPageSandbox);
  }));

  beforeEach(() => {
    getRequestSpy = spyOn(sb, 'getProcessTypes');
    getRequestSpy.and.returnValue(of([types]));
    fixture = TestBed.createComponent(CreateRequestDialogComponent);
    component = fixture.componentInstance;
    dialog = component.dialogRef;
    spyOn(dialog, 'close');
    component.selectedProcessType = {
      id: '123',
      definitionKey: '123',
      name: 'Name',
      version: null,
      processDefinitionType: null,
      processTypeCategory: null
    };
    component.ngOnInit();
    fixture.detectChanges();
  });

  it('should be created and have options', () => {
    expect(component).toBeTruthy();
    expect(component.processTypes.length).toEqual(1);
  });

  it('should get error on could not get process types', () => {
    getRequestSpy.and.returnValue(throwError('error'));
    component.ngOnInit();
    fixture.detectChanges();
    const error = fixture.debugElement.queryAll(By.css('p'));
    expect(error).toBeTruthy();
  });

  it('should continue ', () => {
    spyOn(sb, 'createRequest').and.returnValue(of('123'));
    spyOn(sb, 'startProcess').and.returnValue(of(''));
    const saveButton = fixture.debugElement.query(By.css('#continueButton'));
    saveButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(sb.getProcessTypes).toHaveBeenCalled();
  });

  xit('should not continue and get error on start process error', () => {
    spyOn(sb, 'createRequest').and.returnValue(of('123'));
    spyOn(sb, 'startProcess').and.returnValue(throwError(error));
    const continueButton = fixture.debugElement.query(By.css('#continueButton'));
    continueButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(component.dialogRef.close).not.toHaveBeenCalled();
    expect(sb.startProcess).not.toHaveBeenCalled();
  });

  it('should not continue and get error on createRequest error', () => {
    spyOn(sb, 'createRequest').and.returnValue(throwError(error));
    const continueButton = fixture.debugElement.query(By.css('#continueButton'));
    continueButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(component.dialogRef.close).not.toHaveBeenCalled();
  });

  it('should save', () => {
    spyOn(sb, 'createRequest').and.returnValue(of('123'));
    const saveButton = fixture.debugElement.query(By.css('#saveButton'));
    saveButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    dialog.close();
    expect(component.dialogRef.close).toHaveBeenCalled();
  });

  it('should get error on save', () => {
    spyOn(sb, 'createRequest').and.returnValue(throwError(error));
    const saveButton = fixture.debugElement.query(By.css('#saveButton'));
    saveButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(component.errorMessage).toBeTruthy();
  });

  it('should change type and get form fields', () => {
    const forms: FormsModel[] = [{
      id: '123',
      formFieldValue: {
        defaultValue: 'a',
        valueInfo: 'a'
      },
      label: 'hello',
      type: 'string',
      validations: [],
      options: [],
      disabled: false,
      required: false,
    }];
    spyOn(sb, 'getRequestForm').and.returnValue(of(forms));
    component.changedType(types[0]);
    fixture.detectChanges();
    expect(component.fieldError).toBeFalsy();
    expect(component.forms.length).toEqual(1);
  });

  it('should change type and get error', () => {
    spyOn(sb, 'getRequestForm').and.returnValue(throwError(error));
    component.changedType(types[0]);
    fixture.detectChanges();
    expect(component.fieldError).toBeTruthy();
    expect(component.forms.length).toEqual(0);
  });

  it('should get product by borrower type when borrower type is selected', function () {
    spyOn(component, 'validateRegisterNumber').and.callThrough();
    spyOn(component, 'setProductOptionByBorrowerType').and.callThrough();
    // spyOn(component, 'ge')
    spyOn(sb, 'getProductsByAppCategoryAndBorrowerType').and.returnValue(of(microCitizenProductOption));
    component.forms = microForm;
    fixture.detectChanges();

    component.forms.find(field => field.id === BORROWER_TYPE).formFieldValue.defaultValue = 'ААН';
    component.validateRegisterNumber();
    fixture.detectChanges();
    expect(component.validateRegisterNumber).toHaveBeenCalled();
    expect(component.setProductOptionByBorrowerType).toHaveBeenCalled();
  });
});
