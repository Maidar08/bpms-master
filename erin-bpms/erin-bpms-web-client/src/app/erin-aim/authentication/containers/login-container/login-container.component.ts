import {Component, Input} from '@angular/core';
import {finalize} from 'rxjs/operators';
import {AuthenticationSandboxService} from '../../authentication-sandbox.service';

@Component({
  selector: 'login',
  template: `
    <div id="login-illustration" class="imageStyle" [ngStyle]="{'background-image':' url(' + loginImage+ ')'}">
      <mat-card class="default-login-mat-card" [ngClass]="{'default-login-mat-card': !hasCustomStyle, customStyle : hasCustomStyle}">

        <mat-card-content>
          <div id="login-wrapper">
            <app-login-card
              (onLogin)="login($event)"
              [errorMessage]="errorMessage"
              [headerLogo]="logoPath"
              [loading]="loading"
              [headerText]=headerText>
            </app-login-card>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
})
export class LoginContainerComponent {
  @Input() logoPath: string;
  @Input() loginImage: string;
  @Input() url: string;
  @Input() customStyle: string;
  @Input() hasCustomStyle: boolean;
  errorMessage: string;
  loading = false;
  headerText = 'Хэрэглэгчийн нэр, нууц үгээ оруулна уу.';

  constructor(private sb: AuthenticationSandboxService) {
    this.hasCustomStyle = false;
  }

  login(credentials) {
    this.errorMessage = '';
    this.loading = true;
    this.sb.login(credentials.username, credentials.password, this.url).pipe(finalize(() => {
        this.loading = false;
      })
    ).subscribe(() => {
    }, error => {
      this.errorMessage = error;
    });
  }
}
