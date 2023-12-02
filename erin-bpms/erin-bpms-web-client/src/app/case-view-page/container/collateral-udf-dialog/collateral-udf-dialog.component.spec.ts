import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CollateralUdfDialogComponent} from './collateral-udf-dialog.component';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MaterialModule} from '../../../material.module';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {By} from '@angular/platform-browser';
import {DynamicFieldsComponent} from '../../component/dynamic-fields/dynamic-fields.component';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {HttpClientModule} from '@angular/common/http';
import {TranslateModule} from '@ngx-translate/core';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {of} from 'rxjs';
import {CommonSandboxService} from '../../../common/common-sandbox.service';

describe('CollateralUdfDialogComponent', () => {
  let component: CollateralUdfDialogComponent;
  let parentFixture: ComponentFixture<CollateralUdfDialogComponent>;
  let childFixture: ComponentFixture<DynamicFieldsComponent>;
  let sb: CaseViewSandboxService;
  let csb: CommonSandboxService;
  const dialogMock = {
    close: () => {
    },
    disableClose: true
  };
  const data = {
    title: 'UDF талбар',
    form: [{
      id: 'negj talbar',
      formFieldValue: {defaultValue: '', valueInfo: ''},
      label: 'Нэгж талбарын дугаар',
      type: 'String',
      validations: [],
      options: [],
      disabled: false,
      context: 'String',
      required: false
    },
      {
        id: 'umchlul', formFieldValue: {defaultValue: '', valueInfo: ''}, label: 'Өмчлөлтийн хэлбэр', type: 'String',
        validations: [], options: [], disabled: true, context: 'BigDecimal', required: false
      }],
    instanceId: 'instanceId',
    taskId: 'taskId',
    collateralId: 'collateralId'
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CollateralUdfDialogComponent, DynamicFieldsComponent],
      imports: [
        MatDialogModule,
        MaterialModule,
        HttpClientModule,
        BrowserDynamicTestingModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot()
      ],
      providers: [
        {provide: MatDialogRef, useValue: dialogMock},
        {provide: MAT_DIALOG_DATA, useValue: data}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    parentFixture = TestBed.createComponent(CollateralUdfDialogComponent);
    childFixture = TestBed.createComponent(DynamicFieldsComponent);
    sb = TestBed.inject(CaseViewSandboxService);
    csb = TestBed.inject(CommonSandboxService);
    component = parentFixture.componentInstance;
    parentFixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show title which from dialog data', () => {
    expect(parentFixture.debugElement.query(By.css('.mat-dialog-title'))).toBeTruthy();
    expect(parentFixture.debugElement.query(By.directive(DynamicFieldsComponent))).toBeTruthy();
    expect(parentFixture.debugElement.query(By.css('.mat-dialog-title')).nativeElement.innerText).toEqual('UDF талбар');
  });

  it('should get dynamic udf form',  () => {
    spyOn(sb, 'getCollateralUdfFromProcessTable').and.callThrough();
    component.ngOnInit();
    component.getCollateralUdf();
    parentFixture.detectChanges();
    expect(sb.getCollateralUdfFromProcessTable).toHaveBeenCalled();
  });

  it('save form values after click save()', () => {
    spyOn(csb, 'getFormValue').and.callFake(() => data.form);
    spyOn(csb, 'setFieldDefaultValue').and.callFake(() => data.form);
    spyOn(sb, 'saveCollateralUdf').and.callFake(() => of(data));
    spyOn(sb, 'saveUdfToProcessTable').and.callFake(() => of(data));
    component.save();
    parentFixture.detectChanges();
    expect(sb.saveCollateralUdf).toHaveBeenCalled();
    expect(sb.saveUdfToProcessTable).toHaveBeenCalled();
  });
});
