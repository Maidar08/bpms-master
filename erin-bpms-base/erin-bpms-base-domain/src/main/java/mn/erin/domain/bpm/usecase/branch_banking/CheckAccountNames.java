package mn.erin.domain.bpm.usecase.branch_banking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.AccountInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_INFO_LIST_EMPTY_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ACCOUNT_INFO_LIST_EMPTY_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

public class CheckAccountNames extends AbstractUseCase<CheckAccountNamesInput, Map<String, Object>>
{
  public final BranchBankingService branchBankingService;

  public CheckAccountNames(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService);
  }

  @Override
  public Map<String, Object> execute(CheckAccountNamesInput input) throws UseCaseException
  {
    validateInput(input);

    try
    {
      List<AccountInfo> customerInfos = input.getCustomerInfos();
      List<String> accountIds = customerInfos.stream().map(AccountInfo::getAccountId).collect(Collectors.toList());

      Map<String, AccountInfo> accountsFromService = branchBankingService.getAccountNames(accountIds, input.getInstanceId());

      return checkInvalidAcc(accountsFromService, customerInfos);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }

  private static Map<String, Object> checkInvalidAcc(Map<String, AccountInfo> accountsFromService, List<AccountInfo> accountInfos)
  {
    Map<String, Object> responseMap = new HashMap<>();
    List<String> invalidNameAccountIds = new ArrayList<>();

    for (AccountInfo account : accountInfos)
    {
       String accountId = account.getAccountId();
      if (matchAccountInfo(account, accountsFromService))
      {
        account.setChecked(true);
        String accCurrency = accountsFromService.get(account.getAccountId()).getAccountCcy();
        account.setAccountCcy(accCurrency);
      }
      else
      {
        account.setChecked(false);
        invalidNameAccountIds.add(accountId);
      }


    }
    responseMap.put("accountInfo", accountInfos);
    responseMap.put("invalidAccounts", invalidNameAccountIds);
    return responseMap;
  }

  private void validateInput(CheckAccountNamesInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
    if (null == input.getCustomerInfos() || input.getCustomerInfos().isEmpty())
    {
      throw new UseCaseException(BB_ACCOUNT_INFO_LIST_EMPTY_CODE, BB_ACCOUNT_INFO_LIST_EMPTY_MESSAGE);
    }
  }

  private static boolean matchAccountInfo(AccountInfo account, Map<String, AccountInfo> accountsFromService)
  {
    String accountId = account.getAccountId();
    String customerName = account.getCustomerName();
    if(accountsFromService.containsKey(accountId))
    {
      AccountInfo accFromService = accountsFromService.get(accountId);
      return accFromService.getCustomerName().equals(customerName);
    }
    return false;
  }
}
