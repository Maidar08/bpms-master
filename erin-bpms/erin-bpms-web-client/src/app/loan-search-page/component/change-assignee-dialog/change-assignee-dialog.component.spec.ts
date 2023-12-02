import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ChangeAssigneeDialogComponent} from './change-assignee-dialog.component';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';

describe('ChangeAssigneeDialogComponent', () => {
  let component: ChangeAssigneeDialogComponent;
  let fixture: ComponentFixture<ChangeAssigneeDialogComponent>;
  let dialog;
  const dialogMock = {
    close: () => {
    },
    disableClose: false
  };
  const data = {
    taskName: ' Task1',
    message: ' Хүсэлт шилжүүлэх',
    confirmButton: 'close'

  };
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatDialogModule,
        BrowserDynamicTestingModule
      ],
      providers: [
        LoanSearchPageSandbox,
        {provide: MatDialogRef, useValue: {dialogMock}},
        {provide: MAT_DIALOG_DATA, useValue: (data)}
      ],
      declarations: [ ChangeAssigneeDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeAssigneeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have message text from data', () => {
    const taskName = fixture.nativeElement.querySelector('p');
    expect(taskName.innerText).toEqual('Хүсэлт шилжүүлэх');
  });
});
