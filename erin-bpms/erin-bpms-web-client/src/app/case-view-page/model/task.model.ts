import {FormsModel} from '../../models/app.model';

export interface TaskModel {
  taskFormFields: FormsModel[];
  taskId: string;
  formId: string;
  tableData?: any;
}

export interface SalaryModel {
  afterTaxSalaries: SalaryCalculationModel[];
  averageSalaryAfterTax: number;
  averageSalaryBeforeTax: number;
}

export interface SaveSalaryModel {
  salariesInfo;
  averageBeforeTax: number;
  averageAfterTax: number;
  hasMortgage: boolean;
  ndsh: boolean;
  emd: boolean;
}

export interface SalaryCalculationModel {
  month: number;
  date: Date;
  defaultValue: number;
  socialInsuranceTax: number;
  personalIncomeTax: number;
  salaryAfterTax: number;
  checked: boolean;
  isXypSalary: boolean;
  isCompleted: boolean;
}

export interface TaskItem {
  icon: string;
  name: string;
  executionId: string;
  instanceId: string;
  executionType: string;
  taskId: string;
  parentTaskId: string;
  definitionKey: string;
}

export interface DocumentsModel {
  fileId: string;
  fileName: string;
  type: string;
  subType: string;
  downloadable: boolean;
  isSubDocument: boolean;
  reference: any;
  source: any;
  file?: string;
}

export interface DocumentTypeModel {
  id: string;
  name: string;
  parentId: string;
  type?: string;
}

export interface DocumentModel {
  name: string;
  contentAsBase64: any;
}

export interface NoteModel {
  note: string;
  username: string;
  date: Date;
  isReason: boolean;
}

export interface CollateralListModel {
  checked: boolean;
  collateralId: string;
  description: string;
  amountOfAssessment: number;
  hairCut: string;
  availableAmount: number;
  startDate: string;
  revalueDate: string;
  accountId: string;
  createdOnBpms: any;
}

export interface LoanProduct {
  id: string;
  categoryDescription: string;
  productDescription: string;
  type: string;
  loanToValueRatio: number;
  hasCollateral: false;
}
export const ENABLED_TASK_DIALOG_TEXT = 'үйлдлийг эхлүүлэх үү?';

export const SALARY_CALCULATION = {id: 'salaryCalculation', formFieldValue: {defaultValue: false}, label: '', type: 'boolean', validations: [], options: [], required: false}



