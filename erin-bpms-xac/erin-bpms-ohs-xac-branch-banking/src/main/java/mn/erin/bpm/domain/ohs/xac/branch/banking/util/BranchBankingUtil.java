package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACC;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNTS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_CLASS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_CLASS_TYPE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_NAME;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_NUMBER_ALT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_RECORD_STATUS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_STATUS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_TYPE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_TYPE_C;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_TYPE_LOWERCASE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_TYPE_S;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACCOUNT_TYPE_U;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACC_CURRENCY;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.ACC_LIST;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.AVAILABLE_BALANCE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BANK_CORE_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.BASIC_PAYMENT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CIF;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CURRENCY;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CURRENCY_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CURRENT_BALANCE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CUSTOMER_NAME;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.CUSTOMER_NAME_ALT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.DORMANCY_ACCOUNT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.FAILED_LOGIN_COUNT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.FEE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.FEE_PAYMENT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.FROZEN_ACCOUNT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.HAS_ACCESS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INTEREST_PAYMENT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.IS_ACCOUNT_REGISTERED;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.IS_PRIMARY_ACCOUNT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.JOINT_CUSTOMER_ID;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.JOINT_CUSTOMER_NAME;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.JOINT_HOLDERS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.LAST_LOGGED_DATE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.MATURITY_DATE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.NAME;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.NO_DEBIT_ACCOUNT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.OPEN_DATE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PENALTY_AMOUNT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PHONE_USSD;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PRODUCT_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PRODUCT_CODE_LOWERCASE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.REGISTERED_BRANCH;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.REGISTERED_DATE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.REGISTERED_USER;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.REGISTER_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.SCHEDULED;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TD_DETAILS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TOTAL_AMOUNT;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.USER_STATUS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.USSD_STATUS;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.YES_STRING;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_IS_UNAUTHORIZED_FOR_EXPENSE_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_IS_UNAUTHORIZED_FOR_EXPENSE_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_STATUS_IS_CLOSED_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_STATUS_IS_CLOSED_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_STATUS_IS_DORMANCY_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_STATUS_IS_DORMANCY_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_STATUS_IS_FROZEN_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_STATUS_IS_FROZEN_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_STATUS_IS_NO_DEBIT_ERROR_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.ACCOUNT_STATUS_IS_NO_DEBIT_ERROR_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.USER_INFO_NOT_FOUND_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.USER_INFO_NOT_FOUND_MESSAGE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.USER_NOT_REGISTERED_CODE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchbankingMessagesConstants.USER_NOT_REGISTERED_MESSAGE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE_REF;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BRANCH;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CREATED_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_END_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_HAS_ACCESS;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_JOINT_HOLDERS;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOMER_FULL_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.IS_PRIMARY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.MAIN_ACCOUNTS;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.ISO_SIMPLE_DATE_FORMATTER;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getThousandSepStrWithDigit;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.roundDouble;
import static mn.erin.domain.bpm.util.process.BpmUtils.getFormattedDateString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValue;
import static mn.erin.domain.bpm.util.process.BpmUtils.getStringValueFrmJSONObject;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import mn.erin.domain.bpm.model.branch_banking.AccountInfo;
import mn.erin.domain.bpm.model.branch_banking.account_reference.AccountJointHolder;
import mn.erin.domain.bpm.service.BpmServiceException;

public class BranchBankingUtil
{
  private static final String[] accountClassTypes = { "AA170", "AA180", "AA190", "AA240", "AA250", "AC100", "AC120", "AC200", "AC220" };

  private BranchBankingUtil()
  {
  }

  private static void validateAccountClass(JSONObject response) throws BpmServiceException
  {
    String accountClassType = getValidString(response.get(ACCOUNT_CLASS_TYPE));
    String accountClass = getValidString(response.get(ACCOUNT_CLASS));

    if (accountClassType.equals("TUA") || accountClassType.equals("ODA") || Arrays.asList(accountClassTypes).contains(accountClass))
    {
      throwException(ACCOUNT_IS_UNAUTHORIZED_FOR_EXPENSE_ERROR_CODE, ACCOUNT_IS_UNAUTHORIZED_FOR_EXPENSE_ERROR_MESSAGE);
    }
  }

  private static void validateAccountStatus(JSONObject accountStatus) throws BpmServiceException
  {
    String frozenAccount = getValidString(accountStatus.get(FROZEN_ACCOUNT));
    String dormancyAccount = getValidString(accountStatus.get(DORMANCY_ACCOUNT));
    String noDebitAccount = getValidString(accountStatus.get(NO_DEBIT_ACCOUNT));

    if (frozenAccount.equals(YES_STRING))
    {
      throwException(ACCOUNT_STATUS_IS_FROZEN_ERROR_CODE, ACCOUNT_STATUS_IS_FROZEN_ERROR_MESSAGE);
    }
    if (dormancyAccount.equals(YES_STRING))
    {
      throwException(ACCOUNT_STATUS_IS_DORMANCY_ERROR_CODE, ACCOUNT_STATUS_IS_DORMANCY_ERROR_MESSAGE);
    }
    if (noDebitAccount.equals(YES_STRING))
    {
      throwException(ACCOUNT_STATUS_IS_NO_DEBIT_ERROR_CODE, ACCOUNT_STATUS_IS_NO_DEBIT_ERROR_MESSAGE);
    }

    if (!getValidString(accountStatus.get(ACCOUNT_RECORD_STATUS)).equals("O"))
    {
      throwException(ACCOUNT_STATUS_IS_CLOSED_ERROR_CODE, ACCOUNT_STATUS_IS_CLOSED_ERROR_MESSAGE);
    }
  }

  public static void checkAccountHasValidation(JSONObject accountStatus, boolean hasAccountValidation) throws BpmServiceException
  {
    if (hasAccountValidation)
    {
      validateAccountStatus(accountStatus);
    }
    else if (getValidString(accountStatus.get(ACCOUNT_RECORD_STATUS)).equals("C"))
    {
      throwException(ACCOUNT_STATUS_IS_CLOSED_ERROR_CODE, ACCOUNT_STATUS_IS_CLOSED_ERROR_MESSAGE);
    }
  }

  public static Map<String, Object> toAccountInfo(JSONObject jsonResponse, boolean hasAccountValidation) throws ParseException, BpmServiceException
  {
    if(hasAccountValidation){
      validateAccountClass(jsonResponse);
    }
    JSONObject accountStatus = jsonResponse.has(ACCOUNT_STATUS) ? (JSONObject) jsonResponse.get(ACCOUNT_STATUS) : null;
    if (accountStatus != null && accountStatus.length() > 0)
    {
      checkAccountHasValidation(accountStatus, hasAccountValidation);
    }
    Map<String, Object> accountInfo = new HashMap<>();
    String hasAccess = getValidString(jsonResponse.get(HAS_ACCESS));
    String currency = getValidString(jsonResponse.get(CURRENCY));
    String customerName = getValidString(jsonResponse.get(CUSTOMER_NAME));
    String availableBalanceStr = jsonResponse.has(AVAILABLE_BALANCE) ? jsonResponse.getString(AVAILABLE_BALANCE) : "0";
    String availableBalance = availableBalanceStr.equals(EMPTY_VALUE) ? "0" : availableBalanceStr;
    String currentBalanceStr = jsonResponse.has(CURRENT_BALANCE) ? jsonResponse.getString(CURRENT_BALANCE) : "0";
    String currentBalance = currentBalanceStr.equals(EMPTY_VALUE) ? "0" : currentBalanceStr;
    String accCreatedDate = getValidString(jsonResponse.get(OPEN_DATE));
    JSONObject tdDetails = jsonResponse.getJSONObject(TD_DETAILS);
    String accCloseDate = getStringValueFrmJSONObject(tdDetails, MATURITY_DATE);
    String accountBranch = getValidString(jsonResponse.get("BranchId"));

    accountInfo.put(ACCOUNT_CURRENCY, currency);
    accountInfo.put(ACCOUNT_CCY, currency);
    accountInfo.put(ACCOUNT_NAME, customerName);
    accountInfo.put(ACCOUNT_BALANCE, getThousandSepStrWithDigit(Double.parseDouble(availableBalance), 2));
    accountInfo.put(CUSTOMER_FULL_NAME, customerName);
    accountInfo.put(ACCOUNT_CREATED_DATE, getFormattedDateString(accCreatedDate, ISO_SIMPLE_DATE_FORMATTER));
    accountInfo.put(ACCOUNT_END_DATE, getFormattedDateString(accCloseDate, ISO_SIMPLE_DATE_FORMATTER));
    accountInfo.put(ACCOUNT_BALANCE_REF, getThousandSepStrWithDigit(Double.parseDouble(currentBalance), 2));
    accountInfo.put(ACCOUNT_JOINT_HOLDERS,

        getAccountJointHolders(jsonResponse));
    accountInfo.put(ACCOUNT_BRANCH, accountBranch);
    accountInfo.put(ACCOUNT_HAS_ACCESS, hasAccess);
    return accountInfo;
  }

  public static List<AccountJointHolder> getAccountJointHolders(JSONObject jsonResponse)
  {
    List<AccountJointHolder> jointHolders = new ArrayList<>();

    if (jsonResponse.has(JOINT_HOLDERS) && jsonResponse.get(JOINT_HOLDERS) instanceof JSONObject)
    {
      if (((JSONObject) jsonResponse.get(JOINT_HOLDERS)).length() != 0)
      {
        JSONObject jointHolder = (JSONObject) jsonResponse.get(JOINT_HOLDERS);
        jointHolders.add(setJointHoldersInfo(jointHolder));
      }
    }
    else
    {
      JSONArray responseArray = (JSONArray) jsonResponse.get(JOINT_HOLDERS);
      for (int i = 0; i < responseArray.length(); i++)
      {
        JSONObject jointHolder = (JSONObject) responseArray.get(i);
        jointHolders.add(setJointHoldersInfo(jointHolder));
      }
    }
    return jointHolders;
  }

  private static AccountJointHolder setJointHoldersInfo(JSONObject jointHolder)
  {
    String jointCustomerId = String.valueOf(jointHolder.get(JOINT_CUSTOMER_ID));
    String jointHolderName = (String) jointHolder.get(JOINT_CUSTOMER_NAME);
    return new AccountJointHolder(EMPTY_VALUE, jointHolderName, jointCustomerId);
  }

  public static Map<String, Object> toAccountReference(JSONObject jsonResponse)
  {
    Map<String, Object> accountRefInfo = new HashMap<>();
    String fee = jsonResponse.has(FEE) ? (String) jsonResponse.get(FEE) : String.valueOf(0);

    accountRefInfo.put("currency", getValidString(jsonResponse.get("currency")));
    accountRefInfo.put("branch_code", getValidString(jsonResponse.get("branch_code")));
    accountRefInfo.put("toBranchCode", getValidString(jsonResponse.get("toBranchCode")));
    accountRefInfo.put("toAccountNo", getValidString(jsonResponse.get("toAccountNo")));
    accountRefInfo.put("toAccountCurrency", getValidString(jsonResponse.get("toAccountCurrency")));
    accountRefInfo.put("feesAmount", fee);
    return accountRefInfo;
  }

  public static Map<String, Object> ussdUserInfoToXacSchema(Map<String, Object> source)
  {
    Map<String, Object> ussdInfo = new HashMap<>();
    ussdInfo.put("Cif", getValidString(source.get("customerId")));
    ussdInfo.put("LoginUserName", getValidString(source.get("ussdPhoneNumber")));
    ussdInfo.put("RegBrn", getValidString(source.get("registeredBranch")));

    List<Map<String, Object>> filteredAccounts = new ArrayList<>();
    List<Map<String, Object>> unfilteredAllAccounts = new ArrayList<>();

    if (source.get(MAIN_ACCOUNTS) instanceof List && source.get("allAccounts") instanceof List)
    {
      filteredAccounts = (List<Map<String, Object>>) source.get(MAIN_ACCOUNTS);
      unfilteredAllAccounts = (List<Map<String, Object>>) source.get("allAccounts");
    }

    Map<String, Object> accounts = new HashMap<>();
    List<Map<String, Object>> accountList = new ArrayList();

    for (Map<String, Object> account : filteredAccounts)
    {
      accountList.add(convertUSSDInfoToXacService(account));
    }

    putComparedFilteredAndUnfilteredAccounts(filteredAccounts, unfilteredAllAccounts, accountList);

    accounts.put("Account", accountList);
    ussdInfo.put("Accounts", accounts);

    return ussdInfo;
  }

  public static Map<String, Object> convertUSSDInfoToXacService(Map<String, Object> source)
  {
    Map<String, Object> dest = new HashMap<>();
    boolean isPrimary;
    try
    {
      isPrimary = (boolean) source.get(IS_PRIMARY);
    }
    catch (Exception e)
    {
      isPrimary = Boolean.parseBoolean(getValidString(source.get(IS_PRIMARY)));
    }
    dest.put("IsPrimaryAccount", isPrimary ? "Y" : "N");
    dest.put("AccountNumber", getValidString(source.get(ACCOUNT_NUMBER)));
    dest.put("AccountType", getValidString(source.get("accountType")));
    dest.put("ProductCode", getValidString(source.get("productCode")));
    dest.put("CurrencyCode", getValidString(source.get("currencyCode")));
    return dest;
  }

  public static Map<String, Object> toUSSDInfo(JSONObject jsonResponse) throws BpmServiceException
  {
    Map<String, Object> ussdInfo = new HashMap<>();
    String code = jsonResponse.has(CODE) ? String.valueOf(jsonResponse.get(CODE)) : NULL_STRING;
    String status = jsonResponse.has(USSD_STATUS) ? String.valueOf(jsonResponse.get(USSD_STATUS)) : NULL_STRING;

    if (code.equals(NULL_STRING) && status.equals(NULL_STRING))
    {
      throw new BpmServiceException(USER_INFO_NOT_FOUND_CODE, USER_INFO_NOT_FOUND_MESSAGE);
    }
    else if ((!jsonResponse.has(REGISTER_NUMBER) || !jsonResponse.has(CIF)))
    {
      throw new BpmServiceException(USER_NOT_REGISTERED_CODE, USER_NOT_REGISTERED_MESSAGE);
    }
    String phoneUSSD = getValidString(jsonResponse.get(PHONE_USSD));
    String cif = getValidString(jsonResponse.get(CIF));
    String customerName = getValidString(jsonResponse.get(CUSTOMER_NAME_ALT));
    String registerNo = getValidString(jsonResponse.get(REGISTER_NUMBER));
    String phoneCore = getValidString(jsonResponse.get(BANK_CORE_NUMBER));
    String userStatus = getValidString(jsonResponse.get(USER_STATUS));
    String registeredBranch = getValidString(jsonResponse.get(REGISTERED_BRANCH));
    String regDate = getValidString(jsonResponse.get(REGISTERED_DATE));
    String regMakerId = getValidString(jsonResponse.get(REGISTERED_USER));
    String lastLogged = getValidString(jsonResponse.get(LAST_LOGGED_DATE));
    String id = getValidString(jsonResponse.get("ID"));

    Integer failedCount = jsonResponse.optInt(FAILED_LOGIN_COUNT, 0);

    List<Map<String, Object>> accounts = new ArrayList<>();

    JSONObject jsonAccounts = jsonResponse.getJSONObject(ACCOUNTS);
    JSONArray jsonAccount = jsonAccounts.getJSONArray(ACCOUNT);
    for (int i = 0; i < jsonAccount.length(); i++)
    {
      Map<String, Object> tmp = toUSSDAccountInfo(jsonAccount.getJSONObject(i));
      accounts.add(tmp);
    }

    List<Map<String, Object>> filteredAccounts = accounts.stream()
        .filter(account -> !(account.get(PRODUCT_CODE_LOWERCASE).toString().startsWith("AC") &&
            !account.get(PRODUCT_CODE_LOWERCASE).toString().startsWith("AG")) && isCorrectAccountType(account.get(ACCOUNT_TYPE_LOWERCASE).toString()))
        .collect(Collectors.toList());

    ussdInfo.put(MAIN_ACCOUNTS, filteredAccounts);
    ussdInfo.put("allAccounts", accounts);
    ussdInfo.put("customerId", cif);
    ussdInfo.put("customerIdTwo", cif);
    ussdInfo.put("coreNumber", phoneCore);
    ussdInfo.put("ussdPhoneNumber", phoneUSSD);
    ussdInfo.put("customerStatus", userStatus);
    ussdInfo.put("registeredDt", regDate);
    ussdInfo.put("registeredBranch", registeredBranch);
    ussdInfo.put("registeredEmployee", regMakerId);
    ussdInfo.put("customerRegister", registerNo);
    ussdInfo.put("failedLoginAttempt", failedCount);
    ussdInfo.put("customerFullName", customerName);
    ussdInfo.put("lastLoggedDt", lastLogged);
    ussdInfo.put("checkRegistration", !StringUtils.isBlank(phoneUSSD));
    ussdInfo.put("id", id);
    return ussdInfo;
  }

  private static Map<String, Object> toUSSDAccountInfo(JSONObject jsonAccount)
  {
    Map<String, Object> accountInfo = new HashMap<>();
    Boolean isRegistered = getValidString(jsonAccount.get(IS_ACCOUNT_REGISTERED)).equals("Y");
    Boolean isPrimary = getValidString(jsonAccount.get(IS_PRIMARY_ACCOUNT)).equals("Y");
    String productCode = getValidString(jsonAccount.get(PRODUCT_CODE));
    String accountType = getValidString(jsonAccount.get(ACCOUNT_TYPE));
    String accountNumber = getValidString(jsonAccount.get(ACCOUNT_NUMBER_ALT));
    String currencyCode = getValidString(jsonAccount.get(CURRENCY_CODE));

    accountInfo.put("isRegistered", isRegistered);
    accountInfo.put(IS_PRIMARY, isPrimary);
    accountInfo.put(PRODUCT_CODE_LOWERCASE, productCode);
    accountInfo.put(ACCOUNT_TYPE_LOWERCASE, accountType);
    accountInfo.put(ACCOUNT_NUMBER, accountNumber);
    accountInfo.put("currencyCode", currencyCode);

    return accountInfo;
  }

  public static Map<String, Object> toLoanAccountInfo(JSONObject jsonResponse)
  {
    Map<String, Object> loanAccountInfo = new HashMap<>();
    Map<String, Object> closingLoan = new HashMap<>();
    Map<String, Object> scheduledPayment = new HashMap<>();
    Map<String, Object> nonScheduledPayment = new HashMap<>();

    double pTotalOvdu = Double.parseDouble(getValidString(jsonResponse.get("PTotalOvdu")));
    double totalIntOvdu = Double.parseDouble(getValidString(jsonResponse.get("TotalIntOvdu")));

    loanAccountInfo.put("customerFullName", getValidString(jsonResponse.get("AccountName")));
    loanAccountInfo.put("currencyValue", getValidString(jsonResponse.get("AccountCcy")));
    loanAccountInfo.put("loanBalance", getValidString(jsonResponse.get("LnClrBal")));

    closingLoan.put(INTEREST_PAYMENT, getValidString(jsonResponse.get("IntAmt")));
    closingLoan.put(PENALTY_AMOUNT, getValidString(jsonResponse.get("PenaltyAmt")));
    closingLoan.put(FEE_PAYMENT, getValidString(jsonResponse.get("MiscFee")));
    closingLoan.put(TOTAL_AMOUNT, getValidString(jsonResponse.get("ClosingAmount")));

    scheduledPayment.put(INTEREST_PAYMENT, totalIntOvdu);
    scheduledPayment.put(BASIC_PAYMENT, pTotalOvdu);
    scheduledPayment.put(TOTAL_AMOUNT, roundDouble((totalIntOvdu + pTotalOvdu), 2));

    double totalAmt = Double.parseDouble(getValidString(jsonResponse.get("TotalAmt")));
    nonScheduledPayment.put(INTEREST_PAYMENT, getValidString(jsonResponse.get("IntAmt")));
    nonScheduledPayment.put(PENALTY_AMOUNT, getValidString(jsonResponse.get("PenaltyAmt")));
    nonScheduledPayment.put(TOTAL_AMOUNT, roundDouble(totalAmt, 2));

    loanAccountInfo.put("closingLoanPayment", closingLoan);
    loanAccountInfo.put("scheduledPayment", scheduledPayment);
    loanAccountInfo.put("nonScheduledPayment", nonScheduledPayment);

    loanAccountInfo.put(SCHEDULED, roundDouble((totalIntOvdu + pTotalOvdu), 2));

    return loanAccountInfo;
  }

  public static Map<String, AccountInfo> tocAccountInfoList(JSONObject response)
  {
    Map<String, AccountInfo> accountInfoMap = new HashMap<>();
    JSONArray responseArray = response.getJSONArray(ACC_LIST);

    for (int index = 0; index < responseArray.length(); index++)
    {
      JSONObject jsonObject = (JSONObject) responseArray.get(index);
      accountInfoMap.put(getStringValue(jsonObject.get(ACC)), setCheckBasicInfo(jsonObject));
    }

    return accountInfoMap;
  }

  private static AccountInfo setCheckBasicInfo(JSONObject responseObject)
  {
    String accountId = getStringValue(responseObject.get(ACC));
    String customerName = getStringValue(responseObject.get(NAME));
    String accCurrency = getStringValue(responseObject.get(ACC_CURRENCY));

    return new AccountInfo(null, null, accountId, null, customerName, accCurrency, false);
  }

  private static boolean isCorrectAccountType(String type)
  {
    return type.equals(ACCOUNT_TYPE_C) || type.equals(ACCOUNT_TYPE_U) || type.equals(ACCOUNT_TYPE_S);
  }

  public static Map<String, Object> toLoanPayment(JSONObject responseObject)
  {
    Map<String, Object> loanPaymentInfo = new HashMap<>();
    loanPaymentInfo.put("status", getStringValueFrmJSONObject(responseObject, "Status"));
    loanPaymentInfo.put("transactionIdScheduled", getStringValueFrmJSONObject(responseObject, "TranId1"));
    loanPaymentInfo.put("transactionIdUnscheduled", getStringValueFrmJSONObject(responseObject, "TranId2"));
    loanPaymentInfo.put("appnum", getStringValueFrmJSONObject(responseObject, "Appnum"));
    loanPaymentInfo.put("trnDate1", getStringValueFrmJSONObject(responseObject, "TranDt1"));
    loanPaymentInfo.put("trnDate2", getStringValueFrmJSONObject(responseObject, "TranDt2"));
    return loanPaymentInfo;
  }

  public static Map<String, Object> toScheduledLoanPayment(JSONObject responseObject)
  {
    Map<String, Object> loanPaymentInfo = new HashMap<>();
    loanPaymentInfo.put("transactionId", responseObject.get("TrnId"));
    loanPaymentInfo.put("transactionDate", responseObject.get("TrnDt"));
    return loanPaymentInfo;
  }

  public static void throwException(String errorCode, String errorMessage) throws BpmServiceException
  {
    throw new BpmServiceException(errorCode, errorMessage);
  }

  private static void putComparedFilteredAndUnfilteredAccounts(List<Map<String, Object>> filteredAccounts, List<Map<String, Object>> unfilteredAllAccounts,
      List<Map<String, Object>> accountList)
  {
    List<String> filteredAccountNumbers = filteredAccounts.stream().map(filteredAccount -> (String) filteredAccount.get(ACCOUNT_NUMBER))
        .collect(Collectors.toList());

    for (Map<String, Object> unfilteredAllAccount : unfilteredAllAccounts)
    {
      String unfilteredAccountNumber = (String) unfilteredAllAccount.get(ACCOUNT_NUMBER);
      if (!filteredAccountNumbers.contains(unfilteredAccountNumber))
      {
        accountList.add(convertUSSDInfoToXacService(unfilteredAllAccount));
      }
    }
  }
}
