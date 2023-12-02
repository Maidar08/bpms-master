package mn.erin.bpms.loan.consumption.task_listener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;

public class SaveReasonTaskListener implements TaskListener
{
  private static final String LOAN_DECISION_REASON = "loanDecisionReason";
  public static final String USER_NAME = "username";
  public static final String NOTE = "note";
  public static final String NOTE_DATE = "noteDate";
  public static final String IS_REASON = "isReason";

  private AuthorizationService authorizationService;
  private AuthenticationService authenticationService;
  private UserRepository userRepository;
  private ProcessRepository processRepository;

  public SaveReasonTaskListener(AuthorizationService authorizationService, AuthenticationService authenticationService, UserRepository userRepository,
      ProcessRepository processRepository)
  {
    this.authorizationService = authorizationService;
    this.authenticationService = authenticationService;
    this.userRepository = userRepository;
    this.processRepository = processRepository;
  }

  @Override
  public void notify(DelegateTask delegateTask)
  {
    String taskId = delegateTask.getId();
    String caseInstanceId = delegateTask.getCaseInstanceId();

    DelegateExecution execution = delegateTask.getExecution();
    String decisionReason = (String) execution.getVariable(LOAN_DECISION_REASON);
    String currentUserId = authenticationService.getCurrentUserId();
    if (decisionReason != null && !StringUtils.isEmpty(decisionReason))
    {
      if (currentUserId != null)
      {
        User user = userRepository.findById(UserId.valueOf(currentUserId));
        if (user != null)
        {
          String username = user.getUserInfo().getUserName();
          if (StringUtils.isEmpty(username))
          {
            username = currentUserId;
          }

          Map<ParameterEntityType, Map<String, Serializable>> parameters = new HashMap<>();
          Map<String, Serializable> notesMap = new HashMap<>();

          JSONObject notesJson = new JSONObject();

          notesJson.put(USER_NAME, username);
          notesJson.put(NOTE, decisionReason);
          notesJson.put(NOTE_DATE, LocalDateTime.now());
          notesJson.put(IS_REASON, true);
          notesMap.put(taskId, notesJson.toString());

          parameters.put(ParameterEntityType.NOTE, notesMap);

          UpdateProcessParametersInput input = new UpdateProcessParametersInput(caseInstanceId, parameters);
          UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
          try
          {
            updateProcessParameters.execute(input);
          }
          catch (UseCaseException e)
          {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
