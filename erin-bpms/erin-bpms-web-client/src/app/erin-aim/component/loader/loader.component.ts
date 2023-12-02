import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'erin-loader',
  template: `
    <div *ngIf="loading" [ngClass]="loaderStyle" [class]="style">
      <mat-spinner [diameter]="diameter"></mat-spinner>
    </div>
  `,
  styleUrls: ['./loader.component.scss']
})
export class ErinLoaderComponent implements OnInit {
  @Input() loading: boolean = true;
  @Input() isPageLoader: boolean= true;
  @Input() hasShade = true;
  @Input() style: string;
  loaderStyle: string;
  diameter: number;

  ngOnInit() {
    if (this.isPageLoader) {
      this.loaderStyle = 'loading-page-spinner';
      if (this.hasShade) {
        this.loaderStyle = this.loaderStyle.concat(' shade');
      }
      this.diameter = 100;
    } else {
      this.loaderStyle = 'loading-button-spinner';
      this.diameter = 20;
    }
  }
}



