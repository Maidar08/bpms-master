import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {TaskDashletsContainerComponent} from '../../../case-view-page/container/task-dashlets-container/task-dashlets-container.component';
import {BranchBankingWorkspaceComponent} from '../branch-banking-workspace/branch-banking-workspace.component';
import {MatSidenav} from '@angular/material/sidenav';
import {CaseViewSandboxService} from '../../../case-view-page/case-view-sandbox.service';

@Component({
  selector: 'branch-banking-case-view',
  template: `
    <mat-sidenav-container class="sidenav-container" autosize>
      <mat-sidenav-content class="content">
        <div [ngClass]="setContainerStyle()">
          <div class="left">
            <branch-banking-workspace [instanceId]="instanceId" (saveActivated)="refreshContinueTask($event)"
                                      (expandWorkspace)="expandWorkspace($event)" #bankingWorkspace></branch-banking-workspace>
          </div>
          <div class="right">
            <task-dashlets-container [filterTask]="true" [instanceId]="instanceId" [saveAction]="saved"
                                     (taskActivated)="refreshSections()" (taskFromEnable)="getTaskFromEnable($event)"
                                     (clickOnTask)="updateMainWorkspace($event)"
                                     #taskDashlets></task-dashlets-container>
          </div>
        </div>
      </mat-sidenav-content>

      <!--This section only show when tablet portrait mode - smaller screen-->
      <mat-sidenav #taskDashletDrawer fixedInViewport [opened]="isTabletSidenavOpen" position='end'>
        <mat-sidenav-content class="tablet_right_menu">
          <task-dashlets-container [filterTask]="true" [instanceId]="instanceId" [saveAction]="saved"
                                   (taskActivated)="refreshSections()" (taskFromEnable)="getTaskFromEnable($event)"
                                   #taskDashlets></task-dashlets-container>

        </mat-sidenav-content>
      </mat-sidenav>
    </mat-sidenav-container>

    <!--This button only shows up on tablet portrait mode to handle right sidenav-->
    <button mat-icon-button class="taskDashletButton" (click)="openTaskDashlet()">
      <mat-icon>apps</mat-icon>
    </button>
  `,
  styleUrls: ['./branch-banking-case-view.component.scss']
})
export class BranchBankingCaseViewComponent {
  instanceId: string;
  currentURL: string;
  saved = false;
  isTabletSidenavOpen = false;
  isExpandWorkspace = false;

  @ViewChild('taskDashletDrawer', {static: true}) public sidenav: MatSidenav;
  @ViewChild('taskDashlets', {static: true}) taskDashlets: TaskDashletsContainerComponent;
  @ViewChild('bankingWorkspace', {static: true}) bankingWorkspace: BranchBankingWorkspaceComponent;

  constructor(private route: ActivatedRoute, private router: Router, private sb: CaseViewSandboxService) {
    this.currentURL = this.router.url;
    this.route.paramMap.subscribe(params => {
      this.instanceId = params.get('id');
    });
  }

  refreshContinueTask(refreshState) {
    if (refreshState.save) {
      if (refreshState.relatedTaskId) {
        this.sb.getActiveTasks(this.instanceId).subscribe(activeTaskList => {
          const prevTask = activeTaskList.find(task => task.definitionKey === refreshState.relatedTaskId);
          if (prevTask) {
            this.bankingWorkspace.getDynamicForms(prevTask.taskId);
          } else {
            this.bankingWorkspace.getDynamicForms();
          }
        });
      } else {
        this.bankingWorkspace.getDynamicForms();
      }
      this.taskDashlets.getTaskItems(this.instanceId);
    }
  }

  refreshSections() {
    this.taskDashlets.getTaskItems(this.instanceId);
  }

  getTaskFromEnable(taskId: string) {
    if (taskId != null) {
      this.bankingWorkspace.getDynamicForms(taskId, true);
    }
  }

  openTaskDashlet() {
    this.sidenav.toggle();
    this.isTabletSidenavOpen = true;
  }

  updateMainWorkspace(taskId: string) {
    if (this.isTabletSidenavOpen) {
      this.openTaskDashlet();
      this.isTabletSidenavOpen = false;
    }
    this.saved = false;
    this.bankingWorkspace.getDynamicForms(taskId);
    this.taskDashlets.getTaskItems(this.instanceId);
  }

  expandWorkspace(expand: boolean) {
    this.isExpandWorkspace =  expand;
  }

  setContainerStyle(): string {
    return this.isExpandWorkspace ? 'full-container' : 'container';
  }
}
