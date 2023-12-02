import {Component, EventEmitter, Input, Output, ViewEncapsulation} from '@angular/core';
import {TreeNode} from '../../../common/model/erin-model';

@Component({
  selector: 'grouped-task-list-dashlet',
  template: `
    <div class="tree-node">
      <mat-card>
        <div class="header-text">
          <erin-title [titleName]="headerText"></erin-title>
        </div>
        <div class="node-container" *ngFor="let subMenuNode of menuNode">
          <span class="nodeTitle" *ngIf="subMenuNode.children != null">{{get(subMenuNode)}}</span>
          <erin-tree-node *ngIf="subMenuNode.children != null" [setNode]="subMenuNode.children" (nodeEmitter)="setTaskItem($event)"></erin-tree-node>
          <erin-tree-node *ngIf="subMenuNode.children == null" [setNode]="[subMenuNode]" (nodeEmitter)="setTaskItem($event)"></erin-tree-node>
        </div>
      </mat-card>
    </div>
  `,
  styleUrls: ['./grouped-task-list-dashlet.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GroupedTaskListDashletComponent {
  @Input() headerText: string;
  @Input( ) menuNode: TreeNode[] = [];

  @Output() clickedItem = new EventEmitter<TreeNode>();

  setTaskItem(node) {
    this.clickedItem.emit(node);
  }
  get(subMenuNode) {
    return subMenuNode.label;
  }
}
