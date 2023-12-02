package mn.erin.bpm.rest.controller;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import mn.erin.alfresco.client.ApiClient;
import mn.erin.alfresco.client.ApiException;
import mn.erin.alfresco.connector.AlfrescoClient;
import mn.erin.alfresco.connector.service.download.AlfrescoRemoteDownloadService;
import mn.erin.alfresco.connector.service.download.DownloadService;
import mn.erin.alfresco.connector.service.download.DownloadServiceException;
import mn.erin.bpm.rest.model.RestDocument;
import mn.erin.bpm.rest.model.RestProcessEntityParameters;
import mn.erin.bpm.rest.model.RestUpdated;
import mn.erin.bpm.rest.model.RestUploadDocument;
import mn.erin.bpm.rest.model.RestUploadDocuments;
import mn.erin.bpm.rest.model.RestViewDocument;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.document.Document;
import mn.erin.domain.bpm.model.document.DocumentInfo;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.DocumentService;
import mn.erin.domain.bpm.usecase.contract.CreateLoanContractDocument;
import mn.erin.domain.bpm.usecase.contract.CreateOnlineSalaryLoanContractDocument;
import mn.erin.domain.bpm.usecase.contract.DownloadLoanContractAsBase64;
import mn.erin.domain.bpm.usecase.contract.DownloadLoanContractAsBase64Input;
import mn.erin.domain.bpm.usecase.contract.DownloadLoanPaymentScheduleAsBase64;
import mn.erin.domain.bpm.usecase.contract.DownloadLoanReportAsBase64;
import mn.erin.domain.bpm.usecase.contract.DownloadOnlineSalaryContractAsBase64;
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;
import mn.erin.domain.bpm.usecase.document_info.GetBasicDocumentInfos;
import mn.erin.domain.bpm.usecase.document_info.GetBasicDocumentInfosOutput;
import mn.erin.domain.bpm.usecase.document_info.GetSubDocumentInfos;
import mn.erin.domain.bpm.usecase.document_info.GetSubDocumentInfosOutput;
import mn.erin.domain.bpm.usecase.loan.GetLoanContractsByType;
import mn.erin.domain.bpm.usecase.loan.GetLoanContractsByTypeInput;
import mn.erin.domain.bpm.usecase.loan_contract.GetPurchaseSaleContractTypeInput;
import mn.erin.domain.bpm.usecase.loan_contract.GetPurchaseSaleContractType;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersOutput;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesById;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesByIdOutput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;
import static mn.erin.bpm.rest.constant.BpmsControllerConstants.INTERNAL_SERVER_ERROR;
import static mn.erin.bpm.rest.util.BpmRestUtils.mapLoanContractForm;
import static mn.erin.bpm.rest.util.BpmRestUtils.objectMapToSerializableMap;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestDocumentInfos;
import static mn.erin.domain.bpm.BpmDocumentConstant.BNPL_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.CONSUMPTION_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_PARAMETER;
import static mn.erin.domain.bpm.BpmDocumentConstant.DOCUMENT_TYPE;
import static mn.erin.domain.bpm.BpmDocumentConstant.GENERAL_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.INSTANT_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.LEASING_ORG_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.LOAN_REPAYMENT_BEFORE;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LEASING_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ONLINE_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.REPURCHASE_TRADE_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.SALARY_ORG_CONTRACT;
import static mn.erin.domain.bpm.BpmDocumentConstant.ZET_DOCUMENT_PARAMETER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmLoanContractConstants.CONTRACT_Id;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_CONTRACT_AS_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_ENQUIRE;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_CONTRACT;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_DECISION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.CATEGORY_LOAN_PERMISSION_DOCUMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER_LOAN_ENQUIRE_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.DOCUMENT_NAME_MONGOL_BANK_ENQUIRE;
import static mn.erin.domain.bpm.BpmModuleConstants.DOC_NAME_MONGOL_BANK_ENQUIRE_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_CONTRACT_AS_BASE64;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ENQUIRE_PDF_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PAYMENT_SCHEDULE_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_REPORT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_LOAN_CONTRACT_AS_BASE_64;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_REQUEST;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_CAMUNDA;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_PUBLISHER;
import static mn.erin.domain.bpm.BpmModuleConstants.SUB_CATEGORY_MONGOL_BANK;
import static mn.erin.domain.bpm.BpmPublisherParamConstants.P_ACC;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.LOAN_CONTRACT;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Oyungerel Chuluunsukh
 */
@Api @RestController @RequestMapping(value = "bpm/documents", name = "Case documents") public class DocumentRestApi extends BaseBpmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentRestApi.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  private final CaseService caseService;
  private final DocumentService documentService;

  private final DocumentInfoRepository documentInfoRepository;
  private final MembershipRepository membershipRepository;

  private final DocumentRepository documentRepository;
  private final ProcessRepository processRepository;

  private final Environment environment;

  @Inject
  public DocumentRestApi(
      BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService,
      MembershipRepository membershipRepository,
      Environment environment)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "AuthorizationService service is required!");

    this.caseService = Objects.requireNonNull(bpmsServiceRegistry.getCaseService(), "Case service is required!");
    this.documentService = Objects.requireNonNull(bpmsServiceRegistry.getDocumentService(), "Document service is required!");

    this.documentInfoRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getDocumentInfoRepository(), "Document info repository is required!");
    this.documentRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getDocumentRepository(), "Document repository is required!");

    this.processRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessRepository(), "Process repository is required!");
    this.membershipRepository = Objects.requireNonNull(membershipRepository, "Membership repository is required!");
    this.environment = environment;
  }

  @ApiOperation("Uploads multiple document.")
  @PostMapping("/upload/{caseInstanceId}")
  public ResponseEntity uploadDocuments(@PathVariable String caseInstanceId, @RequestBody RestUploadDocuments restDocuments)
      throws UseCaseException, BpmServiceException
  {
    if (StringUtils.isEmpty(caseInstanceId))
    {
      return RestResponse.badRequest("Case instance is cannot be empty!");
    }

    if (null == restDocuments)
    {
      return RestResponse.badRequest("Rest upload documents cannot be null!");
    }

    List<RestUploadDocument> uploadDocuments = restDocuments.getDocuments();

    if (uploadDocuments.isEmpty())
    {
      return RestResponse.badRequest("Upload documents empty!");
    }

    String type = restDocuments.getType();
    String subType = restDocuments.getSubType();

    if (StringUtils.isEmpty(type) || StringUtils.isEmpty(subType))
    {
      return RestResponse.badRequest("Document type or sub type cannot be empty, these values mandatory!");
    }

    List<UploadFile> uploadFiles = new ArrayList<>();

    for (RestUploadDocument uploadDocument : uploadDocuments)
    {
      String name = uploadDocument.getName();
      String contentAsBase64 = uploadDocument.getContentAsBase64();

      uploadFiles.add(new UploadFile(name, contentAsBase64));
    }
    Map<String, Object> variableMap = new HashMap<>();
    if (caseService.getVariables(caseInstanceId).isEmpty())
    {
      variableMap = bpmsServiceRegistry.getRuntimeService().getRuntimeVariables(caseInstanceId);
    }
    else
    {
      List<Variable> variableList = caseService.getVariables(caseInstanceId);
      for (Variable variable: variableList)
      {
        variableMap.put(variable.getId().getId(), variable.getValue());
      }
    }
    String processTypeId = String.valueOf(variableMap.get(PROCESS_TYPE_ID));
    String contractId = String.valueOf(variableMap.get(CONTRACT_Id));
    Map<String, String> parameters = new HashMap<>();

    if (processTypeId.contains("Organization"))
    {
      parameters.put(PROCESS_TYPE_ID, processTypeId);
      parameters.put(CONTRACT_Id, contractId);
      parameters.put("registerId", String.valueOf(variableMap.get("registeredId")));
      parameters.put("registeredName", String.valueOf(variableMap.get("registeredName")));
      parameters.put("partnerCif", String.valueOf(variableMap.get("partnerCif")));
    }
    if (processTypeId.contains(INSTANT_LOAN_PROCESS_TYPE_ID) || processTypeId.contains(ONLINE_LEASING_PROCESS_TYPE_ID))
    {
      parameters.put(PROCESS_TYPE_ID, processTypeId);
      parameters.put(PROCESS_REQUEST_ID, String.valueOf(variableMap.get(PROCESS_REQUEST_ID)));
      parameters.put(CIF_NUMBER, String.valueOf(variableMap.get(CIF_NUMBER)));
      parameters.put(REGISTER_NUMBER, String.valueOf(variableMap.get(REGISTER_NUMBER)));
      parameters.put(FULL_NAME, String.valueOf(variableMap.get(FULL_NAME)));
    }
    UploadDocumentsInput input = new UploadDocumentsInput(caseInstanceId, type, subType, SOURCE_LDMS, parameters, uploadFiles);
    UploadDocuments useCase = new UploadDocuments(authenticationService, authorizationService,
        documentService, membershipRepository, documentInfoRepository, documentRepository);

    try
    {
      Boolean isUpdated = useCase.execute(input);
      return RestResponse.success(new RestUpdated(isUpdated));
    }
    catch (IllegalArgumentException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Gets all basic document infos")
  @GetMapping("/basic/all")
  public ResponseEntity getBasicDocumentInfos()
  {
    GetBasicDocumentInfos getBasicDocumentInfos = new GetBasicDocumentInfos(authenticationService, authorizationService, documentInfoRepository);
    try
    {
      GetBasicDocumentInfosOutput documentInfosOutput = getBasicDocumentInfos.execute(null);

      Collection<DocumentInfo> documentInfos = documentInfosOutput.getDocumentInfos();

      if (documentInfos.isEmpty())
      {
        return RestResponse.success();
      }

      return RestResponse.success(toRestDocumentInfos(documentInfos));
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Gets all sub document infos by parent document info id.")
  @GetMapping("/sub/{parentId}")
  public ResponseEntity getSubDocuments(@PathVariable String parentId)
  {
    if (StringUtils.isEmpty(parentId))
    {
      return RestResponse.badRequest("Document parent document id cannot be empty!");
    }

    GetSubDocumentInfos getSubDocumentInfos = new GetSubDocumentInfos(authenticationService, authorizationService, documentInfoRepository);

    try
    {
      GetSubDocumentInfosOutput output = getSubDocumentInfos.execute(parentId);

      Collection<DocumentInfo> documentInfos = output.getDocumentInfos();

      return RestResponse.success(toRestDocumentInfos(documentInfos));
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Gets documents")
  @GetMapping("/all/{instanceId}")
  public ResponseEntity getVariableDocuments(@PathVariable String instanceId)
  {
    if (StringUtils.isEmpty(instanceId))
    {
      return RestResponse.badRequest("Instance id is required!");
    }

    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);

    try
    {
      GetVariablesByIdOutput output = getVariablesById.execute(instanceId);
      List<Variable> processVariables = output.getVariables();
      List<RestDocument> restDocuments = new ArrayList<>();
      for (Variable processVariable : processVariables)
      {
        VariableId id = processVariable.getId();
        String variableId = id.getId();

        if (variableId.equalsIgnoreCase(CUSTOMER_LOAN_ENQUIRE_PDF))
        {
          RestDocument document = new RestDocument(variableId, DOCUMENT_NAME_MONGOL_BANK_ENQUIRE, CATEGORY_ENQUIRE, SUB_CATEGORY_MONGOL_BANK, SOURCE_CAMUNDA,
              variableId, true);
          restDocuments.add(document);
        }

        addCoBorrowerLoanEnquire(restDocuments, processVariables, processVariable, variableId);
      }

      Collection<Document> documents = documentRepository.findByProcessInstanceId(instanceId);

      boolean isDownloadable = false;

      for (Document document : documents)
      {
        if (null != document)
        {
          String id = document.getDocumentId().getId();
          String name = document.getName();
          String reference = document.getReference();

          String mainType = document.getCategory();

          isDownloadable =
              mainType.equalsIgnoreCase(CATEGORY_LOAN_DECISION_DOCUMENT) || mainType.equals(CATEGORY_LOAN_CONTRACT) || mainType.equals(CATEGORY_ENQUIRE)
                  || mainType.equalsIgnoreCase(CATEGORY_LOAN_PERMISSION_DOCUMENT);
          String subType = document.getSubCategory();
          String source = document.getSource();

          RestDocument restDocument = new RestDocument(id, name, mainType, subType, source, reference, isDownloadable);
          restDocuments.add(restDocument);
        }
      }

      return RestResponse.success(restDocuments);
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Downloads document")
  @GetMapping("/download/{instanceId}/{fileId}")
  public ResponseEntity downloadDocuments(@PathVariable String instanceId, @PathVariable String fileId)
  {
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);
    try
    {
      GetVariablesByIdOutput output = getVariablesById.execute(instanceId);
      List<Variable> processVariables = output.getVariables();
      for (Variable processVariable : processVariables)
      {
        VariableId id = processVariable.getId();
        String variableId = id.getId();

        if (variableId.equals(fileId))
        {
          return RestResponse.success(processVariable.getValue());
        }
      }
      return RestResponse.success();
    }
    catch (UseCaseException e)
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Creates loan contract document and persists parameters")
  @PostMapping("/contract/loan/create/{processInstanceId}")
  public ResponseEntity createContractDocument(@PathVariable String processInstanceId, @RequestBody RestProcessEntityParameters restProcessEntityParameters)
      throws UseCaseException
  {
    if (restProcessEntityParameters.getParameters().isEmpty() || restProcessEntityParameters.getParameters() == null)
    {
      return RestResponse.badRequest("Parameters are empty!");
    }
    Map<String, Object> parameters = new HashMap<>();
    String entityType = ParameterEntityType.enumToString(LOAN_CONTRACT);

    if (restProcessEntityParameters.getParameterEntityType().equals(entityType))
    {
      parameters = mapLoanContractForm(restProcessEntityParameters.getParameters());
    }
    else
    {
      parameters = restProcessEntityParameters.getParameters();
    }
    Map<String, Serializable> serializableParametersMap = objectMapToSerializableMap(parameters);

    UpdateProcessParametersInput input = new UpdateProcessParametersInput(processInstanceId, Collections
        .singletonMap(ParameterEntityType.fromStringToEnum(restProcessEntityParameters.getParameterEntityType()), serializableParametersMap));
    UpdateProcessParameters updateProcessParameters = new UpdateProcessParameters(authenticationService, authorizationService, processRepository);
    UpdateProcessParametersOutput output = updateProcessParameters.execute(input);

    if (restProcessEntityParameters.getParameterEntityType().equals(entityType))
    {
      String loanProduct = String.valueOf(parameters.get(LOAN_PRODUCT));

      Boolean isCreated = null;

      if (loanProduct.equalsIgnoreCase(ONLINE_SALARY_PRODUCT_CODE))
      {
        LOGGER.info("####### CREATING ONLINE SALARY LOAN CONTRACT TO DOCUMENT TABLE with PRODUCT CODE = [{}]", ONLINE_SALARY_PRODUCT_CODE);
        CreateOnlineSalaryLoanContractDocument onlineSalary = new CreateOnlineSalaryLoanContractDocument(authenticationService, authorizationService,
            caseService,
            documentRepository);
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("instanceId", processInstanceId);
        isCreated = onlineSalary.execute(inputMap);
      }
      else
      {
        CreateLoanContractDocument createContractDocument = new CreateLoanContractDocument(authenticationService, authorizationService, caseService,
            documentRepository);
        isCreated = createContractDocument.execute(processInstanceId);
      }

      if (null != isCreated && isCreated)
      {
        LOGGER.info("########## Created loan contract document to DOCUMENT table.");
      }
      else
      {
        LOGGER.error("###### Could not create loan contract document to DOCUMENT table!");
      }

      return RestResponse.success(isCreated);
    }
    return RestResponse.success(output);
  }

  @ApiOperation("Download bpms user manual from alfresco")
  @GetMapping("/user-manual")
  ResponseEntity<RestResult> downloadUserManual() throws DownloadServiceException
  {
    final String s = downloadFromAlfrescoByName("userManual.pdf");
    return RestResponse.success(s);
  }
 
  @ApiOperation("View documents")
  @PostMapping("/view/{instanceId}")
  public ResponseEntity<RestResult> viewDocument(@PathVariable String instanceId, @RequestBody RestDocument restDocument)
      throws UseCaseException, BpmServiceException
  {
    if (null == restDocument)
    {
      return RestResponse.badRequest("Rest document cannot be null!");
    }

    String source = restDocument.getSource();
    String id = restDocument.getId();

    if (null == source)
    {
      return RestResponse.badRequest("Rest document source cannot be null");
    }

    if (source.equalsIgnoreCase(SOURCE_CAMUNDA))
    {
      GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, caseService);

      GetVariablesByIdOutput output = getVariablesById.execute(instanceId);
      List<Variable> processVariables = output.getVariables();

      for (Variable processVariable : processVariables)
      {
        String variableId = processVariable.getId().getId();

        if (variableId.equals(CUSTOMER_LOAN_ENQUIRE_PDF) && variableId.equals(id))
        {
          return getFileAsString(processVariable);
        }
        else if (variableId.contains(LOAN_ENQUIRE_PDF_CO_BORROWER) && id.contains(LOAN_ENQUIRE_PDF_CO_BORROWER))
        {
          return getFileAsString(processVariable);
        }

        if (variableId.equals(id))
        {
          byte[] documentAsBytes = (byte[]) processVariable.getValue();
          String documentAsBase64 = Base64.getEncoder().encodeToString(documentAsBytes);

          return RestResponse.success(new RestViewDocument(documentAsBase64));
        }
      }
      LOGGER.info("####### NO MATCHING CAMUNDA VARIABLE FOUND WITH DOCUMENT ID = {}", id);
      return RestResponse.success();
    }

    else if (source.equalsIgnoreCase(SOURCE_ALFRESCO))
    {
      try
      {
        String documentAsBase64 = downloadFromAlfresco(id);
        return RestResponse.success(new RestViewDocument(documentAsBase64));
      }
      catch (DownloadServiceException e)
      {
        LOGGER.error(e.getMessage(), e);
        return RestResponse.internalError(e.getMessage());
      }
    }

    else if (source.equalsIgnoreCase(SOURCE_PUBLISHER))
    {
      String contractAsBase64 = null;
      String reportAsBase64 = null;
      String loanPaymentScheduleAsBase64 = null;
      String documentType = (restDocument.getId().contains(BNPL) || restDocument.getId().contains("INSTANT_LOAN") || restDocument.getId().contains("Instant")  || restDocument.getId().contains("OnlineLeasing")) ?
          getValidString(bpmsServiceRegistry.getRuntimeService().getVariableById(instanceId, DOCUMENT_TYPE)) :
          getValidString(caseService.getVariableById(instanceId, DOCUMENT_TYPE));
      String documentId = id.substring(id.indexOf("-") + 1);
      if(documentType.equals(GENERAL_TRADE_CONTRACT) || documentType.equals(REPURCHASE_TRADE_CONTRACT))
      {
        return RestResponse.success(downloadPurchaseTradeContract(instanceId, documentType));
      }
      if (!StringUtils.isEmpty(documentType))
      {
        return RestResponse.success(downloadContractByType(instanceId, documentId));
      }

      if (id.contains(LOAN_CONTRACT_AS_BASE_64))
      {
        contractAsBase64 = downloadLoanContract(instanceId);
        if (null != contractAsBase64)
        {
          return RestResponse.success(new RestViewDocument(contractAsBase64));
        }
      }
      else if (id.contains(LOAN_PAYMENT_SCHEDULE_AS_BASE_64))
      {
        loanPaymentScheduleAsBase64 = downloadLoanPaymentSchedule(instanceId);

        if (null != loanPaymentScheduleAsBase64)
        {
          return RestResponse.success(new RestViewDocument(loanPaymentScheduleAsBase64));
        }
      }
      else if (id.contains(LOAN_REPORT_AS_BASE_64))
      {
        reportAsBase64 = downloadLoanReport(instanceId);

        if (null != reportAsBase64)
        {
          return RestResponse.success(new RestViewDocument(reportAsBase64));
        }
      }
      else if (id.contains(ONLINE_SALARY_LOAN_CONTRACT_AS_BASE_64) || id.contains(BNPL_CONTRACT_AS_BASE64) || id.contains(INSTANT_LOAN_CONTRACT_AS_BASE64) || id.contains("OnlineLeasingContractAsBase64"))

      {
        String type = EMPTY_VALUE;
        if (id.contains(ONLINE_SALARY_LOAN_CONTRACT_AS_BASE_64))
        {
          type = ONLINE_LOAN_CONTRACT;
        }
        else if (id.contains(BNPL_CONTRACT_AS_BASE64))
        {
          type = BNPL_CONTRACT;
        }
        else if (id.contains(INSTANT_LOAN_CONTRACT_AS_BASE64))
        {
          type = INSTANT_LOAN_CONTRACT;
        }
        else if (id.contains("OnlineLeasingContractAsBase64")){
          type = ONLINE_LEASING_CONTRACT;
        }

        String loanAccountNumber;
        if (type.equalsIgnoreCase(BNPL_CONTRACT) || type.equalsIgnoreCase(INSTANT_LOAN_CONTRACT) || type.equalsIgnoreCase(ONLINE_LEASING_CONTRACT))
        {
          Map<String, Object> parameters = bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(instanceId);
          loanAccountNumber = parameters.containsKey(LOAN_ACCOUNT_NUMBER) ? getValidString(parameters.get(LOAN_ACCOUNT_NUMBER)) : EMPTY_VALUE;
        }
        else
        {
          loanAccountNumber = String.valueOf(caseService.getVariableById(instanceId, LOAN_ACCOUNT_NUMBER));
        }
        Map<String, Object> documentParam = new HashMap<>();
        documentParam.put(P_ACC, loanAccountNumber);
        GetLoanContractsByType getLoanContractsByType = new GetLoanContractsByType(bpmsServiceRegistry.getDocumentService(),
            bpmsServiceRegistry.getCaseService(), authenticationService);
        GetLoanContractsByTypeInput bipInput = new GetLoanContractsByTypeInput(instanceId, type, documentParam);
        final List<Document> documentList = getLoanContractsByType.execute(bipInput);
        String base64;
        if (documentList.isEmpty())
        {
          return null;
        }
        else
        {
          base64 = documentList.get(0).getSource();
          if (null != base64)
          {
            return RestResponse.success(new RestViewDocument(base64));
          }
        }
      }
      else
      {
        LOGGER.info("####### NO MATCHING PUBLISHER OPTION FOUND WITH ID = {}", id);
        return RestResponse.success();
      }
    }
    LOGGER.info("####### NO MATCHING SOURCE OPTION FOUND WITH SOURCE = {}", source);
    return RestResponse.success(null);
  }

  @ApiOperation("View organization documents")
  @PostMapping("/view/organization/{instanceId}")
  public ResponseEntity<RestResult> viewDocument(@PathVariable String instanceId, @RequestBody Map<String, Object> request)
      throws UseCaseException
  {
    String contractId = getValidString(request.get(CONTRACT_Id));
    String type = getValidString(request.get("type"));
    String contractType;
    if (StringUtils.isEmpty(type))
    {
      LOGGER.info("####### NO MATCHING PUBLISHER OPTION FOUND WITH ORGANIZATION TYPE = {}", type);
      return RestResponse.success();
    }
    else
    {
      if (type.equals(SALARY_REQUEST)){
        contractType = SALARY_ORG_CONTRACT;
      }
      else
      {
        contractType = LEASING_ORG_CONTRACT;
      }

      Map<String, Object> documentParam = new HashMap<>();
      documentParam.put(P_ACC, contractId);
      GetLoanContractsByType getLoanContractsByType = new GetLoanContractsByType(bpmsServiceRegistry.getDocumentService(),
          bpmsServiceRegistry.getCaseService(), authenticationService);
      GetLoanContractsByTypeInput bipInput = new GetLoanContractsByTypeInput(instanceId, contractType, documentParam);
      List<Document> documents = getLoanContractsByType.execute(bipInput);
      List<Document> restDocuments = new ArrayList<>();

      if (documents.size() > 1)
      {
        for (Document document : documents)
        {
          restDocuments.add(document);
        }
        return RestResponse.success(restDocuments);
      }
      Document document = documents.get(0);
      return RestResponse.success(document);
    }
  }

  private ResponseEntity<RestResult> getFileAsString(Variable variable)
  {
    byte[] documentAsBytes = (byte[]) variable.getValue();

    if (null != documentAsBytes)
    {
      byte[] decodedString = Base64.getDecoder().decode(new String(documentAsBytes).getBytes(StandardCharsets.UTF_8));
      return RestResponse.success(new RestViewDocument(new String(decodedString)));
    }
    return null;
  }

  private String downloadFromAlfresco(String documentId) throws DownloadServiceException
  {
    DownloadService downloadService = new AlfrescoRemoteDownloadService();
    byte[] content = downloadService.download(documentId);
    return Base64.getEncoder().encodeToString(content);
  }

  private String downloadFromAlfrescoByName(String docName) throws DownloadServiceException
  {
    ApiClient apiClient = new AlfrescoClient()
        .setApiBase("/alfresco/versions/1")
        .getCoreApiClient();

    Response response = sendRequest(apiClient, "/queries/nodes?term=" + docName);

    try (ResponseBody body = response.body())
    {
      if (response.isSuccessful())
      {
        final JSONObject node = new JSONObject(body.string());
        JSONArray entries = node.getJSONObject("list").getJSONArray("entries");
        if (entries.isEmpty())
        {
          return null;
        }
        String nodeId = entries.getJSONObject(0).getJSONObject("entry").getString("id");
        return downloadFromAlfresco(nodeId);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }

  private Response sendRequest(ApiClient apiClient, String path) throws DownloadServiceException
  {
    String[] CONTENT_TYPES = { "application/json" };
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put("Content-Type", apiClient.selectHeaderContentType(CONTENT_TYPES));

    String[] authNames = { "basicAuth" };

    try
    {
      return apiClient.buildCall(path, "GET",
          Collections.emptyList(), Collections.emptyList(),
          null,
          headerParams, Collections.emptyMap(), authNames, null).execute();
    }
    catch (IOException | ApiException e)
    {
      throw new DownloadServiceException(e.getMessage());
    }
  }

  private String downloadLoanContract(String instanceId) throws UseCaseException
  {

    DownloadLoanContractAsBase64Input input = new DownloadLoanContractAsBase64Input(instanceId);
    DownloadLoanContractAsBase64 downloadLoanContractAsBase64 = new DownloadLoanContractAsBase64(documentService, caseService, environment);

    return downloadLoanContractAsBase64.execute(input);
  }

  private Object downloadContractByType(String instanceId, String documentType) throws UseCaseException, BpmServiceException
  {
    Map<String, Object> parameter = documentType.equals(LOAN_REPAYMENT_BEFORE) ?
        (Map<String, Object>) caseService.getVariableById(instanceId, ZET_DOCUMENT_PARAMETER) :
        (Map<String, Object>) caseService.getVariableById(instanceId, DOCUMENT_PARAMETER);
    String consumptionLoanRepayment = String.valueOf(caseService.getVariableById(instanceId, REPAYMENT_TYPE_ID));
    String productDescription = String.valueOf(caseService.getVariableById(instanceId, "productDescription"));
    if (!StringUtils.isEmpty(productDescription)){
      parameter.put(PRODUCT_CODE, productDescription.split("-")[0]);
    }

    if (documentType.equals(CONSUMPTION_LOAN_CONTRACT))
    {
      parameter.put(REPAYMENT_TYPE_ID, consumptionLoanRepayment);
    }
    GetLoanContractsByTypeInput input = new GetLoanContractsByTypeInput(instanceId, documentType, parameter);
    GetLoanContractsByType getLoanContractsByType = new GetLoanContractsByType(documentService, caseService, authenticationService);
    final List<Document> documentList = getLoanContractsByType.execute(input);
    if (documentList.isEmpty())
    {
      return null;
    }
    if (documentList.size() == 1)
    {
      return documentList.get(0).getSource();
    }
    return documentList.stream().map(Document::getSource).collect(Collectors.toList());
  }

  public Object downloadPurchaseTradeContract(String instanceId, String documentType) throws UseCaseException, BpmServiceException
  {
    String accountNumber = String.valueOf(caseService.getVariableById(instanceId, ACCOUNT_NUMBER));
    GetPurchaseSaleContractTypeInput input = new GetPurchaseSaleContractTypeInput( instanceId, documentType, accountNumber);
    GetPurchaseSaleContractType usecase = new GetPurchaseSaleContractType(documentService, caseService, authenticationService);
    List<Document> documents = usecase.execute(input);
    if (documents.isEmpty())
    {
      return null;
    }
    if (documents.size() == 1)
    {
      return documents.get(0).getSource();
    }
    return documents.stream().map(Document::getSource).collect(Collectors.toList());
  }

  private String downloadOnlineSalaryLoanContract(String instanceId) throws UseCaseException
  {
    DownloadLoanContractAsBase64Input input = new DownloadLoanContractAsBase64Input(instanceId);

    DownloadOnlineSalaryContractAsBase64 onlineSalaryContractAsBase64 = new DownloadOnlineSalaryContractAsBase64(documentService, caseService);

    return onlineSalaryContractAsBase64.execute(input);
  }

  private String downloadLoanReport(String instanceId) throws UseCaseException
  {

    DownloadLoanReportAsBase64 downloadLoanReportAsBase64 = new DownloadLoanReportAsBase64(documentService, caseService);

    return downloadLoanReportAsBase64.execute(instanceId);
  }

  private String downloadLoanPaymentSchedule(String instanceId) throws UseCaseException
  {
    DownloadLoanPaymentScheduleAsBase64 downloadLoanPaymentScheduleAsBase64 = new DownloadLoanPaymentScheduleAsBase64(documentService, caseService,
        authenticationService, membershipRepository);
    Map<String, String> input = new HashMap<>();
    input.put(CASE_INSTANCE_ID, instanceId);

    return downloadLoanPaymentScheduleAsBase64.execute(input);
  }

  private void addCoBorrowerLoanEnquire(List<RestDocument> restDocuments, List<Variable> processVariables, Variable processVariable, String variableId)
  {
    if (variableId.equalsIgnoreCase(INDEX_CO_BORROWER))
    {
      Integer indexCoBorrower = (Integer) processVariable.getValue();

      for (Integer index = indexCoBorrower; index > 0; index--)
      {
        for (Variable variable : processVariables)
        {
          String idRelatedCoBorrower = variable.getId().getId();

          if (idRelatedCoBorrower.equalsIgnoreCase(LOAN_ENQUIRE_PDF_CO_BORROWER + "-" + index))
          {
            String indexedName = DOC_NAME_MONGOL_BANK_ENQUIRE_CO_BORROWER + index;

            RestDocument restDocument = new RestDocument(LOAN_ENQUIRE_PDF_CO_BORROWER + "-" + index, indexedName, CATEGORY_ENQUIRE, SUB_CATEGORY_MONGOL_BANK,
                SOURCE_CAMUNDA, variableId, true);
            restDocuments.add(restDocument);
          }
        }
      }
    }
  }
}
