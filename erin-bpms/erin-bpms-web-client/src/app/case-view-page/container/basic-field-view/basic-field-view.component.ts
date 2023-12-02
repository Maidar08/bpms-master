import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {CaseViewSandboxService} from '../../case-view-sandbox.service';

@Component({
  selector: 'basic-field-view',
  template: `
      <mat-card class="card-container">
          <div class="fields">
              <div class="form-field-container">
                  <ng-container *ngFor="let form of formContainer" class="form-container">
                      <dynamic-fields [forms]="form" [inputState]="true"></dynamic-fields>
                  </ng-container>
              </div>
          </div>
      </mat-card>
  `,
  styleUrls: ['./basic-field-view.component.scss']
})
export class BasicFieldViewComponent implements OnChanges {
  @Input() instanceId: string;
  @Input() currentURL: string;
  @Output() cif = new EventEmitter<string>();
  @Output() requestIdEmit = new EventEmitter();
  formContainer = [];
  subtitle;
  cifNumber;
  requestId;
  constructor(private sb: CaseViewSandboxService) {
  }
  ngOnChanges(): void {
    this.sb.getCustomerVariables(this.instanceId, 'active-request-main-info').subscribe(res => {
      this.handleFormResponse(res);
    });
  }

  hasNoCaseFolder(): boolean {
    return (this.currentURL.includes('headerFields'));
  }

  private handleFormResponse(res) {
    this.cifNumber =  res.find(field => field.id === 'cifNumber').formFieldValue.defaultValue;
    this.requestId = res.find(field => field.id === 'processRequestId').formFieldValue.defaultValue;
    this.formContainer = [];
    this.subtitle = new Set();
    for (const item of res) {
      this.subtitle.add(item.context);
    }
    this.formContainer = [];
    let forms = [];
    for (const title of this.subtitle) {
      for (const item of res) {
        if (item.context === title) {
          forms.push(item);
        }
      }
      this.formContainer.push(forms);
      forms = [];
    }
    this.cif.emit(this.cifNumber);
    this.requestIdEmit.emit(this.requestId);
  }
}
