import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ConfirmDialogComponent} from './confirm-dialog.component';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {LoanSearchPageSandbox} from '../../loan-search-page/loan-search-page-sandbox.service';

describe('ConfirmDialogComponent', () => {
  let component: ConfirmDialogComponent;
  let fixture: ComponentFixture<ConfirmDialogComponent>;
  let dialog;
  const dialogMock = {
    close: () => {
    },
    disableClose: false
  };
  const data = {
    taskName: ' Task1',
    message: ' message1',
    confirmButton: 'close',
    executionType: 'enabled'

  };
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmDialogComponent],
      imports: [
        MatDialogModule,
        BrowserDynamicTestingModule
      ],
      providers: [
        LoanSearchPageSandbox,
        {provide: MatDialogRef, useValue: {dialogMock}},
        {provide: MAT_DIALOG_DATA, useValue: (data)}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmDialogComponent);
    component = fixture.componentInstance;
    component.data.taskName = data.taskName;
    component.data.message = data.message;
    dialog = component.dialogRef;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have message text from data', () => {
    const taskName = fixture.nativeElement.querySelector('.taskName');
    const message = fixture.nativeElement.querySelector('.message');
    expect(taskName.innerText).toEqual('Task1');
    expect(message.innerText).toEqual('message1');
  });

  it('should not show taskName when isCreateCollateral is true', () => {
    const taskName = fixture.nativeElement.querySelector('.taskName');
    if (component.data.taskName === 'Task1' && component.data.executionType === 'enabled') {
      expect(taskName).toBeFalsy();
    } else {
      expect(taskName).toBeTruthy();
    }
  });
});
