import {Component, HostListener, Input, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {TaskDashletsContainerComponent} from '../task-dashlets-container/task-dashlets-container.component';
import {FormsModel} from '../../../models/app.model';
import {CaseFolderComponent} from '../case-folder/case-folder.component';
import {MatSidenav} from '@angular/material/sidenav';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {BasicFieldViewComponent} from '../basic-field-view/basic-field-view.component';
import {MainWorkspaceNewComponent} from '../main-workspace-new/main-workspace-new.component';
import {CommonSandboxService} from "../../../common/common-sandbox.service";
import {DIGITAL_LOANS_PROCESS_TYPE_ID} from "../../model/task-constant";

interface Task {
  name: string;
  id: string;
}

@Component({
  selector: 'app-case-view',
  template: `
    <mat-sidenav-container class="sidenav-container" autosize>
      <mat-sidenav-content class="content">
        <div class="container">
          <div class="left">
            <basic-field-view #basicFields
                              *ngIf="!hasNoHeaderSection && showBasicField() "
                              (cif)="getCifNumber($event)"
                              [instanceId]="instanceId"
                              [currentURL]="currentURL"
                              (requestIdEmit)="getRequestId($event)">
            </basic-field-view>

            <!--            <main-workspace [instanceId]="instanceId" [currentURL]="currentURL" [cifNumber]="cifNumber" [requestId]="requestId" (saveActivated)="refreshContinueTask($event)"-->
            <!--                            (salaryTaskId)="checkSalaryTaskState($event)" (calculateTasks)="refreshByTaskId($event)"-->
            <!--                            (taskIdEmitter)="bindTaskId($event)" (saved)="bindSaveAction($event)"-->
            <!--                            [isContinueTask]="isSalaryTask"-->
            <!--                            #mainWorkspace [isCalculation]="isCalculation"></main-workspace>-->


            <main-workspace-new [instanceId]="instanceId" [cifNumber]="cifNumber" [requestId]="requestId"
                                (saveActivated)="refreshContinueTask($event)"
                                (salaryTaskId)="checkSalaryTaskState($event)" (calculateTasks)="refreshByTaskId($event)"
                                (taskIdEmitter)="bindTaskId($event)" (saved)="bindSaveAction($event)"
                                [isContinueTask]="isSalaryTask" [requestType]="requestType"
                                #mainWorkspace [isCalculation]="isCalculation" [isInstantLoan]="isInstantLoanTask"></main-workspace-new>


            <case-folder [instanceId]="instanceId" [currentURL]="currentURL" [stretch]="stretchCaseFolder()"
                         #requestDetail></case-folder>
          </div>
          <div class="right">
            <!--This section only show when tablet landscape mode handled by scss- large screen-->
            <task-dashlets-container [filterTask]="false"
                                     [instanceId]="instanceId" (taskActivated)="refreshSections()" #taskDashlets
                                     [saveAction]="saved" (clickOnTask)="updateMainWorkspace($event)" [taskId]="currentTaskId"
                                     (taskFromEnable)="getTaskFromEnable($event)" (clickOnCompletedTask)="showCompletedTask($event)">
            </task-dashlets-container>
          </div>
        </div>
      </mat-sidenav-content>
      <mat-sidenav #taskDashletDrawer fixedInViewport
                   [opened]="isTabletSidenavOpen"
                   position='end'>
        <!--This section only show when tablet portrait mode - smaller screen-->
        <mat-sidenav-content class="tablet_right_menu">
          <task-dashlets-container [filterTask]="false"
                                   [instanceId]="instanceId" (taskActivated)="refreshSections()" #taskDashletsSideNav
                                   [saveAction]="saved" (clickOnTask)="updateMainWorkspace($event)" [taskId]="currentTaskId"
                                   (taskFromEnable)="getTaskFromEnable($event)" (clickOnCompletedTask)="showCompletedTask($event)">
          </task-dashlets-container>
        </mat-sidenav-content>
      </mat-sidenav>
    </mat-sidenav-container>
    <!--This button only shows up on tablet portrait mode to handle right sidenav-->
    <button mat-icon-button class="taskDashletButton" (click)="openTaskDashlet()">
      <mat-icon>apps</mat-icon>
    </button>
  `,
  styleUrls: ['./case-view.component.scss']
})
export class CaseViewComponent {
  @Input() taskId: string;
  currentTaskId: string;
  instanceId: string;
  form: FormsModel[] = [];
  isSalaryTask = true;
  isCalculation = true;
  saved = false;
  previousTaskName: Task[];
  isContinueTask = false;
  isTabletSidenavOpen = false;
  screenSize;
  currentURL: string;
  cifNumber;
  requestId;
  requestType: string;
  hasNoHeaderSection: boolean;
  processType: string;
  processRequestId: string;
  isInstantLoanTask = false;
  @ViewChild('taskDashletDrawer', {static: true}) public sidenav: MatSidenav;
  // @ViewChild('mainWorkspace', {static: true}) mainWorkspace: MainWorkspaceComponent;
  @ViewChild('mainWorkspace', {static: true}) mainWorkspace: MainWorkspaceNewComponent;
  // @ViewChild('testMainWorkspace', {static: true}) testMainWorkspace: MainWorkspaceNew;
  @ViewChild('requestDetail', {static: true}) requestDetail: CaseFolderComponent;
  @ViewChild('taskDashlets', {static: true}) taskDashlets: TaskDashletsContainerComponent;
  @ViewChild('taskDashletsSideNav', {static: true}) taskDashletsSideNav: TaskDashletsContainerComponent;
  @ViewChild('basicFields', {static: false}) basicFields: BasicFieldViewComponent;

  constructor(private route: ActivatedRoute, private sb: CaseViewSandboxService, private commonSb: CommonSandboxService, private router: Router) {
    this.route.paramMap.subscribe(params => {
      this.instanceId = params.get('id');
      this.hasNoHeaderSection = params.get('noHeaderSection') === 'true';
    });
    this.screenSize = window.innerWidth;
    this.currentURL = this.router.url;

    const currentNavigationExtras = this.router.getCurrentNavigation().extras.state;
    if (null != currentNavigationExtras && null != currentNavigationExtras.data) {
      this.requestType = this.router.getCurrentNavigation().extras.state.requestType;
      this.processType = this.router.getCurrentNavigation().extras.state.productCode;
      this.processRequestId = this.router.getCurrentNavigation().extras.state.processRequestId;
      localStorage.setItem('productType', this.processType);
      localStorage.setItem('processRequestId', this.processRequestId);
    }
    if (this.processType === 'instantLoan' || this.processType === 'onlineLeasing') {
      this.isInstantLoanTask = true
    }
  }

  @HostListener('window:resize', ['$event'])

  onResize() {
    this.screenSize = window.innerWidth;
  }

  @HostListener('window:popstate', ['$event'])
  onPopState() {
    this.router.navigateByUrl('/loan-page/dashboard/my-loan-request').then(() => {
      sessionStorage.clear();
    });
  }

  ngOnInit() {
    this.processType = localStorage.getItem('productType');
    this.processRequestId = localStorage.getItem('processRequestId')
    if (this.processType != null && (this.processType === 'instantLoan' || this.processType === 'onlineLeasing')) {
      this.updateMainWorkspace(this.taskId)
    }
  }

  refreshSections() {
    this.taskDashlets.getTaskItems(this.instanceId, this.processRequestId, this.processType);
    this.taskDashletsSideNav.getTaskItems(this.instanceId, this.processRequestId, this.processType);
    this.requestDetail.getVariables();
  }

  refreshContinueTask(refreshState) {
    if (refreshState.save) {
      this.previousTaskName = [];
      let prevTask;
      let processType;
      processType = this.commonSb.getDefinitionKeyByProcessType(this.processType);
      this.sb.getActiveTasks(this.instanceId, this.requestType, this.processRequestId, this.processType).subscribe(activeTaskList => {
        if (DIGITAL_LOANS_PROCESS_TYPE_ID.includes(this.processType)) {
          this.mainWorkspace.getBpmnDynamicForms(this.instanceId, processType)
        } else {
          if (refreshState.relatedTaskId) {
            prevTask = activeTaskList.find(task => task.definitionKey === refreshState.relatedTaskId);
            prevTask ? this.mainWorkspace.getDynamicForms(prevTask.taskId) : this.mainWorkspace.getDynamicForms();
          } else if (prevTask) {
            this.isContinueTask = !!prevTask;
            activeTaskList.forEach(task => this.previousTaskName.push({name: task.name, id: task.taskId}));
            prevTask = this.previousTaskName.find(task => task.name === sessionStorage.getItem('taskName'));
            this.mainWorkspace.getDynamicForms(prevTask.id);
          } else {
            this.mainWorkspace.getDynamicForms();
          }
        }
        this.taskDashlets.getTaskItems(this.instanceId, this.processRequestId, this.processType);
        this.taskDashletsSideNav.getTaskItems(this.instanceId, this.processRequestId, this.processType);
        this.requestDetail.getVariables();
      });
    }
  }

  refreshByTaskId(taskId?: string) {
    const processType = this.commonSb.getDefinitionKeyByProcessType(this.processType);
    if (taskId) {
      (DIGITAL_LOANS_PROCESS_TYPE_ID.includes(processType)) ? this.mainWorkspace.getBpmnDynamicForms(this.instanceId, processType) :
        this.mainWorkspace.getDynamicForms(taskId, this.processRequestId, this.processType);
      this.taskDashlets.getTaskItems(this.instanceId, this.processRequestId, this.processType);
      this.taskDashletsSideNav.getTaskItems(this.instanceId, this.processRequestId, this.processType);
      this.requestDetail.getVariables();
    }
  }

  getTaskFromEnable(taskId: string) {
    if (this.isTabletSidenavOpen) {
      this.openTaskDashlet();
      this.isTabletSidenavOpen = false;
    }
    if (taskId != null) {
      this.mainWorkspace.getDynamicForms(taskId, this.processRequestId, this.processType);
    }
  }

  updateMainWorkspace(taskId: string) {
    if (this.isTabletSidenavOpen) {
      this.openTaskDashlet();
      this.isTabletSidenavOpen = false;
    }
    this.saved = false;
    if (DIGITAL_LOANS_PROCESS_TYPE_ID.includes(this.processType)) {
      this.mainWorkspace.getBpmnDynamicForms(this.instanceId, this.processType, this.taskId);
    } else {
      this.mainWorkspace.getDynamicForms(taskId, this.processRequestId, this.processType);
    }
    this.taskDashlets.getTaskItems(this.instanceId, this.processRequestId, this.processType);
    this.taskDashletsSideNav.getTaskItems(this.instanceId, this.processRequestId, this.processType);
  }

  showCompletedTask(taskId: string) {
    this.mainWorkspace.getCompletedTaskForm(taskId);
  }

  checkSalaryTaskState(state: boolean): void {
    this.isSalaryTask = state;
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

  bindSaveAction(saved: boolean) {
    this.saved = saved;
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

  showBasicField() {
    return !DIGITAL_LOANS_PROCESS_TYPE_ID.includes(this.processType);
  }
}
