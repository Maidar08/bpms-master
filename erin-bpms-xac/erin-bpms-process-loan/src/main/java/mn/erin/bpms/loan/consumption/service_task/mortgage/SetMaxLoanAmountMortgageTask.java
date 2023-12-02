package mn.erin.bpms.loan.consumption.service_task.mortgage;

import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_LOAN_PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_LOAN_PRODUCT_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CONDITION_MET;
import static mn.erin.domain.bpm.BpmModuleConstants.MAX_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.MAX_LOAN_AMOUNT_VALIDATION;
import static mn.erin.domain.bpm.BpmModuleConstants.MAX_LOAN_TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.NO_MN_VALUE;

/**
 * @author Lkhagvadorj.A
 **/

public class SetMaxLoanAmountMortgageTask implements JavaDelegate
{
    @Override
    public void execute(DelegateExecution execution) throws Exception
    {
        CaseService caseService = execution.getProcessEngine().getCaseService();
        String instanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
        String conditionMet = String.valueOf(execution.getVariable(CONDITION_MET));
        if (conditionMet.equals(NO_MN_VALUE))
        {
            execution.setVariable(MAX_LOAN_AMOUNT, 0);
            execution.setVariable(MAX_LOAN_TERM, 0);
            caseService.setVariable(instanceId, MAX_LOAN_AMOUNT, 0);
            caseService.setVariable(instanceId, MAX_LOAN_TERM, 0);
        }
        else
        {
            @SuppressWarnings(value = "unchecked")
            Map<String, Integer> maxValidation = (Map<String, Integer>) execution.getVariable(MAX_LOAN_AMOUNT_VALIDATION);
            if (null == maxValidation || maxValidation.isEmpty())
            {
                throw new UseCaseException(INVALID_LOAN_PRODUCT_CODE, INVALID_LOAN_PRODUCT_MESSAGE);
            }

            int maxLoanAmount = maxValidation.get(MAX_LOAN_AMOUNT);
            int maxLoanTerm = maxValidation.get(MAX_LOAN_TERM);

            if (maxLoanAmount <= 0 || maxLoanTerm <= 0)
            {
                throw new UseCaseException(INVALID_LOAN_PRODUCT_CODE, INVALID_LOAN_PRODUCT_MESSAGE);
            }

            // Set max loan amount and max term
            execution.setVariable(MAX_LOAN_AMOUNT, maxLoanAmount);
            execution.setVariable(MAX_LOAN_TERM, maxLoanTerm);
            caseService.setVariable(instanceId, MAX_LOAN_AMOUNT, maxLoanAmount);
            caseService.setVariable(instanceId, MAX_LOAN_TERM, maxLoanTerm);
        }
    }
}
