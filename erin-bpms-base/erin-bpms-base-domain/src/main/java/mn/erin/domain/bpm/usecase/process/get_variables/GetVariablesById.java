/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process.get_variables;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;

/**
 * @author Tamir
 */
public class GetVariablesById extends AuthorizedUseCase<String, GetVariablesByIdOutput>
{
  public static final Permission permission = new BpmModulePermission("GetVariablesById");

  private final CaseService caseService;

  public GetVariablesById(AuthenticationService authenticationService, AuthorizationService authorizationService, CaseService caseService)
  {
    super(authenticationService, authorizationService);
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected GetVariablesByIdOutput executeImpl(String instanceId) throws UseCaseException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new UseCaseException(BpmMessagesConstants.EXECUTION_ID_NULL_CODE, BpmMessagesConstants.EXECUTION_ID_NULL_MESSAGE);
    }

    try
    {
      List<Variable> variables = caseService.getVariables(instanceId);
      return new GetVariablesByIdOutput(variables);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }
}
