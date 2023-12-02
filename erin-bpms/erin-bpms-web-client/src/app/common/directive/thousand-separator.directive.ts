import {ChangeDetectorRef, Directive, ElementRef, HostListener, Input, OnChanges, OnInit} from '@angular/core';

@Directive({
  selector: '[thousandSeparator]'
})
export class ThousandSeparatorDirective implements OnInit, OnChanges {
  @Input('thousandSeparator') defaultValue;
  @Input() validations;
  @Input() element;
  @Input() elementProp: string;
  @Input() isRoundup = true;
  curElement;
  numberOnly: false;
  regExpNumberFilter = /([0-9])+/;
  constructor(private elementRef: ElementRef, private cdRef: ChangeDetectorRef) {
    this.curElement = this.elementRef.nativeElement;
  }

  ngOnInit() {
    if (this.validations) {
      this.numberOnly = this.validations.find(validation => validation.name === 'numberonly');
    }
    if (this.curElement.tagName !== 'INPUT' && this.isNumber(this.getElementText())) {
      this.curElement.innerHTML = !!this.numberOnly ? this.getElementText() : this.thousandSeparator(this.getElementText());
    }
    if (this.curElement.tagName === 'INPUT' && this.isNumber(this.getInputFieldValue())) {
      let num = this.removeFirstZero(this.getInputFieldValue());
      num = this.formatNumber(num);
      if (this.element) {
        if (this.element.defaultValue != null) {
          this.element.defaultValue = !!this.numberOnly ? num : this.thousandSeparator(num);
        } else if (this.elementProp != null && this.elementProp !== '' && this.element[ this.elementProp ] > 0) {
          this.element[ this.elementProp ] = !!this.numberOnly ? this.formatNumber(num) : this.thousandSeparator(this.formatNumber(num));

        }
        this.cdRef.detectChanges();
      }
      this.curElement.value = !!this.numberOnly ? this.formatNumber(num) : this.thousandSeparator(this.formatNumber(num));
    }
  }
  removeFirstZero(num: string) {
    const zeroState: boolean = num[0] === '0';
    let retVal = num;
    if (zeroState) {
      for (const char of num) {
        retVal = num.substr(num.indexOf(char), num.length);
        if (char !== '0') {
          break;
        }
      }
    }
    return retVal;
  }
  ngOnChanges(): void {
    this.ngOnInit();
  }

  @HostListener('keypress', ['$event']) keyPress(event: KeyboardEvent) {
    this.setValidator(event);
  }

  @HostListener('input', ['$event.target']) onInput(inputElement) {
    this.isStartWithZero(inputElement);
    const num = this.formatNumber(inputElement.value);
    if (!!!this.numberOnly) {
      inputElement.value = this.thousandSeparator(num);
    }
  }

  getInputFieldValue(): string {
    return (this.defaultValue !== undefined) ? this.defaultValue : this.curElement.value;
  }

  getElementText(): string {
    return (this.defaultValue !== undefined) ? this.defaultValue : this.curElement.innerHTML;
  }

  thousandSeparator(num) {
    const useNum = this.isDecimal(String(num));
    return String(useNum).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  }

  formatNumber(input: string) {
    return (String(input).replace(/(,)/g, ''));
  }

  setValidator(event): void {
    const inputValue: string = event.target.value;
    const inputLength: number = inputValue === '0' ? 0 : inputValue.length;
    const decimalLength: number = inputValue.split('.')[1] !== undefined ? inputValue.split('.')[1].length : 0;

    // can not type if decimal place is more than 2
    if (inputLength === 0 && !this.regExpNumberFilter.test(event.key)) {
      event.preventDefault();
      return;
    }
    if (!this.regExpNumberFilter.test(event.key) && event.key !== '.') {
      event.preventDefault();
      return;
    }
    if (!this.isRoundup && decimalLength === 0) {
      return;
    }
    if (decimalLength > 1 && inputLength !== 0) {
      event.preventDefault();
      return;
    }
    if (!this.regExpNumberFilter.test(event.key)) {
      event.preventDefault();
      return;
    }
  }
  isStartWithZero(inputElement): void {
    const value: string = inputElement.value;
    const zeroState: boolean = value[0] === '0';
    if (zeroState) {
      for (const char of value) {
        inputElement.value = value.substr(value.indexOf(char), value.length);
      }
    }
  }
  isDecimal(num: string): string {
    if (this.isRoundup) {
      return Number(num) > 0 ? String(Math.round(Number(num))) : num;
    }
    return num;
  }
  isNumber(value: string) {
    const val = this.formatNumber(value);
    return !isNaN(Number(val)) ;
  }
}
