package service_task.loan_repayment;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmBranchBankingConstants;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACC_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.CASH;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FROM_ACCOUNT_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.PAID_DATE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_DESCRIPTION;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRANSACTION_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_ID;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TRAN_TYPE;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.VALUE_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author bilguunbor
 **/

public class SetPayOffProcTask implements JavaDelegate
{
  private final BranchBankingService branchBankingService;

  public SetPayOffProcTask(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService);
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    CaseService caseService = execution.getProcessEngine().getCaseService();

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String accountNumber = String.valueOf(caseService.getVariable(caseInstanceId, ACCOUNT_NUMBER));

    JSONObject requestParams = new JSONObject();

    requestParams.put(TRAN_TYPE, execution.getVariable(TRANSACTION_TYPE).equals(CASH) ? "C" : "T");
    requestParams.put(FROM_ACCOUNT_ID, execution.getVariable(TRANSACTION_TYPE).equals(CASH) ? EMPTY_VALUE : accountNumber);
    requestParams.put(ACC_ID, getValidString(execution.getVariable(ACCOUNT_ID)));
    requestParams.put(DESCRIPTION, getValidString(execution.getVariable(TRANSACTION_DESCRIPTION)));
    requestParams.put(VALUE_DATE, timestamp.toString());

    try
    {
      Map<String, String> resultMap = branchBankingService.setPayOffProc(requestParams);

      String trnId = resultMap.get(TRAN_ID);
      String trnDt = resultMap.get(BpmBranchBankingConstants.TRAN_DATE);

      execution.setVariable(TRANSACTION_NUMBER, trnId);
      caseService.setVariable(caseInstanceId, TRANSACTION_NUMBER, trnId);
      execution.setVariable(PAID_DATE, trnDt);
      caseService.setVariable(caseInstanceId, PAID_DATE, trnDt);

    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
