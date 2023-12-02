/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.ExecutionService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskService;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByInstanceId;
import mn.erin.domain.bpm.usecase.task.GetActiveTaskByInstanceIdOutput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.util.BpmRestUtils.toRestExecutions;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestTasks;
import static mn.erin.domain.bpm.BpmModuleConstants.ADMIN_1;
import static mn.erin.domain.bpm.BpmModuleConstants.ALL_LOAN_REQUEST;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_DECISION_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MICRO_LOAN_DECISION_ACTIVITY_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.ROLE_ADMIN_1;
import static mn.erin.domain.bpm.BpmModuleConstants.ROLE_BRANCH_DIRECTOR;
import static mn.erin.domain.bpm.BpmModuleConstants.ROLE_HUB_DIRECTOR;
import static mn.erin.domain.bpm.BpmModuleConstants.ROLE_RANALYST;
import static mn.erin.domain.bpm.BpmModuleConstants.ROLE_RC_SPECIALIST;
import static mn.erin.domain.bpm.BpmUserRoleConstants.HR_SPECIALIST;

/**
 * Represents a task REST API.
 *
 * @author Tamir
 */
@Api
@RestController
@RequestMapping(value = "bpm/tasks", name = "Provides BPM case task APIs.")
public class TaskRestApi extends BaseBpmsRestApi
{
  private final TaskService taskService;
  private final RuntimeService runtimeService;
  private final AuthenticationService authenticationService;
  private final ExecutionService executionService;

  private final MembershipRepository membershipRepository;
  private final TenantIdProvider tenantIdProvider;

  public TaskRestApi(
      BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry,
      RuntimeService runtimeService, AuthenticationService authenticationService,
      MembershipRepository membershipRepository,
      TenantIdProvider tenantIdProvider)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);
    this.runtimeService = runtimeService;
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.taskService = Objects.requireNonNull(bpmsServiceRegistry.getTaskService(), "Task service is required!");
    this.executionService = Objects.requireNonNull(bpmsServiceRegistry.getExecutionService(), "Execution service is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");
    this.tenantIdProvider = Objects.requireNonNull(tenantIdProvider, "Tenant id provider is required!");
  }

  @ApiOperation("Gets active tasks by case instance id.")
  @GetMapping("/active/{caseInstanceId}/{requestType}")
  public ResponseEntity<RestResult> getVariables(@PathVariable String caseInstanceId, @PathVariable String requestType) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isEmpty(caseInstanceId))
    {
      String errorCode = "BPMS015";
      throw new BpmInvalidArgumentException(errorCode, "Case instance id is required!");
    }

    GetActiveTaskByInstanceId getActiveTaskByInstanceId = new GetActiveTaskByInstanceId(taskService, runtimeService, executionService);
    GetActiveTaskByInstanceIdOutput output = getActiveTaskByInstanceId.execute(caseInstanceId);

    List<Task> activeTasks = output.getActiveTasks();

    String currentUserRole = getCurrentUserRole();

    if ((currentUserRole.equals(HR_SPECIALIST) && requestType.equalsIgnoreCase(ALL_LOAN_REQUEST)) || (currentUserRole.equals(ADMIN_1)) ){
      return RestResponse.success(toRestExecutions(Collections.emptyList()));
    }

    else if (directorRole(currentUserRole) && !requestType.equalsIgnoreCase("ORGANIZATION"))
    {
      for (Task activeTask : activeTasks)
      {
        String activeTaskName = activeTask.getName();
        if (activeTaskName.equalsIgnoreCase(LOAN_DECISION_ACTIVITY_NAME) || activeTaskName.equalsIgnoreCase(MICRO_LOAN_DECISION_ACTIVITY_NAME))
        {
          return RestResponse.success(toRestTasks(Collections.singletonList(activeTask)));
        }
      }
      return RestResponse.success(Collections.emptyList());
    }

    if (headOfficeRole(currentUserRole))
    {
      return RestResponse.success(Collections.emptyList());
    }

    List<Task> filteredActiveTasks = activeTasks.stream()
        .filter(task -> !task.getName().equalsIgnoreCase(LOAN_DECISION_ACTIVITY_NAME) || !task.getName().equalsIgnoreCase(MICRO_LOAN_DECISION_ACTIVITY_NAME))
        .collect(Collectors.toList());

    return RestResponse.success(toRestTasks(filteredActiveTasks));
  }

  private String getCurrentUserRole() throws UseCaseException
  {
    String userId = authenticationService.getCurrentUserId();
    try
    {
      Membership membership = membershipRepository.listAllByUserId(TenantId.valueOf(tenantIdProvider.getCurrentUserTenantId()), UserId.valueOf(userId)).get(0);
      return membership.getRoleId().getId();
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private boolean directorRole(String role)
  {
    return role.equals(ROLE_RANALYST) || role.equals(ROLE_HUB_DIRECTOR) || role.equals(ROLE_BRANCH_DIRECTOR) || role.equals(ROLE_RC_SPECIALIST);
  }

  private boolean headOfficeRole(String role)
  {
    return role.equals(ROLE_ADMIN_1);
  }
}
