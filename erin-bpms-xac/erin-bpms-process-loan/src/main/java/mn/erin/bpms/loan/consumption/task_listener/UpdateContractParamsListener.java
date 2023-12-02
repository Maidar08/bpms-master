package mn.erin.bpms.loan.consumption.task_listener;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskId;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskIdInput;
import mn.erin.domain.bpm.usecase.form.get_form_by_task_id.GetFormByTaskIdOutput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class UpdateContractParamsListener implements TaskListener
{
  public static final String IS_COMPLETED_CONTRACT_PARAMS_UPDATE = "IS_COMPLETED_CONTRACT_PARAMS_UPDATE";
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateContractParamsListener.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  private final ProcessRepository processRepository;
  private final TaskFormService taskFormService;

  public UpdateContractParamsListener(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRepository processRepository, TaskFormService taskFormService)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");

    this.processRepository = Objects.requireNonNull(processRepository, "Process repository is required!");
    this.taskFormService = Objects.requireNonNull(taskFormService, "Task form service is required!");
  }

  @Override
  public void notify(DelegateTask delegateTask)
  {
    String taskId = delegateTask.getId();
    String caseInstanceId = delegateTask.getCaseInstanceId();

    LOGGER.info("######### Sets loan contract preparation TASK ID = [{}] to EXECUTION VARIABLE", taskId);

    DelegateExecution execution = delegateTask.getExecution();
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    try
    {
      updateParametersByFormField(taskId, requestId, caseInstanceId, execution);
    }
    catch (UseCaseException e)
    {
      LOGGER.error("########### Could not update LOAN CONTRACT PARAMETERS with REQUEST ID = [{}]", requestId);
      execution.setVariable(IS_COMPLETED_CONTRACT_PARAMS_UPDATE, false);
    }

    execution.setVariable(IS_COMPLETED_CONTRACT_PARAMS_UPDATE, true);
    LOGGER.info("######### Successfully set loan contract preparation TASK ID = [{}]", taskId);
  }

  private void updateParametersByFormField(String taskId, String requestId, String caseInstanceId, DelegateExecution execution)
      throws UseCaseException
  {
    GetFormByTaskIdInput input = new GetFormByTaskIdInput(caseInstanceId, taskId);
    GetFormByTaskId getFormByTaskId = new GetFormByTaskId(taskFormService);

    Map<ParameterEntityType, Map<String, Serializable>> processParameters = new HashMap<>();
    Map<String, Object> loanContractVariables = new HashMap<>();

    LOGGER.info("####### Updates loan contract parameters with REQUEST_ID = [{}]", requestId);

    GetFormByTaskIdOutput output = getFormByTaskId.execute(input);
    TaskForm taskForm = output.getTaskForm();

    Collection<TaskFormField> taskFormFields = taskForm.getTaskFormFields();

    for (TaskFormField taskFormField : taskFormFields)
    {
      String formFieldId = taskFormField.getId().getId();

      Object value = execution.getVariable(formFieldId);

      if (null != value)
      {
        loanContractVariables.put(formFieldId, value);
      }
    }

    Map<String, Serializable> serializableVariables = objectMapToSerializable(loanContractVariables);
    processParameters.put((ParameterEntityType) getEntityType(execution), serializableVariables);

    updateProcessParams(caseInstanceId, processParameters);
  }

  private Enum getEntityType(DelegateExecution execution)
  {
    switch (execution.getCurrentActivityName())
    {
    case "20. Зээлийн хавсралт гэрээ бэлтгэх":
      return ParameterEntityType.LOAN_ATTACHMENT_CONTRACT;
    case "21. Хамтран өмчлөгчийн мэдээлэл":
      return ParameterEntityType.CO_OWNER_CONTRACT;
    case "22. Барьцаа хөрөнгийн жагсаалт харах":
      return ParameterEntityType.COLLATERAL_REAL_ESTATE_CONTRACT;
    case "Фидуцийн хөрөнгийн мэдээлэл оруулах":
      return ParameterEntityType.FIDUCIARY_CONTRACT;
    default:
      return ParameterEntityType.LOAN_CONTRACT;
    }
  }

  private void updateProcessParams(String caseInstanceId, Map<ParameterEntityType, Map<String, Serializable>> processParameters)
      throws UseCaseException
  {
    UpdateProcessParametersInput updateInput = new UpdateProcessParametersInput(caseInstanceId, processParameters);
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService,
        authorizationService, processRepository);

    updateProcessParameters.execute(updateInput);
  }

  private static Map<String, Serializable> objectMapToSerializable(Map<String, Object> objectMap)
  {
    Map<String, Serializable> serializableMap = new HashMap<>();

    for (Map.Entry<String, Object> parameter : objectMap.entrySet())
    {
      if (null != parameter.getValue())
      {
        serializableMap.put(parameter.getKey(), (Serializable) parameter.getValue());
      }
    }
    return serializableMap;
  }
}
