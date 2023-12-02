import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {DatePipe} from '@angular/common';

export interface ColumnDef {
  columnDef: string;
  headerText: string;
}

@Component({
  selector: 'salary-calculation-table',
  template: `
    <p class="errorMessage">{{errorMessage}}</p>
    <mat-table [dataSource]="dataSource">
      <ng-container *ngFor="let column of columns" [cdkColumnDef]="column.columnDef">
        <mat-header-cell *cdkHeaderCellDef>{{column.headerText}}</mat-header-cell>
        <mat-cell *cdkCellDef="let row; let i=index">
          <!--          <mat-checkbox #value color="primary" *ngIf="useCheckBox(column.columnDef)"-->
          <!--                        [checked]="row.checked" (change)="check(i, value)"-->
          <!--                        [disabled]="data.length - 12<i || disableState"></mat-checkbox>-->
          <mat-checkbox #value color="primary" *ngIf="useCheckBox(column.columnDef)"
                        [checked]="row.checked" (change)="check(i, value)"
                        [disabled]="disableCheckBox(i) || disableState"></mat-checkbox>
          <span [thousandSeparator]="row[column.columnDef]"
                *ngIf="!isDate(row[column.columnDef]) && !isEditable(column.columnDef)">
          </span>
          <input
            [disabled]="disableState"
            [ngClass]="{'highlightedSalaryRow': !row.isXypSalary && row.isCompleted}"
            [(ngModel)]="row[column.columnDef]"
            *ngIf="!isDate(row[column.columnDef]) && isEditable(column.columnDef)"
            [thousandSeparator]="row[column.columnDef]"
            [element]="row" tabindex="34" onCopy="return false" onDrag="return false" onDrop="return false" onPaste="return false" autocomplete=off>
          <input #date_input *ngIf="isDate(row[column.columnDef])"
                 [disabled]="disableState"
                 [ngModel]="getDate(row[column.columnDef])"
                 (ngModelChange)="dateChange(i, date_input)"
                 dateFormatValidation
                 maxlength="7"
                 class="dateInput" tabindex="34"
                 onCopy="return false" onDrag="return false" onDrop="return false" onPaste="return false" autocomplete=off>
        </mat-cell>
      </ng-container>

      <mat-header-row *matHeaderRowDef="displayedColumns; sticky:true"></mat-header-row>
      <mat-row *matRowDef="let row; columns: displayedColumns;"></mat-row>
    </mat-table>

  `,
  styleUrls: ['./salary-calculation-table.component.scss']
})
export class SalaryCalculationTableComponent implements OnChanges, OnInit {
  @Input() data;
  @Input() columns: ColumnDef[];
  @Input() disableState: boolean;
  @Output() dateFieldValidation: EventEmitter<boolean> = new EventEmitter();
  displayedColumns: string[];
  dataSource = new MatTableDataSource();
  month: string[] = ['Jan', 'Feb', 'March', 'Apr', 'May', 'June', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  errorMessage: string;
  SELECT_LIMIT = 6;
  isDownloadedXyp = false;

  constructor(public datePipe: DatePipe) {
  }

  ngOnInit() {
    this.displayedColumns = this.columns.map(c => c.columnDef);
    this.dateFieldValidation.emit(false);
  }

  ngOnChanges(): void {
    this.dataSource = new MatTableDataSource(this.data);
    this.autoSelectFirstRows();
  }

  autoSelectFirstRows() {
    if (this.isDownloadedXyp == false) {
      for (const row of this.data) {
        if (!row.checked && row.month < 13) {
          row.checked = true
          this.isDownloadedXyp = true;
        }
      }
    }
  }

  isDate(rowElement): boolean {
    return rowElement instanceof Date;
  }

  isEditable(type: string): boolean {
    return type === 'defaultValue';
  }

  useCheckBox(columnDef: string): boolean {
    return columnDef === 'month';
  }

  private uncheckAll(): void {
    this.data.forEach( rowData => rowData.checked = false);
  }

  private inLastLimitedRows(index: number): boolean {
    return this.data.length - this.SELECT_LIMIT < index && !this.data[index - 1].checked;
  }

  private isPreviousChecked(index: number): boolean {
    if (index === 0) {
      return false;
    }
    return this.data[index - 1].checked;
  }

  disableCheckBox(index: number): boolean {
    // if row is checked then enable checkbox
    if (this.data[index].checked) {
      return false;
    }

    let count = 0;
    for (const row of this.data) {
        if (row.checked) {
          count++;
        }
    }

    // if any row except first row is checked then first row must be disabled
    if (index === 0 && count > 0 && count < 12) {
      return true;
    }

    // if no row is checked then each row must be enabled
    if (!this.inLastLimitedRows(index) && count === 0 ) {
      return false;
    }

    // if no row is checked and current row is the one of the last rows then it cannot be enabled
    if (this.inLastLimitedRows(index) && count === 0 && this.data[index - 1].checked && !this.data[index].checked) {
      return true;
    }

    // if total checked row is less than 12 and previous row is checked then current row must be enabled. If not it should be disabled
    if (count < 12 && this.data[index - 1].checked && !this.data[index].checked) {
      return false;
    } else if (count < 12 && !(this.data[index - 1].checked && !this.data[index].checked) ) {
      return true;
    }

    // if all checked row is 12 then the other rows must be disabled.
    return count >= 12;

  }

  private validateAndCheck(index: number): void {
    if (this.isPreviousChecked(index)) {
      this.data[index].checked = true;
    } else {
      for (let i = index; i < index + 12; i++) {
        this.data[i].checked = true;
      }
    }
  }

  check(index: number, value) {
    const checkbox = document.getElementById(value.id);
    const input = checkbox.getElementsByTagName('input')[0];

    if (input.checked) {
      this.validateAndCheck(index);
    } else {
      // uncheck previous checks
      this.uncheckAll();
    }
  }

  dateChange(index, dateInput) {
    let currentDate;
    if (new Date().getMonth() + 1 < 10) {
      currentDate = new Date().getFullYear().toString() + '-' + '0' + (new Date().getMonth() + 1).toString() ;
    } else {
      currentDate = new Date().getFullYear().toString() + '-' + (new Date().getMonth() + 1).toString();
    }
    if (dateInput.value !== undefined && dateInput.value.length > 0) {
      const date = String(dateInput.value).split('/');
      if ((date[0] !== undefined && date[0].length > 0) && (date[1] !== undefined && date[1].length >= 4)) {
        this.data[index].date = new Date(Number(date[1]), Number(date[0]) - 1);
        // if (this.datePipe.transform(new Date(this.data[index].date), 'yyyy-MM') < this.datePipe.transform(new Date(currentDate), 'yyyy-MM')) {
          for (let i = 0; i < 12; i++) {
            const tmp = Number(date[0]) - 1;
            date[0] = String(tmp);
            this.data[i].date = new Date(Number(date[1]), Number(date[0]));
          }
          this.dateFieldValidation.emit(false);
          this.errorMessage = '';
        // }
        //  else {
        //   this.dateFieldValidation.emit(true);
        //   this.errorMessage = 'Зөв огноо оруулна уу!';
        // }
      }
    }
  }
  getDate(date: Date): string {
    const month = (date.getMonth() + 1) < 10 ? '0' + (date.getMonth() + 1) : (date.getMonth() + 1);
    return month + '/' + date.getFullYear();
  }
}
