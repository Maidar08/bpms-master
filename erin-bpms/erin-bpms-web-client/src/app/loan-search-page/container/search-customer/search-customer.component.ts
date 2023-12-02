import {Component} from '@angular/core';
import {RequestModel} from '../../models/process.model';
import {REQUEST_COLUMNS} from '../../../models/app.model';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {Overlay} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'search-customer',
  template: `
    <div class="mat-typography margin search-height">
      <search-input  (customerNumber)="search($event)" [type]="'search-customer'"></search-input>
      <download-excel [searchKey]=this.searchKey [topHeader]="'search-customer'" [data]="data" [urlHeader]="urlHeader"></download-excel>
      <result-table #resultTableComponent
                    [data]="data"
                    [columns]=columns
                    [paginator]="true"
                    [topHeader]="'ХАРИЛЦАГЧИЙН ЗЭЭЛИЙН ХҮСЭЛТ РЕГИСТРЭЭР ХАЙХ'"
                    [hoverHighlight]="true"
                    (clickOnRow) = clickedOnItem($event,true)
      ></result-table>
    </div>
  `,
})
export class SearchCustomerComponent {
  data: RequestModel[] = [];
  columns = REQUEST_COLUMNS;
  searchKey: string;
  urlHeader = 'loan-requests';
  requestType: string;

  constructor(private sb: LoanSearchPageSandbox, private overlay: Overlay, private activatedRoute: ActivatedRoute) {
    this.activatedRoute.data.subscribe(data => {
      if (null != data) {
        if (null != data.urlHeader) {
          this.urlHeader = data.urlHeader;
          this.requestType = data.requestType;
        }
      }
    });
  }

  search(value) {
    this.searchKey = value;
    const registerNumber = value.toUpperCase();
    const overlayRef = this.setOverlay();
    overlayRef.attach(new ComponentPortal(ErinLoaderComponent));
    this.sb.getRequestsByCustomerNumber(encodeURI(registerNumber)).subscribe(res => {
      this.data = res;
      overlayRef.dispose();
    });

  }
  private setOverlay() {
    return this.overlay.create({
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
    });
  }

  clickedOnItem(model: RequestModel, isReadOnlyReq: boolean) {
    if (model.state === 'ШИНЭ') {
      const overlayRef = this.setOverlay();
      this.sb.startProcess(model.id, isReadOnlyReq).subscribe(() => {
        overlayRef.dispose();
      }, () => {
        overlayRef.dispose();
      });
    } else {
      if (model.registerNumber === this.searchKey) {
        isReadOnlyReq = true;
      }
      this.sb.routeToCaseView(model.instanceId, isReadOnlyReq);
    }
  }
}
