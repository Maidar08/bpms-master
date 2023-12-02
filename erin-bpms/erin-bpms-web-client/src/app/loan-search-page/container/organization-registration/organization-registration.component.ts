import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {DatePickerWithBranchModel, Group, ORGANIZATION_REGISTRATION_COLUMNS} from '../../../models/app.model';
import {OrganizationRequestModel} from '../../models/process.model';
import {LoanSearchPageSandbox} from '../../loan-search-page-sandbox.service';
import {DialogService} from '../../../services/dialog.service';
import {Overlay} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {ErinLoaderComponent} from '../../../common/erin-loader/erin-loader.component';
import {ActivatedRoute, Router} from '@angular/router';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {NavigationSandboxService} from '../../../app-navigation/navigation-sandbox.service';
import {MatMenuTrigger} from '@angular/material/menu';
import {ErinTableComponent} from '../../../common/erin-table/erin-table/erin-table.component';
import {CONFIRM_DIALOG_BUTTONS} from '../../../branch-banking/model/branch-banking-constant';
import {ErinCustomDialogComponent} from '../../../common/erin-custom-dialog/erin-custom-dialog.component';
import {CaseViewSandboxService} from '../../../case-view-page/case-view-sandbox.service';
import {AuthenticationSandboxService} from '../../../erin-aim/authentication/authentication-sandbox.service';
import {ORGANIZATION} from '../../models/process-constants.model';
import {GroupManagementSandboxService} from "../../../erin-aim/group-management/group-management-sandbox.service";
import {Node} from "../../../erin-aim/group-management/models/tree.model";

@Component({
  selector: 'organization-registration',
  template: `
      <div class="mat-typography margin search-height">
        <div class="search-section">
          <div class="search-left">
            <search-input (searchStr)="search($event)"></search-input>
            <p class="mat-error mat-caption ">{{errorMessage | translate}}</p>
          </div>
          <div class="search-right">
            <date-range-picker-with-branch (selectedValueChangeEvent)="onPickerAndBranchListChange($event)"  [userGroupId]="userGroupId" [branchList]="sortedList" [defaultStart]=this.defaultStart [defaultEnd]=this.defaultEnd></date-range-picker-with-branch>
            <download-excel [searchKey]=this.searchKey [topHeader]="topHeader" [data]="data" [urlHeader]="urlHeader"></download-excel>
          </div>
        </div>
        <erin-table #erinTableComponent
                    [data]="data"
                    [columns]=columns
                    [hasPagination]="true"
                    [hasContextMenu]="true"
                    [topHeader]="title"
                    (rowClickListener)="clickedOnItem($event)"
                    (rowContextClickListener)="contextMenuAction($event)"
                    [groupId]="userGroupId"
        ></erin-table>
      </div>
  `,
  styleUrls: ['./organization-registration.component.scss']
})
export class OrganizationRegistrationComponent implements OnInit {
  @ViewChild('erinTableComponent', {static: false}) private table: ErinTableComponent;
  @ViewChild(MatMenuTrigger, {static: true}) contextMenuTrigger: MatMenuTrigger;
  @Input() completed;

  columns = ORGANIZATION_REGISTRATION_COLUMNS;
  data: OrganizationRequestModel[];
  errorMessage: string;
  searchKey: string;
  title = '';
  topHeader;
  urlHeader = 'loan-requests';

  currentUrl: string;
  textValue;
  userGroupId;
  start: Date;
  end: Date;
  defaultStart: Date;
  defaultEnd = new Date();
  nodes: Node[];
  allGroupIds: Group[] = [];
  sortedList;
  selectedBranchIds = [];

  constructor(
    private sb: LoanSearchPageSandbox,
    private dialogService: DialogService,
    public overlay: Overlay,
    private activatedRoute: ActivatedRoute,
    private navSb: NavigationSandboxService,
    public dialog: MatDialog,
    private caseViewSandboxService: CaseViewSandboxService,
    private router: Router,
    private auth: AuthenticationSandboxService,
    private groupManagementSb: GroupManagementSandboxService
  ) {

    this.currentUrl = this.router.url;

    this.dialogService.processDialogSbj.subscribe(() => {
      this.getOrganizationRequests(this.topHeader, this.defaultStart, this.defaultEnd, this.selectedBranchIds);
    });

    this.activatedRoute.data.subscribe(data => {
      if (null != data) {
        if (null != data.title) {
          this.title = data.title;
        }
        if (null != data.topHeader) {
          this.topHeader = data.topHeader;
        }
        if (null != data.urlHeader) {
          this.urlHeader = data.urlHeader;
        }
      }
    });
  }

  ngOnInit() {
    this.setDefaultDate();
    this.selectedBranchIds = [this.userGroupId];
    this.getGroupId();
    this.getAllGroupIds();
  }

  private getAllGroupIds() {
    this.groupManagementSb.getAllGroups().subscribe((res: Node[]) => {
      this.nodes = res[0].children;
      this.allGroupIds = this.allGroupIds.concat(res.map<Group>(item => {return { groupId: item.id, groupName: item.name}}))
      this.nodes.forEach(group => {
        this.allGroupIds.push({groupName: group.name, groupId: group.id});
        group.children.forEach(subGroup => {
          if (!this.allGroupIds.includes({groupId: subGroup.id, groupName: subGroup.name})) {
            this.allGroupIds.push({groupId: subGroup.id, groupName: subGroup.name})
          }
        })
      })
      this.allGroupIds.sort(compare);
      this.sortedList = this.allGroupIds.filter((v,i,a)=>a.findIndex(v2=>(JSON.stringify(v2) === JSON.stringify(v)))===i)
    })

    function compare(a, b) {
      if (a.groupId < b.groupId) {
        return -1;
      }
      if (a.groupId > b.groupId) {
        return 1;
      }
      return 0
    }
  }

  clickedOnItem(model: OrganizationRequestModel) {
    if (this.currentUrl.includes('salary')) {
      this.sb.routeToSalaryOrganizationCaseView(model.instanceId, {requestState: model.state}, 'salary-organization');
    } else {
      this.sb.routeToLeasingOrganizationCaseView(model.instanceId, {requestState: model.state}, 'leasing-organization');
    }
  }

  search(key: string) {
    this.searchKey = key;
    this.table.filter(key);
  }

  getOrganizationRequests(topHeader: string, startDate: Date, endDate: Date, branchId: any) {
    const overlayRef = this.setOverlay();
    this.sb.getOrganizationRequests(topHeader, startDate.toDateString(), endDate.toDateString(), branchId).subscribe(res => {
      if (res) {
        this.data = res;
        overlayRef.dispose();
      }
    }, error => {
      overlayRef.dispose();
      this.errorMessage = error.error;
    });
  }


  private setOverlay() {
    const overlayRef = this.overlay.create({
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true
    });
    overlayRef.attach(new ComponentPortal(ErinLoaderComponent));
    return overlayRef;
  }

  contextMenuAction(data) {
    const overlayRef = this.setOverlay();
    this.dialogTextConfig(data);
    const config = this.setDialogConfig(CONFIRM_DIALOG_BUTTONS, ('Байгууллагын гэрээг ' + this.textValue + ' итгэлтэй байна уу?'), '400px');
    this.dialogService.openCustomDialog(ErinCustomDialogComponent, config).then((res: any) => {
        if (null != res && null != res.action && res.action.actionId === 'confirm') {
          switch (data.action) {
            case 'cancel':
              this.cancelOrganizationRequest(data.row.id, data.row.instanceId, data.action);
              break;
            case 'edit':
              const requestType = this.currentUrl.includes('salary') ? 'salary-organization' : 'leasing-organization';
              if (requestType === 'salary-organization') {
                this.sb.routeToSalaryOrganizationCaseView(data.row.instanceId, {requestState: data.row.state}, 'edit');
              } else {
                this.sb.routeToLeasingOrganizationCaseView(data.row.instanceId, {requestState: data.row.state}, 'edit');
              }
              break;
            case 'extend':
              const requestType2 = this.currentUrl.includes('salary') ? 'salaryOrganization' : 'leasingOrganization';
              const overlayRef1 = this.setOverlay();
              this.sb.createRequest(requestType2, [], ORGANIZATION, data.row.instanceId, data.row.id, data.row.organizationNumber).subscribe(res1 => {
                if (requestType2 === 'salaryOrganization') {
                  this.sb.routeToSalaryOrganizationCaseView(res1, {requestState: data.row.state}, 'extend');
                  overlayRef1.dispose();
                } else {
                  this.sb.routeToLeasingOrganizationCaseView(res1, {requestState: data.row.state}, 'extend');
                  overlayRef1.dispose();
                }
              });
              break;
            default:
              break;
          }
          overlayRef.dispose();
        } else {
          overlayRef.dispose();
        }
      }
    );
  }

  dialogTextConfig(data) {
    if (data.action === 'cancel') {
      this.textValue = 'цуцлахдаа';
    } else if (data.action === 'edit') {
      this.textValue = 'засахдаа';
    } else if (data.action === 'extend') {
      this.textValue = 'сунгахдаа';
    }
  }

  private cancelOrganizationRequest(contractId, instanceId, action) {
    const overlayRef = this.setOverlay();
    const processType = this.currentUrl.includes('leasing-organization-request') ? 'leasingOrganization' : 'salaryOrganization';
    this.caseViewSandboxService.updateOrganizationRequest(contractId, instanceId, processType, action).subscribe(res => {
      if (res) {
        this.getOrganizationRequests(this.topHeader, this.defaultStart, this.defaultEnd, this.selectedBranchIds);
      }
      overlayRef.dispose();
    });
    overlayRef.dispose();
  }

  private setDialogConfig(buttons, message, width): MatDialogConfig {
    const dialogData = {buttons, message};
    const config = new MatDialogConfig();
    config.width = width;
    config.data = dialogData;
    return config;
  }

  getGroupId() {
    return this.auth.getCurrentUserGroup().subscribe(res => this.userGroupId = res);
  }

  onPickerAndBranchListChange(change: DatePickerWithBranchModel): void{
    [this.start, this.end] = change.date;
    this.selectedBranchIds = change.selectedBranchList;
    if(this.start && this.end && this.selectedBranchIds.length>0) {
      this.getOrganizationRequests(this.topHeader, this.start, this.end, this.selectedBranchIds);
    }else {
      this.data = [];
    }
  }

  setDefaultDate(): void {
    const today = new Date('January 1, 2000 00:00:00');
    this.defaultStart = today;
  }
}
