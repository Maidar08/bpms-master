package mn.erin.domain.bpm.usecase.organization;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.BRANCH_ID_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_CREATE_ORGANIZATION_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ORG_REG_NUM_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ORG_TYPE_NULL_ERROR_CODE;

/**
 * @author Bilguunbor
 */
public class CreateOrganizationLeasing extends AuthorizedUseCase<CreateOrganizationLeasingInput, String>
{
  private static final Permission permission = new BpmModulePermission("CreateOrganizationLeasing");
  private final OrganizationLeasingRepository organizationLeasingRepository;

  public CreateOrganizationLeasing(AuthenticationService authenticationService, AuthorizationService authorizationService,
      OrganizationLeasingRepository organizationLeasingRepository)
  {
    super(authenticationService, authorizationService);
    this.organizationLeasingRepository = organizationLeasingRepository;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected String executeImpl(CreateOrganizationLeasingInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    validateInput(input);

    String organizationName = input.getOrganizationName();
    String regNumber = input.getRegisterNumber();
    String cif = input.getCif();
    LocalDate establishedDate = input.getEstablishedDate();
    String branchNumber = input.getBranchNumber();
    String assignee = input.getAssignee();
    String contractNumber = organizationLeasingRepository.getJdbcLeasingOrgLastContractId();
    String orgRequestId = String.valueOf(getContractId(contractNumber));
    String instanceId = String.valueOf(input.getProcessInstanceId());
    Date createdDate = input.getCreatedDate();

    try
    {
      organizationLeasingRepository
          .create(orgRequestId, organizationName, regNumber, cif, LocalDateTime.now(), establishedDate, "N", branchNumber, assignee, instanceId, createdDate);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(COULD_NOT_CREATE_ORGANIZATION_CODE,
          e.getErrorMessage());
    }

    return orgRequestId;
  }

  private void validateInput(CreateOrganizationLeasingInput input) throws UseCaseException
  {
    //todo: error code refactor
    if (StringUtils.isBlank(input.getOrganizationName()))
    {
      throw new UseCaseException(BRANCH_ID_NULL_ERROR_CODE, "Organization Name is missing in the input!");
    }
    else if (StringUtils.isBlank(input.getOrganizationType()))
    {
      throw new UseCaseException(ORG_TYPE_NULL_ERROR_CODE, "Organization type is missing in the input!");
    }
    else if (StringUtils.isBlank(input.getRegisterNumber()))
    {
      throw new UseCaseException(ORG_REG_NUM_NULL_ERROR_CODE, "Organization register number is missing in the input");
    }
  }

  private int getContractId(String contractNumber)
  {
    int contractId;
    if (contractNumber == null)
    {
      contractId = 1;
      return contractId;
    }
    else
    {
      contractId = Integer.parseInt(contractNumber);
      contractId = contractId + 1;
      return contractId;
    }
  }
}
