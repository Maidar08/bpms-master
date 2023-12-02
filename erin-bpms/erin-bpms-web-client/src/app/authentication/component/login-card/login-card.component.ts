import {Component, EventEmitter, Input, Output, ViewEncapsulation} from '@angular/core';
import {Link, UserLoginModel} from '../../models/login.model';
import {version} from '../../../../../package.json';
import {DatePipe} from '@angular/common';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {CommonSandboxService} from '../../../common/common-sandbox.service';


@Component({
  selector: 'app-login-card',
  template: `
    <div class="login-content">
      <div><img id="header-logo" src="{{logo}}" style="width: 170px; height: 90px;" alt=""></div>
      <form (ngSubmit)="login()">
        <div class="login-actions">
          <div><span class="mat-caption" color="accent">{{headerText}}</span></div>
          <p *ngIf="errorMessage" class="mat-error mat-caption ">{{errorMessage}}</p>
          <mat-form-field class="half-width input-height" appearance="outline">
            <mat-icon matPrefix color="accent">person</mat-icon>
            <input matInput [(ngModel)]="userId" [ngModelOptions]="{standalone: true}" placeholder="Нэвтрэх нэр" type="text" required>
          </mat-form-field>
          <br>
          <mat-form-field id="passwordField" class="half-width input-height" appearance="outline" color="primary">
            <mat-icon matPrefix color="accent">lock</mat-icon>
            <input matInput [(ngModel)]="password" [ngModelOptions]="{standalone: true}" [type]=" hide ? 'password':'text'" placeholder="Нууц үг"
                   required>
            <mat-icon id="visibilityBtn" matSuffix (click)=" hide = !hide">{{hide ? 'visibility_off' : 'visibility'}}</mat-icon>
          </mat-form-field>
          <br>
          <button mat-flat-button [disabled]=" !userId ||!password || disabled" class="half-width input-height login-button">
            НЭВТРЭХ
          </button>
        </div>
      </form>
      <br>
      <div>
        <a routerLink="/user-manual" class="user-manual" target="_blank">
          BPMS Хэрэглэгчийн гарын авлага
        </a>
      </div>
      <div class="finacle-buttons">
        <a mat-icon-button *ngFor="let link of routeLinks" id="svg-button" href="{{link.url}}">
          <mat-icon #svgIcon svgIcon="{{link.icon}}" class="svg-icon"></mat-icon>
        </a>
      </div>
      <div class="copyright"><a href="{{erinSystemsLink}}" class="link">{{copyright()}}</a></div>
    </div>
  `,
  styleUrls: ['./login-card.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [DatePipe]
})
export class LoginCardComponent {
  @Input() logo: any;
  @Input() errorMessage: string;
  @Input() loading: boolean;
  @Input() headerText: boolean;
  @Input() disabled: boolean;
  @Output() onLogin = new EventEmitter<UserLoginModel>();
  userId: string;
  password: string;
  hide = true;
  erinSystemsLink = 'https://erin.systems/page/home';
  jsonURL = 'assets/URL.json';
  routeLinks: Link[] = [];
  fileToView: string;
  pdfHtmlData;
  fileName: string;
  source: string;
  isHtml = false;
  htmlFile;
  id = 'b41b6417-8e00-4cf9-93c8-966ce6e09ae0';
  instanceId;


  constructor(private datePipe: DatePipe,  private matIconRegistry: MatIconRegistry,
              private domSanitizer: DomSanitizer, private router: Router, private http: HttpClient,
              private commonService: CommonSandboxService,
  ) {
    this.matIconRegistry.addSvgIcon(
      `finacleCRM`,
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/custom-icons/button_crm.svg')
    );
    this.matIconRegistry.addSvgIcon(
      `finacleCore`,
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/custom-icons/button_core.svg')
    );
    this.getJSON().subscribe(data => this.routeLinks.push({url: data.finacleCore.url, icon: 'finacleCRM'}));
    this.getJSON().subscribe(data => this.routeLinks.push({url: data.finacleCRM.url,  icon: 'finacleCore'}));
  }

  getJSON(): Observable<any> {
    return this.http.get(this.jsonURL);
  }
  login(): void {
    this.onLogin.emit({userId: this.userId, password: this.password});
  }

  copyright() {
    const year = this.datePipe.transform(new Date(), 'yyyy');
    return '©' + year + ' Powered by ERIN Systems LLC | BPMS v' + version;
  }
}
