/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;
import java.util.Objects;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;

/**
 * @author Tamir
 */
public class GetProcessTypes extends AuthorizedUseCase<Void, Collection<ProcessType>>
{
  private static final Permission permission = new BpmModulePermission("GetProcessTypes");

  private final ProcessTypeRepository processTypeRepository;

  public GetProcessTypes(AuthenticationService authenticationService, AuthorizationService authorizationService, ProcessTypeRepository processTypeRepository)
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
  protected Collection<ProcessType> executeImpl(Void input) throws UseCaseException
  {
    Collection<ProcessType> processTypes = processTypeRepository.findAll();

    if (processTypes.isEmpty())
    {
      String errorCode = "BPMS050";
      throw new UseCaseException(errorCode, "Process type does not exist!");
    }

    return processTypes;
  }
}
