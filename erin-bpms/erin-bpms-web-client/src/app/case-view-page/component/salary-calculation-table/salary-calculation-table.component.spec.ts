import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SalaryCalculationTableComponent} from './salary-calculation-table.component';
import {MaterialModule} from '../../../material.module';
import {FormsModule} from '@angular/forms';
import {CommonModule, DatePipe} from '@angular/common';
import {CdkTableModule} from '@angular/cdk/table';
import {MatTableModule} from '@angular/material/table';
import {HttpClientModule} from '@angular/common/http';
import {TranslateModule} from '@ngx-translate/core';
import {StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {By} from '@angular/platform-browser';
import {ColumnDef} from '../../../models/common.model';
import {SalaryCalculationModel} from '../../model/task.model';

const salaryInfo: SalaryCalculationModel[] = [
  {month: 1, date: new Date(2018, 1), defaultValue: 50000000, socialInsuranceTax: 420000,
    personalIncomeTax: 4958000, salaryAfterTax: 44622000, checked: false, isXypSalary: false, isCompleted: false},
  {month: 2, date: new Date(2018, 0), defaultValue: 50000000, socialInsuranceTax: 420000,
    personalIncomeTax: 4958000, salaryAfterTax: 44622000, checked: false, isXypSalary: false, isCompleted: false},
  {month: 3, date: new Date(2018, 11), defaultValue: 50000000, socialInsuranceTax: 368000,
    personalIncomeTax: 4963200, salaryAfterTax: 44668800, checked: false, isXypSalary: false, isCompleted: false},
  {month: 4, date: new Date(2018, 10), defaultValue: 50000000, socialInsuranceTax: 368000,
    personalIncomeTax: 4963200, salaryAfterTax: 44668800, checked: false, isXypSalary: false, isCompleted: false},
  {month: 5, date: new Date(2018, 9), defaultValue: 0, socialInsuranceTax: 0,
    personalIncomeTax: 0, salaryAfterTax: 0, checked: false, isXypSalary: false, isCompleted: false},
  {month: 6, date: new Date(2018, 8), defaultValue: 1200000, socialInsuranceTax: 138000,
    personalIncomeTax: 89533, salaryAfterTax: 972467, checked: false, isXypSalary: false, isCompleted: false},
  {month: 7, date: new Date(2018, 7), defaultValue: 250000, socialInsuranceTax: 28750,
    personalIncomeTax: 2125, salaryAfterTax: 279125, checked: false, isXypSalary: false, isCompleted: false},
  {month: 8, date: new Date(2018, 6), defaultValue: 1800000, socialInsuranceTax: 270000,
    personalIncomeTax: 144300, salaryAfterTax: 1448700, checked: false, isXypSalary: false, isCompleted: false},
  {month: 9, date: new Date(2018, 5), defaultValue: 350000, socialInsuranceTax: 40250,
    personalIncomeTax: 10975, salaryAfterTax: 298775, checked: false, isXypSalary: false, isCompleted: false},
  {month: 10, date: new Date(2018, 4), defaultValue: 8000000, socialInsuranceTax: 368000,
    personalIncomeTax: 763200, salaryAfterTax: 6868800, checked: false, isXypSalary: false, isCompleted: false},
  {month: 11, date: new Date(2018, 3), defaultValue: 1200000, socialInsuranceTax: 138000,
    personalIncomeTax: 89533, salaryAfterTax: 972467, checked: false, isXypSalary: false, isCompleted: false},
  {month: 12, date: new Date(2018, 2), defaultValue: 4000000, socialInsuranceTax: 368000,
    personalIncomeTax: 363200, salaryAfterTax: 3268800, checked: false, isXypSalary: false, isCompleted: false},
  {month: 13, date: new Date(2018, 2), defaultValue: 4000000, socialInsuranceTax: 368000,
    personalIncomeTax: 363200, salaryAfterTax: 3268800, checked: false, isXypSalary: false, isCompleted: false},
];
const SALARY_CALCULATION_COLUMNS: ColumnDef[] = [
  {columnDef: 'month', headerText: 'Сар'},
  {columnDef: 'date', headerText: 'Огноо'},
  {columnDef: 'defaultValue', headerText: 'НД баталгаажсан цалин'},
  {columnDef: 'socialInsuranceTax', headerText: 'НДШ'},
  {columnDef: 'personalIncomeTax', headerText: 'ХХОАТ'},
  {columnDef: 'salaryAfterTax', headerText: 'Татварын дараах цалин'},
];

describe('LoanCalculationTableComponent', () => {
  let component: SalaryCalculationTableComponent;
  let fixture: ComponentFixture<SalaryCalculationTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MaterialModule,
        FormsModule,
        CommonModule,
        CdkTableModule,
        MatTableModule,
        HttpClientModule,
        TranslateModule.forRoot(),
        StoreModule.forRoot({}),
        RouterModule.forRoot([]),
      ],
      declarations: [
        SalaryCalculationTableComponent,
        ThousandSeparatorDirective,
      ],
      providers: [
        DatePipe,
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SalaryCalculationTableComponent);
    component = fixture.componentInstance;
    component.columns = SALARY_CALCULATION_COLUMNS;
    component.data = salaryInfo;
    component.ngOnChanges();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should have table with columns and rows', () => {
    const headerCell = fixture.debugElement.queryAll(By.css('mat-header-cell'));
    expect(headerCell).toBeTruthy();
    expect(headerCell.length).toEqual(6);

    const rowElement = fixture.debugElement.queryAll(By.css('mat-row'));
    expect(rowElement).toBeTruthy();
    expect(rowElement.length).toEqual(13);
  });

  it('should check a checkbox', () => {
    const matCheckBox = fixture.debugElement.queryAll(By.css('mat-checkbox'));
    const input = matCheckBox[0].query(By.css('input'));
    expect(input.nativeElement.checked).toBeFalsy();
    input.nativeElement.click();
    fixture.detectChanges();
    for (let i = 0; i < matCheckBox.length ; i++) {
      const childInput = matCheckBox[i].query(By.css('input'));
      if (i < 12) {
        expect(childInput.nativeElement.checked).toBeTruthy();
      } else {
        expect(childInput.nativeElement.checked).toBeFalsy();
      }
    }
  });
  it('should disable some checkbox', () => {
    const matCheckBox = fixture.debugElement.queryAll(By.css('mat-checkbox'));
    for (let i = 0; i < matCheckBox.length ; i++) {
      const childInput = matCheckBox[i].query(By.css('input'));
      if (i < 2) {
        expect(childInput.nativeElement.disabled).toBeFalsy();
      } else {
        expect(childInput.nativeElement.disabled).toBeTruthy();
      }
    }
  });

  it('should show checkbox when column def is month', () => {
      const matCell = fixture.debugElement.queryAll(By.css('mat-cell'));
      for (let i = 0; i < matCell.length; i++) {
        const checkBox = matCell[i].query(By.css('mat-checkbox'));
        if ( (i + 1) % 6 === 1) {
          expect(checkBox).not.toBeNull();
        } else {
          expect(checkBox).toBeNull();
        }
      }
  });

  it('should show span when cell is not date and non editable', () => {
    const matCell = fixture.debugElement.queryAll(By.css('mat-cell'));
    for (let i = 0; i < matCell.length; i++) {
      const span = matCell[i].query(By.css('span'));
      const index = (i + 1) % 6;
      if (index < 2 || index > 3) {
        expect(span).not.toBeNull();
      } else {
        expect(span).toBeNull();
      }
    }
  });

  it('should show date input when cell type is date', () => {
    const matCell = fixture.debugElement.queryAll(By.css('mat-cell'));
    for (let i = 0; i < matCell.length; i++) {
      const dateInput = matCell[i].query(By.css('.dateInput'));
      const index = (i + 1) % 6;
      if (index === 2) {
        expect(dateInput).not.toBeNull();
      } else {
        expect(dateInput).toBeNull();
      }
    }
  });

  it('should check Date Validation', () => {
    let currentDate;
    if (new Date().getMonth() + 1 < 10) {
      currentDate = new Date().getFullYear().toString() + '-' + '0' + (new Date().getMonth() + 1).toString() ;
    } else {
      currentDate = new Date().getFullYear().toString() + '-' + (new Date().getMonth() + 1).toString();
    }
    // tslint:disable-next-line:max-line-length
    expect(component.datePipe.transform(new Date(component.data[0].date), 'yyyy-MM') < component.datePipe.transform(new Date(currentDate))).toBeTruthy();
  });
});
