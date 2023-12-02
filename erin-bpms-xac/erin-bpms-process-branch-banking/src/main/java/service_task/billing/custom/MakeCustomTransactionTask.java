package service_task.billing.custom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.BpmBranchBankingConstants;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.billing.MakeCustomTransaction;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_NO_CUSTOM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_ACC_LIST_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_ACC_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_ACC_NUMBER_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_CODE_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BRANCH_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CUSTOM_INVOICE_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CUSTOM_TRANSACTION_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.DECLARATION_DATE_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INVOICE_NUM_CUSTOM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INVOICE_TYPE_NAME_CUSTOM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PAYMENT_ACC_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PAYMENT_ACC_NUMBER_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PAYMENT_AMOUNT_CUSTOM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PAYMENT_LIST_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PHONE_NUMBER_CUSTOM_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.REF_NO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.REGISTER_ID_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TAX_PAYER_NAME_SERVICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRANSACTION_VALUE_CUSTOM_SERVICE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BANK_ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BANK_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BANK_CODE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BANK_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BRANCH_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHARGE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.DECLARATION_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAID_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAYMENT_LIST;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.REGISTER_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_PAYER_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TYPE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.TYPE;
import static mn.erin.domain.bpm.util.process.BpmUtils.checkAccountNumberValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertDateStringToInt;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Odgavaa
 **/
public class MakeCustomTransactionTask implements JavaDelegate
{
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MakeCustomTransactionTask.class);

  private final BranchBankingService branchBankingService;

  public MakeCustomTransactionTask(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    CaseService caseService = execution.getProcessEngine().getCaseService();

    checkRequiredVariable(caseService, instanceId);
    Map<String, Object> inputParameters = setTransactionParams(execution);
    inputParameters.put(CASE_INSTANCE_ID, instanceId);
    MakeCustomTransaction makeCustomTransaction = new MakeCustomTransaction(branchBankingService);
    Map<String, String> outputParameters = makeCustomTransaction.execute(inputParameters);


    String bankTransactionNo = outputParameters.get(REF_NO);
    String transactionDate = outputParameters.get("TrnDt");
    execution.setVariable(TRANSACTION_NUMBER, bankTransactionNo);
    caseService.setVariable(instanceId, TRANSACTION_NUMBER, bankTransactionNo);
    execution.setVariable(PAID_DATE, transactionDate);
    caseService.setVariable(instanceId, PAID_DATE, transactionDate);
    LOG.info("######################## Custom transaction is successfully made with transaction id = [{}], \n process instance id = [{}]", bankTransactionNo,
        instanceId);
  }

  private List<Map<String, Object>> setPaymentInfo(Map<String, Object> variables)
  {
    List<Map<String, Object>> paymentList = new ArrayList<>();

    List<Map<String, Object>> payList = (List<Map<String, Object>>) variables.get(PAYMENT_LIST);

    for (Map<String, Object> pay : payList)
    {
      String paymentAccountName = (String) pay.get(PAYMENT_ACCOUNT_NAME);
      String paymentAccountNumber = (String) pay.get(PAYMENT_ACCOUNT_NUMBER);

      String paymentAmountString = String.valueOf(pay.get(PAYMENT_AMOUNT));
      BigDecimal paymentAmount = new BigDecimal(paymentAmountString);

      String bankAccountName = (String) pay.get(BANK_ACCOUNT_NAME);
      String bankAccountNumber = (String) pay.get(BANK_ACCOUNT_NUMBER);
      String bankCode = (String) pay.get(BANK_CODE);
      String bankName = (String) pay.get(BANK_NAME);

      Map<String, Object> paymentInfo = new HashMap<>();
      Map<String, Object> bankAccountInfo = new HashMap<>();

      bankAccountInfo.put(BANK_ACC_NAME_SERVICE, bankAccountName);
      bankAccountInfo.put(BANK_ACC_NUMBER_SERVICE, bankAccountNumber);
      bankAccountInfo.put(BANK_CODE_SERVICE, bankCode);
      bankAccountInfo.put(BANK_NAME_SERVICE, bankName);

      paymentInfo.put(PAYMENT_ACC_NAME_SERVICE, paymentAccountName);
      paymentInfo.put(PAYMENT_ACC_NUMBER_SERVICE, paymentAccountNumber);
      paymentInfo.put(PAYMENT_AMOUNT_CUSTOM_SERVICE, paymentAmount);
      paymentInfo.put(BANK_ACC_LIST_SERVICE, bankAccountInfo);
      paymentList.add(paymentInfo);
    }
    return paymentList;
  }

  private Map<String, Object> setTransactionValues(Map<String, Object> variables, DelegateExecution execution) throws BpmServiceException
  {
    Map<String, Object> transactionValues = new HashMap<>();
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    CaseService caseService = execution.getProcessEngine().getCaseService();

    Object accountNumberValue = caseService.getVariable(caseInstanceId, ACCOUNT_NUMBER);
    String accountNumber =  String.valueOf(accountNumberValue).equals("0") ? EMPTY_VALUE: getValidString(accountNumberValue);
    String transactionDescription = getValidString(variables.get(TRANSACTION_DESCRIPTION));
    String phoneNumber = getValidString(variables.get(PHONE_NUMBER));

    transactionValues.put(ACCOUNT_NO_CUSTOM_SERVICE, accountNumber);
    transactionValues.put(TRANSACTION_VALUE_CUSTOM_SERVICE, transactionDescription);
    transactionValues.put(PHONE_NUMBER_CUSTOM_SERVICE, phoneNumber);
    return transactionValues;
  }

  private Map<String, Object> setInvoiceValues(Map<String, Object> variables)
  {

    Map<String, Object> invoiceValues = new HashMap<>();

    String branchName = (String) variables.get(BRANCH_NAME);
    int charge = Integer.parseInt(String.valueOf(variables.get(CHARGE)));
    String declarationDateString = String.valueOf(variables.get(DECLARATION_DATE));
    int declarationDate = convertDateStringToInt(declarationDateString);
    String invoiceNumber = (String) variables.get(INVOICE_NUMBER);
    String registerId = String.valueOf(variables.get(REGISTER_ID));
    String type = (String) variables.get(TYPE);
    String taxPayerName = (String) variables.get(TAX_PAYER_NAME);

    invoiceValues.put(BRANCH_NAME_SERVICE, branchName);
    invoiceValues.put(CHARGE, charge);
    invoiceValues.put(DECLARATION_DATE_SERVICE, declarationDate);
    invoiceValues.put(INVOICE_NUM_CUSTOM_SERVICE, invoiceNumber);
    invoiceValues.put(INVOICE_TYPE_NAME_CUSTOM_SERVICE, type);
    invoiceValues.put(PAYMENT_LIST_SERVICE, setPaymentInfo(variables));
    invoiceValues.put(REGISTER_ID_SERVICE, registerId);
    invoiceValues.put(TAX_PAYER_NAME_SERVICE, taxPayerName);
    return invoiceValues;
  }

  private Map<String, Object> setTransactionParams(DelegateExecution execution) throws BpmServiceException
  {
    Map<String, Object> transactionParams = new HashMap<>();

    transactionParams.put(CUSTOM_TRANSACTION_SERVICE, setTransactionValues(execution.getVariables(), execution));
    transactionParams.put(CUSTOM_INVOICE_SERVICE, setInvoiceValues(execution.getVariables()));
    return transactionParams;
  }

  private void checkRequiredVariable(CaseService caseService, String instanceId) throws BpmServiceException
  {
    String transactionType = String.valueOf(caseService.getVariable(instanceId, TRANSACTION_TYPE));
    String accountNumber = String.valueOf(caseService.getVariable(instanceId, BpmBranchBankingConstants.ACCOUNT_NUMBER));

    if (checkAccountNumberValue(accountNumber, transactionType)){
      throw new BpmServiceException(BB_ACCOUNT_NUMBER_NULL_CODE, BB_ACCOUNT_NUMBER_NULL_MESSAGE);
    }
  }
}
