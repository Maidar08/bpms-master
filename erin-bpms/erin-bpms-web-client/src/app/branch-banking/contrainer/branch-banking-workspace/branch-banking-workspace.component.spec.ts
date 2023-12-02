import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {BranchBankingWorkspaceComponent} from './branch-banking-workspace.component';
import {DynamicFieldsComponent} from '../../../case-view-page/component/dynamic-fields/dynamic-fields.component';
import {ErinDatepickerFieldComponent} from '../../../common/erin-fields/erin-datepicker-field/erin-datepicker-field.component';
import {ErinDropdownFieldComponent} from '../../../common/erin-fields/erin-dropdown-field/erin-dropdown-field.component';
import {ErinNumberInputFieldComponent} from '../../../common/erin-fields/erin-number-input-field/erin-number-input-field.component';
import {ErinSimpleInputFieldComponent} from '../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {ErinButtonComponent} from '../../../common/erin-fields/erin-button/erin-button.component';
import {CdkTableModule} from '@angular/cdk/table';
import {MaterialModule} from '../../../material.module';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule, NoopAnimationsModule} from '@angular/platform-browser/animations';
import {StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {CaseServiceSandbox} from '../../../services/sandbox/case-service-sandbox';
import {BranchBankingSandbox} from '../../services/branch-banking-sandbox.service';
import {FormRelationService} from '../../../case-view-page/services/form-relation.service';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {TreeNode} from '../../../common/model/erin-model';
import {of} from 'rxjs';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {CaseViewSandboxService} from "../../../case-view-page/case-view-sandbox.service";

const treeNode: TreeNode = {processId: 'taxPaying', label: 'Татвар төлөх'};
const dummyRes = [
  {
    processId: 'caseId', label: 'Гүйлгээ',
    children: [
      {processId: 'processId1', label: 'Зээл урьдчилан төлөх', children: []},
      {
        processId: 'processId2', label: 'Билингийн үйлчилгээ', children: [
          {processId: 'taxPaying', label: 'Татвар төлөх', children: []}
        ]
      },
      {processId: 'processId3', label: 'Цалин ЗЭТ', children: []},
      {
        processId: 'processId4', label: 'Гүйлгээний баримт хэвлэх',
        children: [
          {processId: 'processId5', label: 'Харилцагчийн гүйлгээний маягт', children: []},
          {processId: 'processId6', label: 'Мемориал', children: []},
          {processId: 'processId7', label: 'Цахим гүйлгээ', children: []}
        ]
      }
    ]
  }
];
const transactionList = [
  {
    transactionId: 'XB0004498',
    transactionDate: '2021-01-28T00:00:00.000+08:00',
    transactionAmount: '900',
    transactionCCY: 'MNT',
    branchId: '101',
    accountId: '5000000674',
    userId: 'XAC41',
    type: 'T',
    subType: 'CI',
    status: 'N',
    particulars: 'null'
  }
];

describe('BranchBankingWorkspaceComponent', () => {
  let component: BranchBankingWorkspaceComponent;
  let fixture: ComponentFixture<BranchBankingWorkspaceComponent>;
  let caseService: CaseServiceSandbox;
  let branchBankingService: BranchBankingSandbox;
  let relationService: FormRelationService;
  let commonService: CommonSandboxService;
  let caseViewSandboxService: CaseViewSandboxService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        CdkTableModule,
        MaterialModule,
        FormsModule,
        MatIconModule,
        MatCardModule,
        HttpClientModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
        HttpClientTestingModule,
        StoreModule.forRoot({}),
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        }),
      ],
      declarations: [
        BranchBankingWorkspaceComponent,
        DynamicFieldsComponent,
        ErinDatepickerFieldComponent,
        ErinDropdownFieldComponent,
        ErinNumberInputFieldComponent,
        ErinSimpleInputFieldComponent,
        ErinLoaderComponent,
        ErinButtonComponent,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    caseService = TestBed.inject(CaseServiceSandbox);
    branchBankingService = TestBed.inject(BranchBankingSandbox);
    caseViewSandboxService = TestBed.inject(CaseViewSandboxService);
    relationService = TestBed.inject(FormRelationService);
    commonService = TestBed.inject(CommonSandboxService);
    fixture = TestBed.createComponent(BranchBankingWorkspaceComponent);
    component = fixture.componentInstance;
    spyOn(caseService, 'getCasesByCategory').and.returnValue(of(dummyRes));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  xit('should get Task Item', () => {
    component.setTaskItem(treeNode);
    expect(component.form).not.toBe(null);
  });

  xit('should getTransactionList and show Table', () => {
    component.getTransactionList();
    spyOn(branchBankingService, 'getTransactionList').and.returnValue(of(transactionList));
    fixture.detectChanges();
    expect(component.tableData).not.toBe(null);
  });

  xit('should getTransactionDocument and get File', () => {
    component.getTransactionDocument();
    const getDocument = spyOn(branchBankingService, 'getTransactionDocument').and.callThrough();
    expect(getDocument).toBeTruthy();
  });

  it('should get Action name', () => {
    component.handleAction('getTaxInvoiceList');
    spyOn(branchBankingService, 'getTaxInvoiceList').and.returnValue(of(transactionList));
    expect(component.tableData).not.toBe(null);
  });

  it('should get custom invoice list', () => {
    component.handleAction('getCustomInvoiceList');
    spyOn(branchBankingService, 'getCustomInvoiceList').and.callThrough();
    expect(branchBankingService.getCustomInvoiceList).toHaveBeenCalled();
    expect(component.tableData).not.toBe(null);
  })


  it('should submit data ', () => {
    component.handleAction('tableRowDoubleClick');
    spyOn(caseViewSandboxService, 'submitThenCallUserTask').and.callThrough();
    expect(caseViewSandboxService.submitThenCallUserTask).toHaveBeenCalled();
  })
});
