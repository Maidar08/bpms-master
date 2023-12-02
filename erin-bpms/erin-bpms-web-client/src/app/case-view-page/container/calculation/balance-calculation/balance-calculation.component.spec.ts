import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {BalanceCalculationComponent} from './balance-calculation.component';
import {HttpClientModule} from '@angular/common/http';
import {MaterialModule} from '../../../../material.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModel} from '../../../../models/app.model';
import {BALANCE_ASSET_TABLE_DATA, BALANCE_DEBT_TABLE_DATA, BALANCE_OPERATION_TABLE_DATA, BALANCE_SALE_TABLE_DATA} from '../../../model/task-table-column.model';
import {TranslateModule} from '@ngx-translate/core';
import {CaseViewSandboxService} from '../../../case-view-sandbox.service';
import {of} from "rxjs";

const dummyForm: FormsModel[] = [
  {id: 'reportPeriod', formFieldValue: {defaultValue: '', valueInfo: ''}, options: [], required: false, label: '', type: 'string', validations: [] },
  {id: 'totalIncomeAmount', formFieldValue: {defaultValue: '0', valueInfo: ''}, options: [], required: false, label: '', type: 'BigDecimal', validations: [] },
  {id: 'totalIncomePercent', formFieldValue: {defaultValue: '', valueInfo: ''}, options: [], required: false, label: '', type: 'BigDecimal', validations: [] },
 ];

const dummyRes = {
    asset: {amount1: '5,000,000', amount2: '5,000,000', currentAssets: 'Бэлэн мөнгө', mainAsset: 'Оффис үйлдвэрлэлийн байр', percent1: 20, percent2: 20},
    columnHeader: {
      asset: {currentAssetAmount: '15,000,000', currentAssetPercent: 60, mainAssetAmount: '10,000,000', mainAssetPercent: 40},
      debt: {shortTermDebtAmount: '5,000,000', shortTermDebtPercent: 100, totalDebtAmount: '5,000,000', totalDebtPercent: 100},
      operation: {totalOperationCostAmount: '2,000,000', totalOperationCostPercent: 133.3, totalOperationIncomeAmount: '-2,000,000', totalOperationIncomePercent: -133.3},
      sale: {totalSaleAmount: '1,500,000', totalSaleCost: '1,500,000', totalSaleCostPercent: 100}
    },
    debt: {amount1: '5,000,000', amount2: 0, disabled: ['amount2', 'percent2'], percent1: 100, percent2: 0, shortTermDebt: 'Бэлт/нийлүүлэгчийн өглөг', totalDebt: 'Эзэмшигчийн өмч'},
    operation: {amount1: '500,000', amount2: 0, operationCost: 'Цалин шимтгэл', operationProfit: 'Үйл ажиллагааны бус орлого', percent1: 33.3, percent2: 0},
    reportPeriod: 1,
    sale: {amount1: '500,000', amount2: '500,000', bbUrtug: 'Бүтээгдэхүүн 1', percent1: 33.3, percent2: 33.3, placeholder: 'Бүтээгдэхүүн 1', sale: ''},
    totalIncomeAmount: 0,
    totalIncomePercent: 0
  };

describe('BalanceCalculationComponent', () => {
  let component: BalanceCalculationComponent;
  let fixture: ComponentFixture<BalanceCalculationComponent>;
  let sb: CaseViewSandboxService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        MaterialModule,
        HttpClientModule,
        TranslateModule.forRoot(),
      ],
      declarations: [ BalanceCalculationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BalanceCalculationComponent);
    sb = TestBed.inject(CaseViewSandboxService);
    component = fixture.componentInstance;
    component.form = dummyForm;
    setDefaultTableData(component);
    spyOn(sb, 'getBalanceFromProcessParameter').and.returnValue(of(dummyRes));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should hide add remove box if task is completed task',  () => {
    let box = document.getElementsByTagName('add-remove-box');
    expect(box.length).toEqual(1);
    component.completedState = true;
    fixture.detectChanges();
    box = document.getElementsByTagName('add-remove-box');
    expect(box.length).toEqual(0);
  });

  it('should update sale table column when type on sale input',  () => {
    expect(component.data[0][0].bbUrtug).not.toBe('test1');
    component.data[0][0].sale = 'test1';
    component.updateSaleData();
    fixture.detectChanges();
    expect(component.data[0][0].bbUrtug).toBe('test1');
  });

  it('should emit saveEmitter when save button clicked', () => {
    const save = spyOn(sb, 'saveMicroBalance').and.callThrough();
    expect(save).not.toHaveBeenCalled();
    component.save();
    expect(save).toHaveBeenCalled();
  });

  it('should emit submitEmitter when submit button clicked', () => {
    spyOn(component.submitEmitter, 'emit').and.callThrough();
    expect(component.submitEmitter.emit).not.toHaveBeenCalled();
    component.submit();
    expect(component.submitEmitter.emit).toHaveBeenCalled();
  });

  it('should increase sale table',  () => {
    expect(component.data[0].length).toEqual(3);
    component.increaseSaleData(true);
    expect(component.data[0].length).toEqual(4);
  });

  it('should not increase sale table when data[0] length is 10',  () => {
    for (let i = 0; i < 7; i++) {
      component.data[0].push(BALANCE_SALE_TABLE_DATA[0]);
    }
    fixture.detectChanges();
    expect(component.data[0].length).toEqual(10);
    component.increaseSaleData(true);
    expect(component.data[0].length).toEqual(10);
  });


});
function setDefaultTableData(component) {
  component.data.push(getSaleData());
  component.data.push(clone(BALANCE_OPERATION_TABLE_DATA));
  component.data.push(clone(BALANCE_ASSET_TABLE_DATA));
  component.data.push(clone(BALANCE_DEBT_TABLE_DATA));
}

function getSaleData() {
  const balanceData = clone(BALANCE_SALE_TABLE_DATA);
  for (let i = 0; i < balanceData.length; i++) {
    balanceData[i].placeholder = balanceData[i].placeholder + ' ' + (i + 1);
    balanceData[i].bbUrtug = balanceData[i].placeholder;
  }
  return balanceData;
}

function clone(data) {
  return JSON.parse(JSON.stringify(data));
}
