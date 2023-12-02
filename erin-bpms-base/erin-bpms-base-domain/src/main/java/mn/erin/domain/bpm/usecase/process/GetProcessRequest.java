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
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

/**
 * @author EBazarragchaa
 */
public class GetProcessRequest extends AuthorizedUseCase<String, ProcessRequest>
{
  private static final Permission permission = new BpmModulePermission("GetProcessRequest");
  private final ProcessRequestRepository processRequestRepository;

  public GetProcessRequest()
  {
    super();
    this.processRequestRepository = null;
  }

  public GetProcessRequest(AuthenticationService authenticationService, AuthorizationService authorizationService,
    ProcessRequestRepository processRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "ProcessRequestRepository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected ProcessRequest executeImpl(String processRequestId) throws UseCaseException
  {
    if (StringUtils.isBlank(processRequestId))
    {
      throw new UseCaseException(BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_CODE, BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_MESSAGE);
    }

    ProcessRequest processRequest = processRequestRepository.findById(ProcessRequestId.valueOf(processRequestId));

    if (null == processRequest)
    {
      throw new UseCaseException(BpmMessagesConstants.PROCESS_REQUEST_NOT_EXISTS_CODE, BpmMessagesConstants.PROCESS_REQUEST_NOT_EXISTS_MESSAGE);
    }

    return processRequest;
  }
}
