import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {DocumentsModel} from '../../model/task.model';

@Component({
  selector: 'documentation-table',
  template: `
    <table mat-table [dataSource]="dataSource">
      <ng-container matColumnDef="number">
        <th mat-header-cell *matHeaderCellDef> No.</th>
        <td mat-cell *matCellDef="let item; let i=index"> {{get(item, i)}} </td>
      </ng-container>

      <!-- Name Column -->
      <ng-container matColumnDef="fileName">
        <th mat-header-cell *matHeaderCellDef> Баримт бичиг</th>
        <td mat-cell *matCellDef="let item"> {{item.fileName}} </td>
      </ng-container>

      <!-- Type Column -->
      <ng-container matColumnDef="type">
        <th mat-header-cell *matHeaderCellDef> Үндсэн төрөл</th>
        <td mat-cell *matCellDef="let item"> {{item.type}} </td>
      </ng-container>

      <!-- Sub type Column -->
      <ng-container matColumnDef="subType">
        <th mat-header-cell *matHeaderCellDef> Дэд төрөл</th>
        <td mat-cell *matCellDef="let item"> {{item.subType}} </td>
      </ng-container>

      <!-- Action type Column -->
      <ng-container matColumnDef="source">
        <th mat-header-cell *matHeaderCellDef> Үйлдэл</th>
        <td mat-cell *matCellDef="let item"><a (click)="openFile(item)">{{item.source | translate}}</a></td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
      <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
    </table>
    <p *ngIf="isDataEmpty()" class="emptyMessage">ЖАГСААЛТ ХООСОН БАЙНА</p>
  `,
  styleUrls: ['./documentation-table.component.scss']
})
export class DocumentationTableComponent implements OnChanges {
  @Input() documentsData;
  @Output() openFileViewer = new EventEmitter();
  dataSource = new MatTableDataSource();
  displayedColumns: string[] = ['number', 'fileName', 'type', 'subType', 'source'];
  actions: string[] = [];

  constructor() {
  }

  ngOnChanges(): void {
    this.dataSource = new MatTableDataSource(this.documentsData);
  }

  get(item, i) {
    return i + 1;
  }

  isDataEmpty(): boolean {
    return (!this.documentsData || this.documentsData.length === 0);
  }

  openFile(item: DocumentsModel) {
    if (item.source === 'LDMS') {
      window.open(item.reference);
    } else {
      this.openFileViewer.emit(item);
    }
  }
}
