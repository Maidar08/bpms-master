import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoanContractComponent} from './loan-contract.component';
import {CdkTableModule} from '@angular/cdk/table';
import {MaterialModule} from '../../../material.module';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {HttpClientModule} from '@angular/common/http';
import {DynamicFieldsComponent} from '../dynamic-fields/dynamic-fields.component';
import {ErinDropdownFieldComponent} from '../../../common/erin-fields/erin-dropdown-field/erin-dropdown-field.component';
import {ErinNumberInputFieldComponent} from '../../../common/erin-fields/erin-number-input-field/erin-number-input-field.component';
import {ErinSimpleInputFieldComponent} from '../../../common/erin-fields/erin-simple-input-field/erin-simple-input-field.component';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {ThousandSeparatorDirective} from '../../../common/directive/thousand-separator.directive';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';

describe('LoanContractComponent', () => {
  let component: LoanContractComponent;
  let fixture: ComponentFixture<LoanContractComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        CdkTableModule,
        MaterialModule,
        FormsModule,
        MatIconModule,
        MatCardModule,
        HttpClientModule,
      ],
      declarations: [
        LoanContractComponent,
        DynamicFieldsComponent,
        ErinDropdownFieldComponent,
        ErinNumberInputFieldComponent,
        ErinSimpleInputFieldComponent,
        ErinLoaderComponent,
        ThousandSeparatorDirective
      ]
    }).overrideModule(BrowserDynamicTestingModule, {set: {entryComponents: [ErinLoaderComponent]}})
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoanContractComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should increaseNumber', () => {
    component.increaseNumber(1);
    expect(component.accountForm.length).toBeLessThan(5);
  });

  it('should decreaseNumber', () => {
    component.decreaseNumber(2);
    expect(component.accountForm.length).toEqual(0);
  });

  it('should printContract, saveContract, submitContract', () => {
    component.printContract();
    expect(component.printEmitter).not.toBe(null);
    component.saveContract();
    expect(component.saveEmitter).not.toBe(null);
    component.submitContract();
    expect(component.submitEmitter).not.toBe(null);
  });

  it('should return mapForm', () => {
    expect(component.mapForm()).not.toBe(null);
  });
});
