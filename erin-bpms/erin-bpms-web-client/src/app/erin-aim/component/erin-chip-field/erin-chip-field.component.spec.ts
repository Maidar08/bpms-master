import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {ErinLoaderComponent} from '../loader/loader.component';
import {ErinChipField} from './erin-chip-field.component';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {Chip} from './chip.model';
import {By} from '@angular/platform-browser';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

describe('ErinChipsComponent', () => {
  let component: ErinChipField;
  let fixture: ComponentFixture<ErinChipField>;

  const white = 'rgb(255, 255, 255)';
  const green = 'rgb(0, 128, 0)';

  const allChips: Chip[] = [
    {
      value: 'Matt Damon',
      color: white,
      textColor: green
    },
    {
      value: 'Matt Damon',
      color: white,
      textColor: white
    },
    {
      value: 'Mark Wahlberg',
      color: green,
      textColor: white
    }
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ErinChipField, ErinLoaderComponent],
      imports: [
        ReactiveFormsModule,
        FormsModule,
        MatAutocompleteModule,
        MatIconModule,
        MatChipsModule,
        MatFormFieldModule,
        MatInputModule,
        MatProgressSpinnerModule,
        BrowserAnimationsModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErinChipField);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      test: new FormControl('')
    });
    component.loading = false;
    component.autocomplete = true;
    component.formType = 'test';
    component.allAutoCompleteOption = allChips;
    fixture.detectChanges();
  });

  afterEach(() => {
    fixture.destroy();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should filter chips based on input', async(() => {
    generateInput('M').then(() => {
      fixture.detectChanges();
      const matOptions = fixture.debugElement.queryAll(By.css('.mat-option'));
      expect(matOptions.length).toBe(2);
    });
  }));

  it('should display the corresponding color on the selected chip', async(() => {
    generateInput('M').then(() => {
      fixture.detectChanges();
      const matOptions = fixture.debugElement.queryAll(By.css('.mat-option-text'));
      const firstOption = matOptions[0];
      firstOption.nativeElement.click();

      fixture.detectChanges();
      const matChip = fixture.debugElement.query(By.css('.mat-chip'));
      expect(matChip).toBeDefined();

      //TODO: fixed style test's not recomended find better way to test.
      const styles = window.getComputedStyle(matChip.nativeElement);
      expect(styles.backgroundColor).toBe(white);
      expect(styles.color).toBe(green);
    });
  }));

  async function generateInput(value: string) {
    let inputElement: HTMLInputElement;

    inputElement = fixture.nativeElement.querySelector('input');
    inputElement.focus();
    inputElement.dispatchEvent(new Event('focusing'));
    inputElement.value = value;
    inputElement.dispatchEvent(new Event('input'));

    fixture.detectChanges();
    return await fixture.whenStable();
  }

  it('should not add user when user has already registered', async(() => {

    const testData = allChips;
    testData[0].disabled = true;

    component.allAutoCompleteOption = testData;
    const initialChipCount = component.chips.length;

    let inputElement = fixture.nativeElement.querySelector('input');
    component.addChip({value: testData[0].value, input: inputElement});
    expect(component.chips.length).toBe(initialChipCount);
  }));

  it('should add user when user has already registered', async(() => {

    const testData = allChips;
    testData[0].disabled = true;

    component.allAutoCompleteOption = testData;
    const initialChipCount = component.chips.length;

    let inputElement = fixture.nativeElement.querySelector('input');
    component.addChip({value: testData[2].value, input: inputElement});
    expect(component.chips.length).toBe(initialChipCount + 1);
  }));
});
