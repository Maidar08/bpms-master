package service_task.billing.tax;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.bpm.BpmBranchBankingConstants;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.billing.MakeTaxTransaction;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BRANCH;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_HAS_ACCESS;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ASSET_DETAIL;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BLNC;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BRANCH_CODE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.BRANCH_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.GROUP_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_NO;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAID_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_FULL;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAY_MORE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PERIOD;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PERIOD_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PIN;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PREVIOUS_INVOICE_AMOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.STATE_ACCOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.STATE_ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SUB_BRANCH_CODE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SUB_BRANCH_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_ACCOUNT;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_PAYER_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_TYPE_CODE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TAX_TYPE_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TIN;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESC;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.YEAR;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_INVALID_INVOICE_AMOUNT_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_INVALID_INVOICE_AMOUNT_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_PHONE_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_PHONE_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_REGISTER_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_REGISTER_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_DESCRIPTION_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_TRANSACTION_DESCRIPTION_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.checkAccountNumberValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;
import static mn.erin.domain.bpm.util.process.BpmUtils.toUppercaseRegisterNum;

/**
 * @author Lkhagvadorj.A
 **/

public class MakeTaxTransactionTask implements JavaDelegate
{
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MakeTaxTransactionTask.class);
  private final BranchBankingService branchBankingService;

  public MakeTaxTransactionTask(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();
    String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    double invoiceAmount = Double.parseDouble(String.valueOf(execution.getVariable(INVOICE_AMOUNT)));
    double previousInvoiceAmount = Double.parseDouble(String.valueOf(execution.getVariable(PREVIOUS_INVOICE_AMOUNT)));

    String accountHasAccess = getValidString(caseService.getVariable(instanceId, ACCOUNT_HAS_ACCESS));
    String accountBalanceString = accountHasAccess.equals("0") ? String.valueOf(caseService.getVariable(instanceId, BLNC)) :
        String.valueOf(caseService.getVariable(instanceId, ACCOUNT_BALANCE));
    String payMore = getValidString(caseService.getVariable(instanceId, PAY_MORE));

    if (!StringUtils.isBlank(accountBalanceString) && !accountBalanceString.equals("null") && !accountBalanceString.equals("0")
        && invoiceAmount > Double.parseDouble(accountBalanceString))
    {
      throw new ProcessTaskException(BB_INVALID_INVOICE_AMOUNT_CODE, BB_INVALID_INVOICE_AMOUNT_MESSAGE);
    }

    if (StringUtils.equals("0", payMore) && invoiceAmount > previousInvoiceAmount)
    {
      throw new ProcessTaskException(BB_INVALID_INVOICE_AMOUNT_CODE, BB_INVALID_INVOICE_AMOUNT_MESSAGE);
    }

    checkRequiredVariables(execution.getVariables(), caseService, instanceId);
    Map<String, String> transactionValues = getTransactionValues(caseService.getVariables(instanceId));
    transactionValues.put(CASE_INSTANCE_ID, instanceId);

    MakeTaxTransaction makeTaxTransaction = new MakeTaxTransaction(branchBankingService);
    Map<String, String> outputParam = makeTaxTransaction.execute(transactionValues);

    String bankTransactionNo = outputParam.get(TRANSACTION_NUMBER);
    // commented because value date is right date to download transaction document
//    String paidDate = outputParam.get(PAID_DATE);
    String valueDate = outputParam.get("valueDate");
    execution.setVariable(TRANSACTION_NUMBER, bankTransactionNo);
    execution.setVariable(PAID_DATE, valueDate);
    caseService.setVariable(instanceId, TRANSACTION_NUMBER, bankTransactionNo);
    caseService.setVariable(instanceId, PAID_DATE, valueDate);
    LOG.info("######################## Tax transaction is successfully made with transaction id = [{}], \n process instance id = [{}]", bankTransactionNo,
        instanceId);
  }

  private void checkRequiredVariables(Map<String, Object> variables, CaseService caseService, String instanceId) throws BpmServiceException, UnsupportedEncodingException
  {
    String accountPin = String.valueOf(variables.get(REGISTER_NUMBER));
    accountPin = toUppercaseRegisterNum(accountPin);
    accountPin = URLDecoder.decode(accountPin, StandardCharsets.UTF_8.name());
    if (StringUtils.isBlank(accountPin) || accountPin.equals(NULL_STRING))
    {
      throw new BpmServiceException(BB_REGISTER_NUMBER_NULL_CODE, BB_REGISTER_NUMBER_NULL_MESSAGE);
    }

    String customerPhoneNumber = String.valueOf(variables.get(PHONE_NUMBER));
    if (StringUtils.isBlank(customerPhoneNumber) || customerPhoneNumber.equals(NULL_STRING))
    {
      throw new BpmServiceException(BB_PHONE_NUMBER_NULL_CODE, BB_PHONE_NUMBER_NULL_MESSAGE);
    }

    String trxdescription = String.valueOf(variables.get(TRANSACTION_DESC));
    if (StringUtils.isBlank(trxdescription) || trxdescription.equals(NULL_STRING))
    {
      throw new BpmServiceException(BB_TRANSACTION_DESCRIPTION_NULL_CODE, BB_TRANSACTION_DESCRIPTION_NULL_MESSAGE);
    }

     String transactionType = String.valueOf(caseService.getVariable(instanceId, TRANSACTION_TYPE));
     String accountNumber = String.valueOf(caseService.getVariable(instanceId, ACCOUNT_NUMBER));

     if(checkAccountNumberValue(accountNumber, transactionType)) {
      throw new BpmServiceException(BB_ACCOUNT_NUMBER_NULL_CODE, BB_ACCOUNT_NUMBER_NULL_MESSAGE);
    }
  }

  private Map<String, String> getTransactionValues(Map<String, Object> variables) throws UnsupportedEncodingException
  {

    Map<String, String> transactionValues = new HashMap<>();

    String paymentType = "2";
    String transactionType = String.valueOf(variables.get(TRANSACTION_TYPE));
    transactionType = transactionType.equals("Бэлэн") ? "Y" : "N";
    double invoiceAmount = Double.parseDouble(String.valueOf(variables.get(INVOICE_AMOUNT)));
    BigDecimal invoiceAmountBigDecimal = BigDecimal.valueOf(invoiceAmount).setScale(2, RoundingMode.HALF_UP);
    String invoiceAmountString = invoiceAmountBigDecimal.toString();

    String tin = String.valueOf(variables.get(TIN));
    String taxPayerName = String.valueOf(variables.get(TAX_PAYER_NAME));
    String trxdescription = String.valueOf(variables.get(TRANSACTION_DESC));

    Object accountNumberValue = variables.get(ACCOUNT_NUMBER);
    String accountNumber = String.valueOf(accountNumberValue).equals("0") ? EMPTY_VALUE : getValidString(accountNumberValue);

    String accountName = getValidString(variables.get(ACCOUNT_NAME));
    String accountPin = String.valueOf(variables.get(REGISTER_NUMBER));
    accountPin = toUppercaseRegisterNum(accountPin);
    accountPin = URLDecoder.decode(accountPin, StandardCharsets.UTF_8.name());
    String payerInfo = accountPin;
    String pin = String.valueOf(variables.get(PIN));

    String customerPhoneNumber = String.valueOf(variables.get(PHONE_NUMBER));

    String invoiceNumber = String.valueOf(variables.get(INVOICE_NUMBER));
    String stateAccount = String.valueOf(variables.get(STATE_ACCOUNT));
    String stateAccountName = String.valueOf(variables.get(STATE_ACCOUNT_NAME));

    // additional variables
    String assetDetail = String.valueOf(variables.get(ASSET_DETAIL));
    String invoiceType = String.valueOf(variables.get(INVOICE_TYPE));
    Integer payFull = (Integer) variables.get(PAY_FULL);
    String period = String.valueOf(variables.get(PERIOD));
    String CCY = String.valueOf(variables.get(BpmBranchBankingConstants.CCY));
    String taxAccountName = String.valueOf(variables.get(TAX_ACCOUNT_NAME));
    String payMore = String.valueOf(variables.get(PAY_MORE));
    String amount = String.valueOf(variables.get(AMOUNT));
    String branchCode = String.valueOf(variables.get(BRANCH_CODE));
    String branchName = String.valueOf(variables.get(BRANCH_NAME));
    String periodType = String.valueOf(variables.get(PERIOD_TYPE));
    String subBranchName = String.valueOf(variables.get(SUB_BRANCH_NAME));
    String taxAccount = String.valueOf(variables.get(TAX_ACCOUNT));
    String taxTypeName = String.valueOf(variables.get(TAX_TYPE_NAME));
    String year = String.valueOf(variables.get(YEAR));
    String subBranchCode = String.valueOf(variables.get(SUB_BRANCH_CODE));
    String accountBranch = String.valueOf(variables.get(ACCOUNT_BRANCH));
    String trxBranch = String.valueOf(variables.get(GROUP_ID));
    String taxTypeCode = String.valueOf(variables.get(TAX_TYPE_CODE));

    if (accountBranch.equals("null"))
    {
      accountBranch = "";
    }

    transactionValues.put("payType", paymentType);
    transactionValues.put("cash", transactionType);
    transactionValues.put("paymentAmount", String.valueOf(invoiceAmountString));
    transactionValues.put("payerInfo", payerInfo);
    transactionValues.put("tin", tin);
    transactionValues.put(TAX_PAYER_NAME, taxPayerName);
    transactionValues.put("trxdescription", trxdescription);

    transactionValues.put("accountNumber", accountNumber);
    transactionValues.put("accountName", accountName);
    transactionValues.put("accountPin", accountPin);

    transactionValues.put("bankTellerInfo", "teller");
    transactionValues.put("phoneNo", customerPhoneNumber);

    transactionValues.put(INVOICE_NO, invoiceNumber);
    transactionValues.put("pin", pin);
    transactionValues.put("stateAccount", stateAccount);
    transactionValues.put("stateAccountName", stateAccountName);
    transactionValues.put("LanguageId", "en");

    // additional variables
    transactionValues.put(ASSET_DETAIL, assetDetail);
    transactionValues.put(INVOICE_TYPE, invoiceType);
    transactionValues.put(PAY_FULL, String.valueOf(payFull));
    transactionValues.put("period", period);
    transactionValues.put(BpmBranchBankingConstants.CCY.toUpperCase(), CCY);
    transactionValues.put(TAX_ACCOUNT_NAME, taxAccountName);
    transactionValues.put(PAY_MORE, payMore);
    transactionValues.put(AMOUNT, amount);
    transactionValues.put(BRANCH_CODE, branchCode);
    transactionValues.put(BRANCH_NAME, branchName);
    transactionValues.put(PERIOD_TYPE, periodType);
    transactionValues.put(SUB_BRANCH_NAME, subBranchName);
    transactionValues.put(TAX_ACCOUNT, taxAccount);
    transactionValues.put(TAX_TYPE_NAME, taxTypeName);
    transactionValues.put(YEAR, year);
    transactionValues.put(SUB_BRANCH_CODE, subBranchCode);
    transactionValues.put(ACCOUNT_BRANCH, accountBranch);
    transactionValues.put("trxBranch", trxBranch);
    transactionValues.put(TAX_TYPE_CODE, taxTypeCode);
    return transactionValues;
  }
}
