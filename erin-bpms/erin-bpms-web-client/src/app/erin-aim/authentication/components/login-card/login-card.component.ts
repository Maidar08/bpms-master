import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-login-card',
  template: `
    <div class="login-content">
      <div align="center"><img id="header-logo" src="{{headerLogo}}"></div>
      <form #loginForm="ngForm" (ngSubmit)="login()">
        <div class="login-actions">
          <div><span class="header-text" color="accent">{{headerText}}</span></div>
          <p *ngIf="errorMessage" class="mat-error mat-caption ">{{errorMessage}}</p>
          <mat-form-field class="width-full" appearance="standard">
            <input matInput [(ngModel)]="username" placeholder="Хэрэглэгчийн нэр" name="username" required>
          </mat-form-field>
          <mat-form-field class="width-full" appearance="standard">
            <input matInput [(ngModel)]="password" (keydown.enter)="login()" [type]="hide ? 'password' : 'text'" placeholder="Нууц үг"
                   name="password" required>
            <mat-icon matSuffix (click)="hide = !hide">{{hide ? 'visibility_off' : 'visibility'}}</mat-icon>
          </mat-form-field>
          <div id="login-button">
            <button mat-flat-button color="primary" [disabled]="loading|| loginForm.invalid" class="login-button">
              НЭВТРЭХ
              <erin-loader [loading]="loading" [style]="'float-right'" [isPageLoader]="false"></erin-loader>
            </button>
          </div>
        </div>
      </form>
    </div>
  `

})
export class LoginCardComponent {
  @Input() headerLogo: string;
  @Input() errorMessage: string;
  @Input() loading: boolean;
  @Input() headerText: string;
  @Output() onLogin = new EventEmitter();
  hide = true;
  username: string;
  password: string;

  login(): void {
    this.onLogin.emit({username: this.username, password: this.password});
  }
}
