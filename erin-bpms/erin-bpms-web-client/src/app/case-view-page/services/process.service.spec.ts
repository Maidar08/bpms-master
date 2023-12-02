import {async, TestBed} from '@angular/core/testing';
import {ProcessService} from './process.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {BrowserAnimationsModule, NoopAnimationsModule} from '@angular/platform-browser/animations';
import {TranslateModule} from '@ngx-translate/core';
import {StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {CollateralListModel} from '../model/task.model';

describe('ProcessService', () => {
  let service: ProcessService;
  let httpMock: HttpTestingController;
  let snack: MatSnackBar;
  const taskItem = [
    {
      icon: 'notes',
      activityName: 'name123',
      executionId: 'id1',
      instanceId: 'id2',
      executionType: 'type',
      taskId: 'id3'
    }
  ];

  const activeTaskItem = [
    {
      icon: 'notes',
      name: 'name',
      executionId: 'execute',
      instanceId: 'instance',
      type: 'typeA',
      taskId: 'task'
    }
  ];
  const submitForm = {
    taskId: 'task1234',
    properties: {}
  };
  const createReq = {
    processRequestId: 'typeId1234'
  };
  const formsModel = [
    {
      id: 'formId123',
      type: 'string',
      validations: null,
      options: [],
      disabled: true
    }
  ];
  const salaryReq = {
    niigmiinDaatgalExcluded: true,
    healthInsuranceExcluded: false,
    dateAndSalaries:
      {
        '2018-02-01': 69,
        '2018-01-01': 50000000,
        '2018-12-01': 50000000,
        '2018-11-01': 50000000,
        '2018-10-01': 0,
        '2018-09-01': 1200000,
        '2018-08-01': 250000,
        '2018-07-01': 1800000,
        '2018-06-01': 350000,
        '2018-05-01': 8000000,
        '2018-04-01': 1200000,
        '2018-03-01': 4000000
      }
  };
  const dummyCollateralList: CollateralListModel[] = [
    {
      checked: false,
      collateralId: '123456789',
      description: 'Үл хөдлөх хөрөнгө орон сууц байшин барилга',
      startDate: '2020/10/9',
      revalueDate: '2020/11/8',
      amountOfAssessment: 2000000000,
      hairCut: '20%',
      accountId: '5978454545',
      availableAmount: 5000000,
      createdOnBpms: true
    }];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        MatSnackBarModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
        TranslateModule.forRoot(),
        StoreModule.forRoot({}),
        RouterModule.forRoot([])
      ],
    });
    service = TestBed.inject(ProcessService);
    httpMock = TestBed.inject(HttpTestingController);
    snack = TestBed.inject(MatSnackBar);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should post salary and get new salary', () => {
    service.setSalaryAverage(salaryReq).subscribe(res => {
      expect(res.averageSalaryBeforeTax).not.toBe(null);
      expect(res.averageSalaryAfterTax).not.toBe(null);
      expect(res.afterTaxSalaries).toEqual([]);
    });
    const httpRequest = httpMock.expectOne({method: 'POST'});
    httpRequest.flush({entity: createReq});
  });

  it('should submit form', () => {
    spyOn(snack, 'open');
    service.submitForm('', '', '', [], '', '', '').subscribe(res => {
      expect(res).toEqual(submitForm);
    });
    const httpRequest = httpMock.expectOne({method: 'POST'});
    httpRequest.flush(submitForm);
  });

  it(' should get task', () => {
    service.getTask('', '').subscribe(res => {
      expect(res[0].icon).toEqual('notes');
      expect(res[0].name).toEqual('name123');
      expect(res[0].executionId).toEqual('id1');
      expect(res[0].instanceId).toEqual('id2');
      expect(res[0].executionType).toEqual('type');
      expect(res[0].taskId).toEqual('id3');
    });
    const httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({entity: taskItem});
  });

  it('should get customer variables', () => {
    service.getCustomerVariables('', '').subscribe(res => {
      expect(res[0].type).toEqual('string');
      expect(res[0].validations).toEqual([]);
      expect(res[0].options).toEqual([]);
      expect(res[0].disabled).toBeTruthy();
    });
    const httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({entity: formsModel});
  });

  it('should get active task', () => {
    service.getActiveTasks('').subscribe(res => {
      expect(res[0].icon).toEqual('notes');
      expect(res[0].name).toEqual('name');
      expect(res[0].executionId).toEqual('execute');
      expect(res[0].instanceId).toEqual('instance');
      expect(res[0].executionType).toEqual('typeA');
      expect(res[0].taskId).toEqual('task');
    });
    const httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({entity: activeTaskItem});
  });

  it ('should get collateral list', () =>  {
    service.getCollateral('', '').subscribe(res => {
      expect(res.length).toEqual(1);
    });
    const  httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({entity: dummyCollateralList});
  });
});
