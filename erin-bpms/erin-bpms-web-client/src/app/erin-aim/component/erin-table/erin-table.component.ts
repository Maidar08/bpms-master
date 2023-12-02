import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';

import {ColumnDef} from './models/erin-table.model';
import {SelectionModel} from '@angular/cdk/collections';
import {UserModel, UserStatus} from '../../user-management/models/user-management.model';

/**
 * TO-DO style
 */
@Component({
  selector: 'erin-table',
  template: `
    <div class="erin-table" [ngClass]="style">
      <mat-table [dataSource]="dataSource" [class.selected-row]="(hoverHighlight)">

        <!--Checkbox-->
        <ng-container *ngIf="select" [cdkColumnDef]="'select'">
          <mat-header-cell *cdkHeaderCellDef class="table-header-cell">
            <mat-checkbox (change)="$event ? masterToggle() : null"
                          [checked]="selection.hasValue() && isAllSelected()"
                          [indeterminate]="selection.hasValue() && !isAllSelected()">
            </mat-checkbox>
          </mat-header-cell>
          <mat-cell *cdkCellDef="let row">
            <mat-checkbox (click)="$event.stopPropagation()"
                          (change)="$event ? selection.toggle(row) : null"
                          [checked]="selection.isSelected(row)">
            </mat-checkbox>
          </mat-cell>
        </ng-container>

        <!--Action buttons-->
        <ng-container *ngIf="action" [cdkColumnDef]="'action'">
          <mat-header-cell *cdkHeaderCellDef class="table-header-cell">
            {{selection.isEmpty() ? 'Үйлдэл' : '' }}
            <!--Bulk action buttons-->
            <div *ngIf="!selection.isEmpty()">
              <button mat-icon-button aria-label="unarchive" color="primary"
                      (click)="onArchive.emit({
                        data: selection.selected,
                        isArchived: true
                      })">
                <mat-icon matTooltip="{{UNARCHIVE}}">unarchive</mat-icon>
              </button>
              <button mat-icon-button aria-label="archive" color="primary"
                      (click)="onArchive.emit({
                        data: selection.selected,
                        isArchived: false
                      })">
                <mat-icon matTooltip="{{ARCHIVE}}">archive</mat-icon>
              </button>
              <button mat-icon-button aria-label="delete" color="primary"
                      [disabled]="!bulkDeletable()"
                      (click)="onDelete.emit(selection.selected)">
                <mat-icon matTooltip="{{bulkDeletable() ? DELETE : INDELIBLE}}">delete</mat-icon>
              </button>
            </div>
          </mat-header-cell>

          <mat-cell *cdkCellDef="let row">
            <button mat-icon-button aria-label="edit" color="primary"
                    [disabled]="row['status'] !== ACTIVE"
                    (click)="onEdit.emit(row)">
              <mat-icon matTooltip="{{EDIT}}">create</mat-icon>
            </button>
            <div *ngIf="selection.isEmpty()">
              <button mat-icon-button aria-label="archive" color="primary"
                      matTooltip="{{row['status'] === ACTIVE ? ARCHIVE : UNARCHIVE}}"
                      (click)="onArchive.emit({
                        data: [row],
                        isArchived: row['status'] !== ACTIVE
                      })">
                <mat-icon *ngIf="row['status'] === ACTIVE">archive</mat-icon>
                <mat-icon *ngIf="row['status'] !== ACTIVE">unarchive</mat-icon>
              </button>
              <button mat-icon-button aria-label="delete" color="primary"
                      [disabled]="row['status'] !== ACTIVE || !row['deletable']"
                      (click)="onDelete.emit([row])">
                <mat-icon matTooltip="{{row['status'] !== ACTIVE || !row['deletable'] ? INDELIBLE : DELETE}}">delete</mat-icon>
              </button>
            </div>
          </mat-cell>
        </ng-container>

        <ng-container *ngFor="let column of columns" [cdkColumnDef]="column.columnDef">
          <mat-header-cell *cdkHeaderCellDef class="table-header-cell">{{column.headerText}}</mat-header-cell>
          <mat-cell *cdkCellDef="let row">
            <span id="table-row-data"
                  [ngStyle]="{fontWeight: select && selection.isSelected(row) ? '500' : 'normal',
                  margin: '0 8px'}">

              <erin-status *ngIf="column.columnDef === 'status'"
                           [content]="row[column.columnDef] === ACTIVE ? 'ИДЭВХИТЭЙ' : 'ИДЭВХГҮЙ'"
                           [isActive]="row[column.columnDef] === ACTIVE"></erin-status>
              {{column.columnDef !== 'status' ? (row[column.columnDef] ? row[column.columnDef] : '-') : ''}}
            </span>
          </mat-cell>
        </ng-container>

        <mat-header-row *matHeaderRowDef="displayedColumns; sticky:true"></mat-header-row>

        <mat-row *matRowDef="let row; columns: displayedColumns;"
                 [ngClass]="checkSelectedItem(row)"></mat-row>

      </mat-table>
      <p *ngIf="isDataEmpty()" class="mat-title error-table-text">ЖАГСААЛТ ХООСОН БАЙНА</p>
    </div>

  `,
  styleUrls: ['./erin-table.component.scss']
})
export class ErinTableComponent implements OnChanges, OnInit {
  @Input() data: UserModel[];
  @Input('defaultPageSize') defaultPageSize;
  @Input() columns: ColumnDef[];
  @Input() paginator;
  @Input() select: boolean;
  @Input() action: boolean;
  @Input() withFilter: boolean;
  @Input() hoverHighlight: boolean;
  @Input() style = '';

  @Output() onEdit = new EventEmitter();
  @Output() onArchive = new EventEmitter();
  @Output() onDelete = new EventEmitter();
  ARCHIVE = 'Архивлах';
  UNARCHIVE = 'Архиваас гаргах';
  EDIT = 'Засварлах';
  DELETE = 'Устгах';
  INDELIBLE = 'Дататай эсвэл архивласан';
  ACTIVE = UserStatus.ACTIVE;
  displayedColumns: string[];
  dataSource = new MatTableDataSource<UserModel>();
  selection = new SelectionModel<UserModel>(true, []);

  ngOnInit(): void {
    this.displayedColumns = this.columns.map(c => c.columnDef);
    this.setupTableColumn();
  }

  ngOnChanges() {
    this.dataSource = new MatTableDataSource<UserModel>(this.data);
  }

  private setupTableColumn() {
    if (this.select) {
      const selectColumn = ['select'];
      this.displayedColumns = selectColumn.concat(this.displayedColumns);
    }
    if (this.action) {
      const actionColumn = ['action'];
      this.displayedColumns = this.displayedColumns.concat(actionColumn);
    }
  }

  public isDataEmpty(): boolean {
    return this.dataSource.data === undefined || this.dataSource.data.length === 0;
  }

  public checkSelectedItem(item: any): string {
    return (this.selection.isSelected(item)) ? 'selectedItemStyle' : '';
  }

  // Checkbox
  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
  }

  unselectAll(): void {
    this.selection.clear();
  }

  bulkDeletable(): boolean {
    return this.selection.selected.filter(user => !user.deletable || user.status !== this.ACTIVE).length === 0;
  }
}
