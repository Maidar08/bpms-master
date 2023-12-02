import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'erin-subtitle',
  template: `
    <div class="title">
      <span class="title-text">{{subtitleName}}</span>
      <hr class="line separator-line-style">
    </div>
  `,
  styleUrls: ['./erin-subtitle.component.scss']
})
export class ErinSubtitleComponent implements OnInit {
  @Input() subtitleName: string;

  constructor() {
  }

  ngOnInit(): void {
  }

}
