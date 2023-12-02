package mn.erin.domain.bpm.usecase.organization;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;

import static mn.erin.domain.bpm.BpmLoanContractConstants.CONTRACT_Id;
import static mn.erin.domain.bpm.BpmMessagesConstants.CONTRACT_NUMBER_BLANK_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CONTRACT_NUMBER_BLANK_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CANCEL_ORGANIZATION_STATE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPORARY_STATE;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class UpdateStateOrgSalary extends AuthorizedUseCase<Map<String, Object>, Boolean>
{
  private static final Permission permission = new BpmModulePermission("UpdateStateOrganizationSalary");
  private final OrganizationSalaryRepository organizationSalaryRepository;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final Environment environment;

  public UpdateStateOrgSalary(AuthenticationService authenticationService, AuthorizationService authorizationService,
      OrganizationSalaryRepository organizationSalaryRepository, BpmsServiceRegistry bpmsServiceRegistry, Environment environment)
  {
    super(authenticationService, authorizationService);
    this.organizationSalaryRepository = Validate.notNull(organizationSalaryRepository);
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.environment = environment;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Boolean executeImpl(Map<String, Object> input) throws UseCaseException
  {
    String contractId = getValidString(input.get(CONTRACT_Id));
    String instanceId = getValidString(input.get(CASE_INSTANCE_ID));
    String actionType = getValidString(input.get("actionType"));
    String makerId = getValidString(input.get("makerId"));
    if (StringUtils.isBlank(contractId))
    {
      throw new UseCaseException(CONTRACT_NUMBER_BLANK_CODE, CONTRACT_NUMBER_BLANK_MESSAGE);
    }

    try
    {
      String updateState;
      switch (actionType)
      {
      case "cancel":
        updateState = Objects.requireNonNull(environment.getProperty("confirmByDirector.organization.state"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(instanceId, TEMPORARY_STATE, CANCEL_ORGANIZATION_STATE);
        return organizationSalaryRepository.update(contractId, updateState);
      case "confirmByDirector":
        Date makerDate = new Date();
        updateState = Objects.requireNonNull(environment.getProperty("confirmByDirector.organization.state"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(instanceId, "temporaryState", "Батлагдсан");
        return organizationSalaryRepository.updateToConfirmByDirector(contractId, makerId, makerDate, updateState);
      case "confirm":
        String confirmedUser = authenticationService.getCurrentUserId();
        Date confirmedDate = new Date();
        if (getValidString(bpmsServiceRegistry.getCaseService().getVariableById(instanceId, TEMPORARY_STATE)).equalsIgnoreCase(CANCEL_ORGANIZATION_STATE))
        {
          updateState = Objects.requireNonNull(environment.getProperty("cancel.organization.state"));
        }
        else
        {
          updateState = Objects.requireNonNull(environment.getProperty("confirm.organization.state"));
        }
        return organizationSalaryRepository.updateToConfirmSalary(contractId, updateState, confirmedUser, confirmedDate);
      case "reject":
        updateState = Objects.requireNonNull(environment.getProperty("reject.organization.state"));
        return organizationSalaryRepository.update(contractId, updateState);
      default:
        return false;
      }
    }
    catch (BpmRepositoryException | BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getErrorMessage());
    }
  }
}
