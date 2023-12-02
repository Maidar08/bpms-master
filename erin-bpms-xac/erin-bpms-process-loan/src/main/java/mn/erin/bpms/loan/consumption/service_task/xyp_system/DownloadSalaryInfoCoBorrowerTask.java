package mn.erin.bpms.loan.consumption.service_task.xyp_system;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.alfresco.connector.service.transform.AlfrescoRemoteTransformerService;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentInput;
import mn.erin.alfresco.connector.service.transform.RestTransformDocumentOutput;
import mn.erin.alfresco.connector.service.transform.TransformerService;
import mn.erin.bpms.loan.consumption.utils.DelegationExecutionUtils;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.salary.Organization;
import mn.erin.domain.bpm.model.salary.OrganizationId;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.CustomerService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerSalaryInfo;
import mn.erin.domain.bpm.usecase.customer.GetCustomerSalaryInfoInput;

import static mn.erin.bpms.loan.consumption.service_task.bpms.co_borrower.CoBorrowerUtils.INDEX_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.ALFRESCO;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMERS_FOLDER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPLOYEE_REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.MONTH;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_INFO_CO_BORROWER;
import static mn.erin.domain.bpm.BpmModuleConstants.SLASH;

/**
 * @author Tamir
 */
public class DownloadSalaryInfoCoBorrowerTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadSalaryInfoCoBorrowerTask.class);

  private final CustomerService customerService;
  private final AuthenticationService authenticationService;
  private final GroupRepository groupRepository;
  private final MembershipRepository membershipRepository;
  private final DocumentRepository documentRepository;

  public DownloadSalaryInfoCoBorrowerTask(CustomerService customerService,
      AuthenticationService authenticationService, GroupRepository groupRepository, MembershipRepository membershipRepository,
      DocumentRepository documentRepository)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.groupRepository = groupRepository;
    this.membershipRepository = membershipRepository;
    this.documentRepository = documentRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String regNum = (String) execution.getVariable(REGISTER_NUMBER);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER
        .info("*********** Download Salary Info Co Borrower Task with initial borrower REG_NUMBER ={}, REQUEST_ID ={}, User ID ={}", regNum, requestId, userId);

    Integer indexCoBorrower = (Integer) execution.getVariable(INDEX_CO_BORROWER);
    String currentRegNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, REGISTER_NUMBER_CO_BORROWER);
    String employeeRegNum = DelegationExecutionUtils.getExecutionParameterStringValue(execution, EMPLOYEE_REGISTER_NUMBER);

    LOGGER.info("*********** Downloading CO-BORROWER SALARY information from xyp system with REG_NUMBER ={}", currentRegNum);

    String month = (String) execution.getVariable(MONTH);
    Integer monthIntValue = Integer.valueOf(month);

    List<SalaryInfo> salaryInfos = getSalaryInfo(currentRegNum, employeeRegNum, monthIntValue);

    Integer currentCoBorrowerIndex = 0;
    if (null != indexCoBorrower)
    {
      for (Integer index = indexCoBorrower; index > 0; index--)
      {
        String regNumCoBorrower = (String) execution.getVariable(REGISTER_NUMBER_CO_BORROWER + "-" + index);

        if (regNumCoBorrower.equalsIgnoreCase(currentRegNum))
        {
          Map<String, Object> variables = execution.getVariables();

          String indexedSalaryInfo = SALARY_INFO_CO_BORROWER + "-" + index;
          currentCoBorrowerIndex = index;
          if (variables.containsKey(indexedSalaryInfo))
          {
            execution.removeVariable(indexedSalaryInfo);
          }
          execution.setVariable(indexedSalaryInfo, salaryInfos);
        }
      }
    }

    // Generates salary info document.
    LOGGER.info("############ Generates CO-BORROWER salary info document with REG_NUMBER ={}", currentRegNum);
    LocalDate localDate = LocalDate.now();

    List<RestTextValueField> textFields = new ArrayList<>();

    int year = localDate.getYear();
    Month localDateMonth = localDate.getMonth();
    int monthValue = localDateMonth.getValue();
    int dayOfMonth = localDate.getDayOfMonth();

    textFields.add(new RestTextValueField("year", String.valueOf(year)));
    textFields.add(new RestTextValueField("month", String.valueOf(monthValue)));
    textFields.add(new RestTextValueField("day", String.valueOf(dayOfMonth)));

    String currentUserId = authenticationService.getCurrentUserId();
    List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(currentUserId));

    Membership membership = memberships.get(0);

    if (null != membership)
    {
      GroupId groupId = membership.getGroupId();

      Group group = groupRepository.findById(groupId);

      if (null != group)
      {
        String branchCode = group.getId().getId();
        String branchName = group.getName();

        textFields.add(new RestTextValueField("branch_code", branchCode));
        textFields.add(new RestTextValueField("branch_name", branchName));
      }
    }

    String fullName = (String) execution.getVariable(FULL_NAME_CO_BORROWER + "-" + currentCoBorrowerIndex);

    if (null != fullName)
    {
      String[] splitFullName = fullName.split("\\s+");

      if (splitFullName.length != 1)
      {
        textFields.add(new RestTextValueField("last_name", splitFullName[0]));
        textFields.add(new RestTextValueField("first_name", splitFullName[1]));
      }
      else
      {
        textFields.add(new RestTextValueField("first_name", splitFullName[0]));
      }
    }

    textFields.add(new RestTextValueField("reg_num", currentRegNum));

    salaryInfos = getSortedSalaryInfo(salaryInfos);
    int fillIndex = 0;

    for (int index = 0; index < salaryInfos.size(); index++)
    {
      SalaryInfo salaryInfo = salaryInfos.get(index);

      Integer counter = getIndexOfMonth(salaryInfos, index);
      Integer salaryInfoYear = salaryInfo.getYear();
      Integer salaryInfoMonth = salaryInfo.getMonth();

      Organization organization = salaryInfo.getOrganization();
      String name = organization.getName();
      OrganizationId organizationId = organization.getOrganizationId();

      BigDecimal salaryFee = salaryInfo.getSalaryFee();
      BigDecimal salaryAmount = salaryInfo.getAmount();

      BigDecimal paid = salaryAmount.subtract(salaryFee);

      String[] fillIndex2 = { "a", "b", "c" };
      if (counter == 0)
      {
        fillIndex++;
      }

      int emptyFieldNumber = isEmptyField(salaryInfos, index);
      if (emptyFieldNumber > -1)
      {
        for (int i = emptyFieldNumber; i < 2; i++)
        {
          String emptyIndex = (fillIndex - 1) + fillIndex2[i + 1];
          addEmptyField(textFields, emptyIndex);
        }
        if (salaryInfos.size() == index + 1)
        {
          for (int i = counter; i < 2; i++)
          {
            String emptyIndex = (fillIndex) + fillIndex2[i + 1];
            addEmptyField(textFields, emptyIndex);
          }
        }
      }
      textFields.add(new RestTextValueField("orgSiID" + fillIndex + fillIndex2[counter], organizationId.getId()));
      textFields.add(new RestTextValueField("orgName" + fillIndex + fillIndex2[counter], name));

      textFields.add(new RestTextValueField("year" + fillIndex, String.valueOf(salaryInfoYear)));
      textFields.add(new RestTextValueField("month" + fillIndex, String.valueOf(salaryInfoMonth)));

      textFields.add(new RestTextValueField("salaryAmount" + fillIndex + fillIndex2[counter], String.valueOf(salaryAmount)));
      textFields.add(new RestTextValueField("salaryFee" + fillIndex + fillIndex2[counter], String.valueOf(salaryFee)));
      textFields.add(new RestTextValueField("paid" + fillIndex + fillIndex2[counter], String.valueOf(paid)));
    }

    String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String systemDateStr = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

    textFields.add(new RestTextValueField("request_id", processRequestId));
    textFields.add(new RestTextValueField("system_date", systemDateStr));
    textFields.add(new RestTextValueField("user", currentUserId));

    String templateName = "social_insurance_payment_info_" + monthIntValue + ".docx";
    String documentPath = getDocumentPath(currentRegNum, processRequestId, currentCoBorrowerIndex);

    RestTransformDocumentInput input = new RestTransformDocumentInput("Templates/" + templateName, documentPath);

    input.setTextValueField(textFields);
    input.setDynamicTables(new ArrayList<>());

    TransformerService transformerService = new AlfrescoRemoteTransformerService();

    RestTransformDocumentOutput output = transformerService.transform(input);

    String documentId = output.getDocumentId();

    // Persists ID card document.
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    if (null == caseInstanceId)
    {
      caseInstanceId = "empty";
    }

    documentRepository
        .removeBy(caseInstanceId, "04. Лавлагаа", "01. Лавлагаа", SALARY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME + currentCoBorrowerIndex);

    documentRepository
        .create(documentId, "04.01", caseInstanceId, SALARY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME + currentCoBorrowerIndex, "04. Лавлагаа", "01. Лавлагаа",
            documentId, ALFRESCO);

    LOGGER.info("############ Successful generated CO-BORROWER salary info document.");
    LOGGER.info("*********** Successful downloaded CO-BORROWER SALARY information from xyp system...");
  }

  private List<SalaryInfo> getSalaryInfo(String regNum, String employeeRegNum, Integer month)
      throws UseCaseException
  {
    GetCustomerSalaryInfoInput input = new GetCustomerSalaryInfoInput(regNum, employeeRegNum, month);
    GetCustomerSalaryInfo getCustomerSalaryInfo = new GetCustomerSalaryInfo(customerService);

    return getCustomerSalaryInfo.execute(input);
  }

  private String getDocumentPath(String registerNumber, String requestId, Integer coBorrowerIndex)
  {
    long timeStamp = new Date().getTime();
    String timeStampStr = Long.toString(timeStamp);

    return CUSTOMERS_FOLDER + SLASH + registerNumber + SLASH + requestId + SLASH + SALARY_ENQUIRE_CO_BORROWER_DOCUMENT_NAME + coBorrowerIndex + "_"
        + timeStampStr;
  }

  private List<SalaryInfo> getSortedSalaryInfo(List<SalaryInfo> salaryInfos)
  {
    salaryInfos.sort(Comparator.comparing(SalaryInfo::getYear, Comparator.reverseOrder()).thenComparing(SalaryInfo::getMonth, Comparator.reverseOrder()));
    return salaryInfos;
  }

  private int isEmptyField(List<SalaryInfo> salaryInfos, int index)
  {
    if (0 <= index - 1)
    {
      int thisMonth = salaryInfos.get(index).getMonth();
      int previousMonth = salaryInfos.get(index - 1).getMonth();
      if (thisMonth != previousMonth)
      {
        return getIndexOfMonth(salaryInfos, index - 1);
      }
    }
    return -1;
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

  private int getIndexOfMonth(List<SalaryInfo> salaryInfos, int index)
  {
    int counter = 0;

    counter += equalWithNext(salaryInfos, index + 1);
    if (0 < equalWithNext(salaryInfos, index + 1))
    {
      counter += equalWithNext(salaryInfos, index + 2);
    }

    counter -= equalWithPrevious(salaryInfos, index - 1);
    if (0 < equalWithPrevious(salaryInfos, index - 1))
    {
      counter -= equalWithPrevious(salaryInfos, index - 2);
    }

    if (counter > 0)
    {
      return 0;
    }
    if (counter == -2)
    {
      return 2;
    }
    if (counter == -1 || (counter == 0 && equalWithNext(salaryInfos, index + 1) == 1 && equalWithPrevious(salaryInfos, index - 1) == 1))
    {
      return 1;
    }
    return 0;
  }

  private int equalWithNext(List<SalaryInfo> salaryInfos, int index)
  {
    if (index < salaryInfos.size())
    {
      boolean isEqual = salaryInfos.get(index).getMonth().equals(salaryInfos.get(index - 1).getMonth());
      return isEqual ? 1 : 0;
    }
    return 0;
  }

  private int equalWithPrevious(List<SalaryInfo> salaryInfos, int index)
  {
    if (index > 0)
    {
      boolean isEqual = salaryInfos.get(index).getMonth().equals(salaryInfos.get(index + 1).getMonth());
      return isEqual ? 1 : 0;
    }
    return 0;
  }
}
