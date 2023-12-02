import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {FormsModel} from '../../../models/app.model';

@Component({
  selector: 'dynamic-columns',
  template: `
      <erin-title [titleName]="title" *ngIf="title"></erin-title>
      <div [ngClass]="getCustomColumnStyle(data.length)">
        <div *ngFor="let forms of data; index as i">
        <span class="column-title">{{subtitlesInput[i]}}</span>
          <dynamic-fields [forms]="forms" [numberOfColumns]="columnNumber" [inputState]="disableState"></dynamic-fields>
        </div>
      </div>
  `,
  styleUrls: ['./dynamic-columns.component.scss']
})
export class DynamicColumnsComponent implements OnChanges {
  @Input() title: string;
  @Input() data: FormsModel[][];
  @Input() subtitlesInput: string[];
  @Input() disableState;
  columnNumber: number;
  constructor() {}

  ngOnChanges(changes: SimpleChanges): void {
    this.columnNumber = 1;
  }

  getCustomColumnStyle(numberOfColumns): string {
    switch (numberOfColumns) {
      case 2:
        return 'two-column';
      case 3:
        return 'three-column';
      default:
        return null;
    }
  }
}
