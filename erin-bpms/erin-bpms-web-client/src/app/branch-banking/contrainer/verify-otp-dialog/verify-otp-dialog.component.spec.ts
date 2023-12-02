import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {VerifyOtpDialogComponent} from './verify-otp-dialog.component';

describe('VerifyOtpDialogComponent', () => {
  let component: VerifyOtpDialogComponent;
  let fixture: ComponentFixture<VerifyOtpDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VerifyOtpDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifyOtpDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
