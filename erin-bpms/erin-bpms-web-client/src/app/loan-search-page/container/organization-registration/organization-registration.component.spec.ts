import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {OrganizationRegistrationComponent} from './organization-registration.component';
import {HttpClientModule} from "@angular/common/http";
import {MaterialModule} from "../../../material.module";
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {StoreModule} from "@ngrx/store";
import {ActivatedRoute, Data, RouterModule} from "@angular/router";
import {LoanSearchPageSandbox} from "../../loan-search-page-sandbox.service";
import {of} from "rxjs";
import {NavigationSandboxService} from "../../../app-navigation/navigation-sandbox.service";
import {RequestModel} from "../../models/process.model";
import {BrowserAnimationsModule, NoopAnimationsModule} from "@angular/platform-browser/animations";
import {MAT_DIALOG_DATA, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {ConfirmDialogComponent} from "../../../common/confirm-dialog/confirm-dialog.component";

let activatedRoute: ActivatedRoute;

describe('OrganizationRegistrationComponent', () => {
  class MockNavigationSandbox {

    private userName = 'Username';
    getUserName() {
      return this.userName;
    }

    setUserName(value: string) {
      this.userName = value;
    }
  }

  let dialog;
  const dialogMock = {
    close: () => {
    },
    disableClose: false
  };
  const data = {
    taskName: '',
    message: ' Та 65454323 дугаартай хүсэлт дээр ажиллахдаа итгэлтэй байна уу? Энэ хүсэлт дээр хэрэглэгч ажиллаж байна',
    confirmButton: 'yes',
    closeButton: 'no',
    title: 'test title',
    hasDivider: true,
  };

  const mockNavigationSb = new MockNavigationSandbox();
  let component: OrganizationRegistrationComponent;
  let fixture: ComponentFixture<OrganizationRegistrationComponent>;
  let requestService: LoanSearchPageSandbox;
  let getRequestSpy;
  let navSb: NavigationSandboxService;

  const requestModel: RequestModel[] = [
    {
      id: '123', fullName: '123', registerNumber: '123', cifNumber: '123', instanceId: '123', phoneNumber: 123,
      email: '123', createdDate: '123', productCode: '', amount: '', branchNumber: '', channel: '',
      assignee: '123', userName: '', state: ''
    },
    {
      id: '321', fullName: '321', registerNumber: '321', cifNumber: '321', instanceId: '321', phoneNumber: 321,
      email: '321', createdDate: '321', productCode: '', amount: '', branchNumber: '', channel: '',
      assignee: '321', userName: '', state: ''
    }
  ];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OrganizationRegistrationComponent ],
      imports: [
        HttpClientModule,
        MaterialModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
        StoreModule.forRoot({}),
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useClass: TranslateFakeLoader
          }
        }),
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: {
              subscribe: (fn: (value: Data) => void) => fn({
                title: 'title',
              }),
            },
          }
        },
        {provide: MatDialogRef, useValue: {dialogMock}},
        {provide: MAT_DIALOG_DATA, useValue: (data)},
        {provide: NavigationSandboxService, useValue: mockNavigationSb},
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganizationRegistrationComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    component = fixture.componentInstance;

    requestService = TestBed.inject(LoanSearchPageSandbox);
    getRequestSpy = spyOn(requestService, 'getOrganizationRequests');
    getRequestSpy.and.returnValue(of(requestModel));

    navSb = TestBed.inject(NavigationSandboxService);
    spyOn(navSb, 'getUserName').and.returnValue('testUser');

    dialog = component.dialog;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get title data from route',  () => {
    expect(component.title).toBe('title');
  });

  it('should get requests', () => {
    component.ngOnInit();
    expect(requestService.getOrganizationRequests).toHaveBeenCalled();
  });

  it('should have table with data', () => {
    component.ngOnInit();
    fixture.detectChanges();
    expect(component.data.length).toEqual(2);
  });

  it('should show dialog when click on task', () => {
    spyOn(component.dialog, 'open').and.callThrough();
    component.clickedOnItem(requestModel[0], false);
    component.dialog.closeAll();
    const config = new MatDialogConfig();
    config.disableClose = false;
    config.data = {
      taskName: '',
      confirmButton: 'Тийм',
      closeButton: 'Үгүй',
      message: 'Та  "123"  дугаартай хүсэлт дээр ажиллахдаа итгэлтэй байна уу? Энэ хүсэлт дээр "123" хэрэглэгч ажиллаж байна ',
      title: "Хүсэлтийг өөртөө шилжүүлэх",
      hasDivider: true,
    };
    config.width= '500px';
    fixture.detectChanges();
    expect(component.dialog.open).toHaveBeenCalledWith(ConfirmDialogComponent, config);
  });
});
