import {Component, EventEmitter, Inject, Input, OnInit, Output} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {Gender, UserDialogData, UserModel} from '../../models/user-management.model';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {debounceTime, distinctUntilChanged} from 'rxjs/operators';

@Component({
  selector: 'user-dialog',
  template: `
    <h1 mat-dialog-title>{{data.title}}</h1>
    <mat-divider></mat-divider>
    <form (submit)="submit()" [formGroup]="userForm">
      <mat-dialog-content>
        <div *ngIf="loading" class="lds-ellipsis">
          <div></div>
          <div></div>
          <div></div>
        </div>
        <mat-form-field appearance="outline" class="user-dialog-form-field">
          <mat-label>Хэрэглэгчийн нэр</mat-label>
          <input
            class="testing-input"
            matInput
            formControlName="username"
            [pattern]="pattern"
            minlength="4"
            required
            autocomplete="Username"
            (keyup)="setLoader()"/>
          <mat-error *ngIf="userForm.get('username').invalid">
            {{getUsernameErrorMessage()}}
          </mat-error>
          <mat-hint>
            Хэрэглэгчийн нэр хоосон зай агуулахгүйг анхаарна уу
          </mat-hint>
        </mat-form-field>

        <mat-form-field appearance="outline" class="user-dialog-form-field">
          <mat-label>Имэйл</mat-label>
          <input
            matInput
            required
            formControlName="email"
            autocomplete="Email"
          />
          <mat-error *ngIf="userForm.get('email').invalid">
            Имэйл буруу оруулсан байна!
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="user-dialog-form-field">
          <mat-label>Овог</mat-label>
          <input
            matInput
            formControlName="lastName"
            autocomplete="Last Name"
          />
        </mat-form-field>

        <mat-form-field appearance="outline" class="user-dialog-form-field">
          <mat-label>Нэр</mat-label>
          <input
            matInput
            formControlName="firstName"
            autocomplete="First Name"
          />
        </mat-form-field>

        <mat-form-field appearance="outline" class="user-dialog-form-field">
          <mat-label>Утас</mat-label>
          <input
            matInput
            formControlName="phoneNumber"
            autocomplete="Phone Number"
          />
        </mat-form-field>

        <div class="gender-picker">
          <label>Хүйс: </label>
          <mat-radio-group formControlName="gender">
            <mat-radio-button value="FEMALE">Эмэгтэй</mat-radio-button>
            <mat-radio-button style="margin-left: 15px" value="MALE">Эрэгтэй</mat-radio-button>
          </mat-radio-group>
        </div>

        <mat-form-field *ngIf="data.type === 'create'" appearance="outline" class="user-dialog-form-field">
          <mat-label>Нууц үг</mat-label>
          <input
            matInput
            formControlName="password"
            type="password"
            minlength="8"
            required
            autocomplete="current-password"
          />
          <mat-error *ngIf="userForm.get('password').invalid">
            {{getPasswordErrorMessage()}}
          </mat-error>
          <mat-hint>
            8-аас урт, мөн нэг тоо, нэг том тэмдэгт агуулсан байхыг анхаарна уу
          </mat-hint>
        </mat-form-field>

        <mat-accordion *ngIf="data.type === 'update'">
          <mat-expansion-panel hideToggle (opened)="passwordReset(true)"
                               (closed)="passwordReset(false)">
            <mat-expansion-panel-header>
              <mat-panel-title>Нууц үг солих</mat-panel-title>
            </mat-expansion-panel-header>
            <mat-form-field appearance="outline" class="user-dialog-form-field">
              <mat-label>Шинэ нууц үг</mat-label>
              <input
                matInput
                formControlName="password"
                type="password"
                minlength="8"
                autocomplete="current-password"
              />
              <mat-error *ngIf="userForm.get('password').invalid">
                {{getPasswordErrorMessage()}}
              </mat-error>
              <mat-hint>
                8-аас урт, мөн ядаж нэг тоо, нэг том тэмдэгт агуулсан байхыг анхаарна уу
              </mat-hint>
            </mat-form-field>
          </mat-expansion-panel>
        </mat-accordion>
      </mat-dialog-content>

      <mat-dialog-actions align="end">
        <button mat-button mat-dialog-close>ЦУЦЛАХ</button>
        <button mat-button [disabled]="loading || userForm.invalid" type="submit" name="submit">
          {{data.type === 'create' ? 'ҮҮСГЭХ' : 'ШИНЭЧЛЭХ'}}
        </button>
      </mat-dialog-actions>
    </form>
  `,
  styleUrls: ['./user-dialog.component.scss']
})
export class UserDialogComponent implements OnInit {
  userForm: FormGroup;
  hasShade: boolean;
  loaderStyle: string;
  originalUserName: string;
  @Input() pattern = '^\\S*$';
  @Input() isUsernameAlreadyExists: boolean;
  @Input() loading: boolean;
  @Input() user: UserModel;
  @Output() onSearch = new EventEmitter();
  @Output() onSubmit = new EventEmitter();
  @Output() username = new EventEmitter();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: UserDialogData,
    private fb: FormBuilder) {
    this.userForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      firstName: [''],
      lastName: [''],
      email: ['', [Validators.email, Validators.required]],
      phoneNumber: [''],
      gender: [Gender[Gender.NA]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{0,}$')
      ]],
    });
  }

  ngOnInit(): void {
    this.loaderStyle = 'loading-page-spinner';
    if (this.data.user) {
      this.originalUserName = this.data.user.username;
    }
    if (this.hasShade) {
      this.loaderStyle = this.loaderStyle.concat(' shade');
    }
    // Time out for the search
    this.userForm.get('username').valueChanges.pipe(
      debounceTime(1000),
      distinctUntilChanged(),
    ).subscribe(username => {
      if (username) {
        this.loading = true;
        this.onSearch.emit(username);
      }
    });

    if (this.data.type === 'update') {
      this.userForm.get('password').disable();
      this.userForm.get('username').disable();
      this.userForm.get('username').setValue(this.data.user.username ? this.data.user.username : '');
      this.userForm.get('firstName').setValue(this.data.user.firstName ? this.data.user.firstName : '');
      this.userForm.get('lastName').setValue(this.data.user.lastName ? this.data.user.lastName : '');
      this.userForm.get('email').setValue(this.data.user.email ? this.data.user.email : '');
      this.userForm.get('phoneNumber').setValue(this.data.user.phoneNumber ? this.data.user.phoneNumber : '');
      let gender;
      if (typeof this.data.user.gender === 'string') {
        gender = this.data.user.gender;
      } else if (typeof this.data.user.gender === 'number') {
        gender = Gender[this.data.user.gender].toLowerCase();
      } else {
        gender = '';
      }
      this.userForm.get('gender').setValue(gender);
    }
  }

  setLoader() {
    this.loading = false;
  }

  submit(): void {
    this.loading = true;
    this.username.emit(this.originalUserName);
    this.onSubmit.emit(this.userForm.value);
  }

  getUsernameErrorMessage() {
    if (this.userForm.get('username').hasError('required')) {
      return 'Нэвтрэх нэр заавал оруулна уу.';
    } else if (this.userForm.get('username').hasError('minlength')) {
      return '3-аас дээш урттай нэр оруулна уу.';
    } else if (this.userForm.get('username').hasError('pattern')) {
      if (this.isUsernameAlreadyExists) {
        return 'Нэр давхцаж байна!';
      }
      return 'Хоосон зай агуулсан байна!';
    } else {
      return 'true';
    }
  }

  getPasswordErrorMessage() {
    if (this.userForm.get('password').hasError('required')) {
      return 'Нууц үг заавал оруулна уу.';
    } else if (this.userForm.get('password').hasError('minlength')) {
      return 'Нууц үг богино байна!';
    } else if (this.userForm.get('password').hasError('pattern')) {
      return 'Нэг тоо эсвэл нэг том тэмдэгт агуулаагүй байна!';
    } else {
      return '';
    }
  }

  passwordReset(isResetting: boolean) {
    if (isResetting) {
      this.userForm.get('password').enable();
    } else {
      this.userForm.get('password').disable();
    }
  }
}
