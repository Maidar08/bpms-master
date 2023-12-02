package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_INSTANCE_ID_NULL_MESSAGE;

/**
 * @author Zorig
 */
public class GetProcessRequestByProcessInstanceId extends AuthorizedUseCase<String, ProcessRequest>
{
  private static final Logger LOG = LoggerFactory.getLogger(GetProcessRequestByProcessInstanceId.class);

  private static final Permission permission = new BpmModulePermission("GetProcessRequestByProcessInstanceId");
  private final ProcessRequestRepository processRequestRepository;
  private final LoanContractRequestRepository loanContractRequestRepository;

  public GetProcessRequestByProcessInstanceId()
  {
    super();
    this.processRequestRepository = null;
    this.loanContractRequestRepository = null;
  }

  public GetProcessRequestByProcessInstanceId(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository,
      LoanContractRequestRepository loanContractRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "ProcessRequestRepository is required!");
    this.loanContractRequestRepository = Objects.requireNonNull(loanContractRequestRepository, "LoanContractRequestRepository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected ProcessRequest executeImpl(String processInstanceId) throws UseCaseException
  {
    if (StringUtils.isBlank(processInstanceId))
    {
      throw new UseCaseException(PROCESS_INSTANCE_ID_NULL_CODE, PROCESS_INSTANCE_ID_NULL_MESSAGE);
    }

    try
    {
      LOG.info("############# GETS PROCESS REQUEST BY PROCESS INSTANCE ID = [{}]", processInstanceId);
      ProcessRequest processRequest = processRequestRepository.getByProcessInstanceId(processInstanceId);
      if (processRequest != null)
      {
        return processRequest;
      }
      else
      {
        LoanContractRequest loanContractRequest = loanContractRequestRepository.findByInstanceId(processInstanceId);
        if (loanContractRequest != null)
        {
          return mapToProcessRequest(loanContractRequest);
        }
        else
        {
          return new ProcessRequest(null, ProcessTypeId.valueOf("organizationRegistration"), null, null, null);
        }
      }
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private ProcessRequest mapToProcessRequest(LoanContractRequest loanContractRequest)
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put("account", loanContractRequest.getAccount());
    parameters.put("amount", loanContractRequest.getAmount());
    parameters.put("tenantId", loanContractRequest.getTenantId());
    return new ProcessRequest(
        loanContractRequest.getId(),
        ProcessTypeId.valueOf(loanContractRequest.getType()),
        GroupId.valueOf(loanContractRequest.getGroupId()),
        loanContractRequest.getUserId(),
        loanContractRequest.getCreatedDate(),
        ProcessRequestState.valueOf(loanContractRequest.getState()),
        parameters
    );
  }
}
