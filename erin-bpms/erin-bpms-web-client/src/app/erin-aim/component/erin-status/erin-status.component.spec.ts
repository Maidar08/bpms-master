import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ErinStatusComponent} from './erin-status.component';

describe('ErinStatusComponent', () => {
  let component: ErinStatusComponent;
  let fixture: ComponentFixture<ErinStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ErinStatusComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErinStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
