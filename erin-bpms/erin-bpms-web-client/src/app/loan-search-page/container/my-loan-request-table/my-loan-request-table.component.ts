import { Overlay } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ErinLoaderComponent } from '../../../common/erin-loader/erin-loader.component';
import { REQUEST_COLUMNS } from '../../../models/app.model';
import { DialogService } from '../../../services/dialog.service';
import { ResultTableComponent } from '../../component/result-table/result-table.component';
import { LoanSearchPageSandbox } from '../../loan-search-page-sandbox.service';
import { RequestModel } from '../../models/process.model';


@Component({
  template: `
    <div class="mat-typography margin search-height">
      <search-input (searchStr)="search($event)"></search-input>
      <download-excel [searchKey]=this.searchKey [topHeader]="'my-loan-request'" [data]="data" [urlHeader]="urlHeader"></download-excel>
      <date-range-picker (dateRangeChange)="setDateRange($event)" [defaultStart]=this.defaultStart [defaultEnd]=this.defaultEnd></date-range-picker>
      <p class="mat-error mat-caption ">{{errorMessage | translate}}</p>
      <result-table #resultTableComponent
                    [data]="data"
                    [columns]=columns
                    [paginator]="true"
                    [topHeader]="'МИНИЙ ЗЭЭЛИЙН ХҮСЭЛТ'"
                    [hoverHighlight]="true"
                    (clickOnRow)="clickedOnItem($event, false)"
                    (refresh)="getMyLoanRequests(this.defaultStart, this.defaultEnd)"
      ></result-table>
    </div>
  `
})
export class MyLoanRequestTableComponent implements OnInit {
  // FIXME: this is here until search api is complete
  @ViewChild('resultTableComponent', {static: false}) private table: ResultTableComponent;
  columns = REQUEST_COLUMNS;
  data: RequestModel[];
  errorMessage: string;
  searchKey: string;
  urlHeader = 'loan-requests';
  requestType: string;
  allData: RequestModel[];
  start: Date;
  end: Date;
  defaultStart = new Date();
  defaultEnd = new Date();

  constructor(private sb: LoanSearchPageSandbox, private dialogService: DialogService, public overlay: Overlay,
    private activatedRoute: ActivatedRoute) {
    this.dialogService.processDialogSbj.subscribe(() => {
      this.getMyLoanRequests(this.defaultStart, this.defaultEnd);
    });
    this.activatedRoute.data.subscribe(data => {
      if (null != data) {
        if (null != data.urlHeader) {
          this.urlHeader = data.urlHeader;
          this.requestType = data.requestType;
        }
      }
    });
  }

  ngOnInit() {
    // this.setDefaultDate();
    this.getMyLoanRequests(this.defaultStart, this.defaultEnd);
  }

  setDefaultDate(): void {
    const today = new Date();
    today.setMonth(new Date().getMonth() - 1);
    this.defaultStart = today;
  }

  clickedOnItem(model: RequestModel, isReadOnlyReq: boolean) {
    if (model.state === 'ШИНЭ') {
      const overlayRef = this.setOverlay();
      this.sb.startProcess(model.id, isReadOnlyReq, this.requestType).subscribe(() => {
        overlayRef.dispose();
      }, () => {
        overlayRef.dispose();
      });
    } else {
      this.sb.routeToCaseView(model.instanceId, isReadOnlyReq, this.requestType, false ,model.productCode, model.id);
    }
  }

  search(key: string) {
    this.searchKey = key;
    this.table.filter(key);
  }

  getMyLoanRequests(startDate: Date, endDate: Date) {
    const overlayRef = this.setOverlay();
    this.sb.getMyLoanRequests(startDate.toDateString(), endDate.toDateString()).subscribe(res => {
      if (res) {
        this.allData = res;
        this.data = res;
        overlayRef.dispose();
      }
    }, error => {
      overlayRef.dispose();
      this.errorMessage = error.error.message;
    });
  }

  setDateRange(date: Date[]) {
    [this.start, this.end] = date;
    this.getMyLoanRequests(this.start, this.end);
  }
  private setOverlay() {
    const overlayRef = this.overlay.create({
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true
    });
    overlayRef.attach(new ComponentPortal(ErinLoaderComponent));
    return overlayRef;
  }
}


