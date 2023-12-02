package mn.erin.bpms.loan.consumption.task_listener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;

import static mn.erin.domain.bpm.BpmModuleConstants.ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_APPROVED;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.IS_CUSTOMER_CONFIRMED;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TASK_DEF_MICRO_LOAN_AMOUNT;

/**
 * @author Odgavaa
 **/

public class UpdateLoanAmountTaskListener implements TaskListener {
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final ProcessRequestRepository processRequestRepository;

    public UpdateLoanAmountTaskListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
                                        ProcessRequestRepository processRequestRepository) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.processRequestRepository = processRequestRepository;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        Map<String, Serializable> processRequestParameters = new HashMap<>();
        setLoanProductParameter(execution, processRequestParameters);
        setLoanAmountParameter(execution, processRequestParameters);

        if (!processRequestParameters.isEmpty()) {
            updateParameters(execution, processRequestParameters);
        }
    }

    private void setLoanAmountParameter(DelegateExecution execution, Map<String, Serializable> processRequestParameters) {

        String isCustomerConfirmed = (String) execution.getVariable(IS_CUSTOMER_CONFIRMED);
        String taskDefinitionId =  execution.getCurrentActivityId();
        Long approvedAmount;
        if (taskDefinitionId.equals(TASK_DEF_MICRO_LOAN_AMOUNT)) {
            approvedAmount = (Long) execution.getVariable(ACCEPTED_LOAN_AMOUNT);
            processRequestParameters.put(LOAN_AMOUNT, approvedAmount);
        } else {
            if (!StringUtils.isBlank(isCustomerConfirmed) && isCustomerConfirmed.equals(CUSTOMER_APPROVED)) {
                approvedAmount = (Long) execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT);
                processRequestParameters.put(LOAN_AMOUNT, approvedAmount);
            }
        }
    }

    private void setLoanProductParameter(DelegateExecution execution, Map<String, Serializable> processRequestParameters) {
        String loanProduct = (String) execution.getVariable(LOAN_PRODUCT);
        String loanProductDescription = (String) execution.getVariable(LOAN_PRODUCT_DESCRIPTION);
        if (!StringUtils.isBlank(loanProduct) && !StringUtils.isBlank(loanProductDescription)) {
            processRequestParameters.put(LOAN_PRODUCT, loanProduct);
            processRequestParameters.put(LOAN_PRODUCT_DESCRIPTION, loanProductDescription);
        }
    }

    private void updateParameters(DelegateExecution execution, Map<String, Serializable> processRequestParameters) {
        String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
        UpdateRequestParametersInput input = new UpdateRequestParametersInput(processRequestId, processRequestParameters);
        UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
        try {
            updateRequestParameters.execute(input);
        } catch (UseCaseException e) {
            e.printStackTrace();
        }
    }
}
