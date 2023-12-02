import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TaskItem} from '../../model/task.model';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {FormRelationService} from '../../services/form-relation.service';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'update-task-form',
  template: `
    <ng-container class="media1">
      <dynamic-form-container [data]="data" [task]="task" (actionEmitter)="handleAction($event)"></dynamic-form-container>
    </ng-container>
  `,
  styleUrls: ['../../container/main-workspace/main-workspace.component.scss']
})
export class UpdateTaskFormComponent implements OnInit {
  task: TaskItem = {
    taskId: null, parentTaskId: null, name: null, executionId: null, executionType: null, icon: null, instanceId: null, definitionKey: null
  };

  @Input() data;
  @Input() form;
  @Input() taskId: string;
  @Input() title: string;
  @Input() taskDefKey: string;
  @Input() formRelationDefKey: string;

  @Output() update = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<any>();

  constructor(private commonService: CommonSandboxService, private relationService: FormRelationService, public dialog: MatDialog) { }

  ngOnInit(): void {
    if (this.data != null) {
      this.task.definitionKey = this.taskDefKey;
      this.task.name = this.title;
    }
    if (null != this.formRelationDefKey) {
      this.relationService.setForm(this.form, this.formRelationDefKey).then(() => undefined);
    }
  }

  updateField(fieldId: string) {
    this.relationService.updateForm(fieldId);
  }

  handleAction(event: any): void {
    switch (event.action) {
      case 'cancel':
        this.cancel.emit(true);
        break;
      case 'continue':
        this.update.emit(true);
        break;
      default:
        break;
    }
  }
}
