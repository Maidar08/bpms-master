import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {BranchBankingSandbox} from '../../services/branch-banking-sandbox.service';
import {CommonSandboxService} from '../../../common/common-sandbox.service';
import {OverlayRef} from '@angular/cdk/overlay';
import {CLOSE} from '../../../case-view-page/model/task-constant';

@Component({
  selector: 'verify-otp-dialog',
  template: `
    <div class="dialog-container">
      <div mat-dialog-title>
        <button mat-icon-button class="clear-btn" (click)="close()">
          <mat-icon color="grey">close</mat-icon>
        </button>
        <p>{{data.title}}</p>
      </div>
      <div mat-dialog-content>
        <div class="form-field">
          <span>{{data.otpField.label}}</span>
          <input class="simple-field" #workerCif="ngModel" [(ngModel)]=data.otpField.formFieldValue.defaultValue>
        </div>
      </div>
      <div mat-dialog-actions>
        <button id="resendButton" mat-flat-button color="primary" (click)="resendCode()">Код дахин илгээх</button>
        <button id="verifyButton" mat-flat-button color="primary" (click)="verifyOtp()">Баталгаажуулах</button>
      </div>
    </div>
  `,
  styleUrls: ['./verify-otp-dialog.component.scss']
})
export class VerifyOtpDialogComponent implements OnInit {

  constructor(private  commonService: CommonSandboxService, private branchBankingService: BranchBankingSandbox,
              public dialogRef: MatDialogRef<VerifyOtpDialogComponent>, @Inject(MAT_DIALOG_DATA) public data) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (null != this.data.otpField.formFieldValue.defaultValue) {
      this.data.otpField.formFieldValue.defaultValue = '';
    }
  }

  close(): void {
    this.dialogRef.close();
  }

  resendCode(): void {
    const overlayRef = this.commonService.setOverlay();
    const destination = this.commonService.getFormValue(this.data.form, 'ussdPhoneNumber');
    this.branchBankingService.sendOtpCode(this.data.instanceId, destination).subscribe(res => {
      if (res) {
        this.commonService.showSnackBar('Баталгаажуулах код амжилттай илгээлээ', null, 4000);
      } else {
        this.commonService.showSnackBar('Баталгаажуулах код илгээхэд алдаа гарлаа', CLOSE, 3000);
      }
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });
  }

  verifyOtp(): void {
    if (this.commonService.isMandatoryField([this.data.otpField])) {
      return;
    }
    const overlayRef = this.commonService.setOverlay();
    const action = this.data.dialogAction;
    const verificationCode = this.data.otpField.formFieldValue.defaultValue;
    this.branchBankingService.verifyOtpCode(this.data.dialogAction, this.data.instanceId, verificationCode).subscribe(res => {
      if (res) {
        if (action.actionId === 'recoveryRights') {
          this.data.changeUssdState();
          overlayRef.dispose();
        } else if (action.actionId === 'save') {
          this.saveCustomerUssdInfo(overlayRef);
        }
        this.dialogRef.close();
      } else {
        overlayRef.dispose();
        this.commonService.showSnackBar(`Баталгаажуулах код буруу байна!`, null, 3000);
      }
    }, () => {
      overlayRef.dispose();
    });
  }

  private saveCustomerUssdInfo(overlayRef: OverlayRef): void {
    const mainAccountValue = this.commonService.getFormValue(this.data.form, 'mainAccount');
    this.data.mainAccounts.forEach(mainAccount => {
      mainAccount.isPrimary = mainAccount.accountNumber === mainAccountValue;
    });
    this.branchBankingService.saveUssdInfo(this.data.form, this.data.instanceId, this.data.mainAccounts, this.data.allAccounts).subscribe(res2 => {
      if (res2) {
        this.data.refreshUssdInfo();
        this.commonService.showSnackBar('Харилцагчийн мэдээлэл амжилттай хадгаллаа', null, 3000);
      }
      overlayRef.dispose();
    }, () => {
      overlayRef.dispose();
    });
  }
}
