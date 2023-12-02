import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {FormRelationService} from '../../services/form-relation.service';
import {
  CLOSE,
  MICRO_LOAN_CALCULATION_FIELDS,
  MORTGAGE_LOAN_CALCULATION_FIELDS,
  TASK_DEF_MICRO_LOAN_CALCULATION,
  TASK_DEF_MORTGAGE_LOAN_CALCULATION,
} from '../../model/task-constant';

@Component({
  selector: 'flexible-form',
  template: `
    <mat-card class="card-container">
      <erin-title [titleName]="currentTaskName"></erin-title>
      <ng-container *ngFor="let subForm of form; let i = index; first as isFirst">
        <hr class="separator-line" *ngIf="!isFirst && isBlank(subtitles[i])">
        <erin-title [titleName]="subtitles[i]" *ngIf="!isFirst && (!isBlank(subtitles[i]) && !isDynamicColumn(i) || checkTaskDef())"></erin-title>
        <div class="add-remove-box">
          <span class="material-icons-outlined add-btn" *ngIf="!disableState && isFlexibleForm(i)"
                (click)="increaseForm(subForm, i)">add_box</span>
          <span class="material-icons-outlined remove-btn" *ngIf="!disableState && isFlexibleForm(i)"
                (click)="decreaseForm(subForm)">indeterminate_check_box</span>
        </div>
        <dynamic-fields *ngIf="!isFlexibleForm(i) && !isDynamicColumn(i)" [forms]="subForm" [inputState]="disableState"
                        [numberOfColumns]="fieldColumnNum[i]" [loanType]="loanType" (inputCheck)="isWrongInputValue($event)"></dynamic-fields>
        <ng-container *ngIf="isFlexibleForm(i)">
          <ng-container *ngFor="let subForm2 of subForm; first as firstRow">
            <hr class="separator-line" *ngIf="!firstRow && subForm.length < 3 && isDynamicColumn(i)" >
            <dynamic-fields [forms]="subForm2" [inputState]="disableState" (changeDetector)= "updateField($event,$event, i)" [numberOfColumns]="fieldColumnNum[i]"></dynamic-fields>
          </ng-container>
        </ng-container>
        <ng-container *ngIf="isDynamicColumn(i)" class="dynamic-column-fields">
          <dynamic-columns [data]="subForm" [subtitlesInput]="this.columnSubtitles"
                           [disableState]="disableState"></dynamic-columns>
        </ng-container>
      </ng-container>

      <div class="workspace-actions">
        <button mat-flat-button color="primary" (click)="submit()" *ngIf="showContinueButton" [disabled]="disableButton()">
          ҮРГЭЛЖЛҮҮЛЭХ
        </button>
        <button mat-flat-button color="primary" *ngIf="showPrintButton" [disabled]="disableState" (click)="print()">
          ХЭВЛЭХ
        </button>
        <button mat-flat-button color="primary" (click)="save()" *ngIf="showSaveButton" [disabled]="disableState">
          ХАДГАЛАХ
        </button>
        <button id="saveById" mat-flat-button color="primary" *ngIf="showSaveByIdButton" [disabled]="disableState">
          ХАДГАЛАХ
        </button>
        <button mat-flat-button color="primary" *ngIf="showSeeContractButton" [disabled]="disableState">ГЭРЭЭ ХАРАХ
        </button>
        <button mat-flat-button color="primary" (click)="calculate()" *ngIf="showCalculateButton"
                [disabled]="disableState"> ТООЦООЛОХ
        </button>
        <button mat-flat-button color="primary" id="download" *ngIf="showDownloadButton" (click)="download()"
                [disabled]="disableButton()">
          ТАТАХ
        </button>
      </div>
    </mat-card>
  `,
  styleUrls: ['./flexible-form.component.scss']
})
export class FlexibleFormComponent implements OnInit, OnChanges {
  @Input() columnSubtitles: string[];
  @Input() currentTaskName: string;
  @Input() subtitlesInput;
  @Input() data;
  @Input() loanType;
  @Input() dynamicColumnFields;
  @Input() taskDefinitionKey;
  @Input() flexibleForms: FormsModel[][];
  @Input() dynamicColumnForms: FormsModel[][];
  @Input() flexMaxLengths: any[];
  @Input() disableState: boolean;
  @Input() showContinueButton: boolean;
  @Input() showSaveButton: boolean;
  @Input() showCalculateButton: boolean;
  @Input() showDownloadButton: boolean;
  @Input() showPrintButton: boolean;
  @Input() showSeeContractButton: boolean;
  @Input() showSaveByIdButton: boolean;
  @Input() fieldColumnNum;
  @Output() saveEmitter = new EventEmitter<any>();
  @Output() getLoanInfoEmitter = new EventEmitter<any[]>();
  @Output() submitEmitter = new EventEmitter<FormsModel[]>();
  @Output() printEmitter = new EventEmitter<FormsModel[]>();
  @Output() calculateEmitter = new EventEmitter<FormsModel[]>();
  @Output() downloadEmitter = new EventEmitter<FormsModel[]>();
  @Output() formEmitter = new EventEmitter<FormsModel[]>();
  form: any[][];
  subtitles: string[];
  isWrongInput;

  constructor(private commonService: CommonSandboxService, private relationService: FormRelationService,
              private commonSb: CommonSandboxService) {
  }

  ngOnChanges(changes?: SimpleChanges): void {
    if (null != changes.currentTaskName && null != changes.currentTaskName.previousValue) {
      this.ngOnInit();
    }
    if (changes.data != null) {
      if (!changes.data.firstChange) { this.ngOnInit(); }
    }
  }

  ngOnInit(): void {
    this.form = this.data;
    this.subtitles = this.subtitlesInput;
    if (this.form != null) { this.setForm(); }
    this.getLoanInfoEmitter.emit();
  }

  isBlank(value: string) {
    return value === null || value === '' || value === ' ' || value === undefined;
  }

  isFlexibleForm(index: number): boolean {
    return this.flexibleForms[index] != null;
  }

  isDynamicColumn(index: number): boolean {
      return this.dynamicColumnFields[index] != null;
  }

  checkTaskDef() {
    return this.taskDefinitionKey === TASK_DEF_MORTGAGE_LOAN_CALCULATION;
  }

  increaseForm(form, index: number) {
    if (form.length < this.flexMaxLengths[index]) {
      const tmp = [];
      this.pushFlexForm(index, form, tmp);
      form.push(tmp);
    }
  }

  decreaseForm(form) {
    if (form.length > 1) {
      form.pop();
    }
  }

  save() {
    const submitForm = this.getFinalForm();
    this.saveEmitter.emit(submitForm);
  }

  submit() {
    if (this.checkValidation()) {
      return;
    }
    const submitForm = this.getFinalForm();
    this.submitEmitter.emit(submitForm);
  }

  print() {
    const submitForm = this.getFinalForm();
    this.printEmitter.emit(submitForm);
  }

  calculate() {
    const submitForm = this.getFinalForm();
    this.calculateEmitter.emit(submitForm);
  }

  download() {
    const submitForm = this.getFinalForm();
    this.downloadEmitter.emit(submitForm);
  }

  checkValidation(): boolean {
    const formList = this.getFinalForm();
    for (const form of formList) {
      if (form.type === 'BigDecimal' && form.required && this.commonService.formatNumberService(form.formFieldValue.defaultValue) === 0) {
        this.commonService.showSnackBar(form.label + ' 0 байна!', CLOSE, 3000);
        return true;
      }
    }
    return false;
  }

  disableButton() {
    return this.disableState || this.isWrongInput;
  }

  setDynForm(dynamicColumnFields: FormsModel[][], submitField) {
    for (const fields of dynamicColumnFields) {
      for (const field of fields) {
        submitField.push(field);
      }
    }
  }

  private setForm(): void {
    for (let index = 0; index < this.form.length; index++) {
      if (this.flexibleForms[index] != null) {
        this.form[index] = this.setFlexibleForm(this.form[index], this.flexibleForms[index]);
      }
      if (this.dynamicColumnFields[index] != null) {
        this.form[index] = this.setDynamicColumnForm(this.form[index], index);
      }
    }
  }

  private setFlexibleForm(form: FormsModel[], template: FormsModel[]) {
    const finalForm = [];
    let tmpForm: FormsModel[] = [];
    const length = form.length / template.length;
    finalForm.push(this.findForm(form, template, 0));

    for (let index = 1; index < length; index++) {
      tmpForm = this.findForm(form, template, index);
      if (tmpForm.length != null && tmpForm.length > 0) {
        const tmpFieldValue = tmpForm[0].formFieldValue.defaultValue;
        if (!this.isBlank(tmpFieldValue) && tmpFieldValue !== 'empty') {
          finalForm.push(tmpForm);
        }
      }
    }
    return finalForm;
  }

  private setDynamicColumnForm(form: any[], index: number) {
    let form2 = [];
    form2 = this.mergeSubArrays(form, form2);
    const finalForm = [];
    for (const column of this.dynamicColumnFields[index]) {
      const tmpForm = [];
      for (const fieldId of column) {
        const field = form2.filter(f => f.id === fieldId);
        if (field != null) {
          tmpForm.push(field[0]);
        }
      }
      finalForm.push(tmpForm);
    }
    return finalForm;
  }

  private findForm(form: FormsModel[], template: FormsModel[], index: number): FormsModel[] {
    const tmpForm: FormsModel[] = [];
    let form2 = [];
    form2 = this.mergeSubArrays(form, form2);
    template.forEach(templateField => {
      const id = templateField.id;
      const tmpField = form2.find(field => field != null && field.id === id + index);
      if (!!tmpField) {
        tmpForm.push(tmpField);
      }
    });
    return tmpForm;
  }

  private pushFlexForm(index: number, form, tmpForm: any[]): void {
    this.flexibleForms[index].forEach(template => {
      tmpForm.push(
        {
          id: (template.id + form.length),
          formFieldValue: {defaultValue: ''},
          options: template.options,
          disabled: template.disabled,
          type: template.type,
          label: template.label,
          validations: template.validations,
          required: template.required
        }
      );
    });
  }

  private getFinalForm() {
    const submitForm = this.getSubmitFlexForm();
    return this.commonService.getForm(submitForm);
  }

  private getSubmitFlexForm() {
    const submitForm = this.commonService.clone(this.form);
    for (const [index, flexForm] of submitForm.entries()) {
      if (flexForm.filter(Array.isArray).length > 0) {
        for (let i = flexForm.length; i < this.flexMaxLengths[index]; i++) {
          this.increaseForm(flexForm, index);
        }
      }
    }
    return submitForm;
  }

  setFormRelation(form) {
    this.relationService.setForm(form, this.taskDefinitionKey);
  }

  updateField(fieldId: string, change, index) {
    this.setFormRelation(this.getFinalForm());
    const formMap = this.relationService.updateForm(fieldId);
    const formMapValue = [];
    formMap.forEach((value) => formMapValue.push(value));
    let subTitledForm = [];
    if (this.taskDefinitionKey === TASK_DEF_MICRO_LOAN_CALCULATION) {
      subTitledForm = this.commonSb.setForm(formMapValue, MICRO_LOAN_CALCULATION_FIELDS);
    }
    if (this.taskDefinitionKey === TASK_DEF_MORTGAGE_LOAN_CALCULATION) {
      subTitledForm = this.commonSb.setForm(formMapValue, MORTGAGE_LOAN_CALCULATION_FIELDS);
    }
    let mergedForm = [];
    mergedForm = this.mergeSubArrays(this.form[index], mergedForm);
    for (let i = 0; i < mergedForm.length; i++) {
      if (mergedForm[i].id === subTitledForm[index][i].id) {
        if (mergedForm[i].formFieldValue.defaultValue !== subTitledForm[index][i].formFieldValue.defaultValue) {
          this.form[index] = subTitledForm[index];
          this.setForm();
          break;
        }
      }
    }
  }

  mergeSubArrays(form1: any[], form2: any[]): any[] {
    for (const form1Element of form1) {
      form2 = form2.concat(form1Element);
    }
    return form2;
  }

  acceptAmountValidation(fieldId1: string, fieldId2: string, form: FormsModel[]) {
    const field1 = form.find(field => field.id === fieldId1);
    const field2 = form.find(field => field.id === fieldId2);
    if (this.commonService.formatNumberService(field2.formFieldValue.defaultValue) > this.commonService.formatNumberService(field1.formFieldValue.defaultValue)) {
      this.commonSb.showSnackBar(field2.label + ' ' + field1.label + 'нээс их байж болохгүйг анхаарна уу!', 'Хаах', 3000);
      return true;
    }
    return false;
  }

  isWrongInputValue(value: boolean): void {
    this.isWrongInput = value;
  }
}
