import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {BranchRequestPageComponent} from './branch-request-page.component';
import {RequestModel} from '../../models/process.model';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {ResultTableComponent} from '../../component/result-table/result-table.component';
import {TablePaginatorComponent} from '../../../common/table-paginator/table-paginator.component';
import {SearchInputComponent} from '../../component/search-input/search-input.component';
import {DialogService} from '../../../services/dialog.service';
import {MaterialModule} from '../../../material.module';
import {CdkTableModule} from '@angular/cdk/table';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {StoreModule} from '@ngrx/store';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {of, Subject} from 'rxjs';
import {By} from '@angular/platform-browser';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AuthModel} from '../../../models/auth.model';

describe('BranchLoanRequestSearchPageComponent', () => {
  let component: BranchRequestPageComponent;
  let childComponent: ResultTableComponent;
  let fixture: ComponentFixture<BranchRequestPageComponent>;
  let requestService: LoanSearchPageSandbox;
  let getRequestSpy;
  let newSbj;
  let sb;

  const dummyPermission: AuthModel = { userName: 'branchSpecialist108', role: undefined, userGroup: '108',
    permissions: [
      {id: 'bpms.bpm.UploadDocuments', properties: {disable: null, visible: null}},
      {id: 'bpms.bpm.CreateProcessRequest', properties: {disable: null, visible: null}},
      {id: 'GetGroupProcessRequests', properties: {disable: null, visible: null}},
      {id: 'GetProcessTypes', properties: {disable: null, visible: null}},
      {id: 'GetProcessRequest', properties: {disable: null, visible: null}},
      {id: 'GetProcessRequestsByAssignedUserId', properties: {disable: null, visible: null}},
      {id: 'UpdateAssignedUser', properties: {disable: null, visible: null}},
      {id: 'SearchByRegisterNumber', properties: {disable: null, visible: null}},
      {id: 'StartProcess', properties: {disable: null, visible: null}},
    ]
  };

  const dummyRequest: RequestModel = {
    id: '1',
    fullName: 'John',
    registerNumber: 'AA20200297'
    ,
    cifNumber: 'R0000001',
    phoneNumber: 99998888,
    email: 'erin@test.systems',
    productCode: 'DE02',
    amount: '3000000',
    channel: 'Phone',
    state: 'ШИНЭ',
    instanceId: '123',
    createdDate: 'string',
    branchNumber: 'string',
    assignee: 'string',
    userName: 'string',
    organizationType: 'new'
  };

  const requestModel: RequestModel = {
    id: '123', fullName: '123', registerNumber: '123', cifNumber: '123', instanceId: '123', phoneNumber: 123,
    email: '123', createdDate: '123', productCode: '', amount: '', branchNumber: '', channel: '',
    assignee: '', userName: '', state: ''
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, CdkTableModule, HttpClientTestingModule, RouterTestingModule,
        StoreModule.forRoot({}),
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        }),
      BrowserAnimationsModule],
      declarations: [
        BranchRequestPageComponent,
        ResultTableComponent,
        TablePaginatorComponent,
        SearchInputComponent,
        ThousandSeparatorDirective,
      ],
      providers: [LoanSearchPageSandbox, DialogService]
    })
    .compileComponents();
    requestService = TestBed.inject(LoanSearchPageSandbox);
  }));

  beforeEach(() => {
    const dialogService: DialogService = TestBed.get(DialogService);
    newSbj = new Subject<string>();
    sb = TestBed.inject(LoanSearchPageSandbox);
    dialogService.processDialogSbj = newSbj;
    getRequestSpy = spyOn(requestService, 'getRequests');
    getRequestSpy.and.returnValue(of([]));
    fixture = TestBed.createComponent(BranchRequestPageComponent);
    component = fixture.componentInstance;
    childComponent = TestBed.createComponent(ResultTableComponent).componentInstance;
    childComponent.userPermissions = dummyPermission;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(fixture.debugElement.queryAll(By.css('mat-row')).length).toEqual(0);
  });
  it('should have table with data', () => {
    getRequestSpy.and.returnValue(of([dummyRequest]));
    component.ngOnInit();
    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('mat-row')).length).toEqual(1);
  });

  it('should update table on dialog subject next', () => {
    getRequestSpy.and.returnValue(of([dummyRequest, dummyRequest]));
    newSbj.next('');
    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('mat-row')).length).toEqual(2);
  });

  it('should start new process when state is NEW', () => {
    const model = requestModel;
    model.state = 'ШИНЭ';
    spyOn(sb, 'startProcess').and.callThrough();
    component.clickedOnItem(model, false);
    expect(sb.startProcess).toHaveBeenCalled();
  });

  it('should navigate to caseView when state is not NEW ', () => {
    const model = requestModel;
    model.state = 'СУДЛАГДАЖ БАЙНА';
    spyOn(sb, 'routeToCaseView');
    component.clickedOnItem(model, false);
    expect(sb.routeToCaseView).toHaveBeenCalled();
  });

  it('should get requests', () => {
    component.ngOnInit();
    expect(sb.getRequests).toHaveBeenCalled();
  });
});
