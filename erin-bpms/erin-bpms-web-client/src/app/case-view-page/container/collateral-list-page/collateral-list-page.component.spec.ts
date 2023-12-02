import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CollateralListPageComponent} from './collateral-list-page.component';
import {of} from 'rxjs';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {TranslateModule} from '@ngx-translate/core';
import {StoreModule} from '@ngrx/store';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientModule} from '@angular/common/http';
import {MaterialModule} from '../../../material.module';
import {By} from '@angular/platform-browser';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {CollateralListModel} from '../../model/task.model';

const dummyData: CollateralListModel[] = [
  {
    checked: true, collateralId: 'COL123', description: 'Хадгаламж', amountOfAssessment: 10000, hairCut: '15%',
    availableAmount: 850000 , startDate: '2016/07/08', revalueDate: '2016/07/08', accountId: '152121212', createdOnBpms: true
  },
  {
    accountId: 'EMPTY-ACCOUNT-NUMBER', amountOfAssessment: 10000, availableAmount: 142100000, checked: undefined,
    collateralId: 'BPMS01101824001', createdOnBpms: 'Тийм', description: 'Үйлдвэрийн барилга - test1010 ', hairCut: '2%',
    revalueDate: '', startDate: '2021/6/18',
  }
];
describe('CollateralListPageComponent', () => {
  let component: CollateralListPageComponent;
  let fixture: ComponentFixture<CollateralListPageComponent>;
  let sb: CaseViewSandboxService;
  let commonSB: CommonSandboxService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        HttpClientModule,
        MaterialModule,
        BrowserDynamicTestingModule,
        MatSnackBarModule,
        TranslateModule.forRoot(),
        StoreModule.forRoot({}),
        RouterTestingModule,
      ],
      declarations: [ CollateralListPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CollateralListPageComponent);
    sb = TestBed.inject(CaseViewSandboxService);
    commonSB = TestBed.inject(CommonSandboxService);
    component = fixture.componentInstance;
    component.data = dummyData;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get collaterals', () => {
    spyOn(sb, 'getCollateral').and.returnValue(of([]));
    component.ngOnInit();
    component.getCollateralList('cif');
    expect(sb.getCollateral).toHaveBeenCalled();
  });

  it('should get loan info by requestId', () => {
    spyOn(sb, 'getRequestById').and.returnValue(of([]));
    component.ngOnInit();
    component.getLoanRequestInfo('requestId123');
    fixture.detectChanges();
    expect(sb.getRequestById).toHaveBeenCalled();
  });

  it('should show loan to product ratio if LTV is higher than procedure criteria', () => {
    component.LTV = '100%';
    component.procedureCriteriaOfProduct = 70;
    const loanProductRatio = fixture.debugElement.query(By.css('#ltv'));
    fixture.detectChanges();
    if (commonSB.formatNumberService(component.LTV) !== null) {
      if (component.LTV > component.procedureCriteriaOfProduct * 100) {
        expect(loanProductRatio).toBeTruthy();
      } else {
        expect(loanProductRatio).toBeFalsy();
      }
    }
  });

  it('should calculate collateral amount when click calculate button ', () => {
    spyOn(component, 'calculate').and.callThrough();
    const calculateButton = fixture.debugElement.query(By.css('#calculate'));
    calculateButton.triggerEventHandler('click', null);
    if ( expect(component.calculate).toHaveBeenCalled()) {
      expect(component.totalCollateralAmount).toEqual(20000);
    }
  });


  it('should emit form fields when click submit button', () => {
    const  continueButton = fixture.debugElement.query(By.css('#continueProcess'));
    continueButton.triggerEventHandler('click', null);
    spyOn(component.collateralListEmitter, 'emit').and.callThrough();
    component.continueProcess();
    fixture.detectChanges();
    expect(component.collateralListEmitter.emit).toHaveBeenCalled();
  });

  it('should call to get collateral to update',  () => {
    spyOn(sb, 'getUpdateCollateralForm').and.callThrough();
    component.getUpdateCollateral({collateralId: '123', collateralType: 'M'});
    expect(sb.getUpdateCollateralForm).toHaveBeenCalled();
  });

  it('should refresh collateralList to click refresh button', () => {
     spyOn(sb, 'refreshCollateral').and.callThrough();
     const refreshButton = fixture.debugElement.query(By.css('.mat-icon-button'));
     refreshButton.triggerEventHandler('click', null);
     component.refreshTable();
     component.refreshCollateralList('cif123');
     fixture.detectChanges();
     expect(sb.refreshCollateral).toHaveBeenCalled();
  });
});
