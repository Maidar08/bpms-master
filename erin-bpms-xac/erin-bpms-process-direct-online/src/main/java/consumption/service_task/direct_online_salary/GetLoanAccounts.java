package consumption.service_task.direct_online_salary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

public class GetLoanAccounts implements UseCase<Map<String, String>, Map<String, String>>
{
  private final NewCoreBankingService newCoreBankingService;
  private final Environment environment;

  public GetLoanAccounts(NewCoreBankingService newCoreBankingService, Environment environment)
  {
    this.newCoreBankingService = newCoreBankingService;
    this.environment = environment;
  }

  @Override
  public Map<String, String> execute(Map<String, String> input) throws UseCaseException
  {
    try
    {
      List<XacAccount> xacAccountsList = newCoreBankingService.getAccountsList(input);
      xacAccountsList = xacAccountsList.stream().filter(a -> a.getSchemaType().equals(environment.getProperty("laa.type"))).collect(Collectors.toList());
      Map<String, String> mappedAccountList = new HashMap<>();
      for (XacAccount xacAccount : xacAccountsList)
      {
        mappedAccountList.put(xacAccount.getId().getId(), xacAccount.getType());
      }
      return mappedAccountList;
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
