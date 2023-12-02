import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LoginContainerComponent} from './container/login-container/login-container.component';
import {LoginCardComponent} from './component/login-card/login-card.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {HttpClientModule} from '@angular/common/http';
import {MaterialModule} from '../material.module';
import {AuthenticationSandboxService} from '../erin-aim/authentication/authentication-sandbox.service';
import {RouterModule} from '@angular/router';
import {UserManualComponent} from './component/login-card/user-manual/user-manual.component';


@NgModule({
  declarations: [LoginContainerComponent, LoginCardComponent,
    UserManualComponent],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    HttpClientModule,
    RouterModule
  ],
  providers: [AuthenticationSandboxService]
})
export class LoginModule {
}
