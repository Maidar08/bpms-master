import {ChangeDetectorRef, Component, EventEmitter, HostListener, OnChanges, OnInit, Output} from '@angular/core';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {NestedTreeControl} from '@angular/cdk/tree';
import {NewNode, Node, NodeColor} from '../../models/tree.model';
import {GroupManagementSandboxService} from '../../group-management-sandbox.service';
import {MatDialog} from '@angular/material/dialog';
import {CdkDragDrop} from '@angular/cdk/drag-drop';
import {ConfirmDialogComponent} from '../../../component/confirm-dialog/confirm-dialog.component';

const COLOR_DATA: NodeColor = {
  value: '#00126E',
  color: {
    value: '#008265',
    color: {
      value: '#FF8F00',
      color: {
        value: '#60a2e1',
        color: {
          value: '#e6777b',
          color: {
            value: 'gray',
            color: {
              value: 'gray',
              color: {
                value: 'gray',
                color: {
                  value: 'gray',
                  color: {
                    value: 'gray',
                    color: {
                      value: 'gray'
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
};

@Component({
  selector: 'tree-view',
  template: `
    <mat-drawer-container class="container">
      <div class="tree-actions">
        <button mat-flat-button class="root-name" [ngClass]="this.selected === this.root ? 'root-selected' : ''"
                (click)="onRootClicked()"> {{rootName}} </button>
        <button mat-icon-button (click)="addFirstGroup()" class="add-group-button">
          <mat-icon color="primary">add_box</mat-icon>
        </button>
      </div>

      <mat-tree [dataSource]="dataSource" [treeControl]="treeControl" class="tree">

        <!--Has no child nodes started here-->

        <mat-nested-tree-node *matTreeNodeDef="let node" matTreeNodePadding>
          <div
            *ngIf="!node.editable; else editableField"
            class="mat-tree-node"
            (click)="this.onNodeClicked(node)"
            [ngClass]="isActive(node) ? 'active node-activated' : 'node'">
            <button mat-button disabled></button>
            <div class="nochild-node-size">
              <button
                *ngIf='this.loadingNodeId !== node.id'
                matTreeNodeToggle
                class="node-name-button">
                <mat-icon
                  (mouseleave)="onLeft($event, treeControl.isExpanded(node) ? 'expand_more' : 'keyboard_arrow_right')"
                  [ngStyle]="{'visibility': 'hidden', marginRight: '4px'}"
                  (mouseover)="onHover($event, treeControl.isExpanded(node) ? 'expand_more' : 'keyboard_arrow_right')">
                  {{treeControl.isExpanded(node) ? 'expand_more' : 'keyboard_arrow_right'}}
                </mat-icon>
              </button>
              <div cdkDropList [cdkDropListData]="nodes" class="node-name" (cdkDropListDropped)="drop($event)" [id]="node.id"
                   [matTooltip]="node.name">
                {{node.name}}
              </div>
            </div>

            <div *ngIf="this.loadingNodeId === node.id">
              <erin-loader [loading]="loading" [hasShade]="false" [isPageLoader]="false"></erin-loader>
            </div>

            <div *ngIf="this.loadingNodeId !== node.id">
              <button mat-icon-button class="add" [matMenuTriggerFor]="add">
                <mat-icon class="more-button" (mouseleave)="onLeft($event, 'more_vert')" [ngStyle]="{'color': node.color}"
                          (mouseover)="onHover($event, 'more_vert')">
                  <mat-menu #add="matMenu">
                    <button mat-menu-item (click)="saveNode(node)">
                      <i class="material-icons" [ngStyle]="{'color': node.color}">add_box</i>
                      Нэмэх
                    </button>
                    <button mat-menu-item (click)="updateGroupName(node)">
                      <i class="material-icons" [ngStyle]="{'color': node.color}">edit</i>
                      Засварлах
                    </button>
                    <button mat-menu-item (click)="deleteNode(node.id)">
                      <i class="material-icons" [ngStyle]="{'color': node.color}">delete</i>
                      Устгах
                    </button>
                  </mat-menu>
                  {{treeControl.dataNodes ? 'more_vert' : 'more_vert'}}
                </mat-icon>
              </button>
            </div>
          </div>

          <ng-template #editableField>
            <div class="edit-input-style">
              <i class="material-icons" [ngStyle]="{'color': node.color}" style="line-height: 1">create</i>
              <input matInput [(ngModel)]="editNodeName" maxlength="30">
              <erin-loader [loading]="loading" [hasShade]="false" [isPageLoader]="false"></erin-loader>
            </div>
            <mat-error>{{ERROR_MSG}}</mat-error>
          </ng-template>

        </mat-nested-tree-node>

        <!-- Has child nodes started here       -->

        <mat-nested-tree-node *matTreeNodeDef="let node; when: hasNoContent" matTreeNodePadding>
          <div class="input-style">
            <i class="material-icons" [ngStyle]="{'color': node.color}" style="line-height: 1">create</i>
            <input matInput [(ngModel)]="groupName" pattern="" (ngModelChange)="clearErrorMsg()" minlength="3" maxlength="30"
                   placeholder="Группийн нэр">
            <erin-loader [loading]="loading" [hasShade]="false" [isPageLoader]="false"></erin-loader>
          </div>
          <mat-error>{{ERROR_MSG}}</mat-error>
        </mat-nested-tree-node>
        <mat-nested-tree-node *matTreeNodeDef="let node; when: hasNoChild" matTreeNodePadding>
          <div class="input-style">
            <i class="material-icons" [ngStyle]="{'color': node.color}" style="line-height: 1">create</i>
            <input matInput [(ngModel)]="groupName" minlength="3" maxlength="30" placeholder="Группийн нэр">
            <erin-loader [loading]="loading" [hasShade]="false" [isPageLoader]="false"></erin-loader>
          </div>
          <mat-error>{{ERROR_MSG}}</mat-error>
        </mat-nested-tree-node>
        <mat-nested-tree-node *matTreeNodeDef="let node; when: hasChild">
          <div *ngIf="!node.editable; else editableField" class="has-child" [ngClass]="isActive(node) ? 'active' : ''">
            <button [attr.aria-label]="node.name" class="button-size" (click)="this.onNodeClicked(node)">
              <div class="mat-tree-node" [ngClass]="isActive(node) ? MAT_TREE_NODE_WITHOUT_HOVER : MAT_TREE_NODE">
                <button *ngIf='this.loadingNodeId !== node.id' matTreeNodeToggle class="node-name-button">
                  <mat-icon
                    (mouseleave)="onLeft($event, treeControl.isExpanded(node) ? 'expand_more' : 'keyboard_arrow_right')"
                    [ngStyle]="{'color': node.color, marginRight: '4px'}"
                    (mouseover)="onHover($event, treeControl.isExpanded(node) ? 'expand_more' : 'keyboard_arrow_right')">
                    {{treeControl.isExpanded(node) ? 'expand_more' : 'keyboard_arrow_right'}}
                  </mat-icon>
                </button>
                <div cdkDropList [cdkDropListData]="nodes" class="node-name" (cdkDropListDropped)="drop($event)" [id]="node.id"
                     [matTooltip]="node.name">
                  {{node.name}}
                </div>
              </div>
            </button>
            <div *ngIf="this.loadingNodeId === node.id">
              <erin-loader [loading]="loading" [hasShade]="false" [isPageLoader]="false"></erin-loader>
            </div>
            <button mat-icon-button class="add" *ngIf='this.loadingNodeId !== node.id' [matMenuTriggerFor]="add"
                    [disabled]="!treeControl.isExpanded(node)"
                    [ngStyle]="{'visibility':!treeControl.isExpanded(node)? 'hidden' : 'visible' }">
              <mat-icon (mouseleave)="onLeft ($event, 'more_vert')" class="more-button" [ngStyle]="{'color': node.color}"
                        (mouseover)="onHover($event, treeControl.isExpanded(node) ? 'more_vert' : 'more_vert')">
                {{treeControl.isExpanded(node) ? 'more_vert' : 'more_vert'}}
                <mat-menu #add="matMenu">
                  <button mat-menu-item (click)="addNewItem(node)">
                    <i class="material-icons" [ngStyle]="{'color': node.color}">add_box</i>
                    Нэмэх
                  </button>
                  <button mat-menu-item (click)="updateGroupName(node)">
                    <i class="material-icons" [ngStyle]="{'color': node.color}">edit</i>
                    Засварлах
                  </button>
                  <button mat-menu-item (click)="deleteNode(node.id)">
                    <i class="material-icons" [ngStyle]="{'color': node.color}">delete</i>
                    Устгах
                  </button>
                </mat-menu>
              </mat-icon>
            </button>
          </div>

          <ng-template #editableField>
            <div class="edit-input-style">
              <i class="material-icons" [ngStyle]="{'color': node.color}" style="line-height: 1">create</i>
              <input matInput [(ngModel)]="editNodeName" maxlength="30">
              <erin-loader [loading]="loading" [hasShade]="false" [isPageLoader]="false"></erin-loader>
            </div>
            <mat-error>{{ERROR_MSG}}</mat-error>
          </ng-template>


          <ul [class.tree-invisible]="!treeControl.isExpanded(node)">
            <ng-container matTreeNodeOutlet></ng-container>
          </ul>
        </mat-nested-tree-node>
      </mat-tree>
    </mat-drawer-container>
    <erin-loader [loading]="pageLoading" [hasShade]="false" [isPageLoader]="true"></erin-loader>
  `,
  styleUrls: ['./tree-view-container.component.scss']
})
export class TreeViewContainerComponent implements OnInit, OnChanges {
  @Output() loaded: EventEmitter<string> = new EventEmitter<string>();
  @Output() selectionChange: EventEmitter<Node> = new EventEmitter<Node>();
  @Output() dragAndDrop = new EventEmitter<any>();
  @Output() groupDeleted = new EventEmitter<string[]>();
  @Output() treeLoad = new EventEmitter<boolean>();

  root: Node;
  nodes: Node[];
  color = COLOR_DATA;
  groupName;
  rootName;
  currentNode: Node = null;
  treeControl = new NestedTreeControl<Node>(node => node.children);
  dataSource = new MatTreeNestedDataSource<Node>();
  loading: boolean;
  pageLoading: boolean;
  selected: any;
  MAT_TREE_NODE = 'node';
  MAT_TREE_NODE_WITHOUT_HOVER = 'node-activated';
  ERROR_MSG: string;
  editNodeName: string;
  loadingNodeId: string;
  isDuplicated: boolean;

  constructor(public dialog: MatDialog, private cd: ChangeDetectorRef, private sb: GroupManagementSandboxService) {
  }

  hasNoContent = (_: number, _nodeData: Node) => _nodeData.name === '' || _nodeData.children === [];
  hasNoChild = (_: number, node: Node) => node.children === null;
  hasChild = (_: number, node: Node) => !!node.children && node.children.length > 0;

  ngOnInit(): void {
    this.treeLoad.emit(false);
    this.sb.getAllGroups().subscribe((res: Node[]) => {
      this.rootName = res[0].name;
      this.root = res[0];
      this.nodes = res[0].children;
      this.pageLoading = false;
      this.updateTreeView();
      this.selected = this.root;
      this.selectionChange.emit(this.selected);
      this.loaded.emit(this.root.id);
      this.treeLoad.emit(true);
    }, () => {
      this.treeLoad.emit(true);
      this.pageLoading = false;
    });
  }

  ngOnChanges(): void {
    this.updateTreeView();
  }

  @HostListener('document: keydown', ['$event'])
  onKeydownHandler(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      if (this.groupName != null && (this.groupName.trim() === '' || this.groupName.length < 3)) {
        this.ERROR_MSG = '3-с дээш тэмдэгт оруулна уу';
      } else {
        this.addOrRenameGroup();
      }
    } else if (event.key === 'Escape') {
      this.deleteEmptyNode();
    }
  }

  private clearInputVariables() {
    this.groupName = null;
    this.currentNode = null;
  }

  addOrRenameGroup() {
    if (this.currentNode.editable) {
      this.callRenameNode();
    } else {
      this.callAddGroup();
    }
  }

  callAddGroup() {
    this.isDuplicated = false;
    if (this.groupName === undefined) {
      this.ERROR_MSG = '3-с дээш тэмдэгт оруулна уу';
      return;
    } else {
      this.ERROR_MSG = '';
      const node = this.currentNode.children[this.currentNode.children.length - 1];
      for (let i = 0; i < this.currentNode.children.length; i++) {
        if (i === this.currentNode.children.length - 1) {
          continue;
        }
        const child = this.currentNode.children[i];
        if (!this.isDuplicated && child.name.toLowerCase() === this.groupName.toLowerCase() && child.id === this.groupName.slice(0,3)){
          this.isDuplicated = true;
          break;
        }
      }
      if (this.isDuplicated) {
        this.ERROR_MSG = 'Давхардсан утга байна';
        return;
      } else {
        this.loading = true;
        this.groupName = this.groupName.trim();
        const newNode: NewNode = {parentId: node.parent, name: this.groupName};
        this.sb.addGroup(newNode).subscribe(res => {
          this.currentNode.children[this.currentNode.children.length - 1].name = res.name;
          this.currentNode.children[this.currentNode.children.length - 1].id = res.id;
          this.clearInputVariables();
          this.loading = false;
          this.ngOnChanges();
        }, error => {
          this.loading = false;
          this.ERROR_MSG = 'Давхардсан утга байна';
        });
      }
    }
  }

  deleteNode(id) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '500px',
      data: {title: 'Анхааруулга', message: 'Та энэ группийг устгахдаа итгэлтэй байна уу?\nУстгасан группийг дахин сэргээх боломжгүй.'}
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loading = true;
        this.loadingNodeId = id;
        this.sb.deleteGroup(id).subscribe(res => {
            this.delete(id, this.nodes);
            this.currentNode = null;
            this.loading = false;
            this.loadingNodeId = null;
            this.updateTreeView();
            this.groupDeleted.emit(res);
          }
        );
      }
    });
  }

  delete(id: string, node: Node[]): void {
    if (node == null) {
      return;
    }

    let indexNumber = -1;
    for (let i = 0; i < node.length; i++) {
      const nodeData = node[i];
      if (id === nodeData.id) {
        indexNumber = i;
        node.splice(indexNumber, 1);
        break;
      } else {
        this.delete(id, nodeData.children);
      }
    }
  }

  updateGroupName(node) {
    if (this.currentNode) {
      this.deleteEmptyNode();
    }
    this.ERROR_MSG = '';
    this.currentNode = node;
    this.currentNode.editable = true;
    this.editNodeName = node.name;
    this.cd.detectChanges();
  }

  renameNode(id: string, name: string) {
    name = name.trim();
    this.isDuplicated = false;
    const parent = this.getNodeById(this.currentNode.parent, this.nodes);
    if (parent != null) {
      for (const child of parent.children) {
        if (id === child.id) {
          continue;
        }
        if (!this.isDuplicated && child.name.toLowerCase() === this.editNodeName.toLowerCase()) {
          this.isDuplicated = true;
          break;
        }
      }
      if (this.isDuplicated) {
        this.ERROR_MSG = 'Давхардсан утга байна';
        return;
      } else {
        this.sb.updateGroupName(id, name).subscribe((res) => {
          this.currentNode.editable = false;
          this.currentNode.name = name;
          this.currentNode = null;
          this.editNodeName = null;
        });
      }
    } else {
      this.sb.updateGroupName(id, name).subscribe((res) => {
        this.currentNode.editable = false;
        this.currentNode.name = name;
        this.currentNode = null;
        this.editNodeName = null;
      });
    }
  }

  addFirstGroup() {
    this.isDuplicated = false;
    if (this.currentNode != null) {
      this.deleteEmptyNode();
    } else {
      const node = {parent: this.root.id, id: '', name: '', nthSibling: null};
      this.root.children.push(node);
      this.currentNode = this.root;
      this.updateTreeView();
    }
  }

  callRenameNode() {
    if (this.editNodeName != null && (this.editNodeName.trim() === '' || this.editNodeName.length < 3)) {
      this.ERROR_MSG = '3-с дээш тэмдэгт оруулна уу';
    } else {
      this.ERROR_MSG = '';
      this.renameNode(this.currentNode.id, this.editNodeName);
    }
  }

  getNodeById(parent: string, nodes: Node[]): Node {
    let parentNode: Node = null;
    if (nodes === undefined) {
      return parentNode;
    }
    for (const nodeOriginal of nodes) {
      if (parentNode != null) {
        return parentNode;
      }
      const node = nodeOriginal;
      if (node.id === parent) {
        parentNode = node;

        return parentNode;
      } else {
        parentNode = this.getNodeById(parent, node.children);
      }
    }
    return parentNode;
  }

  updateTreeView(): void {
    this.setColor(this.nodes, this.color);
    this.dataSource.data = null;
    this.dataSource.data = this.nodes;
    this.cd.detectChanges();
  }

  onNodeClicked(node): void {
    this.activeBtn(node);
    this.selectionChange.emit(node);
  }

  activeBtn(node) {
    this.selected = node;
  }

  isActive(node) {
    return this.selected === node;
  }

  setColor(node: Node[], color: NodeColor): void {
    if (node === undefined) {
      return;
    }
    for (const datum of node) {
      datum.color = color === undefined ? 'black' : color.value;
      this.setColor(datum.children, color === undefined ? undefined : color.color);
    }
  }

  deleteEmptyNode() {
    if (this.currentNode.children) {
      if (this.currentNode.editable) {
        this.currentNode.editable = false;
      } else {
        const index = this.currentNode.children.length - 1;
        if (index > -1) {
          this.currentNode.children.splice(index, 1);
          this.groupName = null;
        }
        this.updateTreeView();
        this.currentNode = null;
        this.ERROR_MSG = '';
      }
    } else {
      this.nodes.pop();
      this.updateTreeView();
      this.currentNode = null;
      this.ERROR_MSG = '';
    }
  }

  onHover(event: any, iconName: string) {
    if (iconName === 'more_vert') {
      event.target.style.opacity = '1';
    } else {
      event.target.style.borderRadius = '20px';
      event.target.style.backgroundColor = '#fffcfc';
    }
  }

  onLeft(event: any, iconName: string) {
    if (iconName === 'more_vert') {
      event.target.style.opacity = '0.3';
    } else {
      event.target.style.borderRadius = '20px';
      event.target.style.backgroundColor = 'transparent';
    }
  }

  addNewItem(node: Node) {
    if (this.currentNode != null) {
      this.deleteEmptyNode();
    }
    node.children.push({parent: node.id, id: '', name: '', nthSibling: node.children.length + 1});
    this.currentNode = node;
    this.updateTreeView();
  }

  clearErrorMsg() {
    this.ERROR_MSG = '';
  }

  saveNode(node: Node) {
    this.treeControl.expand(node);
    if (this.currentNode != null) {
      this.deleteEmptyNode();
    }
    node.children = [];
    node.children.push({parent: node.id, id: '', name: '', nthSibling: node.children.length + 1});
    this.currentNode = node;
    this.updateTreeView();
  }

  drop(event: CdkDragDrop<any[]>) {
    this.dragAndDrop.emit(event);
  }


  onRootClicked() {
    this.selected = this.root;
    this.selectionChange.emit(this.root);
  }
}

