import {Component, HostListener, Input, OnInit, ViewChild} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {TaskItem} from '../../../case-view-page/model/task.model';
import {MatSidenav} from '@angular/material/sidenav';
import {MainWorkspaceComponent} from '../../../case-view-page/container/main-workspace/main-workspace.component';
import {CaseFolderComponent} from '../../../case-view-page/container/case-folder/case-folder.component';
import {TaskDashletsContainerComponent} from '../../../case-view-page/container/task-dashlets-container/task-dashlets-container.component';
import {BasicFieldViewComponent} from '../../../case-view-page/container/basic-field-view/basic-field-view.component';
import {ActivatedRoute, Router} from '@angular/router';
import {CaseViewSandboxService} from '../../../case-view-page/case-view-sandbox.service';
import {AuthenticationSandboxService} from '../../../erin-aim/authentication/authentication-sandbox.service';
import {OrganizationRegistrationWorkspaceServices} from '../services/organization-registration-workspace-service';

@Component({
  selector: 'app-organization-registration-case-view',
  template: `
    <mat-sidenav-container class="sidenav-container" autosize>
      <mat-sidenav-content class="content">
        <div class="container">
          <div class="left">
            <main-workspace [instanceId]="instanceId" [workspaceService]="workspaceService" [requestType]="requestType"
                            [requestData]="requestData" [contextMenuAction]="contextMenuAction" [groupId]="groupId"
                            (eventEmitter)="handleEvent($event)" #mainWorkspace></main-workspace>
            <case-folder [instanceId]="instanceId" [currentURL]="currentURL" [stretch]="stretchCaseFolder()"
                         #requestDetail></case-folder>
          </div>
          <div class="right">
            <!--This section only show when tablet landscape mode handled by scss- large screen-->
            <task-dashlets-container [filterTask]="true"
                                     [instanceId]="instanceId" (taskActivated)="refreshSections()" #taskDashlets
                                     [saveAction]="saved" (clickOnTask)="updateMainWorkspace($event)" [taskId]="currentTaskId"
                                     (taskFromEnable)="getTaskFromEnable($event)">
            </task-dashlets-container>
          </div>
        </div>
      </mat-sidenav-content>
      <mat-sidenav #taskDashletDrawer fixedInViewport
                   [opened]="isTabletSidenavOpen"
                   position='end'>
        <!--This section only show when tablet portrait mode - smaller screen-->
        <mat-sidenav-content class="tablet_right_menu">
          <task-dashlets-container [filterTask]="true"
                                   [instanceId]="instanceId" (taskActivated)="refreshSections()" #taskDashletsSideNav
                                   [saveAction]="saved" (clickOnTask)="updateMainWorkspace($event)" [taskId]="currentTaskId"
                                   (taskFromEnable)="getTaskFromEnable($event)">
          </task-dashlets-container>
        </mat-sidenav-content>
      </mat-sidenav>
    </mat-sidenav-container>
    <!--This button only shows up on tablet portrait mode to handle right sidenav-->
    <button mat-icon-button class="taskDashletButton" (click)="openTaskDashlet()">
      <mat-icon>apps</mat-icon>
    </button>
  `,
  styleUrls: ['./organization-registration-case-view.component.scss']
})
export class OrganizationRegistrationCaseViewComponent implements OnInit {
  @Input() taskId: string;
  @Input() tableData;
  currentTaskId: string;
  instanceId: string;
  form: FormsModel[] = [];
  saved = false;
  previousTaskName = [];
  isContinueTask = false;
  isTabletSidenavOpen = false;
  screenSize;
  currentURL: string;
  cifNumber;
  requestId;
  groupId: string;
  requestType: string;
  requestData;
  contextMenuAction: string;
  hasNoHeaderSection: boolean;
  task: TaskItem = {
    taskId: null,
    parentTaskId: null,
    name: null,
    executionId: null,
    executionType: null,
    icon: null,
    instanceId: null,
    definitionKey: null
  };
  tableDataMap = {};
  @ViewChild('taskDashletDrawer', {static: true}) public sidenav: MatSidenav;
  @ViewChild('mainWorkspace', {static: true}) mainWorkspace: MainWorkspaceComponent;
  @ViewChild('requestDetail', {static: true}) requestDetail: CaseFolderComponent;
  @ViewChild('taskDashlets', {static: true}) taskDashlets: TaskDashletsContainerComponent;
  @ViewChild('basicFields', {static: false}) basicFields: BasicFieldViewComponent;

  constructor(private route: ActivatedRoute, private sb: CaseViewSandboxService, private router: Router,
              public workspaceService: OrganizationRegistrationWorkspaceServices, private auth: AuthenticationSandboxService) {
    this.route.paramMap.subscribe(params => {
      this.instanceId = params.get('id');
      this.hasNoHeaderSection = params.get('noHeaderSection') === 'true';
    });
    this.screenSize = window.innerWidth;
    this.currentURL = this.router.url;
    const currentNavigationExtras = this.router.getCurrentNavigation().extras.state;
    if (null != currentNavigationExtras && null != currentNavigationExtras.data) {
      this.contextMenuAction = currentNavigationExtras.actionType;
      this.requestData = currentNavigationExtras.data;
    }
  }

  @HostListener('window:resize', ['$event'])

  onResize() {
    this.screenSize = window.innerWidth;
  }

  @HostListener('window:popstate', ['$event'])
  onPopState() {
    const reqType = sessionStorage.getItem('requestType');
    if (reqType != null && reqType === 'salary-organization') {
      this.router.navigateByUrl('/loan-page/dashboard/salary-organization-request').then(() => {
        sessionStorage.clear();
      });
    } else {
      this.router.navigateByUrl('/loan-page/dashboard/leasing-organization-request').then(() => {
        sessionStorage.clear();
      });
    }
  }

  ngOnInit(): void {
    const routeData = this.route.data['value'];
    if (routeData) {
      this.requestType = routeData.requestType;
      sessionStorage.setItem('requestType', this.requestType);
    }
    this.auth.getCurrentUserGroup().subscribe(res => this.groupId = res);
  }

  private bindSaveAction(saved: boolean) {
    this.saved = saved;
  }

  refreshSections() {
    this.taskDashlets.getTaskItems(this.instanceId);
    this.requestDetail.getVariables();
    this.mainWorkspace.getDynamicForms();
  }

  refreshContinueTask(refreshState) {
    if (refreshState.save) {
      this.previousTaskName = [];
      let prevTask;
      this.sb.getActiveTasks(this.instanceId, this.requestType).subscribe(activeTaskList => {
        if (refreshState.relatedTaskId) {
          prevTask = activeTaskList.find(task => task.definitionKey === refreshState.relatedTaskId);
          if (prevTask) {
            this.mainWorkspace.getDynamicForms(prevTask.taskId);
          } else {
            this.mainWorkspace.getDynamicForms();
          }
        } else if (prevTask) {
          this.isContinueTask = !!prevTask;
          activeTaskList.forEach(task => this.previousTaskName.push({name: task.name, id: task.taskId}));
          prevTask = this.previousTaskName.find(task => task.name === sessionStorage.getItem('taskName'));
          this.mainWorkspace.getDynamicForms(prevTask.id);
        } else {
          this.mainWorkspace.getDynamicForms();
        }
        this.taskDashlets.getTaskItems(this.instanceId);
        this.requestDetail.getVariables();
      });
    }
  }

  refreshByTaskId(taskId?: string) {
    if (taskId) {
      this.mainWorkspace.getDynamicForms(taskId);
      this.taskDashlets.getTaskItems(this.instanceId);
      this.requestDetail.getVariables();
    }
  }

  getTaskFromEnable(taskId: string) {
    if (this.isTabletSidenavOpen) {
      this.openTaskDashlet();
      this.isTabletSidenavOpen = false;
    }
    if (taskId != null) {
      this.mainWorkspace.getDynamicForms(taskId);
    }
  }

  updateMainWorkspace(taskId: string) {
    if (this.isTabletSidenavOpen) {
      this.openTaskDashlet();
      this.isTabletSidenavOpen = false;
    }
    this.saved = false;
    this.mainWorkspace.getDynamicForms(taskId);
    this.taskDashlets.getTaskItems(this.instanceId);
  }

  stretchCaseFolder() {
    let stretch = false;
    if (this.mainWorkspace.hideMainWorkspace === true) {
      stretch = true;
    }
    return stretch;
  }

  bindTaskId(taskId: string) {
    this.currentTaskId = taskId;
  }

  openTaskDashlet() {
    this.sidenav.toggle();
    this.isTabletSidenavOpen = true;
  }

  getRequestId(requestId: string) {
    this.requestId = requestId;
  }

  getCifNumber(cifNumber: string) {
    this.cifNumber = cifNumber;
  }

  handleEvent(event) {
    switch (event.action) {
      case 'save' :
        this.refreshContinueTask({save: true});
        break;
      default:
        break;
    }
  }

  // getTableValue() {
  //   if (null == this.task || null == this.task.definitionKey) {
  //     return [];
  //   }
  //   return this.tableDataMap[this.task.definitionKey];
  // }
}
