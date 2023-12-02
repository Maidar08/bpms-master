import {ChangeDetectorRef, Directive, ElementRef, HostListener, Input, OnChanges, OnInit} from '@angular/core';

@Directive({
  selector: '[dateFormatValidation]'
})
export class dateFormatValidation implements OnInit, OnChanges {
  @Input('dateFormatValidation') defaultValue;
  @Input() element;
  curElement;
  useSlash = false;
  constructor(private elementRef: ElementRef, private cdRef: ChangeDetectorRef) {
    this.curElement = this.elementRef.nativeElement;
  }

  ngOnInit() {
    if (this.curElement.tagName === 'INPUT' && this.getInputFieldValue()) {
      const dateInput = this.getInputFieldValue();
      if (this.element) {
        this.element.defaultValue = this.slashAdder(dateInput);
        this.cdRef.detectChanges();
      }
      this.curElement.value = this.slashAdder(dateInput);
    }
  }

  ngOnChanges(): void {
    this.ngOnInit();
  }

  @HostListener('keypress', ['$event']) keyPress(event: KeyboardEvent) {
    this.setValidator(event);
  }

  @HostListener('input', ['$event.target']) onInput(inputElement) {
    const dateInput = inputElement.value;
    inputElement.value = this.slashAdder(dateInput);
  }

  slashAdder(dateInput) {
    const usedateInput = String(dateInput);
    if (this.useSlash ) {
      return usedateInput + '/';
    }
    return usedateInput;
  }

  getInputFieldValue(): string {
    return this.curElement.value;
  }

  setValidator(event): void {
    const regExpNumberFilter = /([0-9])+/;
    if (!regExpNumberFilter.test(event.key)) {
      event.preventDefault();
    } else {
      this.useSlash = this.curElement.value.length === 1;
    }
  }
}
