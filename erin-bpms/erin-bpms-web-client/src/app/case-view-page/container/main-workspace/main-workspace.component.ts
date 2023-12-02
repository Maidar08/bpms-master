import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {SafeResourceUrl} from '@angular/platform-browser';
import {FormsModel} from '../../../models/app.model';
import {OverlayRef} from '@angular/cdk/overlay';
import {ErinCommonWorkspaceService} from '../../../common/model/erin-model';
import {FormRelationService} from '../../services/form-relation.service';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {FormServiceSandboxService} from '../../form-service-sandbox.service';
import {TaskItem, TaskModel} from '../../model/task.model';
import {DynamicFormContainerComponent} from '../../component/dynamic-form-container/dynamic-form-container.component';
import {TASK_DEF_C0_OWNER_CONTRACT, TASK_DEF_EMPLOYEE_CONSUMPTION_CONTRACT} from '../../model/task-constant';

@Component({
  selector: 'main-workspace',
  template: `
    <div class="workspace-container">

      <div class="workspace">
        <!-- Sub task form section-->
        <div class="task-form" *ngIf="isDataFull">
          <dynamic-form-container class="dynamic-form-container"
                                  [data]="fieldGroups" [task]="task" [form]="form" [tableData]="getTableValue()" [highlightRow]="highlightRow"
                                  [errorCheckHeaderCol]="errorHeaderCol" [errorCheckColVal]="errorColVal" [separateSubField]="true"
                                  (tableLength)="setWorkspaceStyle($event)" (actionEmitter)="handleAction($event)"
                                  (fieldActionListener)="handleFieldAction($event)"
                                  [isRoundup]="false" [tableFilterKey]="filterKey"></dynamic-form-container>
        </div>
        <!-- Empty message section-->
        <p *ngIf="!isDataFull" class="mat-title error-table-text">АЖЛЫН ТАЛБАР ХООСОН БАЙНА</p>
        <!--        <iframe [src]="iframeURL" id="iframeElement" style="display: none"></iframe>-->
      </div>
    </div>
  `,
  styleUrls: ['./main-workspace.component.scss']
})
export class MainWorkspaceComponent implements OnInit, OnChanges {

  constructor(
    private relationService: FormRelationService, private commonService: CommonSandboxService,
    private sb: CaseViewSandboxService, private formSb: FormServiceSandboxService) {
  }

  @ViewChild(DynamicFormContainerComponent) dynamicFormContainer: DynamicFormContainerComponent;

  @Input() instanceId: string;
  @Input() requestId: string;
  @Input() requestType: string;
  @Input() groupId: string;
  @Input() contextMenuAction: string;
  @Input() requestData;
  @Input() workspaceService: ErinCommonWorkspaceService;
  @Output() saveActivated = new EventEmitter<any>();
  @Output() expandWorkspace = new EventEmitter();
  @Output() eventEmitter = new EventEmitter<any>();
  hideMainWorkspace = false;
  tableDataMap = {};
  tableHeader: string;
  errorHeaderCol;
  errorColVal: any[];
  highlightRow = false;
  isDataFull = false;
  parentTaskDefKey: string;
  form: FormsModel[] = [];
  loanRepaymentInfo;
  fieldGroups;
  iframeURL: SafeResourceUrl;
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
  previousTaskDefKey: string;
  filterKey = null;


  private static setSession(taskId: string, taskName: string) {
    sessionStorage.removeItem('taskId');
    sessionStorage.removeItem('taskName');
    sessionStorage.setItem('taskId', taskId);
    sessionStorage.setItem('taskName', taskName);
  }

  ngOnChanges(changes: SimpleChanges): void {
    const idSession = sessionStorage.getItem('taskId');
    if (null != idSession) {
      sessionStorage.removeItem('taskId');
      this.getDynamicForms(idSession);
    } else {
      this.getDynamicForms();
    }
  }

  ngOnInit(): void {
    if (null != localStorage.getItem('currentTaskData') && null != JSON.parse(localStorage.getItem('currentTaskData'))) {
      const currentTaskData = JSON.parse(localStorage.getItem('currentTaskData'));
      this.form = currentTaskData.form != null ? currentTaskData.form : this.form;
      this.tableDataMap = currentTaskData.tableData != null ? currentTaskData.tableData : this.tableDataMap;
    }
    localStorage.clear();
  }

  getDynamicForms(taskId?: string) {
    this.hideCards();
    this.isDataFull = false;
    this.previousTaskDefKey = this.task.definitionKey;
    this.sb.getActiveTasks(this.instanceId, this.requestType).subscribe(taskList => {
      if (taskList.length > 0) {
        this.setTask(taskId, taskList);
        if (this.task != null) {
          taskId = this.task.taskId;
          this.parentTaskDefKey = undefined;
        }
        const overlayRef = this.commonService.setOverlay();
        this.sb.switchActiveTasks(taskId ? taskId : taskList[0].taskId, this.instanceId, this.task.definitionKey).subscribe(res => {
          this.handleResponse(res, overlayRef);
        }, error => {
          if (error) {
            this.hideMainWorkspace = true;
            this.isDataFull = false;
          }
          overlayRef.dispose();
        });
      } else {
        localStorage.setItem('currentTaskData', null);
      }
    });
  }

  getTableValue() {
    if (null == this.task || null == this.task.definitionKey) {
      return [];
    }
    return this.tableDataMap[this.task.definitionKey];
  }

  setWorkspaceStyle(length: number) {
    this.expandWorkspace.emit(length > 8);
  }

  handleAction(event: any): void {
    this.workspaceService.handleAction(event, this.form, this.tableDataMap, this.fieldGroups.buttons).then(res => {
      if (res == null) {
        return;
      }
      if (res.type === 'event') {
        this.eventEmitter.emit(res);
      }
      if (event.action === 'save' && this.task.definitionKey.includes('organization')) {
        this.eventEmitter.emit(res);
      }
      if (res.type === 'filter') {
        this.handleActionResponse(res);
      }
    });
  }

  handleFieldAction(fieldId: string) {
    if (null != this.fieldGroups.fieldAction) {
      const functionName = this.fieldGroups.fieldAction[fieldId];
      if (null != functionName) {
        this.workspaceService.handleFieldAction(functionName, this.form, this.tableDataMap, this.fieldGroups.buttons).then(res => {
          this.handleActionResponse(res);
        });
      }
    }
  }

  private handleActionResponse(res) {
    if (res == null) {
      return;
    }
    if (res.type === 'event') {
      this.eventEmitter.emit(res);
    } else if (res.type === 'filter') {
      this.filterKey = res.value;
      this.dynamicFormContainer.filterPredicate(res.property);
      this.dynamicFormContainer.filterTable(this.filterKey);
    }
  }

  private setFormRelation(form) {
    this.relationService.setForm(form, this.task.definitionKey).then(() => undefined);
  }

  private handleResponse(res: TaskModel, overlayRef: OverlayRef, isCompletedTask?: boolean) {
    this.getFormGroup(res, isCompletedTask).then(() => overlayRef.dispose());
    this.setFormRelation(res.taskFormFields);
    this.form = res.taskFormFields;
    // TODO improve this logic later
    if (this.task.definitionKey.includes('loan_contract') || this.task.definitionKey === TASK_DEF_EMPLOYEE_CONSUMPTION_CONTRACT) {
      this.form.forEach(field => {
        if (field.type === 'long') {
          field.type = 'BigDecimal';
        }
      });
    }
    this.workspaceService.setInstanceId(this.instanceId);
    this.workspaceService.setTask(this.task);
    if (res['tableData']) {
      this.tableDataMap[this.task.definitionKey] = res['tableData'].table;
    }
    MainWorkspaceComponent.setSession(res.taskId, res.formId);
    this.hideMainWorkspace = false;
  }

  private hideCards(): void {
    this.hideMainWorkspace = true;
  }

  private async getFormGroup(res: TaskModel, isCompletedTask: boolean) {
    this.fieldGroups = await this.formSb.getForm(this.task, res.taskFormFields, this.requestType);
    this.isDataFull = null != this.fieldGroups;
    if (isCompletedTask) {
      for (const buttonId in this.fieldGroups.buttons) {
        if (this.fieldGroups.buttons.hasOwnProperty(buttonId)) {
          this.fieldGroups.buttons[buttonId] = true;
        }
      }
    }
    if (this.task.definitionKey === TASK_DEF_C0_OWNER_CONTRACT) {
      const fieldValue = this.commonService.getFormValue(this.form, 'accountNumber');
      this.commonService.setFieldDefaultValue(this.form, {coOwnerLoanAttachmentNumber: fieldValue});
    }
    if ( this.task.definitionKey != null && this.task.definitionKey.includes('organization')) {
      this.workspaceService.setFormValue({actionValue: this.contextMenuAction, requestData: this.requestData}, this.form);
    }
  }

  private setTask(taskId: string, taskList: TaskItem[]): void {
    if (taskId == null && this.parentTaskDefKey != null) {
      const filteredTaskName = taskList.find(task => task.parentTaskId === this.parentTaskDefKey);
      if (filteredTaskName) {
        this.task = filteredTaskName;
      } else {
        this.task = taskList[0];
      }
    } else if (taskId == null) {
      this.task = taskList[0];
    } else {
      const filteredTask = taskList.find(task => task.taskId === taskId);
      if (filteredTask) {
        this.task = filteredTask;
      }
    }
  }
}

