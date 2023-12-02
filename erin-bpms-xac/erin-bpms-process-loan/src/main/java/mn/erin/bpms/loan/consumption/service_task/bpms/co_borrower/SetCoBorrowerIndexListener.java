package mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.IS_LOAN_ACCOUNT_CREATE;
import static mn.erin.domain.bpm.BpmModuleConstants.CO_BORROWER_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;

/**
 * @author Tamir
 */
public class SetCoBorrowerIndexListener implements TaskListener
{
  private static final String DISABLE_DOWNLOAD_ENQUIRE_PROCESS = "disableDownloadEnquireProcess";
  private static final String FIRST_INDEX = "-1";
  private static final String EMPTY = "";

  @Override
  public void notify(DelegateTask delegateTask)
  {
    DelegateExecution execution = delegateTask.getExecution();

    String regNumberCoBorrower = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER);
    String coBorrowerType = execution.hasVariable(CO_BORROWER_TYPE)? (String) execution.getVariable(CO_BORROWER_TYPE) : null;

    if (null != regNumberCoBorrower)
    {
      String letterOfRegNum = regNumberCoBorrower.substring(0, 2);
      String numberOfRegNum = regNumberCoBorrower.substring(2);

      regNumberCoBorrower = letterOfRegNum.toUpperCase() + numberOfRegNum;
    }

    if (!execution.hasVariable(INDEX_CO_BORROWER))
    {
      execution.setVariable(IS_LOAN_ACCOUNT_CREATE, false);
      execution.setVariable(INDEX_CO_BORROWER, 1);

      execution.setVariable(REGISTER_NUMBER_CO_BORROWER + FIRST_INDEX, regNumberCoBorrower);
      execution.setVariable(CO_BORROWER_TYPE + FIRST_INDEX, coBorrowerType);
      execution.setVariable(LOAN_CLASS_NAME + FIRST_INDEX, EMPTY);
    }

    else
    {
      Integer indexCoBorrower = (Integer) execution.getVariable(INDEX_CO_BORROWER);
      Integer increasedIndex = indexCoBorrower + 1;

      execution.setVariable(INDEX_CO_BORROWER, increasedIndex);
      execution.setVariable(REGISTER_NUMBER_CO_BORROWER + "-" + increasedIndex, regNumberCoBorrower);
      execution.setVariable(CO_BORROWER_TYPE + "-" + increasedIndex, coBorrowerType);
      execution.setVariable(LOAN_CLASS_NAME + "-" + increasedIndex, EMPTY);
    }

    execution.setVariable(DISABLE_DOWNLOAD_ENQUIRE_PROCESS, false);
    execution.setVariable("repeatElementaryCriteria", true);
  }
}
