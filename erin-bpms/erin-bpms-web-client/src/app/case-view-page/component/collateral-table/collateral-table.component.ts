import {Component, EventEmitter, Input, OnChanges, Output, ViewChild} from '@angular/core';
import {ColumnDef} from '../../../models/common.model';
import {MatTableDataSource} from '@angular/material/table';
import {CollateralListModel} from '../../model/task.model';
import {MatMenuTrigger} from '@angular/material/menu';

@Component({
  selector: 'collateral-table',
  template: `

    <div class="table-container">
      <mat-table [dataSource]="dataSource" #table>
        <ng-container *ngFor="let column of columns let i = index" [cdkColumnDef]="column.columnDef">
          <mat-header-cell *cdkHeaderCellDef class="table-header-cell_{{i}}">{{column.headerText}}</mat-header-cell>
          <mat-cell *cdkCellDef="let row; let i=index" class="table-cell" matTooltip="{{row[column.columnDef]}}" #value>
            <mat-checkbox *ngIf="hasCheckbox(column.columnDef)" [(ngModel)]="row.checked" color="primary" [disabled]="completed"></mat-checkbox>
            <span *ngIf="!isDate(row[column.columnDef]) && !useSeparator(column.columnDef)">{{row[column.columnDef]}}</span>
            <span *ngIf="!isDate(row[column.columnDef]) && useSeparator(column.columnDef)" [thousandSeparator]="row[column.columnDef]"></span>
            <span *ngIf="isBoolean(row[column.columnDef])"> {{row[column.columnDef]}}</span>
            <span *ngIf="isDate(row[column.columnDef])">{{row[column.columnDef]}}</span>
            <span *ngIf="isPercent(row[column.columnDef])">{{row[column.columnDef]}}</span>
          </mat-cell>
        </ng-container>
        <mat-header-row *matHeaderRowDef="displayedColumns; sticky:true"></mat-header-row>
        <mat-row *matRowDef="let row ; columns: displayedColumns;" (contextmenu)="contextMenuOpen($event, row)"></mat-row>
      </mat-table>
      <p *ngIf="isDataEmpty()" class="mat-title error-table-text">ЖАГСААЛТ ХООСОН БАЙНА</p>
    </div>
    <div class="context-menu"
         [style.left]="contextMenuPosition.x"
         [style.top]="contextMenuPosition.y"
         [matMenuTriggerFor]="contextMenu">
    </div>
    <div>
      <mat-menu #contextMenu="matMenu" class="collateral-context-menu">
        <ng-template matMenuContent let-item="item">
          <button mat-menu-item (click)="updateCollateral()">
            <mat-icon color="primary">edit</mat-icon>
            Засварлах
          </button>
        </ng-template>
      </mat-menu>
    </div>
  `,
  styleUrls: ['./collateral-table.component.scss']
})
export class CollateralTableComponent implements OnChanges {
  @Input() data: CollateralListModel[];
  @Input() columns: ColumnDef[];
  @Input() completed;
  @Output() update = new EventEmitter<any>();
  @ViewChild(MatMenuTrigger, {static: true}) contextMenu: MatMenuTrigger;
  displayedColumns: string[];
  dataSource = new MatTableDataSource();
  collateralId: string = null;
  collateralType: string = null;
  unselectedRows = [];
  contextMenuPosition = {x: '0px', y: '0px'};

  constructor() {
  }

  ngOnChanges(): void {
    this.displayedColumns = this.columns.map(c => c.columnDef);
    this.dataSource = new MatTableDataSource(this.data);
  }

  hasCheckbox(columnDef): boolean {
    return columnDef === 'checkbox';
  }

  isDate(rowElement: any): boolean {
    return rowElement instanceof Date;
  }

  useSeparator(columnDef: string): boolean {
    return (columnDef === 'amountOfAssessment' || columnDef === 'availableAmount');
  }

  isDataEmpty(): boolean {
    return (this.dataSource.data.length === 0 || false);
  }

  isBoolean(columnDef: any): boolean {
    return columnDef instanceof Boolean;
  }

  filter(key: string): void {
    if (typeof key === 'string') {
      this.dataSource.filter = key.toLowerCase();
    }
  }

  isPercent(columnDef): boolean {
    return columnDef === 'percent';
  }

  contextMenuOpen(event: MouseEvent, row: any): void {
    this.collateralId = null;
    event.preventDefault();
    this.contextMenuPosition.x = event.clientX + 'px';
    this.contextMenuPosition.y = event.clientY + 'px';
    this.contextMenu.menu.focusFirstItem('mouse');
    if (!this.completed) {
      this.collateralId = row.collateralId;
      this.collateralType = row.type;
      this.contextMenu.openMenu();
    }
  }

  updateCollateral(): void {
    this.update.emit({collateralId: this.collateralId, collateralType: this.collateralType});
  }
}

