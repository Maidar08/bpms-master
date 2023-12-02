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

import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.MAIN_LOAN_CONTRACT_DOC_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_LOAN_CONTRACT;

/**
 * @author Tamir
 */
public class CreateOnlineSalaryLoanContractDocument extends AbstractUseCase<Map<String, String>, Boolean>
{
  private static final Logger LOG = LoggerFactory.getLogger(CreateOnlineSalaryLoanContractDocument.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final CaseService caseService;
  private final DocumentRepository documentRepository;

  public CreateOnlineSalaryLoanContractDocument(AuthenticationService authenticationService, AuthorizationService authorizationService,
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
    LOG.info("###### Gets all variables by CASE INSTANCE ID with CASE INSTANCE ID");
    String caseInstanceId = input.get("instanceId");
    String requestId = input.containsKey(PROCESS_REQUEST_ID) ? input.get(PROCESS_REQUEST_ID) : getRequestId(caseInstanceId);
    String processType = input.getOrDefault(PROCESS_TYPE_ID, EMPTY_VALUE);
    removePreviousContracts(caseInstanceId, requestId);
    createOnlineSalaryContractDocument(caseInstanceId, requestId, processType);

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
      LOG.error("###### COULD NOT GET REQUEST ID:", e);
    }
    return null;
  }

  private void removePreviousContracts(String caseInstanceId, String requestId)
  {
    if (null != caseInstanceId)
    {
      LOG.info("##### Removes previous loan contracts with REQUEST ID =[{}]", requestId);
      documentRepository.removeBy(caseInstanceId, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_CONTRACT);
      LOG.info("##### Successful removed previous loan contracts with REQUEST ID =[{}]", requestId);
    }
  }

  private void createOnlineSalaryContractDocument(String caseInstanceId, String requestId, String processType) throws UseCaseException
  {
    String documentId;
    if (processType.equals(BNPL_PROCESS_TYPE_ID))
    {
      LOG.info("########## Creates BNPL loan contract with REQUEST_ID =[{}]", requestId);
      documentId = System.currentTimeMillis() + "-" + "BNPLContractAsBase64";
    }
    else if (processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID)){
      LOG.info("########## Creates INSTANT_LOAN loan contract with REQUEST_ID =[{}]", requestId);
      documentId = System.currentTimeMillis() + "-" + "InstantLoanContractAsBase64";
    }
    else if (processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      LOG.info("########## Creates ONLINE_LEASING loan contract with REQUEST_ID =[{}]", requestId);
      documentId = System.currentTimeMillis() + "-" + "OnlineLeasingContractAsBase64";
    }
    else
    {
      LOG.info("########## Creates ONLINE_SALARY loan contract with REQUEST_ID =[{}]", requestId);
      documentId = System.currentTimeMillis() + "-" + ONLINE_SALARY_LOAN_CONTRACT_AS_BASE_64;
    }

    try
    {
      documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, caseInstanceId,
          SUB_CATEGORY_LOAN_CONTRACT, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_CONTRACT, documentId, SOURCE_PUBLISHER);
    }
    catch (BpmRepositoryException e)
    {
      LOG.error("####### ERROR OCCURRED DURING CREATE LOAN CONTRACT TO DATABASE : " + e.getMessage(), e);
      removePreviousContracts(caseInstanceId, requestId);
      try
      {
        LOG.info("##### TRYING TO CREATE LOAN CONTRACT DOCUMENT SECOND TIME.");

        documentRepository.create(documentId, MAIN_LOAN_CONTRACT_DOC_ID, caseInstanceId,
            SUB_CATEGORY_LOAN_CONTRACT, CATEGORY_LOAN_CONTRACT, SUB_CATEGORY_LOAN_CONTRACT, documentId, SOURCE_PUBLISHER);
      }
      catch (BpmRepositoryException ex)
      {
        LOG.error("####### ERROR OCCURRED DURING CREATE LOAN CONTRACT TO DATABASE : " + e.getMessage(), e);
        throw new UseCaseException(e.getMessage(), e);
      }
    }
    finally
    {
      LOG.info("###### FINAL INVOKED WHEN CREATE LOAN CONTRACT WITH REQUEST ID = [{}]!", requestId);
    }

    LOG.info("########## Successful created new loan contract document with REQUEST ID =[{}]", requestId);
  }
}
