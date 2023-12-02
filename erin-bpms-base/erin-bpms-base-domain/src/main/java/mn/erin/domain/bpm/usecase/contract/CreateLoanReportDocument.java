/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.contract;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesById;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesByIdOutput;

import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_REPORT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_REPORT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_REPORT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_REPORT;

/**
 * @author Zorig
 */
public class CreateLoanReportDocument extends AbstractUseCase<String, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateLoanReportDocument.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final CaseService caseService;
  private final DocumentRepository documentRepository;

  public CreateLoanReportDocument(AuthenticationService authenticationService, AuthorizationService authorizationService,
      CaseService caseService, DocumentRepository documentRepository)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.documentRepository = Objects.requireNonNull(documentRepository, "Document repository is required!");
  }

  @Override
  public Boolean execute(String caseInstanceId) throws UseCaseException
  {
    LOGGER.info("###### Gets all variables by CASE INSTANCE ID.");

    String requestId = getRequestId(caseInstanceId);

    removePreviousReports(caseInstanceId, requestId);
    createLoanReportDocument(caseInstanceId, requestId);

    return true;
  }

  private String getRequestId(String caseInstanceId)
  {
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    try
    {
      GetVariablesByIdOutput variablesOutput = getVariablesById.execute(caseInstanceId);
      List<Variable> variables = variablesOutput.getVariables();

      for (Variable variable : variables)
      {
        String variableId = variable.getId().getId();
        if (variableId.equalsIgnoreCase(PROCESS_REQUEST_ID))
        {
          return (String) variable.getValue();
        }
      }
    }
    catch (UseCaseException e)
    {
      LOGGER.error("###### COULD NOT GET REQUEST ID:", e);
    }
    return null;
  }

  private void removePreviousReports(String caseInstanceId, String requestId)
  {
    if (!StringUtils.isBlank(caseInstanceId))
    {
      LOGGER.info("##### Removes previous loan reports with REQUEST ID =[{}]", requestId);
      documentRepository.removeBy(caseInstanceId, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, LOAN_REPORT_NAME);

      LOGGER.info("##### Successful removed previous loan reports with REQUEST ID =[{}]", requestId);
    }
  }

  private void createLoanReportDocument(String caseInstanceId, String requestId) throws UseCaseException
  {
    LOGGER.info("########## Creates loan report with REQUEST_ID =[{}]", requestId);

    String documentId = (System.currentTimeMillis()) + "-" + LOAN_REPORT_AS_BASE_64;

    try
    {
      documentRepository.create(documentId, MAIN_LOAN_REPORT_DOC_ID, caseInstanceId,
          LOAN_REPORT_NAME + ".pdf", CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, LOAN_REPORT_AS_BASE_64, SOURCE_PUBLISHER);
    }
    catch (BpmRepositoryException e)
    {
      LOGGER.error("####### ERROR OCCURRED DURING CREATION OF LOAN REPORT TO DATABASE : " + e.getMessage(), e);
      removePreviousReports(caseInstanceId, requestId);

      try
      {
        LOGGER.info("##### TRYING TO CREATE LOAN REPORT DOCUMENT SECOND TIME.");

        documentRepository.create(documentId, MAIN_LOAN_REPORT_DOC_ID, caseInstanceId,
            SUB_CATEGORY_LOAN_REPORT, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, LOAN_REPORT_AS_BASE_64, SOURCE_PUBLISHER);
      }
      catch (BpmRepositoryException ex)
      {
        LOGGER.error("####### ERROR OCCURRED DURING CREATION OF LOAN REPORT TO DATABASE : " + e.getMessage(), e);
        throw new UseCaseException(e.getMessage(), e);
      }
    }
    finally
    {
      LOGGER.info("###### FINALLY INVOKED!");
    }

    LOGGER.info("########## Successful created new loan report document with REQUEST ID =[{}]", requestId);
  }

}
