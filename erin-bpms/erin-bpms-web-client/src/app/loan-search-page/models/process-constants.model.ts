// loan type
export const ORGANIZATION_BORROWER = 'Аж ахуй нэгж';

export const PRODUCT_CATEGORY_MAP = new Map([
  ['consumptionLoan', 'CONSUMER'],
  ['microLoan', 'SMALL_MICRO']
]);

export const PRODUCT_BORROWER_TYPE_MAP = new Map([
  ['institution', 'ААН'],
  ['citizen', 'Иргэн']
]);

export const LOAN_PRODUCT = 'loanProduct';
export const BORROWER_TYPE = 'borrowerType';

// organization registration
export const ORGANIZATION_REQUEST = 'organization';
export const ORGANIZATION = 'ORGANIZATION';
export const EDIT_PERMISSION_ORGANIZATION_REGISTRATION = 'bpms.bpm.EditOrganizationRequests';
export const CONTINUE_PER_FOR_BRANCH_SPECHIALIST = 'bpms.bpm.ContinueOrganizationRequests';
export const CONFIRM_PER_FOR_DIRECTOR = 'bpms.bpm.ConfirmOrganizationRequests';

export const LOAN = 'LOAN';

//ussd const
export const CONFIRM_PERMISSION_FOR_USSD = 'bpms.bpm.USSDConfirmRequest';

// branch-banking
export const BRANCH_BANKING = 'branch-banking';
export const BRANCHBANKING = 'BRANCHBANKING';

// loan-contract
export const LOAN_CONTRACT = 'loan-contract';
export const LOANCONTRACT = 'LOAN_CONTRACT';

// Organization
export const ORGANIZATION_STATES = new Map(
  [
    ['N', 'ШИНЭ'],
    ['U', 'БАТЛАГДААГҮЙ'],
    ['C', 'ЦУЦЛАГДСАН'],
    ['O', 'БАТЛАГДСАН'],
    ['R', 'ТАТГАЛЗСАН'],
  ]);
