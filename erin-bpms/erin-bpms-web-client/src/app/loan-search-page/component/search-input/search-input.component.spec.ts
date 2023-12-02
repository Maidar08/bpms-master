import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SearchInputComponent} from './search-input.component';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {By} from '@angular/platform-browser';
import {TranslateModule} from "@ngx-translate/core";

describe('SearchInputComponent', () => {
  let component: SearchInputComponent;
  let fixture: ComponentFixture<SearchInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SearchInputComponent],
      imports: [
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        FormsModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot()
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should hide icon if *ngIF is false', () => {
    const searchButton = fixture.debugElement.queryAll(By.css('button'));
    const serachIcon = searchButton[0];
    expect(serachIcon.nativeElement.innerText).toBe('search');

    const input = fixture.debugElement.query(By.css('input'));
    input.nativeElement.value = 108;
    fixture.detectChanges();
    const closeButton = fixture.debugElement.queryAll(By.css('button'));
    const closeIcon = closeButton[0];
    expect(closeIcon.nativeElement.innerText).toBe('close');

  });

  it('should call search method when input element has typed', () => {
    const input: HTMLInputElement = fixture.debugElement.query(By.css('#productSearch')).nativeElement;
    spyOn(component.searchStr, 'emit');
    input.value = '5';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(component.searchStr.emit).toHaveBeenCalledWith('5');
    expect(component.error).toBeFalsy();
  });

  it('should clear input value when clear button has clicked', () => {
    const input: HTMLInputElement = fixture.debugElement.query(By.css('#productSearch')).nativeElement;
    input.value = '5';
    component.type = 'loan-request';
    fixture.detectChanges();

    const clearButton = fixture.debugElement.query(By.css('#clearIcon'));
    spyOn(component.searchStr, 'emit');
    clearButton.triggerEventHandler('click', null);
    fixture.detectChanges();

    expect(input.value).toBe('');
    expect(component.searchStr.emit).toHaveBeenCalledWith('');
  });

  it('should call emitSearchValue method when search button has clicked and it is CUSTOMER SEARCH BY REGISTER NUMBER task', () => {
    spyOn(component.customerNumber, 'emit');
    const input: HTMLInputElement = fixture.debugElement.query(By.css('#productSearch')).nativeElement;
    input.value = 'АА12345678';
    component.type = 'search-customer';
    fixture.detectChanges();

    const clearButton = fixture.debugElement.query(By.css('#searchIcon'));
    clearButton.triggerEventHandler('click', null);
    component.error = false;
    fixture.detectChanges();

    expect(component.customerNumber.emit).toHaveBeenCalledWith('АА12345678');
  });

  it('should call emitSearchValue method when enter has pressed on input element and it is CUSTOMER SEARCH BY REGISTER NUMBER task',
    () => {
      spyOn(component.customerNumber, 'emit');
      const input = fixture.debugElement.query(By.css('#productSearch'));
      input.nativeElement.value = 'АА12345678';
      component.type = 'search-customer';
      fixture.detectChanges();

      input.triggerEventHandler('keyup.enter', null);
      component.error = false;
      fixture.detectChanges();

      expect(component.customerNumber.emit).toHaveBeenCalledWith('АА12345678');
    });
});
