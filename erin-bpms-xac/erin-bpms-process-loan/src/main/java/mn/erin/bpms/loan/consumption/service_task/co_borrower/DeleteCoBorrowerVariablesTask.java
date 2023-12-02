package mn.erin.bpms.loan.consumption.service_task.co_borrower;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.process.DeleteVariableInput;
import mn.erin.domain.bpm.usecase.process.DeleteVariables;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class DeleteCoBorrowerVariablesTask implements JavaDelegate
{
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final CaseService caseService;

  private static final String INDEXES = "indexes";
  private static final String CO_BORROWER = "CoBorrower";

  public DeleteCoBorrowerVariablesTask(AuthenticationService authenticationService,
      AuthorizationService authorizationService, CaseService caseService)
  {
    this.authorizationService = authorizationService;
    this.authenticationService = authenticationService;
    this.caseService = caseService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    Map<String, Object> allVariables = execution.getVariables();
    Object indexes = allVariables.get(INDEXES);

    if(null == indexes)
    {
      return;
    }

    List<String> indexList = (List<String>) indexes;
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    removeVariablesFromExecution(execution, allVariables, indexList);
    removeVariablesFromCaseService(caseInstanceId, indexList);
    execution.removeVariable(INDEXES);
  }

  private void removeVariablesFromExecution(DelegateExecution execution, Map<String, Object> allVariables, List<String> indexes)
  {
    if (null != indexes)
    {
      List<String> removeVariableKeys = new ArrayList<>();
      for (String index: indexes)
      {
        for (Map.Entry<String, Object > variable : allVariables.entrySet())
        {
          if (variable.getKey().contains(CO_BORROWER + "-" + index) || variable.getKey().contains("-" + index))
          {
            removeVariableKeys.add(variable.getKey());
          }
        }
      }
      execution.removeVariables(removeVariableKeys);
    }
  }

  private void removeVariablesFromCaseService(String caseInstanceId, List<String> indexes) throws UseCaseException
  {
    DeleteVariableInput deleteVariableInput = new DeleteVariableInput(caseInstanceId, CO_BORROWER, indexes);
    DeleteVariables deleteVariables = new DeleteVariables(authenticationService, authorizationService, this.caseService);
    deleteVariables.execute(deleteVariableInput);
  }
}
