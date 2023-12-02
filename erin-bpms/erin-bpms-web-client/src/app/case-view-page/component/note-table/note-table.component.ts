import {Component, Input, OnChanges} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';

@Component({
  selector: 'note-table',
  template: `

    <table mat-table [dataSource]="dataSource">
      <ng-container matColumnDef="number">
        <th mat-header-cell *matHeaderCellDef>№</th>
        <td mat-cell *matCellDef="let item; let i=index"> {{i + 1}} </td>
      </ng-container>

      <!-- Note Column -->
      <ng-container matColumnDef="note" class="note-column">
        <th mat-header-cell *matHeaderCellDef> Тэмдэглэл</th>
        <td mat-cell class="note" *matCellDef="let item">
<!--          <span data-toggle="tooltip" title="hello world!"></span>-->
          <span matTooltip="{{ item.isReason === true ? 'Шалтгаан: ' :'Тайлбар: '}} {{item.note}}"
                [matTooltipPosition]="'after'"
                [matTooltipClass]="'tooltip'">{{item.note}}</span>
        </td>
      </ng-container>

      <!-- Username Column -->
      <ng-container matColumnDef="username">
        <th mat-header-cell *matHeaderCellDef> Хэрэглэгчийн нэр</th>
        <td mat-cell *matCellDef="let item"> {{item.username}} </td>
      </ng-container>

      <!-- Date Column -->
      <ng-container matColumnDef="date">
        <th mat-header-cell *matHeaderCellDef> Огноо</th>
        <td mat-cell *matCellDef="let item"> {{item.date}} </td>
      </ng-container>

      <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
      <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
    </table>
    <p *ngIf="isDataEmpty()" class="emptyMessage">ЖАГСААЛТ ХООСОН БАЙНА</p>
  `,
  styleUrls: ['./note-table.component.scss']
})
export class NoteTableComponent implements OnChanges {
  @Input() notesData;
  dataSource = new MatTableDataSource();
  displayedColumns: string[] = ['number', 'note', 'username', 'date'];
  actions: string[] = [];

  constructor() {
  }

  ngOnChanges(): void {
    this.dataSource = new MatTableDataSource(this.notesData);
  }

  isDataEmpty(): boolean {
    return (!this.notesData || this.notesData.length === 0);
  }
}
