package mn.erin.bpms.loan.consumption.case_listener;

import java.util.List;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.collateral.GetCollateralsByCustNumber;
import mn.erin.domain.bpm.usecase.collateral.GetCollateralsByCustNumberOutput;

import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_STARTED_COLLATERAL_LIST_EXECUTION;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST_FORM_XAC_BANK;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class DownloadCollateralInfoByCifListener implements CaseExecutionListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadCollateralInfoByCifListener.class);

  private final CoreBankingService coreBankingService;
  private final NewCoreBankingService newCoreBankingService;

  public DownloadCollateralInfoByCifListener(CoreBankingService coreBankingService, NewCoreBankingService newCoreBankingService)
  {
    this.coreBankingService = coreBankingService;
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    String caseInstanceId = (String) caseExecution.getVariable(CASE_INSTANCE_ID);
    CaseService caseService = caseExecution.getProcessEngine().getCaseService();

    String requestId = String.valueOf(caseExecution.getVariable(PROCESS_REQUEST_ID));

    if (caseExecution.hasVariable(IS_STARTED_COLLATERAL_LIST_EXECUTION)
        && null != caseExecution.getVariable(IS_STARTED_COLLATERAL_LIST_EXECUTION)
        && caseExecution.getVariable(IS_STARTED_COLLATERAL_LIST_EXECUTION).equals(true))
    {
      LOGGER.info("############ SKIPPED DOWNLOAD COLLATERAL LIST BECAUSE EXECUTION IS ALREADY STARTED, REQUEST ID = [{}]", requestId);
      if (caseExecution.hasVariable(COLLATERAL_LIST_FORM_XAC_BANK) && null != caseExecution.getVariable(COLLATERAL_LIST_FORM_XAC_BANK))
      {
        List<Collateral> collaterals = (List<Collateral>) caseExecution.getVariable(COLLATERAL_LIST_FORM_XAC_BANK);
        caseService.setVariable(caseInstanceId, COLLATERAL_LIST_FORM_XAC_BANK, collaterals);

        for (Collateral collateral : collaterals)
        {
          String collId = collateral.getId().getId();
          caseService.setVariable(caseInstanceId, collId, collateral);
        }
        return;
      }
    }

    try
    {
      String customerCifNumber = (String) caseExecution.getVariable(BpmModuleConstants.CIF_NUMBER);
      LOGGER.info("############ DOWNLOADS COLLATERAL LIST FROM CBS WITH REQUEST ID = [{}]", requestId);

      List<Collateral> downloadedCollaterals = downloadCollateralsByCIF(customerCifNumber);

      LOGGER.info("############ SETS COLLATERAL LIST TO CASE VARIABLE WITH REQUEST ID = [{}], COLL LIST SIZE = [{}]",
          requestId, downloadedCollaterals.size());

      caseService.setVariable(caseInstanceId, COLLATERAL_LIST_FORM_XAC_BANK, downloadedCollaterals);

      for (Collateral downloadedCollateral : downloadedCollaterals)
      {
        String collId = downloadedCollateral.getId().getId();
        caseService.setVariable(caseInstanceId, collId, downloadedCollateral);
      }
      caseService.setVariable(caseInstanceId, IS_STARTED_COLLATERAL_LIST_EXECUTION, true);
    }
    catch (Exception e)
    {
      LOGGER.error("########### ERROR OCCURRED DURING DOWNLOAD COLLATERALS AND SET "
          + "TO VARIABLES WITH REQUEST ID = [{}], REASON = [{}]", requestId, e.getMessage());
    }
  }

  private List<Collateral> downloadCollateralsByCIF(String customerCifNumber) throws UseCaseException
  {
    GetCollateralsByCustNumber getCollateralsByCustNumber = new GetCollateralsByCustNumber(newCoreBankingService);
    GetCollateralsByCustNumberOutput output = getCollateralsByCustNumber.execute(customerCifNumber);
    return output.getCollaterals();
  }
}
