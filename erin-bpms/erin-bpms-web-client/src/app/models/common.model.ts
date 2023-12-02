export interface DialogData {
  title?: string;
  message: string;
  confirmButton?: string;
  closeButton?: string;
  data?: any;
}

export class RowEvent {
  row: any;
  $event: MouseEvent;
}

export interface ColumnDef {
  columnDef: string;
  headerText?: string;
  type?: string;
  hasCheckbox?: boolean;
  edit?: boolean;
  separator?: boolean;
  percent?: boolean;
  link?
}
