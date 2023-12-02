/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package consumption.service_task.direct_online_salary;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.contract.CreateLoanPaymentScheduleDocument;

import static consumption.util.CamundaUtils.updateTaskStatus;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class OnlineSalaryCreateLoanPaymentScheduleTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineSalaryCreateLoanPaymentScheduleTask.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  private final CaseService caseService;
  private final DocumentRepository documentRepository;

  public OnlineSalaryCreateLoanPaymentScheduleTask(AuthenticationService authenticationService, AuthorizationService authorizationService,
      CaseService caseService, DocumentRepository documentRepository)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.documentRepository = Objects.requireNonNull(documentRepository, "Document repository is required!");
  }

  @Override
  public void execute(DelegateExecution execution)
  {
    try
    {
      String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
      String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
      String instanceId = execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_SALARY_PROCESS_TYPE) ? caseInstanceId : processInstanceId;
      String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
      boolean hasNoAction = StringUtils.isBlank(getValidString(execution.getVariable(ACTION_TYPE)));

      LOGGER.info("##### LOAN PAYMENT SCHEDULE persisting to database. {}",
          (hasNoAction ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));

      if (null == instanceId)
      {
        LOGGER.error("######### CASE INSTANCE ID is null during download loan payment schedule! with REQUEST_ID =[{}]. {}", requestId,
            (hasNoAction ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
        throw new BpmnError("Create Loan Payment Schedule", "###### CASE INSTANCE ID is null! with REQUEST ID =" + requestId);
      }

      Map<String, String> input = new HashMap<>();
      input.put(PROCESS_TYPE_ID, (String) execution.getVariable(PROCESS_TYPE_ID));
      input.put(PROCESS_REQUEST_ID, requestId);
      input.put(CASE_INSTANCE_ID, caseInstanceId);
      CreateLoanPaymentScheduleDocument createLoanPaymentScheduleDocument = new CreateLoanPaymentScheduleDocument(authenticationService, authorizationService,
          caseService,
          documentRepository);
      Boolean isCreated = createLoanPaymentScheduleDocument.execute(input);

      if (null != isCreated && isCreated)
      {
        LOGGER.info("########## Successful create loan payment schedule document with REQUEST ID =[{}]. {}", requestId,
            (hasNoAction ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
      }
      else
      {
        LOGGER.error("###### Could not create loan loan payment schedule document with REQUEST ID =[{}]. {}", requestId,
            (hasNoAction ? "" : " ActionType :" + execution.getVariable(ACTION_TYPE) + "."));
        throw new BpmnError("Create Loan Payment Schedule", "###### Could not create loan payment schedule document with REQUEST ID =" + requestId);
      }
      if (execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Create loan payment schedule document task", "Success");
      if (execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Create online leasing payment schedule document task", "Success");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (execution.getVariable(PROCESS_TYPE_ID).equals(INSTANT_LOAN_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Create loan payment schedule document task", "Failed");
      execution.setVariable(ERROR_CAUSE, e.getMessage());
      if (execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_LEASING_PROCESS_TYPE_ID))
        updateTaskStatus(execution, "Create online leasing payment schedule document task", "Failed");
      execution.setVariable(ERROR_CAUSE, e.getMessage());
      throw new BpmnError("Create Loan Payment Schedule", e.getMessage());
    }
  }
}
