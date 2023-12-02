package mn.erin.domain.bpm.usecase.loan;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmDocumentConstant.BNPL_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.BNPL_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.CONSUMPTION_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.CREDIT_LINE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.DIRECT_PRINTING_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.DISBURSEMENT_PERMISSION;
import static mn.erin.domain.bpm.BpmDocumentConstant.EMPLOYEE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.EMPLOYEE_MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.INSTANT_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.INSTANT_LOAN_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.LEASING_ORG_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_PERMISSION;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_TYPE_GOVERNMENT;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_TYPE_LOAN;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LEASING_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LEASING_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SALARY_ORG_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SME_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_COLLATERAL_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_FIDUC_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_WARRANTY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_ASSETS_LIST_MORTGAGE;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_CO_OWNER_CONSENT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.EQUIPMENT_ASSETS;
import static mn.erin.domain.bpm.BpmLoanContractConstants.FIDUCIARY_PROPERTY_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_REPORT_PAGE;
import static mn.erin.domain.bpm.BpmLoanContractConstants.MOVABLE_ASSETS_FIDUCIARY;
import static mn.erin.domain.bpm.BpmLoanContractConstants.NEXT_COLLATERAL_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DOCUMENT_TYPE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DOCUMENT_TYPE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.STRING_TO_DATE_EXCEPTION_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.STRING_TO_DATE_EXCEPTION_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.REQUEST_ID;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_ACC;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_FORACID;
import static mn.erin.domain.bpm.util.branch_banking.BranchBankingDocumentUtil.getDocumentParameters;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Bilguunbor
 */

public class GetLoanContractsByType extends AbstractUseCase<GetLoanContractsByTypeInput, List<Document>>
{
  private static final Logger LOG = LoggerFactory.getLogger(GetLoanContractsByType.class);
  private final DocumentService documentService;
  private final CaseService caseService;
  private final AuthenticationService authenticationService;

  public GetLoanContractsByType(DocumentService documentService, CaseService caseService,
      AuthenticationService authenticationService)
  {
    this.documentService = Objects.requireNonNull(documentService, "Document service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
  }

  @Override
  public List<Document> execute(GetLoanContractsByTypeInput input) throws UseCaseException
  {
    validateNotNull(input, INPUT_NULL_CODE + ": " + INPUT_NULL_MESSAGE);
    validateNotNull(input.getDocumentType(), PARAMETER_NULL_CODE + ": " + PARAMETER_NULL_MESSAGE);
    validateNotNull(input.getParameter(), CASE_INSTANCE_ID_NULL_CODE + ": " + CASE_INSTANCE_ID_NULL_MESSAGE);
    validateNotNull(input.getInstanceId(), BB_DOCUMENT_TYPE_NULL_CODE + ": " + BB_DOCUMENT_TYPE_NULL_MESSAGE);

    try
    {
      String instanceId = input.getInstanceId();
      String documentType = input.getDocumentType();

      Map<String, Object> parameter = input.getParameter();

      String documentId = getDocumentIdByType(documentType, parameter, instanceId);
      Map<String, String> documentParameter = getDocumentParameters(caseService, authenticationService, instanceId, documentType, parameter, null);
      String requestId = null;
      if (documentParameter.containsKey(REQUEST_ID))
      {
        requestId = documentParameter.get(REQUEST_ID);
      }
      else if (documentType.equals(ONLINE_LOAN_CONTRACT))
      {
        requestId = getValidString(caseService.getVariableById(instanceId, PROCESS_REQUEST_ID));
      }
      else if (documentType.equals(ONLINE_LEASING_CONTRACT) || documentType.equals(ONLINE_LEASING_REPORT))
      {
        requestId = documentParameter.get(P_FORACID);
      }
      else if (documentParameter.containsKey(P_ACC))
      {
        requestId = documentParameter.get(P_ACC);
      }
      LOG.info("LOAN CONTRACT BIP document parameter= [{}] with request id = {}", documentParameter, requestId);
      documentParameter.put(PRODUCT_CODE, getValidString(parameter.get(PRODUCT_CODE)));
      String base64File = documentService
          .downloadDocumentByType(documentParameter, documentType, instanceId);
      LOG.info("#### DOWNLOADED BASE64 CONTRACT DOCUMENT");
      List<Document> documents = new ArrayList<>();
      documents.add(new Document(null, null, null, getDocumentNameByType(documentType, documentId), null, null, null, base64File));

      return documents;
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    catch (ParseException e)
    {
      throw new UseCaseException(STRING_TO_DATE_EXCEPTION_CODE, STRING_TO_DATE_EXCEPTION_MESSAGE);
    }
  }

  private String getDocumentIdByType(String documentType, Map<String, Object> parameters, String instanceId) throws BpmServiceException
  {
    switch (documentType)
    {
    case SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT:
    case SME_LOAN_CONTRACT:
    case COLLATERAL_CO_OWNER_CONSENT:
    case NEXT_COLLATERAL_LOAN_CONTRACT:
    case ATTACHMENT_COLLATERAL_LOAN_CONTRACT:
    case ATTACHMENT_MORTGAGE_LOAN_CONTRACT:
    case ATTACHMENT_WARRANTY_LOAN_CONTRACT:
    case ATTACHMENT_FIDUC_LOAN_CONTRACT:
    case FIDUCIARY_PROPERTY_CONTRACT:
    case CONSUMPTION_LOAN_CONTRACT:
    case LOAN_REPORT_PAGE:
    case COLLATERAL_ASSETS_LIST_MORTGAGE:
    case MOVABLE_ASSETS_FIDUCIARY:
    case EQUIPMENT_ASSETS:
    case CREDIT_LINE_LOAN_CONTRACT:
    case MORTGAGE_LOAN_CONTRACT:
    case EMPLOYEE_LOAN_CONTRACT:
    case DIRECT_PRINTING_CONTRACT:
    case DISBURSEMENT_PERMISSION:
    case LOAN_PERMISSION:
    case EMPLOYEE_MORTGAGE_LOAN_CONTRACT:
    case MORTGAGE_TYPE_LOAN:
    case MORTGAGE_TYPE_GOVERNMENT:
      if (parameters.containsKey(ACCOUNT_NUMBER))
      {
        return String.valueOf(parameters.get(ACCOUNT_NUMBER));
      }
      return String.valueOf(caseService.getVariableById(instanceId, ACCOUNT_NUMBER));
    case BNPL_CONTRACT:
    case BNPL_REPORT:
    case SALARY_ORG_CONTRACT:
    case LEASING_ORG_CONTRACT:
    case INSTANT_LOAN_CONTRACT:
    case INSTANT_LOAN_REPORT:
    case ONLINE_LEASING_CONTRACT:
    case ONLINE_LEASING_REPORT:
      return String.valueOf(parameters.get(P_ACC));
    default:
      return EMPTY_VALUE;
    }
  }

  private String getDocumentNameByType(String documentType, String documentId)
  {
    switch (documentType)
    {
    case SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT:
      return SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT + documentId;
    case SME_LOAN_CONTRACT:
      return SME_LOAN_CONTRACT + documentId;
    case NEXT_COLLATERAL_LOAN_CONTRACT:
      return NEXT_COLLATERAL_LOAN_CONTRACT + documentId;
    case COLLATERAL_CO_OWNER_CONSENT:
      return COLLATERAL_CO_OWNER_CONSENT + documentId;
    case ATTACHMENT_WARRANTY_LOAN_CONTRACT:
      return ATTACHMENT_WARRANTY_LOAN_CONTRACT + documentId;
    case ATTACHMENT_MORTGAGE_LOAN_CONTRACT:
      return ATTACHMENT_MORTGAGE_LOAN_CONTRACT + documentId;
    case ATTACHMENT_COLLATERAL_LOAN_CONTRACT:
      return ATTACHMENT_COLLATERAL_LOAN_CONTRACT + documentId;
    case ATTACHMENT_FIDUC_LOAN_CONTRACT:
      return ATTACHMENT_FIDUC_LOAN_CONTRACT + documentId;
    case COLLATERAL_ASSETS_LIST_MORTGAGE:
      return COLLATERAL_ASSETS_LIST_MORTGAGE + documentId;
    case MOVABLE_ASSETS_FIDUCIARY:
      return MOVABLE_ASSETS_FIDUCIARY + documentId;
    case EQUIPMENT_ASSETS:
      return EQUIPMENT_ASSETS + documentId;
    case CREDIT_LINE_LOAN_CONTRACT:
      return CREDIT_LINE_LOAN_CONTRACT + documentId;
    case MORTGAGE_LOAN_CONTRACT:
      return MORTGAGE_LOAN_CONTRACT + documentId;
    case MORTGAGE_TYPE_LOAN:
      return MORTGAGE_TYPE_LOAN + documentId;
    case MORTGAGE_TYPE_GOVERNMENT:
      return MORTGAGE_TYPE_GOVERNMENT + documentId;
    case EMPLOYEE_LOAN_CONTRACT:
      return EMPLOYEE_LOAN_CONTRACT + documentId;
    case DIRECT_PRINTING_CONTRACT:
      return DIRECT_PRINTING_CONTRACT + documentId;
    case EMPLOYEE_MORTGAGE_LOAN_CONTRACT:
      return EMPLOYEE_MORTGAGE_LOAN_CONTRACT + documentId;
    case DISBURSEMENT_PERMISSION:
      return DISBURSEMENT_PERMISSION + documentId;
    case LOAN_PERMISSION:
      return LOAN_PERMISSION + documentId;
    case BNPL_CONTRACT:
      return BNPL_CONTRACT + documentId;
    case BNPL_REPORT:
      return BNPL_REPORT + documentId;
    case SALARY_ORG_CONTRACT:
      return SALARY_ORG_CONTRACT + documentId;
    case LEASING_ORG_CONTRACT:
      return LEASING_ORG_CONTRACT + documentId;
    case INSTANT_LOAN_CONTRACT:
      return INSTANT_LOAN_CONTRACT + documentId;
    case INSTANT_LOAN_REPORT:
      return INSTANT_LOAN_REPORT + documentId;
    default:
      return EMPTY_VALUE;
    }
  }
}
