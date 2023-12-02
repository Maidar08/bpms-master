import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {GroupManagementContainerComponent} from './group-management-container.component';

import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {MatTreeModule} from '@angular/material/tree';
import {GroupManagementSandboxService} from '../../group-management-sandbox.service';
import {provideMockStore} from '@ngrx/store/testing';
import {DetailedUserInfo, Membership, Role, UserInfo} from '../../models/group-management.model';
import {of} from 'rxjs';
import {UserCardViewComponent} from '../../components/user-card-view/user-card-view.component';
import {TreeViewContainerComponent} from '../tree-view-container/tree-view-container.component';
import {UserCardFilter} from '../../components/user-card-view/filter/user-card-filter.component';
import {DragDropModule} from '@angular/cdk/drag-drop';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatChipsModule} from '@angular/material/chips';
import {CommonModule} from '@angular/common';
import {MatDialogModule} from '@angular/material/dialog';
import {MatInputModule} from '@angular/material/input';
import {MatMenuModule} from '@angular/material/menu';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatSelectModule} from '@angular/material/select';
import {MatCardModule} from '@angular/material/card';
import {MatDividerModule} from '@angular/material/divider';
import {BrowserModule} from '@angular/platform-browser';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {ErinChipField} from '../../../component/erin-chip-field/erin-chip-field.component';
import {DropdownComponent} from '../../../component/dropdown/dropdown.component';
import {ErinLoaderComponent} from '../../../component/loader/loader.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {AIM_CONFIG} from '../../../aim.config.token';
import {AimConfig} from '../../../aim.config';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {Node} from '../../models/tree.model';
import {UserSearchFieldComponent} from '../../components/search/user-search-field.component';
import {delay} from 'rxjs/operators';


describe('GroupManagementContainerComponent', () => {
  let component: GroupManagementContainerComponent;
  let fixture: ComponentFixture<GroupManagementContainerComponent>;
  let sb: GroupManagementSandboxService;
  let userSpy;
  let roleSpy;
  let membershipSpy;
  const auth = {permissions: [{id: 'user.promotion.addButton'}]};

  const dialogMock: any = {
    afterClosed: () => {
      return of(true);
    }
  };


  const DETAILED_USER_INFOS: DetailedUserInfo[] = [
    {
      username: 'Kenyan', email: 'kevin@gmail.com', phoneNumber: '123123',
      membership: {userId: 'Kevin', roleId: 'LMS_ADMIN', groupId: 'Group', membershipId: '123'}
    },
    {
      username: 'Ken', email: 'od@gmail.com', phoneNumber: '123123',
      membership: {userId: 'Ken', roleId: 'LMS_ADMIN', groupId: 'Group', membershipId: '1234'}
    },
    {username: 'John', email: 'john@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Bold', email: 'bold@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Jason', email: 'kevin@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Anu', email: 'anu@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Bat', email: 'kevin@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Dorj', email: 'bold@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Onon', email: 'bold@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Ben', email: 'bold@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Saraa', email: 'bold@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Nina', email: 'kevin@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Nomin', email: 'bold@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Hanu', email: 'kevin@gmail.com', phoneNumber: '123123', membership: null},
    {username: 'Bob', email: 'bold@gmail.com', phoneNumber: '123123', membership: null}
  ];

  const ROLES: Role[] = [
    {roleId: 'LMS_ADMIN', roleName: 'Админ'},
    {roleId: 'LMS_SUPERVISOR', roleName: 'Супер'},
    {roleId: 'LMS_USER', roleName: 'Ажилтан'}
  ];
  const NODES: Node[] = [
    {
      parent: '',
      id: 'Group',
      name: 'Group',
      nthSibling: 0
    },
  ];

  beforeEach(async(() => {
    const aimConfig: AimConfig = {baseUrl: '', tenantId: ''};
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        FormsModule,
        HttpClientTestingModule,
        BrowserDynamicTestingModule,
        MatAutocompleteModule,
        MatChipsModule,
        CommonModule,
        ReactiveFormsModule,
        MatTreeModule,
        DragDropModule,
        MatDialogModule,
        MatInputModule,
        MatMenuModule,
        MatSidenavModule,
        MatButtonModule,
        MatIconModule,
        MatTooltipModule,
        MatProgressSpinnerModule,
        MatSelectModule,
        MatCardModule,
        MatDividerModule,
        BrowserModule,
        MatSnackBarModule,
      ],
      providers: [
        GroupManagementSandboxService,
        {provide: AIM_CONFIG, useValue: aimConfig},
        provideMockStore({initialState: {auth, notification: {courseCategory: new Map()}}})
      ],
      declarations: [
        UserSearchFieldComponent,
        UserCardFilter,
        ErinChipField,
        DropdownComponent,
        ErinLoaderComponent,
        TreeViewContainerComponent,
        UserCardViewComponent,
        GroupManagementContainerComponent],
    }).overrideModule(BrowserDynamicTestingModule, {
      set: {
        entryComponents: [
          ErinLoaderComponent
        ]
      }
    });
  }));

  beforeEach(() => {
    sb = TestBed.inject(GroupManagementSandboxService);
    roleSpy = spyOn(sb, 'getAllRoles').and.returnValue(of(ROLES));
    roleSpy = spyOn(sb, 'getAllGroups').and.returnValue(of(NODES));
    membershipSpy = spyOn(sb, 'createMembership');
    fixture = TestBed.createComponent(GroupManagementContainerComponent);
    component = fixture.componentInstance;
    component.ngOnInit();
    fixture.autoDetectChanges();
  });
  afterEach(() => {
    fixture.destroy();
    component = null;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get all users', () => {
    userSpy = spyOn(sb, 'getAllUsers').and.returnValue(of(DETAILED_USER_INFOS));
    component.ngOnInit();
    expect(sb.getAllUsers).toHaveBeenCalled();
    expect(component.allUsers.length).toEqual(15);
  });
// TODO FIX ME
  it('should add user', () => {
    userSpy = spyOn(sb, 'getAllUsers').and.returnValue(of(DETAILED_USER_INFOS));
    component.ngOnInit();
    component.selectedGroupUsers = [{
      username: 'Kenyan', email: 'kevin@gmail.com', phoneNumber: '123123'
    },
      {
        username: 'Ken', email: 'od@gmail.com', phoneNumber: '123123'
      }];
    expect(component.selectedGroupUsers.length).toEqual(2);
    const membership: Membership = {membershipId: 'id', groupId: 'Group', roleId: 'LMS_ADMIN', userId: 'Dorj'};
    membershipSpy.and.returnValue(of([membership]));
    component.onUserAdded({roleId: 'LMS_ADMIN', users: ['Dorj']});
    fixture.detectChanges();
    expect(component.selectedGroupUsers.length).toEqual(3);
    fixture.detectChanges();
  });
// TODO FIX ME
  it('should remove user', () => {
    component.selectedGroupUsers = [{
      username: 'Kenyan', email: 'kevin@gmail.com', phoneNumber: '123123'
    },
      {
        username: 'Ken', email: 'od@gmail.com', phoneNumber: '123123'
      }];
    fixture.detectChanges();
    expect(component.selectedGroupUsers.length).toEqual(2);
    spyOn(sb, 'deleteMember').and.returnValue(of({deleted: true}));
    spyOn(component.dialog, 'open').and.returnValue(dialogMock);
    const userInfo: UserInfo = {username: 'Ken', email: 'od@gmail.com', phoneNumber: '123123', role: 'Админ'};
    component.onUserRemoved(userInfo);
    fixture.detectChanges();
    for (let i = 0; i < component.selectedGroupUsers.length; i++) {
      expect(component.selectedGroupUsers[i].username === 'Ken').toBeFalsy();
    }
  });

  it('should Check is groupManagementLoaded is true', fakeAsync(() => {
    component.treeLoaded = true;
    component.groupManagementLoaded = false;
    userSpy = spyOn(sb, 'getAllUsers').and.returnValue(of(DETAILED_USER_INFOS).pipe(delay(3000)));
    component.ngOnInit();
    // over created
    expect(false).toEqual(component.groupManagementLoaded);
    tick(4000);
    expect(true).toEqual(component.groupManagementLoaded);
  }));

  it('should Check treeLoaded and groupManagementLoaded is true', fakeAsync(() => {
    component.treeLoaded = false;
    component.groupManagementLoaded = false;
    userSpy = spyOn(sb, 'getAllUsers').and.returnValue(of(DETAILED_USER_INFOS).pipe(delay(3000)));
    component.ngOnInit();
    expect(component.treeLoaded).toEqual(false);
    expect(component.groupManagementLoaded).toEqual(false);
    component.updateTreeLoad(true);
    tick(4000);
    expect(component.treeLoaded).toEqual(true);
    expect(component.groupManagementLoaded).toEqual(true);
  }));

  it('should Check treeLoaded is true', fakeAsync(() => {
    component.treeLoaded = false;
    userSpy = spyOn(sb, 'getAllUsers').and.returnValue(of(DETAILED_USER_INFOS).pipe(delay(3000)));
    component.ngOnInit();
    expect(component.treeLoaded).toEqual(false);
    component.updateTreeLoad(true);
    tick(4000);
    expect(component.treeLoaded).toEqual(true);
  }));
});
