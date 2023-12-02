export interface Node {
  parent: string;
  id: string;
  name: string;
  nthSibling: number;
  color?: string;
  children?: Node[];
  editable?: boolean;
}

export interface NewNode {
  parentId: string;
  name: string;
}

export interface NodeColor {
  value: string;
  color?: NodeColor;
}
