import {Component, OnInit, ViewChild} from '@angular/core';
import {NavigationComponent} from '../navigation/navigation.component';
import {NavigationSandboxService} from '../../navigation-sandbox.service';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-frame',
  template: `
    <header-toolbar
      [headerLogo]="headerLogo"
      [headerText]="headerText"
      [username]="username"
      [createRequest]="createRequest"
      (toggleMenuButton)="toggleMenu()"
      (openDialogButton)="openDialog()">
    </header-toolbar>
    <navigation #navigationComponent></navigation>
  `,
})
export class AppFrameComponent implements OnInit {
  @ViewChild('navigationComponent', {static: true}) private sidenav: NavigationComponent;
  headerLogo = 'assets/images/khas-logo_1.png';
  headerText = 'ХАСBANK - БИЗНЕС ПРОЦЕСС УДИРДЛАГЫН СИСТЕМ';
  createRequest = 'Хүсэлт үүсгэх';
  username = '';

  constructor(private sb: NavigationSandboxService, public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.username = this.sb.getUserName();
  }

  toggleMenu() {
    this.sidenav.toggleDrawer();
  }

  openDialog(): void {
    this.sb.openRequestDialog();
  }
}
