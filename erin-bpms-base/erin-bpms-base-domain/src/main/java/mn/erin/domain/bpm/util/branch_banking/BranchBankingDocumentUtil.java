package mn.erin.domain.bpm.util.branch_banking;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.BpmBranchBankingConstants;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID_ENTER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_REFERENCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ADDITIONAL_INFO;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ADD_CASH_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ATTACHMENT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASHIER_INCOME_EXPENCE_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASH_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_ACCOUNT_CHILD_MONEY_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CITIZEN_MASTER_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CITIZEN_MASTER_CONTRACT_EXTENSION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CURRENCY_EXCHANGE_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOMER_FULL_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOMER_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOM_PAY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOM_PAY_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.DESC_RECIPIENT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENGLISH;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_ACCOUNT_DEPOSIT_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ENTITY_TIME_DEPOSIT_CONTRACT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.EXEMPT_FROM_FEES;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.EXPENSE_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.E_CUSTOM_PAY_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.E_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FEES_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INCOME_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.JOINT_HOLDERS_LIST;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.LOAN_REPAYMENT_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.MEMORIAL_ORDER_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.NON_CASH_TRANSACTION_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.OFF_BALANCE_RECEIPT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAID_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PARENTS_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYER_USER_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_FORM;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PRINT_AVERAGE_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PRINT_BALANCE_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.REDUCE_FEES;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.REGISTER_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SALARY_PACKAGE_TRANSACTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SHOW_ACCOUNT_CREATED_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_PAY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.USER_NAME;
import static mn.erin.domain.bpm.BpmDocumentConstant.BNPL_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.BNPL_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.CONSUMPTION_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.CREDIT_LINE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.DIRECT_PRINTING_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.DISBURSEMENT_PERMISSION;
import static mn.erin.domain.bpm.BpmDocumentConstant.EMPLOYEE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.EMPLOYEE_MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.INSTANT_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.INSTANT_LOAN_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.LEASING_ORG_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_PERMISSION;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_REPAYMENT_AFTER;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_REPAYMENT_BEFORE;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_BUILD_AOC_FENCED_HOUSE;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_BUY_CAR_GARAGE;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_BUY_FENCED_HOUSE_AOC;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_BUY_PUBLIC_HOUSE;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_FIX_BUYING_HOUSE;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_TYPE_GOVERNMENT;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_TYPE_LOAN;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LEASING_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LEASING_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.P_CON_TYPE;
import static mn.erin.domain.bpm.BpmDocumentConstant.P_CON_TYPE1;
import static mn.erin.domain.bpm.BpmDocumentConstant.P_CON_TYPE2;
import static mn.erin.domain.bpm.BpmDocumentConstant.P_CON_TYPE3;
import static mn.erin.domain.bpm.BpmDocumentConstant.P_CON_TYPE4;
import static mn.erin.domain.bpm.BpmDocumentConstant.P_CON_TYPE5;
import static mn.erin.domain.bpm.BpmDocumentConstant.SALARY_ORG_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SME_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_COLLATERAL_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_FIDUC_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_WARRANTY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_ASSETS_LIST_MORTGAGE;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_CO_OWNER_CONSENT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_CUSTOMER_NAME;
import static mn.erin.domain.bpm.BpmLoanContractConstants.CONTRACT_ID;
import static mn.erin.domain.bpm.BpmLoanContractConstants.EQUIPMENT_ASSETS;
import static mn.erin.domain.bpm.BpmLoanContractConstants.FIDUCIARY_PROPERTY_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_ATTACHMENT_NUMBER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_REPORT_PAGE;
import static mn.erin.domain.bpm.BpmLoanContractConstants.MOVABLE_ASSETS_FIDUCIARY;
import static mn.erin.domain.bpm.BpmLoanContractConstants.NEXT_COLLATERAL_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REQUEST_ID;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.ACCOUNT;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.ADDINFO;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.CHARGE_AMOUNT;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.DIRECTORY_CHECKED;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.F_NAME;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.JOINT_HOLDER;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.LANGUAGE;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.LISTAVGAMT;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.NO;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.ONLINE_LEASING_CONTRACT_NUMBER;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.PRINTACCOD;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.PRINT_BAL;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.PUSE_ID;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_ACC;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_APPLICATION;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_BRN;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_CCY;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_CIF;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_CONTRACTN;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_CUSTOMER_NAME;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_DATE;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_FORACID;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_GRACE;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_LEGAL_FNAME;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_LEGAL_LNAME;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_LEGAL_REGISTER;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_PRINTCOLL;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_REGISTER;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_TRAN;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_USER;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.REFERENCE_CUSTOMER;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.SIGN_IN_USER;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.YES;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.BpmUtils.stringToDateByFormat;

public class BranchBankingDocumentUtil
{
  private BranchBankingDocumentUtil()
  {
    /* Private constructor */
  }

  public static Map<String, String> getDocumentParameters(CaseService caseService, AuthenticationService authenticationService, String instanceId,
      String documentType, Map<String, Object> parameter, String transactionId)
      throws BpmServiceException, ParseException
  {

    Map<String, String> documentParameters = new HashMap<>();
    String formattedDate;
    String eTransactionDate = getValidString(parameter.get(TRANSACTION_DATE));
    String stringDate;
    switch (documentType)
    {
    case TAX_PAY:
    case CUSTOM_PAY:
      stringDate = getValidString(caseService.getVariableById(instanceId, PAID_DATE));
      formattedDate = getFormattedDateForBIP(stringDate);
      documentParameters.put(P_TRAN, String.valueOf(caseService.getVariableById(instanceId, TRANSACTION_NUMBER)));
      documentParameters.put(P_DATE, formattedDate);
      break;
    case NON_CASH_TRANSACTION_RECEIPT:
      setTransactionParameters(caseService, documentParameters, parameter, instanceId);
      break;
    case ADD_CASH_TRANSACTION_RECEIPT:
      formattedDate = getFormattedDateForBIP(getValidString(caseService.getVariableById(instanceId, PAID_DATE)));
      documentParameters.put(P_TRAN, getValidString(caseService.getVariableById(instanceId, TRANSACTION_NUMBER)));
      documentParameters.put(P_DATE, formattedDate);
      documentParameters.put(P_CUSTOMER_NAME, getValidString(caseService.getVariableById(instanceId, CUSTOMER_FULL_NAME)));
      documentParameters.put(P_REGISTER, getValidString(caseService.getVariableById(instanceId, REGISTER_ID)));
      break;
    case CASH_TRANSACTION_RECEIPT:
      setTransactionParameters(caseService, documentParameters, parameter, instanceId);
      documentParameters.put(P_CUSTOMER_NAME, getValidString(parameter.get(ACCOUNT_NAME)));
      break;
    case EXPENSE_TRANSACTION_RECEIPT:
    case MEMORIAL_ORDER_RECEIPT:
    case PAYMENT_FORM:
    case CUSTOM_PAY_RECEIPT:
      String stringData = getValidString(parameter.get(TRANSACTION_DATE));
      formattedDate = getFormattedDateForBIP(stringData);
      documentParameters.put(P_TRAN, transactionId);
      documentParameters.put(P_DATE, formattedDate);
      break;
    case SALARY_PACKAGE_TRANSACTION:
      formattedDate = getFormattedDateForBIP(String.valueOf(caseService.getVariableById(instanceId, TRANSACTION_DT)));
      documentParameters.put(P_TRAN, String.valueOf(caseService.getVariableById(instanceId, TRANSACTION_NUMBER)));
      documentParameters.put(P_DATE, formattedDate);
      break;
    case INCOME_TRANSACTION_RECEIPT:
    case CURRENCY_EXCHANGE_RECEIPT:
      formattedDate = getFormattedDateForBIP(getValidString(parameter.get(TRANSACTION_DATE)));
      documentParameters.put(P_TRAN, transactionId);
      documentParameters.put(P_CUSTOMER_NAME, getValidString(parameter.get(CUSTOMER_NAME)));
      documentParameters.put(P_DATE, formattedDate);
      break;
    case CASHIER_INCOME_EXPENCE_RECEIPT:
    case OFF_BALANCE_RECEIPT:
      formattedDate = getFormattedDateForBIP(getValidString(parameter.get(TRANSACTION_DATE)));
      documentParameters.put(P_TRAN, transactionId);
      documentParameters.put(P_DATE, formattedDate);
      documentParameters.put(P_CUSTOMER_NAME, getValidString(parameter.get(CUSTOMER_NAME)));
      documentParameters.put(P_REGISTER, getValidString(parameter.get(REGISTER_ID)));
      break;
    case E_CUSTOM_PAY_RECEIPT:
      formattedDate = getFormattedDateForBIP(eTransactionDate);
      documentParameters.put(P_TRAN, transactionId);
      documentParameters.put(P_DATE, formattedDate);
      documentParameters.put(PUSE_ID, String.valueOf(parameter.get(PAYER_USER_ID)));
      break;
    case E_TRANSACTION_RECEIPT:
      formattedDate = getFormattedDateForBIP(eTransactionDate);
      documentParameters.put(P_TRAN, transactionId);
      documentParameters.put(P_DATE, formattedDate);
      break;
    case CHILD_FUTURE_MILLIONARE_CONTRACT:
    case CHILD_FUTURE_MILLIONARE_THIRD_PARTY_CONDITION_CONTRACT:
    case CHILD_ACCOUNT_CHILD_MONEY_CONTRACT:
      String accountNumber = String.valueOf(parameter.get(ACCOUNT_ID));
      String branchNumber = String.valueOf(parameter.get(BRANCH_NUMBER));
      String userName = String.valueOf(parameter.get(USER_NAME));

      documentParameters.put(P_ACC, accountNumber);
      documentParameters.put(P_USER, userName);
      documentParameters.put(P_BRN, branchNumber);
      break;
    case CHILD_FUTURE_MILLIONARE_ENDOW_CONTRACT:
      String name = String.valueOf(parameter.get(NAME));
      String parentsName = String.valueOf(parameter.get(PARENTS_NAME));
      String registerId = String.valueOf(parameter.get(REGISTER_ID));
      accountNumber = String.valueOf(parameter.get(ACCOUNT_ID));
      branchNumber = String.valueOf(parameter.get(BRANCH_NUMBER));
      userName = String.valueOf(parameter.get(USER_NAME));

      documentParameters.put(P_ACC, accountNumber);
      documentParameters.put(P_USER, userName);
      documentParameters.put(P_BRN, branchNumber);
      documentParameters.put(P_LEGAL_LNAME, parentsName);
      documentParameters.put(P_LEGAL_FNAME, name);
      documentParameters.put(P_LEGAL_REGISTER, registerId);
      break;
    case CITIZEN_MASTER_CONTRACT:
    case CITIZEN_MASTER_CONTRACT_EXTENSION:
      documentParameters.put(P_ACC, getValidString(parameter.get(ACCOUNT_ID)));
      documentParameters.put(P_BRN, String.valueOf(parameter.get(BRANCH_NUMBER)));
      documentParameters.put(P_USER, String.valueOf(parameter.get(USER_NAME)));
      documentParameters.put(P_CIF, getValidString(parameter.get(CIF_NUMBER)));
      break;
    case ENTITY_TIME_DEPOSIT_CONTRACT:
    case ENTITY_PREPAID_INTEREST_DEPOSIT_CONTRACT:
    case ENTITY_ACCOUNT_DEPOSIT_CONTRACT:
      documentParameters.put(P_ACC, String.valueOf(parameter.get(ACCOUNT_ID)));
      documentParameters.put(P_BRN, String.valueOf(parameter.get(BRANCH_NUMBER)));
      documentParameters.put(P_USER, String.valueOf(parameter.get(USER_NAME)));
      documentParameters.put(P_APPLICATION, getValidString(parameter.get(ATTACHMENT_NUMBER)));
      break;
    case ACCOUNT_REFERENCE:
      setAccountReferenceParam(authenticationService, parameter, documentParameters);
      break;
    case EMPLOYEE_MORTGAGE_LOAN_CONTRACT:
      final String pConType = String.valueOf(caseService.getVariableById(instanceId, P_CON_TYPE));
      if (!StringUtils.isBlank(pConType) && !pConType.equals(NULL_STRING))
      {
        documentParameters.put(P_CON_TYPE, pConType);
      }
      documentParameters.put(P_ACC, String.valueOf(parameter.get(PROCESS_REQUEST_ID)));
      break;
    case SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT:
    case SME_LOAN_CONTRACT:
    case COLLATERAL_CO_OWNER_CONSENT:
    case NEXT_COLLATERAL_LOAN_CONTRACT:
    case ATTACHMENT_COLLATERAL_LOAN_CONTRACT:
    case ATTACHMENT_MORTGAGE_LOAN_CONTRACT:
    case ATTACHMENT_WARRANTY_LOAN_CONTRACT:
    case ATTACHMENT_FIDUC_LOAN_CONTRACT:
    case FIDUCIARY_PROPERTY_CONTRACT:
    case LOAN_REPORT_PAGE:
    case CREDIT_LINE_LOAN_CONTRACT:
    case EMPLOYEE_LOAN_CONTRACT:
    case DIRECT_PRINTING_CONTRACT:
      documentParameters.put(P_ACC, String.valueOf(parameter.get(PROCESS_REQUEST_ID)));
      break;
    case ONLINE_LOAN_CONTRACT:
      final Object pAcc = parameter.containsKey(P_ACC) ? parameter.get(P_ACC) : parameter.get(LOAN_ACCOUNT_NUMBER);
      documentParameters.put(P_ACC, String.valueOf(pAcc));
      break;
    case BNPL_CONTRACT:
      final Object pAcc1 = parameter.containsKey(P_ACC) ? parameter.get(P_ACC) : parameter.get(LOAN_ACCOUNT_NUMBER);
      documentParameters.put(P_ACC, String.valueOf(pAcc1));
      documentParameters.put(REQUEST_ID, getValidString(parameter.get(REQUEST_ID)));
      break;
    case BNPL_REPORT:
      final Object pAcc2 = parameter.containsKey(P_ACC) ? parameter.get(P_ACC) : parameter.get(LOAN_ACCOUNT_NUMBER);
      documentParameters.put(P_ACC, String.valueOf(pAcc2));
      documentParameters.put(REQUEST_ID, getValidString(parameter.get(REQUEST_ID)));
      break;
    case MORTGAGE_TYPE_LOAN:
    case MORTGAGE_TYPE_GOVERNMENT:

      if (parameter.containsKey(MORTGAGE_BUY_PUBLIC_HOUSE))
      {
        documentParameters.put(P_CON_TYPE1, "1");
      }

      if (parameter.containsKey(MORTGAGE_BUY_FENCED_HOUSE_AOC))
      {
        documentParameters.put(P_CON_TYPE2, "2");
      }

      if (parameter.containsKey(MORTGAGE_BUY_CAR_GARAGE))
      {
        documentParameters.put(P_CON_TYPE3, "3");
      }

      if (parameter.containsKey(MORTGAGE_FIX_BUYING_HOUSE))
      {
        documentParameters.put(P_CON_TYPE4, "4");
      }

      if (parameter.containsKey(MORTGAGE_BUILD_AOC_FENCED_HOUSE))
      {
        documentParameters.put(P_CON_TYPE5, "5");
      }

      documentParameters.put(P_ACC, String.valueOf(parameter.get(PROCESS_REQUEST_ID)));
      break;
    case CONSUMPTION_LOAN_CONTRACT:
      documentParameters.put(P_ACC, String.valueOf(parameter.get(PROCESS_REQUEST_ID)));
      documentParameters.put("P_PAYMENTTYPE", String.valueOf(parameter.get(REPAYMENT_TYPE_ID)));
      break;
    case LOAN_REPAYMENT_BEFORE:
      documentParameters.put(P_ACC, String.valueOf(parameter.get(ACCOUNT_NUMBER)));
      documentParameters.put(P_GRACE, YES);
      break;
    case LOAN_REPAYMENT_AFTER:
      documentParameters.put(P_ACC, String.valueOf(parameter.get(PROCESS_REQUEST_ID)));
      documentParameters.put(P_GRACE, NO);
      break;
    case COLLATERAL_ASSETS_LIST_MORTGAGE:
    case MOVABLE_ASSETS_FIDUCIARY:
    case EQUIPMENT_ASSETS:
      documentParameters.put(P_ACC, String.valueOf(parameter.get(PROCESS_REQUEST_ID)));
      documentParameters.put("P_CONTNO", String.valueOf(parameter.get(CONTRACT_ID)));
      documentParameters.put("P_SUBNO", String.valueOf(parameter.get(LOAN_ATTACHMENT_NUMBER)));
      documentParameters.put("P_CUSTNAME", String.valueOf(parameter.get(COLLATERAL_CUSTOMER_NAME)));
      break;
    case DISBURSEMENT_PERMISSION:
    case LOAN_PERMISSION:
      if (StringUtils.equals(documentType, DISBURSEMENT_PERMISSION))
      {
        documentParameters.put("P_ZOZTYPE", "A");
        documentParameters.put("P_TOTAL", String.valueOf(parameter.get("loanContractTotalAmount")));
      }
      else if(StringUtils.equals(documentType, LOAN_PERMISSION))
      {
        documentParameters.put("P_ZOZTYPE", "B");
        documentParameters.put("P_TOTALLIMIT", String.valueOf(parameter.get("amount")));
      }

      documentParameters.put("P_PARTDISB", StringUtils.equals(String.valueOf(parameter.get("checkboxPoll")), "Байгаа") ? "Y" : "N");

      documentParameters.put(P_ACC, String.valueOf(parameter.get("accountNumber")));
      documentParameters.put("P_DISBACCMNT", String.valueOf(parameter.get("confirmedTotalAmount")));
      documentParameters.put("P_DISBMNT", String.valueOf(parameter.get("currentTotalAmount")));
      documentParameters.put("P_CROSS", String.valueOf(parameter.get("crossTrade")));
      documentParameters.put("P_CLOSEACC", String.valueOf(parameter.get("closingAccountNumber")));
      documentParameters.put(P_CCY, String.valueOf(parameter.get("currencyValue")));
      documentParameters.put("P_PROCHARGE", String.valueOf(parameter.get("loanRepaymentFee")));
      documentParameters.put(P_PRINTCOLL, String.valueOf(parameter.get("printCollateralList")));
      documentParameters.put("P_ZUHMNT", String.valueOf(parameter.get("buNe")));
      documentParameters.put("P_PRINCIPAL", String.valueOf(parameter.get("loanAccountFirstAmount")));
      documentParameters.put("P_DISBACC", String.valueOf(parameter.get("accountNumberTrans")));
      documentParameters.put("P_DISBACCNAME", String.valueOf(parameter.get("accountName")));
      break;
    case SALARY_ORG_CONTRACT:
    case LEASING_ORG_CONTRACT:
      documentParameters.put(P_ACC, String.valueOf(parameter.get(P_ACC)));
      break;
    case INSTANT_LOAN_CONTRACT:
      final Object pAcc3 = parameter.containsKey(P_ACC) ? parameter.get(P_ACC) : parameter.get(LOAN_ACCOUNT_NUMBER);
      documentParameters.put(P_ACC, String.valueOf(pAcc3));
      documentParameters.put(REQUEST_ID, getValidString(parameter.get(REQUEST_ID)));
      break;
    case INSTANT_LOAN_REPORT:
      final Object pAcc4 = parameter.containsKey(P_ACC) ? parameter.get(P_ACC) : parameter.get(LOAN_ACCOUNT_NUMBER);
      documentParameters.put(P_ACC, String.valueOf(pAcc4));
      break;
    case ONLINE_LEASING_CONTRACT:
    case ONLINE_LEASING_REPORT:
      final Object pAcc5 = parameter.containsKey(P_ACC) ? parameter.get(P_ACC) : parameter.get(LOAN_ACCOUNT_NUMBER);
      documentParameters.put(P_FORACID, String.valueOf(pAcc5));
      documentParameters.put(P_CONTRACTN, ONLINE_LEASING_CONTRACT_NUMBER);
      break;
    default:
      return Collections.emptyMap();
    }
    return documentParameters;
  }

  private static void setTransactionParameters(CaseService caseService, Map<String, String> documentParameters, Map<String, Object> parameter,
      String instanceId)
      throws BpmServiceException, ParseException
  {
    String formattedDate;
    String loanRepaymentType = getValidString(parameter.get(LOAN_REPAYMENT_TYPE));
    String transactionIdScheduled = getValidString(caseService.getVariableById(instanceId, "transactionIdScheduled"));
    String transactionIdUnscheduled = getValidString(caseService.getVariableById(instanceId, "transactionIdUnscheduled"));
    String transactionDate1 = getValidString(caseService.getVariableById(instanceId, "transactionDate1"));
    String transactionDate2 = getValidString(caseService.getVariableById(instanceId, "transactionDate2"));

    if (loanRepaymentType.equals("Хуваарийн бус"))
    {
      String transactionNumber1 = transactionIdUnscheduled.equals(EMPTY_VALUE) ? transactionIdScheduled : transactionIdUnscheduled;
      String transactionDate = transactionDate1.equals(EMPTY_VALUE) ? transactionDate2 : transactionDate1;
      formattedDate = getFormattedDateForBIP(getValidString(transactionDate));
      documentParameters.put(P_TRAN, transactionNumber1);
      documentParameters.put(P_DATE, formattedDate);
    }
    else
    {
      formattedDate = getFormattedDateForBIP(getValidString(caseService.getVariableById(instanceId, PAID_DATE)));
      documentParameters.put(P_TRAN, String.valueOf(caseService.getVariableById(instanceId, TRANSACTION_NUMBER)));
      documentParameters.put(P_DATE, formattedDate);
    }
  }

  private static void setAccountReferenceParam(AuthenticationService authenticationService, Map<String, Object> parameter,
      Map<String, String> documentParameters)
  {
    setBooleanValues(documentParameters, parameter, PRINT_BALANCE_AMOUNT, PRINT_BAL);
    setBooleanValues(documentParameters, parameter, PRINT_AVERAGE_AMOUNT, LISTAVGAMT);
    setBooleanValues(documentParameters, parameter, SHOW_ACCOUNT_CREATED_DATE, PRINTACCOD);

    documentParameters.put(ACCOUNT, String.valueOf(parameter.get(ACCOUNT_ID_ENTER)));
    documentParameters.put(SIGN_IN_USER, authenticationService.getCurrentUserId());

    validateInput(documentParameters, parameter, ADDITIONAL_INFO, ADDINFO);
    validateInput(documentParameters, parameter, DESC_RECIPIENT, REFERENCE_CUSTOMER);

    if (String.valueOf(parameter.get(BpmBranchBankingConstants.LANGUAGE)).equals(ENGLISH))
    {
      documentParameters.put(LANGUAGE, "ENG");
    }
    else
    {
      documentParameters.put(LANGUAGE, "MNG");
    }

    if (Boolean.TRUE.equals(parameter.get(EXEMPT_FROM_FEES)))
    {
      documentParameters.put(CHARGE_AMOUNT, "0");
    }
    else if (Boolean.TRUE.equals(parameter.get(REDUCE_FEES)))
    {
      documentParameters.put(CHARGE_AMOUNT, String.valueOf(parameter.get("feeConstant")));
    }
    else
    {
      documentParameters.put(CHARGE_AMOUNT, String.valueOf(parameter.get(FEES_AMOUNT)).replace(",", ""));
    }

    if (!String.valueOf(parameter.get(JOINT_HOLDERS_LIST)).isEmpty())
    {
      documentParameters.put(F_NAME, String.valueOf(parameter.get(JOINT_HOLDERS_LIST)));
      documentParameters.put(JOINT_HOLDER, YES);
    }
    else
    {
      documentParameters.put(F_NAME, EMPTY_VALUE);
      documentParameters.put(JOINT_HOLDER, EMPTY_VALUE);
    }
    if (Boolean.TRUE.equals(parameter.get(REDUCE_FEES)) || Boolean.TRUE.equals(parameter.get(EXEMPT_FROM_FEES)))
    {
      documentParameters.put(DIRECTORY_CHECKED, YES);
    }
  }

  private static void setBooleanValues(Map<String, String> documentParameters, Map<String, Object> parameter, String parameterKey, String key)
  {
    if (Boolean.TRUE.equals(parameter.get(parameterKey)))
    {
      documentParameters.put(key, YES);
    }
    else
    {
      documentParameters.put(key, NO);
    }
  }

  public static String getFormattedDateForBIP(String stringDate) throws ParseException
  {
    if (stringDate.isEmpty())
    {
      return stringDate;
    }
    LocalDate localDate = stringToDateByFormat(ISO_DATE_FORMAT, stringDate);
    return localDate.getMonthValue() + "-" + localDate.getDayOfMonth() + "-" + localDate.getYear();
  }

  private static void validateInput(Map<String, String> documentParameter, Map<String, Object> parameter, String parameterKey, String key)
  {
    if (parameter.get(parameterKey) == null)
    {
      documentParameter.put(key, "");
    }
    else
    {
      documentParameter.put(key, String.valueOf(parameter.get(parameterKey)));
    }
  }
}
