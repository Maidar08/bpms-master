import {Component, EventEmitter, Input, OnDestroy, Output} from '@angular/core';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '../../../common/confirm-dialog/confirm-dialog.component';
import {Overlay} from '@angular/cdk/overlay';
import {ActivatedRoute, Router} from '@angular/router';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {TaskItem} from '../../model/task.model';
import {
  ACTIVE_TASK_DIALOG_TEXT,
  CREATE_COLLATERAL_CONFIRM_DIALOG_TEXT,
  CREATE_COLLATERAL_TASK_NAME,
  ENABLED_TASK_DIALOG_TEXT,
  TASK_NAME_REGEX,
} from '../../model/task-constant';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {Subscription} from 'rxjs';
import {TreeNode} from '../../../common/model/erin-model';

@Component({
  selector: 'task-dashlets-container',
  template: `
    <div class="idvehteiTaskPaddingForTablet"></div>
    <task-list-dashlet *ngIf="activeTaskItems.length > 0" [headerText]="'Идэвхтэй таск'" [items]="activeTaskItems" [isReadOnly]="isReadOnly"
                       (clickedItem)="showSelectedTask($event)"></task-list-dashlet>
    <task-list-dashlet *ngIf="!filterTask && enabledTaskItems.length > 0"
                       [headerText]="'Боломжит таск'" [items]="enabledTaskItems" [isReadOnly]="isReadOnly"
                       (clickedItem)="manualActivateTask($event)"></task-list-dashlet>
    <grouped-task-list-dashlet *ngIf="filterTask && enabledTaskNode.length > 0"
                               [menuNode]="enabledTaskNode" [headerText]="'Боломжит таск'"
                               (clickedItem)="manualActivateSubTask($event)"></grouped-task-list-dashlet>
    <task-list-dashlet [hideTasks]="activeTaskItems.length < 1 || enabledTaskItems.length < 1"
                       [fullStretch]="activeTaskItems.length < 1 && enabledTaskItems.length < 1"
                       [headerText]="'Дууссан таск'" [items]="completedTaskItems" class="completed-task"
                       (clickedCompletedItem)="showCompletedTask($event)"
    ></task-list-dashlet>

  `,
  styleUrls: ['./task-dashlets-container.component.scss']
})
export class TaskDashletsContainerComponent implements OnDestroy {
  @Input() set instanceId(value: string) {
    {
      this.getTaskItems(value, this.processRequestId, this.productType);
    }
  }
  @Input() taskId: string;
  @Input() saveAction: boolean;
  @Input() filterTask = false;
  @Output() taskActivated = new EventEmitter<string>();
  @Output() clickOnTask = new EventEmitter<string>();
  @Output() clickOnCompletedTask = new EventEmitter<string>();
  @Output() taskFromEnable = new EventEmitter<string>();
  hideTasks = false;
  enabledTaskNode: TreeNode[] = [];
  enabledTaskItems: TaskItem[] = [];
  activeTaskItems: TaskItem[] = [];
  completedTaskItems: TaskItem[] = [];
  selectLastTask = undefined;
  hideActive = false;
  hideEnable = false;
  errorMessage: string;
  dialogData = {taskName: '', confirmButton: 'Тийм', closeButton: 'Үгүй', message: '', executionType: ''};
  isReadOnly = false;
  requestType: string;
  subscription = new Subscription();
  processRequestId: string;
  productType: string;

  constructor(private sb: CaseViewSandboxService, public dialog: MatDialog, private overlay: Overlay, private router: Router,
              private common: CommonSandboxService, private route: ActivatedRoute) {
    this.route.paramMap.subscribe(params => {
      this.isReadOnly = params.get('isReadOnlyReq') === 'true';
    });
    const currentNavigationExtras = this.router.getCurrentNavigation().extras.state;
    if (null != currentNavigationExtras  &&  null != currentNavigationExtras.data) {
      this.requestType = this.router.getCurrentNavigation().extras.state.requestType;
      this.processRequestId = this.router.getCurrentNavigation().extras.state.processRequestId;
      this.productType = this.router.getCurrentNavigation().extras.state.productCode;
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getTaskItems(value: string, processRequestId?: string, productType?: string) {
    const overlayRef = this.common.setOverlay();
    if (this.filterTask) {
      this.getSubModuledEnabledTask(value, overlayRef);
    } else {
      this.getEnableTask(value, overlayRef);
    }

    this.sb.getActiveTasks(value, this.requestType, processRequestId, productType).subscribe(res => {
      overlayRef.dispose();
      this.activeTaskItems = res;
      this.hideActive = res.length === 0;
      if (this.selectLastTask) {
        this.activeTaskItems.forEach(task => {
          const activeTaskName = task.name.replace(TASK_NAME_REGEX, '');
          const lastSelectedTaskName = this.selectLastTask.replace(TASK_NAME_REGEX, '');
          if (activeTaskName === lastSelectedTaskName) {
            this.taskFromEnable.emit(task.taskId);
          }
        });
        this.selectLastTask = undefined;
      }
    }, () => {
      overlayRef.dispose();
    });
    this.sb.getTask(value, 'completed', this.requestType).subscribe(res => {
      overlayRef.dispose();
      this.completedTaskItems = res;
      this.hideTasks = (this.hideEnable || this.hideActive);
    }, () => {
      overlayRef.dispose();
    });
  }

  manualActivateSubTask(node: TreeNode) {
    const task: TaskItem = {taskId: null, name: node.label, parentTaskId: null,
      definitionKey: null, executionId: node.processId, executionType: null, icon: null, instanceId: this.instanceId};
    this.manualActivateTask(task);
  }

  manualActivateTask(task: TaskItem) {
    const config = new MatDialogConfig();
    this.dialogData.taskName = task.name;
    this.dialogData.executionType = task.executionType;
    this.dialogData.message = task.name.includes(CREATE_COLLATERAL_TASK_NAME) ? CREATE_COLLATERAL_CONFIRM_DIALOG_TEXT : ENABLED_TASK_DIALOG_TEXT;
    if (task.name.includes(CREATE_COLLATERAL_TASK_NAME) || task.name === 'Зээлийн эргэн төлөлтийн хуваарь (өмнөх)') {
      this.dialogData.taskName = '';
    }
    if (task.name === 'Зээлийн эргэн төлөлтийн хуваарь (өмнөх)') {
      this.dialogData.message = 'Зээлийн эргэн төлөлтийн хуваарь татах үйлдлийг эхлүүлэх үү?';
    }
    config.data = this.dialogData;
    const dialogRef = this.dialog.open(ConfirmDialogComponent, config);
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        const overlayRef = this.common.setOverlay();
        this.sb.manualActivateTask(task.executionId).subscribe(() => {
          this.selectLastTask = task.name;
          this.taskActivated.emit(task.name);
          overlayRef.dispose();
        }, error => {
          overlayRef.dispose();
          this.errorMessage = error;
        });
      }
    });
  }

  showSelectedTask(task: TaskItem) {
    if (this.taskId !== task.taskId) {
      if (this.saveAction) {
        this.clickOnTask.emit(task.taskId);
      } else {
        const config = new MatDialogConfig();
        this.dialogData.taskName = task.name;
        this.dialogData.executionType = task.executionType;
        this.dialogData.message = ACTIVE_TASK_DIALOG_TEXT;
        config.data = this.dialogData;
        const dialogRef = this.dialog.open(ConfirmDialogComponent, config);
        dialogRef.afterClosed().subscribe(res => {
          if (res) {
            this.clickOnTask.emit(task.taskId);
          }
        });
      }
    }
  }

  showCompletedTask(task: TaskItem) {
    this.clickOnCompletedTask.emit(task.taskId);
  }

  private getEnableTask(value: string, overlayRef) {
    this.subscription.add(
      this.sb.getTask(value, 'enabled', this.requestType).subscribe(res => {
        overlayRef.dispose();
        this.enabledTaskItems = res;
        this.hideEnable = res.length === 0;
      }, () => {
        overlayRef.dispose();
      })
    );
  }

  private getSubModuledEnabledTask(value: string, overlayRef) {
    this.subscription.add(
      this.sb.getSubModuledTask(value, 'enabled').subscribe(res => {
        overlayRef.dispose();
        this.enabledTaskNode = res;
        this.hideEnable = res.length === 0;
      }, () => {
        overlayRef.dispose();
      })
    );
  }
}
