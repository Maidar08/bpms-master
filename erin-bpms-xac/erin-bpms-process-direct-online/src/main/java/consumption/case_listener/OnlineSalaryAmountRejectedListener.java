package consumption.case_listener;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;

import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;

import static consumption.constant.CamundaVariableConstants.GENDER;
import static consumption.constant.CamundaVariableConstants.GENDER_INPUT;
import static consumption.util.CamundaUtils.toConsumptionLoanProcess;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.FAMILY_INCOME;
import static mn.erin.domain.bpm.BpmModuleConstants.FAMILY_INCOME_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FEMALE_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.MALE_MN_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.WORK_SPAN;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.REJECTED;

/**
 * @author Lkhagvadorj.A
 **/

public class OnlineSalaryAmountRejectedListener implements CaseExecutionListener
{
  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final GroupRepository groupRepository;
  private final TenantIdProvider tenantIdProvider;
  private static Map<Integer, String> FAMILY_INCOME_MAP = new HashMap<>();

  public OnlineSalaryAmountRejectedListener(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry, GroupRepository groupRepository, TenantIdProvider tenantIdProvider)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.groupRepository = groupRepository;
    this.tenantIdProvider = tenantIdProvider;
    FAMILY_INCOME_MAP.put(0, "0 - 488,940");
    FAMILY_INCOME_MAP.put(1, "488,941 - 627,812");
    FAMILY_INCOME_MAP.put(2, "627,813 - 948,608");
    FAMILY_INCOME_MAP.put(3, "948,608+");
  }

  @Override
  public void notify(DelegateCaseExecution execution) throws BpmServiceException
  {
    setScoringField(execution);
    Map<String, String> result = null;
    try
    {
      result = toConsumptionLoanProcess(aimServiceRegistry, tenantIdProvider, bpmsRepositoryRegistry, bpmsServiceRegistry,
          groupRepository, execution);
      String requestId = result.get(PROCESS_REQUEST_ID);
      String instanceId = result.get(CASE_INSTANCE_ID);

      execution.setVariable("consumptionLoanRequestId", requestId);
      execution.setVariable("consumptionLoanInstanceId", instanceId);

      UpdateRequestState updateRequestState = new UpdateRequestState(bpmsRepositoryRegistry.getProcessRequestRepository());
      UpdateRequestStateInput input = new UpdateRequestStateInput(requestId, REJECTED);
      UpdateRequestStateInput input2 = new UpdateRequestStateInput(String.valueOf(execution.getVariable(PROCESS_REQUEST_ID)), REJECTED);
      updateRequestState.execute(input);
      updateRequestState.execute(input2);
    }
    catch (BpmRepositoryException | UseCaseException e)
    {
      throw new BpmServiceException(e.getCode(), e.getMessage());
    }


  }

  private void setScoringField(DelegateCaseExecution execution)
  {
    if (execution.getVariable(WORK_SPAN) instanceof Double)
    {
      final long workspan = ((Double) execution.getVariable(WORK_SPAN)).longValue();
      execution.setVariable(WORK_SPAN, workspan);
    }
    String familyIncomeStringValue = String.valueOf(execution.getVariable(FAMILY_INCOME));
    long familyIncome = Long.parseLong(familyIncomeStringValue);

    String familyIncomeString;
    if (familyIncome <= 488940)
    {
      familyIncomeString = FAMILY_INCOME_MAP.get(0);
    }
    else if (familyIncome <= 627812)
    {
      familyIncomeString = FAMILY_INCOME_MAP.get(1);
    }
    else if (familyIncome <= 948608)
    {
      familyIncomeString = FAMILY_INCOME_MAP.get(2);
    }
    else
    {
      familyIncomeString = FAMILY_INCOME_MAP.get(3);
    }

    execution.setVariable(FAMILY_INCOME_STRING, familyIncomeString);

    String genderInput = String.valueOf(execution.getVariable(GENDER)).equals("M") ? MALE_MN_VALUE : FEMALE_MN_VALUE;
    execution.setVariable(GENDER_INPUT, genderInput);
  }


}
