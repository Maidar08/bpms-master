import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NavigationSandboxService} from '../../navigation-sandbox.service';

@Component({
  selector: 'header-toolbar',
  template: `
    <mat-toolbar class="header-toolbar">
      <button mat-icon-button (click)="toggleMenu()">
        <mat-icon>menu</mat-icon>
      </button>
      <img class="logo" src="{{headerLogo}}" width="50" height="50">
      <span class="header-title">{{headerText}}</span>
      <span class="spacer"></span>
      <button *canAccess="'bpms.bpm.StartProcess' || 'bpms.bpm.CreateOrganizationRequest'" class="addButton" mat-icon-button (click)="openDialog()">
        <mat-icon>add_circle_outline</mat-icon>
      </button>
      <span  *canAccess="'bpms.bpm.StartProcess'" ><p>{{createRequest}}</p></span>
      <button mat-icon-button [matMenuTriggerFor]="logoutMenu" class="logoutButton">
        <mat-icon #userIcon>account_circle</mat-icon>
      </button>
      <mat-menu #logoutMenu="matMenu">
        <button mat-menu-item (click)="logout()">
          <mat-icon class="exit-icon">exit_to_app  </mat-icon>
          <span>Гарах</span></button>
      </mat-menu>
      <span class="current-user"><p>{{username}}</p></span>
    </mat-toolbar>
  `,
  styleUrls: ['./header-toolbar.component.scss']
})
export class HeaderToolbarComponent implements OnInit {
  @Input() headerLogo: any;
  @Input() headerText: string;
  @Input() username: string;
  @Input() createRequest: string;
  @Output() toggleMenuButton = new EventEmitter<any>();
  @Output() openDialogButton = new EventEmitter<any>();

  constructor(private sb: NavigationSandboxService) {

  }

  ngOnInit(): void {
    this.username = this.sb.getUserName();
  }

  logout() {
    sessionStorage.clear();
    this.sb.logout();
  }

  toggleMenu() {
    this.toggleMenuButton.emit();
  }

  openDialog() {
    this.openDialogButton.emit();
  }
}
