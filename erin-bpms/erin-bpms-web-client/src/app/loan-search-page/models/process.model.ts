export interface ProcessTypeModel {
  id: string;
  definitionKey: string;
  name: string;
  version: string;
  processDefinitionType: string;
  processTypeCategory: string;
}

export interface RequestModel {
  id: string;
  fullName: string;
  registerNumber: string;
  cifNumber: string;
  instanceId: string;
  phoneNumber: number;
  email: string;
  createdDate: string;
  productCode: string;
  amount: string;
  branchNumber: string;
  channel: string;
  assignee: string;
  userName: string;
  state: string;
  organizationType?: string;
  processTypeId: string;
}

export interface ContractRequestModel {
  id: string;
  cif: string;
  loanAccount: string;
  product: string;
  loanAmount: string;
  date: string;
  assignee: string;
  processInstanceId: string;
}

export interface OrganizationRequestModel {
  id: string;
  branchId: string;
  organizationName: string;
  organizationNumber: string;
  cifNumber: string;
  state: string;
  contractDate: string;
  assignee: string;
  instanceId: string;
  confirmedUser: string;
}


