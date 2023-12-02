import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CreateAccountWithCollateralComponent} from './create-account-with-collateral.component';
import {HttpClientModule} from '@angular/common/http';
import {MaterialModule} from '../../../material.module';
import {ProcessService} from '../../services/process.service';
import {DynamicFieldsComponent} from '../dynamic-fields/dynamic-fields.component';
import {ErinSimpleInputFieldComponent} from '../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {MatDialogModule} from '@angular/material/dialog';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {CdkTableModule} from '@angular/cdk/table';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ErinNumberInputFieldComponent} from '../../../common/erin-fields/erin-number-input-field/erin-number-input-field.component';
import {AMOUNT_OF_ASSESSMENT, COLLATERAL_CONNECTION_RATE, COLLATERAL_ID, LOAN_AMOUNT, ORDER} from '../../model/task-constant';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {ConfirmDialogComponent} from '../../../common/confirm-dialog/confirm-dialog.component';
import {TranslateModule} from '@ngx-translate/core';

describe('CreateAccountWithCollateralComponent', () => {
  let component: CreateAccountWithCollateralComponent;
  let fixture: ComponentFixture<CreateAccountWithCollateralComponent>;
  let sb: CommonSandboxService;

  const dummyCollateralLis = [
    {collateralId: 'BPM001', amountOfAssessment: 1},
    {collateralId: 'BPM002', amountOfAssessment: 2},
  ];

  const dummy = [
    [
      {id: COLLATERAL_ID, formFieldValue: {defaultValue: 'COL0002580402', valueInfo: ''}, label: 'Барьцааны код',
        type: 'string', validations: [], options: [], disabled: false, context: 'string', required: false},
      {id: AMOUNT_OF_ASSESSMENT, formFieldValue: {defaultValue: '15,000,000', valueInfo: ''}, label: 'Барьцааны үнэлгээ',
        type: 'BigDecimal', validations: [], options: [], disabled: false, context: 'BigDecimal', required: false},
      {id: COLLATERAL_CONNECTION_RATE, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Барьцаанд холбох хувь', type: 'BigDecimal',
        validations: [{name: 'readonly', configuration: null}], options: [], disabled: false, context: 'BigDecimal', required: false},
      {id: LOAN_AMOUNT, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Зээлд холбох дүн', type: 'BigDecimal',
        validations: [], options: [], disabled: false, context: 'BigDecimal', required: false},
      {id: ORDER, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Дугаарлалт', type: 'BigDecimal',
        validations: [], options: [], disabled: false, context: 'BigDecimal', required: false}
    ],
    [
      {id: COLLATERAL_ID, formFieldValue: {defaultValue: 'COL0002580500', valueInfo: ''}, label: 'Барьцааны код',
        type: 'string', validations: [], options: [], disabled: false, context: 'string', required: false},
      {id: AMOUNT_OF_ASSESSMENT, formFieldValue: {defaultValue: '10,000,000', valueInfo: ''}, label: 'Барьцааны үнэлгээ',
        type: 'BigDecimal', validations: [], options: [], disabled: false, context: 'BigDecimal', required: false},
      {id: COLLATERAL_CONNECTION_RATE, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Барьцаанд холбох хувь', type: 'BigDecimal',
        validations: [{name: 'readonly', configuration: null}], options: [], disabled: false, context: 'BigDecimal', required: false},
      {id: LOAN_AMOUNT, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Зээлд холбох дүн', type: 'BigDecimal',
        validations: [], options: [], disabled: false, context: 'BigDecimal', required: false},
      {id: ORDER, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Дугаарлалт', type: 'BigDecimal',
        validations: [], options: [], disabled: false, context: 'BigDecimal', required: false}
    ],
    [
      {id: COLLATERAL_ID, formFieldValue: {defaultValue: '', valueInfo: ''}, label: 'Барьцааны код',
        type: 'string', validations: [], options: [], disabled: false, context: 'string', required: false},
      {id: AMOUNT_OF_ASSESSMENT, formFieldValue: {defaultValue: '10,000,000', valueInfo: ''}, label: 'Барьцааны үнэлгээ',
        type: 'BigDecimal', validations: [], options: [], disabled: false, context: 'BigDecimal', required: false},
      {id: COLLATERAL_CONNECTION_RATE, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Барьцаанд холбох хувь', type: 'BigDecimal',
        validations: [{name: 'readonly', configuration: null}], options: [], disabled: false, context: 'BigDecimal', required: false},
      {id: LOAN_AMOUNT, formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Зээлд холбох дүн', type: 'BigDecimal',
        validations: [], options: [], disabled: false, context: 'BigDecimal', required: false},
      {id: 'insuranceCUDF', formFieldValue: {defaultValue: 0, valueInfo: ''}, label: 'Дугаарлалт', type: 'BigDecimal',
        validations: [], options: [], disabled: false, context: 'BigDecimal', required: false}
    ]
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatDialogModule,
        BrowserDynamicTestingModule,
        CdkTableModule,
        MaterialModule,
        FormsModule,
        MatIconModule,
        MatCardModule,
        HttpClientModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot()
      ],
      declarations: [
        CreateAccountWithCollateralComponent,
        DynamicFieldsComponent,
        ErinSimpleInputFieldComponent,
        ErinNumberInputFieldComponent,
        ConfirmDialogComponent,
        ThousandSeparatorDirective
      ],
      providers: [
        {provide: ProcessService, useClass: ProcessService}
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateAccountWithCollateralComponent);
    component = fixture.componentInstance;
    component.data = dummy;
    sb = TestBed.inject(CommonSandboxService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not to emit submitEmitter when isValidConnectionRate is false',  () => {
    spyOn(component.submitEmitter, 'emit').and.callThrough();
    component.isValidCollateralRate = false;
    component.submit();
    expect(component.submitEmitter.emit).not.toHaveBeenCalled();
  });

  it('should not to emit submitEmitter when collateral order is 0', () => {
    spyOn(component.submitEmitter, 'emit').and.callThrough();
    component.collateralAssetForm = [{}, {}, {}];
    component.collateralAssetForm.push(dummy);
    component.isValidCollateralRate = true;
    component.submit();
    expect(component.submitEmitter.emit).toHaveBeenCalled();
  });

  it('should emit submitEmitter when everything is ok', () => {
    dummy.forEach( dummyCollateral => {
      const orderField = dummyCollateral.find( field => field.id === ORDER );
      if (orderField) { orderField.formFieldValue.defaultValue = 1; }
    } );
    spyOn(component.submitEmitter, 'emit').and.callThrough();
    component.collateralAssetForm = [{}, {}, {}];
    component.collateralAssetForm.push(dummy);
    component.isValidCollateralRate = true;
    component.submit();
    expect(component.submitEmitter.emit).toHaveBeenCalled();
  });
});
