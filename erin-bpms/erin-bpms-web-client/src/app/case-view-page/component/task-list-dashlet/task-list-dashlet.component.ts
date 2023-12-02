import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {Router} from '@angular/router';
import {TaskItem} from '../../model/task.model';

@Component({
  selector: 'task-list-dashlet',
  template: `
    <mat-card class="card-container"
              [ngClass]="{'fullContainer': hideTasks && headerText === 'Дууссан таск', 'card-container': !hideTasks,
              'full-stretch': fullStretch}">
      <div class="title">
        <hr class="line separator-line-style">
        <span class="title-text">{{headerText}}</span>
        <hr class="line separator-line-style">
      </div>
      <div class="list-body">
        <mat-list>
          <mat-list-item *ngFor="let item of items" (click)="onClickItem(item,$event)" [ngClass]="highlightItem(item)">
            <span class="list-text" [ngClass]="getDisableStyle()">{{item.name}}</span>
            <button mat-icon-button *ngIf="item.executionType=='enabled'" (click)="selectItem(item)" [ngClass]="getDisableStyle()">
              <mat-icon class="play-arrow" [ngClass]="getDisableStyle()">play_arrow</mat-icon>
            </button>
          </mat-list-item>
        </mat-list>
      </div>
    </mat-card>
  `,
  styleUrls: ['./task-list-dashlet.component.scss']
})
export class TaskListDashletComponent implements OnChanges {
  @Input() headerText: string;
  @Input() isReadOnly: boolean;
  @Input() items: TaskItem[] = [];
  @Input() hideTasks: boolean;
  @Input() fullStretch: boolean;
  @Output() itemSelected = new EventEmitter<TaskItem>();
  @Output() clickedItem = new EventEmitter<TaskItem>();
  @Output() clickedCompletedItem = new EventEmitter<TaskItem>();
  idSession: string;

  constructor(public router: Router) {
  }

  ngOnChanges() {
    sessionStorage.removeItem('completedTaskId');
    this.idSession = sessionStorage.getItem('taskId');
    this.items.sort((first, second) => (first.name > second.name) ? 1 : -1);
  }

  selectItem(item: TaskItem) {
    this.itemSelected.emit(item);
  }

  onClickItem(item: TaskItem, event) {
    if (!this.isReadOnly) {
      if (this.headerText === 'Дууссан таск') {
        sessionStorage.removeItem('completedTaskId');
        sessionStorage.setItem('completedTaskId', item.taskId);
        this.clickedCompletedItem.emit(item);
        this.highlightItem(item);
      } else {
        this.clickedItem.emit(item);
        this.highlightItem(item);
      }
    } else {
      event.stopPropagation();
    }
  }

  highlightItem(item: TaskItem) {
    if (this.headerText !== 'Дууссан таск' && item.taskId === sessionStorage.getItem('taskId')) {
      if (item.executionType === 'userTask') {
        return ' active';
      }
    }
    if (this.headerText === 'Дууссан таск' && item.taskId === sessionStorage.getItem('completedTaskId')) {
      return ' active';
    }
  }

  getDisableStyle() {
    if (this.isReadOnly) {
      return 'read-only';
    }
  }
}
