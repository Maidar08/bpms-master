import {Component, OnInit} from '@angular/core';
import {NavigationSandboxService} from '../app-navigation/navigation-sandbox.service';

@Component({
  selector: 'app-not-found',
  template: `
    <mat-card class="card">
      <mat-card-header>
        <p class="header">404 Хуудсыг харах эрхгүй байна</p>
      </mat-card-header>
      <mat-divider></mat-divider>
      <mat-card-content>
        <p class="content">
          Та энэ хуудсыг харах эрхгүй байна. <br>
          Cистем админтай холбоо барина уу.
        </p>
      </mat-card-content>
      <mat-card-actions>
        <span class="spacer"></span>
        <button id="logout" mat-stroked-button color="primary" (click)="logout()">ГАРАХ</button>
      </mat-card-actions>
    </mat-card>
  `,
  styleUrls: ['./not-found.component.scss']
})
export class NotFoundComponent implements OnInit {

  constructor(private sb: NavigationSandboxService) {

  }

  ngOnInit(): void {
  }

  logout() {
    sessionStorage.clear();
    this.sb.logout();
  }
}
