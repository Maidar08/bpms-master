import {FormsModel} from '../../models/app.model';
import {TaskItem} from '../../case-view-page/model/task.model';

export interface TreeNode {
  processId: string;
  label: string;
  children?: TreeNode[];
}

export interface FlatNode {
  processId: string;
  label: string;
  children: FlatNode[];
  expandable: boolean;
  level: number;
}

export interface ErinCommonWorkspaceService {
  handleAction(event, form: FormsModel[], tableDataMap: any, buttons);
  handleFieldAction(functionName, form: FormsModel[], tableDataMap: any, buttons);
  setInstanceId(instanceId: string);
  setTask(task: TaskItem);
  setFormValue(value: {}, form: FormsModel[]);
}
