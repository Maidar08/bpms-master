import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UploadFileDialogComponent} from './upload-file-dialog.component';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatDividerModule} from '@angular/material/divider';
import {MatButtonModule} from '@angular/material/button';
import {MatChipsModule} from '@angular/material/chips';
import {TranslateModule} from '@ngx-translate/core';
import {StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {LoanSearchPageSandbox} from '../../../loan-search-page/loan-search-page-sandbox.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {of} from 'rxjs';
import {By} from '@angular/platform-browser';
import {MatIconModule} from '@angular/material/icon';
import {DocumentTypeModel} from '../../model/task.model';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';

describe('UploadFileDialogComponent', () => {
  let component: UploadFileDialogComponent;
  let fixture: ComponentFixture<UploadFileDialogComponent>;
  let sb: CaseViewSandboxService;
  let dialog: MatDialogRef<UploadFileDialogComponent>;
  // tslint:disable-next-line:prefer-const
  let dialogMock: { close: () => void };
  const basicDocTypes = [{id: '1', name: 'Contract', parentId: '12', type: 'type'}];
  const subDocTypes = [{id: '2', name: 'Contract.contract', parentId: '123', type: 'type'}];
  const type: DocumentTypeModel = {
    id: '1',
    name: '',
    parentId: '12'
  };
  // tslint:disable-next-line:prefer-const
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserDynamicTestingModule,
        HttpClientTestingModule,
        MatSnackBarModule,
        MatDividerModule,
        MatButtonModule,
        MatChipsModule,
        MatIconModule,
        TranslateModule.forRoot(),
        StoreModule.forRoot({}),
        RouterModule.forRoot([])
      ],
      declarations: [UploadFileDialogComponent],
      providers: [
        LoanSearchPageSandbox,
        {provide: MAT_DIALOG_DATA, useValue: {}},
        {provide: MatDialogRef, useValue: dialogMock}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadFileDialogComponent);
    sb = TestBed.inject(CaseViewSandboxService);
    component = fixture.componentInstance;
    dialog = component.dialogRef;
    component.basicTypes = basicDocTypes;
    component.subTypes = subDocTypes;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get basic document types', () => {
    spyOn(sb, 'getBasicDocumentType').and.returnValue(of(basicDocTypes));
    component.getBasicDocumentType();
    fixture.detectChanges();
    expect(sb.getBasicDocumentType).toHaveBeenCalled();
    expect(component.basicTypes.length).toEqual(1);
  });

  it('should get sub document types', () => {
    spyOn(sb, 'getSubDocumentTypes').and.returnValue(of(subDocTypes));
    component.getSubDocumentType(type);
    fixture.detectChanges();
    expect(sb.getSubDocumentTypes).toHaveBeenCalledWith('1');
  });

  it('should upload files', () => {
    spyOn(component, 'uploadFiles');
    const uploadButton = fixture.debugElement.query(By.css('.insert-btn'));
    uploadButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(component.uploadFiles).toBeTruthy();
  });

  it('should show empty message if any file is not selected', () => {
    const emptyMessage = fixture.debugElement.query(By.css('.emptyMessage'));
    const selectedDocuments = fixture.debugElement.query(By.css('.mat-chip'));
    fixture.detectChanges();
    expect(selectedDocuments).toBeFalsy();
    expect(emptyMessage).toBeTruthy();
  });


  it('show error message if document type not selected when save file', () => {
    const errorMessage = fixture.debugElement.query(By.css('mat-error p'));
    const saveButton = fixture.debugElement.query(By.css('.save-btn'));
    component.selectedType = null;
    component.selectedSubType = null;
    saveButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(errorMessage).toBeDefined();
  });
});
