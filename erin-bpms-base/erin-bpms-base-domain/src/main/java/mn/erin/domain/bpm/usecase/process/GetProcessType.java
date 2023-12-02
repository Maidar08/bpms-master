/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;

/**
 * @author Tamir
 */
public class GetProcessType extends AuthorizedUseCase<String, ProcessType>
{
  private static final Permission permission = new BpmModulePermission("GetProcessType");

  private final ProcessTypeRepository processTypeRepository;

  public GetProcessType(AuthenticationService authenticationService, AuthorizationService authorizationService, ProcessTypeRepository processTypeRepository)
  {
    super(authenticationService, authorizationService);
    this.processTypeRepository = Objects.requireNonNull(processTypeRepository, "Process type repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected ProcessType executeImpl(String id) throws UseCaseException
  {
    if (StringUtils.isBlank(id))
    {
      throw new UseCaseException("Process type cannot be blank!");
    }

    ProcessType processType = processTypeRepository.findById(ProcessTypeId.valueOf(id));

    if (null == processType)
    {
      throw new UseCaseException("Process type does not exist with id: " + id);
    }

    return processType;
  }
}
