/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;

import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_CATEGORY_BRANCH_BANKING;

/**
 * @author EBazarragchaa
 */
public class CreateProcessRequest extends AuthorizedUseCase<CreateProcessRequestInput, CreateProcessRequestOutput>
{
  private static final Permission permission = new BpmModulePermission("CreateProcessRequest");
  private final ProcessRequestRepository processRequestRepository;
  private final GroupRepository groupRepository;
  private final ProcessTypeRepository processTypeRepository;
  private final TenantIdProvider tenantIdProvider;

  public CreateProcessRequest()
  {
    super();
    this.processRequestRepository = null;
    this.groupRepository = null;
    this.tenantIdProvider = null;
    this.processTypeRepository = null;
  }

  public CreateProcessRequest(AuthenticationService authenticationService, AuthorizationService authorizationService, TenantIdProvider tenantIdProvider,
      ProcessRequestRepository processRequestRepository, GroupRepository groupRepository, ProcessTypeRepository processTypeRepository)
  {
    super(authenticationService, authorizationService);
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "TenantIdProvider is required!");
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "ProcessRequestRepository is required!");
    this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository is required!");
    this.processTypeRepository = Objects.requireNonNull(processTypeRepository, "ProcessTypeRepository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected CreateProcessRequestOutput executeImpl(CreateProcessRequestInput input) throws UseCaseException
  {
    validateInput(input);

    try
    {
      TenantId tenantId = TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId());
      Group group = groupRepository.findByNumberAndTenantId(input.getGroupNumber(), tenantId);

      if (null == group)
      {
        String errorCode = "BPMS044";
        throw new UseCaseException(errorCode, "Group with number [" + input.getGroupNumber() + "] and tenant [" + tenantId.getId() + "] doesn't exist!");
      }

      ProcessType processType = processTypeRepository.findById(ProcessTypeId.valueOf(input.getProcessTypeId()));
      if (null == processType)
      {
        String errorCode = "BPMS045";
        throw new UseCaseException(errorCode, "Process type with id [" + input.getProcessTypeId() + "] and tenant [" + tenantId.getId() + "] doesn't exist!");
      }

      validateParameters(input.getParameters());

      ProcessRequest request = createProcessByType(processType, group.getId(), input.getRequestedUserId(), input.getParameters());
      if (null == request)
      {
        String errorCode = "BPMS046";
        throw new UseCaseException(errorCode,
            "ProcessRequest with type [" + input.getProcessTypeId() + "], number [" + input.getGroupNumber() + "] and tenant [" + tenantId.getId()
                + "] could not created!");
      }

      return new CreateProcessRequestOutput(request.getId().getId());
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void validateInput(CreateProcessRequestInput input) throws UseCaseException
  {
    if (StringUtils.isBlank(input.getProcessTypeId()))
    {
      throw new UseCaseException("Process type is missing in input!");
    }

    if (StringUtils.isBlank(input.getGroupNumber()))
    {
      throw new UseCaseException("Group number is missing in input!");
    }

    if (StringUtils.isBlank(input.getRequestedUserId()))
    {
      throw new UseCaseException("Requested user id is missing in input!");
    }
  }

  private void validateParameters(Map<String, Serializable> parameters) throws UseCaseException
  {
    if (parameters == null)
    {
      throw new UseCaseException("Parameters must not be null!");
    }

    for (Map.Entry<String, Serializable> parameter : parameters.entrySet())
    {
      if (parameter.getKey() == null || StringUtils.isBlank(parameter.getKey()))
      {
        throw new UseCaseException("Name for parameter must not be null or blank!");
      }
      if (parameter.getValue() instanceof String)
      {
        String parameterValueString = parameter.getValue().toString();
        if (StringUtils.isBlank(parameterValueString))
        {
          throw new UseCaseException("Value for parameter must not be blank");
        }
      }
    }
  }

  private ProcessRequest createProcessByType(ProcessType processType, GroupId groupId, String requestedUserId, Map<String, Serializable> parameters)
      throws BpmRepositoryException
  {
    String processTypeCategory = processType.getProcessTypeCategory();
    if (StringUtils.isBlank(processTypeCategory))
    {
      return null;
    }

    if (processTypeCategory.equals(PROCESS_CATEGORY_BRANCH_BANKING))
    {
      return createBranchBankingProcessRequest(processType, groupId, requestedUserId, parameters);
    }

    return processRequestRepository.createProcessRequest(processType.getId(), groupId, requestedUserId, parameters);
  }

  private ProcessRequest createBranchBankingProcessRequest(ProcessType processType, GroupId groupId, String requestedUserId, Map<String, Serializable> parameters)
  {
    LocalDateTime createdTime = LocalDateTime.now(ZoneId.of("UTC+8"));
    return new ProcessRequest(new ProcessRequestId("branch-banking-request"), processType.getId(), groupId, requestedUserId, createdTime, ProcessRequestState.NEW, parameters);
  }
}
