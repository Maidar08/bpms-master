import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FormsModel, SALARY_CALCULATION_COLUMNS} from '../../../models/app.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {catchError} from 'rxjs/operators';
import {throwError} from 'rxjs';
import {ComponentPortal} from '@angular/cdk/portal';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {Overlay} from '@angular/cdk/overlay';
import {SALARY_CALCULATION, SalaryCalculationModel, SaveSalaryModel} from '../../model/task.model';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {MORTGAGE_LOAN} from '../../model/case-folder-constants';
import {BANK_CONDITION_HOUSE, MONTH_NAMES, NO, SALARY_FIELDS, SALARY_RESULT_FORM, YES} from '../../model/task-constant';

const defaultDate = new Date();

@Component({
  selector: 'salary-table',
  template: `
    <div class="parent">
      <div class="title-container">
        <span class="title">Цалингийн тооцоолол</span>
      </div>
      <hr>
      <dynamic-fields [forms]="form" [inputState]="false"></dynamic-fields>
      <salary-calculation-table [data]="data" [columns]="columns" (dateFieldValidation)="dateFieldValidation($event)"
                                [disableState]="buttonState"></salary-calculation-table>
      <ng-container class="result-container">
        <div class="result">
          <div>
            <mat-card>
              <span>Дундаж цалин</span>
              <erin-number-input-field [field]="averageSalaryBeforeTax"></erin-number-input-field>
              <erin-number-input-field [field]="averageSalaryAfterTax"></erin-number-input-field>
            </mat-card>
          </div>
          <div class="result-buttons">
            <button color="primary" mat-flat-button (click)="completeTask();" [disabled]="setButtonDisable || buttonState">ҮРГЭЛЖЛҮҮЛЭХ</button>
<!--            <button color="primary" mat-flat-button (click)="save(false, true)" [disabled]="buttonState">ХАДГАЛАХ</button>-->
<!--            <button color="primary" mat-flat-button (click)="editButton()" [disabled]="setButtonDisable ||buttonState">ЗАСАХ</button>-->

            <button class="calculate-button" color="primary" mat-flat-button
                    (click)="calculateAverage()" [disabled]="setButtonDisable || buttonState">ТООЦООЛОХ
            </button>
          </div>
        </div>
      </ng-container>
    </div>

  `,
  styleUrls: ['./salary-table.component.scss']
})
export class SalaryTableComponent implements OnInit, OnChanges {
  salaryModel = {
    niigmiinDaatgalExcluded: false,
    healthInsuranceExcluded: false,
    dateAndSalaries: {},
  };
  @Input() completedForm;
  @Input() instanceId;
  @Input() requestId;
  @Output() submitFormEmitter = new EventEmitter<any>();
  columns = SALARY_CALCULATION_COLUMNS;
  data: SalaryCalculationModel[];
  saveSalaryModel: SaveSalaryModel = {
    averageAfterTax: 0, averageBeforeTax: 0, hasMortgage: false, ndsh: false, emd: false, salariesInfo: {}
  };
  defaultSalaryMap = new Map();
  form;
  loanType: string;
  resultForm = SALARY_RESULT_FORM;
  averageSalaryAfterTax: FormsModel = SALARY_RESULT_FORM[0];
  averageSalaryBeforeTax: FormsModel = SALARY_RESULT_FORM[1];
  checkCounter = 0;
  dateFormat = false;
  setButtonDisable: boolean;
  buttonState = false;

  // isSalaryCalculated = false;
  constructor(private sb: CaseViewSandboxService, private snackBar: MatSnackBar, private overlay: Overlay, private commonSb: CommonSandboxService) {
    const salaryInfo: SalaryCalculationModel[] = [];
    let month = defaultDate.getMonth() - 1;
    for (let i = 0; i < 12; i++) {
      const dates = new Date(defaultDate.getFullYear(), month);
      month--;

      salaryInfo.push({
        checked: false, date: dates, defaultValue: 0, month: i + 1, personalIncomeTax: 0, salaryAfterTax: 0, socialInsuranceTax: 0,
        isXypSalary: false, isCompleted: false
      });
    }
    this.data = salaryInfo;
    this.buttonState = false;
  }

  ngOnChanges(changes: SimpleChanges): void {
    /* It's called when switch between active and completed task */
    if (!changes.instanceId) {
      this.ngOnInit().then();
    }
  }

  getXypSalary() {
    this.sb.getXypSalary(this.instanceId).subscribe(res => {
      for (const property in res) {
        if (res.hasOwnProperty(property)) {
          this.defaultSalaryMap.set(property, res[property]);
        }
      }
    });
  }

  async ngOnInit() {
    this.getXypSalary();
    this.buttonState = false;
    this.form = SALARY_FIELDS;
    const res = await this.sb.getRequestById(this.requestId).toPromise();
    this.loanType = (null != res && null != res.entity) ? res.entity.productCategory : null;
    if (this.completedForm && this.completedForm.length === undefined || this.completedForm.length > 0) {
      this.setForm(this.completedForm, true, []);
      this.setDefaultSalary(this.completedForm, '-', true);
    } else {
      const overlay = this.commonSb.setOverlay();
      this.sb.getDefaultSalary(this.instanceId).subscribe(resFromDefault => {
        const options = [{id: 'option1', value: NO}, {id: 'option2', value: YES}];
        this.setForm(resFromDefault, false, options);
        this.setSalaryValue(resFromDefault);
        overlay.dispose();
      });
      this.resultForm[0].formFieldValue.defaultValue = 0;
      this.resultForm[1].formFieldValue.defaultValue = 0;
    }
  }

  setSalaryValue(resFromDefault) {
    const isEmptyObject: boolean = this.isEmptyObject(resFromDefault.salariesInfo);
    // If default salary is not empty
    if (!isEmptyObject) {
      this.setDefaultSalary(resFromDefault, ' ');
    } else {
      this.sb.getSalary(this.instanceId).subscribe((res: any) => {
        const isEmpty: boolean = this.isEmptyObject(res.dateAndSalaries);
        if (!isEmpty) {
          this.sb.setSalaryAverage(res).subscribe(resFromCalculation => {
            this.commonSb.sortArray(resFromCalculation.afterTaxSalaries, 'date', 'date', 'desc');
            this.data = resFromCalculation.afterTaxSalaries;
            this.data.forEach(salary => salary.isXypSalary = false);
          });
        }
      }, error => {
        throwError(error);
      });
    }
  }

  setDefaultSalary(resFromDefault, splitChar: string, isCompleted?): void {
    this.buttonState = !!isCompleted;
    const tmpData: SalaryCalculationModel[] = [];
    // Copy data to this.data
    for (const entity in resFromDefault.salariesInfo) {
      if (resFromDefault.salariesInfo.hasOwnProperty(entity)) {
        const year = this.getYear(entity, splitChar);
        const month = this.getMonth(entity, splitChar);
        const date2 = new Date(Number(year), Number(month));
        const xypSalary = resFromDefault.salariesInfo[entity].salaryFromXyp;
        const isChecked = resFromDefault.salariesInfo[entity].checked;
        const completedForm = !!isCompleted;
        const salaryValue = resFromDefault.salariesInfo[entity].monthlySalaryBeforeTax;
        tmpData.push({
          checked: isChecked, month: 0, isXypSalary: xypSalary, date: date2, isCompleted: completedForm,
          defaultValue: salaryValue !== undefined ? salaryValue : resFromDefault.salariesInfo[entity].salaryBeforeTax,
          socialInsuranceTax: resFromDefault.salariesInfo[entity].ndsh,
          personalIncomeTax: resFromDefault.salariesInfo[entity].hhoat,
          salaryAfterTax: resFromDefault.salariesInfo[entity].monthlySalaryAfterTax !== undefined ?
            resFromDefault.salariesInfo[entity].monthlySalaryAfterTax : resFromDefault.salariesInfo[entity].salaryAfterTax
        });
      }
    }
    this.commonSb.sortArray(tmpData, 'date', 'date', 'desc');
    for (let i = 0; i < tmpData.length; i++) {
      tmpData[i].month = (i + 1);
    }
    this.data = tmpData;
  }

  getYear(entity: string, splitChar) {
    return splitChar === ' ' ? entity.split(splitChar)[5] : entity.split(splitChar)[0];
  }

  getMonth(entity: string, splitChar) {
    return splitChar === ' ' ? MONTH_NAMES.indexOf(entity.split(splitChar)[1]) : Number(entity.split(splitChar)[1]) - 1;
  }

  sortByDate(salaryInfo: SalaryCalculationModel[]) {
    salaryInfo.sort((a, b) => {
      return Number(b.date) - Number(a.date);
    });
  }

  completeTask() {
    this.save(true);
  }

  // editButton(): void {
  //   this.isSalaryCalculated = false
  //   this.form.forEach(field => {
  //     field.validations = []
  //   })
  // }
  save(completeTask: boolean, isOnlySave?): void {
    this.simplifyForSave();
    const isValidAverageSalary = this.checkAverageSalary();
    const isValidToSaveSalary = this.dateFormat && isValidAverageSalary;
    if (isValidToSaveSalary) {
      this.saveSalary(isOnlySave);
      if (completeTask) {
        this.submit();
      }
    } else if (!this.dateFormat) {
      this.snackBar.open('Он сар буруу байна.', null, {duration: 3000});
    } else {
      this.snackBar.open('Дундаж цалин талбар хоосон байна.', null, {duration: 3000});
    }
  }

  saveSalary(isOnlySave?) {
    const overlayRef = this.setOverlay();
    this.sb.saveSalary(this.saveSalaryModel, this.instanceId).subscribe(() => {
      if (isOnlySave) {
        this.snackBar.open('Амжилттай хадгаллаа!', null, {duration: 3000});
      }
    }, catchError(err => {
      this.snackBar.open('Амжилтгүй!', null, {duration: 3000});
      overlayRef.dispose();
      return throwError(err);
    }));
    overlayRef.dispose();
  }

  submit(): void {
    const overlayRef = this.setOverlay();
    const submitForm: FormsModel[] = [];
    submitForm.push(this.form[2]);
    submitForm.push(this.resultForm[0]);
    submitForm.push(this.resultForm[1]);
    submitForm[0].formFieldValue.defaultValue = submitForm[0].formFieldValue.defaultValue === YES;
    submitForm.push(SALARY_CALCULATION);
    this.submitFormEmitter.emit([this.saveSalaryModel, submitForm]);
    overlayRef.dispose();
  }

  checkAverageSalary(): boolean {
    for (const value of this.resultForm) {
      if (value.formFieldValue.defaultValue <= 0) {
        return false;
      }
    }
    return true;
  }

  calculateAverage(): void {
    const overlayRef = this.setOverlay();
    this.simplifySalary();
    const isValid = this.checkCounter > -1 && this.dateFormat;
    const hasMortgage = this.commonSb.getFormValue(this.form, 'hasMortgage');
    if (isValid) {
      this.sb.setSalaryAverage(this.salaryModel).subscribe(res => {
        SALARY_RESULT_FORM[0].formFieldValue.defaultValue = res.averageSalaryAfterTax;
        this.averageSalaryAfterTax = SALARY_RESULT_FORM[0];
        SALARY_RESULT_FORM[1].formFieldValue.defaultValue = res.averageSalaryBeforeTax;
        this.averageSalaryBeforeTax = SALARY_RESULT_FORM[1];

        if (hasMortgage == null) {
          this.snackBar.open('Орон сууцны зээлтэй эсэх талбарыг сонгоно уу.', null, {duration: 3000});
        }
        // this.isSalaryCalculated = true;
        // this.form.forEach(field => {
        //   field.validations.push({name: 'readonly', configuration: null})
        // })

        const tmpData: SalaryCalculationModel[] = [];
        for (let i = 0; i < this.data.length; i++) {
          if (i < this.checkCounter || i > this.checkCounter + 11) {
            tmpData[i] = this.data[i];
          } else if (null != res.afterTaxSalaries[i - this.checkCounter]) {
            res.afterTaxSalaries[i - this.checkCounter].checked = this.data[i].checked;
            res.afterTaxSalaries[i - this.checkCounter].month = this.data[i].month;
            res.afterTaxSalaries[i - this.checkCounter].isXypSalary = this.data[i].isXypSalary;
            tmpData[i] = res.afterTaxSalaries[i - this.checkCounter];
          } else {
            tmpData[i] = this.data[i];
          }
        }
        this.data = tmpData;
        overlayRef.dispose();
      }, catchError(err => {
        return throwError(err);
      }));
    } else {
      overlayRef.dispose();
      if (this.checkCounter === -1) {
        this.snackBar.open('Мөр сонгогдоогүй байна.', null, {duration: 3000});
      } else if (!this.dateFormat) {
        this.snackBar.open('Он сар буруу байна.', null, {duration: 3000});
      }
    }
  }

  formatNumber(input: string) {
    return this.commonSb.formatNumberService(input);
  }

  simplifySalary(): void {
    this.salaryModel.niigmiinDaatgalExcluded = this.form[0].formFieldValue.defaultValue === YES;
    this.salaryModel.healthInsuranceExcluded = this.form[1].formFieldValue.defaultValue === YES;
    this.dateFormat = true;
    let salaries = {};
    this.isChecked();
    if (this.checkCounter !== -1) {
      salaries = this.getSalaryForm(this.checkCounter, this.checkCounter + 12);
    }
    this.salaryModel.dateAndSalaries = salaries;
  }

  getSalaryForm(start: number, end: number, saveSalary?: boolean) {
    this.dateFormat = true;
    const salaries = {};
    const dateInput = document.getElementsByClassName('dateInput');
    for (let i = start; i < end; i++) {
      if (i >= dateInput.length) {
        break;
      }
      const dateString = (dateInput[i] as HTMLInputElement).value.split('/');
      if (!Number(dateString[0]) || Number(dateString[0]) > 12 || !Number(dateString[1]) || dateString[1].length !== 4) {
        this.dateFormat = false;
        break;
      }
      const property = this.getDateProperty(dateString[0], dateString[1]);
      this.setData(property, salaries, i, saveSalary);
    }

    return salaries;
  }

  getDateProperty(month: string, year: string) {
    if (month.length === 1) {
      month = '0' + month;
    }
    return year + '-' + month + '-01';
  }

  setData(property, salaries, i, saveSalary) {
    if (saveSalary) {
      const checked = this.data[i].checked;
      const ndsh = this.data[i].socialInsuranceTax;
      const hhoat = this.data[i].personalIncomeTax;
      const salaryBeforeTax = this.formatNumber(String(this.data[i].defaultValue));
      const salaryAfterTax = this.data[i].salaryAfterTax;
      const salaryFromXyp = this.checkXypSalary(property, salaryBeforeTax, i);
      salaries[property] = {
        checked, ndsh, hhoat, salaryBeforeTax, salaryAfterTax, salaryFromXyp
      };
    } else {
      if (this.data[i].checked) {
        salaries[property] = this.formatNumber(String(this.data[i].defaultValue));
      }
    }
  }

  simplifyForSave(): void {
    const start = 0;
    const end = this.data.length;
    const salaries = this.getSalaryForm(start, end, true);
    const hasMortgage = this.form.find(field => field.id === 'hasMortgage').formFieldValue.defaultValue;
    const NDSH = this.form.find(field => field.id === 'ndsh').formFieldValue.defaultValue;
    const EMD = this.form.find(field => field.id === 'emd').formFieldValue.defaultValue;

    if (this.averageSalaryBeforeTax.formFieldValue.defaultValue !== '' && this.averageSalaryAfterTax.formFieldValue.defaultValue !== '') {
      this.saveSalaryModel.averageBeforeTax = this.formatNumber(this.averageSalaryBeforeTax.formFieldValue.defaultValue);
      this.saveSalaryModel.averageAfterTax = this.formatNumber(this.averageSalaryAfterTax.formFieldValue.defaultValue);
      this.saveSalaryModel.hasMortgage = hasMortgage;
      this.saveSalaryModel.ndsh = NDSH;
      this.saveSalaryModel.emd = EMD;
      this.saveSalaryModel.salariesInfo = salaries;
    } else {
      this.checkCounter = -2;
    }
  }

  checkXypSalary(property, salary, index): boolean {
    if (!this.defaultSalaryMap.has(property)) {
      return false;
    }
    const currentSalary = Math.round(salary);
    const xypSalary = Math.round(this.defaultSalaryMap.get(property));
    return currentSalary === xypSalary;
  }

  isChecked() {
    // check whether is there any checked row on the table
    this.checkCounter = -1;
    for (let i = 0; i < this.data.length; i++) {
      if (this.data[i].checked) {
        this.checkCounter = i;
        break;
      }
    }
  }

  private setOverlay() {
    const overlayRef = this.overlay.create({
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
    });
    overlayRef.attach(new ComponentPortal(ErinLoaderComponent));
    return overlayRef;
  }

  isEmptyObject(object: {}): boolean {
    let counter = 0;
    for (const property in object) {
      if (object.hasOwnProperty(property)) {
        counter++;
        break;
      }
    }
    return counter === 0;
  }

  setForm(res, disabled: boolean, options) {
    const optionHasMortgage = [{id: 'option1', value: NO}, {id: 'option2', value: YES}, {id: 'option3', value: BANK_CONDITION_HOUSE}];
    if (!this.isEmptyObject(res)) {
      this.form.forEach(field => {
        if (field.id === 'hasMortgage') {
          field.options = optionHasMortgage;
        } else {
          field.options = options;
        }
        field.disabled = disabled;
        if (field.id === 'ndsh' && res.ndsh.length > 0) {
          if (res.ndsh) {
            field.options = this.getDefaultOptionValue(res.ndsh);
          } else {
            field.formFieldValue.defaultValue = res.ndsh;
          }
        }
        if (field.id === 'emd' && res.emd.length > 0) {
          if (res.emd) {
            field.options = this.getDefaultOptionValue(res.emd);
          } else {
            field.formFieldValue.defaultValue = res.emd;
          }
        }
        if (this.loanType === MORTGAGE_LOAN) {
          if (field.id === 'hasMortgage') {
            field.formFieldValue.defaultValue = YES;
            field.disabled = true;
            field.options = this.getDefaultOptionValue(YES);
          }
        } else {
          if (field.id === 'hasMortgage' && res.hasMortgage.length > 0) {
            if (res.hasMortgage) {
              field.options = this.getMortgageDefaultOptionValue(res.hasMortgage);
              field.formFieldValue.defaultValue = res.hasMortgage;
            }
          }
        }
      });
      this.averageSalaryBeforeTax.formFieldValue.defaultValue = res.averageBeforeTax;
      this.averageSalaryAfterTax.formFieldValue.defaultValue = res.averageAfterTax;
    }
  }

  dateFieldValidation(dateFieldValidation: boolean) {
    this.setButtonDisable = dateFieldValidation;
  }

  getDefaultOptionValue(value) {
    if (value === YES) {
      return [{id: 'option1', value: YES}, {id: 'option2', value: NO}];
    }
    if (value === NO) {
      return [{id: 'option1', value: NO}, {id: 'option2', value: YES}];
    }
  }

  getMortgageDefaultOptionValue(value) {
    if (value === YES) {
      return [{id: 'option1', value: YES}, {id: 'option2', value: NO}, {id: 'option3', value: BANK_CONDITION_HOUSE}];
    }
    if (value === NO) {
      return [{id: 'option1', value: NO}, {id: 'option2', value: YES}, {id: 'option3', value: BANK_CONDITION_HOUSE}];
    }
    if (value === BANK_CONDITION_HOUSE) {
      return [{id: 'option1', value: BANK_CONDITION_HOUSE}, {id: 'option2', value: NO}, {id: 'option3', value: YES}];
    }
    if (value == null) {
      return [{id: 'option1', value: NO}, {id: 'option2', value: YES}, {id: 'option3', value: BANK_CONDITION_HOUSE}];
    }
  }
}
