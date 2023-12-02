package mn.erin.bpms.loan.consumption.case_listener.micro;

import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;

import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.IS_LOAN_ACCOUNT_CREATE;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.disableExecutionsByActivityId;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVITY_ID_CALCULATE_LOAN_AMOUNT_AFTER_ACCOUNT_CREATION;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVITY_ID_COLLATERAL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTIVITY_ID_CREATE_COLLATERAL;


/**
 * @author Odgavaa
 **/

public class DisableCollateralExecutionsListener implements CaseExecutionListener {

    @Override
    public void notify(DelegateCaseExecution caseExecution) throws Exception
    {
        boolean isCreateLoanAccount = (boolean) caseExecution.getVariable(IS_LOAN_ACCOUNT_CREATE);
        String caseInstanceId = caseExecution.getCaseInstanceId();
        List<String> processTaskDefKeys = Arrays.asList(ACTIVITY_ID_COLLATERAL_LIST, ACTIVITY_ID_CALCULATE_LOAN_AMOUNT_AFTER_ACCOUNT_CREATION, ACTIVITY_ID_CREATE_COLLATERAL);
        if (!isCreateLoanAccount) {
            disableExecutionsByActivityId(caseInstanceId, caseExecution, processTaskDefKeys);
        }
    }
}
