import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ScanFingerprintFieldComponent} from './scan-fingerprint-field.component';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MaterialModule} from '../../../material.module';
import {MatIconModule} from '@angular/material/icon';
import {By} from '@angular/platform-browser';

describe('ScanFingerprintFieldComponent', () => {
  let component: ScanFingerprintFieldComponent;
  let fixture: ComponentFixture<ScanFingerprintFieldComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ScanFingerprintFieldComponent],
      imports: [
        MaterialModule,
        MatIconModule,
        MatInputModule,
        MatFormFieldModule,
        MatButtonModule,
        FormsModule,
        ReactiveFormsModule
      ]

    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScanFingerprintFieldComponent);
    component = fixture.componentInstance;
    component.field = {
      id: 'test1', formFieldValue: {defaultValue: null, valueInfo: 'test'},
      label: 'test', type: 'regular', validations: [], options: [], required: false
    };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have label', () => {
    const label = fixture.debugElement.query(By.css('label'));
    expect(label).toBeDefined();
  });

  it('should show fingerprint icon if image is null', () => {
    const icon = fixture.debugElement.query(By.css('.material-icons'));
    const image = fixture.debugElement.query(By.css('img'));
    expect(image).toBeNull();
    expect(icon).toBeDefined();
  });

  it('should show image value', () => {
    component.field.formFieldValue.defaultValue = 'hello';
    fixture.detectChanges();
    const progressBar = fixture.debugElement.query(By.css('.progress-bar'));
    const image = fixture.debugElement.query(By.css('img'));
    const icon = fixture.debugElement.query(By.css('.material-icons'));
    expect(image).toBeTruthy();
    expect(progressBar).toBeFalsy();
    expect(icon).toBeFalsy();
  });
});
