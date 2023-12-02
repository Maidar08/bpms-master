import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UpdateTaskFormComponent} from './update-task-form.component';
import {DynamicFieldsComponent} from '../dynamic-fields/dynamic-fields.component';
import {CdkTableModule} from '@angular/cdk/table';
import {MaterialModule} from '../../../material.module';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {HttpClientModule} from '@angular/common/http';
import {Store, StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {CommonService} from '../../../common/service/common.service';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {FormsModel} from '../../../models/app.model';
import {FormRelationService} from '../../services/form-relation.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

const dummyForm: FormsModel[][] = [
  [ {id: 'test1', formFieldValue: {defaultValue: 'test'}, options: [], label: 'test',
    type: 'string', required: false, disabled: false, validations: null},
    {id: 'test2', formFieldValue: {defaultValue: 'test'}, options: [], label: 'test',
      type: 'string', required: false, disabled: false, validations: null},
  ]
];

describe('UpdateTaskFormComponent', () => {
  let component: UpdateTaskFormComponent;
  let fixture: ComponentFixture<UpdateTaskFormComponent>;
  let relationService: FormRelationService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        CdkTableModule,
        MaterialModule,
        FormsModule,
        MatIconModule,
        MatCardModule,
        HttpClientTestingModule,
        HttpClientModule,
        StoreModule.forRoot({}),
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        }),
      ],
      declarations: [
        UpdateTaskFormComponent,
        DynamicFieldsComponent,
      ],

      providers: [
        {provide: CommonService, useClass: CommonService},
        Store, CommonSandboxService, FormRelationService
      ]

    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdateTaskFormComponent);
    component = fixture.componentInstance;
    relationService = TestBed.inject(FormRelationService);
    fixture.detectChanges();
    component.data = dummyForm;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set task item data when data is not null',  () => {
    expect(component.task.definitionKey).toBeNull();
    expect(component.task.name).toBeNull();

    component.taskDefKey = 'defKey';
    component.title = 'title';
    component.data = {};

    component.ngOnInit();
    expect(component.task.definitionKey).toBe('defKey');
    expect(component.task.name).toBe('title');
  });

  it('should call relation service to set form relation',  () => {
    spyOn(relationService, 'setForm').and.callThrough();
    component.formRelationDefKey = 'defKey';
    component.ngOnInit();
    expect(relationService.setForm).toHaveBeenCalled();
  });

  it('should emit cancel emitter when action handler called with cancel action',  () => {
    spyOn(component.cancel, 'emit').and.callThrough();
    component.handleAction({action: 'cancel'});

    expect(component.cancel.emit).toHaveBeenCalled();
  });
});
