import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatButtonModule} from '@angular/material/button';
import {ConfirmDialogComponent} from './confirm-dialog.component';

describe('ConfrimDialogComponent', () => {
  let component: ConfirmDialogComponent;
  let fixture: ComponentFixture<ConfirmDialogComponent>;
  let closeBtn: DebugElement;
  let continueBtn: DebugElement;
  let dialog;
  const data = {
    title: 'Warning',
    message: 'Explanation',
    hideCancelButton: false
  };
  const dialogRefMock = {
    close: () => {
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatButtonModule,
        MatDialogModule,
        MatDividerModule],
      declarations: [ConfirmDialogComponent],
      providers: [
        {provide: MatDialogRef, useValue: dialogRefMock},
        {provide: MAT_DIALOG_DATA, useValue: (data)}]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmDialogComponent);
    component = fixture.componentInstance;
    dialog = component.dialogRef;
    continueBtn = fixture.debugElement.query(By.css('#confirm-dialog-yes-btn'));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close dialog on yes button', () => {
    closeBtn = fixture.debugElement.query(By.css('#confirm-dialog-no-btn'));
    spyOn(dialog, 'close');
    closeBtn.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(dialog.close).toHaveBeenCalledWith(false);
  });

  it('should close dialog on no button', () => {
    spyOn(dialog, 'close');
    continueBtn.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(dialog.close).toHaveBeenCalledWith(true);
  });

  it('should have header texts from data', () => {
    const headerText = fixture.nativeElement.querySelector('h1');
    expect(headerText.innerText).toEqual('Warning');
  });

  it('should have p texts from data', () => {
    const headerText = fixture.nativeElement.querySelector('pre');
    expect(headerText.innerText).toEqual('Explanation');
  });
});
