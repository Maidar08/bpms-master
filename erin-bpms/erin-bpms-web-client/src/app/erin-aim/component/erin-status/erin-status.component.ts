import {Component, Input} from '@angular/core';

//TODO improve
@Component({
  selector: 'erin-status',
  template: `
    <div [ngClass]="isActive ? 'erin-status active' : 'erin-status inactive'">{{content}}</div>
  `,
  styleUrls: ['./erin-status.component.scss']
})
export class ErinStatusComponent {
  @Input() content: string;
  @Input() isActive: boolean;

  constructor() {
  }
}
