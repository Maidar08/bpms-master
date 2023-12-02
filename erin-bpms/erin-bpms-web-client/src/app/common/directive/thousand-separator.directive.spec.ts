import {ThousandSeparatorDirective} from './thousand-separator.directive';
import {Component, DebugElement} from '@angular/core';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';

@Component({
  template: `
    <input [thousandSeparator]="1010" class="input1" value="1010">
    <input [thousandSeparator]="0" class="input2" value="0">
    <span [thousandSeparator]=1200>1200</span>
  `
})
class DummyComponent {
}

let component: DummyComponent;
let fixture: ComponentFixture<DummyComponent>;

describe('ThousandSeparatorDirective', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ThousandSeparatorDirective, DummyComponent]
    });
    fixture = TestBed.createComponent(DummyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should put thousand separator', () => {
    const inputElement: DebugElement = fixture.debugElement.query(By.css('.input1'));
    const spanElement: DebugElement = fixture.debugElement.query(By.css('span'));
    expect(inputElement.nativeElement.value).toEqual('1,010');
    expect(spanElement.nativeElement.innerText).toEqual('1,200');
  });

  it('should check whether input is numeric or not when key has pressed', () => {
    const inputElement1 = fixture.nativeElement.querySelector('.input1');
    const inputElement2: DebugElement = fixture.debugElement.query(By.css('.input2'));
    fakeTyping('2d5.3.4', inputElement1);
    expect('10,102,534').toEqual(inputElement1.value);
    fakeTyping('2', inputElement2.nativeElement);
    expect('2').toEqual(inputElement2.nativeElement.value);
  });

  function fakeTyping(value: string, inputEl: HTMLInputElement) {
    let result: string = inputEl.value;
    for (const char of value) {
      const eventMock = new KeyboardEvent('keypress', {key: char, bubbles: true, cancelable: true });
      inputEl.dispatchEvent(eventMock);
      if (!eventMock.defaultPrevented) {
        result = result.concat(char);
        inputEl.value = result;
      }
    }
    inputEl.dispatchEvent(new Event('input'));
    fixture.detectChanges();
  }
});
