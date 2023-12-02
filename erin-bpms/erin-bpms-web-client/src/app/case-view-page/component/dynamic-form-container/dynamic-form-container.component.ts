import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FieldGroups, FormsModel, TaskEntity} from '../../../models/app.model';
import {
  BIG_DECIMAL,
  CALCULATE,
  CLOSE,
  COLLATERAL_ID,
  DUPLICATED_COLLATERAL_ORDER_MESSAGE,
  FIXED_ACCEPTED_LOAN_AMOUNT,
  LOAN_CALCULATION,
  NOT_CHECK_MANDATORY_FIELDS_NAME,
  ORDER,
  TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT,
  TASK_DEF_LEASING_ORGANIZATION_REGISTRATION,
  TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT,
  TASK_DEF_SALARY_ORGANIZATION_REGISTRATION,
  VALIDATE_NEGATIVE_VALUE_MESSAGE,
  WRONG_COLLATERAL_CONNECTION_RATE_VALUE_MESSAGE,
  WRONG_COLLATERAL_ORDER_MESSAGE
} from '../../model/task-constant';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {MatDialog} from '@angular/material/dialog';
import {TaskItem} from '../../model/task.model';
import {FormRelationService} from '../../services/form-relation.service';
import {ErinTableComponent} from '../../../common/erin-table/erin-table/erin-table.component';
import {AuthenticationSandboxService} from '../../../erin-aim/authentication/authentication-sandbox.service';
import {
  CONFIRM_PER_FOR_DIRECTOR,
  CONFIRM_PERMISSION_FOR_USSD,
  CONTINUE_PER_FOR_BRANCH_SPECHIALIST
} from '../../../loan-search-page/models/process-constants.model';

/**
 * @param form - stores form fields in format of FormsModel[].
 * form is used when to execute submitting actions such as continue, calculate, save etc.
 *
 * @param fieldGroups - used to show form fields in UI. It stores form fields separately by divided sections.
 *
 * @param buttons - used to determine whether buttons are disable or not. Sets button's state.
 *
 * @param entities - is used when to submit execute submitting actions such as continue, calculate, save etc. It stores API endpoint.
 */

@Component({
  selector: 'dynamic-form-container',
  template: `
    <mat-card [ngClass]="getContainerStyle()" *ngIf="null != fieldGroups">
      <erin-title [titleName]="task.name"></erin-title>

      <ng-container *ngFor="let subForm of fieldGroups; let i = index; first as isFirst">

        <!--    ### COMMENT    separator line and erin title are different (shows separator line when subForm has no title)-->
        <hr class="separator-line" *ngIf="showSeparatorLine(subForm, isFirst, i)">
        <erin-title [titleName]="subForm.title" *ngIf="!isFirst && haveTitle(subForm)"></erin-title>
        <erin-subtitle [subtitleName]="subForm.subtitle" *ngIf="!isFirst && haveSubtitle(subForm)"></erin-subtitle>
        <!--    ### COMMENT    only shows when subForm is flexibleForm-->
        <div class="add-remove-box">
          <span class="material-icons-outlined add-btn" *ngIf="isFlexibleForm(subForm)"
                (click)="increaseForm(subForm, i)">add_box</span>
          <span class="material-icons-outlined remove-btn" *ngIf="isFlexibleForm(subForm)"
                (click)="decreaseForm(subForm)">indeterminate_check_box</span>
        </div>

        <!--    ### COMMENT Default form (Form is divided into sections horizontally)-->
        <dynamic-fields *ngIf="isDefaultForm(subForm, i)" (changeDetector)="updateField($event, subForm)"
                        (actionEmitter)="handleButtonFieldAction($event)"
                        [forms]="subForm.fields" [numberOfColumns]="subForm.column"
                        [currentTaskName]="task.name" [setBorrowerTypeCoBorrower]="borrowerTypeCoBorrower"
                        [isRoundup]="isRoundup"></dynamic-fields>

        <!--    ### COMMENT    Textarea form-->
        <dynamic-fields *ngIf="isTextAreaField(subForm)" (changeDetector)="updateField($event)"
                        [textareaForms]="subForm.fields" [numberOfColumns]="subForm.column"></dynamic-fields>

        <!--    ### COMMENT    Column-Form (Form is divided into sections vertically)-->
        <ng-container *ngIf="isVerticalForm(i)">
          <div [ngClass]="getCustomColumnStyle(fieldGroups[i].subFields.length)">
            <div *ngFor="let forms of fieldGroups[i].subFields; let t = index">
              <span *ngIf="null != fieldGroups[i].subtitles" class="column-title">{{fieldGroups[i].subtitles[t]}}</span>
              <dynamic-fields [forms]="forms" [numberOfColumns]="fieldGroups[i].subColumn[t]" [currentTaskName]="task.name"></dynamic-fields>
            </div>
          </div>
        </ng-container>

        <!--    ### COMMENT    Removable rows in form-->
        <ng-container *ngIf="isRemovableForm(subForm)" class="collateralAssets">
          <div *ngFor="let form of subForm.subFields index as i">
          <span *ngIf="subForm.subFields.length > 1 && !disableButton('createAccount', false)"
                class="material-icons-outlined remove-button"
                (click)="collateralAssetsRemove(i, subForm)">indeterminate_check_box</span>
            <dynamic-fields [forms]="form" [numberOfColumns]="subForm.column"
                            (changeDetector)="updateField($event)"></dynamic-fields>
          </div>
        </ng-container>

        <!--    ### COMMENT    Flexible form that is able to add or remove a row-->
        <ng-container *ngIf="isFlexibleForm(subForm)">
          <ng-container *ngFor="let fields of subForm.subFields; let firstRow = first">
            <hr class="separator-line" *ngIf="!firstRow && separateSubField">
            <dynamic-fields [forms]="fields" [numberOfColumns]="subForm.column" [currentTaskName]="task.name"
                            (changeDetector)="updateField($event)"></dynamic-fields>
          </ng-container>

          <!--show table between formGroups-->
        </ng-container>
        <ng-container *ngIf="showSubGroupTable(subForm)">
          <erin-table [columns]="table" [topHeader]="getTableHeader()" [data]="tableData" [hasPagination]="hasPagination"
                      [errorCheckHeaderCol]="errorCheckHeaderCol" [errorCheckColVal]="errorCheckColVal"
                      [tableHasScrollX]="tableHasScrollX" (rowDblClickListener)="doubleRowClickAction($event)"></erin-table>
        </ng-container>
      </ng-container>

      <div *ngIf="showTable()">
        <erin-table [columns]="table" [topHeader]="getTableHeader()" [data]="tableData" [highlightRow]="highlightRow"
                    [tableHasScrollX]="tableHasScrollX" [hasPagination]="hasPagination" (rowDblClickListener)="doubleRowClickAction($event)"
                    [errorCheckHeaderCol]="errorCheckHeaderCol" [errorCheckColVal]="errorCheckColVal" [setKey]="tableFilterKey"
                    (rowClickListener)="rowClickAction($event)"></erin-table>
      </div>

      <div class="workspace-actions">
        <button mat-flat-button color="primary" (click)="submit()" *ngIf="showButton('continue')"
                [disabled]="disableButton('continue', true)">
          ҮРГЭЛЖЛҮҮЛЭХ
        </button>

        <!--        <button mat-flat-button color="primary" (click)="submit('continueWithDialog')" *ngIf="showButton('continueWithDialog')"-->
        <!--                [disabled]="disableButton('continueWithDialog', true)">-->
        <!--          ҮРГЭЛЖЛҮҮЛЭХ-->
        <!--        </button>-->

        <button mat-flat-button color="primary" (click)="submit()" *ngIf="showButton('createContract')"
                [disabled]="disableButton('createContract', true)">
          ГЭРЭЭ ҮҮСГЭХ
        </button>

        <button mat-flat-button color="primary" (click)="submit('createContractWithDialog')" *ngIf="showButton('createContractWithDialog')"
                [disabled]="disableButton('continueWithDialog', true)">
          ГЭРЭЭ ҮҮСГЭХ
        </button>

        <button mat-flat-button color="primary" id="complete" *ngIf="showButton('complete')"
                [disabled]="disableButton('complete', false)" (click)="complete()">
          ДУУСГАХ
        </button>

        <button mat-flat-button color="primary" id="print" *ngIf="showButton('print')"
                (click)="print()" [hidden]="showSpecialistButton()" [disabled]="disableButton('print', false)">
          ХЭВЛЭХ
        </button>

        <button mat-flat-button color="primary" id="confirmByDirector" *ngIf="showButton('confirmByDirector')"
                (click)="confirmByDirector()" [hidden]="showSpecialistButton()" [disabled]="disableButton('confirmByDirector', false)">
          БАТЛУУЛАХ
        </button>

        <button mat-flat-button color="primary" (click)="save()" *ngIf="showButton('save')" [disabled]="disableButton('save', false)">
          ХАДГАЛАХ
        </button>

        <button mat-flat-button color="primary" (click)="save()" *ngIf="showButton('saveUssd')" [disabled]="disableButton('saveUssd', false)">
          ХАДГАЛАХ
        </button>

        <button mat-flat-button color="primary" (click)="save()" *ngIf="showButton('saveOrg')" [hidden]="showSpecialistButton()"
                [disabled]="disableButton('saveOrg', false)">
          ХАДГАЛАХ
        </button>

        <button mat-flat-button color="primary" (click)="calculate()" *ngIf="showButton('calculate')"
                [disabled]="disableButton('calculate', false)">
          ТООЦООЛОХ
        </button>

        <button mat-flat-button color="primary" id="createLoanAccount" *ngIf="showButton('createAccount')"
                (click)="createLoanAccount()" [disabled]="disableButton('createAccount', false)">
          ДАНС ҮҮСГЭХ
        </button>

        <button mat-flat-button color="primary" id="udf" *ngIf="showButton('udf')"
                (click)="openUdfDialog()" [disabled]="disableButton('udf', false)">
          UDF
        </button>

        <button mat-flat-button color="primary" id="download" *ngIf="showButton('download')" (click)="download()"
                [disabled]="disableButton('download', false)">
          ТАТАХ
        </button>

        <button mat-flat-button color="primary" *ngIf="showButton('transaction')"
                [disabled]="disableButton('transaction', false)" (click)="transaction()">
          ГҮЙЛГЭЭ ХИЙХ
        </button>

        <button mat-flat-button color="primary" id="createLoanAccount" *ngIf="showButton('cancel')"
                (click)="cancel()" [disabled]="disableButton('cancel', false)">
          БУЦАХ
        </button>

        <button mat-flat-button color="primary" id="showTemplate" *ngIf="showButton('showTemplate')"
                (click)="showTemplate()" [disabled]="disableButton('showTemplate', false)">
          ЗАГВАР ХАРАХ
        </button>

        <button mat-stroked-button color="primary" id="recoveryRights" *ngIf="showButton('recoveryRights')"
                (click)="recoveryRights()" [hidden]="showRecoveryRightsButton()" [disabled]="disableButton('recoveryRights', false)">
          ЭРХ СЭРГЭЭХ
        </button>

        <button mat-stroked-button color="primary" id="removeRights" *ngIf="showButton('removeRights')"
                (click)="removeRights()" [hidden]="showRemoveRightsButton()" [disabled]="disableButton('removeRights', false)">
          ЭРХ ЦУЦЛАХ
        </button>

        <button mat-stroked-button color="primary" id="unblock" *ngIf="showButton('unblock')"
                (click)="unblock()" [hidden]="showUnblockButton()" [disabled]="disableButton('unblock', false)">
          БЛОК ГАРГАХ
        </button>

        <button mat-stroked-button color="primary" id="forgetPassword" *ngIf="showButton('forgetPassword')"
                (click)="forgetPassword()" [hidden]="showForgetPasswordButton()" [disabled]="disableButton('forgetPassword', false)">
          НУУЦ ҮГ ИЛГЭЭХ
        </button>

        <span class="textFieldWarn" *ngIf="showTextField('warning')">
          {{this.textMessage['warning']}}
        </span>

        <button mat-stroked-button color="primary" id="clearForm" *ngIf="showButton('clearForm')"
                (click)="clearForm()" [disabled]="disableButton('clearForm', false)">
          CLEAR
        </button>

        <button mat-flat-button color="primary" id="close" *ngIf="showButton('close')"
                (click)="close()" [hidden]="showDirectorButton()" [disabled]="disableButton('close', false)">
          ХААХ
        </button>

        <button mat-flat-button color="primary" id="refuse" *ngIf="showButton('refuse')"
                (click)="refuse()" [hidden]="showDirectorButton()" [disabled]="disableButton('refuse', false)">
          ТАТГАЛЗАХ
        </button>

        <button mat-flat-button color="primary" id="confirm" *ngIf="showButton('confirm')"
                (click)="confirm()" [hidden]="showDirectorButton()" [disabled]="disableButton('confirm', false)">
          БАТЛАХ
        </button>

<!--        <button mat-flat-button color="primary" id="editForm" *ngIf="showButton('editForm')"-->
<!--                (click)="editForm()" [hidden]="showSpecialistButton()" [disabled]="disableButton('editForm', false)">-->
<!--          ЗАСАХ-->
<!--        </button>-->

        <button mat-flat-button color="primary" id="refuseUssd" *ngIf="showButton('refuseUssd')"
                (click)="cancelUSSD()" [hidden]="showUssdRefuseButton()" [disabled]="disableButton('refuseUssd', false)">
          ЦУЦЛАХ
        </button>

        <button mat-flat-button color="primary" id="confirmUssd" *ngIf="showButton('confirmUssd')"
                (click)="confirmUSSD()" [hidden]="showUssdConfirmButton()" [disabled]="disableButton('confirmUssd', false)">
          БАТЛАХ
        </button>

        <!--        <button id="saveById" mat-flat-button color="primary" *ngIf="showSaveByIdButton" [disabled]="disableState">-->
        <!--          ХАДГАЛАХ-->
        <!--        </button>-->
        <!--        <button mat-flat-button color="primary" *ngIf="showSeeContractButton" [disabled]="disableState">ГЭРЭЭ ХАРАХ-->
        <!--        </button>-->
      </div>
    </mat-card>
  `,
  styleUrls: ['./dynamic-form-container.component.scss']
})
export class DynamicFormContainerComponent implements OnInit {
  @Input() set data(form) {
    if (null != form) {
      this.setDefaultData(form);
    } else {
      this.fieldGroups = null;
    }
  }

  constructor(private commonSb: CommonSandboxService, public dialog: MatDialog, private relationService: FormRelationService,
              private auth: AuthenticationSandboxService) {
  }

  @ViewChild(ErinTableComponent) erinTableComponent: ErinTableComponent;

  @Input() form: FormsModel[];
  @Input() task: TaskItem;
  @Input() tableData;
  @Input() isValidColConnectionRate = true;
  @Input() hasInsurance;
  @Input() accountList = [];
  @Input() highlightRow;
  @Input() errorCheckHeaderCol;
  @Input() errorCheckColVal: any[];
  @Input() isRoundup = true;
  @Input() tableFilterKey;
  @Input() separateSubField = false;
  @Input() borrowerTypeCoBorrower;
  @Output() actionEmitter = new EventEmitter<any>();
  @Output() fieldActionListener = new EventEmitter<any>();
  @Output() tableLength = new EventEmitter<any>();
  fieldGroups: FieldGroups[];

  buttons: any = null;
  entities: TaskEntity;
  table: any = null;
  tableHeader: string;
  tableDoubleClick: string = null;
  tableClick: string = null;
  isFlexibleTable = false;
  hasPagination = false;
  tableHasScrollX = false;
  textMessage = false;

  ID = 'id';
  VALUE = 'value';

  userRole: string;
  userPermission;
  continuePermission;
  confirmPermission;
  ussdConfirmPermission;
  COLLATERAL_ASSET_INDEX = 5;

  private static isBlank(value: string) {
    return value === null || value === '' || value === ' ' || value === undefined;
  }

  private static isValidFieldValue(field: FormsModel): boolean {
    if (null == field.formFieldValue || null == field.formFieldValue.defaultValue) {
      return false;
    }
    if (field.type === 'string') {
      return !DynamicFormContainerComponent.isBlank(field.formFieldValue.defaultValue) && field.formFieldValue.defaultValue !== 'empty';
    }
    if (field.type === 'BigDecimal') {
      return field.formFieldValue.defaultValue >= 0;
    }
    return false;
  }

  private static matchFlexibleField(template: FormsModel, formField: FormsModel, defaultValue: boolean, i: number): boolean {
    return (template.id + i === formField.id) && (defaultValue || DynamicFormContainerComponent.isValidFieldValue(formField));
  }

  private static checkCodes() {
    return !(sessionStorage.getItem('loanProduct').toLowerCase().includes('цалингийн') || sessionStorage.getItem('loanProduct').includes('EF50') ||
      sessionStorage.getItem('loanProduct').includes('EG50'));
  }

  // Check if a row is removed or not
  private static checkFlexible(templateForm: FormsModel[], form: FormsModel[], i: number, tmpForm: FormsModel[][], defaultValue: boolean) {
    const dummyForm: FormsModel[] = [];
    const store: FormsModel[] = [];
    for (const template of templateForm) {
      for (const formField of form) {
        if (DynamicFormContainerComponent.matchFlexibleField(template, formField, defaultValue, i)) {
          dummyForm.push(formField);
          break;
        } else if (template.id + i === formField.id) {
          store.push(formField);
          break;
        }
      }
    }
    if (dummyForm.length > 0) {
      // this loop is inorder to place fields in correct order
      const finalForm: FormsModel[] = [];
      dummyForm.push.apply(dummyForm, store);
      for (const template of templateForm) {
        for (const field of dummyForm) {
          if (field.id.includes(template.id)) {
            finalForm.push(field);
            break;
          }
        }
      }
      tmpForm.push(finalForm);
    }
  }

  private static getDefaultFlexibleForm(fieldGroup: FieldGroups, index: number): FormsModel[][] {
    const form: FormsModel[] = fieldGroup.fields;
    const templateForm: FormsModel[] = fieldGroup.flexibleForm[index];
    const length = form.length / templateForm.length;
    const tmpForm: FormsModel[][] = [];
    // get default first row field data
    DynamicFormContainerComponent.checkFlexible(templateForm, form, 0, tmpForm, true);
    // get additional row field data
    for (let i = 1; i < length; i++) {
      DynamicFormContainerComponent.checkFlexible(templateForm, form, i, tmpForm, false);
    }
    return tmpForm;
  }

  ngOnInit() {
    if (null != this.table) {
      this.tableLength.emit(this.table.length);
    }
    this.userPermissionChecker();
  }

  private userPermissionChecker() {
    this.auth.getCurrentUserRole().subscribe(res => this.userRole = res);
    this.auth.getCurrentUserPermission().subscribe(res => this.userPermission = res);
    this.continuePermission = this.userPermission.find(value => value.id === CONTINUE_PER_FOR_BRANCH_SPECHIALIST);
    this.confirmPermission = this.userPermission.find(value => value.id === CONFIRM_PER_FOR_DIRECTOR);
    this.ussdConfirmPermission = this.userPermission.find(value => value.id === CONFIRM_PERMISSION_FOR_USSD);
  }

  private setDefaultFlexibleForm(): void {
    let index = 0;
    for (const fields of this.fieldGroups) {
      if (fields.type === 'flexible') {
        fields.subFields = DynamicFormContainerComponent.getDefaultFlexibleForm(fields, index);
      }
      index++;
    }
  }

  private pushFlexFormRow(index: number, form: FormsModel[][], template: FormsModel[]): void {
    const tmpForm: FormsModel[] = [];
    for (const templateField of template) {
      const tmp: FormsModel = this.commonSb.clone(templateField);
      tmp.id = tmp.id + index;
      tmpForm.push(tmp);
    }
    form.push(tmpForm);
  }

  private setDefaultData(form): void {
    if (null != form.fieldGroups) {
      this.fieldGroups = form.fieldGroups;
      this.setDefaultFlexibleForm();
    }
    if (null != form.buttons) {
      this.buttons = form.buttons;
    }
    if (null != form.entities) {
      this.entities = form.entities;
    }
    if (null != form.table) {
      if (null != form.table.tableColumns) {
        this.table = form.table.tableColumns;
      }
      if (null != form.table.tableHeader) {
        this.tableHeader = form.table.tableHeader;
      }
      if (null != form.table.isFlexibleTable) {
        this.isFlexibleTable = form.table.isFlexibleTable;
      }
      if (null != form.table.tableDoubleClick) {
        this.tableDoubleClick = form.table.tableDoubleClick;
      }
      if (null != form.table.tableClick) {
        this.tableClick = form.table.tableClick;
      }
      if (form.table.hasPagination) {
        this.hasPagination = form.table.hasPagination;
      }
      if (form.table.tableHasScrollX) {
        this.tableHasScrollX = form.table.tableHasScrollX;
      }
      if (null != form.textMessage) {
        this.textMessage = form.textMessage;
      }
    }
  }

  private getFinalForm(): FormsModel[] {
    const form: FormsModel[] = [];
    if (null != this.fieldGroups) {
      for (const group of this.fieldGroups) {
        group.fields.forEach(f => form.push(f));
      }
    }
    return form;
  }

  private validateGrantAndFixedLoanAmount(): boolean {
    // if (this.commonSb.checkFieldValue(this.form, 'hasCollateral', 'Үгүй') && DynamicFormContainerComponent.checkCodes()) {
    //   this.commonSb.showSnackBar('Сонгосон бүтээгдэхүүний хувьд барьцаагүй үргэлжлүүлэх боломжгүй, та бүтээгдэхүүнээ солиод дахин оролдоно уу!',
    //     'Хаах', 3000);
    //   return true;
    // }
    for (const field of this.form) {
      if (field.id === 'grantLoanAmountString' || field.id === 'fixedAcceptedLoanAmount') {
        const value = Number(field.formFieldValue.defaultValue);
        if (value <= 0) {
          this.commonSb.showSnackBar(field.label + ' талбар 0 байна!', 'Хаах', 3000);
          return true;
        }
      }
    }
    return false;
  }

  private checkMandatoryFields(isValidateLoanAmountFields?, actionType?): boolean {
    if (isValidateLoanAmountFields && this.task.name.includes(LOAN_CALCULATION)) {
      return this.validateGrantAndFixedLoanAmount();
    }
    const mandatoryFieldName = this.commonSb.getFieldLabel(this.getFinalForm());
    if (mandatoryFieldName) {
      if (actionType === CALCULATE && NOT_CHECK_MANDATORY_FIELDS_NAME.includes(mandatoryFieldName)) {
        return false;
      } else {
        this.commonSb.showSnackBar(mandatoryFieldName + ' талбарыг бөглөнө үү!', 'Хаах', 3000);
        return true;
      }
    }
    return false;
  }

  private checkValidation(): boolean {
    if (!this.isValidColConnectionRate) {
      this.commonSb.showSnackBar(WRONG_COLLATERAL_CONNECTION_RATE_VALUE_MESSAGE, CLOSE, null);
      return false;
    }
    const collateralList = this.getCollateralForm();
    const orderSet = new Set();
    for (const collateral of collateralList) {
      const connectionRate = collateral[ORDER];
      orderSet.add(connectionRate);
      if (connectionRate != null && this.commonSb.formatNumberService(connectionRate) === 0) {
        this.commonSb.showSnackBar(WRONG_COLLATERAL_ORDER_MESSAGE, CLOSE, null);
        return false;
      }
    }
    if (collateralList.length !== orderSet.size) {
      this.commonSb.showSnackBar(DUPLICATED_COLLATERAL_ORDER_MESSAGE, CLOSE, null);
      return false;
    }
    return true;
  }

  private changeOption(field: FormsModel, id: string, setProperty: string) {
    if (id === field.id) {
      let value;
      if (setProperty === 'value') {
        value = field.options.find(option => option.id === field.formFieldValue.defaultValue);
        if (value !== undefined) {
          value = value.value;
        }
      } else if (setProperty === 'id') {
        value = field.options.find(option => option.value === field.formFieldValue.defaultValue);
        if (value !== undefined) {
          value = value.id;
        }
      }
      if (value !== undefined) {
        field.formFieldValue.defaultValue = value;
      }
    }
  }

  private getCollateralMap() {
    if (null == this.fieldGroups[this.COLLATERAL_ASSET_INDEX]) {
      return [{}, []];
    }
    const colObj = {};
    const colIdArray = [];
    for (const collateral of this.fieldGroups[this.COLLATERAL_ASSET_INDEX].subFields) {
      let collateralId = null;
      const collateralMap = {};
      collateral.forEach(field => {
        if (field.type === BIG_DECIMAL) {
          field.formFieldValue.defaultValue = this.commonSb.formatNumberService(field.formFieldValue.defaultValue);
        }
        if (field.id === COLLATERAL_ID) {
          collateralId = field.formFieldValue.defaultValue;
        }
        collateralMap[field.id] = field.formFieldValue.defaultValue;
      });
      if (collateralId) {
        colIdArray.push(collateralId);
        colObj[collateralId] = collateralMap;
      }
    }
    return [colObj, colIdArray];
  }

  private getCollateralForm() {
    if (null == this.fieldGroups[this.COLLATERAL_ASSET_INDEX].subFields) {
      return [];
    }
    const collateralForm = [];
    for (const form of this.fieldGroups[this.COLLATERAL_ASSET_INDEX].subFields) {
      const collateralField = {};
      for (const field of form) {
        if (field.id === 'loanAmount' || field.id === 'amountOfAssessment') {
          const value = this.commonSb.formatNumberService(field.formFieldValue.defaultValue);
          collateralField[field.id] = value.toString();
        } else {
          collateralField[field.id] = field.formFieldValue.defaultValue;
        }
      }
      collateralForm.push(collateralField);
    }
    return collateralForm;
  }

  private setDefaultValue(form: FormsModel[], id: string) {
    const formField = form.find(field => field.id === id).formFieldValue;
    if (formField.defaultValue === '' || null == formField.defaultValue || formField.defaultValue === 'empty') {
      formField.defaultValue = 0;
    }
  }

  private calculateCollateralAccountAmount() {
    const overlayRef = this.commonSb.setOverlay();
    const collateralForm = this.getCollateralForm();
    const acceptedLoanAmount = this.fieldGroups[0].fields.find(field => field.id === FIXED_ACCEPTED_LOAN_AMOUNT);
    if (acceptedLoanAmount != null) {
      const value = this.commonSb.formatNumberService(acceptedLoanAmount.formFieldValue.defaultValue);
      collateralForm.forEach(collateral => collateral[acceptedLoanAmount.id] = value);
    }
    this.actionEmitter.emit({action: 'calculateCollateralAccount', data: collateralForm});
    overlayRef.dispose();
  }

  private createCollateralAccount() {
    if (!this.checkValidation()) {
      return;
    }
    const collateralMap = this.getCollateralMap();
    const submitForm = this.commonSb.clone(this.getFinalForm());
    this.setDefaultValue(submitForm, 'insuranceCUDF');
    this.actionEmitter.emit({action: 'createColAccount', data: [submitForm, collateralMap[0], this.hasInsurance, collateralMap[1]]});
  }

  private submitCollateralAccount() {
    if (!this.checkValidation()) {
      return;
    }
    const collateralMap = this.getCollateralMap();
    const submitForm = this.commonSb.getForm(this.getFinalForm());
    this.setDefaultValue(submitForm, 'insuranceCUDF');
    this.actionEmitter.emit({action: 'continueAccount', data: [submitForm, collateralMap[0], this.hasInsurance, collateralMap[1]]});
  }

  private setSubmitFlexibleForm(flexibleForm: FieldGroups, i) {
    for (const subForm of flexibleForm.subFields) {
      for (const field of subForm) {
        const formField = this.form.find(f => f.id === field.id);
        if (null != formField) {
          formField.formFieldValue.defaultValue = field.formFieldValue.defaultValue;
        }
      }
    }

    for (let index = flexibleForm.subFields.length; index < flexibleForm.max; index++) {
      for (const flexField of flexibleForm.flexibleForm[i]) {
        const formField = this.form.find(f => f.id === flexField.id + index);
        if (null != formField) {
          if (formField.type === 'string') {
            formField.formFieldValue.defaultValue = '';
          } else if (formField.type === 'BigDecimal') {
            formField.formFieldValue.defaultValue = -1;
          }
          formField.options = [];
        }
      }
    }

  }

  private setSubmitField() {
    let index = 0;
    for (const group of this.fieldGroups) {
      if (group.type === 'flexible') {
        this.setSubmitFlexibleForm(group, index);
      }
      index++;
    }
  }

  private changeLoanAccount(subForm: FormsModel[]) {
    if (null != subForm) {
      const loanRate = this.commonSb.getFormValue(subForm, 'yearlyInterestRateString');
      let increasedRate: any;
      if (loanRate != null) {
        increasedRate = this.commonSb.findMultiplication(Number(loanRate), 0.2).toFixed(2);
      }
      const startDate = this.commonSb.getFormValue(subForm, 'firstPaymentDate');
      let startDay;
      if (startDate != null && typeof startDate !== 'number') {
        startDay = new Date(startDate).getDate();
      } else if (startDate != null && typeof startDate === 'number') {
        startDay = new Date(startDate * 1000).getDate();
      }
      const accountNumber = this.commonSb.getFormValue(subForm, 'currentAccountNumber');
      let branchId;
      const accountForPayment = this.accountList.find(account => account.accountId === accountNumber);
      if (null != accountForPayment) {
        branchId = accountForPayment.branchId;
      }
      const newMap = {depositInterestRateString: String(increasedRate) + '%', dayOfPayment: String(startDay), accountBranchNumber: String(branchId)};
      this.commonSb.setFieldDefaultValue(subForm, newMap);
    }
  }


  private disableContinue() {
    if (this.task.name != null && this.task.name.includes(LOAN_CALCULATION) && null != this.form) {
      const confirmLoanAmount = this.form.find(field => field.id === 'confirmLoanAmount');
      return confirmLoanAmount ? confirmLoanAmount.formFieldValue.defaultValue !== null : false;
    }
    return false;
  }


  private setAccountCreationFormWithNoInsurance() {
    if (!this.hasInsurance) {
      const insuranceFields = this.fieldGroups[2].fields;
      for (const field of this.form) {
        const id = field.id;
        if (!!insuranceFields.find(f => f.id === id)) {
          field.validations = [];
          field.required = false;
        }
      }
    }
  }

  showSeparatorLine(fieldGroup: FieldGroups, isFirst: boolean, index: number): boolean {
    if ((fieldGroup.fields == null || fieldGroup.fields.length === 0) && (fieldGroup.subFields == null || fieldGroup.subFields.length === 0)) {
      return false;
    }
    if ((this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
        || this.task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT)
      && index === 2) {
      return this.hasInsurance;
    }
    if (!this.haveTitle(fieldGroup) && !this.haveSubtitle(fieldGroup)) {
      return !isFirst && fieldGroup.noSeparator == null;
    }
    return false;
  }

  isDefaultForm(fieldGroup: FieldGroups, index: number): boolean {
    if ((this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
      || this.task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT) && index === 2) {
      return this.hasInsurance;
    }
    return null != fieldGroup && null != fieldGroup.type && fieldGroup.type === 'default';
  }

  isTextAreaField(fieldGroup: FieldGroups): boolean {
    return null != fieldGroup && null != fieldGroup.type && fieldGroup.type === 'textarea';
  }

  isVerticalForm(index: number): boolean {
    return null != this.fieldGroups[index] && null != this.fieldGroups[index].type && this.fieldGroups[index].type === 'vertical';
  }

  isRemovableForm(fieldGroup: FieldGroups): boolean {
    return null != fieldGroup && null != fieldGroup.type && fieldGroup.type === 'removable';
  }

  isFlexibleForm(fieldGroup: FieldGroups): boolean {
    return null != fieldGroup && null != fieldGroup.type && fieldGroup.type === 'flexible';
  }

  haveTitle(fieldGroup: FieldGroups): boolean {
    return null != fieldGroup.title;
  }

  haveSubtitle(fieldGroup: FieldGroups): boolean {
    return null != fieldGroup.subtitle;
  }

  getCustomColumnStyle(numberOfColumns): string {
    switch (numberOfColumns) {
      case 2:
        return 'two-column';
      case 3:
        return 'three-column';
      default:
        return null;
    }
  }

  increaseForm(form: FieldGroups, i): void {
    if (form.subFields.length < form.max) {
      this.pushFlexFormRow(form.subFields.length, form.subFields, form.flexibleForm[i]);
    }
  }

  decreaseForm(form: FieldGroups): void {
    if (form.subFields.length > form.min) {
      form.subFields.pop();
      return;
    }
    if (form.subFields.length > 1) {
      form.subFields.pop();
    }
    if (form.fields.length > form.column) {
      for (let t = 0; t < form.column; t++) {
        form.fields.pop();
      }
    }
  }

  showTable() {
    return null != this.table && this.isFlexibleTable === false;
  }

  showSubGroupTable(fieldGroup: FieldGroups) {
    return null != fieldGroup && null != fieldGroup.type && fieldGroup.type === 'table' && this.table != null && this.isFlexibleTable;
  }

  getTableHeader() {
    return null != this.tableHeader ? this.tableHeader : '';
  }

  showButton(buttonId: string): boolean {
    if (null == this.buttons) {
      return false;
    }
    return this.buttons[buttonId] != null;
  }

  showTextField(warning: string): boolean {
    if (null == this.textMessage) {
      return false;
    }
    return this.textMessage[warning] != null;
  }

  disableButton(buttonId: string, checkContinueButton: boolean): boolean {
    if (null == this.buttons) {
      return false;
    }
    if (checkContinueButton) {
      if (this.disableContinue()) {
        this.buttons[buttonId] = false;
      }
    }
    return this.buttons[buttonId];
  }

  showDirectorButton(): boolean {
    return this.confirmPermission == null;
  }

  showUssdConfirmButton(): boolean {
    return this.ussdConfirmPermission == null;
  }

  showUssdRefuseButton(): boolean {
    return this.ussdConfirmPermission == null;
  }

  showSpecialistButton(): boolean {
    return this.continuePermission == null;
  }

  showUnblockButton(): boolean {
    return this.continuePermission != null;
  }

  showRemoveRightsButton(): boolean {
    return this.continuePermission != null;
  }

  showRecoveryRightsButton(): boolean {
    return this.continuePermission != null;
  }

  showUssdSaveButton(): boolean {
    return this.ussdConfirmPermission != null;
  }

  showForgetPasswordButton(): boolean {
    return this.continuePermission != null;
  }

  collateralAssetsRemove(index, subform) {
    this.actionEmitter.emit({action: 'removeField', data: {index, subform}, showDialog: true});
  }

  updateField(fieldId: string, subform?: FieldGroups) {
    this.fieldActionListener.emit(fieldId);
    this.changeOrgRegistrationFields(fieldId);
    if (null != subform) {
      this.changeLoanAccount(subform.fields);
    }
    if (fieldId === 'loanAmount') {
      this.calculateLinkedAccountCollaterals();
    }
  }

  calculateLinkedAccountCollaterals() {
    if (this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
      || this.task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT) {
      this.setAccountCreationFormWithNoInsurance();
      this.calculateCollateralAccountAmount();
      return;
    }
  }

  openUdfDialog() {
    this.actionEmitter.emit({action: 'udf', data: true});
  }

  cancel() {
    this.actionEmitter.emit({action: 'cancel', data: true});
  }

  transaction() {
    this.actionEmitter.emit({action: 'transaction', data: true});
  }

  complete() {
    this.actionEmitter.emit({action: 'complete', data: true});
  }

  createLoanAccount(): void {
    if (this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
      || this.task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT) {
      this.setAccountCreationFormWithNoInsurance();
    }
    if (this.checkMandatoryFields() || this.commonSb.validateFieldValueLessThanZero(this.commonSb.getFormValue
    (this.getFinalForm(), 'insuranceCUDF'), VALIDATE_NEGATIVE_VALUE_MESSAGE)) {
      return;
    }
    if (this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
      || this.task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT) {
      this.createCollateralAccount();
      return;
    }
    this.actionEmitter.emit({action: 'createAccount', data: true});
  }

  download(): void {
    if (this.checkMandatoryFields()) {
      return;
    }
    this.actionEmitter.emit({action: 'download', data: true});
  }

  showTemplate(): void {
    this.actionEmitter.emit({action: 'showTemplate', data: true});
  }

  print(): void {
    this.actionEmitter.emit({action: 'print', data: true});
  }

  forgetPassword(): void {
    this.actionEmitter.emit({action: 'forgetPassword', data: true});
  }

  clearForm(): void {
    this.actionEmitter.emit({action: 'clearForm', data: true});
  }

  confirm(): void {
    this.actionEmitter.emit({action: 'confirm', data: true});
  }


  // editForm(): void {
  //   this.actionEmitter.emit({action: 'editForm', data: true});
  // }

  refuse(): void {
    this.actionEmitter.emit({action: 'refuse', data: true});
  }

  close(): void {
    this.actionEmitter.emit({action: 'close', data: true});
  }

  confirmByDirector(): void {
    this.actionEmitter.emit({action: 'confirmByDirector', data: true});
  }

  unblock(): void {
    this.actionEmitter.emit({action: 'unblock', data: true});
  }

  removeRights(): void {
    this.actionEmitter.emit({action: 'removeRights', data: true});
  }

  recoveryRights(): void {
    this.actionEmitter.emit({action: 'recoveryRights', data: true});
  }

  calculate(): void {
    if (this.checkMandatoryFields(false, CALCULATE)) {
      return;
    }
    this.setSubmitField();
    this.actionEmitter.emit({action: 'calculate', data: true});
  }

  save(): void {
    if (this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
      || this.task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT) {
      this.setAccountCreationFormWithNoInsurance();
      const collateralMap = this.getCollateralMap();
      const submitForm = this.commonSb.clone(this.getFinalForm());
      this.actionEmitter.emit({action: 'collateralAccountSave', data: [submitForm, JSON.stringify(collateralMap[0]), collateralMap[1]]});
      return;
    }
    if (this.task.definitionKey === TASK_DEF_SALARY_ORGANIZATION_REGISTRATION || this.task.definitionKey === TASK_DEF_LEASING_ORGANIZATION_REGISTRATION) {
      if (this.checkMandatoryFields()) {
        return;
      }
    }
    this.setSubmitField();
    this.actionEmitter.emit({action: 'save', data: true});
  }

  submit(buttonId?: string): void {
    if (this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
      || this.task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT) {
      this.setAccountCreationFormWithNoInsurance();
    }
    if (this.checkMandatoryFields(true)) {
      return;
    }
    if (this.task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
      || this.task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT) {
      this.submitCollateralAccount();
      return;
    }
    this.setSubmitField();
    this.actionEmitter.emit({action: buttonId ? buttonId : 'continue', data: true});
  }

  switchFieldIdAndValue(form, id: string, setProperty: string) {
    for (const subForm of form) {
      if (subForm.length !== undefined) {
        for (const field of subForm) {
          this.changeOption(field, id, setProperty);
        }
      } else {
        this.changeOption(subForm, id, setProperty);
      }
    }
  }

  handleButtonFieldAction(value: any) {
    if (value instanceof Object && value.hasOwnProperty('action') && value.hasOwnProperty('data')) {
      this.actionEmitter.emit({action: value.action, data: value.data});
      return;
    }
    this.actionEmitter.emit({action: value, data: {}});
  }

  rowClickAction(row) {
    this.actionEmitter.emit({action: this.tableClick, data: {row}});
  }

  doubleRowClickAction(row) {
    this.actionEmitter.emit({action: this.tableDoubleClick, data: {row}});
  }

  getContainerStyle(): string {
    if (this.table != null) {
      return this.table.length > 8 ? 'full-card-container' : 'card-container';
    }
    return 'card-container';
  }

  filterTable(key) {
    this.erinTableComponent.filter(key);
  }

  filterPredicate(property: string) {
    this.erinTableComponent.filterPredicate(property);
  }

  changeOrgRegistrationFields(fieldId: string) {
    const feeTypeValue = this.commonSb.getFormValue(this.form, 'feeType');
    if (feeTypeValue !== 'Тусгай') {
      this.relationService.updateForm(fieldId);
    }
    if (fieldId === 'feeSalaryDays') {
      this.relationService.updateForm(fieldId);
    }
    if (fieldId === 'feeType' && feeTypeValue === 'Тусгай') {
      this.commonSb.removeFieldsValidation(this.form, ['feeAmountPercent'])
    }
  }
  confirmUSSD(): void {
    this.actionEmitter.emit({action: 'confirmUSSD', data: true});
  }
  cancelUSSD(): void {
    this.actionEmitter.emit({action: 'cancelUSSD', data: true});
  }
}
