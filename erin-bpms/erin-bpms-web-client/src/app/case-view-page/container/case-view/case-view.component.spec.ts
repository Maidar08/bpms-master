import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CaseViewComponent} from './case-view.component';
import {MainWorkspaceComponent} from '../main-workspace/main-workspace.component';
import {CaseFolderComponent} from '../case-folder/case-folder.component';
import {TaskDashletsContainerComponent} from '../task-dashlets-container/task-dashlets-container.component';
import {TaskListDashletComponent} from '../../component/task-list-dashlet/task-list-dashlet.component';
import {DynamicFieldsComponent} from '../../component/dynamic-fields/dynamic-fields.component';
import {MaterialModule} from '../../../material.module';
import {MatCardModule} from '@angular/material/card';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';
import {Store, StoreModule} from '@ngrx/store';
import {MatOptionModule} from '@angular/material/core';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {By} from '@angular/platform-browser';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {SalaryTableComponent} from '../salary-table/salary-table.component';
import {ErinSimpleInputFieldComponent} from '../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ErinDatepickerFieldComponent} from '../../../common/erin-fields/erin-datepicker-field/erin-datepicker-field.component';
import {ErinDropdownFieldComponent} from '../../../common/erin-fields/erin-dropdown-field/erin-dropdown-field.component';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {ErinNumberInputFieldComponent} from '../../../common/erin-fields/erin-number-input-field/erin-number-input-field.component';
import {ScanFingerprintFieldComponent} from '../../../common/erin-fields/scan-fingerprint-field/scan-fingerprint-field.component';
import {SalaryCalculationTableComponent} from '../../component/salary-calculation-table/salary-calculation-table.component';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {CdkTableModule} from '@angular/cdk/table';
import {ProcessRequestService} from '../../../loan-search-page/services/process-request.service';
import {of} from 'rxjs';
import {FormsModel} from '../../../models/app.model';
import {LoanSearchPageSandbox} from '../../../loan-search-page/loan-search-page-sandbox.service';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TaskItem, TaskModel} from '../../model/task.model';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {DatePipe} from '@angular/common';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('CaseViewComponent', () => {
  let component: CaseViewComponent;
  let fixture: ComponentFixture<CaseViewComponent>;
  let service;
  let sb;
  let mainWorkspace;
  let taskDashlets;
  let instanceId;

  const taskItems: TaskItem[] = [
    {icon: '', name: 'task1', executionId: '', instanceId: 'instanceId', executionType: '', taskId: 'task1', definitionKey: 'key1', parentTaskId: 'task4'},
    {icon: '', name: 'task2', executionId: '', instanceId: 'instanceId', executionType: '', taskId: 'task2',  definitionKey: 'key1', parentTaskId: 'task5'},
    {icon: '', name: 'task3', executionId: '', instanceId: 'instanceId', executionType: '', taskId: 'task3', definitionKey: 'key1', parentTaskId: 'task6'},
    ];
  const dummyFormModel: FormsModel = {
    id: 'string',
    formFieldValue: {defaultValue: '', valueInfo: ''},
    label: 'string',
    type: 'string',
    options: [],
    validations: [{
      name: 'string;',
      configuration: 'any;'
    }],
    required: false
  };
  const taskModel: TaskModel = {
    taskFormFields: [dummyFormModel],
    taskId: 'string',
    formId: 'string'};

  class MockGetDynamicForms {
    private taskId: string;

    public getTaskId(): string {
      return this.taskId;
    }

    public setTaskId(taskId: string): void {
      this.taskId = taskId;
    }
  }

  class MockgetTaskItems {
    private instanceId: string;

    public getInstanceId(): string {
      return this.instanceId;
    }

    public setInstanceId(taskId: string): void {
      this.instanceId = taskId;
    }
  }

  const mockGetDynamicForms = new MockGetDynamicForms();
  const mockgetTaskItems = new MockgetTaskItems();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        CdkTableModule,
        MaterialModule,
        MatListModule,
        MatCardModule,
        MatIconModule,
        RouterModule,
        HttpClientModule,
        HttpClientTestingModule,
        BrowserDynamicTestingModule,
        BrowserAnimationsModule,
        StoreModule.forRoot({}),
        RouterModule.forRoot([]),
        MatSelectModule,
        MatOptionModule,
        MatFormFieldModule,
        FormsModule,
        ReactiveFormsModule,
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        })
      ],
      declarations: [
        ErinLoaderComponent,
        CaseViewComponent,
        MainWorkspaceComponent,
        CaseFolderComponent,
        TaskDashletsContainerComponent,
        DynamicFieldsComponent,
        TaskListDashletComponent,
        SalaryTableComponent,
        ErinSimpleInputFieldComponent,
        ErinDatepickerFieldComponent,
        ErinDropdownFieldComponent,
        ErinLoaderComponent,
        ErinNumberInputFieldComponent,
        ScanFingerprintFieldComponent,
        SalaryCalculationTableComponent,
        ThousandSeparatorDirective
      ],
      providers: [Store, ProcessRequestService, LoanSearchPageSandbox, DatePipe,
        {provide: taskItems, useClass: MockGetDynamicForms},
        {provide: instanceId, useClass: MockgetTaskItems}],
    }).overrideModule(BrowserDynamicTestingModule, {
      set: {
        entryComponents: [ErinLoaderComponent],
      }
    })
      .compileComponents();
  }));

  beforeEach(() => {
    service = TestBed.inject(CaseViewSandboxService);
    sb = TestBed.inject(CaseViewSandboxService);
    spyOn(service, 'getInstanceForm').and.returnValue(of(taskModel));
    spyOn(service, 'getCustomerVariables').and.returnValue(of([dummyFormModel]));
    spyOn(service, 'getActiveTasks').and.returnValue(of([taskItems]));
    mainWorkspace = TestBed.createComponent(MainWorkspaceComponent);
    taskDashlets = TestBed.createComponent(TaskDashletsContainerComponent);
    fixture = TestBed.createComponent(CaseViewComponent);
    component = fixture.componentInstance;
    component.taskDashlets.activeTaskItems = taskItems;
    component.instanceId = 'instanceId';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should component to be initial state', () => {
    expect(fixture.debugElement.query(By.css('main-workspace'))).not.toBeNull();
    expect(fixture.debugElement.query(By.css('case-folder'))).not.toBeNull();
    expect(fixture.debugElement.query(By.css('task-dashlets-container'))).not.toBeNull();
  });


  it('should open Salary Calculation', () => {
    component.isSalaryTask = false;
    const hideSalary = component.mainWorkspace.hideSalaryTable = true;
    component.checkSalaryTaskState(hideSalary);
    fixture.detectChanges();
    expect(component.isSalaryTask).toBeTruthy();
  });
  it('should update mainWorkspace, caseFolder and taskDashlets when activate task ', () => {
    const getDynamic = mockGetDynamicForms.setTaskId('string');
    const getTaskItems = mockgetTaskItems.setInstanceId('string');
    expect(getDynamic).not.toBeNull();
    expect(getTaskItems).not.toBeNull();
    expect(component.requestDetail.getVariables()).not.toBeNull();
  });
  it('should get current TaskId', () => {
    const taskId = taskItems[0].taskId;
    component.bindTaskId(taskId);
    expect(component.currentTaskId).toEqual(taskId);
  });

  it('should bind Save Action', () => {
    component.bindSaveAction(true);
    expect(component.saved).toBeTruthy();
  });

  it('should open taskDashlet', () =>{
    const openDashletButton = fixture.debugElement.query(By.css('.taskDashletButton'));
    openDashletButton.triggerEventHandler('click', null);
    expect(component.isTabletSidenavOpen).toBeTruthy();
  });
  it('should not stretch CaseFolder', () => {
    if(component.mainWorkspace.hideMainWorkspace === true){
      expect(component.stretchCaseFolder()).toBeTruthy();
    } else {
      expect(component.stretchCaseFolder()).toBeFalsy();
    }
  });

  it('should refresh sections to continue task form', () => {
     component.refreshContinueTask(true);
     expect(sb.getActiveTasks).toHaveBeenCalled();
   });

  it('should show completed task', ()=>{
     component.showCompletedTask('string');
     expect(component.mainWorkspace.getDynamicForms()).not.toBeNull();
     expect(component.requestDetail.getVariables()).not.toBeNull();
     expect(component.mainWorkspace.getDynamicForms()).not.toBeNull();
  })
});
