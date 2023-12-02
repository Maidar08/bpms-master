package mn.erin.bpms.loan.consumption.service_task;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;

import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.WORK_SPAN;

/**
 * @author Zorig
 */
public class SetScoringLevelTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SetScoringLevelTask.class);

  private final AuthenticationService authenticationService;

  public SetScoringLevelTask(AuthenticationService authenticationService)
  {
    this.authenticationService = authenticationService;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
    if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOGGER.info("#########  Setting Scoring Level Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId + ", TrackNumber: " + trackNumber);
    }
    else
    {
      LOGGER.info("#########  Setting Scoring Level Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);
    }

    try
    {
      Map<String, Object> variables = execution.getVariables();

      Map<String, Object> scoringLevelVariables = (Map<String, Object>) variables.get("scoring_level");

      String scoringLevelRisk = (String)scoringLevelVariables.get("risk");
      String decision = (String)scoringLevelVariables.get("pApprove");

      execution.setVariable("scoring_level_risk", scoringLevelRisk);
      execution.setVariable("scoring_level", Integer.valueOf(scoringLevelRisk));
      execution.setVariable("pApprove", decision);

      if (execution.getVariable(WORK_SPAN) instanceof Double)
      {
        final long workspan = ((Double) execution.getVariable(WORK_SPAN)).longValue();
        execution.setVariable(WORK_SPAN, workspan);
      }

      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("#########  Finished Setting Scoring Level... WITH TRACKNUMBER = [{}]", trackNumber);
      }
      else
      {
        LOGGER.info("#########  Finished Setting Scoring Level...");
      }
    }
    catch (NullPointerException e)
    {
      throw new ProcessTaskException(e);
    }
  }
}
