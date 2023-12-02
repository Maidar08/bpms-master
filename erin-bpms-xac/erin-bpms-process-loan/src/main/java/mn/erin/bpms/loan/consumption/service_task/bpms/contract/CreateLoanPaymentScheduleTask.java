/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.bpms.contract;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.contract.CreateLoanPaymentScheduleDocument;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class CreateLoanPaymentScheduleTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateLoanPaymentScheduleTask.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  private final CaseService caseService;
  private final DocumentRepository documentRepository;

  public CreateLoanPaymentScheduleTask(AuthenticationService authenticationService, AuthorizationService authorizationService,
      CaseService caseService, DocumentRepository documentRepository)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.documentRepository = Objects.requireNonNull(documentRepository, "Document repository is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    LOGGER.info("##### LOAN PAYMENT SCHEDULE persisting to database.");

    if (null == caseInstanceId)
    {
      String errorCode = "PUBLISHER003";
      LOGGER.error("######### CASE INSTANCE ID is null during download loan payment schedule! with REQUEST_ID =[{}]", requestId);
      throw new ProcessTaskException(errorCode, "###### CASE INSTANCE ID is null! with REQUEST ID =" + requestId);
    }

    Map<String, String> input = new HashMap<>();
    input.put(CASE_INSTANCE_ID,caseInstanceId);
    CreateLoanPaymentScheduleDocument createLoanPaymentScheduleDocument = new CreateLoanPaymentScheduleDocument(authenticationService, authorizationService, caseService,
        documentRepository);
    Boolean isCreated = createLoanPaymentScheduleDocument.execute(input);

    if (null != isCreated && isCreated)
    {
      LOGGER.info("########## Successful create loan payment schedule document with REQUEST ID =[{}]", requestId);
    }
    else
    {
      String errorCode = "PUBLISHER004";
      LOGGER.error("###### Could not create loan loan payment schedule document with REQUEST ID =[{}]", requestId);
      throw new ProcessTaskException(errorCode, "###### Could not create loan payment schedule document with REQUEST ID =" + requestId);
    }
  }
}
