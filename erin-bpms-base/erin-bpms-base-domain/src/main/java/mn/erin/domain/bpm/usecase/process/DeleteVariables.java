package mn.erin.domain.bpm.usecase.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

public class DeleteVariables extends AuthorizedUseCase<DeleteVariableInput, Void>
{

  private final CaseService caseService;
  private static final Permission permission = new BpmModulePermission("DeleteCoBorrower");

  public DeleteVariables(AuthenticationService authenticationService,
      AuthorizationService authorizationService, CaseService caseService)
  {
    super(authenticationService, authorizationService);
    this.caseService = Objects.requireNonNull(caseService, "CaseService is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Void executeImpl(DeleteVariableInput input) throws UseCaseException
  {
    try
    {
      List<String > variableNames = getVariablesByContext(input.getProcessInstanceId(), input.getContextType(), input.getIds());
      caseService.deleteVariablesByInstanceIdAndVariableName(input.getProcessInstanceId(), variableNames);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }

    return null;
  }

  private List<Variable> getVariables(String instanceId) throws BpmServiceException
  {
    return caseService.getVariables(instanceId);
  }

  private List<String> getVariablesByContext(String instanceId, String contextType, List<String> ids) throws BpmServiceException
  {
    List<Variable> allVariable = getVariables(instanceId);
    List<String > variableNames = new ArrayList<>();

    if (null != ids)
    {
      for (String id: ids)
      {

        List<String> filteredVariables
         = allVariable.stream()
            .filter(variable -> {return variable.getId().getId().contains(contextType + "-" + id) || variable.getId().getId().contains("-" + id);})
            .map(variable -> {return variable.getId().getId();}).collect(Collectors.toList());
        variableNames.addAll(filteredVariables);
      }


    }



    return variableNames;
  }

}
