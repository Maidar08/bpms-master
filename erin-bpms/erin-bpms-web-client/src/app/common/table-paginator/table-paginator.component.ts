import {Component, Input, OnChanges, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material/paginator';

@Component({
  selector: 'pagination',
  template: `
    <div class="paginator-container">
      <span class="spacer"></span>
      <div class="page-select">
        <div class="label mat-typography">Хуудас:</div>
        <mat-form-field class="small-fields">
          <input matInput maxlength="5" type="number" min="1" [value]="pageValue" (keydown.enter)="$event.target.blur()" (blur)="goToPage($event)"
                 id="pageInput">
        </mat-form-field>
        <div class="label mat-typography">/{{totalPageNumber}}</div>
      </div>
      <mat-paginator
        showFirstLastButtons
        [pageSizeOptions]="pageSizeOptions"
        [pageSize]="defaultPageSize"
        (page)="resetPaging()">
      </mat-paginator>
    </div>`,
  styleUrls: ['./table-paginator.component.scss']
})
export class TablePaginatorComponent implements OnChanges {
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @Input('dataSource') dataSource: any;
  @Input('defaultPageSize') defaultPageSize = 15;
  totalPageNumber = 1;
  pageValue = 0;
  pageSizeOptions = [15, 25, 50, 75, 100];

  ngOnChanges() {
    this.setPaginator();
    const totalPageSize = Math.ceil(this.dataSource.data.length / this.defaultPageSize);
    this.totalPageNumber = totalPageSize < 1 ? 1 : totalPageSize;
    this.setFirstPageValue();
  }

  goToPage(event): void {
    const index = Number(event.target.value);
    if (index > 0 && index <= this.totalPageNumber && (index % 1) === 0) {
      this.setPage(index);
    } else {
      this.setPage(this.pageValue);
      event.target.value = this.pageValue.toString();
    }
  }


  resetPaging(): void {
    this.totalPageNumber = this.dataSource.paginator.getNumberOfPages();
    this.setPageValue(this.dataSource.paginator.pageIndex + 1);
  }

  private setFirstPageValue(): void {
    this.setPageValue(this.dataSource.paginator.pageIndex + 1);
  }

  private setPageValue(pageNumber: number): void {
    this.pageValue = pageNumber;
  }

  private setPage(index: number): void {
    this.pageValue = index;
    this.paginator.pageIndex = index - 1;
    this.setPaginator();
  }

  private setPaginator(): void {
    this.dataSource.paginator = this.paginator;
  }
}
