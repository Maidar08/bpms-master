package mn.erin.bpms.loan.consumption.task_listener.mortgage;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_NAME;

/**
 * @author Lkhagvadorj.A
 **/

public class SetProductNameTaskListener implements TaskListener
{
    @Override
    public void notify(DelegateTask delegateTask)
    {
        DelegateExecution execution = delegateTask.getExecution();
        String loanProductDescription = String.valueOf(execution.getVariable(LOAN_PRODUCT_DESCRIPTION));
        String loanProduct = String.valueOf(execution.getVariable(LOAN_PRODUCT));
        if (!StringUtils.isBlank(loanProductDescription) && !StringUtils.isBlank(loanProduct))
        {
            execution.setVariable(LOAN_PRODUCT_NAME, loanProductDescription.substring(loanProduct.length() + 1));
        }
    }
}
