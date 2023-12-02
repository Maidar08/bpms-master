/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task;

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

import static mn.erin.domain.bpm.BpmModuleConstants.AREA;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class GetBranchTaskMicro implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetBranchTaskMicro.class);
  private final AuthenticationService authenticationService;
  private final MembershipRepository membershipRepository;

  public GetBranchTaskMicro(AuthenticationService authenticationService, MembershipRepository membershipRepository)
  {
    this.authenticationService = authenticationService;
    this.membershipRepository = membershipRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Get Branch Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    try
    {
      String branchNumber = getGroupId();

      execution.removeVariable(AREA);
      execution.setVariable(AREA, branchNumber);

      LOGGER.info("#########  Finished Get Branch Task...");
    }
    catch (AimRepositoryException | NullPointerException e)
    {
      throw new ProcessTaskException(e.getMessage());
    }
  }

  private String getGroupId() throws AimRepositoryException
  {
    String currentUserId = authenticationService.getCurrentUserId();
    List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(currentUserId));
    return memberships.get(0).getGroupId().getId();
  }
}
