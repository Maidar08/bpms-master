import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {UserCardViewComponent} from './user-card-view.component';
import {By} from '@angular/platform-browser';
import {UserInfo} from '../../models/group-management.model';
import {UserCardFilter} from './filter/user-card-filter.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UserSearchFieldComponent} from '../search/user-search-field.component';
import {GroupManagementSandboxService} from '../../group-management-sandbox.service';
import {SimpleChange} from '@angular/core';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDividerModule} from '@angular/material/divider';
import {MatInputModule} from '@angular/material/input';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSelectModule} from '@angular/material/select';
import {DropdownComponent} from '../../../component/dropdown/dropdown.component';
import {ErinChipField} from '../../../component/erin-chip-field/erin-chip-field.component';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {ErinLoaderComponent} from '../../../component/loader/loader.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {DragDropModule} from '@angular/cdk/drag-drop';
import {MatMenuModule} from '@angular/material/menu';
import {AIM_CONFIG} from '../../../aim.config.token';
import {provideMockStore} from '@ngrx/store/testing';


describe('UserCardViewComponent', () => {
  let component: UserCardViewComponent;
  let fixture: ComponentFixture<UserCardViewComponent>;
  let sb: GroupManagementSandboxService;
  const aimConfig = {baseUrl: 'base', tenantId: 'tenantId'};
  const roles = [{roleId: '123', roleName: 'Ажилтан'}, {roleId: '321', roleName: 'Админ'}, {roleId: '654', roleName: 'Супер'}];
  const TABLE_DATA: UserInfo[] = [
    {username: 'Kevin', email: 'kevin@gmail.com', role: 'Супер', phoneNumber: '123123'},
    {username: 'Ken', email: 'od@gmail.com', role: 'Ажилтан', phoneNumber: '123123'},
    {username: 'John', email: 'john@gmail.com', role: 'Админ', phoneNumber: '123123'},
    {username: 'Bold', email: 'bold@gmail.com', role: 'Супер', phoneNumber: '123123'},
    {username: 'Jason', email: 'kevin@gmail.com', role: 'Супер', phoneNumber: '123123'},
    {username: 'Anu', email: 'anu@gmail.com', role: 'Супер', phoneNumber: '123123'},
    {username: 'Bat', email: 'kevin@gmail.com', role: 'Ажилтан', phoneNumber: '123123'},
    {username: 'Dorj', email: 'bold@gmail.com', role: 'Ажилтан', phoneNumber: '123123'},
    {username: 'Onon', email: 'bold@gmail.com', role: 'Админ', phoneNumber: '123123'},
    {username: 'Ben', email: 'bold@gmail.com', role: 'Супер', phoneNumber: '123123'},
    {username: 'Saraa', email: 'bold@gmail.com', role: 'Супер', phoneNumber: '123123'},
    {username: 'Nina', email: 'kevin@gmail.com', role: 'Ажилтан', phoneNumber: '123123'},
    {username: 'Nomin', email: 'bold@gmail.com', role: 'Ажилтан', phoneNumber: '123123'},
    {username: 'Hanu', email: 'kevin@gmail.com', role: 'Ажилтан', phoneNumber: '123123'},
    {username: 'Bob', email: 'bold@gmail.com', role: 'Ажилтан', phoneNumber: '123123'}
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule,
        MatChipsModule,
        MatFormFieldModule,
        MatDividerModule,
        MatInputModule,
        MatPaginatorModule,
        MatSelectModule,
        MatTooltipModule,
        MatAutocompleteModule,
        MatProgressSpinnerModule,
        HttpClientTestingModule,
        MatSnackBarModule,
        DragDropModule,
        MatMenuModule,
        BrowserAnimationsModule
      ],
      declarations: [
        UserCardViewComponent,
        UserCardFilter,
        ErinLoaderComponent,
        UserSearchFieldComponent,
        DropdownComponent,
        ErinChipField],
      providers: [
        GroupManagementSandboxService,
        {provide: AIM_CONFIG, useValue: aimConfig},
        provideMockStore({initialState: {auth: {permissions: [{id: 'user.promotion.addButton'}]}}})]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserCardViewComponent);
    sb = TestBed.inject(GroupManagementSandboxService);
    spyOn(sb, 'deleteMember');
    component = fixture.componentInstance;
    component.data = TABLE_DATA;
    component.roles = roles;
    component.ngOnChanges({data: new SimpleChange(null, '', false), roles: new SimpleChange(null, '', false)});
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have users', () => {
    const user = fixture.debugElement.queryAll(By.css('.each-user'));
    expect(user.length).toEqual(15);
  });

  it('user name should be equal', () => {
    const username = fixture.debugElement.queryAll((By.css('.mat-h3')));
    expect(username[0].nativeElement.innerText).toEqual('Kevin');
  });

  it('each user should have icon', () => {
    const icon = fixture.debugElement.queryAll((By.css('.card-icon')));
    expect(icon.length).toEqual(15);
  });

  it(' after filter user should be equal', () => {
    const filter = fixture.debugElement.query((By.css('user-card-filter')));
    filter.triggerEventHandler('change', 'k');

    const user = fixture.debugElement.queryAll((By.css('.mat-h3')));
    expect(user.length).toEqual(2);
    expect(user[1].nativeElement.innerText).toEqual('Ken');
  });

  it('after filter there will be no user', () => {
    const filter = fixture.debugElement.query((By.css('user-card-filter')));
    filter.triggerEventHandler('change', 'q');

    const user = fixture.debugElement.queryAll((By.css('.mat-h3')));
    expect(user.length).toEqual(0);
  });

  it('after filter there will be all user', () => {
    const filter = fixture.debugElement.query((By.css('user-card-filter')));
    filter.triggerEventHandler('change', '');

    const user = fixture.debugElement.queryAll((By.css('.mat-h3')));
    expect(user.length).toEqual(15);
  });

  it('only employee should be displayed', () => {
    component.onChangeRole('Ажилтан');
    fixture.detectChanges();
    const user = fixture.debugElement.queryAll(By.css('.mat-h3'));
    expect(user.length).toEqual(7);
  });

  it('all users should be displayed', () => {
    component.onChangeRole('Бүгд');
    const user = fixture.debugElement.queryAll(By.css('.mat-h3'));
    expect(user.length).toEqual(15);
  });
});
