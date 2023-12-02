/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process.manual_activate;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExecutionService;

/**
 * @author Tamir
 */
public class ManualActivate extends AuthorizedUseCase<ManualActivateInput, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ManualActivate.class);
  public static final Permission permission = new BpmModulePermission("ManualActivate");

  private final ExecutionService executionService;

  public ManualActivate(AuthenticationService authenticationService, AuthorizationService authorizationService, ExecutionService executionService)
  {
    super(authenticationService, authorizationService);
    this.executionService = Objects.requireNonNull(executionService, "Execution service is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Boolean executeImpl(ManualActivateInput input) throws UseCaseException
  {
    if (null == input)
    {
      String errorCode = "BPMS022";
      throw new UseCaseException(errorCode, "Manual activate input cannot be null!");
    }

    String executionId = input.getExecutionId();

    if (StringUtils.isBlank(executionId))
    {
      String errorCode = "CamundaCaseService001";
      throw new UseCaseException(errorCode, "Case execution id cannot be blank!");
    }

    List<Variable> variables = input.getVariables();
    List<Variable> deletions = input.getDeletions();

    return manualActivate(executionId, variables, deletions);
  }

  private boolean manualActivate(String executionId, List<Variable> variables, List<Variable> deletions) throws UseCaseException
  {
    if (CollectionUtils.isEmpty(variables) && CollectionUtils.isEmpty(deletions))
    {
      try
      {
        executionService.manualActivate(executionId);
      }
      catch (BpmServiceException e)
      {
        throw new UseCaseException(e.getCode(), e.getMessage());
      }
      return true;
    }

    if (!CollectionUtils.isEmpty(variables) && CollectionUtils.isEmpty(deletions))
    {
      try
      {
        executionService.manualActivate(executionId, variables);
      }
      catch (BpmServiceException e)
      {
        throw new UseCaseException(e.getCode(), e.getMessage());
      }
      return true;
    }

    try
    {
      executionService.manualActivate(executionId, variables, deletions);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return true;
  }
}
