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
 * @author Tamir
 */
public class DownloadOnlineSalaryContractAsBase64 extends AbstractUseCase<DownloadLoanContractAsBase64Input, String>
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadOnlineSalaryContractAsBase64.class);

  private final DocumentService documentService;
  private final CaseService caseService;

  public DownloadOnlineSalaryContractAsBase64(DocumentService documentService, CaseService caseService)
  {
    this.documentService = Objects.requireNonNull(documentService, "Document service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
  }

  @Override
  public String execute(DownloadLoanContractAsBase64Input input) throws UseCaseException
  {
    String caseInstanceId = input.getCaseInstanceId();

    String accountNumber = null;

    try
    {
      List<Variable> allVariables = caseService.getVariables(caseInstanceId);
      String requestId = getProcessRequestId(allVariables);

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
        LOG.info("########## LOAN ACCOUNT NUMBER is null!, REQUEST_ID = [{}]", requestId);
        throw new UseCaseException(errorCode, "LOAN ACCOUNT NUMBER is null!, REQUEST_ID =" + requestId);
      }

      if (null == requestId)
      {
        String errorCode = "DMS030";
        LOG.info("LOAN REQUEST ID is empty with REQUEST_ID = [{}]", requestId);
        throw new UseCaseException(errorCode, "LOAN REQUEST ID is empty with REQUEST_ID" + requestId);
      }

      LOG.info("########## Downloads customer ONLINE SALARY LOAN CONTRACT by ACCOUNT NUMBER = [{}] with REQUEST_ID = [{}]", accountNumber, requestId);
      return documentService.downloadOnlineSalaryContractAsBase64(accountNumber, requestId);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
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

  private String getNotNullValue(Variable variable) throws UseCaseException
  {
    Serializable value = variable.getValue();

    if (null != value)
    {
      return (String) value;
    }
    String errorCode = "DMS019";
    throw new UseCaseException(errorCode, "######### LOAN ACCOUNT NUMBER variable is null during download ONLINE SALARY contract.");
  }
}
