/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpm.rest.model.RestCreatedRequest;
import mn.erin.bpm.rest.model.RestProcessRequest;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequest;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestInput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestOutput;
import mn.erin.domain.bpm.usecase.process.GetAllProcessRequests;
import mn.erin.domain.bpm.usecase.process.GetGroupProcessRequests;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestsByAssignedUserId;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestsByAssignedUserIdInput;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestsByDateInput;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestsOutput;
import mn.erin.domain.bpm.usecase.process.GetProcessType;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersOutput;
import mn.erin.domain.bpm.usecase.process.manual_activate.GetAllProcessRequestsOutput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.util.BpmRestUtils.toRestCreatedRequest;

/**
 * @author EBazarragchaa
 */
@Api("ProcessRequest")
@RestController
@RequestMapping(value = "/bpm/process-requests", name = "Provides BPM process request APIs.")
public class ProcessRequestRestApi extends BaseBpmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRequestRestApi.class);

  private final ProcessRequestRepository processRequestRepository;
  private final ProcessTypeRepository processTypeRepository;

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  private final GroupRepository groupRepository;
  private final TenantIdProvider tenantIdProvider;

  @Inject
  public ProcessRequestRestApi(
      BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      GroupRepository groupRepository,
      TenantIdProvider tenantIdProvider)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);
    this.processRequestRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessRequestRepository(), "Process request repository is required!");
    this.processTypeRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessTypeRepository(), "Process type repository is required!");

    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");

    this.groupRepository = Objects.requireNonNull(groupRepository, "Group repository is required!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "Tenant id provider is required!");
  }

  @ApiOperation("Lists all process requests by group id")
  @GetMapping("/groups/{groupId}")
  public ResponseEntity<RestResult> getByGroupId(@PathVariable String groupId)
  {
    GetGroupProcessRequests getGroupProcessRequests = new GetGroupProcessRequests(authenticationService,
        authorizationService, processRequestRepository, groupRepository);

    try
    {
      GetProcessRequestsByDateInput input = new GetProcessRequestsByDateInput(groupId, null, null);
      Collection<ProcessRequest> processRequests = getGroupProcessRequests.execute(input);
      Collection<RestCreatedRequest> restRequests = new ArrayList<>();

      for (ProcessRequest processRequest : processRequests)
      {
        String processTypeId = processRequest.getProcessTypeId().getId();
        GetProcessType getProcessType = new GetProcessType(authenticationService, authorizationService, processTypeRepository);

        ProcessType processType = getProcessType.execute(processTypeId);
        String name = processType.getName();

        restRequests.add(toRestCreatedRequest(processRequest, name));
      }
      return RestResponse.success(restRequests);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Creates a process request")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestProcessRequest request)
  {
    if (null == request)
    {
      return RestResponse.badRequest("Request body is null");
    }

    try
    {
      CreateProcessRequest createProcessRequest = new CreateProcessRequest(authenticationService, authorizationService, tenantIdProvider,
          processRequestRepository, groupRepository, processTypeRepository);

      CreateProcessRequestInput input = new CreateProcessRequestInput(request.getGroupNumber(), request.getCreatedUserId(), request.getProcessTypeId());
      input.setParameters(request.getParameters());

      CreateProcessRequestOutput output = createProcessRequest.execute(input);

      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Update process request parameters")
  @PatchMapping("/parameters/{processRequestId}")
  public ResponseEntity<RestResult> update(@PathVariable String processRequestId, @RequestBody Map<String, Serializable> parameters) throws UseCaseException
  {
    if (null == parameters)
    {
      return RestResponse.badRequest("Request body is null!");
    }
    if (processRequestId == null || processRequestId.isEmpty())
    {
      return RestResponse.badRequest("Invalid process request id!");
    }

    try
    {
      UpdateRequestParametersInput input = new UpdateRequestParametersInput(processRequestId, parameters);
      UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
      UpdateRequestParametersOutput output = updateRequestParameters.execute(input);

      return RestResponse.success(output);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Lists all process requests by assigned user id")
  @GetMapping("/users/{assignedUserId}")
  public ResponseEntity<RestResult> getByAssignedUser(@PathVariable String assignedUserId)
  {
    GetProcessRequestsByAssignedUserId getProcessRequestsByAssignedUserId = new GetProcessRequestsByAssignedUserId(authenticationService, authorizationService,
        processRequestRepository);

    try
    {
      GetProcessRequestsByAssignedUserIdInput input = new GetProcessRequestsByAssignedUserIdInput(assignedUserId);
      GetProcessRequestsOutput output = getProcessRequestsByAssignedUserId.execute(input);
      Collection<ProcessRequest> processRequests = output.getProcessRequests();
      Collection<RestCreatedRequest> restRequests = new ArrayList<>();

      for (ProcessRequest processRequest : processRequests)
      {
        String processTypeId = processRequest.getProcessTypeId().getId();
        GetProcessType getProcessType = new GetProcessType(authenticationService, authorizationService, processTypeRepository);

        ProcessType processType = getProcessType.execute(processTypeId);
        String name = processType.getName();

        restRequests.add(toRestCreatedRequest(processRequest, name));
      }

      return RestResponse.success(restRequests);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Lists all process requests")
  @GetMapping
  public ResponseEntity<RestResult> get()
  {
    GetAllProcessRequests getAllProcessRequests = new GetAllProcessRequests(authenticationService, authorizationService, processRequestRepository);

    try
    {
      GetAllProcessRequestsOutput output = getAllProcessRequests.execute(null);
      Collection<ProcessRequest> processRequests = output.getAllProcessRequests();
      Collection<RestCreatedRequest> restRequests = new ArrayList<>();

      for (ProcessRequest processRequest : processRequests)
      {
        String processTypeId = processRequest.getProcessTypeId().getId();
        GetProcessType getProcessType = new GetProcessType(authenticationService, authorizationService, processTypeRepository);

        ProcessType processType = getProcessType.execute(processTypeId);
        String name = processType.getName();

        restRequests.add(toRestCreatedRequest(processRequest, name));
      }

      return RestResponse.success(restRequests);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }
}
