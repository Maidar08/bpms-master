import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {ColumnDef} from '../../../models/common.model';
import {MatMenuTrigger} from '@angular/material/menu';
import {MatDialog} from '@angular/material/dialog';
import {OrganizationRequestModel} from '../../../loan-search-page/models/process.model';

@Component({
  selector: 'erin-table',
  template: `
    <div [ngClass]="getTableContainerStyle()">
      <p class="errorMessage">{{errorMessage}}</p>
      <mat-table [dataSource]="dataSource" [ngClass]="getTableStyle()" multiTemplateDataRows>
        <ng-container *ngFor="let column of _COLUMNS" [cdkColumnDef]="column.columnDef">
          <mat-header-cell [ngClass]="column.columnDef" *cdkHeaderCellDef>{{column.headerText}}</mat-header-cell>
          <mat-cell [ngClass]="column.columnDef" *cdkCellDef="let row; let i=index" matTooltip="{{row[column.columnDef]}}">
            <mat-checkbox #value color="primary" *ngIf="hasCheckBox(row, column)"
                          [(ngModel)]="row.checked" [checked]="row.checked"></mat-checkbox>
            <!-- plain text -->
            <span *ngIf="!column.separator && !column.edit && !column.percent" class="plain_text">{{row[column.columnDef]}}</span>

            <!-- plain text with THOUSAND SEPARATOR -->
            <span [thousandSeparator]="row[column.columnDef]"
                  *ngIf="column.separator && !column.edit">{{row[column.columnDef]}}</span>

            <span *ngIf="!column.edit && column.percent && null != row && null != row[column.columnDef]"
                  [showPercentSymbol]="row[column.columnDef]">{{row[column.columnDef]}}</span>

            <!-- THOUSAND SEPARATOR field -->
            <input *ngIf="hasNumberSeparator(row, column)" [thousandSeparator]="row[column.columnDef]"
                   [element]="row"
                   [elementProp]="column.columnDef"
                   [(ngModel)]="row[column.columnDef]"
                   [disabled]="isDisabled(row, column)">

            <!-- INPUT FIELD WITH PERCENT DIRECTORY-->
            <input *ngIf="hasPercentInput(row, column)" [showPercentSymbol]="row[column.columnDef]"
                   [(ngModel)]="row[column.columnDef]"
                   [disabled]="isDisabled(row, column)">

            <!-- TEXT EDITABLE FIELD -->
            <input *ngIf="isRegularInput(row, column)" placeholder="{{getPlaceholder(row)}}"
                   [(ngModel)]="row[column.columnDef]"
                   [disabled]="isDisabled(row, column)">
          </mat-cell>
        </ng-container>

        <ng-container *ngIf="topHeader != null">
          <mat-header-row *matHeaderRowDef="['extra-header']; sticky:true"></mat-header-row>
          <ng-container matColumnDef="extra-header">
            <mat-header-cell *matHeaderCellDef>{{topHeader}}</mat-header-cell>
          </ng-container>
        </ng-container>

        <mat-header-row *matHeaderRowDef="displayedColumns; sticky:true"></mat-header-row>
        <mat-row *matRowDef="let row; columns: displayedColumns; let i=index" (dblclick)="rowDblClick(row, i)"
                 (click)="rowEventListener(row, i)"
                 (contextmenu)="contextMenuOpen($event, row)"
                 [ngClass]="highlightItem(i, row)"></mat-row>
      </mat-table>
      <div class="context-menu"
           [style.left]="contextMenuPosition.x"
           [style.top]="contextMenuPosition.y"
           [matMenuTriggerFor]="contextMenu">
      </div>
      <mat-menu #contextMenu="matMenu">
        <ng-template matMenuContent let-item="item">
          <button mat-menu-item *ngIf="showContextMenuButton()" (click)="clickContextMenu('edit')">
            ЗАСАХ
          </button>
          <button mat-menu-item *ngIf="showContextMenuButton() && isConfirmedOrgRequest()" (click)="clickContextMenu('extend')">
            СУНГАХ
          </button>
          <button mat-menu-item *ngIf="showContextMenuButton()" (click)="clickContextMenu('cancel')">
            ЦУЦЛАХ
          </button>
          <span mat-menu-item *ngIf="!showContextMenuButton() && showContextMenuButton() != null">Бусад салбарыг засах боломжгүй!</span>
        </ng-template>
      </mat-menu>
    </div>
    <p *ngIf="isDataEmpty()" class="mat-title error-table-text">ЖАГСААЛТ ХООСОН БАЙНА</p>
    <pagination *ngIf="hasPagination" [dataSource]="dataSource"></pagination>

  `,
  styleUrls: ['./erin-table.component.scss']
})
export class ErinTableComponent implements OnInit, AfterViewInit {
  constructor(public dialog: MatDialog
  ) {
    localStorage.setItem('tableRow', null);
  }

  @Input() set data(data) {
    const tempFilter = this.dataSource.filter;
    const filterPredicate = this.dataSource.filterPredicate;
    this.dataSource = new MatTableDataSource(data);
    this.dataSource.filterPredicate = filterPredicate;
    this.dataSource.filter = tempFilter;
  }

  _COLUMNS: ColumnDef[];

  @Input() set columns(columns) {
    this._COLUMNS = columns;
    this.displayedColumns = this._COLUMNS.map(c => c.columnDef);
  }

  @ViewChild(MatMenuTrigger, {static: true}) contextMenu: MatMenuTrigger;

  @Input() disableState: boolean;
  @Input() placeholderIds: string[] = [];
  @Input() checkboxIds: string[] = [];
  @Input() style: string[];
  @Input() topHeader: string = null;
  @Input() highlightRow;
  @Input() tableHasScrollX;
  @Input() hasPagination = false;
  @Input() hasContextMenu = false;
  @Input() errorCheckHeaderCol;
  @Input() errorCheckColVal: any[];
  @Input() showEdit;
  @Output() rowDblClickListener = new EventEmitter<any>();
  @Output() rowClickListener = new EventEmitter<any>();
  @Output() rowContextClickListener = new EventEmitter<any>();
  @Input() groupId;

  dataSource = new MatTableDataSource();
  displayedColumns: string[];
  errorMessage: string;
  key;
  selectedItem: any;
  selectedRow: OrganizationRequestModel;
  contextMenuPosition = {x: '0px', y: '0px'};
  private static setStyle(className, css): void {
    const elements = document.getElementsByClassName(className);
    for (let i = 0; i < elements.length; i++) {
      const el = elements.item(i) as HTMLElement;
      el.style.maxWidth = css;
    }
  }

  @Input() setKey(key) {
    if (key != null) {
      this.key = key;
      this.filter(key);
    }
  }

  ngOnInit(): void {
    if (null != this._COLUMNS) {
      this.displayedColumns = this._COLUMNS.map(c => c.columnDef);
    }
  }

  ngAfterViewInit(): void {
    this.setColumnWidth();
  }

  hasCheckBox(row, column: ColumnDef): boolean {
    return column.hasCheckbox && null != row && null != row[column.columnDef];
  }

  hasNumberSeparator(row, column: ColumnDef): boolean {
    return column.edit && column.separator && null != row && null != row[column.columnDef];
  }

  hasPercentInput(row, column: ColumnDef): boolean {
    return column.edit && column.percent && null != row && null != row[column.columnDef];
  }

  isRegularInput(row, column: ColumnDef): boolean {
    return column.type === 'string' && column.edit && null != row && null != row[column.columnDef];
  }

  getPlaceholder(row): string {
    return row.placeholder;
  }

  isDisabled(row, column: ColumnDef): boolean {
    if (row.disabled) {
      const id = row.disabled.find(colId => colId === column.columnDef);
      return !!id;
    }
    return false;
  }

  isDataEmpty(): boolean {
    return (this.dataSource.data === undefined || this.dataSource.data.length === 0);
  }

  rowEventListener(row, rowNumber) {
    localStorage.setItem('tableRow', rowNumber);
    this.selectedItem = row;
    this.rowClickListener.emit(row);
  }

  contextEventListener(row, rowNumber) {
    localStorage.setItem('tableRow', rowNumber);
    this.selectedRow = row;
    this.rowContextClickListener.emit(row);
  }

  contextMenuOpen(event: MouseEvent, row) {
    if (this.hasContextMenu) {
      event.preventDefault();
      this.contextMenuPosition.x = event.clientX + 'px';
      this.contextMenuPosition.y = event.clientY + 'px';
      this.contextMenu.menu.focusFirstItem('mouse');
      this.selectedRow = row;
      this.contextMenu.openMenu();
    }
  }

  rowDblClick(row, rowNumber) {
    localStorage.setItem('tableRow', rowNumber);
    this.rowDblClickListener.emit(row);
  }

  highlightItem(value, row) {
    if (null != row[this.errorCheckHeaderCol]) {
      const match = this.errorCheckColVal.find(val => val === row[this.errorCheckHeaderCol]);
      if (match) {
        return ' error';
      }
    }
    if (this.highlightRow && this.selectedItem === row) {
      return ' active';
    }
  }

  getTableStyle() {
    return this.tableHasScrollX ? 'mat-table-scrollable' : 'mat-table';
  }

  getTableContainerStyle() {
    return this.tableHasScrollX ? 'table-container-scrollable' : 'table-container';
  }

  filter(key: string) {
    this.dataSource.filter = key.trim();
  }

  filterPredicate(property: string): void {
    this.dataSource.filterPredicate = (data, filter) => {
      return data[property].includes(filter);
    };
  }

  private setColumnWidth(): void {
    if (this.style != null) {
      for (const id of this.style) {
        if (id !== '' && id != null) {
          const config = id.split('-');
          if (config.length === 2) {
            ErinTableComponent.setStyle(config[0], config[1]);
          }
        }
      }
    }
  }

  clickContextMenu(actionType) {
    this.rowContextClickListener.emit({row: this.selectedRow, action: actionType});
  }

  showContextMenuButton(): boolean {
    return this.selectedRow.branchId === this.groupId;
  }

  isConfirmedOrgRequest() {
    return this.selectedRow.state === "БАТЛАГДСАН";
  }
}
