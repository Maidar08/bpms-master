import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TaskListDashletComponent} from './task-list-dashlet.component';
import {MaterialModule} from '../../../material.module';
import {By} from '@angular/platform-browser';
import {TaskDashletsContainerComponent} from '../../container/task-dashlets-container/task-dashlets-container.component';
import {HttpClientModule} from '@angular/common/http';
import {StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {DebugElement} from '@angular/core';

describe('TaskListDashletComponent', () => {
  let component: TaskListDashletComponent;
  let fixture: ComponentFixture<TaskListDashletComponent>;
  const TaskItem = [
    {icon: 'a', name: 'test', executionId: 'test1', instanceId: 'test1', executionType: 'active', taskId: '', parentTaskId: '', definitionKey: ''},
    {icon: 'b', name: 'test', executionId: 'test2', instanceId: 'test2', executionType: 'enabled', taskId: '', parentTaskId: '', definitionKey: ''},
  ];
  const HeaderText = 'test';

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MaterialModule,
        HttpClientModule,
        StoreModule.forRoot({}),
        RouterModule.forRoot([])
      ],
      declarations: [TaskListDashletComponent, TaskDashletsContainerComponent],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskListDashletComponent);
    component = fixture.componentInstance;
    component.items = TaskItem;
    component.headerText = HeaderText;
    fixture.detectChanges();

  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should component to be initial state', () => {
    expect(fixture.debugElement.query(By.css('.title-text')).nativeElement.textContent).toBe(component.headerText);
    expect(component.items.length).toEqual(2);

    const activeTaskButton = fixture.debugElement.query(By.css('button:first-child'));
    expect(activeTaskButton).toBeNull();

    const enabledTaskButton = fixture.debugElement.query(By.css('button:last-child'));
    expect(enabledTaskButton).not.toBeNull();
  });

  it('should select task after button is clicked', () => {
    const button: DebugElement = fixture.debugElement.query(By.css('button'));
    button.triggerEventHandler('click', null);
    fixture.detectChanges();

    spyOn(component, 'selectItem');
    component.selectItem(component.items[0]);
    expect(component.selectItem).toHaveBeenCalled();
  });
});
