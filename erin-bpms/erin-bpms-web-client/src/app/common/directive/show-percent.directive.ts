import {ChangeDetectorRef, Directive, ElementRef, HostListener, Input, OnChanges, OnInit} from '@angular/core';

@Directive({
  selector: '[showPercentSymbol]'
})
export class ShowPercentDirective implements OnInit, OnChanges {
  @Input('showPercentSymbol') defaultValue;
  @Input() validations;
  @Input() element;
  currentElement;
  numberOnly: false;

  constructor( private elementRef: ElementRef, private  cdRef: ChangeDetectorRef) {
    this.currentElement = this.elementRef.nativeElement;
  }

  ngOnInit() {
    if (this.validations) {
      this.numberOnly = this.validations.find(validation => validation.name === 'numberonly');
    }
    if (this.currentElement.tagName !== 'INPUT' && this.isNumber(this.getElementText())) {
      this.currentElement.innerHTML = !!this.numberOnly ? this.getElementText() : this.showPercentSymbol(this.getElementText());
    }
    if (this.currentElement.tagName === 'INPUT' && this.isNumber(this.getInputFieldValue())) {
      const num = this.getInputFieldValue();
      if (this.element) {
        this.element.defaultValue = !!this.numberOnly ? this.formatNumber(num) : this.showPercentSymbol(this.formatNumber(num));
        this.cdRef.detectChanges();
      }
      this.currentElement.value = !!this.numberOnly ? this.formatNumber(num) : this.showPercentSymbol(this.formatNumber(num));
    }
  }

  ngOnChanges(): void {
    this.ngOnInit();
  }

  @HostListener('keypress', ['$event']) keyPress(event: KeyboardEvent) {
    this.setValidator(event);
  }
  @HostListener('input', ['$event.target']) onInput(inputElement) {
    const num = this.formatNumber(inputElement.value);
    if (!!!this.numberOnly) {
      inputElement.value = this.showPercentSymbol(num);
    }
  }

  showPercentSymbol(num) {
    let value;
    if (num != null || num !== '') {
      value = this.isDecimal(String(num));
     }
    return String(value) + '%';
  }

  isDecimal(num: string): string {
    return String(num);
  }

  isNumber(value: string) {
    const val = this.formatNumber(value);
    return  !isNaN(Number(val));
  }
  getElementText() {
    return (this.defaultValue !== undefined) ? this.defaultValue : this.currentElement.innerHTML;
  }

  formatNumber(input: string) {
    return (String(input).replace(/(%)/g, ''));
  }

  getInputFieldValue(): string {
    return (this.defaultValue !== undefined) ? this.defaultValue : this.currentElement.value;
  }

 setValidator(event): void {
    const regExpNumberFilter = /([0-9])+/;
    if (!regExpNumberFilter.test(event.key)) {
      event.preventDefault();
      return;
    }
  }
}

