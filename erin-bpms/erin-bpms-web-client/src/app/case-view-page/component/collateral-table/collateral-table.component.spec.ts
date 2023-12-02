import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CollateralTableComponent} from './collateral-table.component';
import {ColumnDef} from '../../../models/common.model';
import {By} from '@angular/platform-browser';
import {CollateralListModel} from '../../model/task.model';
import {MatTableModule} from '@angular/material/table';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialModule} from '../../../material.module';
import {CdkTableModule} from '@angular/cdk/table';
import {MatCheckboxModule} from '@angular/material/checkbox';

describe('CollateralTableComponent', () => {
  let component: CollateralTableComponent;
  let fixture: ComponentFixture<CollateralTableComponent>;
  const columns: ColumnDef[] = [
    {columnDef: 'checkbox', headerText: ''},
    {columnDef: 'collateralId', headerText: 'Барьцааны код'},
    {columnDef: 'description', headerText: 'Дэлгэрэнгүй'},
    {columnDef: 'amountOfAssessment', headerText: 'Үнэлгээ'},
    {columnDef: 'hairCut', headerText: 'Хасагдуулгын %'},
    {columnDef: 'availableAmount', headerText: 'Боломжит үлдэгдэл'},
    {columnDef: 'startDate', headerText: 'Эхлэх огноо'},
    {columnDef: 'revalueDate', headerText: 'Дахин үнэлсэн огноо'},
    {columnDef: 'accountId', headerText: 'Холбосон дансны дугаар'},
    {columnDef: 'createdOnBpms', headerText: 'CBS дээр үүсгэсэн'}
  ];
  const dummyData: CollateralListModel[] = [
    { checked: false, collateralId: '123456789', description: 'Үл хөдлөх хөрөнгө орон сууц байшин барилга', startDate: '2020/10/9',
      revalueDate: '2020/11/8', amountOfAssessment: 2000000000, hairCut: '20%', accountId: '5978454545', availableAmount: 5000000, createdOnBpms: true
    },
    {
      checked: true, collateralId: '1234567890', description: 'Суудлын автомашин', startDate: '2020/10/10', revalueDate: '2020/11/10',
      amountOfAssessment: 100000000, hairCut: '20%', accountId: '597845454500', availableAmount: 500000000, createdOnBpms: false
    }
  ];
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CollateralTableComponent],
      imports: [
        MatTableModule,
        BrowserAnimationsModule,
        MaterialModule,
        CdkTableModule,
        MatCheckboxModule
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollateralTableComponent);
    component = fixture.componentInstance;
    component.columns = columns;
    component.data = dummyData;
    component.ngOnChanges();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should have table with columns and rows', () => {
    const headerCell = fixture.debugElement.queryAll(By.css('mat-header-cell'));
    expect(headerCell).toBeTruthy();
    expect(headerCell.length).toEqual(10);

    const rowElement = fixture.debugElement.queryAll(By.css('mat-row'));
    expect(rowElement).toBeTruthy();
    expect(rowElement.length).toEqual(2);
  });
  it('should table show number span with thousand separator when useSeparator is true  ', () => {
    const columnLength = component.columns.length;
    for (const datum of component.data) {
      for (let i = 0; i < columnLength; i++) {
        const index = i++;
        const columnValue = datum[component.columns[i].columnDef];
        if (index % columnLength === 3 || index % columnLength === 5) {
          expect(component.useSeparator(columnValue)).toBeTruthy();
          expect(component.isDate(columnValue)).toBeFalsy();
        } else {
          expect(component.useSeparator(columnValue)).toBeFalsy();
        }
      }
    }
  });
  it('should table show text span when use separator and isDate false', () => {
    const columnLength = component.columns.length;
    for (const datum of component.data) {
      for (let i = 0; i < columnLength; i++) {
        const index = i++;
        const columnValue = datum[component.columns[i].columnDef];
        if (index % columnLength === 1 || index % columnLength === 2 || index % columnLength === 8) {
          expect(component.useSeparator(columnValue)).toBeFalsy();
          expect(component.isDate(columnValue)).toBeFalsy();
        }
      }
    }
  });
  it('should show empty message when table data is empty', () => {
    const emptyMessage = fixture.debugElement.query(By.css('.error-table-text'));
    if (component.dataSource.data.length === 0) {
      expect(emptyMessage).toBeTruthy();
    } else {
      expect(emptyMessage).toBeFalsy();
    }
  });
  it('should show checkbox when hasCheckBox is true', () => {
    const columnLength = component.columns.length;
    const checkbox = fixture.debugElement.query(By.css('.mat-checkbox'));
    for (let i = 0; i < columnLength; i++) {
      const columnValue = component.columns[i].columnDef;
      if (component.columns[i].columnDef === 'checkbox') {
        expect(component.hasCheckbox(columnValue)).toBeTruthy();
        component.hasCheckbox(columnValue) === true  ? expect(checkbox).toBeTruthy() : expect(checkbox).toBeFalsy();
      } else {
        expect(component.hasCheckbox(columnValue)).toBeFalsy();
      }
    }
  });
});
