import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DownloadExcelComponent} from './download-excel.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {TranslateModule} from '@ngx-translate/core';


describe('DownloadExcelComponent', () => {
  let component: DownloadExcelComponent;
  let fixture: ComponentFixture<DownloadExcelComponent>;

  class MockLoanSearchPageSandbox {
    private topHeader;
    private searchKey;
    private params;

    getDownloadExcelReport() {
      return this.params;
    }
    setDownloadExcelReport(headerValue, searchKeyValue) {
      this.topHeader = headerValue;
      this.searchKey = searchKeyValue;
      this.params = [headerValue, searchKeyValue];
    }
  }
  const mockLoanSearchSb = new MockLoanSearchPageSandbox();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DownloadExcelComponent],
      imports: [
        HttpClientTestingModule,
        MatSnackBarModule,
        BrowserDynamicTestingModule,
        TranslateModule.forRoot()
      ],
      providers: [
        {provide: LoanSearchPageSandbox, useValue: mockLoanSearchSb},
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadExcelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should get response after click', () => {
    mockLoanSearchSb.setDownloadExcelReport('my-loan-request', 'НР76010571');
    expect(mockLoanSearchSb.getDownloadExcelReport()).toEqual(['my-loan-request', 'НР76010571']);
  });
});
