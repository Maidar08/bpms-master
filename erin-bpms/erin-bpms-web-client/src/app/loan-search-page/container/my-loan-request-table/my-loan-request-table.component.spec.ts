import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {MyLoanRequestTableComponent} from './my-loan-request-table.component';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {RequestModel} from '../../models/process.model';
import {ResultTableComponent} from '../../component/result-table/result-table.component';
import {SearchInputComponent} from '../../component/search-input/search-input.component';
import {TablePaginatorComponent} from '../../../common/table-paginator/table-paginator.component';
import {MaterialModule} from '../../../material.module';
import {CdkTableModule} from '@angular/cdk/table';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {StoreModule} from '@ngrx/store';
import {TranslateModule} from '@ngx-translate/core';
import {DialogService} from '../../../services/dialog.service';
import {of, Subject} from 'rxjs';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {By} from '@angular/platform-browser';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

describe('MyLoanRequestTableComponent', () => {
  let component: MyLoanRequestTableComponent;
  let fixture: ComponentFixture<MyLoanRequestTableComponent>;
  let requestService: LoanSearchPageSandbox;
  let getMyRequestSpy;
  let newSubject;
  let sb;
  const myLoanRequests: RequestModel = {
    id: '2',
    fullName: 'Mike',
    registerNumber: 'AB00112233',
    cifNumber: 'R0000001',
    phoneNumber: 99887766,
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
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        MyLoanRequestTableComponent,
        ResultTableComponent,
        SearchInputComponent,
        TablePaginatorComponent,
        ThousandSeparatorDirective,
      ],
      imports: [
        MaterialModule,
        CdkTableModule,
        HttpClientTestingModule,
        RouterTestingModule,
        BrowserDynamicTestingModule,
        BrowserAnimationsModule,
        StoreModule.forRoot({}),
        TranslateModule.forRoot()],
      providers: [LoanSearchPageSandbox, DialogService]
    })
      .compileComponents();
    requestService = TestBed.get(LoanSearchPageSandbox);
  }));

  beforeEach(() => {
    const dialogService: DialogService = TestBed.get(DialogService);
    newSubject = new Subject<string>();
    dialogService.processDialogSbj = newSubject;
    getMyRequestSpy = spyOn(requestService, 'getMyLoanRequests');
    getMyRequestSpy.and.returnValue(of([]));
    sb = TestBed.inject(LoanSearchPageSandbox);
    fixture = TestBed.createComponent(MyLoanRequestTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have table with data', () => {
    getMyRequestSpy.and.returnValue(of([myLoanRequests]));
    component.ngOnInit();
    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('mat-row')).length).toEqual(1);
  });
  it('should update table on dialog subject next', () => {
    getMyRequestSpy.and.returnValue(of([myLoanRequests, myLoanRequests]));
    newSubject.next('');
    fixture.detectChanges();
    expect(fixture.debugElement.queryAll(By.css('mat-row')).length).toEqual(2);
  });

  it('should start new process when state is NEW', () => {
    const model = myLoanRequests;
    model.state = 'ШИНЭ';
    spyOn(sb, 'startProcess').and.callThrough();
    component.clickedOnItem(model, false);
    expect(sb.startProcess).toHaveBeenCalled();
  });

  it('should navigate to caseView when state is not NEW ', () => {
    const model = myLoanRequests;
    model.state = 'СУДЛАГДАЖ БАЙНА';
    spyOn(sb, 'routeToCaseView').and.callThrough();
    component.clickedOnItem(model, false);
    expect(sb.routeToCaseView).toHaveBeenCalled();
  });

  it('should get requests', () => {
    component.ngOnInit();
    expect(sb.getMyLoanRequests).toHaveBeenCalled();
  });

});
