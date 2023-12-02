package mn.erin.bpms.loan.consumption.service_task.bpms;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.codec.binary.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUser;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUserInput;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUserOutput;

import static mn.erin.domain.bpm.BpmMessagesConstants.ACCEPTED_LOAN_AMOUNT_ZERO_ERR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ACCEPTED_LOAN_AMOUNT_ZERO_ERR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_LIST_TASK_NOT_COMPLETED_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_LIST_TASK_NOT_COMPLETED_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.GRANT_LOAN_AMOUNT_ZERO_ERR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.GRANT_LOAN_AMOUNT_ZERO_ERR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.SCORING_PROCESS_TASK_NOT_COMPLETED_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.SCORING_PROCESS_TASK_NOT_COMPLETED_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_SCORE;
import static mn.erin.domain.bpm.BpmModuleConstants.YES_MN_VALUE;
import static mn.erin.domain.bpm.BpmUserRoleConstants.SPECIALIST_ROLES;

/**
 * @author Zorig
 */
public class SetSentUserTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(SetSentUserTask.class);
  private static final String STRING_AS_NULL = "null";
  private static final String ZERO_STRING = "0";

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final ProcessRequestRepository processRequestRepository;
  private final MembershipRepository membershipRepository;
  private final Environment environment;

  public SetSentUserTask(AuthenticationService authenticationService, AuthorizationService authorizationService,
      ProcessRequestRepository processRequestRepository, MembershipRepository membershipRepository, Environment environment)
  {
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.processRequestRepository = processRequestRepository;
    this.membershipRepository = membershipRepository;
    this.environment = environment;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

    if (null != caseInstanceId && !STRING_AS_NULL.equalsIgnoreCase(caseInstanceId))
    {
      Map<String, Object> caseVariables = CaseExecutionUtils.getCaseVariables(caseInstanceId, execution.getProcessEngine());

      if (null == caseVariables.get(SCORING_SCORE))
      {
        throw new ProcessTaskException(SCORING_PROCESS_TASK_NOT_COMPLETED_CODE, SCORING_PROCESS_TASK_NOT_COMPLETED_MESSAGE);
      }

      String colAmountString = String.valueOf(caseVariables.get(COLLATERAL_AMOUNT));

      if (StringUtils.equals(colAmountString, NULL_STRING))
      {
        throw new ProcessTaskException(COLLATERAL_LIST_TASK_NOT_COMPLETED_CODE, COLLATERAL_LIST_TASK_NOT_COMPLETED_MESSAGE);
      }

      final long colAmountLong = Long.parseLong(colAmountString);

      if ((StringUtils.equals((String) caseVariables.get(HAS_COLLATERAL), YES_MN_VALUE) && colAmountLong == 0) || (caseVariables.get(HAS_COLLATERAL) == null
          && colAmountLong == 0))
      {
        throw new ProcessTaskException(COLLATERAL_LIST_TASK_NOT_COMPLETED_CODE, COLLATERAL_LIST_TASK_NOT_COMPLETED_MESSAGE);
      }

      validateLoanAmountFields(caseVariables);
    }

    String regNum = (String) execution.getVariable(REGISTER_NUMBER);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOG.info("############ Set Sent User Task with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId, userId);

    List<String> rolesAllowed = Arrays.asList(Objects.requireNonNull(environment.getProperty(SPECIALIST_ROLES)).split(","));

    if (!checkRole(rolesAllowed))
    {
      String errorCode = "BPMS073";
      throw new ProcessTaskException(errorCode, "This user is not allowed to make loan decisions!");
    }

    String receivers = (String) execution.getVariable("receivers");
    String processRequestId = (String) execution.getVariable(BpmModuleConstants.PROCESS_REQUEST_ID);

    LOG.info("############ SEND FOR LOAN DECISION TASK STARTED.");
    LOG.info("############ CURRENT USER = [{}] sending loan request to DIRECTOR =[{}] with PROCESS REQUEST ID =[{}] ", userId, receivers, processRequestId);

    updateAssignedUser(processRequestId, receivers);

    execution.setVariable("sentUser", userId);
    execution.setVariable("loanDecision", "");
  }

  private void validateLoanAmountFields(Map<String, Object> caseVariables) throws ProcessTaskException
  {
    String processTypeId = String.valueOf(caseVariables.get(BpmModuleConstants.PROCESS_TYPE_ID));

    if (BpmModuleConstants.CONSUMPTION_LOAN.equalsIgnoreCase(processTypeId))
    {
      String grantLoanAmountString = String.valueOf(caseVariables.get(GRANT_LOAN_AMOUNT_STRING));
      Object fixedAcceptedLoanAmount = caseVariables.get(FIXED_ACCEPTED_LOAN_AMOUNT);

      if (ZERO_STRING.equalsIgnoreCase(grantLoanAmountString))
      {
        throw new ProcessTaskException(GRANT_LOAN_AMOUNT_ZERO_ERR_CODE, GRANT_LOAN_AMOUNT_ZERO_ERR_MESSAGE);
      }

      if (isZeroNumber(fixedAcceptedLoanAmount))
      {
        throw new ProcessTaskException(ACCEPTED_LOAN_AMOUNT_ZERO_ERR_CODE, ACCEPTED_LOAN_AMOUNT_ZERO_ERR_MESSAGE);
      }
    }

    if (BpmModuleConstants.MICRO_LOAN.equalsIgnoreCase(processTypeId))
    {
      Object grantLoanAmount = caseVariables.get(GRANT_LOAN_AMOUNT);
      Object fixedAcceptedLoanAmount = caseVariables.get(ACCEPTED_LOAN_AMOUNT);

      if (isZeroNumber(grantLoanAmount))
      {
        throw new ProcessTaskException(GRANT_LOAN_AMOUNT_ZERO_ERR_CODE, GRANT_LOAN_AMOUNT_ZERO_ERR_MESSAGE);
      }

      if (isZeroNumber(fixedAcceptedLoanAmount))
      {
        throw new ProcessTaskException(ACCEPTED_LOAN_AMOUNT_ZERO_ERR_CODE, ACCEPTED_LOAN_AMOUNT_ZERO_ERR_MESSAGE);
      }
    }
  }

  private boolean isZeroNumber(Object number)
  {
    if (number instanceof BigDecimal)
    {
      return BigDecimal.ZERO.equals(number);
    }
    else if (number instanceof BigInteger)
    {
      return BigInteger.ZERO.equals(number);
    }
    else if (number instanceof Double)
    {
      return Double.valueOf(0).equals(number);
    }
    else if (number instanceof Float)
    {
      return Float.valueOf(0).equals(number);
    }
    else if (number instanceof Integer)
    {
      return Integer.valueOf(0).equals(number);
    }
    else if (number instanceof Long)
    {
      return Long.valueOf(0).equals(number);
    }
    return false;
  }

  private boolean updateAssignedUser(String processRequestId, String userToAssign) throws ProcessTaskException
  {
    try
    {
      UpdateAssignedUser updateAssignedUser = new UpdateAssignedUser(authenticationService, authorizationService, processRequestRepository);
      UpdateAssignedUserInput input = new UpdateAssignedUserInput(processRequestId, userToAssign);
      UpdateAssignedUserOutput output = updateAssignedUser.execute(input);
      return output.isUpdated();
    }
    catch (UseCaseException e)
    {
      throw new ProcessTaskException(e.getCode(), e.getMessage());
    }
  }

  private final boolean checkRole(List<String> rolesAllowed) throws ProcessTaskException
  {
    try
    {
      String userId = authenticationService.getCurrentUserId();
      List<Membership> membershipList = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(userId));
      String roleId = membershipList.get(0).getRoleId().getId();

      for (String role : rolesAllowed)
      {
        if (role.equals(roleId))
        {
          return true;
        }
      }
      return false;
    }
    catch (AimRepositoryException e)
    {
      throw new ProcessTaskException(e.getMessage());
    }
  }
}
