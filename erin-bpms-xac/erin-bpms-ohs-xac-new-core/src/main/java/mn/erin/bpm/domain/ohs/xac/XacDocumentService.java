/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.contract.BiPath;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.BiPathRepository;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;

import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_CONTRACT_FILE_FORMAT;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_DATA_SIZE_FOR_CHUNK_DOWNLOAD;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_FUNCTION_DOWNLOAD_CONTRACT;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_PATH_LOAN_REPORT_CREATION;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_REPORT_ABSOLUTE_PATH;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_REPORT_ABSOLUTE_PATH_LOAN_PAYMENT_SCHEDULE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_REPORT_ONLINE_SALARY_ABSOLUTE_PATH;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_USER_ID;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PUBLISHER_USER_PASSWORD;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.OK;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getAccessCode;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getDmsEndPoint;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getDocRefResult;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getDocumentRequestBody;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getDocumentSoapAction;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getDocumentUploadUserId;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getFunction;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getRequestBodyDownloadContract;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getRequestBodyDownloadPaymentSchedule;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getRequestBodyDownloadReport;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getUploadResult;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.getUploadSoapAction;
import static mn.erin.bpm.domain.ohs.xac.util.DocumentServiceUtil.validateParameters;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getFunctionByProduct;
import static mn.erin.bpm.domain.ohs.xac.util.XacHttpUtil.getPropertyByKey;
import static mn.erin.domain.bpm.BpmDocumentConstant.BNPL_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.BNPL_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.CONSUMPTION_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.CREDIT_LINE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.DIRECT_PRINTING_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.DISBURSEMENT_PERMISSION;
import static mn.erin.domain.bpm.BpmDocumentConstant.EMPLOYEE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.EMPLOYEE_MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.GENERAL_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.INSTANT_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.INSTANT_LOAN_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.LEASING_ORG_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_PERMISSION;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_REPAYMENT_AFTER;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_REPAYMENT_BEFORE;
import static mn.erin.domain.bpm.BpmDocumentConstant.MICRO_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_TYPE_GOVERNMENT;
import static mn.erin.domain.bpm.BpmDocumentConstant.MORTGAGE_TYPE_LOAN;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LEASING_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LEASING_REPORT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.REPURCHASE_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SALARY_ORG_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SME_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_COLLATERAL_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_FIDUC_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_MORTGAGE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ATTACHMENT_WARRANTY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_ASSETS_LIST_MORTGAGE;
import static mn.erin.domain.bpm.BpmLoanContractConstants.COLLATERAL_CO_OWNER_CONSENT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.CONTRACT_Id;
import static mn.erin.domain.bpm.BpmLoanContractConstants.EQUIPMENT_ASSETS;
import static mn.erin.domain.bpm.BpmLoanContractConstants.FIDUCIARY_PROPERTY_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.LOAN_REPORT_PAGE;
import static mn.erin.domain.bpm.BpmLoanContractConstants.MOVABLE_ASSETS_FIDUCIARY;
import static mn.erin.domain.bpm.BpmLoanContractConstants.NEXT_COLLATERAL_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmLoanContractConstants.PURCHASE_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EQUAL_PRINCIPLE_PAYMENT_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_ABSOLUTE_PATH;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_ONLINE_LOAN_ABSOLUTE_PATH;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_BNPL_ABSOLUTE_PATH;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_BNPL_CONTRACT_XDO;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_ENDPOINT;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_FUNCTION;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_INSTANT_LOAN_CONTRACT_XDO;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_ONLINE_LEASING_ABSOLUTE_PATH;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_ONLINE_LEASING_CONTRACT_XDO;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_ONLINE_LOAN_CONTRACT_XDO;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_ORGANIZATION_ABSOLUTE_PATH;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_PURCHASE_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_SOAP_ACTION_DOWNLOAD_CONTRACT;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBR2008A;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBR2008B;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBR2008C;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBR2009NEW;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBR2011;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRBDG;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRBZET;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRCNIPO;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRCOLA;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRCRLA;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBREMLC;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRESL;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRFID;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRGMLC;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRLCC;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRLIP;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRMLC;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRMSL;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRSMEE;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRXAC;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRXACC;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBRZOZ;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBSLSCT;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBSROSO;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XBZ_CONT;
import static mn.erin.domain.bpm.BpmPublisherConstants.BI_PUBLISHER_XRRSCHDL;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_ABSOLUTE_PATH_CONSTANT_IS_INVALID_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_ABSOLUTE_PATH_CONSTANT_IS_INVALID_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_ABSOLUTE_PATH_IS_INVALID_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_ABSOLUTE_PATH_IS_INVALID_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_BNPL_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_BNPL_LOAN_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_BNPL_LOAN_REPORT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_BNPL_LOAN_REPORT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_COLLATERAL_ASSETS_LIST_MORTGAGE_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_COLLATERAL_ASSETS_LIST_MORTGAGE_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_COLLATERAL_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_COLLATERAL_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_COLLATERAL_CO_OWNER_CONSENT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_COLLATERAL_CO_OWNER_CONSENT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CONSUMPTION_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CONSUMPTION_LOAN_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CREDIT_LINE_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_CREDIT_LINE_LOAN_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_DIRECT_PRINTING_DEPOSIT_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_DIRECT_PRINTING_DEPOSIT_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_DISBURSEMENT_PERMISSION_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_DISBURSEMENT_PERMISSION_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_EMPLOYEE_CONSUMPTION_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_EMPLOYEE_CONSUMPTION_LOAN_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_EMPLOYEE_MORTGAGE_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_EMPLOYEE_MORTGAGE_LOAN_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_EQUIPMENT_ASSETS_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_EQUIPMENT_ASSETS_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_FIDUC_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_FIDUC_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_INSTANT_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_INSTANT_LOAN_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_INSTANT_LOAN_REPORT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_INSTANT_LOAN_REPORT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LEASING_ORG_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LEASING_ORG_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LOAN_PERMISSION_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LOAN_PERMISSION_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LOAN_REPAYMENT_AFTER_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LOAN_REPAYMENT_AFTER_CODE_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LOAN_REPAYMENT_BEFORE_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LOAN_REPAYMENT_BEFORE_CODE_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LOAN_REPORT_PAGE_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_LOAN_REPORT_PAGE_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_MORTGAGE_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_MORTGAGE_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_MORTGAGE_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_MORTGAGE_LOAN_CONTRACT_CODE_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_MOVABLE_ASSETS_FIDUCIARY_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_MOVABLE_ASSETS_FIDUCIARY_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_NEXT_COLLATERAL_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_NEXT_COLLATERAL_LOAN_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_SALARY_ORG_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_SALARY_ORG_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_SME_LOAN_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_SME_LOAN_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_WARRANTY_CONTRACT_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.BIP_FAILED_TO_DOWNLOAD_WARRANTY_CONTRACT_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.DOCUMENT_TYPE_IS_VALID_CODE;
import static mn.erin.domain.bpm.BpmPublisherMessageConstant.DOCUMENT_TYPE_IS_VALID_MESSAGE;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_ACC;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_CONTRACTN;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_FORACID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Tamir
 */
public class XacDocumentService implements DocumentService
{
  private static final Logger LOG = LoggerFactory.getLogger(XacDocumentService.class);
  private static final String PUBLISHER_RESPONSE_BODY_NULL_ERR_CODE = "DMS022";
  private static final String PUBLISHER_CONTRACT_BASE_64_NULL_ERR_CODE = "DMS023";

  private static final String TEXT_XML = "text/xml";
  private static final String OPTIONAL = "<!--Optional:-->\n";

  // LDMS Consumption loan request STATIC fields, following fields should be dynamic.
  private static final String APPLICATION_TYPE = "Retail";
  private static final String PRODUCT_CODE = "EB50";

  private static final String PRODUCT_DESCRIPTION = "365-Цалингийн зээл - Иргэн";
  private static final String SEGMENT = "I03";
  private static final String STAGE = "1";

  private final Environment environment;
  private final CaseService caseService;
  private final AuthenticationService authenticationService;
  private static BiPathRepository biPathRepository;

  public XacDocumentService(Environment environment, CaseService caseService, AuthenticationService authenticationService, BiPathRepository biPathRepository)
  {
    this.environment = Objects.requireNonNull(environment, "Environment is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.biPathRepository = biPathRepository;
  }

  @Override
  public String uploadDocument(String caseInstanceId, String documentId, String mainType, String subType, String groupId, String fileName,
      String documentAsBase64, Map<String, String> parameters)
      throws BpmServiceException
  {
    XacHttpClient xacHttpClient = getUploadDocumentClient();

    String accessCode = getAccessCode(environment);
    String currentUserId = getDocumentUploadUserId(environment);

    LOG.info("###### LDMS upload access code = [{}]", accessCode);

    String processRequestId = null;
    String processTypeId = null;
    if (!parameters.isEmpty())
    {
      processTypeId = parameters.get(PROCESS_TYPE_ID);
    }

    String cifNumber = null;
    String registerNumber = null;
    String fullName = null;

    if (null == currentUserId)
    {
      currentUserId = authenticationService.getCurrentUserId();
    }

    if ((!StringUtils.isBlank(processTypeId)) && (processTypeId.equals(BNPL_PROCESS_TYPE_ID) || processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID) || processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID)))
    {
      processRequestId = parameters.get(PROCESS_REQUEST_ID);
      cifNumber = parameters.get(CIF_NUMBER);
      registerNumber = parameters.get(REGISTER_NUMBER);
      fullName = parameters.get(FULL_NAME);
    }
    if (processTypeId != null && processTypeId.contains("Organization"))
    {
      processRequestId = parameters.get(CONTRACT_Id);
      registerNumber = parameters.get("registerId");
      fullName = parameters.get("registeredName");
      cifNumber = parameters.get("partnerCif");
    }
    else
    {
      List<Variable> variables = caseService.getVariables(caseInstanceId);
      for (Variable variable : variables)
      {
        String id = variable.getId().getId();

        if (id.equalsIgnoreCase(PROCESS_REQUEST_ID))
        {
          processRequestId = (String) variable.getValue();
        }

        else if (id.equalsIgnoreCase(PROCESS_TYPE_ID))
        {
          processTypeId = (String) variable.getValue();
        }

        else if (id.equalsIgnoreCase(CIF_NUMBER))
        {
          cifNumber = (String) variable.getValue();
        }

        else if (id.equalsIgnoreCase(FULL_NAME))
        {
          fullName = (String) variable.getValue();
        }

        else if (id.equalsIgnoreCase(REGISTER_NUMBER))
        {
          registerNumber = (String) variable.getValue();
        }
      }
    }

    validateParameters(processRequestId, processTypeId, cifNumber, registerNumber, fullName);

    String uploadDocumentContent = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n"
        + "   <soapenv:Header/>\n"
        + "   <soapenv:Body>\n"
        + "      <tem:UploadFile>\n"
        + OPTIONAL
        + "         <tem:accessCode>" + accessCode + "</tem:accessCode>\n"
        + OPTIONAL
        + "         <tem:applicationID>" + processRequestId + "</tem:applicationID>\n"
        + OPTIONAL
        + "         <tem:accountNumber></tem:accountNumber>\n"
        + OPTIONAL
        + "         <tem:documentReference>" + documentId + "</tem:documentReference>\n"
        + OPTIONAL
        + "         <tem:applicationType>" + APPLICATION_TYPE + "</tem:applicationType>\n"
        + OPTIONAL
        + "         <tem:applicationCategory>" + processTypeId + "</tem:applicationCategory>\n"
        + OPTIONAL
        + "         <tem:productCode>" + PRODUCT_CODE + "</tem:productCode>\n"
        + OPTIONAL
        + "         <tem:productDescription>" + PRODUCT_DESCRIPTION + "</tem:productDescription>\n"
        + OPTIONAL
        + "         <tem:CIF>" + cifNumber + "</tem:CIF>\n"
        + OPTIONAL
        + "         <tem:customerName>" + fullName + "</tem:customerName>\n"
        + OPTIONAL
        + "         <tem:registrationNumber>" + registerNumber + "</tem:registrationNumber>\n"
        + OPTIONAL
        + "         <tem:coApplicants>?</tem:coApplicants>\n"
        + OPTIONAL
        + "         <tem:loanBranch>" + groupId + "</tem:loanBranch>\n"
        + OPTIONAL
        + "         <tem:uploadingOfficer>" + currentUserId + "</tem:uploadingOfficer>\n"
        + OPTIONAL
        + "         <tem:stage>" + STAGE + "</tem:stage>\n"
        + OPTIONAL
        + "         <tem:segment>" + SEGMENT + "</tem:segment>\n"
        + OPTIONAL
        + "         <tem:documentexpirationDate>?</tem:documentexpirationDate>\n"
        + OPTIONAL
        + "         <tem:documentCategory>" + mainType + "</tem:documentCategory>\n"
        + OPTIONAL
        + "         <tem:documentType>" + subType + "</tem:documentType>\n"
        + OPTIONAL
        + "         <tem:uploadDate>?</tem:uploadDate>\n"
        + OPTIONAL
        + "         <tem:documentName>" + fileName + "</tem:documentName>\n"
        + OPTIONAL
        + "         <tem:documentbase64String>" + documentAsBase64 + "</tem:documentbase64String>\n"
        + OPTIONAL
        + "         <tem:remarks></tem:remarks>\n"
        + OPTIONAL
        + "         <tem:vehicleVINNumber></tem:vehicleVINNumber>\n"
        + OPTIONAL
        + "         <tem:vehicleNumber></tem:vehicleNumber>\n"
        + OPTIONAL
        + "         <tem:gerchilgeeDugaar></tem:gerchilgeeDugaar>\n"
        + OPTIONAL
        + "         <tem:ulsiinburtgeliinDugaar></tem:ulsiinburtgeliinDugaar>\n"
        + "      </tem:UploadFile>\n"
        + "   </soapenv:Body>\n"
        + "</soapenv:Envelope>\n";

    try (Response uploadResponse = xacHttpClient.postTextXml(uploadDocumentContent))
    {
      if (uploadResponse.isSuccessful())
      {
        LOG.info("########## Response SUCCESSFUL during upload file =[{}]", fileName);

        ResponseBody uploadResponseBody = uploadResponse.body();

        String responseString = xacHttpClient.read(uploadResponseBody);

        String result = getUploadResult(responseString);

        LOG.info("########## Upload RESULT = [{}]", result);
        if (null == result || !result.equalsIgnoreCase(OK))
        {
          String errorCode = "DMS018";
          throw new BpmServiceException(errorCode, "##### Could not upload document = [" + fileName + "] with REQUEST_ID =" + processRequestId);
        }
        return result;
      }
    }

    return null;
  }

  @Override
  public String getDocumentReference(String documentId, String userId) throws BpmServiceException
  {
    String endPoint = getDmsEndPoint(environment);
    String function = getFunction(environment);

    String accessCode = getAccessCode(environment);
    String documentSoapAction = getDocumentSoapAction(environment);

    XacHttpClient clientGetDocument = new XacHttpClient
        .Builder(endPoint, TEXT_XML, documentSoapAction)
        .headerFunction(function)
        .build();

    String getDocumentContent = getDocumentRequestBody(accessCode, documentId, userId);

    try (Response getDocumentResponse = clientGetDocument.postTextXml(getDocumentContent))
    {
      if (getDocumentResponse.isSuccessful())
      {
        ResponseBody responseBody = getDocumentResponse.body();

        String responseString = clientGetDocument.read(responseBody);

        String documentReference = getDocRefResult(responseString);

        if (null == documentReference)
        {
          String errorCode = "DMS002";
          throw new BpmServiceException(errorCode, "Error occurred when get document reference from DMS!");
        }

        return documentReference;
      }
    }
    return null;
  }

  @Override
  public String downloadContractAsBase64(String accountNumber, String paymentType, String productId) throws BpmServiceException
  {
    // Gets endpoint.
    String endPoint = getPropertyByKey(environment, BI_PUBLISHER_ENDPOINT);

    // Gets download contract function.
    String function = getPropertyByKey(environment, PUBLISHER_FUNCTION_DOWNLOAD_CONTRACT);

    // Gets download contract SOAP action.
    String soapAction = StringUtils.EMPTY;

    // Gets contract file format.
    String fileFormat = getPropertyByKey(environment, PUBLISHER_CONTRACT_FILE_FORMAT);

    // Gets contract absolute path.
    String absolutePath = getFunctionByProduct(environment, productId);

    // Gets data size for chunk download.
    String dataSizeForChunkDownload = getPropertyByKey(environment, PUBLISHER_DATA_SIZE_FOR_CHUNK_DOWNLOAD);

    // Gets publisher user id.
    String userId = getPropertyByKey(environment, PUBLISHER_USER_ID);

    // Gets publisher user password.
    String userPassword = getPropertyByKey(environment, PUBLISHER_USER_PASSWORD);

    verifyPublisherParams(endPoint, function);
    verifyPublisherRequestBodyParams(fileFormat, absolutePath, dataSizeForChunkDownload, userId, userPassword);

    XacHttpClient xacHttpClient = new XacHttpClient
        .Builder(endPoint, TEXT_XML)
        .headerSoap(soapAction)
        .headerFunction(function)
        .build();

    String requestBody = getRequestBodyDownloadContract(absolutePath, fileFormat, accountNumber,
        paymentType, userId, userPassword, dataSizeForChunkDownload);

    LOG.info("########### Downloads loan contract from BI PUBLISHER ... \n REQUEST ENDPOINT [{}], SOAP [{}], FUNCTION [{}], \n REQUEST BODY = [{}]", endPoint,
        soapAction, function, requestBody);
    try (Response getDocumentResponse = xacHttpClient.postTextXml(requestBody))
    {
      if (getDocumentResponse.isSuccessful())
      {
        LOG.info("######## PUBLISHER Response is successful.");
        ResponseBody responseBody = getDocumentResponse.body();

        if (null == responseBody)
        {
          throw new BpmServiceException(PUBLISHER_RESPONSE_BODY_NULL_ERR_CODE, "##### Download contract response body is null from PUBLISHER!");
        }
        String responseString = xacHttpClient.read(responseBody);

        LOG.info("########### LOAN CONTRACT RESPONSE: [{}]", responseString);
        String contractAsBase64 = extractBase64FromResponse(responseString);

        if (null == contractAsBase64)
        {
          throw new BpmServiceException(PUBLISHER_CONTRACT_BASE_64_NULL_ERR_CODE, "##### Contract as base64 value is null from PUBLISHER!");
        }
        return contractAsBase64;
      }
    }
    return null;
  }

  @Override
  public String downloadOnlineSalaryContractAsBase64(String accountNumber, String requestId) throws BpmServiceException
  {
    // Gets endpoint.
    String endPoint = getPropertyByKey(environment, BI_PUBLISHER_ENDPOINT);

    // Gets download contract function.
    String function = getPropertyByKey(environment, PUBLISHER_FUNCTION_DOWNLOAD_CONTRACT);

    // Gets download contract SOAP action.
    String soapAction = StringUtils.EMPTY;

    // Gets contract file format.
    String fileFormat = getPropertyByKey(environment, PUBLISHER_CONTRACT_FILE_FORMAT);

    // Gets contract absolute path.
    String absolutePath = getPropertyByKey(environment, PUBLISHER_REPORT_ABSOLUTE_PATH);

    // Gets data size for chunk download.
    String dataSizeForChunkDownload = getPropertyByKey(environment, PUBLISHER_DATA_SIZE_FOR_CHUNK_DOWNLOAD);

    // Gets publisher user id.
    String userId = getPropertyByKey(environment, PUBLISHER_USER_ID);

    // Gets publisher user password.
    String userPassword = getPropertyByKey(environment, PUBLISHER_USER_PASSWORD);

    verifyPublisherParams(endPoint, function);
    verifyPublisherRequestBodyParams(fileFormat, absolutePath, dataSizeForChunkDownload, userId, userPassword);

    XacHttpClient xacHttpClient = new XacHttpClient
        .Builder(endPoint, TEXT_XML)
        .headerSoap(soapAction)
        .headerFunction(function)
        .build();

    String requestBody = getRequestBodyDownloadContract(absolutePath, fileFormat, accountNumber,
        requestId, userId, userPassword, dataSizeForChunkDownload);

    LOG.info("########### Downloads ONLINE SALARY loan contract from BI PUBLISHER with REQUEST ID = [{}] \n REQUEST BODY = [{}]", requestId, requestBody);
    try (Response getDocumentResponse = xacHttpClient.postTextXml(requestBody))
    {
      if (getDocumentResponse.isSuccessful())
      {
        LOG.info("######## PUBLISHER ONLINE SALARY CONTRACT Response is successful with REQUEST ID = [{}]", requestId);
        ResponseBody responseBody = getDocumentResponse.body();

        if (null == responseBody)
        {
          String errorCode = PUBLISHER_RESPONSE_BODY_NULL_ERR_CODE;
          throw new BpmServiceException(errorCode, "##### Download contract response body is null from PUBLISHER!");
        }
        String responseString = xacHttpClient.read(responseBody);

        LOG.info("########### ONLINE SALARY LOAN CONTRACT RESPONSE: [{}]", responseString);
        String contractAsBase64 = extractBase64FromResponse(responseString);

        if (null == contractAsBase64)
        {
          String errorCode = PUBLISHER_CONTRACT_BASE_64_NULL_ERR_CODE;
          throw new BpmServiceException(errorCode, "##### Contract as base64 value is null from PUBLISHER!");
        }
        return contractAsBase64;
      }
    }
    return null;
  }

  @Override
  public String downloadLoanReportAsBase64(String accountNumber) throws BpmServiceException
  {
    // Gets endpoint.
    String endPoint = getPropertyByKey(environment, BI_PUBLISHER_ENDPOINT);

    // Gets download contract function.
    String function = getPropertyByKey(environment, PUBLISHER_FUNCTION_DOWNLOAD_CONTRACT);

    // Gets download contract SOAP action.
    String soapAction = "";

    // Gets contract file format.
    String fileFormat = getPropertyByKey(environment, PUBLISHER_CONTRACT_FILE_FORMAT);

    // Gets contract absolute path.
    String absolutePath = getPropertyByKey(environment, PUBLISHER_PATH_LOAN_REPORT_CREATION);

    // Gets data size for chunk download.
    String dataSizeForChunkDownload = getPropertyByKey(environment, PUBLISHER_DATA_SIZE_FOR_CHUNK_DOWNLOAD);

    // Gets publisher user id.
    String userId = getPropertyByKey(environment, PUBLISHER_USER_ID);

    // Gets publisher user password.
    String userPassword = getPropertyByKey(environment, PUBLISHER_USER_PASSWORD);

    verifyPublisherParams(endPoint, function);
    verifyPublisherRequestBodyParams(fileFormat, absolutePath, dataSizeForChunkDownload, userId, userPassword);

    XacHttpClient xacHttpClient = new XacHttpClient
        .Builder(endPoint, TEXT_XML)
        .headerFunction(function)
        .headerSoap(soapAction)
        .build();

    String requestBody = getRequestBodyDownloadReport(absolutePath, fileFormat, accountNumber,
        userId, userPassword, dataSizeForChunkDownload);

    LOG.info("########### Downloads loan report from BI PUBLISHER ... \n REQUEST BODY = [{}]", requestBody);
    try (Response getDocumentResponse = xacHttpClient.postTextXml(requestBody))
    {
      if (getDocumentResponse.isSuccessful())
      {
        LOG.info("######## PUBLISHER Response is successful.");
        ResponseBody responseBody = getDocumentResponse.body();

        if (null == responseBody)
        {
          String errorCode = PUBLISHER_CONTRACT_BASE_64_NULL_ERR_CODE;
          throw new BpmServiceException(errorCode, "##### Download report response body is null from PUBLISHER!");
        }
        String responseString = xacHttpClient.read(responseBody);

        LOG.info("########### LOAN REPORT RESPONSE: [{}]", responseString);

        String contractAsBase64 = extractBase64FromResponse(responseString);

        if (null == contractAsBase64)
        {
          String errorCode = "DMS024";
          throw new BpmServiceException(errorCode, "##### Report as base64 value is null from PUBLISHER!");
        }
        return contractAsBase64;
      }
    }
    return null;
  }

  @Override
  public String downloadPaymentScheduleAsBase64(String accountNumber, String repaymentType, Map<String, String> paymentScheduleInfo, String processType)
      throws BpmServiceException
  {
    if (StringUtils.isBlank(repaymentType))
    {
      throw new BpmServiceException("Repayment type is null or empty!");
    }
    if (StringUtils.isBlank(accountNumber))
    {
      throw new BpmServiceException("Account number is null or empty!");
    }
    if (paymentScheduleInfo == null || paymentScheduleInfo.isEmpty())
    {
      throw new BpmServiceException("Payment schedule info is null or empty!");
    }

    // Gets endpoint.
    String endPoint = getPropertyByKey(environment, BI_PUBLISHER_ENDPOINT);

    // Gets download contract function.
    String function = getPropertyByKey(environment, PUBLISHER_FUNCTION_DOWNLOAD_CONTRACT);

    // Gets download contract SOAP action.
    String soapAction = "";

    // Gets contract file format.
    String fileFormat = getPropertyByKey(environment, PUBLISHER_CONTRACT_FILE_FORMAT);

    // Gets contract absolute path.
    String absolutePath;
    if (processType != null && (processType.equals(ONLINE_SALARY_PROCESS_TYPE) || processType.equals(BNPL_PROCESS_TYPE_ID) || processType.equals(INSTANT_LOAN_PROCESS_TYPE_ID) || processType.equals(ONLINE_LEASING_PROCESS_TYPE_ID)))
    {
      absolutePath =
          getPropertyByKey(environment, PUBLISHER_REPORT_ONLINE_SALARY_ABSOLUTE_PATH) + "/" + getPropertyByKey(environment, BI_PUBLISHER_XRRSCHDL) + ".xdo";
    }
    else
    {
      absolutePath = getPropertyByKey(environment, PUBLISHER_REPORT_ABSOLUTE_PATH_LOAN_PAYMENT_SCHEDULE);
    }
    // Gets data size for chunk download.
    String dataSizeForChunkDownload = getPropertyByKey(environment, PUBLISHER_DATA_SIZE_FOR_CHUNK_DOWNLOAD);

    // Gets publisher user id.
    String userId = getPropertyByKey(environment, PUBLISHER_USER_ID);

    // Gets publisher user password.
    String userPassword = getPropertyByKey(environment, PUBLISHER_USER_PASSWORD);

    verifyPublisherParams(endPoint, function);
    verifyPublisherRequestBodyParams(fileFormat, absolutePath, dataSizeForChunkDownload, userId, userPassword);

    XacHttpClient xacHttpClient = new XacHttpClient
        .Builder(endPoint, TEXT_XML)
        .headerFunction(function)
        .headerSoap(soapAction)
        .build();

    String requestBody;

    String grace = paymentScheduleInfo.get("P_GRACE");

    if (StringUtils.isBlank(grace) && repaymentType.equals(EQUAL_PRINCIPLE_PAYMENT_VALUE))
    {
      throw new BpmServiceException("Payment Schedule Information for equal principle payment is missing!");
    }
    if (StringUtils.isBlank(absolutePath))
    {
      throw new BpmServiceException("BI Publisher Absolute Path for equal principle payment is missing!");
    }
    requestBody = getRequestBodyDownloadPaymentSchedule(absolutePath, fileFormat, accountNumber, grace, userId, userPassword, dataSizeForChunkDownload);

    LOG.info("########### Downloads loan payment schedule from BI PUBLISHER ... \n REQUEST BODY = [{}]", requestBody);
    try (Response getDocumentResponse = xacHttpClient.postTextXml(requestBody))
    {
      if (getDocumentResponse.isSuccessful())
      {
        LOG.info("######## PUBLISHER Response is successful with account number [{}]", accountNumber);
        ResponseBody responseBody = getDocumentResponse.body();

        if (null == responseBody)
        {
          String errorCode = PUBLISHER_RESPONSE_BODY_NULL_ERR_CODE;
          throw new BpmServiceException(errorCode, "##### Download payment response body is null from PUBLISHER!");
        }
        String responseString = xacHttpClient.read(responseBody);

        String contractAsBase64 = extractBase64FromResponse(responseString);

        if (null == contractAsBase64 || StringUtils.isBlank(contractAsBase64))
        {
          String errorCode = PUBLISHER_CONTRACT_BASE_64_NULL_ERR_CODE;
          throw new BpmServiceException(errorCode, "##### Payment schedule as base64 value is null from PUBLISHER!");
        }
        return contractAsBase64;
      }
    }
    return null;
  }

  @Override
  public String downloadDocumentByType(Map<String, String> documentParam, String documentType, String instanceId) throws BpmServiceException
  {
    Map<String, String> documentParam2 = new HashMap<>();
    if (documentType.contains("bnpl") || documentType.contains(SALARY_ORG_CONTRACT) || documentType.contains(LEASING_ORG_CONTRACT) || documentType.contains(
        "instant"))
    {
      documentParam2.put(P_ACC, documentParam.get(P_ACC));
    }
    if (documentType.contains(ONLINE_LEASING_REPORT)) {
      documentParam2.put(P_ACC, documentParam.get(P_FORACID));
      documentParam2.put(P_CONTRACTN, documentParam.get(P_CONTRACTN));
    }
    if (documentType.contains(ONLINE_LEASING_CONTRACT)) {
      documentParam2.put(P_FORACID, documentParam.get(P_FORACID));
      documentParam2.put(P_CONTRACTN, documentParam.get(P_CONTRACTN));
    }
    String endPoint = getPropertyByKey(environment, BI_PUBLISHER_ENDPOINT);
    String soapAction = getPropertyByKey(environment, BI_PUBLISHER_SOAP_ACTION_DOWNLOAD_CONTRACT);
    String publisherUserId = getPropertyByKey(environment, PUBLISHER_USER_ID);
    String publisherPassword = getPropertyByKey(environment, PUBLISHER_USER_PASSWORD);
    String productCode = documentParam.get(BpmModuleConstants.PRODUCT_CODE);
    BiPath biPath = getBiPath(documentType, documentType, productCode);
    String reportAbsolutePathDocType = biPath.getFolder();
    if (StringUtils.isBlank(reportAbsolutePathDocType)) {
      reportAbsolutePathDocType = getPropertyByKey(environment, getAbsolutePathDocType(documentType));
    }
    String reportAbsolutePath = biPath.getPath();
    if (StringUtils.isBlank(reportAbsolutePath)) {
      reportAbsolutePath = getPropertyByKey(environment, getAbsolutePathConstantByDocumentType(documentType));
    }
    String requestBody = getDocumentRequestBody(documentParam2.isEmpty() ? documentParam : documentParam2, reportAbsolutePathDocType, reportAbsolutePath,
        publisherUserId, publisherPassword);
    String function = getPropertyByKey(environment, PUBLISHER_FUNCTION_DOWNLOAD_CONTRACT);
    String requestId = null;
    if (documentParam.containsKey("requestId"))
    {
      requestId = documentParam.get("requestId");
    }
    else if (documentType.contains(SALARY_ORG_CONTRACT) || documentType.contains(LEASING_ORG_CONTRACT) || documentType.contains("instant"))
    {
      requestId = documentParam.get(P_ACC);
    }
    else if (documentType.contains(ONLINE_LEASING_CONTRACT) || documentType.contains(ONLINE_LEASING_REPORT))
    {
      requestId = documentParam.get(P_FORACID);
    }
    else
    {
      requestId = getValidString(caseService.getVariableById(instanceId, PROCESS_REQUEST_ID));
    }
    LOG.info("#### HEADER PARAMETERS: FUNCTION = [{}], REPORT ABSOLUTE PATH = [{}]  REPORT ABSOLUTE PATH DOCTYPE = [{}] with REQUEST ID = [{}]", function,
        reportAbsolutePath,
        reportAbsolutePathDocType, requestId);
    XacHttpClient xacHttpClient = getXacClient(documentType, soapAction);
    LOG.info("### BIP XacHttpClient = [{}]", xacHttpClient);
    LOG.info("#### REQUEST BODY= [{}] TO DOWNLOAD BIP DOCUMENT WITH INSTANCE ID = [{}], REQUEST ID = [{}]", requestBody, instanceId, requestId);
    setLogMessage(documentType, instanceId, requestId, endPoint, documentType.equals(XacHttpConstants.ONLINE_LOAN_CONTRACT) ? "" : soapAction,
        reportAbsolutePathDocType, reportAbsolutePath, function);
    try (Response getDocumentResponse = xacHttpClient.postTextXml(requestBody, function))
    {
      if (getDocumentResponse.isSuccessful())
      {
        ResponseBody responseBody = getDocumentResponse.body();
        if (null == responseBody)
        {
          LOG.error(
              "########### RESPONSE BODY IS NULL WHEN DOWNLOAD [{}] document from BI PUBLISHER ... \n WITH PROCESS INSTANCE ID = [{}], REQUEST ID = [{}] \n HEADER PARAM = [{}]",
              documentType, instanceId, requestId, xacHttpClient.headerParam());
          throwException(documentType, instanceId, xacHttpClient.headerParam());
        }
        String responseString = xacHttpClient.read(Objects.requireNonNull(responseBody));

        String contractAsBase64 = StringUtils.substringBetween(responseString, "<reportBytes>", "</reportBytes>");

        if (null == contractAsBase64 || StringUtils.isBlank(contractAsBase64))
        {
          LOG.error(
              "########### RESPONSE DOCUMENT BASE64 IS NULL OR BLANK WHEN DOWNLOAD [{}] document from BI PUBLISHER ... \n WITH PROCESS INSTANCE ID = [{}], REQUEST ID = [{}] \n HEADER PARAM = [{}]",
              documentType, instanceId, requestId, xacHttpClient.headerParam());
          throwException(documentType, instanceId, xacHttpClient.headerParam());
        }
        return contractAsBase64;
      }
      throwException(documentType, instanceId, xacHttpClient.headerParam());
      return null;
    }
  }
  @Override
  public String downloadPurchaseSaleContractAsBase64( Map<String, String> documentParameter, String documentType, String instanceId ) throws BpmServiceException
  {
    String endPoint = getPropertyByKey(environment, BI_PUBLISHER_ENDPOINT);
    String soapAction = getPropertyByKey(environment, BI_PUBLISHER_SOAP_ACTION_DOWNLOAD_CONTRACT);
    String publisherUserId = getPropertyByKey(environment, PUBLISHER_USER_ID);
    String publisherPassword = getPropertyByKey(environment, PUBLISHER_USER_PASSWORD);
    String productCode = documentParameter.get(BpmModuleConstants.PRODUCT_CODE);
    BiPath biPath = getBiPath(documentType, documentType, productCode);
    String reportAbsolutePathDocType = biPath.getFolder();
    if (StringUtils.isBlank(reportAbsolutePathDocType)) {
      reportAbsolutePathDocType = getPropertyByKey(environment, getAbsolutePathDocType(documentType));
    }
    String reportAbsolutePath = biPath.getPath();
    if (StringUtils.isBlank(reportAbsolutePath)) {
      reportAbsolutePath = getPropertyByKey(environment, getAbsolutePathConstantByDocumentType(documentType));
    }
    String requestBody = getDocumentRequestBody(documentParameter, reportAbsolutePathDocType, reportAbsolutePath,
        publisherUserId, publisherPassword);
    String function = getPropertyByKey(environment, PUBLISHER_FUNCTION_DOWNLOAD_CONTRACT);
    String requestId = documentParameter.get(P_ACC);
  
    XacHttpClient xacHttpClient = getXacClient(documentType, soapAction);
    LOG.info("### BIP XacHttpClient = [{}]", xacHttpClient);
    LOG.info("#### REQUEST BODY= [{}] TO DOWNLOAD BIP DOCUMENT WITH INSTANCE ID = [{}]", requestBody, instanceId);
    setLogMessage(documentType, instanceId, requestId, endPoint, documentType.equals(PURCHASE_TRADE_CONTRACT) ? "" : soapAction,
        reportAbsolutePathDocType, reportAbsolutePath, function);
    try (Response getDocumentResponse = xacHttpClient.postTextXml(requestBody, function))
    {
      if (getDocumentResponse.isSuccessful())
      {
        ResponseBody responseBody = getDocumentResponse.body();
        if (null == responseBody)
        {
          LOG.error(
              "########### RESPONSE BODY IS NULL WHEN DOWNLOAD [{}] document from BI PUBLISHER ... \n WITH PROCESS INSTANCE ID = [{}], REQUEST ID = [{}] \n HEADER PARAM = [{}]",
              documentType, instanceId, requestId, xacHttpClient.headerParam());
          throwException(documentType, instanceId, xacHttpClient.headerParam());
        }
        String responseString = xacHttpClient.read(Objects.requireNonNull(responseBody));

        String contractAsBase64 = StringUtils.substringBetween(responseString, "<reportBytes>", "</reportBytes>");

        if (null == contractAsBase64 || StringUtils.isBlank(contractAsBase64))
        {
          LOG.error(
              "########### RESPONSE DOCUMENT BASE64 IS NULL OR BLANK WHEN DOWNLOAD [{}] document from BI PUBLISHER ... \n WITH PROCESS INSTANCE ID = [{}], REQUEST ID = [{}] \n HEADER PARAM = [{}]",
              documentType, instanceId, requestId, xacHttpClient.headerParam());
          throwException(documentType, instanceId, xacHttpClient.headerParam());
        }
        return contractAsBase64;
      }
      throwException(documentType, instanceId, xacHttpClient.headerParam());
      return null;
    }
  }
  private void verifyPublisherParams(String endPoint, String function) throws BpmServiceException
  {
    if (StringUtils.isBlank(endPoint) || StringUtils.isBlank(function))
    {
      String errorCode = "DMS003";
      throw new BpmServiceException(errorCode, "####### Publisher service ENDPOINT, FUNCTION or SOAP ACTION is blank, Please verify configuration file!");
    }

    LOG.info("######### Publisher download ENDPOINT = [{}], FUNCTION = [{}]", endPoint, function);
  }

  private void verifyPublisherParams(String endPoint, String function, String soapAction) throws BpmServiceException
  {
    if (StringUtils.isBlank(endPoint) || StringUtils.isBlank(function) || StringUtils.isBlank(soapAction))
    {
      String errorCode = "DMS003";
      throw new BpmServiceException(errorCode, "####### Publisher service ENDPOINT, FUNCTION or SOAP ACTION is blank, Please verify configuration file!");
    }

    LOG.info("######### Publisher download ENDPOINT = [{}], FUNCTION = [{}]", endPoint, function);
  }

  private void verifyPublisherRequestBodyParams(String fileFormat, String absolutePath, String dataSizeForChunkDownload, String userId, String userPassword)
      throws BpmServiceException
  {
    if (StringUtils.isBlank(fileFormat))
    {
      String errorCode = "DMS004";
      throw new BpmServiceException(errorCode, "###### FILE FORMAT is blank, Please verify configuration file!");
    }

    if (StringUtils.isBlank(absolutePath))
    {
      String errorCode = "DMS005";
      throw new BpmServiceException(errorCode, "###### ABSOLUTE PATH file format is blank, Please verify configuration file!");
    }

    if (StringUtils.isBlank(dataSizeForChunkDownload))
    {
      String errorCode = "DMS006";
      throw new BpmServiceException(errorCode, "###### DATA SIZE OF CHUNK DOWNLOAD is blank, Please verify configuration file!");
    }

    if (StringUtils.isBlank(userId))
    {
      String errorCode = "DMS007";
      throw new BpmServiceException(errorCode, "###### USER ID is blank, Please verify configuration file!");
    }

    if (StringUtils.isBlank(userPassword))
    {
      String errorCode = "DMS008";
      throw new BpmServiceException(errorCode, "###### USER PASSWORD is blank, Please verify configuration file!");
    }

    LOG.info("######## PUBLISHER download FILE FORMAT = [{}], ABSOLUTE PATH = [{}],"
            + " DATA SIZE FOR CHUNK DOWNLOAD = [{}], USER ID = [{}]",
        fileFormat,
        absolutePath,
        dataSizeForChunkDownload,
        userId);
  }

  private XacHttpClient getUploadDocumentClient()
  {
    String endPoint = getDmsEndPoint(environment);
    String uploadAction = getUploadSoapAction(environment);
    String function = getFunction(environment);

    LOG.info("########### UPLOADS DOCUMENT TO LDMS : EndPoint = [{}]", endPoint);
    LOG.info("########### UPLOADS DOCUMENT TO LDMS : UploadAction = [{}]", uploadAction);
    LOG.info("########### UPLOADS DOCUMENT TO LDMS : Function = [{}]", function);

    return new XacHttpClient
        .Builder(endPoint, TEXT_XML, uploadAction)
        .headerFunction(function)
        .build();
  }

  private String extractBase64FromResponse(String responseXml)
  {
    if (StringUtils.isBlank(responseXml))
    {
      LOG.info("############ RESPONSE XML IS BLANK!!!");
      return StringUtils.EMPTY;
    }
    return StringUtils.substringBetween(responseXml, "<reportBytes>", "</reportBytes>");
  }

  private static String getAbsolutePathDocType(String type) throws BpmServiceException
  {
    if (StringUtils.equals(type, SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT) || StringUtils.equals(type, SME_LOAN_CONTRACT) ||
        StringUtils.equals(type, COLLATERAL_CO_OWNER_CONSENT) || StringUtils.equals(type, NEXT_COLLATERAL_LOAN_CONTRACT) ||
        StringUtils.equals(type, ATTACHMENT_COLLATERAL_LOAN_CONTRACT) || StringUtils.equals(type, ATTACHMENT_MORTGAGE_LOAN_CONTRACT) ||
        StringUtils.equals(type, ATTACHMENT_WARRANTY_LOAN_CONTRACT) || StringUtils.equals(type, LOAN_REPAYMENT_BEFORE) ||
        StringUtils.equals(type, ATTACHMENT_FIDUC_LOAN_CONTRACT) || StringUtils.equals(type, CONSUMPTION_LOAN_CONTRACT) ||
        StringUtils.equals(type, FIDUCIARY_PROPERTY_CONTRACT) || StringUtils.equals(type, LOAN_REPORT_PAGE) ||
        StringUtils.equals(type, COLLATERAL_ASSETS_LIST_MORTGAGE) || StringUtils.equals(type, MOVABLE_ASSETS_FIDUCIARY) ||
        StringUtils.equals(type, EQUIPMENT_ASSETS) || StringUtils.equals(type, CREDIT_LINE_LOAN_CONTRACT) ||
        StringUtils.equals(type, LOAN_REPAYMENT_AFTER) || StringUtils.equals(type, EMPLOYEE_LOAN_CONTRACT) || StringUtils.equals(type, MORTGAGE_TYPE_LOAN) ||
        StringUtils.equals(type, MORTGAGE_TYPE_GOVERNMENT) || StringUtils.equals(type, MORTGAGE_LOAN_CONTRACT) ||
        StringUtils.equals(type, EMPLOYEE_MORTGAGE_LOAN_CONTRACT) || StringUtils.equals(type, DIRECT_PRINTING_CONTRACT) ||
        StringUtils.equals(type, LOAN_PERMISSION) || StringUtils.equals(type, DISBURSEMENT_PERMISSION) || StringUtils.equals(type, BNPL_REPORT) ||
        StringUtils.equals(type, INSTANT_LOAN_CONTRACT) || StringUtils.equals(type, INSTANT_LOAN_REPORT))
    {
      return BI_ABSOLUTE_PATH;
    }

    if (type.equals(ONLINE_LOAN_CONTRACT))
    {
      return BI_ONLINE_LOAN_ABSOLUTE_PATH;
    }

    if (type.equals(BNPL_CONTRACT))
    {
      return BI_PUBLISHER_BNPL_ABSOLUTE_PATH;
    }
    if (type.equals(SALARY_ORG_CONTRACT) || type.equals(LEASING_ORG_CONTRACT))
    {
      return BI_PUBLISHER_ORGANIZATION_ABSOLUTE_PATH;
    }
    if (type.equals(ONLINE_LEASING_CONTRACT) || type.equals(ONLINE_LEASING_REPORT))
    {
      return  BI_PUBLISHER_ONLINE_LEASING_ABSOLUTE_PATH;
    }
    if (type.equals(GENERAL_TRADE_CONTRACT) || (type.equals(REPURCHASE_TRADE_CONTRACT)))
    {
      return BI_PUBLISHER_PURCHASE_TRADE_CONTRACT;
    }
    throw new BpmServiceException(BIP_ABSOLUTE_PATH_IS_INVALID_CODE, BIP_ABSOLUTE_PATH_IS_INVALID_MESSAGE);
  }

  private static String getAbsolutePathConstantByDocumentType(String type) throws BpmServiceException
  {
    if (null == type)
    {
      throw new BpmServiceException(DOCUMENT_TYPE_IS_VALID_CODE, DOCUMENT_TYPE_IS_VALID_MESSAGE);
    }

    switch (type)
    {
    case ONLINE_LOAN_CONTRACT:
      return BI_PUBLISHER_ONLINE_LOAN_CONTRACT_XDO;
    case SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT:
      return BI_PUBLISHER_XBRMSL;
    case SME_LOAN_CONTRACT:
      return BI_PUBLISHER_XBRSMEE;
    case COLLATERAL_CO_OWNER_CONSENT:
      return BI_PUBLISHER_XBR2009NEW;
    case NEXT_COLLATERAL_LOAN_CONTRACT:
      return BI_PUBLISHER_XBR2011;
    case ATTACHMENT_WARRANTY_LOAN_CONTRACT:
      return BI_PUBLISHER_XBRBDG;
    case ATTACHMENT_MORTGAGE_LOAN_CONTRACT:
      return BI_PUBLISHER_XBRCNIPO;
    case ATTACHMENT_FIDUC_LOAN_CONTRACT:
    case FIDUCIARY_PROPERTY_CONTRACT:
      return BI_PUBLISHER_XBRFID;
    case ATTACHMENT_COLLATERAL_LOAN_CONTRACT:
      return BI_PUBLISHER_XBRLCC;
    case LOAN_REPAYMENT_BEFORE:
      return BI_PUBLISHER_XBRBZET;
    case CONSUMPTION_LOAN_CONTRACT:
      return BI_PUBLISHER_XBRCOLA;
    case LOAN_REPORT_PAGE:
      return BI_PUBLISHER_XBRLIP;
    case COLLATERAL_ASSETS_LIST_MORTGAGE:
      return BI_PUBLISHER_XBR2008A;
    case MOVABLE_ASSETS_FIDUCIARY:
      return BI_PUBLISHER_XBR2008B;
    case EQUIPMENT_ASSETS:
      return BI_PUBLISHER_XBR2008C;
    case CREDIT_LINE_LOAN_CONTRACT:
      return BI_PUBLISHER_XBRCRLA;
    case EMPLOYEE_LOAN_CONTRACT:
      return BI_PUBLISHER_XBRESL;
    case LOAN_REPAYMENT_AFTER:
      return BI_PUBLISHER_XRRSCHDL;
    case MORTGAGE_TYPE_GOVERNMENT:
      return BI_PUBLISHER_XBRGMLC;
    case MORTGAGE_TYPE_LOAN:
      return BI_PUBLISHER_XBRMLC;
    case DIRECT_PRINTING_CONTRACT:
      return BI_PUBLISHER_XBZ_CONT;
    case EMPLOYEE_MORTGAGE_LOAN_CONTRACT:
      return BI_PUBLISHER_XBREMLC;
    case DISBURSEMENT_PERMISSION:
    case LOAN_PERMISSION:
      return BI_PUBLISHER_XBRZOZ;
    case BNPL_CONTRACT:
      return BI_PUBLISHER_BNPL_CONTRACT_XDO;
    case BNPL_REPORT:
    case INSTANT_LOAN_REPORT:
      return BI_PUBLISHER_XBRLIP;
    case SALARY_ORG_CONTRACT:
      return BI_PUBLISHER_XBSROSO;
    case LEASING_ORG_CONTRACT:
      return BI_PUBLISHER_XBSLSCT;
    case INSTANT_LOAN_CONTRACT:
      return BI_PUBLISHER_INSTANT_LOAN_CONTRACT_XDO;
    case ONLINE_LEASING_CONTRACT:
      return BI_PUBLISHER_ONLINE_LEASING_CONTRACT_XDO;
    case ONLINE_LEASING_REPORT:
      return BI_PUBLISHER_XBRLIP;
    case GENERAL_TRADE_CONTRACT:
      return BI_PUBLISHER_XBRXAC;
    case REPURCHASE_TRADE_CONTRACT:
      return BI_PUBLISHER_XBRXACC;
    default:
      throw new BpmServiceException(BIP_ABSOLUTE_PATH_CONSTANT_IS_INVALID_CODE, BIP_ABSOLUTE_PATH_CONSTANT_IS_INVALID_MESSAGE);
    }
  }

  private void throwException(String documentType, String instanceId, Map<String, String> headerParam) throws BpmServiceException
  {
    String errorCode = null;
    String errorMessage = null;
    LOG.info("#### HEADER PARAMETERS on EXCEPTION METHOD: [{}]", headerParam);

    switch (documentType)
    {
    case SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT_MESSAGE;
      break;
    case SME_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_SME_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_SME_LOAN_CONTRACT_MESSAGE;
      break;
    case COLLATERAL_CO_OWNER_CONSENT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_COLLATERAL_CO_OWNER_CONSENT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_COLLATERAL_CO_OWNER_CONSENT_MESSAGE;
      break;
    case NEXT_COLLATERAL_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_NEXT_COLLATERAL_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_NEXT_COLLATERAL_LOAN_CONTRACT_MESSAGE;
      break;
    case ATTACHMENT_COLLATERAL_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_COLLATERAL_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_COLLATERAL_CONTRACT_MESSAGE;
      break;
    case ATTACHMENT_MORTGAGE_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_MORTGAGE_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_MORTGAGE_CONTRACT_MESSAGE;
      break;
    case ATTACHMENT_WARRANTY_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_WARRANTY_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_WARRANTY_CONTRACT_MESSAGE;
      break;
    case LOAN_REPAYMENT_BEFORE:
      errorCode = BIP_FAILED_TO_DOWNLOAD_LOAN_REPAYMENT_BEFORE_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_LOAN_REPAYMENT_BEFORE_CODE_MESSAGE;
      break;
    case ATTACHMENT_FIDUC_LOAN_CONTRACT:
    case FIDUCIARY_PROPERTY_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_FIDUC_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_FIDUC_CONTRACT_MESSAGE;
      break;
    case CONSUMPTION_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_CONSUMPTION_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_CONSUMPTION_LOAN_CONTRACT_MESSAGE;
      break;
    case LOAN_REPORT_PAGE:
      errorCode = BIP_FAILED_TO_DOWNLOAD_LOAN_REPORT_PAGE_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_LOAN_REPORT_PAGE_CONTRACT_MESSAGE;
      break;
    case COLLATERAL_ASSETS_LIST_MORTGAGE:
      errorCode = BIP_FAILED_TO_DOWNLOAD_COLLATERAL_ASSETS_LIST_MORTGAGE_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_COLLATERAL_ASSETS_LIST_MORTGAGE_CONTRACT_MESSAGE;
      break;
    case MOVABLE_ASSETS_FIDUCIARY:
      errorCode = BIP_FAILED_TO_DOWNLOAD_MOVABLE_ASSETS_FIDUCIARY_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_MOVABLE_ASSETS_FIDUCIARY_CONTRACT_MESSAGE;
      break;
    case EQUIPMENT_ASSETS:
      errorCode = BIP_FAILED_TO_DOWNLOAD_EQUIPMENT_ASSETS_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_EQUIPMENT_ASSETS_CONTRACT_MESSAGE;
      break;
    case CREDIT_LINE_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_CREDIT_LINE_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_CREDIT_LINE_LOAN_CONTRACT_MESSAGE;
      break;
    case LOAN_REPAYMENT_AFTER:
      errorCode = BIP_FAILED_TO_DOWNLOAD_LOAN_REPAYMENT_AFTER_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_LOAN_REPAYMENT_AFTER_CODE_MESSAGE;
      break;
    case MORTGAGE_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_MORTGAGE_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_MORTGAGE_LOAN_CONTRACT_CODE_MESSAGE;
      break;
    case EMPLOYEE_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_EMPLOYEE_CONSUMPTION_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_EMPLOYEE_CONSUMPTION_LOAN_CONTRACT_MESSAGE;
      break;
    case DIRECT_PRINTING_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_DIRECT_PRINTING_DEPOSIT_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_DIRECT_PRINTING_DEPOSIT_CONTRACT_MESSAGE;
      break;
    case LOAN_PERMISSION:
      errorCode = BIP_FAILED_TO_DOWNLOAD_LOAN_PERMISSION_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_LOAN_PERMISSION_CONTRACT_MESSAGE;
      break;
    case DISBURSEMENT_PERMISSION:
      errorCode = BIP_FAILED_TO_DOWNLOAD_DISBURSEMENT_PERMISSION_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_DISBURSEMENT_PERMISSION_CONTRACT_MESSAGE;
      break;
    case EMPLOYEE_MORTGAGE_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_EMPLOYEE_MORTGAGE_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_EMPLOYEE_MORTGAGE_LOAN_CONTRACT_MESSAGE;
      break;
    case BNPL_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_BNPL_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_BNPL_LOAN_CONTRACT_MESSAGE;
      break;
    case BNPL_REPORT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_BNPL_LOAN_REPORT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_BNPL_LOAN_REPORT_MESSAGE;
      break;
    case SALARY_ORG_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_SALARY_ORG_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_SALARY_ORG_CONTRACT_MESSAGE;
      break;
    case LEASING_ORG_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_LEASING_ORG_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_LEASING_ORG_CONTRACT_MESSAGE;
      break;
    case INSTANT_LOAN_CONTRACT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_INSTANT_LOAN_CONTRACT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_INSTANT_LOAN_CONTRACT_MESSAGE;
      break;
    case INSTANT_LOAN_REPORT:
      errorCode = BIP_FAILED_TO_DOWNLOAD_INSTANT_LOAN_REPORT_CODE;
      errorMessage = BIP_FAILED_TO_DOWNLOAD_INSTANT_LOAN_REPORT_MESSAGE;
      break;
    default:
      break;
    }
    LOG.error("########### ERROR OCCURRED DURING DOWNLOAD [{}] document from BI PUBLISHER ... \n WITH PROCESS INSTANCE ID = [{}] \n HEADER PARAM = [{}]",
        documentType, instanceId, headerParam);
    throw new BpmServiceException(errorCode, errorMessage);
  }

  private void setLogMessage(String transactionType, String instanceId, String requestId, String endPoint, String soapAction, String reportAbsolutePathDocType,
      String reportAbsolutePath, String function)
  {
    String type = null;
    switch (transactionType)
    {
    case SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT:
      type = SMALL_AND_MEDIUM_ENTERPRISE_LOAN_CONTRACT;
      break;
    case SME_LOAN_CONTRACT:
      type = SME_LOAN_CONTRACT;
      break;
    case COLLATERAL_CO_OWNER_CONSENT:
      type = COLLATERAL_CO_OWNER_CONSENT;
      break;
    case NEXT_COLLATERAL_LOAN_CONTRACT:
      type = NEXT_COLLATERAL_LOAN_CONTRACT;
      break;
    case ATTACHMENT_COLLATERAL_LOAN_CONTRACT:
      type = ATTACHMENT_COLLATERAL_LOAN_CONTRACT;
      break;
    case ATTACHMENT_MORTGAGE_LOAN_CONTRACT:
      type = ATTACHMENT_MORTGAGE_LOAN_CONTRACT;
      break;
    case ATTACHMENT_WARRANTY_LOAN_CONTRACT:
      type = ATTACHMENT_WARRANTY_LOAN_CONTRACT;
      break;
    case LOAN_REPAYMENT_BEFORE:
      type = LOAN_REPAYMENT_BEFORE;
      break;
    case ATTACHMENT_FIDUC_LOAN_CONTRACT:
      type = ATTACHMENT_FIDUC_LOAN_CONTRACT;
      break;
    case CONSUMPTION_LOAN_CONTRACT:
      type = CONSUMPTION_LOAN_CONTRACT;
      break;
    case FIDUCIARY_PROPERTY_CONTRACT:
      type = FIDUCIARY_PROPERTY_CONTRACT;
      break;
    case LOAN_REPORT_PAGE:
      type = LOAN_REPORT_PAGE;
      break;
    case COLLATERAL_ASSETS_LIST_MORTGAGE:
      type = COLLATERAL_ASSETS_LIST_MORTGAGE;
      break;
    case MOVABLE_ASSETS_FIDUCIARY:
      type = MOVABLE_ASSETS_FIDUCIARY;
      break;
    case EQUIPMENT_ASSETS:
      type = EQUIPMENT_ASSETS;
      break;
    case CREDIT_LINE_LOAN_CONTRACT:
      type = CREDIT_LINE_LOAN_CONTRACT;
      break;
    case LOAN_REPAYMENT_AFTER:
      type = LOAN_REPAYMENT_AFTER;
      break;
    case EMPLOYEE_LOAN_CONTRACT:
      type = EMPLOYEE_LOAN_CONTRACT;
      break;
    case DIRECT_PRINTING_CONTRACT:
      type = DIRECT_PRINTING_CONTRACT;
      break;
    case EMPLOYEE_MORTGAGE_LOAN_CONTRACT:
      type = EMPLOYEE_MORTGAGE_LOAN_CONTRACT;
      break;
    case LOAN_PERMISSION:
      type = LOAN_PERMISSION;
      break;
    case DISBURSEMENT_PERMISSION:
      type = DISBURSEMENT_PERMISSION;
      break;
    default:
      break;
    }
    LOG.info("########### DOWNLOAD [{}] DOCUMENT FROM BI PUBLISHER ... WITH PROCESS INSTANCE ID = [{}] \n "
            + " REQUEST ID = [{}] ENDPOINT = [{}], SOAP =[{}], REPORT_ABSOLUTE_PATH = [{}], FUNCTION=[{}]",
        type, instanceId, requestId, endPoint + function, soapAction, "/" + reportAbsolutePathDocType + "/" + reportAbsolutePath + ".xdo", function);
  }

  private XacHttpClient getXacClient(String documentType, String soapAction)
  {
    String endPoint = getPropertyByKey(environment, BI_PUBLISHER_ENDPOINT);
    if (documentType.equals(XacHttpConstants.ONLINE_LOAN_CONTRACT))
    {
      return new XacHttpClient
          .Builder(endPoint, TEXT_XML)
          .headerSoap("")
          .build();
    }
    return new XacHttpClient
        .Builder(endPoint, TEXT_XML, soapAction)
        .build();
  }

  private BiPath getBiPath(String type, String processTypeId, String productCode){
    BiPath biPath = new BiPath("", "");
    ArrayList<String> types = new ArrayList<>(Arrays.asList(CONSUMPTION_LOAN_CONTRACT, CREDIT_LINE_LOAN_CONTRACT, DIRECT_PRINTING_CONTRACT, EMPLOYEE_LOAN_CONTRACT,
        EMPLOYEE_MORTGAGE_LOAN_CONTRACT, MORTGAGE_LOAN_CONTRACT, MICRO_LOAN_CONTRACT, COLLATERAL_CO_OWNER_CONSENT, COLLATERAL_ASSETS_LIST_MORTGAGE,
        ATTACHMENT_COLLATERAL_LOAN_CONTRACT, LOAN_REPAYMENT_AFTER, LOAN_REPAYMENT_BEFORE, FIDUCIARY_PROPERTY_CONTRACT, LOAN_PERMISSION, MOVABLE_ASSETS_FIDUCIARY,
        EQUIPMENT_ASSETS, DISBURSEMENT_PERMISSION, ATTACHMENT_FIDUC_LOAN_CONTRACT, ATTACHMENT_WARRANTY_LOAN_CONTRACT, ATTACHMENT_MORTGAGE_LOAN_CONTRACT,
        GENERAL_TRADE_CONTRACT, REPURCHASE_TRADE_CONTRACT));
    if(types.contains(type)){
        try {
          biPath = biPathRepository.getBiPath(processTypeId, productCode);
        } catch (BpmRepositoryException e) {
          e.printStackTrace();
        }
    }
    return biPath;
  }
}
