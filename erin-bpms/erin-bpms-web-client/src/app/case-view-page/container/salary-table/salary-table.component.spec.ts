import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SalaryTableComponent} from './salary-table.component';
import {FormsModel} from '../../../models/app.model';
import {By} from '@angular/platform-browser';
import {DynamicFieldsComponent} from '../../component/dynamic-fields/dynamic-fields.component';
import {ErinNumberInputFieldComponent} from '../../../common/erin-fields/erin-number-input-field/erin-number-input-field.component';
import {SalaryCalculationTableComponent} from '../../component/salary-calculation-table/salary-calculation-table.component';
import {MaterialModule} from '../../../material.module';
import {ErinSimpleInputFieldComponent} from '../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ErinDropdownFieldComponent} from '../../../common/erin-fields/erin-dropdown-field/erin-dropdown-field.component';
import {ErinDatepickerFieldComponent} from '../../../common/erin-fields/erin-datepicker-field/erin-datepicker-field.component';
import {ScanFingerprintFieldComponent} from '../../../common/erin-fields/scan-fingerprint-field/scan-fingerprint-field.component';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {FormsModule} from '@angular/forms';
import {CommonModule, DatePipe} from '@angular/common';
import {MatTableModule} from '@angular/material/table';
import {CdkTableModule} from '@angular/cdk/table';
import {HttpClientModule} from '@angular/common/http';
import {TranslateModule} from '@ngx-translate/core';
import {StoreModule} from '@ngrx/store';
import {of} from 'rxjs';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SalaryCalculationModel, SalaryModel, SaveSalaryModel} from '../../model/task.model';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {MORTGAGE_LOAN} from "../../model/case-folder-constants";

const salaryInfo: SalaryCalculationModel[] = [
  {month: 1, date: new Date(2018, 1), defaultValue: 69, socialInsuranceTax: 420000
    , personalIncomeTax: 4958000, salaryAfterTax: 44622000, checked: null, isXypSalary: false, isCompleted: false},
  {month: 2, date: new Date(2018, 0), defaultValue: 50000000, socialInsuranceTax: 420000,
    personalIncomeTax: 4958000, salaryAfterTax: 44622000, checked: null, isXypSalary: false, isCompleted: false},
  {month: 3, date: new Date(2018, 11), defaultValue: 50000000, socialInsuranceTax: 368000,
    personalIncomeTax: 4963200, salaryAfterTax: 44668800, checked: null, isXypSalary: false, isCompleted: false},
  {month: 4, date: new Date(2018, 10), defaultValue: 50000000, socialInsuranceTax: 368000,
    personalIncomeTax: 4963200, salaryAfterTax: 44668800, checked: null, isXypSalary: false, isCompleted: false},
  {month: 5, date: new Date(2018, 9), defaultValue: 0, socialInsuranceTax: 0,
    personalIncomeTax: 0, salaryAfterTax: 0, checked: null, isXypSalary: false, isCompleted: false},
  {month: 6, date: new Date(2018, 8), defaultValue: 0, socialInsuranceTax: 0,
    personalIncomeTax: 0, salaryAfterTax: 0, checked: null, isXypSalary: false, isCompleted: false},
  {month: 7, date: new Date(2018, 7), defaultValue: 0, socialInsuranceTax: 0,
    personalIncomeTax: 0, salaryAfterTax: 0, checked: null, isXypSalary: false, isCompleted: false},
  {month: 8, date: new Date(2018, 6), defaultValue: 0, socialInsuranceTax: 0,
    personalIncomeTax: 0, salaryAfterTax: 0, checked: null, isXypSalary: false, isCompleted: false},
  {month: 9, date: new Date(2018, 5), defaultValue: 0, socialInsuranceTax: 0,
    personalIncomeTax: 0, salaryAfterTax: 0, checked: null, isXypSalary: false, isCompleted: false},
  {month: 10, date: new Date(2018, 4), defaultValue: 0, socialInsuranceTax: 0,
    personalIncomeTax: 0, salaryAfterTax: 0, checked: null, isXypSalary: false, isCompleted: false},
  {month: 11, date: new Date(2018, 3), defaultValue: 0, socialInsuranceTax: 0,
    personalIncomeTax: 0, salaryAfterTax: 0, checked: null, isXypSalary: false, isCompleted: false},
  {month: 12, date: new Date(2018, 2), defaultValue: 0, socialInsuranceTax: 0,
    personalIncomeTax: 0, salaryAfterTax: 0, checked: null, isXypSalary: false, isCompleted: false},
];

const completedSalaryInfo = {
  averageAfterTax: 34854, averageBeforeTax: 41667, emd: 'Тийм', hasMortgage: 'Үгүй', ndsh: 'Үгүй',
  salaryiesInfo: {
    '2018-11-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2018-12-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-01-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-02-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-03-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-04-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-05-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-06-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-07-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-08-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-09-01': {hhoat: 0, ndsh: 0, salaryAfterTax: 0, salaryBeforeTax: 0, salaryFromXyp: true},
    '2019-10-01': {hhoat: 24250, ndsh: 57500, salaryAfterTax: 418250, salaryBeforeTax: 500000, salaryFromXyp: true},
  }
};

describe('SalaryTableComponent', () => {

  const salaryModel: SalaryModel = { afterTaxSalaries: salaryInfo,
    averageSalaryAfterTax: 12,
    averageSalaryBeforeTax: 1000};

  const form: FormsModel[] = [
    {
      id: 'ndsh', formFieldValue: {defaultValue: 'Үгүй', valueInfo: ''}, label: 'НДШ чөлөөлөх эсэх', type: 'string', validations: [],
      options: [{id: 'option1', value: 'Үгүй'}, {id: 'option2', value: 'Тийм'}], disabled: false, context: 'string', required: false
    },
    {
      id: 'emd', formFieldValue: {defaultValue: 'Үгүй', valueInfo: ''}, label: 'ЭМД чөлөөлөх эсэх', type: 'string', validations: [],
      options: [{id: 'option1', value: 'Үгүй'}, {id: 'option2', value: 'Тийм'}], disabled: false, context: 'string', required: false
    },
    {
      id: 'hasMortgage', formFieldValue: {defaultValue: 'Үгүй', valueInfo: ''},
      label: 'Орон сууцны зээлтэй эсэх', type: 'string', validations: [],
      options: [{id: 'option1', value: 'Үгүй'}, {id: 'option2', value: 'Тийм'}], disabled: false, context: 'string', required: false
    },
  ];

  const resultForm = [
    {
      id: 'confirmed-salary', formFieldValue: {defaultValue: null, valueInfo: ''},
      label: 'НД баталгаажсан цалин', type: 'string', validations: [], options: [], disabled: true, context: 'BigDecimal', required: false
    },
    {
      id: 'salary-after-tax', formFieldValue: {defaultValue: null, valueInfo: ''},
      label: 'Татварын өмнөх цалин', type: 'string', validations: [], options: [], disabled: true, context: 'BigDecimal', required: false
    },
  ];

  const saveSalaryModel: SaveSalaryModel = {
    averageAfterTax: 100,
    averageBeforeTax: 200,
    hasMortgage: false,
    ndsh: false,
    emd: false,
    salariesInfo: salaryInfo
  };

  const instanceId = '544d041b-f663-11ea-8674-00090faa0001';

  let component: SalaryTableComponent;
  let fixture: ComponentFixture<SalaryTableComponent>;
  let sb;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MaterialModule,
        FormsModule,
        CommonModule,
        CdkTableModule,
        MatTableModule,
        HttpClientModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot(),
        StoreModule.forRoot({}),
        RouterTestingModule,
      ],
      declarations: [
        SalaryTableComponent,
        DynamicFieldsComponent,
        ErinNumberInputFieldComponent,
        ErinSimpleInputFieldComponent,
        ErinDropdownFieldComponent,
        ErinDatepickerFieldComponent,
        ScanFingerprintFieldComponent,
        ThousandSeparatorDirective,
        SalaryCalculationTableComponent
      ],
      providers: [
        DatePipe,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    const defaultSalary: {} = {};
    sb = TestBed.inject(CaseViewSandboxService);
    spyOn(sb, 'getDefaultSalary').and.returnValue(of(defaultSalary));
    spyOn(sb, 'getSalary').and.returnValue(of(salaryModel));
    spyOn(sb, 'setSalaryAverage').and.returnValue(of(salaryModel));
    spyOn(sb, 'saveSalary').and.callThrough();
    fixture = TestBed.createComponent(SalaryTableComponent);
    component = fixture.componentInstance;

    component.form = form;
    component.completedForm = [];
    component.data = salaryInfo;
    // component.resultForm = resultForm;
    component.instanceId = instanceId;
    component.averageSalaryAfterTax.formFieldValue.defaultValue = 100;
    component.averageSalaryBeforeTax.formFieldValue.defaultValue = 200;
    component.saveSalaryModel = saveSalaryModel;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call calculateAverage method when calculate button clicked',  () => {
    spyOn(component, 'calculateAverage').and.callThrough();
    const  calculateButton = fixture.debugElement.query(By.css('.calculate-button'));

    calculateButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(component.calculateAverage).toHaveBeenCalled();
  });

  it('should call submit method when complete button clicked',  () => {
    setDate();
    spyOn(component, 'submit').and.callThrough();
    spyOn(component, 'checkAverageSalary').and.returnValue(true);
    spyOn(component, 'completeTask').and.callThrough();
    const  completeButton = fixture.debugElement.queryAll(By.css('button'))[0];


    completeButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(component.submit).toHaveBeenCalled();
    removeDate();
  });

  it('should call save method when save button clicked',  () => {
    spyOn(component, 'save').and.callThrough();
    const  saveButton = fixture.debugElement.queryAll(By.css('button'))[1];

    saveButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(component.save).toHaveBeenCalled();
  });

  it('should not save when date format is false', () => {
    spyOn(component, 'saveSalary').and.callThrough();
    spyOn(component, 'checkAverageSalary').and.returnValue(true);
    component.dateFormat = false;

    component.save(false);
    expect(component.saveSalary).not.toHaveBeenCalled();
  });

  it('should not save when isValidaverageSalary is false', () => {
    setDate();
    spyOn(component, 'saveSalary').and.callThrough();
    spyOn(component, 'checkAverageSalary').and.returnValue(false);

    component.save(false);
    expect(component.saveSalary).not.toHaveBeenCalled();
    removeDate();
  });

  it('should save when isValidToSaveSalary is true',  () => {
    spyOn(component, 'saveSalary').and.callThrough();
    spyOn(component, 'checkAverageSalary').and.returnValue(true);
    setDate();

    component.save(false);
    expect(component.saveSalary).toHaveBeenCalled();
    removeDate();
  });

  it('should calculate average salary', () => {
    setDate();
    check();

    component.calculateAverage();
    expect(sb.setSalaryAverage).toHaveBeenCalled();
    removeDate();
    check();
  });

  it('should not calculate average salary when salary is not checked', () => {
    spyOn(component, 'simplifySalary').and.callThrough();
    setDate();
    component.calculateAverage();
    expect(sb.setSalaryAverage).not.toHaveBeenCalled();
    removeDate();
  });

  it('should not calculate average salary when date is not valid', () => {
    spyOn(component, 'simplifySalary').and.callThrough();
    check();
    component.calculateAverage();
    expect(sb.setSalaryAverage).not.toHaveBeenCalled();
    check();
  });

  it('should set form initial value depending on loanType', ()=>{
    const hasMortgageForm = component.form.find(field => field.id ==='hasMortgage')
    if (component.loanType !== MORTGAGE_LOAN){
        expect(hasMortgageForm.options.length).toEqual(2);
        expect(hasMortgageForm.disabled).toBeFalsy();
    }
    else {
      expect(hasMortgageForm.options.length).toEqual(0);
      expect(hasMortgageForm.disabled).toBeTruthy();
      expect(hasMortgageForm.formFieldValue.defaultValue).toEqual('Тийм');
    }
  })

  it('should set completed form',  () => {
    component.completedForm = completedSalaryInfo;
    component.ngOnInit();
    if(component.completedForm.length > 0){
       component.form.map( field => {
        expect(field.options.length).toEqual(0);
        expect(field.disabled).toBeTruthy();
      });
    }
  });
});

function setDate() {
    const dateInput  = document.getElementsByClassName('dateInput');
    for (let i = 0; i < 12; i++) {
      const date = salaryInfo[i].date;
      (dateInput[i] as HTMLInputElement).value = (date.getMonth() + 1) + '/' + date.getFullYear();
    }
}

function removeDate() {
  const dateInput  = document.getElementsByClassName('dateInput');
  for (let i = 0; i < 12; i++) {
    (dateInput[i] as HTMLInputElement).value = '';
  }
}

function check() {
  // @ts-ignore
  salaryInfo.map( salary => salary.checked = !salary.checked );
}

