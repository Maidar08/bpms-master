import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'erin-field-hint',
  template: `
    <span class="field-description">{{hintText}}</span>`
})
export class ErinFieldHintComponent implements OnInit {
  @Input() hintText;

  constructor() {
  }

  ngOnInit(): void {
  }

}
