import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TreeViewContainerComponent} from './tree-view-container.component';
import {MatTreeModule} from '@angular/material/tree';
import {MatIconModule} from '@angular/material/icon';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatButtonModule} from '@angular/material/button';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CdkTreeModule} from '@angular/cdk/tree';
import {Node} from '../../models/tree.model';
import {MatMenuModule} from '@angular/material/menu';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {By} from '@angular/platform-browser';
import {GroupManagementSandboxService} from '../../group-management-sandbox.service';
import {of} from 'rxjs';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {provideMockStore} from '@ngrx/store/testing';
import {DragDropModule} from '@angular/cdk/drag-drop';
import {MatTooltipModule} from '@angular/material/tooltip';
import {AIM_CONFIG} from '../../../aim.config.token';
import {ErinLoaderComponent} from '../../../component/loader/loader.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatDialogModule} from '@angular/material/dialog';

describe('TreeViewContainerComponent', () => {
  let fixture: ComponentFixture<TreeViewContainerComponent>;
  let component: TreeViewContainerComponent;
  let sb: GroupManagementSandboxService;
  const aimConfig = {baseUrl: 'base', tenantId: 'tenantId'};
  const auth = {permissions: [{id: 'user.promotion.addButton'}]};

  const dialogMock: any = {
    afterClosed: () => {
      return of(true);
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TreeViewContainerComponent, ErinLoaderComponent],
      imports: [
        BrowserAnimationsModule,
        MatTreeModule,
        MatIconModule,
        MatSidenavModule,
        MatButtonModule,
        CdkTreeModule,
        MatMenuModule,
        ReactiveFormsModule,
        FormsModule,
        MatFormFieldModule,
        HttpClientTestingModule,
        DragDropModule,
        MatTooltipModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatDialogModule
      ],
      providers: [
        GroupManagementSandboxService,
        provideMockStore({initialState: {auth, notification: {courseCategory: new Map()}}}),
        {provide: AIM_CONFIG, useValue: aimConfig},
      ]
    })
      .compileComponents();
  }));
  beforeEach(() => {
    sb = TestBed.inject(GroupManagementSandboxService);
    const TREE_DATA: Node[] = [
      {
        parent: 'root-root',
        id: 'root',
        name: 'root',
        color: 'black',
        nthSibling: 0,
        children: [
          {
            parent: 'root',
            id: '1',
            name: 'Үйлчилгээний алба',
            color: 'green',
            nthSibling: 1,
            children: [{
              parent: '1',
              id: '1-1',
              name: 'ӨХХХ',
              nthSibling: 1
            }],
          }, {
            parent: 'root',
            id: '2',
            name: 'Борлуулалтын алба',
            nthSibling: 2
          }, {
            parent: 'root',
            id: '3',
            name: 'УБҮСХ',
            nthSibling: 3
          }, {
            parent: 'root',
            id: '4',
            name: 'УБГСХ',
            nthSibling: 4
          }
        ]
      }
    ];
    spyOn(sb, 'getAllGroups').and.returnValue(of(TREE_DATA));
    fixture = TestBed.createComponent(TreeViewContainerComponent);
    component = fixture.debugElement.componentInstance;
    component.ngOnInit();
    fixture.detectChanges();
  });
  afterEach(() => {
    fixture.destroy();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should have 5 nodes', () => {
    const nodes = fixture.debugElement.queryAll(By.css('.mat-nested-tree-node'));
    expect(nodes.length).toEqual(5);
  });

  it('should have first color', () => {
    component.ngOnChanges();
    fixture.detectChanges();
    const colors = fixture.debugElement.queryAll(By.css('mat-icon'));
    expect(colors[1].nativeElement.style.color).toBe('rgb(0, 18, 110)');
  });

  it('should input is false', fakeAsync(() => {
    const input = fixture.debugElement.query(By.css('input'));
    expect(input).toBeFalsy();
  }));

  it('should input is true when click add button', fakeAsync(() => {
    const btn = fixture.debugElement.query(By.css('.add-group-button'));
    btn.triggerEventHandler('click', {});
    tick(1000);
    fixture.detectChanges();
    const input = fixture.debugElement.query(By.css('input'));
    expect(input).toBeTruthy();
    btn.triggerEventHandler('keydown', {escape});
  }));

  it('should input is true', fakeAsync(() => {
    component.nodes[0].children.push({parent: '1', id: '1', name: '', nthSibling: 0});
    component.ngOnChanges();
    fixture.detectChanges();
    const input = fixture.debugElement.query(By.css('input'));
    expect(input).toBeTruthy();
    component.nodes[0].children.pop();
  }));

  it('should delete group', () => {
    spyOn(sb, 'deleteGroup').and.returnValue(of(['']));
    spyOn(component.dialog, 'open').and.returnValue(dialogMock);
    component.deleteNode('3');
    for (let i = 1; i < component.nodes[0].children.length; i++) {
      expect(component.nodes[0].children[i].id === '3').toBeFalsy();
    }
  });
  it('should update input field is true', () => {
    component.updateGroupName(component.nodes[0].children[0]);
    component.ngOnChanges();
    fixture.detectChanges();
    const input = fixture.debugElement.queryAll(By.css('input'));
    expect(input.length).toEqual(1);
  });
});
