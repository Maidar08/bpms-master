package mn.erin.bpms.loan.consumption.service_task.risky_customer;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.customer.CheckRiskyCustomer;

import static mn.erin.bpms.loan.consumption.constant.CamundaRiskyCustomerConstants.IS_RISKY_CUSTOMER;
import static mn.erin.bpms.loan.consumption.constant.CamundaRiskyCustomerConstants.RISKY;
import static mn.erin.bpms.loan.consumption.constant.CamundaRiskyCustomerConstants.RISKY_CUSTOMER_VALUE;
import static mn.erin.bpms.loan.consumption.constant.CamundaRiskyCustomerConstants.SAFELY;
import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;

/**
 * @author Tamir
 */
public class CheckRiskyCoBorrowerTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CheckRiskyCoBorrowerTask.class);
  private final NewCoreBankingService coreBankingService;
  private final AuthenticationService authenticationService;

  public CheckRiskyCoBorrowerTask(NewCoreBankingService coreBankingService, AuthenticationService authenticationService)
  {
    this.coreBankingService = coreBankingService;
    this.authenticationService = authenticationService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String regNum = (String) execution.getVariable(REGISTER_NUMBER);
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("*********** Check Risky Co Borrower Task with initial borrower REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId, userId);

    Integer index = (Integer) execution.getVariable(INDEX_CO_BORROWER);
    String regNoCoBorrower = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);

    LOGGER.info("##### Download RISKY CO-BORROWER INFO by REG_NUMBER ={}", regNoCoBorrower);

    boolean isRisky = new CheckRiskyCustomer(coreBankingService).execute(regNoCoBorrower);
    Integer indexCoBorrower = (Integer) execution.getVariable(INDEX_CO_BORROWER);

    execution.setVariable(IS_RISKY_CUSTOMER + "-" + indexCoBorrower, isRisky);

    if (isRisky)
    {
      LOGGER.info("##### CO-BORROWER is risky customer REG_NUMBER ={} ", regNoCoBorrower);
      execution.setVariable(RISKY_CUSTOMER_VALUE + "-" + indexCoBorrower, RISKY);
    }
    else
    {
      LOGGER.info("##### CO-BORROWER is safe customer REG_NUMBER ={} ", regNoCoBorrower);
      execution.setVariable(RISKY_CUSTOMER_VALUE + "-" + indexCoBorrower, SAFELY);
    }

    LOGGER.info("*********** Successful downloaded risky customer info.");
  }
}
