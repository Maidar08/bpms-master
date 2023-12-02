import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NotFoundComponent} from './not-found.component';
import {RouterModule} from '@angular/router';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatTreeModule} from '@angular/material/tree';
import {MatButtonModule} from '@angular/material/button';
import {NavigationSandboxService} from '../app-navigation/navigation-sandbox.service';
import {PermissionService} from '../erin-aim/authorization/permission/permission.service';
import {By} from '@angular/platform-browser';

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;
  let sb: NavigationSandboxService;

  class MockNavigationSandbox {
  }
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
      declarations: [ NotFoundComponent ],
      providers: [
        {provide: NavigationSandboxService, useValue: mockNavigationSb},
        PermissionService,
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should log out', () => {
    const logoutButton = fixture.debugElement.query(By.css('#logout'));
    logoutButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(component.logout).toBeTruthy();
  });
});
