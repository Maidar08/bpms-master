import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DialogData} from '../../../models/common.model';
import {ComponentPortal} from '@angular/cdk/portal';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {Overlay} from '@angular/cdk/overlay';
import {DocumentModel, DocumentTypeModel} from '../../model/task.model';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';

@Component({
  selector: 'upload-file-dialog',
  template: `
    <div class="mat-dialog">
      <div>
        <p class="dialog-title">Баримт бичиг бүрдүүлэх</p>
        <mat-divider></mat-divider>
      </div>
      <mat-error *ngIf="errorMessage"><p>{{this.errorMessage}}</p></mat-error>
      <div mat-dialog-content class="content">
        <simple-dropdown [items]="basicTypes" [label]="'Үндсэн төрөл:'"
                         (selectionChange)="getChild($event)"></simple-dropdown>
        <span class="spacer"></span>
        <simple-dropdown [items]="subTypes" [selected]="selectedSubType"
                         [label]="'Дэд төрөл:'" (selectionChange)="selectedSubType = $event"></simple-dropdown>
        <span class="spacer"></span>
        <button mat-stroked-button color="primary" (click)="fileInput.click()" class="insert-btn">ФАЙЛ ОРУУЛАХ</button>
        <input #fileInput type="file" style="visibility: hidden; width: 0"
               accept=".doc, .docx, .xls, .xlsx, .ppt, .pptx, .pdf, .jpg, .jpeg, .msg, .html, .htm, .mp3"
               (change)="uploadFiles($event)">
      </div>
      <div class="uploaded-file">
        <div *ngIf="isFileNotSelected()" class="emptyMessage" id="emptyMassage">
          <span>ФАЙЛ СОНГООГҮЙ БАЙНА!</span>
        </div>
        <mat-chip-list #matChipList class="margin-top">
          <mat-chip
            *ngFor="let file of documents"
            color="primary"
            [selectable]="selectable"
            [removable]="removable"
            (removed)="remove(file)">
            <p class="file-name">{{file.name}}</p>
            <mat-icon matChipRemove *ngIf="removable" (click)="clearErrorMessage()">cancel</mat-icon>
          </mat-chip>
        </mat-chip-list>
      </div>
      <div mat-dialog-actions>
        <span class="spacer"></span>
        <button mat-flat-button class="save-btn" color="primary" (click)="saveDocuments()"
                [disabled]="isFileNotSelected() || loading">ХАДГАЛАХ
        </button>
        <button mat-stroked-button color="primary" class="close-button" (click)="closeDialog()">ХААХ</button>
      </div>
    </div>
  `,
  styleUrls: ['./upload-file-dialog.component.scss']
})
export class UploadFileDialogComponent implements OnInit {
  basicTypes: DocumentTypeModel[];
  subTypes: DocumentTypeModel[];
  selectedType: DocumentTypeModel;
  selectedSubType: DocumentTypeModel;
  instanceId: string;
  errorMessage: string;
  documents: DocumentModel[] = [];
  selectable: any;
  removable = true;
  loading: boolean;
  fileExtensions = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'pdf', 'jpg', 'jpeg', 'msg', 'html', 'htm', 'mp3'];

  constructor(private sb: CaseViewSandboxService, public dialogRef: MatDialogRef<UploadFileDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public dialogData: DialogData, public overlay: Overlay) {
    this.instanceId = dialogData.data;
  }

  ngOnInit(): void {
    this.getBasicDocumentType();
  }

  getBasicDocumentType() {
    this.sb.getBasicDocumentType().subscribe(res => {
      if (res) {
        this.basicTypes = res;
      }
    }, error => {
      this.errorMessage = error;
      this.dialogRef.close();
    });
  }

  getSubDocumentType(type: DocumentTypeModel) {
    this.errorMessage = '';
    this.sb.getSubDocumentTypes(type.id).subscribe(res => {
      if (res) {
        this.subTypes = res;
      }
    }, error => {
      this.errorMessage = error;
      this.dialogRef.close();
    });
  }

  closeDialog() {
    this.dialogRef.close();
  }

  uploadFiles(event) {
    const files = event.target.files;
    if (files && files.length > 0) {
      for (const file of files) {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
          this.documents.push({name: file.name.toLowerCase(), contentAsBase64: reader.result.toString()});
        };
      }
    }
  }

  remove(doc: DocumentModel): void {
    const index = this.documents.indexOf(doc);
    if (index >= 0) {
      this.documents.splice(index, 1);
    }
  }

  getChild(type: DocumentTypeModel) {
    this.selectedType = type;
    this.getSubDocumentType(this.selectedType);
  }

  isFileNotSelected(): boolean {
    return this.documents.length === 0;
  }

  saveDocuments() {
    const isValid = this.checkFileExtension();
    if (this.selectedSubType && this.selectedType && isValid) {
      const overlayRef = this.setOverlay();
      this.sb.saveDocuments(this.instanceId, this.selectedType.name, this.selectedSubType.name, this.documents).subscribe(() => {
        overlayRef.dispose();
        this.dialogRef.close(true);
      }, error => {
        overlayRef.dispose();
        this.dialogRef.close();
        this.errorMessage = error;
      });
    } else if (!isValid) {
      this.errorMessage = 'Файлын өргөтгөл буруу байна.';
    } else {
      this.errorMessage = 'Файлын төрлийг сонгоно уу!';
    }
  }

  private setOverlay() {
    const overlayRef = this.overlay.create({
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true
    });
    overlayRef.attach(new ComponentPortal(ErinLoaderComponent));
    return overlayRef;
  }

  private checkFileExtension() {
    const filteredDocs = this.documents.filter(file => {
      const ext = file.name.split('.');
      return this.fileExtensions.find(extension => extension === ext[ext.length - 1]);
    });
    return filteredDocs.length === this.documents.length;
  }

  clearErrorMessage() {
    this.errorMessage = '';
  }
}
