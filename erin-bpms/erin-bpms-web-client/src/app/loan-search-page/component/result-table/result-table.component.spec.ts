import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {By} from '@angular/platform-browser';
import {CdkTableModule} from '@angular/cdk/table';
import {RouterModule} from '@angular/router';
import {ResultTableComponent} from './result-table.component';
import {TablePaginatorComponent} from '../../../common/table-paginator/table-paginator.component';
import {MaterialModule} from '../../../material.module';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ColumnDef} from '../../../models/common.model';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {TranslateModule} from '@ngx-translate/core';
import {StoreModule} from '@ngrx/store';
import {RouterTestingModule} from '@angular/router/testing';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {AuthModel} from '../../../models/auth.model';

describe('ResultTableComponent ', () => {
  let component: ResultTableComponent;
  let fixture: ComponentFixture<ResultTableComponent>;
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

  const dummyTableData = [
    {
      id: '1', fullName: '', registerNumber: 'аа11223344', cifNumber: '', instanceId: '', phoneNumber: 1,
      email: '', createdDate: new Date(), productCode: '', amount: '', incomeType: '',
      branchNumber: '', channel: '', assignee: '', userName: '', state: 'ШИНЭ'
    },
    {
      id: '2', fullName: '', registerNumber: 'аа11223344', cifNumber: '', instanceId: '', phoneNumber: 1,
      email: '', createdDate: new Date(), productCode: '', amount: '', incomeType: '',
      branchNumber: '', channel: '', assignee: '', userName: '', state: ''
    },
    {
      id: '3', fullName: '', registerNumber: 'аа11223344', cifNumber: '', instanceId: '', phoneNumber: 1,
      email: '', createdDate: new Date(), productCode: '', amount: '', incomeType: '',
      branchNumber: '', channel: '', assignee: '', userName: '', state: ''
    }
  ];

  const columnTestData: ColumnDef[] = [
    {headerText: 'id', columnDef: 'id'},
    {headerText: 'fullName', columnDef: 'fullName'},
    {headerText: 'registerNumber', columnDef: 'registerNumber'},
    {headerText: 'cifNumber', columnDef: 'cifNumber'},
    {headerText: 'productCode', columnDef: 'productCode'},
    {headerText: 'amount', columnDef: 'amount'},
    {headerText: 'createdDate', columnDef: 'createdDate'},
    {headerText: 'assignee', columnDef: 'assignee'},
    {headerText: 'branchNumber', columnDef: 'branchNumber'},
    {headerText: 'channel', columnDef: 'channel'},
    {headerText: 'state', columnDef: 'state'},
    {headerText: 'button', columnDef: 'button'},
    ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ResultTableComponent,
        TablePaginatorComponent,
        ThousandSeparatorDirective],
      imports: [
        BrowserAnimationsModule,
        MaterialModule,
        CdkTableModule,
        MatMenuModule,
        RouterModule,
        MatIconModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        StoreModule.forRoot({}),
        RouterTestingModule
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    sb = TestBed.inject(LoanSearchPageSandbox);
    fixture = TestBed.createComponent(ResultTableComponent);
    component = fixture.componentInstance;
    component.data = dummyTableData;
    component.columns = columnTestData;
    component.userPermissions = dummyPermission;
    component.ngOnChanges();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have table with columns and rows', () => {
    const headerCell = fixture.debugElement.queryAll(By.css('mat-header-cell'));
    expect(headerCell).toBeTruthy();
    expect(headerCell.length).toEqual(13);

    const rowElement = fixture.debugElement.queryAll(By.css('mat-row'));
    expect(rowElement).toBeTruthy();
    expect(rowElement.length).toEqual(3);
  });

  it('should show date span when isDate is true ', () => {
    const length = component.columns.length;
    for (const data of component.data) {
      for (let i = 0; i < length; i++ ) {
        const index = i + 1;
        const dataValue = data[component.columns[i].columnDef];
        if (index % length === 7) {
          expect(component.isDate(dataValue)).toBeTruthy();
        } else {
          expect(component.isDate(dataValue)).toBeFalsy();
        }
      }
    }
  });

  it('should use thousand separator on span when useSeparator is true ', () => {
    const length = component.columns.length;
    for (let i = 0; i < length; i++ ) {
      const index = i + 1;
      if (index % length === 6) {
        expect(component.useSeparator(component.columns[i].columnDef)).toBeTruthy();
      } else {
        expect(component.useSeparator(component.columns[i].columnDef)).toBeFalsy();
      }
    }
  });

  it('should uppercase register number', () => {
      component.data.map(data => {
        expect(data.registerNumber).toEqual('АА11223344');
      });
  });

  it('should get users by id', () => {
    spyOn(sb, 'getUsersById').and.returnValue({ subscribe: () => {} });
    component.openDialog();
    expect(sb.getUsersById).toHaveBeenCalled();
  });

});

