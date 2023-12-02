import {Component, Input} from '@angular/core';
import {ComponentPortal} from '@angular/cdk/portal';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {Overlay} from '@angular/cdk/overlay';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {MatTableDataSource} from '@angular/material/table';
import {CommonSandboxService} from '../../../common/common-sandbox.service';

@Component({
  selector: 'download-excel',
  template: `
    <button mat-icon-button class="table_download_button" (click)=downloadExcel()><mat-icon svgIcon="excel"></mat-icon></button>
  `,
  styleUrls: ['./download-excel.component.scss']
})
export class DownloadExcelComponent {

  constructor(private sb: LoanSearchPageSandbox, public overlay: Overlay, private matIconRegistry: MatIconRegistry,
              private domSanitizer: DomSanitizer, private commonService: CommonSandboxService) {
    this.matIconRegistry.addSvgIcon(
      `excel`,
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/custom-icons/excel_icon.svg')
    );
  }
  @Input() searchKey: string;
  @Input() topHeader: string;
  @Input() channel: string;
  @Input() data;
  @Input() urlHeader: string;

  processState = ['ШИНЭ', 'СУДЛАГДАЖ БАЙНА', 'БАТЛАГДСАН', 'ЗАХИРАЛ-БАНК ТАТГАЛЗСАН', 'БАНК ТАТГАЛЗСАН', 'ХАРИЛЦАГЧ ТАТГАЛЗСАН',
    'ДУУССАН', 'БУЦААСАН'];

  private setOverlay() {
    const overlayRef = this.overlay.create({
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true
    });
    overlayRef.attach(new ComponentPortal(ErinLoaderComponent));
    return overlayRef;
  }

  downloadExcel() {
    this.searchKey = this.getSearchKeyValue( this.searchKey );
    this.getExportData().then( (filteredData) => {
      const overlayRef = this.setOverlay();
      this.sb.downloadExcelReport(this.urlHeader, this.topHeader, this.searchKey === '' ? undefined : this.searchKey, filteredData)
        .subscribe(() =>
          overlayRef.dispose(),
        () => {
          overlayRef.dispose();
        });
    } );

  }

  private async getExportData() {
    const tmpData = new MatTableDataSource(this.data);
    if (this.searchKey != null) {
      tmpData.filter = this.searchKey.trim().toLowerCase();
    }
    const filteredData = this.commonService.clone(tmpData.filteredData);

    filteredData.forEach( (req: any) => req.state = this.commonService.translate(req.state) );
    return await filteredData;
  }

  getSearchKeyValue(searchKey: string) {
    if ( searchKey && this.checkSearchKey(searchKey) ) {
      return searchKey.toUpperCase();
    }
    return searchKey;
  }

  checkSearchKey(searchKey: string) {
    const searchKeyToUpperCase = searchKey.toUpperCase();
    return this.processState.includes(searchKeyToUpperCase);
  }
}
