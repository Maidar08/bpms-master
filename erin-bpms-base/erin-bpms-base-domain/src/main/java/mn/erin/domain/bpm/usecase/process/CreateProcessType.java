/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessDefinitionType;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;

/**
 * @author Tamir
 */
public class CreateProcessType extends AuthorizedUseCase<CreateProcessTypeInput, ProcessType>
{
  private static final Permission permission = new BpmModulePermission("CreateProcessType");

  private final ProcessTypeRepository processTypeRepository;

  public CreateProcessType(AuthenticationService authenticationService, AuthorizationService authorizationService, ProcessTypeRepository processTypeRepository)
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
  protected ProcessType executeImpl(CreateProcessTypeInput input) throws UseCaseException
  {
    String processTypeId = input.getProcessTypeId();
    String definitionKey = input.getDefinitionKey();

    String name = input.getName();
    String definitionType = input.getProcessDefinitionType();

    try
    {
      return processTypeRepository.create(processTypeId, definitionKey, name,
          ProcessDefinitionType.fromStringToEnum(definitionType));
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }
}
