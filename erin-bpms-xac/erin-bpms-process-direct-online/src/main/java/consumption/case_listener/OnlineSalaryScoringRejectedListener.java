package consumption.case_listener;

import java.util.Map;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;

import static consumption.util.CamundaUtils.setScoringField;
import static consumption.util.CamundaUtils.toConsumptionLoanProcess;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.SCORING_REJECTED;
import static mn.erin.domain.bpm.util.process.DigitalLoanUtils.updateRequestState;

/**
 * @author Lkhagvadorj.A
 **/

public class OnlineSalaryScoringRejectedListener implements CaseExecutionListener
{
    private final AimServiceRegistry aimServiceRegistry;
    private final BpmsServiceRegistry bpmsServiceRegistry;
    private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
    private final GroupRepository groupRepository;
    private final TenantIdProvider tenantIdProvider;
    private static final Logger LOGGER = LoggerFactory.getLogger(OnlineSalaryScoringRejectedListener.class);

    public OnlineSalaryScoringRejectedListener(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
        BpmsRepositoryRegistry bpmsRepositoryRegistry, GroupRepository groupRepository, TenantIdProvider tenantIdProvider)
    {
        this.aimServiceRegistry = aimServiceRegistry;
        this.bpmsServiceRegistry = bpmsServiceRegistry;
        this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
        this.groupRepository = groupRepository;
        this.tenantIdProvider = tenantIdProvider;
    }

    @Override
    public void notify(DelegateCaseExecution execution) throws Exception
    {
        setScoringField(execution);
        Map<String, String> result = toConsumptionLoanProcess(aimServiceRegistry, tenantIdProvider, bpmsRepositoryRegistry, bpmsServiceRegistry,
            groupRepository, execution);

        String consumptionLoanRequestId = result.get(PROCESS_REQUEST_ID);
        String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
        String instanceId = result.get(CASE_INSTANCE_ID);

        execution.setVariable("consumptionLoanRequestId", consumptionLoanRequestId);
        execution.setVariable("consumptionLoanInstanceId", instanceId);

        updateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository(), consumptionLoanRequestId, SCORING_REJECTED);
        updateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository(), requestId, SCORING_REJECTED);

        Object scoringLevel = execution.getVariable("scoring_level");

        LOGGER.info("#############  SCORING FAILED. Updated process state to {}, with process request id = [{}], SCORING = [{}]",
            ProcessRequestState.fromEnumToString(SCORING_REJECTED), consumptionLoanRequestId, scoringLevel);
    }
}
