import {Component, ViewChild} from '@angular/core';
import {BreakpointObserver} from '@angular/cdk/layout';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {MatSidenav} from '@angular/material/sidenav';
import {FlatTreeControl} from '@angular/cdk/tree';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material/tree';
import {TRANSFORMER} from '../../navigation.model';
import {NavigationSandboxService} from '../../navigation-sandbox.service';
import {Router} from '@angular/router';

interface FlatNode {
  expandable: boolean;
  name: string;
  level: number;
}

@Component({
  selector: 'navigation',
  template: `
    <mat-sidenav-container class="sidenav-container" autosize>
      <mat-sidenav #drawer fixedInViewport
                   [attr.role]="(isTabletPortrait$ | async) ? 'dialog' : 'navigation'"
                   [mode]="(isTabletPortrait$ | async) ? 'over' : 'side'"
                   [opened]="!isTabletPortrait">
        <mat-tree [dataSource]="dataSource" [treeControl]="treeControl" (click)="clearSession()">
          <mat-tree-node *matTreeNodeDef="let node"
                         class="item"
                         routerLink="{{node.route}}"
                         routerLinkActive="active-link"
                         [ngClass]="node.level > 0 ? 'child-tree': 'item' ">
            <button mat-icon-button *ngIf="node.icon" class="margin-left">
              <mat-icon [svgIcon]="node.icon"></mat-icon>
            </button>
            <p *ngIf="isExpanded">{{node.name}}</p>
          </mat-tree-node>
          <mat-tree-node *matTreeNodeDef="let node;when: hasChild"
                         class="item" routerLink="{{getRoute(node)}}" routerLinkActive="active-link">
            <button mat-icon-button>
              <mat-icon [svgIcon]="node.icon"></mat-icon>
            </button>
            <p *ngIf="isExpanded">{{node.name}}</p>
            <button mat-icon-button matTreeNodeToggle
                    [attr.aria-label]="'toggle ' + node.name" class="margin-left-auto" *ngIf="isExpanded">
              <mat-icon class="mat-icon-rtl-mirror">
                {{treeControl.isExpanded(node) ? 'expand_more' : 'chevron_right'}}
              </mat-icon>
            </button>
          </mat-tree-node>
        </mat-tree>
      </mat-sidenav>
      <mat-sidenav-content class="content"
                           [ngClass]="!isTabletPortrait && isExpanded ? 'pushMainArea' : isTabletPortrait? 'originalMain' : 'collapseMain'">
        <router-outlet></router-outlet>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent {
  @ViewChild('drawer', {static: true}) public sidenav: MatSidenav;
  isTabletPortrait = false;
  private BREAK_PORTRAIT_TABLE_POINT = '(max-width: 910px)';

  constructor(private breakpointObserver: BreakpointObserver, private router: Router, private sb: NavigationSandboxService) {
    this.dataSource.data = sb.getPermittedNavItems();
    this.isTabletPortrait$.subscribe(res => {
      this.isTabletPortrait = res;
    });
    if (!this.isFromTask()) {
      this.navigate();
    }
  }


  isTabletPortrait$: Observable<boolean> = this.breakpointObserver.observe(this.BREAK_PORTRAIT_TABLE_POINT)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  treeControl = new FlatTreeControl<FlatNode>(
    node => node.level, node => node.expandable);
  treeFlattener = new MatTreeFlattener(
    TRANSFORMER, node => node.level, node => node.expandable, node => node.children);

  dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);
  isExpanded = false;
  hasChild = (_: number, node: FlatNode) => node.expandable;

  toggleDrawer() {
    if (this.isTabletPortrait) {
      this.isExpanded = true;
      this.sidenav.toggle();
    } else {
      if (this.isExpanded) {
        this.treeControl.collapseAll();
      }
      this.isExpanded = !this.isExpanded;
    }
  }

  isFromTask() {
    return this.router.url.search('case-view') >= 0;
  }

  navigate(): void {
    const permissions = this.dataSource.data;
    if (permissions.length > 0) {
      if (permissions[0].children.length > 0) {
        this.router.navigate([permissions[0].children[0].route]);
      } else {
        this.router.navigate([permissions[0].route]);
      }
    }
  }

  getRoute(node) {
    const icon = node.icon;
    for (const data of this.dataSource.data) {
      if (data.icon === icon && data.children.length > 0) {
        return data.children[0].route;
      }
    }
    return node.route;
  }

  clearSession() {
    sessionStorage.clear();
  }
}
