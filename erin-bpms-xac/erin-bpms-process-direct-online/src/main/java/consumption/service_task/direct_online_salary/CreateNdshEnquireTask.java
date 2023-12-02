package consumption.service_task.direct_online_salary;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.alfresco.connector.service.transform.AlfrescoRemoteTransformerService;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentInput;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentOutput;
import mn.erin.alfresco.connector.service.transform.TransformServiceException;
import mn.erin.alfresco.connector.service.transform.TransformerService;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.directOnline.DanInfo;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.document.UploadDocuments;
import mn.erin.domain.bpm.usecase.document.UploadDocumentsInput;
import mn.erin.domain.bpm.usecase.document.UploadFile;

import static consumption.util.CustomerInfoUtils.getParametersOfCustomerToLdms;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.DAN_INFO;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_SALARY_PROCESS_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_ENQUIRE_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_ENQUIRE_NAME_PDF;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;
import static mn.erin.domain.bpm.BpmModuleConstants.SOURCE_LDMS;
import static mn.erin.domain.bpm.BpmModuleConstants.TEMPLATE_PATH_NDSH_ENQUIRE;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.util.process.BpmUtils.getPdfBase64;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class CreateNdshEnquireTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(CreateNdshEnquireTask.class);

  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final AimServiceRegistry aimServiceRegistry;
  private final GroupRepository groupRepository;

  public CreateNdshEnquireTask(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AimServiceRegistry aimServiceRegistry, GroupRepository groupRepository)
  {
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.aimServiceRegistry = aimServiceRegistry;
    this.groupRepository = groupRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String registerNumber = String.valueOf(execution.getVariable(REGISTER_NUMBER));
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));

    String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));
    String processInstanceId = String.valueOf(execution.getVariable(PROCESS_INSTANCE_ID));
    String instanceId = execution.getVariable(PROCESS_TYPE_ID).equals(ONLINE_SALARY_PROCESS_TYPE) ? caseInstanceId : processInstanceId;
    String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));
    String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));

    String documentPath = getDocumentPath(registerNumber, processRequestId);

    List<RestTextValueField> restTextValueFields = getTextFields(execution);

    List<String> documentIdList = transformDocument(TEMPLATE_PATH_NDSH_ENQUIRE, documentPath, restTextValueFields);
    execution.setVariable("ndshDocumentList", documentIdList);

    if (!documentIdList.isEmpty())
    {
      removePreviousLoanApplication(instanceId, processRequestId);
    }

    List<String> returnValue = getPdfBase64(documentIdList);

    if (null == returnValue || returnValue.isEmpty() || returnValue.size() != 2)
    {
      throw new BpmServiceException("File not found when upload ndsh enquire file to LDMS");
    }

    persistLoanApplication(returnValue.get(0), instanceId, processRequestId, trackNumber, processTypeId);
    if (getValidString(execution.getVariable(PROCESS_TYPE_ID)).equals(ONLINE_SALARY_PROCESS_TYPE))
    {
      sendFileToLDMS(execution, instanceId, returnValue.get(1));
    }
  }

  private List<String> transformDocument(String templatePath, String documentPath, List<RestTextValueField> restTextValueFields)
      throws TransformServiceException
  {
    TransformerService transformerService = new AlfrescoRemoteTransformerService();
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("deleteBaseDocument", "false");
    RestTransformDocumentInput input = new RestTransformDocumentInput(templatePath, documentPath);
    input.setTextValueField(restTextValueFields);

    RestTransformDocumentOutput output = transformerService.transform(input, queryParams);
    return Arrays.asList(output.getBaseDocumentId(), output.getDocumentId());
  }

  private String getDocumentPath(String registerNumber, String processRequestId)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return BpmModuleConstants.CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + processRequestId + SLASH + SALARY_ENQUIRE_DOCUMENT_NAME + "_" + timeStampStr;
  }

  private void removePreviousLoanApplication(String instanceId, String processRequestId)
  {
    LOG.info("############ Removes previous Ndsh Enquire with REQUEST ID = [{}]", processRequestId);
    bpmsRepositoryRegistry.getDocumentRepository().removeBy(instanceId, "04. Лавлагаа", "01. Лавлагаа", SALARY_ENQUIRE_DOCUMENT_NAME);
  }

  private void persistLoanApplication(String documentId, String instanceId, String processRequestId, String trackNumber, String processTypeId) throws BpmRepositoryException
  {
    if (null != documentId)
    {
      LOG.info("########## Persist Ndsh Enquire with REQUEST ID = [{}]", processRequestId);
      bpmsRepositoryRegistry.getDocumentRepository()
          .create(documentId, "04.01", instanceId, SALARY_ENQUIRE_DOCUMENT_NAME, "04. Лавлагаа", "01. Лавлагаа", documentId, ALFRESCO);
      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOG.info("########## Successful generated Ndsh Enquire with REQUEST ID = [{}], TRACK NUMBER = [{}]", processRequestId, trackNumber);
      }
      else
      {
        LOG.info("########## Successful generated Ndsh Enquire with REQUEST ID = [{}]", processRequestId);
      }
    }
  }

  private List<RestTextValueField> getTextFields(DelegateExecution execution) throws AimRepositoryException
  {
    LocalDate localDate = LocalDate.now();

    List<RestTextValueField> textFields = new ArrayList<>();

    int year = localDate.getYear();
    Month localDateMonth = localDate.getMonth();
    int monthValue = localDateMonth.getValue();
    int dayOfMonth = localDate.getDayOfMonth();

    textFields.add(new RestTextValueField("year", String.valueOf(year)));
    textFields.add(new RestTextValueField("month", String.valueOf(monthValue)));
    textFields.add(new RestTextValueField("day", String.valueOf(dayOfMonth)));

    String currentUserId = aimServiceRegistry.getAuthenticationService().getCurrentUserId();
    List<Membership> memberships = aimServiceRegistry.getMembershipRepository().listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(currentUserId));

    Membership membership = memberships.get(0);

    if (null != membership)
    {
      String branchCode = String.valueOf(execution.getVariable("branchNumber"));

      Group group = groupRepository.findById(GroupId.valueOf(branchCode));

      if (null != group)
      {
        String branchName = group.getName();
        textFields.add(new RestTextValueField("branch_code", " "));
        textFields.add(new RestTextValueField("branch_name", branchName));
      }
    }

    String registerNumber = String.valueOf(execution.getVariable(REGISTER_NUMBER));
    textFields.add(new RestTextValueField("reg_num", registerNumber));

    String fullName = (String) execution.getVariable(FULL_NAME);
    String[] s = fullName.split(" ");
    if (fullName.contains(" "))
    {
      String firstName = s[0];
      String lastName = s[1];
      textFields.add(new RestTextValueField("last_name", lastName));
      textFields.add(new RestTextValueField("first_name", firstName));
    }
    else
    {
      textFields.add(new RestTextValueField("first_name", fullName));
      textFields.add(new RestTextValueField("last_name", " "));
    }

    List<DanInfo> danInfos = (List<DanInfo>) execution.getVariable(DAN_INFO);
    danInfos = getSortedDanInfo(danInfos);
    int firstIndex = 1;
    String[] secondIndexArray = { "a", "b", "c" };
    int secondArrayIndex = 0;

    for (int danIndex = 0; danIndex < danInfos.size(); danIndex++)
    {
      DanInfo danInfo = danInfos.get(danIndex);

      Integer danInfoYear = danInfo.getYear();
      Integer danInfoMonth = danInfo.getMonth();
      String organizationName = danInfo.getOrgName();
      String organizationId = danInfo.getOrgSiID();
      Boolean paid = danInfo.getPaid();
      BigDecimal salaryAmount = danInfo.getSalaryAmount();
      BigDecimal salaryFee = danInfo.getSalaryFee();

      if (danIndex > 0 && !equalWithPrevious(danInfos, danIndex) && secondArrayIndex < 3)
      {
        secondArrayIndex++;
        while (secondArrayIndex < 3)
        {
          String emptyIndex = (firstIndex) + secondIndexArray[secondArrayIndex];
          addEmptyField(textFields, emptyIndex);
          secondArrayIndex++;
        }
        firstIndex++;
        secondArrayIndex = 0;
      }
      if (danIndex > 0 && equalWithPrevious(danInfos, danIndex)) {
        if (secondArrayIndex >= 2){
          secondArrayIndex = 0;
        } else {
          secondArrayIndex++;
        }
      }
      textFields.add(new RestTextValueField("orgSiID" + firstIndex + secondIndexArray[secondArrayIndex], organizationId));
      textFields.add(new RestTextValueField("orgName" + firstIndex + secondIndexArray[secondArrayIndex], organizationName));

      textFields.add(new RestTextValueField("year" + firstIndex, String.valueOf(danInfoYear)));
      textFields.add(new RestTextValueField("month" + firstIndex, String.valueOf(danInfoMonth)));

      textFields.add(new RestTextValueField("salaryAmount" + firstIndex + secondIndexArray[secondArrayIndex], String.valueOf(salaryAmount)));
      textFields.add(new RestTextValueField("salaryFee" + firstIndex + secondIndexArray[secondArrayIndex], String.valueOf(salaryFee)));
      textFields.add(new RestTextValueField("paid" + firstIndex + secondIndexArray[secondArrayIndex], String.valueOf(paid)));
    }

    if (secondArrayIndex < 3)
      {
        secondArrayIndex++;
        while (secondArrayIndex < 3)
        {
          String emptyIndex = (firstIndex) + secondIndexArray[secondArrayIndex];
          addEmptyField(textFields, emptyIndex);
          secondArrayIndex++;
        }
      }
    while (firstIndex%13 != 0) {
      if (secondArrayIndex < 3)
      {
        while (secondArrayIndex < 3)
        {
          String emptyIndex = (firstIndex) + secondIndexArray[secondArrayIndex];
          addEmptyField(textFields, emptyIndex);
          textFields.add(new RestTextValueField("year" + firstIndex, "  "));
          textFields.add(new RestTextValueField("month" + firstIndex, "  "));
          secondArrayIndex++;
        }
      }
      firstIndex++;
      secondArrayIndex = 0;
    }

    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String systemDateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

    textFields.add(new RestTextValueField("request_id", processRequestId));
    textFields.add(new RestTextValueField("system_date", systemDateStr));
    textFields.add(new RestTextValueField("user", currentUserId));

    return textFields;
  }

  private List<DanInfo> getSortedDanInfo(List<DanInfo> danInfos)
  {
    danInfos.sort(Comparator.comparing(DanInfo::getYear, Comparator.reverseOrder()).thenComparing(DanInfo::getMonth, Comparator.reverseOrder()));
    return danInfos;
  }

  private void addEmptyField(List<RestTextValueField> textFields, String fillIndex)
  {
    textFields.add(new RestTextValueField("orgSiID" + fillIndex, "  "));
    textFields.add(new RestTextValueField("orgName" + fillIndex, "  "));

    textFields.add(new RestTextValueField("year" + fillIndex, "  "));
    textFields.add(new RestTextValueField("month" + fillIndex, "  "));

    textFields.add(new RestTextValueField("salaryAmount" + fillIndex, "  "));
    textFields.add(new RestTextValueField("salaryFee" + fillIndex, "  "));
    textFields.add(new RestTextValueField("paid" + fillIndex, "  "));
  }

  private boolean equalWithPrevious(List<DanInfo> danInfos, int danIndex)
  {
    return danInfos.get(danIndex).getMonth().equals(danInfos.get(danIndex - 1).getMonth());
  }

  private void sendFileToLDMS(DelegateExecution execution, String instanceId, String base64)
      throws UseCaseException
  {
    String processRequestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String categoryEnquire = String.valueOf(execution.getVariable("categoryEnquire"));
    String subCategoryMongolBank = String.valueOf(execution.getVariable("subCategoryMongolBank"));
    List<UploadFile> uploadFiles = new ArrayList<>();

    uploadFiles.add(new UploadFile(SALARY_ENQUIRE_NAME_PDF, base64));

    Map<String, String> parameterValues = getParametersOfCustomerToLdms(execution);

    UploadDocumentsInput input = new UploadDocumentsInput(instanceId, categoryEnquire, subCategoryMongolBank, SOURCE_LDMS, parameterValues, uploadFiles);
    UploadDocuments useCase = new UploadDocuments(aimServiceRegistry.getAuthenticationService(), aimServiceRegistry.getAuthorizationService(),
        bpmsServiceRegistry.getDocumentService(), aimServiceRegistry.getMembershipRepository(), bpmsRepositoryRegistry.getDocumentInfoRepository(),
        bpmsRepositoryRegistry.getDocumentRepository());
    Boolean isUploadedFile = useCase.execute(input);
    if (Boolean.TRUE.equals(isUploadedFile))
    {
      LOG.info("######## SENT NDSH ENQUIRE FILE TO LDMS WITH REQUEST ID [{}]", processRequestId);
    }
  }
}