import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoanContractCaseViewComponent} from './loan-contract-case-view.component';

describe('LoanContractCaseViewComponent', () => {
  let component: LoanContractCaseViewComponent;
  let fixture: ComponentFixture<LoanContractCaseViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LoanContractCaseViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoanContractCaseViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
