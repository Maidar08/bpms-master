import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FlatNode, TreeNode} from "../model/erin-model";
import {FlatTreeControl} from "@angular/cdk/tree";
import {MatTreeFlatDataSource, MatTreeFlattener} from "@angular/material/tree";

@Component({
  selector: 'erin-tree-node',
  template: `
    <mat-tree [dataSource]="dataSource" [treeControl]="treeControl">
      <!-- This is the tree node template for leaf nodes -->
      <mat-tree-node class="child-node" *matTreeNodeDef="let node" [ngClass]="highlightItem(node)" matTreeNodePadding (click)="clickOnNode(node)">
        <!-- use a disabled button to provide padding for tree leaf -->
        <button mat-icon-button disabled></button>
        <span class="text">
        {{node.label}}
        </span>
        <button mat-icon-button >
          <mat-icon class="play-arrow" >play_arrow</mat-icon>
        </button>
      </mat-tree-node>
      <!-- This is the tree node template for expandable nodes -->
      <mat-tree-node *matTreeNodeDef="let node; when: hasChild" matTreeNodePadding matTreeNodeToggle
                     [ngClass]="highlightItem(node)" (click)="clickOnNode(node)">
        <button mat-icon-button matTreeNodeToggle [attr.aria-label]="'Toggle ' + node.label">
          <mat-icon class="mat-icon-rtl-mirror">
            {{treeControl.isExpanded(node) ? 'expand_more' : 'chevron_right'}}
          </mat-icon>
        </button>
        {{node.label}}
      </mat-tree-node>
    </mat-tree>

  `,
  styleUrls: ['./erin-tree-node.component.scss']
})
export class ErinTreeNode {
  @Input() set setNode(node: TreeNode[]) {
    if (node) {
      this.dataSource.data = node;
    }
  }
  @Output() nodeEmitter = new EventEmitter<TreeNode>();

  private _transformer = (node: TreeNode, level: number) => {
    return {
      expandable: !!node.children && node.children.length > 0,
      label: node.label,
      level: level,
      processId: node.processId,
      children: node.children
    };
  }

  treeControl = new FlatTreeControl<FlatNode>(
    node => node.level, node => node.expandable);

  treeFlattener = new MatTreeFlattener(
    this._transformer, node => node.level, node => node.expandable, node => node.children);

  dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

  constructor() {
    localStorage.clear();
  }

  hasChild = (_: number, node: FlatNode) => node.expandable;

  clickOnNode(node: FlatNode) {
    localStorage.setItem('nodeKey', node.processId);
    if (!node.expandable) {
      this.nodeEmitter.emit(node);
    }
  }

  highlightItem(node: FlatNode) {
    const sessionValue = localStorage.getItem('nodeKey');
    if (node.processId === sessionValue) {
      return ' active';
    }
  }
}
