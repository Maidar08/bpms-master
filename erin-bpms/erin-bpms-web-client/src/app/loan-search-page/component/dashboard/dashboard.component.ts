import {Component} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {NavigationSandboxService} from '../../../app-navigation/navigation-sandbox.service';

@Component({
  selector: 'app-dashboard',
  template: `
    <router-outlet></router-outlet>
  `,
})
export class DashboardComponent {
  constructor(public router: Router, private sb: NavigationSandboxService) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        if (this.router.url === '/loan-page/dashboard') {
          const route = this.sb.getFirstPermittedRoute();
          this.router.navigateByUrl(route ? route : '/loan-page/404');
        }
      }
    });
  }
}
