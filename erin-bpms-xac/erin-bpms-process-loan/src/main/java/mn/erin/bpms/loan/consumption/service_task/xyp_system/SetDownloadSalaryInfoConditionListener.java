package mn.erin.bpms.loan.consumption.service_task.xyp_system;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils;

import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_DOWNLOAD_SALARY_INFO_VARIABLE;
import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.INCOME_TYPE_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.INCOME_TYPE_SALARY;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;

/**
 * @author Tamir
 */
public class SetDownloadSalaryInfoConditionListener implements ExecutionListener
{
  private static final String LINE_SEPARATE = "-";

  @Override
  public void notify(DelegateExecution execution) throws Exception
  {
    if (!execution.hasVariable(INDEX_CO_BORROWER))
    {
      return;
    }

    if (null == execution.getVariable(INDEX_CO_BORROWER))
    {
      return;
    }

    boolean isDownloadSalaryInfo = false;

    String currentRegNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, REGISTER_NUMBER_CO_BORROWER);
    Integer indexCoBorrower = (Integer) execution.getVariable(INDEX_CO_BORROWER);

    for (Integer index = indexCoBorrower; index > 0; index--)
    {
      String regNumCoBorrower = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + LINE_SEPARATE + index);

      if (regNumCoBorrower.equalsIgnoreCase(currentRegNum))
      {
        String incomeTypeCoBorrower = String.valueOf(execution.getVariable(INCOME_TYPE_CO_BORROWER + LINE_SEPARATE + index));

        if (incomeTypeCoBorrower.equalsIgnoreCase(INCOME_TYPE_SALARY))
        {
          isDownloadSalaryInfo = true;
        }
      }
    }

    execution.setVariable(IS_DOWNLOAD_SALARY_INFO_VARIABLE, isDownloadSalaryInfo);
  }
}


