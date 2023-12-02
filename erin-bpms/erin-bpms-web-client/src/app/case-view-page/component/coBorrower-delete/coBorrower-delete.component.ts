import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FormsModel} from '../../../models/app.model';
import {LoanSearchPageSandbox} from '../../../loan-search-page/loan-search-page-sandbox.service';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {ConfirmRemoveCoBorrowerDialogComponent} from '../confirm-remove-coBorrower-dialog/confirm-remove-coBorrower-dialog.component';
import {ConfirmDialogComponent} from '../../../common/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'remove_coborrower',
  template: `
    <mat-card class="card-container">
      <div class="title">
        <hr class="line">
        <span class="title-text">{{title}}</span>
        <hr class="line">
      </div>
      <ng-container class="co-Borrowers">
        <div *ngFor="let form of coBorrowerForm index as i">
          <span class="material-icons-outlined remove-btn" *ngIf="!buttonState"
                (click)="coBorrowerRemove(form, i)">indeterminate_check_box</span>
          <dynamic-fields [forms]="form"></dynamic-fields>
        </div>
      </ng-container>
      <div class="button-container">
        <button mat-flat-button color="primary" (click)="continue()" [disabled]="buttonState">ҮРГЭЛЖЛҮҮЛЭХ</button>
      </div>
    </mat-card>    `,
  styleUrls: ['./coBorrower-delete.component.scss']
})
export class CoBorrowerDeleteComponent implements OnInit, OnChanges {
  @Input() instanceId: string;
  @Input() title: string;
  @Input() data: FormsModel[];
  @Input() buttonState = false;
  @Output() submitEmitter = new EventEmitter<any>();
  coBorrowerForm = [];
  dialogData = {name: '', confirmButton: 'Тийм', closeButton: 'Үгүй', message: '' };
  coBorrowerNumbers = [];

  constructor(private sb: LoanSearchPageSandbox, public dialog: MatDialog) {
  }

  ngOnChanges(changes: SimpleChanges): void { this.ngOnInit(); /* It's called when switch between active and completed task */ }

  ngOnInit(): void {
    this.coBorrowerForm = [];
    const hashMap = new Map();

    for (const form of this.data) {
      hashMap.set(form.id, form);
    }

    const realCoBorrowerNumber = hashMap.size / 3;
    for (let i = 1; i <= realCoBorrowerNumber; i++) {
      if (hashMap.get('fullNameCoBorrower-' + i) !== undefined) {
        const account = [];
        account.push(hashMap.get('fullNameCoBorrower-' + i));
        account.push(hashMap.get('riskyCustomerValue-' + i));
        account.push(hashMap.get('loanClassName-' + i));
        this.coBorrowerForm.push(account);
      }
    }
  }

  coBorrowerRemove(form, index) {
    const coBorrowerIndex = form[0].id.split('fullNameCoBorrower-');
    const coBorrowerName = this.data.find(field => field.id === 'fullNameCoBorrower-' + coBorrowerIndex[1]);
    if (coBorrowerName !== undefined) {
      this.dialogData.name = coBorrowerName.formFieldValue.defaultValue;
    }
    const config = new MatDialogConfig();
    config.width = '500px';
    config.data = this.dialogData;
    const dialogRef = this.dialog.open(ConfirmRemoveCoBorrowerDialogComponent, config);
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.coBorrowerNumbers.push(coBorrowerIndex[1]);
        this.coBorrowerForm.splice(index, 1);
      }
    });
  }

  continue() {
    if (this.coBorrowerNumbers.length === 0) {
      const config = new MatDialogConfig();
      this.dialogData.message = 'Та хамтран хасахгүй үргэлжлүүлэхдээ итгэлтэй байна уу';
      config.data = this.dialogData;
      const dialogRef = this.dialog.open(ConfirmDialogComponent, config);
      dialogRef.afterClosed().subscribe(res => {
        if (res) {
          this.submit();
        }
      });
    } else {
      this.submit();
    }
  }

  submit() {
    this.submitEmitter.emit([this.getCoBorrowerForm(), this.mapForm()]);
  }

  mapForm() {
    const returnForm = [];
    this.mapFields(this.coBorrowerNumbers, returnForm);
    return returnForm;
  }

  mapFields(forms: any[], returnForm: any[]) {
    if (this.coBorrowerNumbers.length > 0) {
      returnForm.push(
        {id: 'indexes', formFieldValue: {defaultValue: this.coBorrowerNumbers}, label: null, type: 'Object', validations: [], options: []}
        );
    }
  }

  getCoBorrowerForm() {
    const returnForm = [];
    this.coBorrowerForm.map( coBorrower => {
      coBorrower.map( field => returnForm.push(field) );
    } );
    return returnForm;
  }
}
