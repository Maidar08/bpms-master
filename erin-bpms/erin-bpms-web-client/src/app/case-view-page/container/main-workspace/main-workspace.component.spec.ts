import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CdkTableModule} from '@angular/cdk/table';
import {MaterialModule} from '../../../material.module';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule, NoopAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {StoreModule} from '@ngrx/store';
import {RouterModule} from '@angular/router';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {AIM_CONFIG} from '../../../erin-aim/aim.config.token';
import {By} from '@angular/platform-browser';
import {DebugElement} from '@angular/core';
import {FormGroupModel} from '../../../models/app.model';
import {LoanContractWorkspaceServices} from '../../../loan-contract/container/services/loan-contract-workspace-services';
import {MainWorkspaceComponent} from './main-workspace.component';
import {DynamicFormContainerComponent} from '../../component/dynamic-form-container/dynamic-form-container.component';

const fieldGroup: FormGroupModel = {
  fieldGroups: [], fieldAction: {fieldId: 'test'}, buttons: ['continue']
};

describe('ContractMainWorkspaceComponent', () => {
  let component: MainWorkspaceComponent;
  let fixture: ComponentFixture<MainWorkspaceComponent>;
  const aimConfig = { baseUrl: 'base', tenantId: 'tenantId'};

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        CdkTableModule,
        MaterialModule,
        FormsModule,
        MatIconModule,
        MatCardModule,
        HttpClientModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
        HttpClientTestingModule,
        StoreModule.forRoot({}),
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        }),
      ],
      declarations: [ MainWorkspaceComponent, DynamicFormContainerComponent, ],
      providers: [{ provide: AIM_CONFIG, useValue: aimConfig}, LoanContractWorkspaceServices]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MainWorkspaceComponent);
    component = fixture.componentInstance;
    component.workspaceService = TestBed.get(LoanContractWorkspaceServices);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call work sapce service when button is pressed', () => {
    spyOn(component, 'handleAction').and.callThrough();
    spyOn(component.workspaceService, 'handleAction').and.callThrough();

    component.isDataFull = true;
    fixture.detectChanges();
    const dynamicForm = findComponent(fixture, 'dynamic-form-container').componentInstance;
    dynamicForm.actionEmitter.emit({action: 'continue', data: true});
    fixture.detectChanges();
    expect(component.handleAction).toHaveBeenCalled();
    expect(component.workspaceService.handleAction).toHaveBeenCalled();

  });

  it('should call work space service when field value has changed', () => {
    spyOn(component, 'handleFieldAction').and.callThrough();
    spyOn(component.workspaceService, 'handleFieldAction').and.callThrough();
    component.fieldGroups = fieldGroup;
    component.isDataFull = true;
    fixture.detectChanges();
    const dynamicForm = findComponent(fixture, 'dynamic-form-container').componentInstance;
    dynamicForm.fieldActionListener.emit('fieldId');
    fixture.detectChanges();
    expect(component.handleFieldAction).toHaveBeenCalled();
    expect(component.workspaceService.handleFieldAction).toHaveBeenCalled();

  });

  it('should emit expandWorkspace emitter when tableLength has emitted from child component', () => {
    spyOn(component, 'setWorkspaceStyle').and.callThrough();
    spyOn(component.expandWorkspace, 'emit').and.callThrough();
    component.isDataFull = true;
    fixture.detectChanges();
    const dynamicForm = findComponent(fixture, 'dynamic-form-container').componentInstance;
    dynamicForm.tableLength.emit(1);

    fixture.detectChanges();
    expect(component.setWorkspaceStyle).toHaveBeenCalled();
    expect(component.expandWorkspace.emit).toHaveBeenCalled();
  });
});
export function findComponent<T>(
  fixture: ComponentFixture<T>,
  selector: string,
): DebugElement {
  return fixture.debugElement.query(By.css(selector));
}
