import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {EbankRequestPageComponent} from './ebank-request-page.component';
import {HttpClientModule} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {MaterialModule} from '../../../material.module';
import {TranslateModule} from '@ngx-translate/core';
import {StoreModule} from '@ngrx/store';
import {RouterTestingModule} from '@angular/router/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {RequestModel} from '../../models/process.model';

const requestModel: RequestModel = {
  id: '123', fullName: '123', registerNumber: '123', cifNumber: '123', instanceId: '123', phoneNumber: 123,
  email: '123', createdDate: '123', productCode: '', amount: '', branchNumber: '', channel: '',
  assignee: '', userName: '', state: ''
};
const isReadOnlyReq = false;

describe('EbankRequestPageComponent', () => {
  let component: EbankRequestPageComponent;
  let fixture: ComponentFixture<EbankRequestPageComponent>;
  let sb;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EbankRequestPageComponent ],
      imports: [
        HttpClientModule,
        HttpClientTestingModule,
        MaterialModule,
        TranslateModule.forRoot(),
        StoreModule.forRoot({}),
        RouterTestingModule,
        BrowserAnimationsModule,
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    sb = TestBed.inject(LoanSearchPageSandbox);
    fixture = TestBed.createComponent(EbankRequestPageComponent);
    component = fixture.componentInstance;

    spyOn(sb, 'getEbankRequests').and.returnValue({ subscribe: () => {} });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start new process when state is NEW', () => {
    const model = requestModel;
    model.state = 'ШИНЭ';
    spyOn(sb, 'startProcess').and.callThrough();
    component.clickedOnItem(model, isReadOnlyReq);
    expect(sb.startProcess).toHaveBeenCalled();
  });

  it('should navigate to caseView when state is not NEW ', () => {
    const model = requestModel;
    model.state = 'СУДЛАГДАЖ БАЙНА';
    spyOn(sb, 'routeToCaseView').and.callThrough();
    component.clickedOnItem(model, isReadOnlyReq);
    expect(sb.routeToCaseView).toHaveBeenCalled();
  });

  it('should get requests', () => {
    component.ngOnInit();
    expect(sb.getEbankRequests).toHaveBeenCalled();
  });
});
