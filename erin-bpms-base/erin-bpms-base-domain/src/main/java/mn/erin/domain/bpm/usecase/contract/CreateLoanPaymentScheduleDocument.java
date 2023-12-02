/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.contract;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PAYMENT_SCHEDULE_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_PAYMENT_SCHEDULE_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.NAME_DOCUMENT_LOAN_PAYMENT_SCHEDULE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_REPORT;

/**
 * @author Zorig
 */
public class CreateLoanPaymentScheduleDocument extends AbstractUseCase<Map<String, String>, Boolean>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateLoanPaymentScheduleDocument.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final CaseService caseService;
  private final DocumentRepository documentRepository;

  public CreateLoanPaymentScheduleDocument(AuthenticationService authenticationService, AuthorizationService authorizationService,
      CaseService caseService, DocumentRepository documentRepository)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.documentRepository = Objects.requireNonNull(documentRepository, "Document repository is required!");
  }

  @Override
  public Boolean execute(Map<String, String> input) throws UseCaseException
  {
    String caseInstanceId = input.get(CASE_INSTANCE_ID);
    LOGGER.info("###### Gets all variables by CASE INSTANCE ID.");
    String requestId = getRequestId(caseInstanceId, input);

    removePreviousPaymentScheduleDocument(caseInstanceId, requestId);
    createLoanPaymentScheduleDocument(caseInstanceId, requestId);

    return true;
  }

  private String getRequestId(String caseInstanceId, Map<String, String> input)
  {
    if(input.containsKey(PROCESS_TYPE_ID)){
      return  input.get(PROCESS_REQUEST_ID);
    }
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
      LOGGER.error("###### COULD NOT GET REQUEST ID: ", e);
    }
    return null;
  }

  private void removePreviousPaymentScheduleDocument(String caseInstanceId, String requestId)
  {
    if (null != caseInstanceId)
    {
      LOGGER.info("##### Removes previous loan payment schedule with REQUEST ID =[{}]", requestId);
      documentRepository.removeBy(caseInstanceId, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, NAME_DOCUMENT_LOAN_PAYMENT_SCHEDULE);

      LOGGER.info("##### Successful removed previous loan payment schedule with REQUEST ID =[{}]", requestId);
    }
  }

  private void createLoanPaymentScheduleDocument(String caseInstanceId, String requestId) throws UseCaseException
  {
    LOGGER.info("########## Creates loan payment schedule with REQUEST_ID =[{}]", requestId);

    String documentId = String.valueOf(System.currentTimeMillis()) + "-" + LOAN_PAYMENT_SCHEDULE_AS_BASE_64;

    try
    {
      documentRepository.create(documentId, MAIN_LOAN_PAYMENT_SCHEDULE_DOC_ID, caseInstanceId,
          NAME_DOCUMENT_LOAN_PAYMENT_SCHEDULE, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, LOAN_PAYMENT_SCHEDULE_AS_BASE_64, SOURCE_PUBLISHER);
    }
    catch (BpmRepositoryException e)
    {
      LOGGER.error("####### ERROR OCCURRED DURING CREATE LOAN PAYMENT SCHEDULE TO DATABASE : " + e.getMessage(), e);
      removePreviousPaymentScheduleDocument(caseInstanceId, requestId);

      try
      {
        LOGGER.info("##### TRYING TO CREATE LOAN PAYMENT SCHEDULE DOCUMENT SECOND TIME.");

        documentRepository.create(documentId, MAIN_LOAN_PAYMENT_SCHEDULE_DOC_ID, caseInstanceId,
            NAME_DOCUMENT_LOAN_PAYMENT_SCHEDULE, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_REPORT, LOAN_PAYMENT_SCHEDULE_AS_BASE_64, SOURCE_PUBLISHER);
      }
      catch (BpmRepositoryException ex)
      {
        LOGGER.error("####### ERROR OCCURRED DURING CREATE LOAN PAYMENT SCHEDULE TO DATABASE : " + e.getMessage(), e);
        throw new UseCaseException(e.getMessage(), e);
      }
    }
    finally
    {
      LOGGER.info("###### FINALLY INVOKED!");
    }

    LOGGER.info("########## Successful created new loan payment schedule document with REQUEST ID =[{}]", requestId);
  }
}
