package mn.erin.domain.bpm.usecase.contract;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;

import static mn.erin.domain.bpm.BpmDocumentConstant.SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPLOYEE_LOAN_PRODUCT_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EQUAL_PRINCIPLE_PAYMENT_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.EQUATED_MONTHLY_INSTALLMENT_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUAL_PRINCIPLE_PAYMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_EQUATED_MONTHLY_INSTALLMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE_ID;

/**
 * @author Tamir
 */
public class DownloadLoanContractAsBase64 extends AbstractUseCase<DownloadLoanContractAsBase64Input, String>
{
  private static final Logger LOG = LoggerFactory.getLogger(DownloadLoanContractAsBase64.class);

  private final DocumentService documentService;
  private final CaseService caseService;
  private final Environment environment;

  public DownloadLoanContractAsBase64(DocumentService documentService, CaseService caseService, Environment environment)
  {
    this.documentService = Objects.requireNonNull(documentService, "Document service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.environment = environment;
  }

  @Override
  public String execute(DownloadLoanContractAsBase64Input input) throws UseCaseException
  {
    String caseInstanceId = input.getCaseInstanceId();

    String accountNumber = null;
    String repaymentType = null;
    String productId = null;

    try
    {
      List<Variable> allVariables = caseService.getVariables(caseInstanceId);
      String processRequestId = getProcessRequestId(allVariables);

      String productDesc = caseService.getVariableById(caseInstanceId, "loanProduct").toString();

      if (productDesc.equals(environment.getProperty(EMPLOYEE_LOAN_PRODUCT_ID)))
      {
        productId = environment.getProperty(EMPLOYEE_LOAN_PRODUCT_ID);
      }


      for (Variable variable : allVariables)
      {
        String variableId = variable.getId().getId();

        if (variableId.equals(LOAN_ACCOUNT_NUMBER))
        {
          accountNumber = getNotNullValue(variable);
        }
        else if (variableId.equals(REPAYMENT_TYPE_ID))
        {
          repaymentType = getRepaymentTypeValue(variable);
        }
      }

      if (null == accountNumber)
      {
        String errorCode = "DMS020";
        LOG.info("########## LOAN ACCOUNT NUMBER is null!, REQUEST_ID = [{}]", processRequestId);
        throw new UseCaseException(errorCode, "########## LOAN ACCOUNT NUMBER is null!, REQUEST_ID =" + processRequestId);
      }

      if (null == repaymentType)
      {
        String errorCode = "DMS021";
        LOG.info("########## REPAYMENT TYPE is null, REQUEST_ID = [{}]", processRequestId);
        throw new UseCaseException(errorCode, "########## REPAYMENT TYPE is null!, REQUEST_ID =" + processRequestId);
      }

      LOG.info("########## Downloads customer LOAN CONTRACT by ACCOUNT NUMBER = [{}], PAYMENT_TYPE = [{}] with REQUEST_ID = [{}]", accountNumber, repaymentType,
          processRequestId);
      return documentService.downloadContractAsBase64(accountNumber, repaymentType, productId);
    }
    catch (BpmServiceException e)
    {
      LOG.error("######## BPM service exception occurred during download contract : ", e);
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

  private String getRepaymentTypeValue(Variable variable)
  {
    Serializable value = variable.getValue();

    if (null != value)
    {
      String repaymentTypeId = (String) value;

      if (repaymentTypeId.equals(SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT))
      {
        return EQUAL_PRINCIPLE_PAYMENT_VALUE;
      }
      else if (repaymentTypeId.equals(REPAYMENT_EQUAL_PRINCIPLE_PAYMENT))
      {
        return EQUAL_PRINCIPLE_PAYMENT_VALUE;
      }
      else
      {
        if (repaymentTypeId.equals(REPAYMENT_EQUATED_MONTHLY_INSTALLMENT))
        {
          return EQUATED_MONTHLY_INSTALLMENT_VALUE;
        }
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
    throw new UseCaseException(errorCode, "######### LOAN ACCOUNT NUMBER variable is null during download contract.");
  }
}
