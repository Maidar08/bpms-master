import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonSandboxService } from '../../../../common/common-sandbox.service';
import { FormsModel } from '../../../../models/app.model';
import { ColumnDef } from '../../../../models/common.model';
import { CaseViewSandboxService } from "../../../case-view-sandbox.service";
import {
  AMOUNT1,
  AMOUNT2,
  BALANCE_ASSET_FORM,
  BALANCE_DEBT_FORM,
  BALANCE_OPERATION_FORM,
  BALANCE_SALE_FORM,
  BALANCE_TOTAL_INCOME_AMOUNT,
  BALANCE_TOTAL_INCOME_PERCENT, CLOSE,
  CURRENT_ASSET_AMOUNT,
  CURRENT_ASSET_PERCENT,
  MAIN_ASSET_AMOUNT,
  MAIN_ASSET_PERCENT,
  PERCENT1,
  PERCENT2,
  REPORT_COVERAGE_PERIOD,
  SHORT_TERM_DEBT_AMOUNT,
  SHORT_TERM_DEBT_PERCENT,
  TOTAL_DEBT_AMOUNT,
  TOTAL_DEBT_PERCENT,
  TOTAL_OPERATION_COST_AMOUNT,
  TOTAL_OPERATION_COST_PERCENT,
  TOTAL_OPERATION_INCOME_AMOUNT,
  TOTAL_OPERATION_INCOME_PERCENT,
  TOTAL_SALE_AMOUNT,
  TOTAL_SALE_COST,
  TOTAL_SALE_COST_PERCENT,
  VALUE_ONE_MONTH_DIALOG_MESSAGE
} from '../../../model/task-constant';
import {
  BALANCE_ASSET_COLUMN,
  BALANCE_ASSET_TABLE_DATA,
  BALANCE_DEBT_COLUMN,
  BALANCE_DEBT_TABLE_DATA,
  BALANCE_OPERATION_COLUMN,
  BALANCE_OPERATION_TABLE_DATA,
  BALANCE_SALE_COLUMN,
  BALANCE_SALE_TABLE_DATA
} from '../../../model/task-table-column.model';

@Component({
  selector: 'balance-calculation',
  template: `
    <mat-card class="card-container">
      <div class="title-container">
        <span class="title">{{taskName}}</span>
      </div>
      <mat-expansion-panel>
        <mat-expansion-panel-header>
          <mat-panel-title>Орлогын тайлан</mat-panel-title>
        </mat-expansion-panel-header>
        <div class="subtitle">
        <mat-label>Эхлэх огноо</mat-label>
        <erin-datepicker-field [field] = "getStartDateField()"></erin-datepicker-field>
        <mat-label>Дуусах огноо</mat-label>
        <erin-datepicker-field [field] = "getEndDateField()"></erin-datepicker-field>
      </div>


      <span>{{this.reminderText}}</span>

      <add-remove-box *ngIf="!completedState" (inc)="increaseSaleData($event)" (dec)="decreaseSaleData()"
      [form]="data[0]"></add-remove-box>

      <erin-table [columns]="saleColumn" [data]="data[0]" (change)="updateSaleData()"
                  [style]="saleColumnStyle"></erin-table>

        <div class="sale-result">
          <span>Нийт ашиг</span>
          <erin-number-input-field [field]="getTotalIncomeAmountField()"></erin-number-input-field>
          <erin-percent-field [field]="getTotalIncomeAmountPercent()"></erin-percent-field>
        </div>

        <erin-table [columns]="operationColumn" [data]="data[1]"></erin-table>
      </mat-expansion-panel>

      <!-- Тайлан тэнцэл -->
      <mat-expansion-panel>
        <mat-expansion-panel-header>
          <mat-panel-title>Тайлан тэнцэл</mat-panel-title>
        </mat-expansion-panel-header>
        <erin-table [columns]="assetColumn" [data]="data[2]"></erin-table>
        <erin-table [columns]="debtColumn" [data]="data[3]"></erin-table>
      </mat-expansion-panel>

      <!-- workspace action -->
      <div class="workspace-action">
<!--        <button mat-flat-button color="primary" [disabled]="completedState" (click)="editForm()">ЗАСАХ</button>-->
        <button mat-flat-button color="primary" [disabled]="completedState" (click)="calculate()">ТООЦООЛОХ</button>
        <!--        <button mat-flat-button color="primary" [disabled]="completedState" (click)="save()">ХАДГАЛАХ</button>-->
        <button mat-flat-button color="primary" [disabled]="completedState" (click)="submit()">ҮРГЭЛЖЛҮҮЛЭХ</button>
      </div>

    </mat-card>
  `,
  styleUrls: ['./balance-calculation.component.scss']
})
export class BalanceCalculationComponent implements OnInit {

  constructor(private commonService: CommonSandboxService, private sb: CaseViewSandboxService) {
  }

  @Input() instanceId: string;
  @Input() taskName: string;
  @Input() form: FormsModel[];
  @Input() completedState: boolean;
  @Output() calculateEmitter = new EventEmitter<any[]>();
  @Output() saveEmitter = new EventEmitter<boolean>();
  @Output() submitEmitter = new EventEmitter<boolean>();
  // table columns
  saleColumn = this.commonService.clone(BALANCE_SALE_COLUMN);
  operationColumn: ColumnDef[] = this.commonService.clone(BALANCE_OPERATION_COLUMN);
  assetColumn: ColumnDef[] = this.commonService.clone(BALANCE_ASSET_COLUMN);
  debtColumn: ColumnDef[] = this.commonService.clone(BALANCE_DEBT_COLUMN);

  // table column style var
  saleColumnStyle = [];

  data = [];
  reminderText = VALUE_ONE_MONTH_DIALOG_MESSAGE;

  // isBalanceCalculated = false;
  private getHeader(): {} {
    const columnHeader = {};

    columnHeader[BALANCE_SALE_FORM] = {
      totalSaleAmount: this.saleColumn[1].headerText, totalSaleCost: this.saleColumn[4].headerText,
      totalSaleCostPercent: this.commonService.formatNumberService(this.saleColumn[5].headerText)
    };

    columnHeader[BALANCE_OPERATION_FORM] = {
      totalOperationCostAmount: this.operationColumn[1].headerText,
      totalOperationCostPercent: this.commonService.formatNumberService(this.operationColumn[2].headerText),
      totalOperationIncomeAmount: this.operationColumn[4].headerText,
      totalOperationIncomePercent: this.commonService.formatNumberService(this.operationColumn[5].headerText)
    };

    columnHeader[BALANCE_ASSET_FORM] = {
      currentAssetAmount: this.assetColumn[1].headerText,
      currentAssetPercent: this.commonService.formatNumberService(this.assetColumn[2].headerText),
      mainAssetAmount: this.assetColumn[4].headerText,
      mainAssetPercent: this.commonService.formatNumberService(this.assetColumn[5].headerText),
    };

    columnHeader[BALANCE_DEBT_FORM] = {
      shortTermDebtAmount: this.debtColumn[1].headerText,
      shortTermDebtPercent: this.commonService.formatNumberService(this.debtColumn[2].headerText),
      totalDebtAmount: this.debtColumn[4].headerText,
      totalDebtPercent: this.commonService.formatNumberService(this.debtColumn[5].headerText),
    };

    return columnHeader;
  }


  private getSubmitData(actionType) {
    const reportPeriod = 1;
    const sale = this.getBalanceSubmit(this.data[0]);
    const operation = this.getBalanceSubmit(this.data[1]);
    const asset = this.getBalanceSubmit(this.data[2]);
    const debt = this.getBalanceSubmit(this.data[3]);
    if (actionType === 'calculate') {
      return {reportPeriod: reportPeriod, sale, operation, asset, debt};
    }

    if (actionType === 'save') {
      const totalIncomeAmount = this.commonService.formatNumberService(this.commonService.getFormValue(this.form, BALANCE_TOTAL_INCOME_AMOUNT));
      const totalIncomePercent = this.commonService.formatNumberService(this.commonService.getFormValue(this.form, BALANCE_TOTAL_INCOME_PERCENT));
      const columnHeader = this.getHeader();
      return {reportPeriod: reportPeriod, sale, operation, asset, debt, columnHeader, totalIncomeAmount, totalIncomePercent};
    }
  }

  private setFieldValue(fieldId: string, value): void {
    const field = this.form.find(f => f.id === fieldId);
    if (null != field) {
      field.formFieldValue.defaultValue = value;
    }
  }

  private static setBalanceDataFromCalculate(res, balance, isCompletedTask?: boolean) {
    if (Object.keys(res).length > 0 && balance == null) {
      balance = [];
    }
    let i = 0;
    while (true) {
      if (null == res[i]) {
        break;
      }
      BalanceCalculationComponent.setRowData(balance, res, i, isCompletedTask);
      i++;
    }
    return balance;
  }

  private static setRowData(balance: any, res: any, i: number, isCompletedTask: boolean): void {
    if (isCompletedTask) {
      BalanceCalculationComponent.setCompletedStateOnRow(res[i]);
    }
    if (null == balance[i]) {
      balance.push(res[i]);
    } else {
      for (const key in res[i]) {
        if (res[i].hasOwnProperty(key)) {
          balance[i][key] = res[i][key];
        }
      }
    }
  }

  private static setCompletedStateOnRow(row: any): void {
    const disabled = [];
    for (const prop in row) {
      if (row.hasOwnProperty(prop)) {
        disabled.push(prop);
      }
    }
    row.disabled = disabled;
  }

  private getSaleData() {
    const balanceData = this.commonService.clone(BALANCE_SALE_TABLE_DATA);
    for (let i = 0; i < balanceData.length; i++) {
      balanceData[i].placeholder = balanceData[i].placeholder + ' ' + (i + 1);
      balanceData[i].bbUrtug = balanceData[i].placeholder;
    }
    return balanceData;
  }

  private setDefaultTableData(): void {
    this.data.push(this.getSaleData());
    this.data.push(this.commonService.clone(BALANCE_OPERATION_TABLE_DATA));
    this.data.push(this.commonService.clone(BALANCE_ASSET_TABLE_DATA));
    this.data.push(this.commonService.clone(BALANCE_DEBT_TABLE_DATA));
  }

  private setFromCalculate(res, isCompletedTask?: boolean): void {
    const headerName = [BALANCE_SALE_FORM, BALANCE_OPERATION_FORM, BALANCE_ASSET_FORM, BALANCE_DEBT_FORM];
    for (let i = 0; i < 4; i++) {
      const balanceData = BalanceCalculationComponent.setBalanceDataFromCalculate(res[headerName[i]], this.data[i], isCompletedTask);
      if (balanceData != null) {
        this.data[i] = balanceData;
      }
    }

    this.setFieldValue(BALANCE_TOTAL_INCOME_AMOUNT, res['totalIncomeAmount']);
    this.setFieldValue(BALANCE_TOTAL_INCOME_PERCENT, res['totalIncomePercent']);
    this.setFieldValue(REPORT_COVERAGE_PERIOD, 1);

    this.setColHeaderData(res['columnHeader']);
  }


  private assignSaleData(): void {
    this.data[0] = this.commonService.clone(this.data[0]);
  }

  private setColHeaderData(balanceTotals) {
    // set sale Col Header
    const saleColumnDefList = [AMOUNT1, AMOUNT2, PERCENT2];
    const saleColumnHeaderText = [TOTAL_SALE_AMOUNT, TOTAL_SALE_COST, TOTAL_SALE_COST_PERCENT];
    BalanceCalculationComponent.setColHeaderByRow(balanceTotals.sale, this.saleColumn, saleColumnDefList, saleColumnHeaderText);

    // set operation col Header
    const operationColumnDefList = [AMOUNT1, PERCENT1, AMOUNT2, PERCENT2];
    const operationColumnHeaderText = [TOTAL_OPERATION_COST_AMOUNT, TOTAL_OPERATION_COST_PERCENT, TOTAL_OPERATION_INCOME_AMOUNT, TOTAL_OPERATION_INCOME_PERCENT];
    BalanceCalculationComponent.setColHeaderByRow(balanceTotals.operation, this.operationColumn, operationColumnDefList, operationColumnHeaderText);

    // set asset col Header
    const assetColumnDefList = [AMOUNT1, PERCENT1, AMOUNT2, PERCENT2];
    const assetColumnHeaderText = [CURRENT_ASSET_AMOUNT, CURRENT_ASSET_PERCENT, MAIN_ASSET_AMOUNT, MAIN_ASSET_PERCENT];
    BalanceCalculationComponent.setColHeaderByRow(balanceTotals.asset, this.assetColumn, assetColumnDefList, assetColumnHeaderText);

    // set debt col Header
    const debtColumnDefList = [AMOUNT1, PERCENT1, AMOUNT2, PERCENT2];
    const debtColumnHeaderText = [SHORT_TERM_DEBT_AMOUNT, SHORT_TERM_DEBT_PERCENT, TOTAL_DEBT_AMOUNT, TOTAL_DEBT_PERCENT];
    BalanceCalculationComponent.setColHeaderByRow(balanceTotals.debt, this.debtColumn, debtColumnDefList, debtColumnHeaderText);
  }

  private static setColHeaderByRow(row, column, colDef: string[], colHeader: string[]): void {
    if (null != row) {
      for (const col of column) {
        BalanceCalculationComponent.setRowCol(row, col, colDef, colHeader);
      }
    }
  }

  private static setRowCol(row, col, colDef, colHeader): void {
    for (let i = 0; i < colDef.length; i++) {
      if (col.columnDef === colDef[i]) {
        if (colDef[i] == PERCENT1 || colDef[i] == PERCENT2) {
          col.headerText = row[colHeader[i]] + '%';
        } else {
          col.headerText = row[colHeader[i]];
        }
      }
    }
  }

  private getBalanceData(isCompletedTask?: boolean) {
    this.sb.getBalanceFromProcessParameter(this.instanceId).subscribe(res => {
      this.setFromCalculate(res, isCompletedTask);
      if (this.data.length === 0) {
        this.setDefaultTableData();
      }
    });
  }

  private setStyle(): void {
    this.saleColumnStyle = ['', '', 'percent1-10%', '', '', 'percent2-10%'];
  }

  ngOnInit(): void {
    const overlayRef = this.commonService.setOverlay();
    this.getBalanceData();
    if (this.completedState === true) {
      this.getBalanceData(true);
    }
    this.setStyle();
    overlayRef.dispose();
  }

  getMonthField() {
    return this.form.find(field => field.id === REPORT_COVERAGE_PERIOD);
  }
  getStartDateField() {
    return this.form.find(field => field.id === "startDate");
  }

  getEndDateField() {
    return this.form.find(field => field.id === "endDate");
  }

  getTotalIncomeAmountField() {
    const fieldResult = this.form.find(field => field.id === BALANCE_TOTAL_INCOME_AMOUNT);
    if (fieldResult) {
      fieldResult.disabled = true;
      return fieldResult;
    }
  }

  getTotalIncomeAmountPercent() {
    const fieldResult = this.form.find(field => field.id === BALANCE_TOTAL_INCOME_PERCENT);
    if (fieldResult) {
      fieldResult.disabled = true;
      return fieldResult;
    }
  }

  updateSaleData() {
    for (const row of this.data[0]) {
      if (row.sale !== '') {
        row.bbUrtug = row.sale;
      } else {
        row.bbUrtug = row.placeholder;
      }
    }
  }

  increaseSaleData(state) {
    if (state && this.data[0].length < 10) {
      const tmp = this.commonService.clone(BALANCE_SALE_TABLE_DATA[0]);
      tmp.placeholder = tmp.placeholder + ' ' + (this.data[0].length + 1);
      tmp.bbUrtug = tmp.placeholder;
      this.data[0].push(tmp);
      this.assignSaleData();
    }
  }

  decreaseSaleData(): void {
    this.assignSaleData();
  }

  getBalanceSubmit(form) {
    const submitMap = {};
    for (let i = 0; i < form.length; i++) {
      submitMap[i] = form[i];
    }
    return submitMap
  }

  calculate(): void {
    const overlayRef = this.commonService.setOverlay();
    const submitForm = this.getSubmitData('calculate');
    // this.isBalanceCalculated = true;
    // this.getBalanceData(true);
    this.sb.calculateMicroBalance(this.instanceId, submitForm).subscribe(res => {
      this.setFromCalculate(res);
      overlayRef.dispose();
    }, () => overlayRef.dispose());
  }

  save(): void {
    const overlayRef = this.commonService.setOverlay();
    const submitForm = this.getSubmitData('save');
    this.sb.saveMicroBalance(this.instanceId, submitForm).subscribe(res => {
      if (res) {
        this.commonService.showSnackBar('Амжилттай хадгаллаа.', null, 1500);
      }
      overlayRef.dispose();
    }, () => overlayRef.dispose());
  }

  submit(): void {
    if (!this.validateAmounts()){
      this.save();
      this.submitEmitter.emit(true);
    }
  }

  // editForm(): void {
  //   this.isBalanceCalculated = false;
  //   this.getBalanceData(false);
  // }
  private validateAmounts(): boolean {
    const amount1 = this.data[2][this.data[2].length - 1].amount2;
    const amount2 = this.data[3][this.data[3].length - 1].amount2;
    if (amount1 === 0){
      this.commonService.showSnackBar('Нийт хөрөнгийн дүн талбар 0 байна!', CLOSE, 3000);
      return true;
    }
    if (amount2 === 0){
      this.commonService.showSnackBar('Нийт эх үүсвэр талбар 0 байна!', CLOSE, 3000);
      return true;
    }
    if (amount1 !== amount2){
      this.commonService.showSnackBar('Нийт хөрөнгийн дүн, Нийт эх үүсвэр талбарууд тэнцэх шаардлагатай', CLOSE, 3000);
      return true;
    }
    let obj1 = this.operationColumn.find(o => o.columnDef === 'amount1');
    let obj2 = this.operationColumn.find(o => o.columnDef === 'amount2');
    if(obj1.headerText == "0"){
      this.commonService.showSnackBar('Үйл ажиллагааны зардал талбар 0 байна!', CLOSE, 3000);
      return true;
    }
    if(obj2.headerText == "0"){
      this.commonService.showSnackBar('Үйл ажиллагааны ашиг талбар 0 байна!', CLOSE, 3000);
      return true;
    }

    if (this.commonService.getFormValue(this.form, BALANCE_TOTAL_INCOME_AMOUNT) == 0) {
      this.commonService.showSnackBar( 'Нийт ашиг талбар 0 байна!', CLOSE, 3000);
      return true;
    }
    return false;
  }
}
