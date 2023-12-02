import {ColumnDef} from './common.model';

export const REQUEST_COLUMNS: ColumnDef[] = [
  {columnDef: 'id', headerText: 'ХҮСЭЛТИЙН ДУГААР'},
  {columnDef: 'fullName', headerText: 'ОВОГ,НЭР'},
  {columnDef: 'registerNumber', headerText: 'РЕГИСТР'},
  {columnDef: 'cifNumber', headerText: 'CIF'},
  {columnDef: 'loanProductDescription', headerText: 'БҮТЭЭГДЭХҮҮН'},
  {columnDef: 'amount', headerText: 'ХЭМЖЭЭ'},
  {columnDef: 'createdDate', headerText: 'ОГНОО'},
  {columnDef: 'assignee', headerText: 'ХЭРЭГЛЭГЧ'},
  {columnDef: 'branchNumber', headerText: 'САЛБАР'},
  {columnDef: 'channel', headerText: 'СУВАГ'},
  {columnDef: 'state', headerText: 'ТӨЛӨВ'},
  {columnDef: 'processTypeId', headerText: 'ТӨРӨЛ'},
  {columnDef: 'button', headerText: ''}
];

export const ORGANIZATION_REGISTRATION_COLUMNS: ColumnDef[] = [
  {columnDef: 'id', headerText: 'ГЭРЭЭНИЙ ДУГААР'},
  {columnDef: 'branchId', headerText: 'САЛБАРЫН ДУГААР'},
  {columnDef: 'organizationName', headerText: 'БАЙГУУЛЛАГЫН НЭР'},
  {columnDef: 'organizationNumber', headerText: 'РЕГИСТРИЙН ДУГААР'},
  {columnDef: 'cifNumber', headerText: 'СИФ ДУГААР'},
  {columnDef: 'state', headerText: 'ТӨЛӨВ'},
  {columnDef: 'contractDate', headerText: 'ГЭРЭЭ БАЙГУУЛСАН ОГНОО'},
  {columnDef: 'assignee', headerText: 'ХЭРЭГЛЭГЧ'},
  {columnDef: 'confirmedUser', headerText: 'БАТАЛСАН ХЭРЭГЛЭГЧ'}
];

export const SALARY_CALCULATION_COLUMNS: ColumnDef[] = [
  {columnDef: 'month', headerText: 'Сар'},
  {columnDef: 'date', headerText: 'Огноо'},
  {columnDef: 'defaultValue', headerText: 'НД баталгаажсан цалин'},
  {columnDef: 'socialInsuranceTax', headerText: 'НДШ'},
  {columnDef: 'personalIncomeTax', headerText: 'ХХОАТ'},
  {columnDef: 'salaryAfterTax', headerText: 'Татварын дараах цалин'},
];

export  const COLLATERAL_TABLE_COLUMNS: ColumnDef[] = [
  {columnDef: 'checkbox', headerText: ''},
  {columnDef: 'collateralId', headerText: 'Барьцааны код'},
  {columnDef: 'description', headerText: 'Дэлгэрэнгүй'},
  {columnDef: 'amountOfAssessment', headerText: 'Үнэлгээ'},
  {columnDef: 'hairCut', headerText: 'Хасагдуулгын %'},
  {columnDef: 'availableAmount', headerText: 'Боломжит үлдэгдэл'},
  {columnDef: 'startDate', headerText: 'Эхлэх огноо'},
  {columnDef: 'revalueDate', headerText: 'Дахин үнэлсэн огноо'},
  {columnDef: 'accountId', headerText: 'Холбосон дансны дугаар'},
  {columnDef: 'createdOnBpms', headerText: 'CBS дээр үүсгэсэн'}
];

export const LOAN_CONTRACT_COLUMNS: ColumnDef[] = [
  {columnDef: 'id', headerText: 'ХҮСЭЛТИЙН ДУГААР'},
  {columnDef: 'cif', headerText: 'СИФ'},
  {columnDef: 'loanAccount', headerText: 'ЗЭЭЛИЙН ДАНС'},
  {columnDef: 'product', headerText: 'БҮТЭЭГДЭХҮҮН'},
  {columnDef: 'loanAmount', headerText: 'ЗЭЭЛИЙН ДҮН'},
  {columnDef: 'date', headerText: 'ГЭРЭЭ БЭЛТГЭСЭН ОГНОО'},
  {columnDef: 'assignee', headerText: 'БЭЛТГЭСЭН ХЭРЭГЛЭГЧ'}
];

export interface FieldOptions {
  id: string;
  value: string;
  default?: string;
  disable?: boolean;
}

export interface FormsModel {
  _pendingValue?: string[];
  id: string;
  formFieldValue: FormValue;
  label: string;
  type: string;
  validations: Validation[];
  options: FieldOptions[];
  optionsCheckbox?: FieldOptions[];
  required: boolean;
  context?: string;
  disabled?: boolean;
  columnIndex?: number;
  fieldNumber?: number;
  fieldHint?: string;
}

export interface FormValue {
  defaultValue: any;
  valueInfo?: string;
}

export interface Validation {
  name: string;
  configuration: any;
}

export interface FormGroupModel {
  fieldGroups: FieldGroups[];
  buttons: any;
  entities?: TaskEntity;
  getPropertyId?: string[];
  fieldAction?: any;
  table?: Table;
}

export interface FieldGroups {
  title: string;
  subtitle?: string;
  type: string;
  column: number;
  fieldIds: string[];
  fields: FormsModel[];
  subFieldIds?: string[][];
  buttonIds?: string[];
  checkboxFieldIds?: string[];
  textAreaFieldIds?: string[];
  chipFieldIds?: string[];
  subFields?: FormsModel[][];
  subtitles?: string[];
  subColumn?: number[];
  noSeparator?: boolean;
  index?: number;
  min?: number;
  max?: number;
  message?: string;
  flexibleForm?: any;
  buttons?: any;
}

export interface Table {
  isFlexibleTable?: boolean;
  tableHeader: string;
  tableColumns: any;
  hasPagination?: boolean;
  tableHasScrollX?: boolean;
  tableDoubleClick?: string;
}
export interface Buttons {
  buttonId: string;
  state: boolean;
}

export interface TaskEntity {
  taskType: string;
  entity: string;
}

export interface Document {
  documentId: string;
  DocumentInfoId: string;
  name: string;
  category: string;
  subCategory: string;
  reference: string;
  source: string;
}

export interface Group {
  groupId: string;
  groupName: string;
}

export interface DatePickerWithBranchModel {
  date: Date[],
  selectedBranchList: string[]
}
