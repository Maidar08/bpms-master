package mn.erin.domain.bpm.usecase.loan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Zorig
 */
public class GetAccountsList extends AbstractUseCase<Map<String, String>, GetAccountsListOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetAccountsList.class);
  private static final String CUSTOMER_DEPOSIT_ACCOUNT_TYPE = "CAA";

  private final NewCoreBankingService newCoreBankingService;

  public GetAccountsList(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New core banking service is required!");
  }

  @Override
  public GetAccountsListOutput execute(Map<String, String> input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INVALID_INPUT_CODE, BpmMessagesConstants.INVALID_INPUT_MESSAGE);
    }

    try
    {
      String regNumber = input.get("registerNumber");
      String customerNumber = input.get("cifNumber");

      if (StringUtils.isBlank(regNumber))
      {
        LOGGER.error("######## CUSTOMER REGISTER NUMBER IS BLANK!");
      }

      if (StringUtils.isBlank(customerNumber))
      {
        LOGGER.error("######## CUSTOMER CIF NUMBER IS BLANK!");
      }

      LOGGER.info("######### Gets ACCOUNT LIST FROM CORE by REGISTER NUMBER  = [{}], CIF NUMBER = [{}]", regNumber, customerNumber);
      List<XacAccount> accountsList = newCoreBankingService.getAccountsList(input);

      return new GetAccountsListOutput(filterAccounts(accountsList));
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }

  private List<XacAccount> filterAccounts(List<XacAccount> accountList)
  {
    List<XacAccount> depositAccounts = new ArrayList<>();

    for (XacAccount account : accountList)
    {
      String schemaType = account.getSchemaType();

      if(schemaType.equals(CUSTOMER_DEPOSIT_ACCOUNT_TYPE))
      {
        depositAccounts.add(account);
      }
    }
    return depositAccounts;
  }

}
