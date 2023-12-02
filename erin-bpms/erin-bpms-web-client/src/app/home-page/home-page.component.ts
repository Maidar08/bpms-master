import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DatePipe} from '@angular/common';
import {version} from '../../../package.json';

@Component({
  selector: 'app-home-page',
  template: `
    <div class="home-page">
      <div class="header">
        <img class="header-logo" src="{{xacLogo}}" alt="">
      </div>
      <div class="navigation">
        <button mat-flat-button class="credit-card" (click)="navigate()">{{titles[0]}}</button>
        <a mat-flat-button href="{{route[1]}}" class="credit-card">{{titles[1]}}</a>
      </div>
      <div class="copyright"><a href="{{erinSystemsLink}}" class="link">{{copyright()}}</a></div>
    </div>
  `,
  styleUrls: ['./home-page.component.scss'],
  providers: [DatePipe]
})
export class HomePageComponent {
  xacLogo = './assets/images/khas-dark-logo.png';
  background = './assets/images/img.jpg';
  titles = ['BPMS', 'Кредит карт'];
  jsonURL = 'assets/URL.json';
  route = ['/login'];
  erinSystemsLink = 'https://erin.systems/page/home';
  constructor(private router: Router, private http: HttpClient, private datePipe: DatePipe) {
    this.getJSON().subscribe(data => this.route.push(data.creditCard.url));
  }

  getJSON(): Observable<any> {
    return this.http.get(this.jsonURL);
  }

  navigate() {
    this.router.navigate([this.route[0]]);
  }
  copyright() {
    const year = this.datePipe.transform(new Date(), 'yyyy');
    return '©' + year + ' Powered by ERIN Systems LLC | BPMS v' + version;
  }
}
