import {Component} from '@angular/core';
import {ErinBaseField} from '../ErinBaseField';

@Component({
  selector: 'scan-fingerprint-field',
  template: `
    <div class="fingerprint-form">
<!--      <label><span *ngIf="isRequired(field)" class="error">*</span>{{field.label}}</label>-->
<!--      <img *ngIf="this.field.formFieldValue.defaultValue" [src]="imageUrl"/>-->
<!--      <button mat-icon-button class="placeholder" *ngIf="!this.field.formFieldValue.defaultValue">-->
<!--        <mat-icon>fingerprint</mat-icon>-->
<!--      </button>-->
<!--      <mat-progress-bar *ngIf="load" class="progress-bar" [mode]="'indeterminate'"></mat-progress-bar>-->
<!--      <button class="scanBtn" (click)="scanFingerPrint()">-->
<!--        Унших-->
<!--      </button>-->
    </div>
  `,
  styleUrls: ['./scan-fingerprint-field.component.scss']
})

export class ScanFingerprintFieldComponent extends ErinBaseField {
  imageUrl: any;
  load = false;

  scanFingerPrint() {
    // this.load = true;
    // fScan().then(res => {
    //   this.load = false;
    //   this.imageUrl = res;
    //   this.field.formFieldValue.defaultValue = res;
    // }, () => {
    //   this.load = false;
    // });
  }
}

