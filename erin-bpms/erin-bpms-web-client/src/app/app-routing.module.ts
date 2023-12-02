import {Routes} from '@angular/router';
import {LoginContainerComponent} from './authentication/container/login-container/login-container.component';
import {AppFrameComponent} from './app-navigation/container/app-frame/app-frame.component';
import {CaseViewComponent} from './case-view-page/container/case-view/case-view.component';
import {MyLoanRequestTableComponent} from './loan-search-page/container/my-loan-request-table/my-loan-request-table.component';
import {SalaryTableComponent} from './case-view-page/container/salary-table/salary-table.component';
import {GroupManagementContainerComponent} from './erin-aim/group-management/containers/group-management-container/group-management-container.component';
import {BranchRequestPageComponent} from './loan-search-page/container/branch-request-page/branch-request-page.component';
import {AllRequestPageComponent} from './loan-search-page/container/all-request-page/all-request-page.component';
import {RouteGuardService} from './erin-aim/authentication/services/route-guard.service';
import {EbankRequestPageComponent} from './loan-search-page/container/ebank-request-page/ebank-request-page.component';
import {DashboardComponent} from './loan-search-page/component/dashboard/dashboard.component';
import {NotFoundComponent} from './not-found/not-found.component';
import {SubGroupRequestPageComponent} from './loan-search-page/container/sub-group-request-page/sub-group-request-page.component';
import {FileViewerComponent} from './common/file-viewer/file-viewer.component';
import {HomePageComponent} from './home-page/home-page.component';
import {SearchCustomerComponent} from './loan-search-page/container/search-customer/search-customer.component';
import {OrganizationRegistrationComponent} from './loan-search-page/container/organization-registration/organization-registration.component';
import {BranchBankingCaseViewComponent} from './branch-banking/contrainer/branch-banking-case-view/branch-banking-case-view.component';
import {BranchBankingInitialViewComponent} from './branch-banking/contrainer/branch-banking-initial-view/branch-banking-initial-view.component';
import {LoanContractPageComponent} from './loan-search-page/container/loan-contract-page/loan-contract-page.component';
import {LoanContractCaseViewComponent} from './loan-contract/container/loan-contract-case-view/loan-contract-case-view.component';
import {LOANCONTRACT, ORGANIZATION} from './loan-search-page/models/process-constants.model';
import {UserManualComponent} from './authentication/component/login-card/user-manual/user-manual.component';
import {
  OrganizationRegistrationCaseViewComponent
} from './organization-registration/container/organization-registration-case-view/organization-registration-case-view.component';


export const routes: Routes = [
  {
    path: 'home-page',
    component: HomePageComponent,
    data: {title: 'Нүүр Хуудас'}
  },
  {
    path: 'login',
    component: LoginContainerComponent,
    data: {title: 'Нэвтрэх'}
  },
  {
    path: '',
    redirectTo: 'home-page',
    pathMatch: 'full'
  },
  {
    path: 'user-manual',
    component: UserManualComponent,
    data: {title: 'BPMS Хэрэглэгчийн гарын авлага'}
  },
  {
    path: 'loan-page',
    component: AppFrameComponent,
    canActivateChild: [RouteGuardService],
    canActivate: [RouteGuardService],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: DashboardComponent,
        data: {
          title: 'Хяналтын самбар',
        },
        children: [
          // Loan request
          {
            path: 'all-request',
            component: AllRequestPageComponent,
            data: {
              title: 'Бүх зээлийн хүсэлт',
              id: 'bpms.bpm.GetAllProcessRequests',
              urlHeader: 'loan-requests',
              requestType: 'all-request'
            }
          },
          {
            path: 'my-loan-request',
            component: MyLoanRequestTableComponent,
            data: {
              title: 'Миний зээлийн хүсэлт',
              id: 'bpms.bpm.GetProcessRequestsByAssignedUserId',
              urlHeader: 'loan-requests',
              requestType: 'my-loan-request'

            }
          },
          {
            path: 'branch-loan-request',
            component: BranchRequestPageComponent,
            data: {
              title: 'Салбарын хүсэлт',
              id: 'bpms.bpm.GetGroupProcessRequests',
              urlHeader: 'loan-requests',
              requestType: '"branch-loan-request'
            },
          },
          {
            path: 'sub-group-request',
            component: SubGroupRequestPageComponent,
            data: {
              title: 'Салбарын хүсэлт',
              id: 'bpms.bpm.GetSubGroupProcessRequests',
              urlHeader: 'loan-requests',
              requestType: 'sub-group-request'
            },
          },
          {
            path: 'ebank-request',
            component: EbankRequestPageComponent,
            data: {
              title: 'Интернет банкны хүсэлт',
              id: 'bpms.bpm.GetUnassignedRequestsByChannel',
              urlHeader: 'loan-requests',
              requestType: 'ebank-request'
            },
          },
          {
            path: 'search-customer',
            component: SearchCustomerComponent,
            data: {
              title: 'Харилцагч хайх',
              id: 'bpms.bpm.SearchByRegisterNumber',
              urlHeader: 'loan-requests',
              requestType: 'search-customer'
            },
          },
          // Organization registration request
          {
            path: 'salary-organization-request',
            component: OrganizationRegistrationComponent,
            data: {
              title: 'ЦАЛИНГИЙН БАЙГУУЛЛАГА',
              topHeader: 'salary',
              urlHeader: 'organization-requests'
            }
          },
          {
            path: 'leasing-organization-request',
            component: OrganizationRegistrationComponent,
            data: {
              title: 'ЛИЗИНГИЙН БАЙГУУЛЛАГА',
              topHeader: 'leasing',
              urlHeader: 'organization-requests'
            }
          },
          {path: '404', component: NotFoundComponent},
          {path: '**', redirectTo: 'dashboard'},
        ]
      },
      {
        path: 'case-view/:id/:isReadOnlyReq/:noHeaderSection',
        component: CaseViewComponent,
        data: {title: 'Кэйс ажлын талбар'}
      },
      {
        path: 'case-view/:id/:isReadOnlyReq',
        component: CaseViewComponent,
        data: {title: 'Кэйс ажлын талбар'}
      },
      {
        path: 'salary-table',
        component: SalaryTableComponent,
        data: {title: 'Зээлийн хүснэгт'}
      },
      {
        path: 'file-viewer/:instanceId/:id/:source/:download/:fileName/:enabledReq',
        component: FileViewerComponent,
        data: {title: 'Файл харах'}
      },
      {
        path: 'file-viewer/:instanceId/:fileName/:download',
        component: FileViewerComponent,
        data: {title: 'Файл харах'}
      },
      {
        path: 'group',
        component: GroupManagementContainerComponent,
        data: {title: 'Групп тохиргоо', id: '*'}
      },
      {
        path: 'branch-banking',
        component: BranchBankingInitialViewComponent,
      },
      {
        path: 'branch-banking/:id',
        component: BranchBankingCaseViewComponent,
      },
      {
        path: 'branch-banking/:id/:isReadOnlyReq',
        component: BranchBankingCaseViewComponent,
      },
      {
        path: 'loan-contract',
        component: LoanContractPageComponent,
        data: {
          title: 'БҮХ ЗЭЭЛИЙН ГЭРЭЭ',
          urlHeader: 'all-loan-contract',
          requestType: 'all-request',
          topHeader: 'all-loan-contract'
        }
      },
      {
        path: 'loan-contract/all-loan-contract',
        component: LoanContractPageComponent,
        data: {
          title: 'БҮХ ЗЭЭЛИЙН ГЭРЭЭ',
          urlHeader: 'all-loan-contract',
          requestType: 'all-request',
          topHeader: 'all-loan-contract'
        }
      },
      {
        path: 'loan-contract/branch-loan-contract',
        component: LoanContractPageComponent,
        data: {
          title: 'САЛБАРЫН ЗЭЭЛИЙН ГЭРЭЭ                                                                                             ',
          urlHeader: 'group-loan-contract',
          requestType: 'group-request',
          topHeader: 'group-loan-contract'
        }
      },
      {
        path: 'loan-contract/sub-branch-loan-contract',
        component: LoanContractPageComponent,
        data: {
          title: 'САЛБАРЫН ЗЭЭЛИЙН ГЭРЭЭ                                                                                             ',
          urlHeader: 'sub-group-loan-contract',
          requestType: 'sub-group-request',
          topHeader: 'sub-group-loan-contract'
        }
      },
      {
        path: 'loan-contract/case-view/:id',
        component: LoanContractCaseViewComponent,
        data: {
          requestType: LOANCONTRACT
        }
      },
      {
        path: 'salary-organization/case-view/:id',
        component: OrganizationRegistrationCaseViewComponent,
        data: {
          requestType: ORGANIZATION
        }
      },
      {
        path: 'leasing-organization/case-view/:id',
        component: OrganizationRegistrationCaseViewComponent,
        data: {
          requestType: ORGANIZATION
        }
      },
      {path: '404', component: NotFoundComponent},
      {path: '**', redirectTo: '404'},
    ]
  },
];
