import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatSelectModule} from '@angular/material/select';
import {MatOptionModule} from '@angular/material/core';
import {MaterialModule} from '../../../material.module';
import {FormsModule} from '@angular/forms';
import {DynamicFieldsComponent} from './dynamic-fields.component';
import {By} from '@angular/platform-browser';
import {ErinNumberInputFieldComponent} from '../../../common/erin-fields/erin-number-input-field/erin-number-input-field.component';
import {ErinSimpleInputFieldComponent} from '../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ErinDropdownFieldComponent} from '../../../common/erin-fields/erin-dropdown-field/erin-dropdown-field.component';
import {ErinDatepickerFieldComponent} from '../../../common/erin-fields/erin-datepicker-field/erin-datepicker-field.component';
import {FormsModel} from '../../../models/app.model';
import {ScanFingerprintFieldComponent} from '../../../common/erin-fields/scan-fingerprint-field/scan-fingerprint-field.component';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {ErinPhoneNumberInputFieldComponent} from '../../../common/erin-fields/erin-phone-number-input-field/erin-phone-number-input-field.component';
import {ErinEmailFieldComponent} from '../../../common/erin-fields/erin-email-input-field/erin-email-field.component';

describe('DynamicFieldsComponent', () => {
  let component: DynamicFieldsComponent;
  let fixture: ComponentFixture<DynamicFieldsComponent>;
  const formsModel: FormsModel[] = [
    {
      id: 'test1', formFieldValue: {defaultValue: 'test', valueInfo: 'test'},
      label: 'test', type: 'regular', validations: [], options: [], required: false
    },
    {
      id: 'test2', formFieldValue: {defaultValue: new Date('2020-12-19'), valueInfo: 'test'},
      label: 'test', type: 'date', validations: [], options: [], required: false
    },
    {
      id: 'test3', formFieldValue: {defaultValue: 'test', valueInfo: 'test'},
      label: 'test', type: 'regular', validations: [], options: [{id: 'option1', value: 'test'}], required: false
    },
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatFormFieldModule,
        MatInputModule,
        MatDatepickerModule,
        MatSelectModule,
        MatOptionModule,
        MaterialModule,
        FormsModule,

      ],
      declarations: [
        DynamicFieldsComponent,
        ErinNumberInputFieldComponent,
        ErinPhoneNumberInputFieldComponent,
        ErinEmailFieldComponent,
        ErinSimpleInputFieldComponent,
        ErinDropdownFieldComponent,
        ErinDatepickerFieldComponent,
        ScanFingerprintFieldComponent,
        ThousandSeparatorDirective
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DynamicFieldsComponent);
    component = fixture.componentInstance;
    component.forms = formsModel;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have initial state', () => {
    expect(fixture.debugElement.queryAll(By.css('.form-field-container'))).not.toBeNull();
    expect(fixture.debugElement.queryAll(By.css('.form-field'))).not.toBeNull();
  });

  it('should have option field', () => {
    expect(component.forms.length).toEqual(3);
    expect(component.forms[0].type).not.toBe('date');
    expect(component.forms[0].options.length).toEqual(0);
  });

  it('should have date field', () => {
    expect(component.forms[1].type).toBe('date');
    expect(component.forms[1].options.length).toEqual(0);
  });

  it('should have simple field', () => {
    expect(component.forms[2].type).toBe('regular');
    expect(component.forms[2].options.length).not.toEqual(0);
  });
});

