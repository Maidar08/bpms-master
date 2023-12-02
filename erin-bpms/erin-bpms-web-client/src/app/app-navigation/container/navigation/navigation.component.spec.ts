import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {NavigationSandboxService} from '../../navigation-sandbox.service';
import {RouterModule} from '@angular/router';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NavigationComponent} from './navigation.component';
import {MenuNode} from '../../navigation.model';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {PermissionService} from '../../../erin-aim/authorization/permission/permission.service';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatTreeModule} from '@angular/material/tree';
import {MatButtonModule} from '@angular/material/button';

describe('NavigationComponent', () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;
  let sb: NavigationSandboxService;

  class MockNavigationSandbox {
    private permittedItems: MenuNode[] = [];

    getPermittedNavItems() {
      return this.permittedItems;
    }
    setPermittedItems(value: MenuNode[]) {
      this.permittedItems = value;
    }
  }
  const permittedItems: MenuNode[] = [{ name: 'string', icon: 'string', route: 'string', id: 'string' }];
  const mockNavigationSb = new MockNavigationSandbox();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        HttpClientTestingModule,
        BrowserAnimationsModule,
        BrowserDynamicTestingModule,
        MatSlideToggleModule,
        MatTreeModule,
        MatButtonModule,
      ],
      declarations: [NavigationComponent, ErinLoaderComponent],
      providers: [
        {provide: NavigationSandboxService, useValue: mockNavigationSb},
        PermissionService,
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigationComponent);
    component = fixture.componentInstance;
    sb = TestBed.inject(NavigationSandboxService);
    component.dataSource.data = permittedItems;
    component.isTabletPortrait = true;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should function toggleDrawer', () => {
    expect(component.isTabletPortrait).toBeTruthy();
    component.isExpanded = true;
    expect(component.treeControl.collapseAll()).toEqual(undefined);
    component.isExpanded = !component.isExpanded;
    expect(component.isExpanded).toBeFalsy();
  });

  it('should getRoute', () => {
    const node = {expandable: true, name: 'string', icon: 'dashboard', route: '/loan-page/dashboard', level: 0};
    const icon = node.icon;
    const data = {icon: 'dashboard', name: 'Хяналтын самбар', id: '', route: '/loan-page/dashboard',
      children: [{icon: '', name: 'string', id: 'string', route: '/loan-page/dashboard/my-loan-request'}]};
    expect(data.icon).toEqual(icon);
    expect(data.children[0].route).not.toBeNull();
    component.getRoute(node);
    expect(node.route).not.toBeNull();
  });
});
