import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HeaderToolbarComponent} from './header-toolbar.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {By} from '@angular/platform-browser';
import {provideMockStore} from '@ngrx/store/testing';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {NavigationSandboxService} from '../../navigation-sandbox.service';
import {PermissionService} from '../../../erin-aim/authorization/permission/permission.service';
import {MatMenuModule} from '@angular/material/menu';

describe('HeaderToolbar', () => {
  let component: HeaderToolbarComponent;
  let fixture: ComponentFixture<HeaderToolbarComponent>;
  let dialog: MatDialog;
  let sb: NavigationSandboxService;
  const auth = {permissions: [{id: 'user.promotion.addButton'}]};
  const currentState = {auth};
  class MockNavigationSandbox {

    private userName = 'Username';
    getUserName() {
      return this.userName;
    }

    setUserName(value: string) {
      this.userName = value;
    }
  }
  const mockNavigationSb = new MockNavigationSandbox();
  // tslint:disable-next-line:prefer-const
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatToolbarModule,
        MatButtonModule,
        MatMenuModule,
        MatIconModule,
        BrowserAnimationsModule,
        RouterTestingModule,
        HttpClientTestingModule,
        MatDialogModule
      ],
      declarations: [HeaderToolbarComponent],
      providers: [
        provideMockStore({initialState: currentState}),
        {provide: NavigationSandboxService, useValue: mockNavigationSb},
        PermissionService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    sb = TestBed.inject(NavigationSandboxService);
    fixture = TestBed.createComponent(HeaderToolbarComponent);
    dialog = TestBed.inject(MatDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have logo', () => {
    const headerLogo = fixture.debugElement.query(By.css('.logo'));
    expect(headerLogo).toBeTruthy();
  });

  it('should have title', () => {
    const headerTitle = fixture.debugElement.query(By.css('.header-title'));
    expect(headerTitle).toBeTruthy();
  });

  it('should have user icon', () => {
    const userIcon = fixture.debugElement.query(By.css('#headerIcon'));
    expect(userIcon).toBeDefined();
  });

  it('if click user icon, display mat tool tip and logout', () => {
    const currentUserIcon = fixture.debugElement.query(By.css('mat-icon'));
    currentUserIcon.triggerEventHandler('click', '');

    const toolTip = fixture.debugElement.query(By.css('.matTooltip'));
    expect(toolTip).toBeDefined();
  });

  it('current username will be on header section', () => {
    const username = fixture.debugElement.query(By.css('.current-user'));
    expect(username.nativeElement.innerText).toEqual('Username');
  });

  it(' should open dialog when click on add button', () => {
    const addButton = fixture.debugElement.query(By.css('.mat-icon-button'));
    addButton.triggerEventHandler('click', null);
    spyOn(component.openDialogButton, 'emit').and.callThrough();
    component.openDialog();
    expect(component.openDialogButton.emit).toHaveBeenCalled();
  });
});
