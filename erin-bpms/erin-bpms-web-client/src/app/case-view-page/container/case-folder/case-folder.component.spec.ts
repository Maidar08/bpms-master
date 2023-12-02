import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CaseFolderComponent} from './case-folder.component';
import {MaterialModule} from '../../../material.module';
import {DynamicFieldsComponent} from '../../component/dynamic-fields/dynamic-fields.component';
import {MatMenuModule} from '@angular/material/menu';
import {ErinSimpleInputFieldComponent} from '../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ErinNumberInputFieldComponent} from '../../../common/erin-fields/erin-number-input-field/erin-number-input-field.component';
import {ErinDropdownFieldComponent} from '../../../common/erin-fields/erin-dropdown-field/erin-dropdown-field.component';
import {ErinDatepickerFieldComponent} from '../../../common/erin-fields/erin-datepicker-field/erin-datepicker-field.component';
import {Store, StoreModule} from '@ngrx/store';
import {LoanSearchPageSandbox} from '../../../loan-search-page/loan-search-page-sandbox.service';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {TranslateModule} from '@ngx-translate/core';
import {By} from '@angular/platform-browser';
import {of} from 'rxjs';
import {DebugElement} from '@angular/core';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {OverlayModule} from '@angular/cdk/overlay';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {FormsModel} from '../../../models/app.model';
import {ScanFingerprintFieldComponent} from '../../../common/erin-fields/scan-fingerprint-field/scan-fingerprint-field.component';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {MatIconModule} from '@angular/material/icon';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';
import {NoteModel} from '../../model/task.model';

describe('CaseFolderComponent', () => {
  let component: CaseFolderComponent;
  let fixture: ComponentFixture<CaseFolderComponent>;
  let sb: CaseViewSandboxService;
  let spy;
  // tslint:disable-next-line:prefer-const
  let dummyData: NoteModel[] = [
    {note: 'note',
      username: 'username',
      date: new Date(),
      isReason: true}
  ];
  const model: FormsModel[] = [
    {
      id: '1', formFieldValue: {defaultValue: 'test', valueInfo: null},
      disabled: false, options: [], validations: null, type: 'notDate', label: 'test', required: false
    },
    {
      id: '2', formFieldValue: {defaultValue: 123, valueInfo: null},
      disabled: false, options: [], validations: null, type: 'BigDecimal', label: 'test', required: false
    },
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        MaterialModule,
        MatMenuModule,
        MatIconModule,
        OverlayModule,
        StoreModule.forRoot({}),
        HttpClientModule,
        FormsModule,
        RouterModule.forRoot([]),
        TranslateModule.forRoot()
      ],
      declarations: [
        CaseFolderComponent,
        DynamicFieldsComponent,
        ErinSimpleInputFieldComponent,
        ErinNumberInputFieldComponent,
        ErinDropdownFieldComponent,
        ErinDatepickerFieldComponent,
        ErinLoaderComponent,
        ScanFingerprintFieldComponent,
        ThousandSeparatorDirective
      ],
      providers: [Store, LoanSearchPageSandbox],
    }).overrideModule(BrowserDynamicTestingModule, {set: {entryComponents: [ErinLoaderComponent]}})
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CaseFolderComponent);
    component = fixture.componentInstance;
    sb = TestBed.inject(CaseViewSandboxService);
    // tslint:disable-next-line:prefer-const
    component.notesData = dummyData;
    spy = spyOn(sb, 'getCustomerVariables');
    spy.and.returnValue(of(model));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have initial state', () => {
    expect(component.titles).not.toEqual([]);
    expect(component.clickOnTitle).toBeTruthy();
    const titleContainer = fixture.debugElement.query(By.css('.title-container'));
    titleContainer.triggerEventHandler('click', null);
    expect(titleContainer).not.toBeNull();
  });

  it('should call functions when title of folder is clicked', () => {
    const folderIndex: DebugElement = fixture.debugElement.query(By.css('#title_id-0'));
    folderIndex.triggerEventHandler('click', null);
    expect(component.customerVariables('main-info', null)).not.toBeNull();
  });

  it('should show table when document title is clicked', () => {
    const folderIndex: DebugElement = fixture.debugElement.query(By.css('#title_id-2'));
    folderIndex.triggerEventHandler('click', null);
    expect(component).toBeTruthy();
    expect(component.isDocuments()).toBeTruthy();
  });

  it('should show table when notes folder is clicked', () => {
    const notesTable = fixture.debugElement.query(By.css('#title_id-5'));
    // tslint:disable-next-line:no-shadowed-variable
    const spy = spyOn(sb, 'getNotes');
    spy.and.returnValue(of(component.notesData));
    notesTable.triggerEventHandler('click', null);
    expect(component).toBeTruthy();
    expect(component.isNoteTable()).toBeTruthy();
    expect(sb.getNotes).toHaveBeenCalled();
    });

  function openOptions() {
    const optionButton = fixture.debugElement.query(By.css('.mat-icon-button'));
    optionButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    const editButton = fixture.debugElement.query(By.css('.edit-button'));
    editButton.triggerEventHandler('click', null);
    fixture.detectChanges();
  }
});
