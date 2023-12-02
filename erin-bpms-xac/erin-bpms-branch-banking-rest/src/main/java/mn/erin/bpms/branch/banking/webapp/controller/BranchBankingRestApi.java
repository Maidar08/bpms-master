package mn.erin.bpms.branch.banking.webapp.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.branch.banking.webapp.model.RestCompletedFormField;
import mn.erin.bpms.branch.banking.webapp.model.RestCustomInvoice;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.branch_banking.AccountInfo;
import mn.erin.domain.bpm.model.branch_banking.CustomInvoice;
import mn.erin.domain.bpm.model.branch_banking.PaymentInfo;
import mn.erin.domain.bpm.model.branch_banking.TaxInvoice;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.CheckAccountNames;
import mn.erin.domain.bpm.usecase.branch_banking.CheckAccountNamesInput;
import mn.erin.domain.bpm.usecase.branch_banking.GetAccountInfo;
import mn.erin.domain.bpm.usecase.branch_banking.GetAccountInfoInput;
import mn.erin.domain.bpm.usecase.branch_banking.GetAccountReference;
import mn.erin.domain.bpm.usecase.branch_banking.GetLoanAccountInfo;
import mn.erin.domain.bpm.usecase.branch_banking.billing.GetBillingTins;
import mn.erin.domain.bpm.usecase.branch_banking.billing.GetBillingTinsInput;
import mn.erin.domain.bpm.usecase.branch_banking.billing.GetCustomInfoList;
import mn.erin.domain.bpm.usecase.branch_banking.billing.GetTaxInfoList;
import mn.erin.infrastucture.rest.common.response.RestEntity;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpms.branch.banking.webapp.constants.BranchBankingRestConstants.DATE_FORMAT;
import static mn.erin.bpms.branch.banking.webapp.constants.BranchBankingRestConstants.INC_DATE_FORMAT;
import static mn.erin.bpms.branch.banking.webapp.util.BranchBankingRestUtils.getFormFieldById;
import static mn.erin.bpms.branch.banking.webapp.util.BranchBankingRestUtils.getSelectedFieldOptionIdByFieldID;
import static mn.erin.bpms.branch.banking.webapp.util.BranchBankingRestUtils.getValuesFromMap;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BALANCE_REF;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_BRANCH;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CREATED_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_CURRENCY;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_END_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_HAS_ACCESS;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_JOINT_HOLDERS;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CUSTOMER_FULL_NAME;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.INVOICE_NO;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SEARCH_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.SEARCH_VALUE_CUSTOM;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.VALUE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOM_SEARCH_TYPE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOM_SEARCH_TYPE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TAX_SEARCH_TYPE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TAX_SEARCH_TYPE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TAX_SEARCH_VALUE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TAX_SEARCH_VALUE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.TYPE;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertDoubleToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.toUppercaseRegisterNum;

@RestController
@RequestMapping(value = "", name = "Provides branch banking tax request API.")
public class BranchBankingRestApi
{
  private final BranchBankingService branchBankingService;
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;

  @Inject
  public BranchBankingRestApi(BranchBankingService branchBankingService, AuthenticationService authenticationService, MembershipRepository membershipRepository)
  {
    this.branchBankingService = branchBankingService;
    this.authenticationService = authenticationService;
    this.membershipRepository = membershipRepository;
  }

  @PostMapping(value = "/tax-invoice/instanceId/{instanceId}")
  public ResponseEntity<RestResult> getTaxInvoices(@PathVariable String instanceId, @RequestBody String regNum)
      throws UseCaseException, UnsupportedEncodingException
  {
    if (StringUtils.isBlank(regNum))
    {
      throwParameterMissingException(REGISTER_NUMBER);
    }

    if (StringUtils.isBlank(instanceId))
    {
      throwParameterMissingException(CASE_INSTANCE_ID);
    }
    regNum = URLDecoder.decode(regNum, StandardCharsets.UTF_8.name());
    regNum = toUppercaseRegisterNum(regNum);
    GetBillingTinsInput input = new GetBillingTinsInput(instanceId, regNum);
    GetBillingTins getBillingTins = new GetBillingTins(branchBankingService);
    return ResponseEntity.ok(RestEntity.of(getBillingTins.execute(input)));
  }

  @PostMapping("/tax-info/instanceId/{instanceId}")
  public ResponseEntity<RestResult> getTaxInfoByType(@PathVariable String instanceId, @RequestBody List<RestCompletedFormField> restFormFields)
      throws BpmInvalidArgumentException, UseCaseException, UnsupportedEncodingException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
    if (null == restFormFields)
    {
      throw new BpmInvalidArgumentException(PARAMETER_NULL_CODE, PARAMETER_NULL_MESSAGE);
    }
    GetTaxInfoList getTaxInfoList = new GetTaxInfoList(branchBankingService);
    Map<String, Object> input = setTaxInfoData(restFormFields);
    input.put(CASE_INSTANCE_ID, instanceId);
    List<TaxInvoice> taxInvoiceList = getTaxInfoList.execute(input);

    if (!input.get(TYPE).equals(INVOICE_NO))
    {
      List<TaxInvoice> newTaxInvoiceList = new ArrayList<>();
      for (TaxInvoice taxInvoice : taxInvoiceList)
      {
        String invoiceNumber = taxInvoice.getInvoiceNumber();
        input.put(TYPE, INVOICE_NO);
        input.put(VALUE, invoiceNumber);
        List<TaxInvoice> taxInvoices = getTaxInfoList.execute(input);
        newTaxInvoiceList.addAll(taxInvoices);
      }
      return RestResponse.success(newTaxInvoiceList);
    }
    return RestResponse.success(taxInvoiceList);
  }

  @PostMapping("/custom-info/instanceId/{instanceId}")
  public ResponseEntity<RestResult> getCustomInfoByType(@PathVariable String instanceId, @RequestBody List<RestCompletedFormField> formFields)
      throws BpmInvalidArgumentException,
      UseCaseException
  {
    if (null == formFields)
    {
      throw new BpmInvalidArgumentException(PARAMETER_NULL_CODE, PARAMETER_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    GetCustomInfoList getCustomInfoList = new GetCustomInfoList(branchBankingService);
    Map<String, Object> input = setCustomInfoData(formFields);
    input.put(CASE_INSTANCE_ID, instanceId);
    List<CustomInvoice> customInvoiceList = getCustomInfoList.execute(input);
    List<RestCustomInvoice> restCustomInvoices = toRestCustomInvoice(customInvoiceList);

    return RestResponse.success(restCustomInvoices);
  }

  @GetMapping("/account-info/{accountId}/instanceId/{instanceId}")
  public ResponseEntity<RestResult> getAccountInfo(@PathVariable String accountId, @PathVariable String instanceId)
      throws BpmInvalidArgumentException, UseCaseException
  {
    if (StringUtils.isBlank(accountId))
    {
      throw new BpmInvalidArgumentException(PARAMETER_NULL_CODE, PARAMETER_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
    GetAccountInfo useCase = new GetAccountInfo(branchBankingService);
    GetAccountInfoInput input = new GetAccountInfoInput(instanceId, accountId, true);
    Map<String, Object> accountInfoMap = useCase.execute(input);

    List<String> parametersId = Arrays.asList(ACCOUNT_NAME, ACCOUNT_CURRENCY, ACCOUNT_BALANCE, ACCOUNT_BRANCH, ACCOUNT_HAS_ACCESS);
    Map<String, Object> output = getValuesFromMap(accountInfoMap, parametersId);
    if(output.containsKey(ACCOUNT_HAS_ACCESS) && output.get(ACCOUNT_HAS_ACCESS).equals("0")){
      output.put("blnc",  output.get(ACCOUNT_BALANCE));
      output.put(ACCOUNT_BALANCE, Long.parseLong("999999999999"));
    }
    return RestResponse.success(output);
  }

  @GetMapping("/account-reference/{accountId}/{instanceId}")
  public ResponseEntity<RestResult> getAccountReference(@PathVariable String accountId, @PathVariable String instanceId)
      throws BpmInvalidArgumentException, UseCaseException
  {
    if (StringUtils.isBlank(accountId))
    {
      throw new BpmInvalidArgumentException(PARAMETER_NULL_CODE, PARAMETER_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    GetAccountInfoInput getAccountInfoInput = new GetAccountInfoInput(instanceId, accountId, false);
    GetAccountInfo getAccountInfo = new GetAccountInfo(branchBankingService);

    GetAccountReference getAccountReference = new GetAccountReference(branchBankingService, authenticationService, membershipRepository);

    Map<String, Object> accountInfoMap = getAccountInfo.execute(getAccountInfoInput);
    List<String> parametersId = Arrays.asList(CUSTOMER_FULL_NAME, ACCOUNT_CCY, ACCOUNT_BALANCE_REF, ACCOUNT_CREATED_DATE, ACCOUNT_END_DATE,
        ACCOUNT_JOINT_HOLDERS, ACCOUNT_HAS_ACCESS);

    Map<String, Object> accountInfo = getValuesFromMap(accountInfoMap, parametersId);
    Map<String, Object> accountReference = getAccountReference.execute(getAccountInfoInput);

    accountInfo.putAll(accountReference);
    if(accountInfo.containsKey(ACCOUNT_HAS_ACCESS) && accountInfo.get(ACCOUNT_HAS_ACCESS).equals("0")){
      accountInfo.put("blnc",  accountInfo.get(ACCOUNT_BALANCE_REF));
      accountInfo.put(ACCOUNT_BALANCE_REF, Long.parseLong("999999999999"));
    }
    return RestResponse.success(accountInfo);
  }

  @PostMapping("/checkAccountsInfo/{instanceId}")
  public ResponseEntity<RestResult> checkAccountsInfo(@PathVariable String instanceId, @RequestBody List<AccountInfo> customerInfos)
      throws BpmInvalidArgumentException, UseCaseException
  {
    if (null == customerInfos)
    {
      throw new BpmInvalidArgumentException(PARAMETER_NULL_CODE, PARAMETER_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    CheckAccountNamesInput input = new CheckAccountNamesInput(customerInfos, instanceId);
    CheckAccountNames useCase = new CheckAccountNames(branchBankingService);

    Map<String, Object> responseMap = useCase.execute(input);

    return RestResponse.success(responseMap);
  }

  @GetMapping(value = "/loanPaymentInfo/{instanceId}/{accountNumber}")
  public ResponseEntity<RestResult> getLoanPaymentInfo(@PathVariable String instanceId, @PathVariable String accountNumber)
      throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(accountNumber))
    {
      throw new BpmInvalidArgumentException(BB_ACCOUNT_NUMBER_NULL_CODE, BB_ACCOUNT_NUMBER_NULL_MESSAGE);
    }

    GetLoanAccountInfo getLoanAccountInfo = new GetLoanAccountInfo(branchBankingService, instanceId);
    return RestResponse.success(getLoanAccountInfo.execute(accountNumber));
  }

  private Map<String, Object> setCustomInfoData(List<RestCompletedFormField> formFields) throws BpmInvalidArgumentException
  {
    String searchTypeId = getSelectedFieldOptionIdByFieldID(formFields, SEARCH_TYPE);
    if (StringUtils.isBlank(searchTypeId))
    {
      throw new BpmInvalidArgumentException(CUSTOM_SEARCH_TYPE_NULL_CODE, CUSTOM_SEARCH_TYPE_NULL_MESSAGE);
    }
    RestCompletedFormField searchValueField = getFormFieldById(formFields, SEARCH_VALUE_CUSTOM);

    Object searchValue = Objects.requireNonNull(searchValueField).getFormFieldValue().getDefaultValue();
    String type = null;

    switch (searchTypeId)
    {
    case "invoiceNumber":
      type = "invoiceNum";
      break;
    case "invoiceBarcode":
      type = "barcode";
      break;
    case "registerNumber":
      type = "RegisterNo";
      break;
    default:
      break;
    }

    Map<String, Object> returnMap = new HashMap<>();
    returnMap.put("type", type);
    returnMap.put("value", searchValue);
    return returnMap;
  }

  private List<RestCustomInvoice> toRestCustomInvoice(List<CustomInvoice> customInvoiceList)
  {
    List<RestCustomInvoice> restCustomInvoiceList = new ArrayList<>();

    if (customInvoiceList.isEmpty())
    {
      return restCustomInvoiceList;
    }

    for (CustomInvoice customInvoice : customInvoiceList)
    {
      String branchName = customInvoice.getBranchName();
      String invoiceNumber = customInvoice.getInvoiceNumber();
      String type = customInvoice.getInvoiceTypeName();
      String taxPayerName = customInvoice.getTaxPayerName();
      String registerId = customInvoice.getRegisterNumber();

      String charge = customInvoice.getCharge();

      String declarationDate = customInvoice.getDeclarationDate();
      String dateString = formatDate(declarationDate);
      BigDecimal paymentAmount = new BigDecimal(0);

      List<PaymentInfo> paymentList = customInvoice.getPaymentInfoList();

      for (PaymentInfo paymentInfo : paymentList)
      {
        paymentAmount = paymentAmount.add(paymentInfo.getPaymentAmount());
      }
      String paymentAmountStr = convertDoubleToString(String.valueOf(paymentAmount), 3);
      restCustomInvoiceList.add(
          new RestCustomInvoice(dateString, branchName, invoiceNumber, taxPayerName, registerId, charge, type, paymentAmountStr, paymentList));
    }
    return restCustomInvoiceList;
  }

  private Map<String, Object> setTaxInfoData(List<RestCompletedFormField> restFormFields) throws BpmInvalidArgumentException, UnsupportedEncodingException
  {
    String searchType = getSelectedFieldOptionIdByFieldID(restFormFields, "searchType");
    if (StringUtils.isBlank(searchType))
    {
      throw new BpmInvalidArgumentException(TAX_SEARCH_TYPE_NULL_CODE, TAX_SEARCH_TYPE_NULL_MESSAGE);
    }

    RestCompletedFormField searchValueField = getFormFieldById(restFormFields, "searchValueTax");
    if (null == searchValueField)
    {
      throw new BpmInvalidArgumentException(TAX_SEARCH_VALUE_NULL_CODE, TAX_SEARCH_VALUE_NULL_MESSAGE);
    }

    Object searchValue = searchValueField.getFormFieldValue().getDefaultValue();
    String type = null;
    Object value = searchValue;

    switch (searchType)
    {
    case "propertyNumber":
      type = "assetNo";
      break;
    case "invoiceNumber":
      type = INVOICE_NO;
      break;
    case "ttdOption":
      type = "tin";
      break;
    case "registerNumber":
      String taxNumberValue = getSelectedFieldOptionIdByFieldID(restFormFields, "taxNumber");
      type = "tin";
      value = taxNumberValue;
      taxNumberValue = URLDecoder.decode(taxNumberValue, StandardCharsets.UTF_8.name());
      toUppercaseRegisterNum(taxNumberValue);

      break;
    default:
      break;
    }

    Map<String, Object> returnMap = new HashMap<>();
    returnMap.put("type", type);
    returnMap.put("value", value);
    return returnMap;
  }

  private String formatDate(String dateString)
  {
    DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(INC_DATE_FORMAT);
    LocalDate date = LocalDate.parse(dateString, inFormatter);

    DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    return outFormatter.format(date);
  }

  private void throwParameterMissingException(String parameterName)
  {
    throw new IllegalArgumentException(parameterName + " is missing!");
  }
}
