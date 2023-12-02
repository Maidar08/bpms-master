package mn.erin.bpms.loan.consumption.service_task.bpms.new_core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.account.UDFieldValue;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByProductCode;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByProductCodeOutput;
import mn.erin.domain.bpm.util.process.BpmUtils;

import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;

public class SetCreatedUserBeforeLoanAccountTask implements JavaDelegate
{
  private final NewCoreBankingService newCoreBankingService;
  private final ProcessRequestRepository processRequestRepository;

  public SetCreatedUserBeforeLoanAccountTask(NewCoreBankingService newCoreBankingService, ProcessRequestRepository processRequestRepository)
  {
    this.newCoreBankingService = newCoreBankingService;
    this.processRequestRepository = processRequestRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String productCode = BpmUtils.getStringValue(execution.getVariable("loanProduct"));
    String instanceId = BpmUtils.getStringValue(execution.getVariable("caseInstanceId"));

    try
    {
      GetUDFieldsByProductCode getUDFieldsByProductCode = new GetUDFieldsByProductCode(newCoreBankingService);
      Map<String, String> input = new HashMap<>();
      input.put(PROCESS_TYPE_ID, String.valueOf(execution.getVariable(PROCESS_TYPE_ID)));
      input.put(PHONE_NUMBER, String.valueOf(execution.getVariable(PHONE_NUMBER)));
      input.put(PRODUCT_CODE, productCode);
      GetUDFieldsByProductCodeOutput output = getUDFieldsByProductCode.execute(input);

      ProcessRequest processRequest = processRequestRepository.getByProcessInstanceId(instanceId);

      Map<String, UDField> udFieldsMap = output.getUdFieldsMap();
      UDField typeOfAdvance = udFieldsMap.get("TypeOfAdvance");

      List<UDFieldValue> values = typeOfAdvance.getValues();
      String requestedUserId = processRequest.getRequestedUserId();

      String setUserValue = null;

      for (UDFieldValue value : values)
      {
        String userName = value.getItemId();

        if (StringUtils.equalsIgnoreCase(userName, requestedUserId))
        {
          setUserValue = value.getItemDescription();
        }
      }

      execution.setVariable("TypeOfAdvance", setUserValue);
    }
    catch (UseCaseException e)
    {
      throw new BpmnError("Failed to set created user field data.");
    }
  }
}
