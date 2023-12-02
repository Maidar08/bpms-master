import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ConfirmRemoveCoBorrowerDialogComponent} from './confirm-remove-coBorrower-dialog.component';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {LoanSearchPageSandbox} from '../../../loan-search-page/loan-search-page-sandbox.service';

describe('ConfirmRemoveCoBorrowerDialogComponent', () => {
  let component: ConfirmRemoveCoBorrowerDialogComponent;
  let fixture: ComponentFixture<ConfirmRemoveCoBorrowerDialogComponent>;
  let dialog;
  const dialogMock = {
    close: () => {
    },
    disableClose: false
  };
  const data = {
    taskName: ' Task',
    message: ' message',
    confirmButton: 'close'

  };
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmRemoveCoBorrowerDialogComponent],
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
    fixture = TestBed.createComponent(ConfirmRemoveCoBorrowerDialogComponent);
    component = fixture.componentInstance;
    dialog = component.dialogRef;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have message text from data', () => {
    const taskName = fixture.nativeElement.querySelector('p');
    expect(taskName.innerText).not.toBe(null);
  });
});
