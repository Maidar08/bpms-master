import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TaskDashletsContainerComponent} from './task-dashlets-container.component';
import {TaskListDashletComponent} from '../../component/task-list-dashlet/task-list-dashlet.component';
import {MaterialModule} from '../../../material.module';
import {HttpClientModule} from '@angular/common/http';
import {Store, StoreModule} from '@ngrx/store';
import {LoanSearchPageSandbox} from '../../../loan-search-page/loan-search-page-sandbox.service';
import {RouterModule} from '@angular/router';
import {By} from '@angular/platform-browser';
import {TranslateModule} from '@ngx-translate/core';
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {ConfirmDialogComponent} from '../../../common/confirm-dialog/confirm-dialog.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

describe('TaskDashletsContainerComponent', () => {
  let component: TaskDashletsContainerComponent;
  let fixture: ComponentFixture<TaskDashletsContainerComponent>;
  let dialog;
  let getRequestSpy;
  let requestService: LoanSearchPageSandbox;
  const taskInfo = {
    icon: '',
    name: '',
    executionId: '',
    instanceId: '',
    executionType: '',
    taskId: '',
    parentTaskId: '',
    definitionKey: ''
  };
  const dialogRefMock = {
    close: () => {
    }
  };
  const dialogData = {
    taskName: 'name',
    confirmButton: 'Тийм',
    closeButton: 'Үгүй',
    message: 'хуудсыг нээх үү? Одоо бөглөж буй талбарын мэдээллүүд хадгалагдахгүйг анхаарна уу'
  };
  beforeEach(async(() => {
      TestBed.configureTestingModule({
        imports: [
          MaterialModule,
          HttpClientModule,
          BrowserAnimationsModule,
          StoreModule.forRoot({}),
          RouterModule.forRoot([]),
          TranslateModule.forRoot(),
        ],

        declarations: [
          TaskDashletsContainerComponent,
          TaskListDashletComponent,
          ConfirmDialogComponent
        ],
        providers: [Store, LoanSearchPageSandbox, ConfirmDialogComponent,
          {provide: MatDialogRef, useValue: dialogRefMock},
          {provide: MAT_DIALOG_DATA, useValue: (dialogData)}],
      }).overrideModule(BrowserDynamicTestingModule, {set: {entryComponents: [ConfirmDialogComponent]}})
        .compileComponents();
      requestService = TestBed.inject(LoanSearchPageSandbox);
    }
  ));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskDashletsContainerComponent);
    requestService = TestBed.inject(LoanSearchPageSandbox);
    component = fixture.componentInstance;
    dialog = ConfirmDialogComponent;
    getRequestSpy = spyOn(requestService, 'getRequests');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have initial state', () => {
    expect(fixture.debugElement.query(By.css('task-list-dashlet'))).not.toBeNull();
    expect(component.enabledTaskItems).toEqual([]);
  });

  it('should show confirm dialog when activate new task', () => {
    spyOn(component.dialog, 'open').and.callThrough();
    component.manualActivateTask(taskInfo);
    component.dialog.closeAll();
    const config = new MatDialogConfig();
    config.disableClose = false;
    config.data = {
      taskName: taskInfo.name,
      executionType: taskInfo.executionType,
      confirmButton: 'Тийм',
      closeButton: 'Үгүй',
      message: 'үйлдлийг эхлүүлэх үү?'
    };
    fixture.detectChanges();
    expect(component.dialog.open).toHaveBeenCalledWith(ConfirmDialogComponent, config);
  });
});

