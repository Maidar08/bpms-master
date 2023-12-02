import {Component, OnInit, ViewChild} from '@angular/core';
import {LOAN_CONTRACT_COLUMNS} from '../../../models/app.model';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {DialogService} from '../../../services/dialog.service';
import {Overlay} from '@angular/cdk/overlay';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {ContractRequestModel} from '../../models/process.model';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {LOANCONTRACT} from '../../models/process-constants.model';
import {ResultTableComponent} from '../../component/result-table/result-table.component';

@Component({
  selector: 'loan-contract-page',
  template: `
    <div class="mat-typography margin search-height">
      <search-input (searchStr)="search($event)"></search-input>
      <download-excel [searchKey]=this.searchKey [topHeader]="'all-request'" [data]="allData" [urlHeader]="urlHeader"></download-excel>
      <date-range-picker (dateRangeChange)="setDateRange($event)" [defaultStart]=this.defaultStart [defaultEnd]=this.defaultEnd></date-range-picker>
      <result-table #contractTable
                    [data]="allData"
                    [columns]=columns
                    [paginator]="hasPagination"
                    [topHeader]="title"
                    [hoverHighlight]="highlightRow"
                    [processType]="urlHeader"
                    (clickOnRow)="clickedOnRow($event)"
      ></result-table>
    </div>
  `,
  styleUrls: ['./loan-contract-page.component.scss']
})
export class LoanContractPageComponent implements OnInit {

  @ViewChild('contractTable', {static: false}) private table: ResultTableComponent;

  highlightRow = true;
  errorMessage: string;
  title: string;
  urlHeader = 'loan-contract';
  requestType: string;
  topHeader;
  hasPagination = true;
  columns = LOAN_CONTRACT_COLUMNS;
  searchKey: string;
  allData: ContractRequestModel[];
  currentUser: string;
  start: Date;
  end: Date;
  defaultStart: Date;
  defaultEnd = new Date();

  constructor(
    private sb: LoanSearchPageSandbox,
    private dialogService: DialogService,
    public overlay: Overlay,
    private activatedRoute: ActivatedRoute,
    public dialog: MatDialog,
    private commonService: CommonSandboxService
  ) {
    this.dialogService.processDialogSbj.subscribe(() => {
      this.getLoanContractRequests(this.requestType, this.defaultStart, this.defaultEnd);
    });

    this.activatedRoute.data.subscribe(data => {
      if (null != data) {
        if (null != data.title) {
          this.title = data.title;
        }
        if (null != data.topHeader) {
          this.topHeader = data.topHeader;
        }
        if (null != data.urlHeader) {
          this.urlHeader = data.urlHeader;
        }
        if (null != data.requestType) {
          this.requestType = data.requestType;
        }
      }
    });
  }

  ngOnInit(): void {
    this.setDefaultDate();
    this.getLoanContractRequests(this.requestType, this.defaultStart, this.defaultEnd);
  }

  clickedOnRow(model: ContractRequestModel) {
    this.sb.routeToContractCaseView(model.processInstanceId, LOANCONTRACT, this.requestType);
  }

  search(key: string): void {
    this.searchKey = key;
    this.table.filter(key);
  }

  getLoanContractRequests(type: string, startDate: Date, endDate: Date) {
    const overlayRef = this.commonService.setOverlay();
    this.sb.getContractRequests(type, startDate.toDateString(), endDate.toDateString()).subscribe(res => {
      this.allData = res;
      this.allData.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });
  }

  setDateRange(date: Date[]) {
    [this.start, this.end] = date;
    this.getLoanContractRequests(this.requestType, this.start, this.end);
  }

  setDefaultDate(): void {
    const today = new Date();
    today.setMonth(new Date().getMonth() - 1);
    this.defaultStart = today;
  }
}
