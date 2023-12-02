package mn.erin.bpms.loan.consumption.service_task.co_borrower;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;

/**
 * @author Tamir
 */
public class CoBorrowerUtils
{
  private CoBorrowerUtils()
  {

  }

  public static Integer getCurrentCoBorrowerIndex(DelegateExecution execution, String currentRegNumber)
  {
    Integer indexCoBorrower = (Integer) execution.getVariable(INDEX_CO_BORROWER);

    if (null != indexCoBorrower)
    {
      for (Integer index = indexCoBorrower; index > 0; index--)
      {
        String regNumCoBorrower = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);

        if (regNumCoBorrower.equalsIgnoreCase(currentRegNumber))
        {
          return index;
        }
      }
    }

    return 1;
  }
}
