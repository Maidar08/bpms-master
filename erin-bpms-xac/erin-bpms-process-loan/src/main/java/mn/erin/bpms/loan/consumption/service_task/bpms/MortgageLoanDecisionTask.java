package mn.erin.bpms.loan.consumption.service_task.bpms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUser;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUserInput;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUserOutput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestState;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestStateOutput;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_CREATE_LOAN_ACCOUNT;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_CREATE_LOAN_ACCOUNT_WITH_COLLATERAL;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_GENERATE_LOAN_DECISION;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_PROCESS_TASK_COLLATERAL_LIST;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_PROCESS_TASK_CREATE_COLLATERAL;
import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_STAGE_COLLATERAL_LIST;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_COLLATERAL_LIST;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_CREATE_COLLATERAL;
import static mn.erin.bpms.loan.consumption.constant.CamundaTaskDefinitionKeyConstants.TASK_DEF_KEY_CREATE_LOAN_ACCOUNT;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.disableExecutions;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.suspendActiveProcessInstancesByDefKey;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.suspendAllActiveProcesses;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CONFIRMED_USER_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.YES_MN_VALUE;
import static mn.erin.domain.bpm.BpmUserRoleConstants.BRANCH_DIRECTOR;
import static mn.erin.domain.bpm.BpmUserRoleConstants.HUB_DIRECTOR;
import static mn.erin.domain.bpm.BpmUserRoleConstants.RC_SPECIALIST;
import static mn.erin.domain.bpm.BpmUserRoleConstants.R_ANALYST;

public class MortgageLoanDecisionTask implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(LoanDecisionTask.class);

    private static final String CONFIRM = "Батлах";
    private static final String REJECT = "Татгалзах";
    private static final String RETURN = "Буцаах";

    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final ProcessRequestRepository processRequestRepository;
    private final MembershipRepository membershipRepository;

    public MortgageLoanDecisionTask(AuthenticationService authenticationService, AuthorizationService authorizationService,
                                    ProcessRequestRepository processRequestRepository, MembershipRepository membershipRepository) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.processRequestRepository = processRequestRepository;
        this.membershipRepository = membershipRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String regNum = (String) execution.getVariable(REGISTER_NUMBER);
        String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
        String userId = authenticationService.getCurrentUserId();
        LOG.info("######### DECIDE LOAN REQUEST TASK STARTED with REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId, userId);

        List<String> rolesAllowed = new ArrayList<>();
        rolesAllowed.add(BRANCH_DIRECTOR);
        rolesAllowed.add(HUB_DIRECTOR);
        rolesAllowed.add(R_ANALYST);
        rolesAllowed.add(RC_SPECIALIST);

        if (!checkRole(rolesAllowed)) {
            String errorCode = "BPMS073";
            throw new ProcessTaskException(errorCode, "This user is not allowed to make loan decisions!");
        }

        String currentUserId = authenticationService.getCurrentUserId();
        LOG.info("########### USER WHO DECIDE LOAN REQUEST = [{}]", currentUserId);

        String loanDecision = (String) execution.getVariable("loanDecision");
        String receivedUser = (String) execution.getVariable("receivedUser");

        LOG.info("########## LOAN DECISION = [{}] , RECEIVED USER = [{}], REQUEST ID =[{}]", loanDecision, receivedUser, requestId);

        if (loanDecision.equals(CONFIRM)) {
            LOG.info("######### CONFIRMS LOAN REQUEST TO USER = [{}], REQUEST ID = [{}], CONFIRMED USER = [{}]", receivedUser, requestId, currentUserId);

            updateRequestState(requestId, ProcessRequestState.CONFIRMED);
            updateAssignedUser(requestId, receivedUser);

            setConfirmedUserInfo(execution);

            String registerNumber = (String) execution.getVariable(REGISTER_NUMBER);

            LOG.info("############## Confirmed loan request with ID = {}, customer REGISTER_NUMBER = {}", requestId, registerNumber);

            // disables enabled executions
            Object caseInstanceIdObj = execution.getVariable(CASE_INSTANCE_ID);

            if (null != caseInstanceIdObj) {
                String caseInstanceId = (String) caseInstanceIdObj;

                execution.setVariable("confirmedLoanRequest", true);

                String hasCollateralValue = (String) execution.getVariable(HAS_COLLATERAL);

                List<String> nonDisableActivityIds;
                List<String> taskDefKeys;

                if (null != hasCollateralValue && hasCollateralValue.equals(YES_MN_VALUE)) {
                    nonDisableActivityIds = Arrays.asList(ACTIVITY_ID_CREATE_LOAN_ACCOUNT_WITH_COLLATERAL, ACTIVITY_ID_STAGE_COLLATERAL_LIST,
                            ACTIVITY_ID_PROCESS_TASK_COLLATERAL_LIST, ACTIVITY_ID_PROCESS_TASK_CREATE_COLLATERAL,
                            ACTIVITY_ID_CREATE_LOAN_ACCOUNT, ACTIVITY_ID_GENERATE_LOAN_DECISION);

                    taskDefKeys = Arrays.asList(TASK_DEF_COLLATERAL_LIST, TASK_DEF_CREATE_COLLATERAL, TASK_DEF_KEY_CREATE_LOAN_ACCOUNT);
                } else {
                    nonDisableActivityIds = Arrays.asList(ACTIVITY_ID_CREATE_LOAN_ACCOUNT, ACTIVITY_ID_GENERATE_LOAN_DECISION);
                    taskDefKeys = Arrays.asList(TASK_DEF_KEY_CREATE_LOAN_ACCOUNT);
                }

                // disables other executions from create loan account, generate loan decision.
                disableExecutions(caseInstanceId, execution, nonDisableActivityIds);

                // completes executions of all active task.
                suspendActiveProcessInstancesByDefKey(caseInstanceId, execution, taskDefKeys, false);
            }
        } else if (loanDecision.equals(REJECT)) {
            LOG.info("######### REJECTING LOAN REQUEST TO USER = [{}], REQUEST ID = [{}], REJECTED USER = [{}]", receivedUser, requestId, currentUserId);

            String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

            updateRequestState(requestId, ProcessRequestState.REJECTED);
            updateAssignedUser(requestId, receivedUser);
            suspendAllActiveProcesses(caseInstanceId, execution);
        } else if (loanDecision.equals(RETURN)) {
            LOG.info("######### RETURNING LOAN REQUEST TO USER = [{}], REQUEST ID = [{}], RETURNED USER = [{}]", receivedUser, requestId, currentUserId);

            updateRequestState(requestId, ProcessRequestState.RETURNED);
            updateAssignedUser(requestId, receivedUser);
        }
    }

    private void setConfirmedUserInfo(DelegateExecution execution) {
        String currentUserId = authenticationService.getCurrentUserId();
        execution.setVariable(CONFIRMED_USER_ID, currentUserId);
    }

    private boolean updateAssignedUser(String processRequestId, String userToAssign) throws ProcessTaskException {
        try {
            LOG.info("########## Updates assigned user by this value =[{}] with REQUEST ID = [{}]", userToAssign, processRequestId);

            UpdateAssignedUser updateAssignedUser = new UpdateAssignedUser(authenticationService, authorizationService, processRequestRepository);
            UpdateAssignedUserInput input = new UpdateAssignedUserInput(processRequestId, userToAssign);
            UpdateAssignedUserOutput output = updateAssignedUser.execute(input);

            if (output.isUpdated()) {
                LOG.info("########## Successful updated assigned user = [{}] with REQUEST ID = [{}]", userToAssign, processRequestId);
            } else {
                LOG.warn("########## Could not UPDATE assigned user = [{}] with REQUEST ID = [{}]", userToAssign, processRequestId);
            }
            return output.isUpdated();
        } catch (UseCaseException e) {
            LOG.error("############ UPDATE ASSIGN USER ERROR :" + e.getMessage(), e);
            throw new ProcessTaskException(e.getMessage());
        }
    }

    private boolean updateRequestState(String processRequestId, ProcessRequestState processRequestState) throws ProcessTaskException {
        try {
            UpdateRequestState updateRequestState = new UpdateRequestState(processRequestRepository);
            UpdateRequestStateInput updateRequestStateInput = new UpdateRequestStateInput(processRequestId, processRequestState);
            UpdateRequestStateOutput output = updateRequestState.execute(updateRequestStateInput);
            return output.isUpdated();
        } catch (UseCaseException e) {
            throw new ProcessTaskException(e.getCode(), e.getMessage());
        }
    }

    private final boolean checkRole(List<String> rolesAllowed) throws ProcessTaskException {
        try {
            String userId = authenticationService.getCurrentUserId();
            List<Membership> membershipList = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(userId));
            String roleId = membershipList.get(0).getRoleId().getId();

            for (String role : rolesAllowed) {
                if (role.equals(roleId)) {
                    return true;
                }
            }
            return false;
        } catch (AimRepositoryException e) {
            throw new ProcessTaskException(e.getMessage());
        }
    }
}

