/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task.bpms.report;

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
import mn.erin.domain.bpm.usecase.contract.CreateLoanReportDocument;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

/**
 * @author Zorig
 */
public class CreateLoanReportTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateLoanReportTask.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final CaseService caseService;
  private final DocumentRepository documentRepository;

  public CreateLoanReportTask(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
    String registrationNumber = (String) execution.getVariable(REGISTER_NUMBER);
    String requestId = (String )execution.getVariable(PROCESS_REQUEST_ID);
    String cif = (String)execution.getVariable(CIF_NUMBER);
    String accountNumber = (String)execution.getVariable(LOAN_ACCOUNT_NUMBER);

    LOGGER.info("#########  Create Loan Report Task... Register Number: [{}], Request ID: [{}], CIF: [{}], Account Number: [{}]", registrationNumber, requestId, cif, accountNumber);

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    LOGGER.info("##### LOAN REPORT DOCUMENT persisting to database.");

    if (null == caseInstanceId)
    {
      String errorCode = "PUBLISHER005";
      LOGGER.error("######### CASE INSTANCE ID is null during download loan report! with REQUEST_ID =[{}]", requestId);
      throw new ProcessTaskException(errorCode, "###### CASE INSTANCE ID is null! with REQUEST ID = " + requestId);
    }

    CreateLoanReportDocument createLoanReportDocument = new CreateLoanReportDocument(authenticationService, authorizationService, caseService,
        documentRepository);
    Boolean isCreated = createLoanReportDocument.execute(caseInstanceId);

    if (null != isCreated && isCreated)
    {
      LOGGER.info("########## Successful create loan report document with REQUEST ID =[{}]", requestId);
    }
    else
    {
      String errorCode = "PUBLISHER006";
      LOGGER.error("###### Could not create loan report document with REQUEST ID =[{}]", requestId);
      throw new ProcessTaskException(errorCode, "###### Could not create loan report document with REQUEST ID =" + requestId);
    }

    LOGGER.info("########## Finished creating loan report with Request Id = [{}], Account Number = [{}]!", requestId, accountNumber);
  }
}
