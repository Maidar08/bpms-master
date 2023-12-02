import {Component} from '@angular/core';
import {UserLoginModel} from '../../models/login.model';
import {AuthenticationSandboxService} from '../../../erin-aim/authentication/authentication-sandbox.service';


@Component({
  selector: 'app-login-container',
  template: `
    <div class="login-main">
      <div class="login-background">
        <mat-progress-bar *ngIf="loading" mode="indeterminate"></mat-progress-bar>
        <img src="./assets/images/img.jpg" class="login_picture" alt="">
      </div>
      <div class="login-wrapper">
        <div class="login-card">
          <app-login-card
            (onLogin)="login($event)"
            [errorMessage]="errorMessage"
            [logo]="logo"
            [disabled]="loading">
          </app-login-card>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./login-container.component.scss']
})
export class LoginContainerComponent {
  logo = './assets/images/khas-dark-logo.png';
  errorMessage: string;
  loading = false;

  constructor(private sb: AuthenticationSandboxService) {
  }

  login(credentials: UserLoginModel): void {
    this.errorMessage = '';
    this.triggerLoader();
    if (credentials.userId && credentials.password) {
      this.sb.login(credentials.userId, credentials.password, '/loan-page').subscribe(() => {
          this.triggerLoader();
        }, error => {
          this.errorMessage = error;
          this.triggerLoader();
        },
      );
    }
  }

  private triggerLoader() {
    this.loading = !this.loading;
  }

}
