import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GroupedTaskListDashletComponent} from './grouped-task-list-dashlet.component';

describe('SubModuledTaskListDashletComponent', () => {
  let component: GroupedTaskListDashletComponent;
  let fixture: ComponentFixture<GroupedTaskListDashletComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GroupedTaskListDashletComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GroupedTaskListDashletComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
