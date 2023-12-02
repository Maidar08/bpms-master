import {async, ComponentFixture, TestBed} from '@angular/core/testing';


import {ConfirmDeleteDialogComponent} from './confirm-delete-dialog.component';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';

describe('ConfirmDeleteDialogComponent', () => {
  let component: ConfirmDeleteDialogComponent;
  let fixture: ComponentFixture<ConfirmDeleteDialogComponent>;
  let dialog;
  const dialogMock = {
    close: () => {
    },
    disableClose: false
  };
  const data = {
    taskName: ' Task1',
    message: ' message1',
    confirmButton: 'close'

  };
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmDeleteDialogComponent],
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
    fixture = TestBed.createComponent(ConfirmDeleteDialogComponent);
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
