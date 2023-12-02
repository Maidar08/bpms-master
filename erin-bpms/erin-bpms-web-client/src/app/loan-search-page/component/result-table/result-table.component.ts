import {AfterViewInit, Component, EventEmitter, Input, OnChanges, Output, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatMenuTrigger} from '@angular/material/menu';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {ChangeAssigneeDialogComponent} from '../change-assignee-dialog/change-assignee-dialog.component';
import {FormsModel} from '../../../models/app.model';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {throwError} from 'rxjs';
import {ColumnDef, RowEvent} from '../../../models/common.model';
import {ConfirmDeleteDialogComponent} from '../confirm-delete-dialog/confirm-delete-dialog.component';
import {AuthModel} from '../../../models/auth.model';
import {CommonSandboxService} from '../../../common/common-sandbox.service';

@Component({
  selector: 'result-table',
  template: `
    <div class="container">
      <mat-table [dataSource]="dataSource" [class.selected-row]="(hoverHighlight)"
                 class="mat-elevation-z2" matSort matSortActive="id" matSortDirection="desc">
        <ng-container *ngFor="let column of columns; let i = index" [cdkColumnDef]="column.columnDef">
          <mat-header-cell *cdkHeaderCellDef mat-sort-header class="table-header-cell_{{i}}">{{column.headerText}}</mat-header-cell>
          <mat-cell class="mat-table-cell_{{i}}" *cdkCellDef="let row" matTooltip="{{row[column.columnDef]}}">

            <span (click)="itemAction(row)" *ngIf="!isDate(row[column.columnDef]) && !useSeparator(column.columnDef)">
            {{row[column.columnDef]}}</span>
            <!-- Default case can be added in future -->
            <span (click)="itemAction(row)" *ngIf="!isDate(row[column.columnDef]) && useSeparator(column.columnDef)"
                  [thousandSeparator]="row[column.columnDef]"></span>

            <span (click)="itemAction(row)" *ngIf="isDate(row[column.columnDef])">{{row[column.columnDef]| date: 'yyyy-MM-dd'}}</span>
            <span class='tablet-button' *ngIf="column.columnDef === 'button' && button">
                  <button *ngIf="showTransfer || deletePermission" mat-icon-button [matMenuTriggerFor]="contextMenu">
                    <mat-icon>more_vert</mat-icon></button>
            </span>
          </mat-cell>
        </ng-container>
        <mat-header-row *matHeaderRowDef="['extra-header']; sticky:true"></mat-header-row>
        <ng-container matColumnDef="extra-header">
          <mat-header-cell *matHeaderCellDef>{{topHeader}}</mat-header-cell>
        </ng-container>

        <mat-header-row *matHeaderRowDef="displayedColumns; sticky:true"></mat-header-row>
        <mat-row *matRowDef="let row; columns: displayedColumns;" (contextmenu)="contextMenuOpen($event, row)" (dblclick)="rowDblClick(row)"
                 (click)="selectRow(row)" [ngClass]="checkSelectedItem(row)"></mat-row>
      </mat-table>
      <p *ngIf="isDataEmpty()" class="mat-title error-table-text">ЖАГСААЛТ ХООСОН БАЙНА</p>
    </div>
    <pagination [defaultPageSize]="100" *ngIf="paginator" [dataSource]="dataSource"></pagination>
    <mat-menu #contextMenu="matMenu">
      <ng-template matMenuContent let-item="item">
        <button mat-menu-item *ngIf="showTransfer" (click)="openDialog()">
          <mat-icon class="menuIcon">person</mat-icon>
          Шилжүүлэх
        </button>
        <button mat-menu-item *ngIf="deleteButtonShow()" (click)="openDeleteDialog()">
          <mat-icon class="menuIcon">delete</mat-icon>
          Устгах
        </button>
        <span mat-menu-item *ngIf="!deleteButtonShow() && deleteButtonShow() != null">Устгах боломжгүй</span>
      </ng-template>
    </mat-menu>
  `,
  styleUrls: ['./result-table.component.scss']
})

export class ResultTableComponent implements OnChanges, AfterViewInit {
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @Input() data;
  @Input() columns: ColumnDef[];
  @Input() hoverHighlight: boolean;
  @Input() paginator;
  @Input() topHeader: string;
  @Input() processType: string;
  @Input() groupId: string;
  @Input() contextmenuIdentifier = false;
  @Output() actionOnRow = new EventEmitter<RowEvent>();
  @Output() refresh = new EventEmitter<boolean>();
  @Output() clickOnRow = new EventEmitter<any>();
  @Input() button;
  @Input() showTransfer;
  displayedColumns: string[];
  dataSource = new MatTableDataSource();
  private selectedItem: any;
  @ViewChild(MatMenuTrigger)
  contextMenu: MatMenuTrigger;
  isClicked = false;
  dialogData = {id: '', state: '', confirmButton: 'Тийм', closeButton: 'Үгүй'};
  userPermissions: AuthModel;
  deletePermission;
  checkInProgressRequest;

  constructor(public dialog: MatDialog, private sb: LoanSearchPageSandbox, private snackBar: MatSnackBar, private commonSb: CommonSandboxService) {
    this.sb.auth$.subscribe(res => this.userPermissions = res);
  }

  ngOnChanges() {
    this.toUpperCaseRegister();
    this.displayedColumns = this.columns.map(c => c.columnDef);

    const afterChangeIdTypeToNumber = this.updateIdTypeToNumber(this.data);
    this.dataSource = new MatTableDataSource(afterChangeIdTypeToNumber);
    if (!this.processType) {
      this.dataSource.sort = this.sort;
    }
    if (this.data && this.data[0]) {
      this.selectRow(this.data[0]);
    }
    this.ngAfterViewInit();
  }

  ngAfterViewInit() {
    if (this.userPermissions) {
      this.deletePermission = this.userPermissions.permissions.find(values => values.id === 'bpms.bpm.DeleteProcess');
      this.checkInProgressRequest = this.userPermissions.permissions.find(values => values.id === 'bpms.bpm.CheckInProgressRequest');
    }
    this.sortData();
  }

  openDialog() {
    const overlayRef = this.commonSb.setOverlay();
    const form: FormsModel = {
      label: '', formFieldValue: {defaultValue: null, valueInfo: null},
      validations: [], type: '', options: [], id: '', context: '', disabled: false, required: false
    };
    this.sb.getUsersById(this.groupId).subscribe(res => {
      for (const user of res) {
        const tmp = {id: user.userId, value: user.firstName};
        form.options.push(tmp);
      }
      this.callDialog(form);
      overlayRef.dispose();
    }, error => {
      overlayRef.dispose();
      return throwError(error);
    });
  }

  callDialog(form: FormsModel) {
    const dialogRef = this.dialog.open(ChangeAssigneeDialogComponent, {
      width: '400px',
      data: {form}
    });
    dialogRef.afterClosed().subscribe(resFromDialog => {
      if (resFromDialog !== undefined) {
        const overlayRef = this.commonSb.setOverlay();
        const valueField = resFromDialog.formFieldValue.defaultValue;
        resFromDialog.formFieldValue.defaultValue = resFromDialog.options.find(option => option.value === valueField).id;
        this.sb.updateAssignedUser(resFromDialog.formFieldValue.defaultValue, this.selectedItem.id).subscribe(res2 => {
          if (this.showSnackBar(res2)) {
            overlayRef.dispose();
            this.refresh.emit(true);
          }
          location.reload();
        }, () => {
          overlayRef.dispose();
        });
      }
    }, error => {
      return throwError(error);
    });
  }

  deleteButtonShow(): boolean {
    if (this.contextmenuIdentifier === false && this.deletePermission) {
      if (this.selectedItem.productCategory === 'onlineSalary') {
        return this.checkOnlineSalaryState(this.selectedItem.state);
      } else if (this.selectedItem.productCategory === 'instantLoan') {
        return this.checkInstantLoanState(this.selectedItem.state);
      } else if (this.selectedItem.productCategory === 'bnplLoan') {
        return this.checkBnplLoanState(this.selectedItem.state);
      } else if (this.selectedItem.productCategory === 'onlineLeasing') {
        return this.checkOnlineLeasingState(this.selectedItem.state);
      }
      return this.selectedItem.state === 'ШИНЭ' || this.selectedItem.state === 'СУДЛАГДАЖ БАЙНА';
    }
  }

  checkOnlineSalaryState(state: string): boolean {
    switch (state) {
      case 'ШИНЭ':
      case 'СУДЛАГДАЖ БАЙНА':
      case 'SESSION ДУУССАН':
      case 'БАНК ТАТГАЛЗСАН':
      case 'СКОРИНГ ТАТГАЛЗСАН':
      case 'ДҮН ХҮРЭЭГҮЙ':
      case 'ЗАХИРАЛ-БАНК ТАТГАЛЗСАН':
      case 'АЛДАА ГАРСАН':
        return true;
      default:
        return false;
    }
  }

  checkInstantLoanState(state: string): boolean {
    switch (state) {
      case 'АЛДАА ГАРСАН':
      case 'БАНК ТАТГАЛЗСАН':
      case 'ЗАХИРАЛ-БАНК ТАТГАЛЗСАН':
      case 'ДҮН ХҮРЭЭГҮЙ':
        return true;
      default:
        return false;
    }
  }

  /* In case of adding new state */
  checkBnplLoanState(state: string): boolean {
    switch (state) {
      case 'АЛДАА ГАРСАН':
      case 'БАНК ТАТГАЛЗСАН':
      case 'ЗАХИРАЛ-БАНК ТАТГАЛЗСАН':
      case 'ДҮН ХҮРЭЭГҮЙ':
        return true;
      default:
        return false;
    }
  }

  checkOnlineLeasingState(state: string): boolean {
    switch (state) {
      case 'АЛДАА ГАРСАН':
      case 'БАНК ТАТГАЛЗСАН':
      case 'ЗАХИРАЛ-БАНК ТАТГАЛЗСАН':
      case 'ДҮН ХҮРЭЭГҮЙ':
      case 'СКОРИНГ ТАТГАЛЗСАН':
      case 'CIB_FAILED':
        return true;
      default:
        return false;
    }
  }

  updateIdTypeToNumber(data) {
    const copyData = data;
    if (data != null) {
      for (let i = 0; i < data.length; i++) {
        if (typeof (data[i].id) !== 'number' && !data[i].id.includes('CHO')) {
          copyData[i].id = parseInt(data[i].id, 10);
        }
      }
    }
    return copyData;
  }

  openDeleteDialog() {
    const buttonClickedRow = this.selectedItem;
    const config = new MatDialogConfig();
    this.dialogData.id = buttonClickedRow.id;
    this.dialogData.state = buttonClickedRow.state;
    config.width = '500px';
    config.data = this.dialogData;
    const dialogRef = this.dialog.open(ConfirmDeleteDialogComponent, config);
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.sb.deleteTableRow(buttonClickedRow.id, buttonClickedRow.instanceId, buttonClickedRow.state).subscribe(() => {
          this.refresh.emit(true);
          this.snackBar.open('Амжилттай устгалаа!', null, {duration: 3000});
        }, error => {
          return throwError(error);
        });
      }
    });
  }

  showSnackBar(state: boolean): boolean {
    if (state) {
      this.snackBar.open('Хүсэлтийг амжилттай шилжүүллээ!', null, {duration: 3000});
      return true;
    }
    this.snackBar.open('ХЭРЭГЛЭГЧИЙН ДУГААР ЭСВЭЛ ХҮСЭЛТИЙН ДУГААР БУРУУ БАЙНА!!', null, {duration: 3000});
    return false;
  }

  isDataEmpty(): boolean {
    return (this.dataSource.data === undefined || this.dataSource.data.length === 0);
  }

  contextMenuOpen($event, row: any): void {
    this.selectRow(row);
    this.actionOnRow.emit({row, $event});
  }

  rowDblClick(row: any) {
    if (this.checkInProgressRequest && row.state === 'ШИНЭ') {
      return;
    }
    if (!this.isClicked && !this.contextmenuIdentifier) {
      this.isClicked = true;
      this.clickOnRow.emit(row);
      this.isClicked = false;
    }
  }

  checkSelectedItem(item: any): string {
    return (item === this.selectedItem) ? 'selectedItemStyle' : '';
  }

  itemAction(row: any): void {
    if (this.selectedItem === row) {
      this.rowDblClick(row);
    }
  }

  selectRow(row: any): void {
    this.selectedItem = row;
  }

  isDate(rowElement) {
    return rowElement instanceof Date;
  }

  useSeparator(title: string): boolean {
    return title === 'amount';
  }

  filter(key: string) {
    this.dataSource.filter = key.trim().toLowerCase();
  }

  toUpperCaseRegister() {
    if (this.data) {
      for (const data of this.data) {
        if (null != data.registerNumber) {
          data.registerNumber = data.registerNumber.toUpperCase();
        }
      }
    }
  }

  compareNumber(a: number, b: number, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

  compareString(a: string, b: string, isAsc: boolean) {
    if (b === null || b === undefined) {
      return 1 * (isAsc ? 1 : -1);
    }
    if (a === null || a === undefined) {
      return -1 * (isAsc ? 1 : -1);
    }
    return a.localeCompare(b) * (isAsc ? 1 : -1);
  }

  sortData() {
    this.dataSource.sortData = (sortData, sort: MatSort) => {
      if (!sort.active || sort.direction === '') {
        return sortData.sort(() => 0);
      }
      return sortData.sort((a: any, b: any) => {
        const property = sort.active;
        const isAsc = sort.direction === 'asc';
        if (typeof a[property] === 'number') {
          return this.compareNumber(a[property], b[property], isAsc);
        }
        return this.compareString(a[property], b[property], isAsc);
      });
    };
  }
}
