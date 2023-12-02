export interface FilterValue {
  columnName: string;
  value: string;
}

export class RowEvent {
  row: any;
  $event: MouseEvent;
}

export interface ColumnDef {
  columnDef: string;
  headerText: string;
  progress?: boolean;
}
