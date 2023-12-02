import {Injectable} from '@angular/core';
import {TaskItem, TaskModel} from '../../model/task.model';
import {
  CO_OWNER_INFORMATION,
  COLLATERAL_LIST,
  COLLATERAL_REAL_ESTATE,
  CREATE_COLLATERAL_LOAN_ACCOUNT,
  CRITERIA,
  DOWNLOADKHUR,
  DOWNLOADKHUR_COBORROWER,
  FIDUCIARY_ASSET,
  LOAN_ACCOUNT,
  LOAN_ATTACHMENT_CONTRACT,
  LOAN_CALCULATION,
  LOAN_CONTRACT,
  LOAN_PERMIT,
  LOAN_SEND,
  REMOVE_COBORROWER,
  SALARY_CALCULATION,
  SCORING,
  TASK_DEF_COLLATERAL_LIST,
  TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT,
  TASK_DEF_MICRO_BALANCE_CALCULATION,
  TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT,
  TASK_DEF_MICRO_LOAN_CALCULATION,
  TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR,
  TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR_CO_BORROWER,
  TASK_DEF_MICRO_LOAN_SCORING,
  TASK_DEF_MICRO_SIMPLE_CALCULATION,
  TASK_DEF_MORTGAGE_BUSINESS_CALCULATION,
  TASK_DEF_MORTGAGE_BUSINESS_LOAN_SCORING,
  TASK_DEF_MORTGAGE_GENERATE_LOAN_DECISION,
  TASK_DEF_MORTGAGE_LOAN_CALCULATION,
  TASK_DEF_MORTGAGE_SALARY_CALCULATION,
  TASK_DEF_MORTGAGE_SALARY_LOAN_SCORING
} from '../../model/task-constant';

@Injectable({
  providedIn: 'root'
})
export class TaskStateHandlerService {
  constructor() {
  }

  private hideSalaryTable = false;
  private hideMainWorkspace = false;

  // Consumption
  private isDownloadKhur = false;
  private isDownloadKhurCoBorrower = false;
  private isCriteriaForm = false;
  private isRemoveCoBorrower = false;
  private isScoringForm = false;
  private isRateForm = false;
  private isLoanCalculation = false;
  private isShowCollateralList = false;
  private isLoanSendForm = false;
  private isLoanPermitForm = false;
  private isLoanAccount = false;
  private isCollateralLoanAccount = false;
  private isCreateCollateralAccount = false;
  private isLoanContract = false;
  private isStandardHousingValuation = false;

  // Micro
  private isMicroLoanDownloadKhur = false;
  private isMicroLoanDownloadKhurCoBorrower = false;
  private isMicroSimpleCalculation = false;
  private isMicroBalanceCalculation = false;
  private isMicroLoanScoringForm = false;
  private isMicroLoanCalculation = false;
  private isMicroCreateCollateralAccount = false;

  // Mortgage
  private isMortgageLoanCalculation = false;
  private isMortgageSalaryScoring = false;
  private isMortgageBusinessCalculation = false;
  private isMortgageGenerateDecision = false;

  public setTaskState(res: TaskModel, task: TaskItem, isCompletedTask?: boolean): void {
    this.isLoanCalculation = (task.name.includes(LOAN_CALCULATION) && task.definitionKey !== TASK_DEF_MICRO_LOAN_CALCULATION
      && task.definitionKey !== TASK_DEF_MORTGAGE_LOAN_CALCULATION);
    this.isMicroLoanCalculation = task.definitionKey === TASK_DEF_MICRO_LOAN_CALCULATION;
    this.isMortgageLoanCalculation = task.definitionKey === TASK_DEF_MORTGAGE_LOAN_CALCULATION;
    this.isRateForm = task.name.includes(LOAN_CALCULATION);
    this.isScoringForm = (task.name.includes(SCORING) && task.definitionKey !== TASK_DEF_MICRO_LOAN_SCORING
      && task.definitionKey !== TASK_DEF_MORTGAGE_BUSINESS_LOAN_SCORING) && task.definitionKey !== TASK_DEF_MORTGAGE_SALARY_LOAN_SCORING;
    this.isMortgageSalaryScoring = task.definitionKey === TASK_DEF_MORTGAGE_SALARY_LOAN_SCORING;
    this.isMicroLoanScoringForm = task.definitionKey === TASK_DEF_MICRO_LOAN_SCORING
      || task.definitionKey === TASK_DEF_MORTGAGE_BUSINESS_LOAN_SCORING;
    this.isLoanAccount = task.name.includes(LOAN_ACCOUNT) && task.definitionKey !== TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT
      && task.definitionKey !== TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT;
    this.isCollateralLoanAccount = task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT;

    this.isCriteriaForm = task.name.includes(CRITERIA);
    this.isLoanSendForm = task.name.includes(LOAN_SEND);
    this.isLoanPermitForm = task.name.includes(LOAN_PERMIT);
    this.isLoanContract = task.name.includes(LOAN_CONTRACT);
    this.isRemoveCoBorrower = task.name.includes(REMOVE_COBORROWER);
    if (task.name.includes(SALARY_CALCULATION) || task.definitionKey === TASK_DEF_MORTGAGE_SALARY_CALCULATION) { this.hideSalaryTable = true; }
    this.isMicroSimpleCalculation = task.definitionKey === TASK_DEF_MICRO_SIMPLE_CALCULATION;
    this.isMortgageBusinessCalculation = task.definitionKey === TASK_DEF_MORTGAGE_BUSINESS_CALCULATION;
    this.isDownloadKhur = task.name.includes(DOWNLOADKHUR) && task.definitionKey !== TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR;
    this.isDownloadKhurCoBorrower = task.name.includes(DOWNLOADKHUR_COBORROWER) && task.definitionKey !== TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR;
    this.isCreateCollateralAccount =
      task.name.includes(CREATE_COLLATERAL_LOAN_ACCOUNT) ||
      (task.definitionKey === TASK_DEF_CREATE_COLLATERAL_LOAN_ACCOUNT && task.name.includes('данс үүсгэх'));
    this.isMicroCreateCollateralAccount =
      task.definitionKey === TASK_DEF_MICRO_CREATE_COLLATERAL_LOAN_ACCOUNT && task.name.includes('данс үүсгэх');
    this.isShowCollateralList = (task.name.includes(COLLATERAL_LIST) || task.definitionKey === TASK_DEF_COLLATERAL_LIST);
    this.isMicroLoanDownloadKhur = task.definitionKey === TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR;
    this.isMicroLoanDownloadKhurCoBorrower = task.definitionKey === TASK_DEF_MICRO_LOAN_DOWNLOAD_KHUR_CO_BORROWER;

    // micro
    this.isMicroBalanceCalculation = task.definitionKey === TASK_DEF_MICRO_BALANCE_CALCULATION;

    // mortgage
    this.isMortgageGenerateDecision = task.definitionKey === TASK_DEF_MORTGAGE_GENERATE_LOAN_DECISION;

    if (!this.isLoanContract && !this.isRemoveCoBorrower && !this.isCreateCollateralAccount
      && !this.isMicroCreateCollateralAccount && !this.isMicroBalanceCalculation) {
      this.hideMainWorkspace = false;
    }
  }

  public isMortgageGenerateDecisionTask(): boolean {
    return this.isMortgageGenerateDecision;
  }

  public showDynamicColumn(): boolean {
    return this.isMicroSimpleCalculation || this.isMicroLoanDownloadKhur || this.isMicroLoanDownloadKhurCoBorrower || this.isMortgageBusinessCalculation;
  }

  public showFlexibleForm(task): boolean {
    return task.name.includes(FIDUCIARY_ASSET) || task.name.includes(LOAN_ATTACHMENT_CONTRACT) || task.name.includes(CO_OWNER_INFORMATION)
      || task.name.includes(COLLATERAL_REAL_ESTATE) || this.isMicroLoanCalculation || this.isMortgageLoanCalculation || this.isMortgageGenerateDecision;
  }
}
