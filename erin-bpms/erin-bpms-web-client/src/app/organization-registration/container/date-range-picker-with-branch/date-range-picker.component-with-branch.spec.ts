import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DateRangePickerComponentWithBranch} from './date-range-picker.component-with-branch';

describe('DateRangePickerComponent', () => {
  let component: DateRangePickerComponentWithBranch;
  let fixture: ComponentFixture<DateRangePickerComponentWithBranch>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DateRangePickerComponentWithBranch ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DateRangePickerComponentWithBranch);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
