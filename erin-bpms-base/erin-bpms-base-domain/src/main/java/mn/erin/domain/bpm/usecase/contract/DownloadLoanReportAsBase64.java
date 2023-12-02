/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;

import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Zorig
 */
public class DownloadLoanReportAsBase64 extends AbstractUseCase<String, String>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadLoanReportAsBase64.class);

  private final DocumentService documentService;
  private final CaseService caseService;

  public DownloadLoanReportAsBase64(DocumentService documentService, CaseService caseService)
  {
    this.documentService = Objects.requireNonNull(documentService, "Document service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
  }

  @Override
  public String execute(String caseInstanceId) throws UseCaseException
  {
    String accountNumber = null;

    try
    {
      List<Variable> allVariables = caseService.getVariables(caseInstanceId);
      String processRequestId = getProcessRequestId(allVariables);

      for (Variable variable : allVariables)
      {
        String variableId = variable.getId().getId();

        if (variableId.equals(LOAN_ACCOUNT_NUMBER))
        {
          accountNumber = getNotNullValue(variable);
        }
      }

      if (null == accountNumber)
      {
        String errorCode = "DMS020";
        LOGGER.info("########## LOAN ACCOUNT NUMBER is null!, REQUEST_ID = [{}]", processRequestId);
        throw new UseCaseException(errorCode, "########## LOAN ACCOUNT NUMBER is null!, REQUEST_ID =" + processRequestId);
      }

      LOGGER.info("########## Downloads customer LOAN REPORT by ACCOUNT NUMBER = [{}] with REQUEST_ID = [{}]", accountNumber,
          processRequestId);
      return documentService.downloadLoanReportAsBase64(accountNumber);
    }
    catch (BpmServiceException e)
    {
      LOGGER.error("######## BPM service exception occurred during download report : ", e);
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }

  private String getNotNullValue(Variable variable) throws UseCaseException
  {
    Serializable value = variable.getValue();

    if (null != value)
    {
      return (String) value;
    }
    String errorCode = "DMS019";
    throw new UseCaseException(errorCode, "######### LOAN ACCOUNT NUMBER variable is null during download contract.");
  }

  private String getProcessRequestId(List<Variable> allVariables)
  {
    for (Variable variable : allVariables)
    {
      String id = variable.getId().getId();

      if (id.equals(PROCESS_REQUEST_ID))
      {
        return (String) variable.getValue();
      }
    }
    return null;
  }
}
