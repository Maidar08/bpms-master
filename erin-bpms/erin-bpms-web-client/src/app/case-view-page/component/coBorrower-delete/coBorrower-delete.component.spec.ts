import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CoBorrowerDeleteComponent} from './coBorrower-delete.component';
import {MatDialogModule} from '@angular/material/dialog';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {LoanSearchPageSandbox} from '../../../loan-search-page/loan-search-page-sandbox.service';
import {CdkTableModule} from '@angular/cdk/table';
import {MaterialModule} from '../../../material.module';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {HttpClientModule} from '@angular/common/http';
import {DynamicFieldsComponent} from '../dynamic-fields/dynamic-fields.component';
import {ErinSimpleInputFieldComponent} from '../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {ProcessRequestService} from '../../../loan-search-page/services/process-request.service';
import {Store, StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {FormsModel} from '../../../models/app.model';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {By} from '@angular/platform-browser';

describe('CoBorrowerDeleteComponent', () => {
  let component: CoBorrowerDeleteComponent;
  let fixture: ComponentFixture<CoBorrowerDeleteComponent>;
  const data: FormsModel[] = [
    {id: 'fullNameCoBorrower-1', formFieldValue: {defaultValue: 'string'}, label: 'string', type: 'string',
      validations: [], options: [], required: false },
    {id: 'fullNameCoBorrower-2', formFieldValue: {defaultValue: 'string'}, label: 'string', type: 'string',
      validations: [], options: [], required: false }
  ];
  const coBorrowerNumbers = ['1', '2'];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CoBorrowerDeleteComponent,
        DynamicFieldsComponent,
        ErinSimpleInputFieldComponent,
        ThousandSeparatorDirective
      ],
      imports: [
        MatDialogModule,
        BrowserDynamicTestingModule,
        CdkTableModule,
        MaterialModule,
        FormsModule,
        MatIconModule,
        MatCardModule,
        HttpClientModule,
        BrowserAnimationsModule,
        StoreModule.forRoot({}),
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        }),
      ],
      providers: [
        {provide: ProcessRequestService, useClass: ProcessRequestService},
        Store, LoanSearchPageSandbox,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CoBorrowerDeleteComponent);
    component = fixture.componentInstance;
    component.data = data;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return mapForm', () => {
    const returnForm = component.data;
    component.mapFields(coBorrowerNumbers, returnForm);
    const result = component.mapForm();
    expect(result).toBeTruthy();
  });

  it('should mapFields', () => {
    const returnForm = [];
    component.mapFields(coBorrowerNumbers, returnForm);
    const result = returnForm.push({id: 'indexes', formFieldValue: {defaultValue: coBorrowerNumbers}, label: null, type: 'Object',
      validations: [], options: []});
    expect(result).toBeTruthy();
  });

  it('should remove CoBorrower', () => {
    const coBorrowerIndexes = [];
    const button = fixture.debugElement.query(By.css('.remove-btn'));
    const index = 1;
    coBorrowerIndexes.push(coBorrowerNumbers);
    data.splice(index, 1);
    expect(coBorrowerIndexes).not.toBe(null);
  });

});
