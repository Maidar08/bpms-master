import { Overlay } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { ErinLoaderComponent } from '../../../common/erin-loader/erin-loader.component';
import { REQUEST_COLUMNS } from '../../../models/app.model';
import { DialogService } from '../../../services/dialog.service';
import { ResultTableComponent } from '../../component/result-table/result-table.component';
import { LoanSearchPageSandbox } from '../../loan-search-page-sandbox.service';
import { RequestModel } from '../../models/process.model';

@Component({
  selector: 'app-all-request-page',
  template: `
    <div class="mat-typography margin search-height">
      <search-input (searchStr)="search($event)"></search-input>
      <download-excel [searchKey]=this.searchKey [topHeader]="'all-request'" [data]="data" [urlHeader]="urlHeader"></download-excel>
      <date-range-picker (dateRangeChange)="setDateRange($event)" [defaultStart]=this.defaultStart [defaultEnd]=this.defaultEnd></date-range-picker>
      <result-table #resultTableComponent
                    [data]="data"
                    [columns]=columns
                    [paginator]="true"
                    [topHeader]="'БҮХ ЗЭЭЛИЙН ХҮСЭЛТ'"
                    [hoverHighlight]="true"
                    [button]="true"
                    [showTransfer]="false"
                    (clickOnRow)="clickedOnItem($event,false)"
                    (refresh)="getRequests(this.defaultStart,this.defaultEnd)"
      ></result-table>
    </div>
  `,
})
export class AllRequestPageComponent implements OnInit {

  @ViewChild('resultTableComponent', {static: false}) private table: ResultTableComponent;
  columns = REQUEST_COLUMNS;
  data: RequestModel[];
  button: boolean;
  searchKey: string;
  urlHeader = 'loan-requests';
  requestType: string;
  allData: RequestModel[];
  startDate: Date;
  endDate: Date;
  defaultStart = new Date();
  defaultEnd = new Date();

  constructor(private sb: LoanSearchPageSandbox, private dialogService: DialogService,
    private snack: MatSnackBar, public overlay: Overlay, private activatedRoute: ActivatedRoute) {
    this.dialogService.processDialogSbj.subscribe(() => {
      this.getRequests(this.defaultStart, this.defaultEnd);
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
    this.getRequests(this.defaultStart, this.defaultEnd);
  }

  setDateRange(date: Date[]) {
    [this.startDate, this.endDate] = date;
    this.getRequests(this.startDate, this.endDate);
  }

  setDefaultDate(): void {
    const startDate = new Date();
    startDate.setMonth(new Date().getMonth() - 1);
    this.defaultStart = startDate;
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
      this.sb.routeToCaseView(model.instanceId, isReadOnlyReq, this.requestType);
    }
  }

  search(key: string) {
    this.searchKey = key;
    this.table.filter(key);
  }

  getRequests(startDate: Date, endDate: Date) {
    const overlayRef = this.setOverlay();
    this.sb.getAllRequests(startDate.toDateString(), endDate.toDateString()).subscribe(res => {
      overlayRef.dispose();
      if (res) {
        this.allData = res;
        this.data = res;
      }
    }, (err) => {
      this.snack.open(err, 'ХААХ', {duration: 3000});
      overlayRef.dispose();
      // this.snack.open('Амжилтгүй!', 'ХААХ', {duration: 3000});
    });
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
