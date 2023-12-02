package consumption.case_listener;

import java.util.Map;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;

import static consumption.constant.CamundaMongolBankConstants.LOAN_CLASS_NAME;
import static consumption.util.CamundaUtils.setScoringField;
import static consumption.util.CamundaUtils.toConsumptionLoanProcess;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.ORG_REJECTED;

/**
 * @author Lkhagvadorj.A
 **/

public class OnlineSalaryElementaryRejectedListener implements CaseExecutionListener
{
    private final AimServiceRegistry aimServiceRegistry;
    private final BpmsServiceRegistry bpmsServiceRegistry;
    private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
    private final TenantIdProvider tenantIdProvider;
    private final GroupRepository groupRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OnlineSalaryElementaryRejectedListener.class);

    public OnlineSalaryElementaryRejectedListener(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
        BpmsRepositoryRegistry bpmsRepositoryRegistry, TenantIdProvider tenantIdProvider, GroupRepository groupRepository)
    {
        this.aimServiceRegistry = aimServiceRegistry;

        this.bpmsServiceRegistry = bpmsServiceRegistry;
        this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
        this.tenantIdProvider = tenantIdProvider;
        this.groupRepository = groupRepository;
    }

    @Override
    public void notify(DelegateCaseExecution execution) throws BpmServiceException
    {
        setScoringField(execution);
        try
        {
            Map<String, String> result = toConsumptionLoanProcess(aimServiceRegistry, tenantIdProvider, bpmsRepositoryRegistry, bpmsServiceRegistry, groupRepository, execution);
            String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
            String consumptionLoanRequestId = result.get(PROCESS_REQUEST_ID);
            String instanceId = result.get(CASE_INSTANCE_ID);

            execution.setVariable("consumptionLoanRequestId", consumptionLoanRequestId);
            execution.setVariable("consumptionLoanInstanceId", instanceId);

            UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
            UpdateRequestStateInput input = new UpdateRequestStateInput(consumptionLoanRequestId, ORG_REJECTED);
            updateRequestState.execute(input);

            UpdateRequestStateInput input1 = new UpdateRequestStateInput(requestId, ORG_REJECTED);
            updateRequestState.execute(input1);

            String loanClassification = String.valueOf(execution.getVariable(LOAN_CLASS_NAME));

            LOGGER.info("#############  ELEMENTARY CRITERIA FAILED. Updated process state to {}, with process request id = [{}], Loan Class Name = [{}]",
                ProcessRequestState.fromEnumToString(ORG_REJECTED), requestId, loanClassification);
        }
        catch (BpmRepositoryException | UseCaseException e)
        {
            throw new BpmServiceException(e.getCode(), e.getMessage());
        }
    }
}
