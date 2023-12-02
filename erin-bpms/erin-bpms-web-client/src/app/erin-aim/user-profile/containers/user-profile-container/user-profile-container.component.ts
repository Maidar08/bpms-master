import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserModel} from '../../../user-management/models/user-management.model';
import {UserProfileSandboxService} from '../../user-profile-sandbox.service';

@Component({
  selector: 'user-profile-container',
  template: `
    <div class="user-profile-container">
      <div class="user-profile-main-container">
        <div style="background-color: #DEEBFF; padding:16px; border-radius: 3px; display: inline-flex; margin-bottom: 5px">
          <mat-icon style="color: #0052cc; vertical-align: middle; margin-right: 16px;">info</mat-icon>
          <span>Хэрэглэгчийн нэр засварлах боломжгүйг анхаарна уу. Дараагийн шинэчлэлтийг хүлээнэ үү.</span>
        </div>
        <button (click)="editProfile()" mat-button class="edit-toggle-button float-right" color="primary">
          <mat-icon *ngIf="isNotEditable">edit</mat-icon>
          <mat-icon *ngIf="!isNotEditable">close</mat-icon>
        </button>
        <form [formGroup]="userProfileForm">
          <mat-form-field appearance="outline" class="user-profile-form-field">
            <mat-label>Хэрэглэгчийн нэр</mat-label>
            <input matInput
                   formControlName="username"
                   [pattern]="pattern"
                   minlength="3"
                   required>
          </mat-form-field>

          <mat-form-field appearance="outline" class="user-profile-form-field">
            <mat-label>Имэйл</mat-label>
            <input matInput formControlName="email" required>
            <mat-error *ngIf="userProfileForm.get('email').invalid">
              Имэйл буруу оруулсан байна!
            </mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="user-profile-form-field">
            <mat-label>Овог</mat-label>
            <input matInput formControlName="lastName">
          </mat-form-field>

          <mat-form-field appearance="outline" class="user-profile-form-field">
            <mat-label>Нэр</mat-label>
            <input matInput formControlName="firstName">
          </mat-form-field>

          <mat-form-field appearance="outline" class="user-profile-form-field">
            <mat-label>Утас</mat-label>
            <input matInput type="number" formControlName="phoneNumber">
          </mat-form-field>

          <div class="gender-picker">
            <mat-label disabled="!isNotEditable">Хүйс:</mat-label>
            <mat-radio-group formControlName="gender">
              <mat-radio-button value="FEMALE">Эмэгтэй</mat-radio-button>
              <mat-radio-button style="margin-left: 15px" value="MALE">Эрэгтэй</mat-radio-button>
            </mat-radio-group>
          </div>

          <mat-accordion *ngIf="!isNotEditable">
            <mat-expansion-panel>
              <mat-expansion-panel-header>
                <mat-panel-title>
                  Нууц үг солих
                </mat-panel-title>
              </mat-expansion-panel-header>
              <mat-form-field *ngIf="!isNotEditable" appearance="outline" class="user-profile-form-field">
                <mat-label>Шинэ нууц үг</mat-label>
                <input matInput formControlName="newPassword" type="password" minlength="8">
                <mat-hint>
                  8-аас урт, мөн нэг тоо, нэг том тэмдэгт агуулсан байхыг анхаарна уу
                </mat-hint>
                <mat-error>
                  {{getPasswordErrorMessage('newPassword')}}
                </mat-error>
              </mat-form-field>

              <mat-form-field *ngIf="!isNotEditable" appearance="outline" class="user-profile-form-field">
                <mat-label>Шинэ нууц үг давтах</mat-label>
                <input matInput formControlName="repeatPassword" type="password">
              </mat-form-field>
              <mat-error *ngIf="notSame()">
                Ижил нууц үг оруулна уу.
              </mat-error>
            </mat-expansion-panel>
          </mat-accordion>

          <button mat-flat-button
                  *ngIf="!isNotEditable"
                  color="primary"
                  type="submit"
                  class="submit-button top-margin float-right"
                  (click)="submit()"
                  [disabled]="userProfileForm.invalid || !!notSame()">{{updateButtonText}}</button>

        </form>
      </div>
    </div>
  `,
  styleUrls: ['./user-profile-container.component.scss']
})
export class UserProfileContainerComponent implements OnInit {
  userProfileForm: FormGroup;
  updateButtonText = 'Шинэчлэх';
  ChangePasswordText = 'Нууц үг солих?';
  isNotEditable = true;
  userData: any;
  loaderStyle: string;
  hasShade: boolean;
  loading = true;
  breakpoint: number;
  @Input() pattern = '^\\S*$';
  @Input() isUsernameAlreadyExists: boolean;
  @Input() user: UserModel;
  @Output() onSearch = new EventEmitter();
  @Output() onSubmit = new EventEmitter();

  constructor(private fb: FormBuilder, private sb: UserProfileSandboxService) {
  }

  ngOnInit(): void {
    this.breakpoint = (window.innerWidth <= 600) ? 1 : 3;
    this.sb.getUserProfile().subscribe(res => {
      this.userData = res;
      this.setValues();
    });
    this.loaderStyle = 'loading-page-spinner';
    if (this.hasShade) {
      this.loaderStyle = this.loaderStyle.concat('shade');
    }

    this.userProfileForm = this.fb.group({
      username: [this.userData ? this.userData.username : '', [Validators.required, Validators.minLength(4)]],
      firstName: [this.userData ? this.userData.firstName : ''],
      lastName: [this.userData ? this.userData.lastName : ''],
      email: [this.userData ? this.userData.email : '', [Validators.required, Validators.email]],
      phoneNumber: [this.userData ? this.userData.phoneNumber : null, [Validators.pattern('^[0-9]*$')]],
      gender: [this.userData ? this.userData.gender : ''],
      newPassword: ['', [
        Validators.minLength(8),
        Validators.pattern('^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{0,}$')
      ]],
      repeatPassword: ['']
    });
    this.toggleEditable(false);
  }

  setValues(): void {
    this.userProfileForm.get('username').setValue(this.userData.username);
    this.userProfileForm.get('firstName').setValue(this.userData.firstName);
    this.userProfileForm.get('lastName').setValue(this.userData.lastName);
    this.userProfileForm.get('email').setValue(this.userData.email);
    this.userProfileForm.get('phoneNumber').setValue(this.userData.phoneNumber);
    this.userProfileForm.get('gender').setValue(this.userData.gender);
  }

  setLoader() {
    this.loading = false;
  }

  submit(): void {
    this.loading = true;
    if (this.userProfileForm.get('newPassword').value === this.userProfileForm.get('repeatPassword').value) {
      this.sb.updateProfile(this.userProfileForm.value).subscribe(res => {
        this.loading = false;
      }, () => this.loading = false);
    } else {
      this.loading = false;
    }
  }

  editProfile() {
    this.toggleEditable(this.isNotEditable);
    this.isNotEditable = !this.isNotEditable;
  }

  toggleEditable(isEditable: boolean) {
    this.userProfileForm.get('username').disable();
    if (isEditable) {
      this.userProfileForm.get('firstName').enable();
      this.userProfileForm.get('lastName').enable();
      this.userProfileForm.get('email').enable();
      this.userProfileForm.get('phoneNumber').enable();
      this.userProfileForm.get('gender').enable();
      this.userProfileForm.get('newPassword').enable();
      this.userProfileForm.get('repeatPassword').enable();
    } else {
      this.userProfileForm.get('firstName').disable();
      this.userProfileForm.get('lastName').disable();
      this.userProfileForm.get('email').disable();
      this.userProfileForm.get('phoneNumber').disable();
      this.userProfileForm.get('gender').disable();
      this.userProfileForm.get('newPassword').disable();
      this.userProfileForm.get('repeatPassword').disable();
    }
  }

  getPasswordErrorMessage(formControlName: string): string {
    if (this.userProfileForm.get(formControlName).hasError('required')) {
      return 'Нууц үг заавал оруулна уу.';
    } else if (this.userProfileForm.get(formControlName).hasError('minlength')) {
      return 'Нууц үг богино байна!';
    } else if (this.userProfileForm.get(formControlName).hasError('pattern')) {
      return 'Нэг тоо эсвэл нэг том тэмдэгт агуулаагүй байна! Зөвхөн латинаар бичнэ үү';
    } else {
      return '';
    }
  }

  notSame(): boolean {
    return this.userProfileForm.get('newPassword').value !== this.userProfileForm.get('repeatPassword').value;
  }

  onResize(event) {
    this.breakpoint = (event.target.innerWidth <= 600) ? 1 : 3;
  }
}

