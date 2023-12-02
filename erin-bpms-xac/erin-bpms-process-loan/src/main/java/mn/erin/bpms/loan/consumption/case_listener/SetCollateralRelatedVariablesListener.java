package mn.erin.bpms.loan.consumption.case_listener;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;

import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_STARTED_COLLATERAL_LIST_EXECUTION;

/**
 * @author Lkhagvadorj.A
 **/

public class SetCollateralRelatedVariablesListener implements CaseExecutionListener
{
    @Override
    public void notify(DelegateCaseExecution caseExecution) throws Exception
    {
        caseExecution.setVariable(IS_STARTED_COLLATERAL_LIST_EXECUTION, false);
    }
}
