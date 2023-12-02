package mn.erin.domain.bpm.usecase.direct_online;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_BALANCE;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_BRANCH_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_CLOSING_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.XAC_CURRENCY;

/**
 * @author Lkhagvadorj.A
 **/

public class GetLoanInfo extends AbstractUseCase<Map<String, String>, GetLoanInfoOutput>
{
  private final NewCoreBankingService newCoreBankingService;
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final ProductRepository productRepository;
  private String finalProcessType;

  public GetLoanInfo(NewCoreBankingService newCoreBankingService, DirectOnlineCoreBankingService directOnlineCoreBankingService, ProductRepository productRepository)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New Core Banking Service is required!");
    this.directOnlineCoreBankingService = Objects.requireNonNull(directOnlineCoreBankingService, " Direct Online Core Banking Service is required!");
    this.productRepository = Objects.requireNonNull(productRepository, "Product Repository is required!");
  }

  @Override
  public GetLoanInfoOutput execute(Map<String, String> input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    Map<String, Map<String, Object>> mappedAccount = new HashMap<>();

    BigDecimal totalClosingAmount = new BigDecimal(0);
    BigDecimal totalBalance = new BigDecimal(0);
    boolean hasActiveLoanAccount;
    try
    {
      checkProcessType(input.get(PROCESS_TYPE_ID));

      List<Product> products;
      List<XacAccount> xacAccountsList = newCoreBankingService.getAccountsList(input);
      if (finalProcessType.equals(ONLINE_LEASING_PROCESS_TYPE_ID)){
        products = productRepository.findByAppCategory(input.get(PRODUCT_CATEGORY));
      } else {
        products = productRepository.findByAppCategory(finalProcessType);
      }
      List<String> productNames = products.stream().map(product -> product.getId().getId()).collect(Collectors.toList());

      for (XacAccount xacAccount : xacAccountsList)
      {
        if (isCorrectProduct(xacAccount, productNames))
        {
          String accountId = xacAccount.getId().getId();
          input.put(BRANCH_NUMBER, xacAccount.getBranchId());
          input.put(ACCOUNT_ID, accountId);
          Map<String, Object> accountLoanInfo = directOnlineCoreBankingService.getAccountLoanInfo(input);
          hasActiveLoanAccount = hasActiveLoanAccount(accountLoanInfo);

          if (hasActiveLoanAccount)
          {
            mappedAccount.put(accountId, mapAccount(xacAccount, accountLoanInfo));
            totalClosingAmount = totalClosingAmount.add((BigDecimal) accountLoanInfo.get("ClosingLoanAmount"));
            double balance = Double.parseDouble(String.valueOf(accountLoanInfo.get("ClearBalance")));
            totalBalance = totalBalance.add(BigDecimal.valueOf(balance));
          }
        }
      }
    }
    catch (BpmServiceException | BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }

    /* Will return true if there is an active loan account. */
    return new GetLoanInfoOutput(mappedAccount.size() > 0, mappedAccount, totalClosingAmount, totalBalance);
  }

  private void checkProcessType(String processType)
  {
    switch (processType) {
    case INSTANT_LOAN_PROCESS_TYPE_ID:
      finalProcessType = INSTANT_LOAN_PROCESS_TYPE_CATEGORY;
      break;
    case ONLINE_SALARY_PROCESS_TYPE:
      finalProcessType = PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY;
      break;
    default:
      finalProcessType = processType;
    }
  }

  private boolean isCorrectProduct(XacAccount xacAccount, List<String> productNames)
  {
    return productNames.contains(xacAccount.getType().substring(0, 4));
  }

  private boolean hasActiveLoanAccount(Map<String, Object> accountLoanInfo)
  {
    String closingLoanAmountString = String.valueOf(accountLoanInfo.get(XAC_CLOSING_LOAN_AMOUNT));
    if (StringUtils.isBlank(closingLoanAmountString) || closingLoanAmountString.equals(NULL_STRING))
    {
      return false;
    }
    double closingLoanAmount = Double.parseDouble(closingLoanAmountString);
    accountLoanInfo.put(XAC_CLOSING_LOAN_AMOUNT, BigDecimal.valueOf(closingLoanAmount));
    return closingLoanAmount > 0;
  }

  private Map<String, Object> mapAccount(XacAccount account, Map<String, Object> accountLoanInfo)
  {
    String branchId = account.getBranchId();
    String accountId = account.getId().getId();
    String currency = account.getCurrencyId();
    String balance = account.getBalance();
    String productCode = account.getType();
    accountLoanInfo.put(XAC_BRANCH_ID, branchId);
    accountLoanInfo.put(XAC_ACCOUNT_ID, accountId);
    accountLoanInfo.put(XAC_CURRENCY, currency);
    accountLoanInfo.put(XAC_BALANCE, balance);
    accountLoanInfo.put(PRODUCT_CODE, productCode);

    return accountLoanInfo;
  }
}
