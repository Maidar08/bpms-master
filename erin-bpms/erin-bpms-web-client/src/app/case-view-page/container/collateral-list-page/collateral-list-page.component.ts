import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {COLLATERAL_TABLE_COLUMNS, FormsModel} from '../../../models/app.model';
import {CollateralTableComponent} from '../../component/collateral-table/collateral-table.component';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {Overlay} from '@angular/cdk/overlay';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {CollateralListModel} from '../../model/task.model';
import {COLLATERAL_FIELDS, COLLATERAL_ID, COLLATERAL_LIST_MAP_KEY, LOAN_PRODUCT} from '../../model/task-constant';
import {FormServiceSandboxService} from '../../form-service-sandbox.service';

@Component({
  selector: 'collateral-list-page',
  template: `
    <mat-card *ngIf="!showUpdateCollateral" class="container">
      <div class="head-section"><span id="title">{{taskName}}</span>
        <button mat-icon-button (click)="refreshTable()" [disabled]="isCompletedCollateral">
          <mat-icon color="primary">replay</mat-icon>
        </button>
      </div>
      <hr>
      <erin-search-field (change)="searchCollateral($event)"></erin-search-field>
      <collateral-table #collateralTable [data]="data" [columns]="columns" [completed]="isCompletedCollateral"
                        (update)="getUpdateCollateral($event)"></collateral-table>
      <div class="collateral-fields">
        <ng-container>
          <erin-number-input-field [field]="form[0]"></erin-number-input-field>
          <div class="ltv-field">
            <erin-simple-input-field [field]="form[1]"></erin-simple-input-field>
            <span *ngIf="isShowCriteria()" id="ltv">Журмын шалгуур: {{this.procedureCriteriaOfProduct * 100}}%</span>
          </div>
          <erin-number-input-field [field]="form[2]"></erin-number-input-field>
          <erin-number-input-field id="special-field" [field]="form[3]"></erin-number-input-field>
        </ng-container>
      </div>
      <div class="action-buttons">
        <button mat-flat-button color="primary" id="calculate" (click)="calculate()" [disabled]="isCompletedCollateral">ТООЦООЛОХ</button>
<!--        <button color="primary" mat-flat-button (click)="editButton()">ЗАСАХ</button>-->
        <button mat-flat-button color="primary" id="continueProcess"
                (click)="continueProcess()" [disabled]="isCompletedCollateral">ҮРГЭЛЖЛҮҮЛЭХ
        </button>
      </div>
    </mat-card>
    <update-task-form *ngIf="showUpdateCollateral" [data]="updateCollateralData"
                      [formRelationDefKey]="formRelationKeyDef" [form]="updateCollateralForm"
                      [title]="updateCollateralTitle" [taskDefKey]="updateCollateralTaskDef" [taskId]="taskId"
                      (update)="updateSelectedCollateral($event)" (cancel)="hideUpdateTask()"></update-task-form>
  `,
  styleUrls: ['./collateral-list-page.component.scss']
})
export class CollateralListPageComponent implements OnInit {
  @ViewChild('collateralTable', {static: false}) private table: CollateralTableComponent;
  @Input() instanceId;
  @Input() taskId;
  @Input() processRequestId;
  @Input() taskName;


  @Input() set setCif(cif: string) {
    this.cif = cif;
    this.ngOnInit();
  }

  @Input() loanAmount;

  @Input() set setIsCompletedCollateral(state: boolean) {
    this.isCompletedCollateral = state;
    if (!state) {
      this.ngOnInit();
    }
  }

  @Input() data: CollateralListModel[];
  @Output() collateralListEmitter = new EventEmitter<any>();
  form: FormsModel[] = COLLATERAL_FIELDS;
  cif: string;
  isCompletedCollateral = false;
  // isCalculatedCollateral = false;
  columns = COLLATERAL_TABLE_COLUMNS;
  productCode;
  procedureCriteriaOfProduct;

  /* collateral calculation field values */
  totalCollateralAmount = 0;
  LTV;
  loanApproveAmount;
  collateralMissingAmount = 0;

  /* update collateral fields */
  updateCollateralForm: FormsModel[] = [];
  updateCollateralData: any = null;
  updateCollateralTitle: string = null;
  updateCollateralTaskDef: string = null;
  updateCollateralType: string;
  formRelationKeyDef: string = null;
  showUpdateCollateral = false;
  updateCollateralId: string;
  TASK_DEF_KEY = 'user_task_create_collateral';

  constructor(private sb: CaseViewSandboxService, public overlay: Overlay,
    private commonSb: CommonSandboxService, private formSb: FormServiceSandboxService) {
  }

  ngOnInit(): void {
    this.form.forEach(field => {
      field.formFieldValue.defaultValue = 0;
    });
    if (this.cif && (this.data == null || this.data.length === 0)) {
      this.getCollateralList(this.cif);
    }
    if (this.processRequestId != null) {
      this.getLoanRequestInfo(this.processRequestId);
    }
    this.form.forEach(field => field.validations.push({name: 'readonly', configuration: null}));
  }

  getCollateralList(cifNumber: string): void {
    const overlayRef = this.commonSb.setOverlay();
    this.sb.getCollateral(cifNumber, this.instanceId).subscribe(res => {
      overlayRef.dispose();
      if (null != res) {
        this.data = res;
        this.commonSb.sortArray(this.data, COLLATERAL_ID, 'string');
        this.setSavedParameters();
      }
    }, () => {
      overlayRef.dispose();
    });
  }

  getLoanRequestInfo(requestId: string): void {
    const overlayRef = this.commonSb.setOverlay();
    this.sb.getRequestById(requestId).subscribe(res => {
      if (null != res && null != res.entity) {
        this.loanAmount = res.entity.amount;
        if (null == res.entity.loanProduct) {
          this.getParametersByName(this.instanceId, LOAN_PRODUCT);
        } else {
          this.productCode = res.entity.loanProduct;
        }
        this.getLoanProductById(this.productCode);
        overlayRef.dispose();
      }
    }, () => {
      overlayRef.dispose();
    });
  }

  private getParametersByName(instanceId: string, parameterName: string) {
    this.sb.getParametersByName(instanceId, parameterName).subscribe(response => {
      this.productCode = response.processParameters.FORM.loanProduct;
    });
  }

  private getLoanProductById(productCode: string): void {
    this.sb.getLoanProductById(productCode).subscribe(res => {
      if (null != res && null != res.loanToValueRatio) {
        this.procedureCriteriaOfProduct = res.loanToValueRatio;
      }
    });
  }

  continueProcess() {
    const collateralList = this.saveCheckedCollaterals();
    const collateralCalculationForm = this.saveCalculationForm();
    const submitForm = [...this.form];
    submitForm.push({
      id: COLLATERAL_LIST_MAP_KEY, formFieldValue: {defaultValue: [collateralList, collateralCalculationForm], valueInfo: null},
      options: [], validations: [], disabled: false, required: false, type: 'string', label: null
    });
    this.collateralListEmitter.emit([this.data, submitForm]);
  }

  private saveCheckedCollaterals() {
    const collateralList = {};
    for (const collateral of this.data) {
      collateralList[collateral.collateralId] = {checked: !!collateral.checked};
    }
    return collateralList;
  }

  private saveCalculationForm() {
    const collateralCalculationParameters = {};
    for (const field of this.form) {
      collateralCalculationParameters[field.id] = field.formFieldValue.defaultValue;
    }
    return collateralCalculationParameters;
  }

  private updateSelectedRows(): void {
    this.totalCollateralAmount = 0;
  }

  calculate(): void {
    // this.isCalculatedCollateral = true;
    this.updateSelectedRows();
    this.data.forEach(row => {
      if (row.checked) {
        this.totalCollateralAmount = this.totalCollateralAmount + row.availableAmount;
      }
    });
    this.loanApproveAmount = this.findMultiplication(this.totalCollateralAmount, this.procedureCriteriaOfProduct);
    this.LTV = this.totalCollateralAmount === 0 ? (0 + '%') : this.findMultiplication(this.loanAmount / this.totalCollateralAmount, 100) + '%';
    const loanCollateralRatio = this.findMultiplication(this.procedureCriteriaOfProduct, 100);
    this.collateralMissingAmount = this.commonSb.formatNumberService(this.LTV) >
    loanCollateralRatio ? this.loanApproveAmount - this.totalCollateralAmount : 0;
    this.setFormValues();
  }

  // editButton(): void {
  //   this.isCalculatedCollateral = false;
  // }
  private setFormValues(): void {
    this.form[0].formFieldValue.defaultValue = this.totalCollateralAmount;
    this.form[1].formFieldValue.defaultValue = this.LTV;
    this.form[2].formFieldValue.defaultValue = this.loanApproveAmount;
    this.form[3].formFieldValue.defaultValue = this.collateralMissingAmount;
  }

  searchCollateral(key: string): void {
    this.table.filter(key);
  }

  refreshTable(): void {
    this.refreshCollateralList(this.cif);
  }

  refreshCollateralList(cifNumber: string): void {
    const overlayRef = this.commonSb.setOverlay();
    this.sb.refreshCollateral(cifNumber, this.instanceId).subscribe(res => {
      overlayRef.dispose();
      if (null != res) {
        this.data = res;
        this.commonSb.sortArray(this.data, COLLATERAL_ID, 'string');
        this.setSavedParameters();
      }
    }, () => {
      overlayRef.dispose();
    });
  }


  private setSavedParameters(): void {
    this.sb.getParametersByName(this.instanceId, COLLATERAL_LIST_MAP_KEY).subscribe(res => {
      if (res != null && res.processParameters.COLLATERAL_LIST != null && res.processParameters.COLLATERAL_LIST.collateralList != null) {
        const collateralParameters = JSON.parse(res.processParameters.COLLATERAL_LIST.collateralList);
        const collateralList = collateralParameters[0];
        const collateralCalculationForm = collateralParameters[1];
        for (const id in collateralList) {
          if (collateralList.hasOwnProperty(id)) {
            const collateral = this.data.find(col => col.collateralId === id);
            if (collateral != null) {
              collateral.checked = collateralList[id].checked;
            }
          }
        }
        if (collateralCalculationForm != null) {
          for (const field of Object.entries(collateralCalculationForm)) {
            if (collateralCalculationForm.hasOwnProperty(field[0])) {
              let collateralForm;
              collateralForm = this.form.find(form => form.id === field[0]);
              if (collateralForm != null && (this.instanceId === res.processInstanceId)) {
                collateralForm.formFieldValue.defaultValue = field[1];
              }
            }
          }
        }
      }
    });
  }

  isShowCriteria(): boolean {
    if (this.LTV != null) {
      const ltv = this.commonSb.formatNumberService(this.LTV);
      return ltv > this.findMultiplication(this.procedureCriteriaOfProduct, 100);
    }
  }

  findMultiplication(input1: number, input2: number) {
    return Number(input1 * input2).toFixed();
  }

  getUpdateCollateral(collateral: any): void {
    const overlayRef = this.commonSb.setOverlay();
    const collateralId = collateral.collateralId;
    this.updateCollateralType = collateral.collateralType;
    if (collateralId) {
      this.updateCollateralForm = [];
      this.sb.getUpdateCollateralForm(collateralId, this.updateCollateralType).subscribe(collateralForm => {
        if (null != collateralForm) {
          this.setUpdateCollateralForm(collateralForm);
          switch (this.updateCollateralType) {
            case 'M':
              this.formRelationKeyDef = 'user_task_new_core_create_machinery_collateral';
              break;
            case 'I':
              this.formRelationKeyDef = 'user_task_new_core_create_immovable_collateral';
              break;
            case 'V':
              this.formRelationKeyDef = 'user_task_new_core_create_vehicle_collateral';
              break;
            default :
              break;
            case 'O':
              this.formRelationKeyDef = 'user_task_new_core_create_other_collateral';
          }
          overlayRef.dispose();
        }
      }, () => {
        overlayRef.dispose();
      });
      this.updateCollateralId = collateralId;
    }
  }

  hideUpdateTask() {
    this.showUpdateCollateral = false;
  }

  updateSelectedCollateral(response): void {
    const overlayRef = this.commonSb.setOverlay();
    if (this.checkFieldValidation()) {
      return;
    }
    this.sb.updateCollateral(this.instanceId, this.updateCollateralId, this.updateCollateralType, this.cif, this.updateCollateralForm)
      .subscribe(collateralId => {
        this.commonSb.showSnackBar(collateralId + ' дугаартай барьцааг амжилттай засварлалаа.', null, 3000);
        this.hideUpdateTask();
        this.refreshTable();
        overlayRef.dispose();
      }, () => overlayRef.dispose());
  }

  private async setUpdateCollateralForm(collateralForm) {
    const task = {taskId: null, definitionKey: collateralForm.taskId};
    this.updateCollateralData = await this.formSb.getForm(task, collateralForm.taskFormFields);
    this.updateCollateralForm = collateralForm.taskFormFields;
    this.updateCollateralTitle = collateralForm.formId;
    this.updateCollateralTaskDef = collateralForm.taskId;
    this.showUpdateCollateral = true;
  }

  checkFieldValidation(): boolean {
    if (this.updateCollateralForm != null) {
      const mandatoryFieldName = this.commonSb.getFieldLabel(this.updateCollateralForm);
      if (mandatoryFieldName) {
        this.commonSb.showSnackBar(mandatoryFieldName + ' талбарыг бөглөнө үү!', 'Хаах', 3000);
        return true;
      }
      return false;
    }
  }
}
