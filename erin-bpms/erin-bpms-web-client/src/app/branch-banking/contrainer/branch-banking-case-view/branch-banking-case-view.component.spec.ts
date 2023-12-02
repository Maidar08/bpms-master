import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {BranchBankingCaseViewComponent} from './branch-banking-case-view.component';

describe('BranchBankingCaseViewComponent', () => {
  let component: BranchBankingCaseViewComponent;
  let fixture: ComponentFixture<BranchBankingCaseViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BranchBankingCaseViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchBankingCaseViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
