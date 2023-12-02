import {BrowserModule, HammerModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {routes} from './app-routing.module';
import {AppComponent} from './app.component';
import {LoginModule} from './authentication/login.module';
import {NavigationComponent} from './app-navigation/container/navigation/navigation.component';
import {LayoutModule} from '@angular/cdk/layout';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {CdkTableModule} from '@angular/cdk/table';
import {CommonModule, DatePipe, HashLocationStrategy, LocationStrategy} from '@angular/common';
import {HeaderToolbarComponent} from './app-navigation/component/header/header-toolbar.component';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {PaginatorIntl} from './common/paginator-intl';
import {MaterialModule} from './material.module';
import {LoanRequest} from './loan-search-page/loan-request.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppFrameComponent} from './app-navigation/container/app-frame/app-frame.component';
import {AimModule} from './erin-aim/aim.module';
import {AuthenticationService} from './erin-aim/authentication/services/authentication.service';
import {AIM_CONFIG} from './aim.config';
import {RouterModule} from '@angular/router';
import {NotFoundComponent} from './not-found/not-found.component';
import {ErinCommonModule} from './common/erin-common.module';
import {HomePageComponent} from './home-page/home-page.component';
import {CaseView} from './case-view-page/case-view.module';
import {BranchBanking} from './branch-banking/branch-banking.module';
import {LoanContract} from './loan-contract/loan-contract.module';
import {OrganizationRegistration} from './organization-registration/model/organization-registration.module';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    HeaderToolbarComponent,
    AppFrameComponent,
    NotFoundComponent,
    HomePageComponent,
  ],

  imports: [
    BrowserAnimationsModule,
    CommonModule,
    ErinCommonModule,
    CaseView,
    BranchBanking,
    LoanContract,
    BrowserModule,
    MaterialModule,
    LoginModule,
    LoanRequest,
    LayoutModule,
    HammerModule,
    FormsModule,
    OrganizationRegistration,
    RouterModule.forRoot(routes),
    AimModule.forRoot(AIM_CONFIG),
    HttpClientModule,
    CdkTableModule,
    ReactiveFormsModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    })
  ],
  providers: [
    AuthenticationService,
    DatePipe,
    {provide: LocationStrategy, useClass: HashLocationStrategy},
    {provide: MatPaginatorIntl, useClass: PaginatorIntl}
  ],
  entryComponents: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}
