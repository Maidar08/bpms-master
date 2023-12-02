import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ComponentPortal} from '@angular/cdk/portal';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {Overlay, OverlayRef} from '@angular/cdk/overlay';
import {MatDialog} from '@angular/material/dialog';
import {UploadFileDialogComponent} from '../upload-file-dialog/upload-file-dialog.component';
import {Router} from '@angular/router';
import {DocumentsModel, NoteModel} from '../../model/task.model';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {LoanSearchPageSandbox} from '../../../loan-search-page/loan-search-page-sandbox.service';
import {
  CASE_FOLDER_TITLE_ENTITY_MAP,
  CASE_FOLDER_TITLES_MAP,
  DOCUMENT_TITLE,
  LOAN_CONTRACT,
  LOAN_CONTRACT_CAMEL_CASE,
  NOTE_TITLE
} from '../../model/case-folder-constants';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {PDF_LOWER_CASE} from '../../model/common.constants';
import {forkJoin, from, Observable} from 'rxjs';


@Component({
  selector: 'case-folder',
  template: `
    <div class="detail-container">
      <div class="title-container">
        <mat-card *ngFor="let title of titles; let i = index" class="trapeze" id="title_id-{{i}}"
                  (click)="clickOnTitle(title)" [ngClass]="getStyle(title)">
          <span class="folder-title">{{title}}</span>
        </mat-card>
      </div>
      <mat-card class="request-detail-card" [ngClass]="{'full-container': stretch}">
        <div class="icon-container" *ngIf="isDocuments()">
          <button mat-icon-button [matMenuTriggerFor]="fileMenu" *ngIf="!isReadOnlyRequestInfo()">
            <mat-icon>more_vert</mat-icon>
          </button>
          <mat-menu #fileMenu="matMenu">
            <button mat-menu-item class="collect-files" (click)=uploadFile()><span>Бүрдүүлэх</span></button>
          </mat-menu>
        </div>
        <div *ngIf="!isDocuments() && !isNoteTable()" class="dynamic-field-container">
          <ng-container *ngFor="let form of formContainer" class="form-container">
            <div class="form-title">
              <span>{{form[0].context | translate}}</span>
              <hr class="line separator-line-style">
            </div>
            <dynamic-fields [forms]="form" *ngIf="!isDocuments()" [inputState]="true"></dynamic-fields>
          </ng-container>
        </div>
        <ng-container *ngIf="isDocuments()" class="documents-detail-card">
          <documentation-table
            [documentsData]="documentsData"
            (openFileViewer)="openFileViewer($event)">
          </documentation-table>
        </ng-container>
        <ng-container *ngIf="isNoteTable()" class="note-detail-card">
          <note-table [notesData]="notesData"></note-table>
        </ng-container>
      </mat-card>
    </div>
  `,
  styleUrls: ['./case-folder.component.scss']
})
export class CaseFolderComponent implements OnChanges {

  constructor(
    private sb: CaseViewSandboxService,
    private loanRequestSb: LoanSearchPageSandbox,
    private commonService: CommonSandboxService,
    private overlay: Overlay,
    private router: Router,
    public dialog: MatDialog) {
    this.currentURL = this.router.url;
  }

  @Input() instanceId: string;
  @Input() currentURL: string;
  @Input() stretch: boolean;
  formContainer = [];
  titles: string[];
  titleState: string;
  subtitle;
  documentsData: DocumentsModel[];
  documentsDataBackup: DocumentsModel[];
  notesData: NoteModel[];
  showFolder = true;
  productCategory: string;
  LDMS = 'LDMS';

  private static sameDocument(doc1: DocumentsModel, doc2: DocumentsModel, checkID: boolean): boolean {
    if (checkID) {
      return doc1.fileId !== doc2.fileId && doc1.fileName === doc2.fileName &&
        doc1.type === doc2.type && doc1.subType === doc2.subType && doc1.source === doc2.source;
    }
    return doc1.fileName === doc2.fileName && doc1.type === doc2.type &&
      doc1.subType === doc2.subType && doc1.source === doc2.source;
  }

  private static setIndex(res) {
    if (null == res) {
      return res;
    }
    for (let i = 0; i < res.length; i++) {
      res[i].index = (i + 1);
    }
    return res;
  }

  ngOnChanges(simpleChanges: SimpleChanges): void {
    if (null != simpleChanges.instanceId) {
      this.setFolderTitles();
    }
    const title = sessionStorage.getItem('title');
    if (title != null && title !== 'undefined' && JSON.parse(title) !== null) {
      this.titleState = JSON.parse(title);
      sessionStorage.removeItem('title');
    }
    for (const prop in simpleChanges) {
      if (prop === 'instanceId') {
        this.getVariables();
      }
    }
  }

  clickOnTitle(title: string) {
    this.titleState = title;
    this.getVariables();
  }

  getStyle(title: string): string {
    return title === this.titleState ? 'clicked' : '';
  }

  getVariables() {
    const overlayRef = this.setOverlay();

    if (this.titleState === DOCUMENT_TITLE) {
      this.getDocuments(overlayRef);
    } else if (this.titleState === NOTE_TITLE) {
      const titleEntity = CASE_FOLDER_TITLE_ENTITY_MAP.get(this.titleState);
      this.getNotes(titleEntity, overlayRef);
    } else {
      const titleEntity = CASE_FOLDER_TITLE_ENTITY_MAP.get(this.titleState);
      this.customerVariables(titleEntity, overlayRef);
    }

    sessionStorage.removeItem('title');
    sessionStorage.setItem('title', JSON.stringify(this.titleState));
  }

  customerVariables(type, overlayRef: OverlayRef) {
    this.sb.getCustomerVariables(this.instanceId, type).subscribe(res => {
      this.handleFormResponse(res);
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });
  }

  isDocuments() {
    return (this.titleState === DOCUMENT_TITLE);
  }

  isNoteTable() {
    return (this.titleState === NOTE_TITLE);
  }

  isReadOnlyRequestInfo(): boolean {
    return (this.currentURL.includes('true'));
  }

  uploadFile() {
    const dialogRef = this.dialog.open(UploadFileDialogComponent, {data: {data: this.instanceId}, width: '600px'});
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        const overlayRef = this.setOverlay();
        this.getDocuments(overlayRef);
      }
    });
  }

  private setProductCategory(request): void {
    const productCategory = request['productCategory'];
    if (productCategory != null && productCategory.includes(LOAN_CONTRACT_CAMEL_CASE)) {
      this.productCategory = LOAN_CONTRACT;
    } else {
      this.productCategory = null;
    }
  }

  private setFolderTitles(): void {
    this.loanRequestSb.getRequestByInstanceId(this.instanceId).subscribe(request => {
      if (null != request['productCategory']) {
        this.titles = CASE_FOLDER_TITLES_MAP.get(request['productCategory']);
        this.setProductCategory(request);
        if (this.titles) {
          this.clickOnTitle(this.titles[0]);
          this.showFolder = false;
        }
      }
    });
  }

  private getDocuments(overlayRef?: OverlayRef) {
    this.sb.getDocuments(this.instanceId).subscribe(res => {
      overlayRef.dispose();
      if (res) {
        const docList = CaseFolderComponent.setIndex(res);
        this.documentsDataBackup = docList;
        this.filterDocument(docList);
      }
    }, () => {
      this.dialog.closeAll();
      overlayRef.dispose();
    });
  }

  private filterDocument(res): void {
    const tmpDocList = [];
    const observables: Observable<any>[] = [];
    if (res.length > 0) {
      const overlay = this.commonService.setOverlay();
      for (const doc of res) {
        observables.push(from(this.checkExtension(doc).then((data: any) => {
          if (null == data && doc.source !== this.LDMS) {
            overlay.dispose();
          } else if (data || doc.source === this.LDMS || !this.findDuplicatedFile(res, doc)) {
            tmpDocList.push(doc);
          }
        })));
      }

      forkJoin(...observables).subscribe(() => {
        if (null == this.documentsData || tmpDocList.length !== this.documentsData.length) {
          this.commonService.sortArray(tmpDocList, 'index', 'number');
          this.documentsData = tmpDocList;
        }
        overlay.dispose();
      });
    } else {
      this.documentsData = tmpDocList;
    }
  }

  private checkExtension(doc) {
    return new Promise(resolve => {
      this.commonService.getFile(doc.fileId, this.instanceId, doc.source).subscribe(file => {
        if (null != file) {
          this.commonService.getFileExtension(file.toString()).then(type => {
            if (type != null) {
              resolve(type.ext === PDF_LOWER_CASE);
            } else {
              resolve(false);
            }
          });
        } else {
          resolve(false);
        }
      }, () => {
        resolve(null);
      });
    });
  }

  private findDuplicatedFile(docList: DocumentsModel[], doc: DocumentsModel): boolean {
    for (const file of docList) {
      if (CaseFolderComponent.sameDocument(doc, file, true)) {
        return true;
      }
    }
    return false;
  }

  private getNotes(type, overlayRef?: OverlayRef) {
    this.sb.getNotes(this.instanceId, type).subscribe(res => {
      overlayRef.dispose();
      this.notesData = res;
    }, () => {
      overlayRef.dispose();
    });
  }

  private setOverlay() {
    const overlayRef = this.overlay.create({
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
    });
    overlayRef.attach(new ComponentPortal(ErinLoaderComponent));
    return overlayRef;
  }

  private handleFormResponse(res) {
    this.formContainer = [];
    this.subtitle = new Set();
    for (const item of res) {
      this.subtitle.add(item.context);
    }
    this.formContainer = [];
    let forms = [];
    for (const title of this.subtitle) {
      for (const item of res) {
        if (item.context === title) {
          forms.push(item);
        }
      }
      this.formContainer.push(forms);
      forms = [];
    }
  }

  async openFileViewer(doc: DocumentsModel) {
    const overlayRef = this.setOverlay();
    const parentURL = this.sb.getParentUrl();
    const enabledReq = this.currentURL.includes('true') ? 'readonly' : 'common';
    const files = await this.getFilesWithDifType(doc, overlayRef);
    overlayRef.dispose();
    if (files.length === 0) {
      this.commonService.showSnackBar('Файл татахад алдаа гарлаа!', 'ХААХ', 3000);
      return;
    }
    if(doc.fileName === 'Бизнесийн зээлийн тооцоолол'){
      doc.downloadable = true;
    }
    this.router.navigate([parentURL + 'file-viewer/' + this.instanceId + '/'
      + doc.fileId + '/' + doc.source + '/' + doc.downloadable + '/' + doc.fileName + '/' + enabledReq],
      {state: {type: files, documents: files, productCategory: this.productCategory}});
  }

  private async getFilesWithDifType(doc, overlayRef) {
    const fileList = [];
    const promises = [];
    for (const document of this.documentsDataBackup) {
      if (CaseFolderComponent.sameDocument(doc, document, false)) {
        promises.push(new Promise(resolve => {
          this.getFileExtension(document, overlayRef).then(res => {
            // tslint:disable-next-line:no-unused-expression
            res != null ? resolve(res['file']) : resolve(null);
          });
        }));

      }
    }
    await Promise.all(promises).then(result => {
      if (result != null) {
        result.forEach(res => {
          if (res != null) {
            fileList.push(res);
          }
        });
      }
    });
    return fileList;
  }

  private async getFileExtension(doc, overlayRef) {
    return new Promise(resolve => {
      this.commonService.getFile(doc.fileId, this.instanceId, doc.source).subscribe(res => {
        if (null != res) {
          this.commonService.getFileExtension(res.toString()).then(type => {
            if (null == type || type.ext == null) {
              this.commonService.showSnackBar('Файл алдаатай байна!', 'ХААХ', null);
              overlayRef.dispose();
              resolve(null);
              return;
            }
            resolve({file: {id: doc.fileId, extension: type.ext, base64: res.toString(), mimeType: type.mime}});
          });
        } else {
          this.commonService.showSnackBar('Файл татахад алдаа гарлаа!', 'ХААХ', null);
          overlayRef.dispose();
          resolve(null);
        }
      });
    });
  }
}
