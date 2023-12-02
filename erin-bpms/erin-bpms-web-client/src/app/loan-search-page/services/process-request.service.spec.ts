import {async, TestBed} from '@angular/core/testing';
import {ProcessRequestService} from './process-request.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {BrowserAnimationsModule, NoopAnimationsModule} from '@angular/platform-browser/animations';
import {TranslateModule} from '@ngx-translate/core';
import {StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';


describe('ProcessService', () => {
  let service: ProcessRequestService;
  let httpMock: HttpTestingController;
  let snack: MatSnackBar;
  const dummyRequests = [
    {
      id: '1',
      fullName: 'John',
      state: 'NEW',
      instanceId: null,
      channel: 'Phone',
      userId: 'admin',
      registerNumber: 'AA20200298',
      cifNumber: 'R0000001',
      phoneNumber: 99998888,
      email: 'erin@test.systems',
      branchNumber: 'ICC',
      productCategory: 'CONSUMPTION_LOAN',
      amount: '3,000,000',
      createdDate: '2000-04-06'
    }
  ];
  const processTypes = [
    {
      id: 'CONSUMPTION_LOAN',
      definitionKey: 'bpms_consumption_loan_case',
      name: 'Хэрэглээний зээл',
      version: null,
      processDefinitionType: 'CASE'
    }
  ];
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
    service = TestBed.inject(ProcessRequestService);
    httpMock = TestBed.inject(HttpTestingController);
    snack = TestBed.inject(MatSnackBar);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get mapped Request models ', () => {
    service.getRequests('', '', '').subscribe(res => {
      expect(res.length).toEqual(1);
      expect(res[0].id).toEqual('1');
      expect(res[0].fullName).toEqual('John');
      expect(res[0].cifNumber).toEqual('R0000001');
      expect(res[0].registerNumber).toEqual('AA20200298');
      expect(res[0].instanceId).toEqual(null);
      expect(res[0].phoneNumber).toEqual(99998888);
      expect(res[0].email).toEqual('erin@test.systems');
      expect(res[0].createdDate).toEqual('2000-04-06');
      expect(res[0].amount).toEqual('3,000,000');
      expect(res[0].branchNumber).toEqual('ICC');
      expect(res[0].channel).toEqual('Phone');
    });
    const httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({entity: dummyRequests});
  });

  it('should get mapped my assigned loan requests', () => {
    service.getMyLoanRequests('', '', '').subscribe(res => {
      expect(res.length).toEqual(1);
      expect(res[0].id).toEqual('1');
      expect(res[0].fullName).toEqual('John');
      expect(res[0].cifNumber).toEqual('R0000001');
      expect(res[0].registerNumber).toEqual('AA20200298');
      expect(res[0].instanceId).toEqual(null);
      expect(res[0].phoneNumber).toEqual(99998888);
      expect(res[0].email).toEqual('erin@test.systems');
      expect(res[0].createdDate).toEqual('2000-04-06');
      expect(res[0].amount).toEqual('3,000,000');
      expect(res[0].branchNumber).toEqual('ICC');
      expect(res[0].channel).toEqual('Phone');
    });
    const httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({entity: dummyRequests});
  });
  it('should get mapped process type', () => {
    service.getProcessTypes("LOAN").subscribe(res => {
      expect(res.length).toEqual(1);
      expect(res[0].id).toEqual('CONSUMPTION_LOAN');
      expect(res[0].definitionKey).toEqual('bpms_consumption_loan_case');
      expect(res[0].name).toEqual('Хэрэглээний зээл');
      expect(res[0].version).toEqual(null);
      expect(res[0].processDefinitionType).toEqual('CASE');
    });
    const httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({entity: processTypes});
  });

  it('should throw error message', () => {
    service.getProcessTypes('LOAN').subscribe(() => {
      },
      error => {
        expect(error).toEqual('Процесс төрөл сонгоход алдаа гарлаа! ');
      });
    const httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({}, {status: 500, statusText: 'Internal Server Error'});
  });

  it('should get request form', () => {
    service.getRequestForm('').subscribe(res => {
      expect(res[0].id).toEqual('formId123');
    });
    const httpRequest = httpMock.expectOne({method: 'GET'});
    httpRequest.flush({entity: {taskFormFields: formsModel}});
  });

  it('should create request', () => {
    service.createRequest('', [], '', '', '').subscribe(res => {
      expect(res).toEqual('typeId1234');
    });
    const httpRequest = httpMock.expectOne({method: 'POST'});
    httpRequest.flush({entity: createReq});
  });

});
