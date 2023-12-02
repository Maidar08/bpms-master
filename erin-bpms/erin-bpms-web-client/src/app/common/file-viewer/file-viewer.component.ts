import {AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, HostListener, OnInit} from '@angular/core';
import pdfjsLib from 'pdfjs-dist/build/pdf';
import pdfjsWorker from 'pdfjs-dist/build/pdf.worker.entry';
import {CommonSandboxService} from '../common-sandbox.service';
import {ActivatedRoute, Router} from '@angular/router';
import html2pdf from 'html2pdf.js';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {MatIconRegistry} from '@angular/material/icon';
import {DOCX_LOWER_CASE, PDF_LOWER_CASE} from '../../case-view-page/model/common.constants';
import {FileService} from '../service/file-service';
import {Document} from '../../models/app.model';

@Component({
  selector: 'app-file-viewer',
  template: `
    <div class="container">
      <div class="file-actions">
        <button mat-icon-button (click)="close()">
          <mat-icon color="primary">close</mat-icon>
        </button>
        <span id="fileName">{{fileName}}</span>
        <span class="spacer"></span>
        <button mat-button (click)="download()" *ngIf="showRegularDownloadButton()">
          <mat-icon color="primary">get_app</mat-icon>
          <span>Татах</span>
        </button>

        <button mat-button *ngIf="showDownloadButtonWithTypeOption()" [matMenuTriggerFor]="fileMenu">
          <mat-icon color="primary">get_app</mat-icon>
          <span>Татах</span>
        </button>
        <mat-menu #fileMenu="matMenu" class="download-menu">
          <button *ngFor="let file of fileWithDifType" mat-menu-item class="menu-button" (click)="downloadWithExtension(file)">
            <mat-icon svgIcon="{{getIcon(file)}}"></mat-icon>
            <span>{{file.extension.toUpperCase()}}</span>
          </button>
        </mat-menu>

        <button mat-button *ngIf="this.downloadable" (click)="printDocument()">
          <mat-icon color="primary">print</mat-icon>
          <span>Хэвлэх</span>
        </button>
      </div>
      <div class="toggle-button" *ngIf = "this.documents.length > 1">
        <mat-button-toggle-group *ngFor="let doc of documents" appearance="legacy" name="fontStyle" aria-label="Font Style">
          <mat-button-toggle value="bold" [ngClass]=" getToggleBtnStyle(doc)"
                             (click)="changeFile(doc); toggleButton(doc)">
            {{doc.name != null ? doc.name.substring(doc.name.length - 9) : ''}}
          </mat-button-toggle>
        </mat-button-toggle-group>
      </div>
      <div class="canvas-container">
        <div id="htmlDiv" [innerHTML]="htmlFile"></div>
        <canvas #canvas *ngIf="!isHtml" id="the-canvas"></canvas>
        <mat-spinner id="spinner" *ngIf="loading" [mode]="'indeterminate'"></mat-spinner>
      </div>
      <div>
        <iframe #iframeElement id="iframe" [src]="this.iframeURL" style="display: none"></iframe>
      </div>
      <div class="file-navigation">
        <span class="spacer"></span>
        <div class="mat-elevation-z3 navigation-card margin-bottom">
          <button mat-icon-button color="primary" (click)="prev()">
            <mat-icon>arrow_back_ios</mat-icon>
          </button>
          <button mat-icon-button color="primary" (click)="next()">
            <mat-icon>arrow_forward_ios</mat-icon>
          </button>
          <span
            class="pagination margin-left margin-right">Хуудас: <span>{{currentPage}}</span>/<span>{{totalPage}}</span></span>
        </div>
        <span class="spacer"></span>
      </div>
    </div>
  `,
  styleUrls: ['./file-viewer.component.scss']
})
export class FileViewerComponent implements OnInit, AfterViewInit, AfterViewChecked {
  fileWithDifType: any[];
  fileToView: string;
  pdfHtmlData;
  fileName: string;
  source: string;
  downloadable: boolean;
  currentPdf;
  currentPage = 1;
  pageRendering: boolean;
  totalPage = 0;
  pageNumPending = null;
  canvas: any;
  ctx;
  instanceId;
  loading = false;
  isHtml = false;
  htmlFile;
  currentURL: string;
  isReadOnlyReq: boolean;
  base64: string;
  documents: Document[];
  rootUrl: string;
  productCategory: string;
  iframeURL: SafeResourceUrl;
  blobUrl;
  buttonText;

  constructor(
    private sb: CommonSandboxService,
    private fileService: FileService,
    private route: ActivatedRoute,
    private router: Router,
    private matIconRegistry: MatIconRegistry,
    private sanitizer: DomSanitizer,
    private changeDetector: ChangeDetectorRef) {
    this.matIconRegistry.addSvgIcon(`pdf`, this.sanitizer.bypassSecurityTrustResourceUrl('../assets/custom-icons/file-pdf.svg'));
    this.matIconRegistry.addSvgIcon(`docx`, this.sanitizer.bypassSecurityTrustResourceUrl('../assets/custom-icons/file-word.svg'));
    this.fileWithDifType = this.router.getCurrentNavigation().extras.state.type;
    this.documents = this.router.getCurrentNavigation().extras.state.documents;
    if (this.documents == null) {
      this.documents = this.fileWithDifType;
    }

    if (this.documents != null && this.documents.length != null) {
      let doc = this.documents.find(d => d['extension'] === 'pdf');
      if (doc == null) {
        doc = this.documents[0];
      }
      this.base64 = doc.source != null ? doc.source : doc['base64'];
    }
    this.productCategory = this.router.getCurrentNavigation().extras.state.productCategory;
    this.rootUrl = this.router.getCurrentNavigation().extras.state.rootUrl;
  }

  ngAfterViewChecked(): void {
    this.changeDetector.detectChanges();
  }

  @HostListener('window:keydown.control.p', ['$event'])
  printFile(event: KeyboardEvent) {
    event.preventDefault();
    if (this.downloadable) {
      this.printDocument();
    }
  }

  @HostListener('window:keyup', ['$event'])
  keyEvent(event: KeyboardEvent) {

    if (event.code === 'ArrowRight') {
      this.next();
    }

    if (event.code === 'ArrowLeft') {
      this.prev();
    }
  }

  ngOnInit() {
    const data = this.sb.base64ToArrayBuffer(this.base64);
    const blob = new Blob([data], {type: 'application/pdf'});
    this.iframeURL = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));
    this.blobUrl = window.URL.createObjectURL(blob);
  }

  printDocument() {
    const iframe = document.getElementById('iframe') as HTMLIFrameElement;
    iframe.contentWindow.print();
    window.URL.revokeObjectURL(this.blobUrl);
  }

  ngAfterViewInit(): void {
    if (null != this.documents[0]) {
      this.fileName = this.documents[0].name;
    }
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      this.source = params.get('source');
      this.instanceId = params.get('instanceId');
      this.downloadable = params.get('download') === 'true';
      if (this.documents.length === 1 || (this.documents.length === 2 && this.productCategory !=='branch-banking')) {
        this.fileName = params.get('fileName');
      }
      this.loading = false;
      if (null == this.base64) {
        this.getFile(id);
      } else {
        this.fileToView = this.base64;
        this.showFile(this.base64);
      }
    });
    this.currentURL = this.router.url;
    this.toggleButton(this.documents[0]);
  }

  private async getFile(id) {
    this.sb.getFile(id, this.instanceId, this.source).subscribe((res: string) => {
      this.isHtml = false;
      this.loading = false;
      this.fileToView = res;
      if (res.includes('<html>')) {
        this.isHtml = true;
        this.convertHtmlAndShowPdf(res);
      } else {
        this.showFile(res);
      }
    }, () => {
      this.loading = false;
      this.close();
    });
  }

  getIcon(file): string {
    return file.extension;
  }

  convertHtmlAndShowPdf(htmlData): void {
    htmlData = htmlData.split('<div class="noprint">');
    this.htmlFile = this.sanitizer.bypassSecurityTrustHtml(htmlData[0]);
    html2pdf().from(htmlData[0]).set({
      margin: 0.1,
      filename: 'tempPDF' + '.pdf',
      html2canvas: {scale: 2},
      jsPDF: {orientation: 'landscape', unit: 'in', format: 'A4', compressPDF: true}
    }).outputPdf().then(pdf => {
      this.pdfHtmlData = btoa(pdf);
      const pdfData = btoa(pdf);
      this.showFile(pdfData);
    });
  }

  showRegularDownloadButton(): boolean {
    return this.downloadable && !this.isHtml && (null == this.fileWithDifType || this.fileWithDifType.length === 0);
  }

  showDownloadButtonWithTypeOption(): boolean {
    return this.downloadable && !this.isHtml && (null != this.fileWithDifType && this.fileWithDifType.length > 0);
  }

  downloadWithExtension(file): void {
    if (file.extension === PDF_LOWER_CASE) {
      this.getFile(file.id).then(() => this.download(PDF_LOWER_CASE));
    } else if (file.extension === DOCX_LOWER_CASE) {
      this.sb.downloadFile(file.base64, file.mimeType, this.fileName);
    }
  }

  showFile(fileData: string): void {
    this.canvas = document.getElementById('the-canvas');
    const pdfData = atob(fileData);
    pdfjsLib.GlobalWorkerOptions.workerSrc = pdfjsWorker;
    const loadingTask = pdfjsLib.getDocument({data: pdfData});
    loadingTask.promise.then((pdf) => {
      this.currentPdf = pdf;
      this.totalPage = pdf.numPages;
      this.ctx = this.canvas.getContext('2d');
      this.renderPage(this.currentPage);
    }, () => undefined);
  }

  private renderPage(pageNumber: number) {
    this.pageRendering = true;

    this.currentPdf.getPage(pageNumber).then((page) => {
      const viewport = page.getViewport({scale: 1.5});
      this.canvas.height = viewport.height;
      this.canvas.width = viewport.width;

      const renderContext = {
        canvasContext: this.ctx,
        viewport
      };
      const renderTask = page.render(renderContext);

      renderTask.promise.then(() => {
        this.pageRendering = false;
        if (this.pageNumPending !== null) {

          this.renderPage(this.pageNumPending);
          this.pageNumPending = null;
        }
      });
    });
  }

  prev() {
    if (this.currentPage <= 1) {
      return;
    }
    this.currentPage--;
    this.queueRenderPage(this.currentPage);
  }

  next() {
    if (this.currentPage >= this.currentPdf.numPages) {
      return;
    }
    this.currentPage++;
    this.queueRenderPage(this.currentPage);
  }

  queueRenderPage(num) {
    if (this.pageRendering) {
      this.pageNumPending = num;
    } else {
      this.renderPage(num);
    }
  }

  download(type?: string) {
    const dataType = type ? type : PDF_LOWER_CASE;
    let linkSource;
    if (this.fileToView.includes('html')) {
      linkSource = `data:application/pdf;base64,${this.pdfHtmlData}`;
    } else {
      linkSource = `data:application/` + dataType + `;base64,${this.fileToView}`;
    }
    const downloadLink = document.createElement('a');

    downloadLink.href = linkSource;
    downloadLink.download = this.fileName;
    downloadLink.click();
  }

  close() {
    let parentURL = this.sb.getparentUrl();
    this.isReadOnlyReq = this.currentURL.includes('readonly');
    if (this.productCategory != null) {
      parentURL = parentURL + this.productCategory + '/';
    }
    let url = this.rootUrl != null ? this.rootUrl : parentURL + 'case-view/' + this.instanceId;
    if (parentURL.includes('leasing-organization') || parentURL.includes('salary-organization')){
      url = parentURL + 'case-view/' + this.instanceId;
    }
    if (this.productCategory == null || (!this.productCategory.includes('loan-contract') && !this.productCategory.includes('branch-banking') && !this.productCategory.includes('leasing-organization') && !this.productCategory.includes('salary-organization')) ) {
      url = url + '/' + this.isReadOnlyReq;
    }
    this.router.navigateByUrl(url);
  }

  changeFile(document: Document) {
    this.base64 = document.source;
    this.ngAfterViewInit();
    this.ngOnInit();
    this.fileName = document.name;
  }

  toggleButton(document: Document) {
    if (document.name != null) {
      this.buttonText = document.name.substring(document.name.length - 9);
    }
  }

  getToggleBtnStyle(doc) {
    if (doc.name == null) {
      return;
    }
    const buttonText = doc.name.substring(doc.name.length - 9);
    return buttonText === this.buttonText ? 'selected' : '';
  }
}
