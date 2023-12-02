import {Component, Input} from '@angular/core';

@Component({
  selector: 'erin-title',
  template: `
    <div class="title">
      <hr class="line separator-line-style">
      <span class="title-text">{{titleName}}</span>
      <hr class="line separator-line-style">
    </div>
  `,
  styleUrls: ['./erin-title.component.scss'],
})
export class ErinTitleComponent {
  @Input() titleName: string;
}
