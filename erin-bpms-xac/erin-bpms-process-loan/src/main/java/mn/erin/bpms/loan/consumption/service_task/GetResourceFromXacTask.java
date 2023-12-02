package mn.erin.bpms.loan.consumption.service_task;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.utils.NumberUtils;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerResource;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class GetResourceFromXacTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetResourceFromXacTask.class);
  private static final String RESOURCE = "resource";

  private final AuthenticationService authenticationService;
  private final CoreBankingService coreBankingService;

  public GetResourceFromXacTask(AuthenticationService authenticationService, CoreBankingService coreBankingService)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.coreBankingService = Objects.requireNonNull(coreBankingService, "Core banking service is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Retrieving Customer Resource From Xac Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    Map<String, Object> variables = execution.getVariables();

    //validation make sure it is there/null check

    //Make sure customer cif is existent/null
    String customerCif = (String) variables.get(CIF_NUMBER);

    Double resource = getCustomerResource(customerCif);

    if (variables.containsKey(RESOURCE))
    {
      execution.removeVariable(RESOURCE);
    }

    execution.setVariable(RESOURCE, BigDecimal.valueOf(resource));
    execution.setVariable("resourceString", NumberUtils.doubleToString(resource));

    LOGGER.info("*********** Successfully Retrieved Customer Resource from Xac");

  }

  private Double getCustomerResource(String customerCif) throws UseCaseException
  {
    GetCustomerResource getCustomerResource = new GetCustomerResource(coreBankingService);
    return getCustomerResource.execute(customerCif);
  }
}
