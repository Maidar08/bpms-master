package mn.erin.bpms.organization.request.webapp.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.organization.request.webapp.OrganizationRequestConstants;
import mn.erin.bpms.organization.request.webapp.model.RestCreateForm;
import mn.erin.domain.aim.service.AimServiceRegistry;
import mn.erin.domain.base.MessageConstants;
import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.organization.ExcludedOrganizationData;
import mn.erin.domain.bpm.model.organization.FormDataOrganizationSalary;
import mn.erin.domain.bpm.model.organization.OrganizationRequest;
import mn.erin.domain.bpm.model.organization.OrganizationRequestExcel;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.usecase.customer.GetCustomerByPersonIdAndType;
import mn.erin.domain.bpm.usecase.customer.GetCustomerByPersonIdAndTypeInput;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitForm;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormInput;
import mn.erin.domain.bpm.usecase.form.submit_form.SubmitFormOutput;
import mn.erin.domain.bpm.usecase.organization.CreateOrganizationLeasing;
import mn.erin.domain.bpm.usecase.organization.CreateOrganizationLeasingInput;
import mn.erin.domain.bpm.usecase.organization.CreateOrganizationRequestsExcel;
import mn.erin.domain.bpm.usecase.organization.CreateOrganizationRequestsExcelInput;
import mn.erin.domain.bpm.usecase.organization.CreateOrganizationSalary;
import mn.erin.domain.bpm.usecase.organization.CreateOrganizationSalaryInput;
import mn.erin.domain.bpm.usecase.organization.GetAllOrganizationRequestInput;
import mn.erin.domain.bpm.usecase.organization.GetAllOrganizationRequestsOutput;
import mn.erin.domain.bpm.usecase.organization.GetAllOrganizationRequestsOutputExcel;
import mn.erin.domain.bpm.usecase.organization.GetLeasingOrganizationRegNumber;
import mn.erin.domain.bpm.usecase.organization.GetLeasingOrganizationRequests;
import mn.erin.domain.bpm.usecase.organization.GetLeasingOrganizationRequestsExcel;
import mn.erin.domain.bpm.usecase.organization.GetSalaryOrganizationRegNumber;
import mn.erin.domain.bpm.usecase.organization.GetSalaryOrganizationRequests;
import mn.erin.domain.bpm.usecase.organization.GetSalaryOrganizationRequestsExcel;
import mn.erin.domain.bpm.usecase.organization.SaveCreateOrganizationFormData;
import mn.erin.domain.bpm.usecase.organization.SaveCreateOrganizationFormDataInput;
import mn.erin.domain.bpm.usecase.organization.SaveCreateOrganizationFormDataOutput;
import mn.erin.domain.bpm.usecase.organization.UpdateStateOrgLeasing;
import mn.erin.domain.bpm.usecase.organization.UpdateStateOrgSalary;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpms.organization.request.webapp.util.OrganizationRequestRestUtil.isAuthenticatedUser;
import static mn.erin.domain.bpm.BpmLoanContractConstants.CONTRACT_Id;
import static mn.erin.domain.bpm.BpmMessagesConstants.CONTRACT_REQUEST_ID_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CONTRACT_REQUEST_ID_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_BASIC_AUTHORIZATION_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INVALID_BASIC_AUTHORIZATION_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_TYPE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_TYPE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CONTRACT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.EMPTY_VALUE;
import static mn.erin.domain.bpm.BpmModuleConstants.EXCEL_MEDIA_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ORGANIZATION_TYPE_LEASING;
import static mn.erin.domain.bpm.BpmModuleConstants.ORGANIZATION_TYPE_SALARY;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_REQUEST;
import static mn.erin.domain.bpm.BpmModuleConstants.SIMPLE_DATE_FORMAT;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertStringToDate;

/**
 * @author Odgavaa
 */

@RestController
@RequestMapping(value = "/organization-requests", name = "Provides BPMS organization request API.")
public class OrganizationRequestApi
{
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationRequestApi.class);

  private final AimServiceRegistry aimServiceRegistry;
  private final BpmsServiceRegistry bpmsServiceRegistry;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final OrganizationSalaryRepository organizationSalaryRepository;
  private final OrganizationLeasingRepository organizationLeasingRepository;
  private final Environment environment;

  public OrganizationRequestApi(AimServiceRegistry aimServiceRegistry, BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry,
      Environment environment, OrganizationLeasingRepository organizationLeasingRepository, OrganizationSalaryRepository organizationSalaryRepository)
  {
    this.aimServiceRegistry = aimServiceRegistry;
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.environment = environment;
    this.organizationLeasingRepository = organizationLeasingRepository;
    this.organizationSalaryRepository = organizationSalaryRepository;
  }

  @ApiOperation("Creates organization request")
  @PostMapping("/organization-registration/create")
  public ResponseEntity<RestResult> createOrganizationRequest(@RequestBody RestOrganizationRequest request)
      throws UseCaseException, BpmServiceException, ParseException
  {
    Validate.notNull(request);

    String registerNumber;
    String custType;
    if (!StringUtils.isBlank(request.getOldContractNumber()) && request.getOldContractNumber() != null)
    {
      registerNumber = String.valueOf(request.getParameters().get("register"));
    }
    else
    {
      registerNumber = request.getRegistrationNumber();
    }
    if (StringUtils.isNumeric(registerNumber))
    {
      custType = "Corporate";
    }
    else
    {
      custType = "Retail";
    }
    LOGGER.info("Downloads customer info by REG_NUMBER = {} from XAC CBS.", request.getRegistrationNumber());
    GetCustomerByPersonIdAndTypeInput input = new GetCustomerByPersonIdAndTypeInput("", custType, registerNumber);
    GetCustomerByPersonIdAndType useCase = new GetCustomerByPersonIdAndType(bpmsServiceRegistry.getNewCoreBankingService());

    Customer customer = useCase.execute(input);
    LOGGER.info("**************** Successful downloaded customer info by REG_NUMBER and CUSTOMER_TYPE = {}", registerNumber);
    PersonInfo personInfo = customer.getPersonInfo();
    String orgName = personInfo.getFirstName();
    String processType = request.getProcessType();
    LocalDate establishedDate = personInfo.getBirthDate();
    List<AddressInfo> addressInfoList = customer.getAddressInfoList();
    String address = addressInfoList.get(0).getFullAddress();

    String establishedDateString = EMPTY_VALUE;
    if (null != establishedDate)
    {
      establishedDateString = establishedDate.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
    }

    String cifNumber = customer.getCustomerNumber();
    String branchNumber = request.getBranchNumber();
    String assignee = request.getUserId();
    String contractId;
    String email = customer.getContactInfoList().get(0).getEmail();

    String countryRegNumber = request.getCountryRegNumber();
    String processInstanceId;

    String oldInstanceId = request.getInstanceId();
    String oldContractNumber = request.getOldContractNumber();
    Date createdDate = new Date();

    processInstanceId = bpmsServiceRegistry.getProcessTypeService().startProcess(request.getProcessType());
    if (processType.equals(SALARY_REQUEST))
    {
      CreateOrganizationSalaryInput salaryInput = new CreateOrganizationSalaryInput(request.getProcessType(), cifNumber, orgName, registerNumber,
          establishedDate, branchNumber, assignee, processInstanceId, createdDate);
      CreateOrganizationSalary createOrganizationSalary = new CreateOrganizationSalary(aimServiceRegistry.getAuthenticationService(),
          aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getOrganizationSalaryRepository());
      contractId = createOrganizationSalary.execute(salaryInput);
    }
    else
    {
      CreateOrganizationLeasingInput leasingInput = new CreateOrganizationLeasingInput(request.getProcessType(), cifNumber, orgName, registerNumber,
          establishedDate, branchNumber, assignee, processInstanceId, createdDate);
      CreateOrganizationLeasing createOrganizationLeasing = new CreateOrganizationLeasing(aimServiceRegistry.getAuthenticationService(),
          aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getOrganizationLeasingRepository());
      contractId = createOrganizationLeasing.execute(leasingInput);
    }
    if (!StringUtils.isBlank(oldInstanceId) && !StringUtils.isBlank(oldContractNumber))
    {
      List<Variable> parameters = bpmsServiceRegistry.getCaseService().getVariables(oldInstanceId);
      Map<String, Object> parameter = new HashMap<>();
      for (Variable variable : parameters)
      {
        parameter.put(variable.getId().getId(), variable.getValue());
      }
      bpmsServiceRegistry.getCaseService().setCaseVariables(processInstanceId, parameter);
    }
    bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, CONTRACT_NUMBER, contractId);
    bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "branchId", branchNumber);
    bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "registerId", registerNumber);
    bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "registeredName", orgName);
    bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerCif", cifNumber);
    bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerRegistryId", countryRegNumber);
    bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerEmail", email);
    bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerAddress", address);
    if (StringUtils.isNotBlank(establishedDateString))
    {
      bpmsServiceRegistry.getCaseService()
          .setCaseVariableById(processInstanceId, "partnerEstablishedDate", convertStringToDate(SIMPLE_DATE_FORMAT, establishedDateString));
    }

    bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractOldNumber", oldContractNumber);

    return RestResponse.success(processInstanceId);
  }

  @GetMapping(value = "/salary")
  public ResponseEntity<RestResult> getSalaryOrgRequests(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
      @RequestParam(required = true) String branchId) throws UseCaseException, ParseException
  {
    Date startValidDate = convertDateString(startDate);
    Date endValidDate = convertDateString(endDate);

    GetAllOrganizationRequestInput allOrganizationRequestInput = new GetAllOrganizationRequestInput(startValidDate, endValidDate);
    GetSalaryOrganizationRequests getSalaryOrganizationRequests = new GetSalaryOrganizationRequests(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getOrganizationSalaryRepository());

    GetAllOrganizationRequestsOutput output = getSalaryOrganizationRequests.execute(allOrganizationRequestInput);
    Collection<OrganizationRequest> organizationRequests = output.getOrganizationRequests();

    List<OrganizationRequest> selectedOrganizationRequests = new ArrayList<>();

    if (branchId.equals("all"))
    {
      selectedOrganizationRequests = (List<OrganizationRequest>) organizationRequests;
    }

    if (!branchId.isEmpty())
    {
      String[] split = branchId.split(",");

      for (String branchIds : split)
      {
        for (OrganizationRequest organizationRequest : organizationRequests)
        {
          if (organizationRequest.getBranchId().equals(branchIds))
          {
            selectedOrganizationRequests.add(organizationRequest);
          }
        }
      }
    }

    return RestResponse.success(selectedOrganizationRequests);
  }

  @ApiOperation("Creates organization request")
  @PostMapping("/createOldOrganization")
  public ResponseEntity<RestResult> createOldOrganizationRequest(@RequestBody Map<String, Object>[] oldOrganizationsMap)
      throws BpmInvalidArgumentException, UseCaseException, ParseException
  {
    if (oldOrganizationsMap == null)
    {
      throw new BpmInvalidArgumentException("Input is null");
    }

    for (Map<String, Object> oldOrganizationMap : oldOrganizationsMap)
    {
      String processInstanceId = bpmsServiceRegistry.getProcessTypeService().startProcess("salaryOrganization");

      String contractId = getValidString(oldOrganizationMap.get("CONTRACTID"));
      String cname = getValidString(oldOrganizationMap.get("CNAME"));
      String registerNumber = getValidString(oldOrganizationMap.get("REGISTERNUMBER"));
      String cifNumber = getValidString(oldOrganizationMap.get("CIF"));
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

      String contractDateString = getValidString(oldOrganizationMap.get("CONTRACTDT"));
      LocalDate contractDate = null;
      if (!StringUtils.equals(contractDateString, NULL_STRING))
      {
        contractDate = LocalDate.parse(contractDateString, formatter);
      }

      String contractCreatedDateString = getValidString(oldOrganizationMap.get("CCREATEDT"));
      Date contractCreatedDate = null;
      if (!StringUtils.equals(contractCreatedDateString, NULL_STRING))
      {
        contractCreatedDate = convertStringToDate("dd-MM-yyyy", contractCreatedDateString);
      }

      String recordStat = getValidString(oldOrganizationMap.get("RECORD_STAT"));
      String branchNumber = getValidString(oldOrganizationMap.get("CONTRACTBRANCH"));
      String createdUserid = getValidString(oldOrganizationMap.get("CREATED_USERID"));
      String contractNumber = getValidString(oldOrganizationMap.get("CONTRACTNUMBER"));

      String createdAtString = getValidString(oldOrganizationMap.get("CREATED_AT"));
      LocalDate createdAtDate = null;
      if (!StringUtils.equals(createdAtString, NULL_STRING))
      {
        createdAtDate = LocalDate.parse(createdAtString, formatter);
      }

      CreateOrganizationSalaryInput input = new CreateOrganizationSalaryInput("salaryOrganization", cifNumber, cname,
          registerNumber, createdAtDate, branchNumber, createdUserid, processInstanceId, contractCreatedDate, contractId, contractDate,
          true, recordStat);
      CreateOrganizationSalary createOrganizationSalary = new CreateOrganizationSalary(aimServiceRegistry.getAuthenticationService(),
          aimServiceRegistry.getAuthorizationService(), organizationSalaryRepository);
      String outputContractId = createOrganizationSalary.execute(input);

      if (!StringUtils.isBlank(outputContractId))
      {
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, CONTRACT_NUMBER, outputContractId);
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "branchId", branchNumber);
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerEstablishedDate", contractCreatedDate);
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "registerId", registerNumber);
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "registeredName", cname);
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerCif", cifNumber);

        String countryRegNumber = getValidString(oldOrganizationMap.get("COUNTRYREGNUMBER"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerRegistryId", countryRegNumber);

        FormDataOrganizationSalary formDataOrganizationSalary = new FormDataOrganizationSalary(contractId);

        String onlineSalary = getValidString(oldOrganizationMap.get("ONLINESAL"));

        if (onlineSalary.equals("Y"))
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeHasOnline", "Тийм");
        }
        else
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeHasOnline", "Үгүй");
        }

        formDataOrganizationSalary.setFeeHasOnline(onlineSalary);

        String district = getValidString(oldOrganizationMap.get("DISTRICT"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerNDSubordinate", district);
        formDataOrganizationSalary.setPartnerNDSubordinate(district);

        String danRegNumber = getValidString(oldOrganizationMap.get("DANREGNUMBER"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerCodeND", danRegNumber);
        formDataOrganizationSalary.setPartnerCodeND(danRegNumber);

        String additionalInfo = getValidString(oldOrganizationMap.get("ADDITIONAL_INFO"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractSpecialRemark", additionalInfo);
        formDataOrganizationSalary.setContractSpecialRemark(additionalInfo);

        String cExtendYear = getValidString(oldOrganizationMap.get("CEXTENDYEAR"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractExtensionYear", cExtendYear + " жил");
        formDataOrganizationSalary.setContractExtensionYear(cExtendYear);

        String cExtendedDateString = getValidString(oldOrganizationMap.get("CEXTENDED_DATE"));
        Date cExtendedDate = null;
        if (!StringUtils.equals(cExtendedDateString, NULL_STRING))
        {
          cExtendedDate = convertStringToDate("dd-MM-yyyy", cExtendedDateString);
        }
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractExtensionNewDate", cExtendedDate);
        formDataOrganizationSalary.setContractExtensionNewDate(cExtendedDate);

        String cExtended = getValidString(oldOrganizationMap.get("CEXTENDED"));
        if (StringUtils.equals(cExtended, "Y"))
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractHasExtension", "Тийм");
        }
        else
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractHasExtension", "Үгүй");
        }
        formDataOrganizationSalary.setContractHasExtension(cExtended);

        String cYear = getValidString(oldOrganizationMap.get("CYEAR"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractPeriod", cYear + " жил");
        formDataOrganizationSalary.setContractPeriod(cYear);

        String sTime = getValidString(oldOrganizationMap.get("STIME"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeSalaryDays", sTime + " удаа");
        formDataOrganizationSalary.setFeeSalaryDays(sTime);

        String sDay1 = getValidString(oldOrganizationMap.get("SDAY1"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeSalaryDaysFirst", sDay1);
        formDataOrganizationSalary.setFeeSalaryDaysFirst(sDay1);

        String sDay2 = getValidString(oldOrganizationMap.get("SDAY2"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeSalaryDaysSecond", sDay2);
        formDataOrganizationSalary.setFeeSalaryDaysSecond(sDay2);

        String intCond = getValidString(oldOrganizationMap.get("INTCOND"));
        if (StringUtils.equals(intCond, "ST"))
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeType", "Стандарт");
        }
        else if (StringUtils.equals(intCond, "SP"))
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeType", "Тусгай");
        }

        formDataOrganizationSalary.setFeeType(intCond);

        String corporateRank = getValidString(oldOrganizationMap.get("CORPORATERANK"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeOrganizationRating", corporateRank);
        formDataOrganizationSalary.setFeeOrganizationRating(corporateRank);

        String isSalaryLoan = getValidString(oldOrganizationMap.get("IS_SALARY_LOAN"));
        if (isSalaryLoan.equals("1"))
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeHasLoan", "Тийм");
        }
        else
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeHasLoan", "Үгүй");
        }
        formDataOrganizationSalary.setFeeHasLoan(Integer.valueOf(isSalaryLoan));

        String salaryTranFee = getValidString(oldOrganizationMap.get("SALARYTRANFEE"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeSalaryTransaction", salaryTranFee);
        formDataOrganizationSalary.setFeeSalaryTransaction(Long.valueOf(salaryTranFee));

        String lastContractNo = getValidString(oldOrganizationMap.get("LASTCONTRACTNO"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractOldNumber", lastContractNo);
        formDataOrganizationSalary.setContractOldNumber(lastContractNo);

        String corporateType = getValidString(oldOrganizationMap.get("CORPORATE_TYPE"));
        if (corporateType.equals("1"))
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerType", "Хувийн");
        }
        else
        {
          bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerType", "Төрийн");
        }
        formDataOrganizationSalary.setPartnerType(Integer.valueOf(corporateType));

        String leakage = getValidString(oldOrganizationMap.get("LEAKAGE"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeBankFraud", leakage);
        formDataOrganizationSalary.setFeeBankFraud(leakage);

        String eRate = getValidString(oldOrganizationMap.get("ERATE"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeAmountPercent", eRate);
        formDataOrganizationSalary.setFeeAmountPercent(Double.valueOf(eRate));

        String aRate = getValidString(oldOrganizationMap.get("ARATE"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "feeKeyWorker", aRate);
        formDataOrganizationSalary.setFeeKeyWorker(Double.valueOf(aRate));

        String expireDtString = getValidString(oldOrganizationMap.get("EXPIREDT"));
        Date expireDt = null;
        if (!StringUtils.equals(expireDtString, NULL_STRING))
        {
          expireDt = convertStringToDate("dd-MM-yyyy", expireDtString);
        }
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractEndDate", expireDtString.replaceAll("-", "/"));
        formDataOrganizationSalary.setContractEndDate(expireDt);

        String contractDtString = getValidString(oldOrganizationMap.get("CONTRACTDT"));
        Date contractDt = null;
        if (!StringUtils.equals(contractDtString, NULL_STRING))
        {
          contractDt = convertStringToDate("dd-MM-yyyy", contractDtString);
        }
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "contractDate", contractDt);
        formDataOrganizationSalary.setContractDate(contractDt);

        String empPhone = getValidString(oldOrganizationMap.get("EMPPHONE"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerPhoneNumber", empPhone);
        formDataOrganizationSalary.setPartnerPhoneNumber(empPhone);

        String empName = getValidString(oldOrganizationMap.get("EMPNAME"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerContactEmployee", empName);
        formDataOrganizationSalary.setPartnerContactEmployee(empName);

        String hrCnt = getValidString(oldOrganizationMap.get("HRCNT"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerTotalEmployee", Long.valueOf(hrCnt));
        formDataOrganizationSalary.setHrCount(Integer.valueOf(hrCnt));

        String exposureCategoryDescription = getValidString(oldOrganizationMap.get("EXPOSURECATEGORY_DESCRIPTION"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerDirection", exposureCategoryDescription);
        formDataOrganizationSalary.setPartnerDirection(exposureCategoryDescription);

        String cAccountId = getValidString(oldOrganizationMap.get("CACCOUNTID"));
        bpmsServiceRegistry.getCaseService().setCaseVariableById(processInstanceId, "partnerAccountId", cAccountId);
        formDataOrganizationSalary.setPartnerAccountId(cAccountId);

        organizationSalaryRepository.updateSalaryOrganizationRequest(formDataOrganizationSalary);

        ExcludedOrganizationData excludedOrganizationData = new ExcludedOrganizationData();

        String lovNumber = getValidString(oldOrganizationMap.get("LOVNUMBER"));
        excludedOrganizationData.setLovNumber(lovNumber);

        String exposureCategoryCode = getValidString(oldOrganizationMap.get("EXPOSURECATEGORY_CODE"));
        excludedOrganizationData.setExposureCategoryCode(exposureCategoryCode);

        String form4001String = getValidString(oldOrganizationMap.get("FORM4001"));
        Date form4001 = null;
        if (!StringUtils.equals(form4001String, NULL_STRING))
        {
          form4001 = convertStringToDate("dd-MM-yyyy", form4001String);
        }
        excludedOrganizationData.setForm4001(form4001);

        String mEndSalaryString = getValidString(oldOrganizationMap.get("MENDSALARY"));
        Date mEndSalary = null;
        if (!StringUtils.equals(mEndSalaryString, NULL_STRING))
        {
          mEndSalary = convertStringToDate("dd-MM-yyyy", mEndSalaryString);
        }
        excludedOrganizationData.setmEndSalary(mEndSalary);

        String mStartSalaryString = getValidString(oldOrganizationMap.get("MSTARTSALARY"));
        Date mStartSalary = null;
        if (!StringUtils.equals(mStartSalaryString, NULL_STRING))
        {
          mStartSalary = convertStringToDate("dd-MM-yyyy", mStartSalaryString);
        }
        excludedOrganizationData.setmStartSalary(mStartSalary);

        String updatedAtString = getValidString(oldOrganizationMap.get("UPDATED_AT"));
        Date updatedAt = null;
        if (!StringUtils.equals(updatedAtString, NULL_STRING))
        {
          updatedAt = convertStringToDate("dd-MM-yyyy", updatedAtString);
        }
        excludedOrganizationData.setUpdatedAt(updatedAt);

        String lastUpdatedBy = getValidString(oldOrganizationMap.get("LAST_UPDATED_BY"));
        excludedOrganizationData.setLastUpdatedBy(lastUpdatedBy);

        excludedOrganizationData.setCreatedUserId(createdUserid);

        String chargeLGAccount = getValidString(oldOrganizationMap.get("CHARGEGLACCOUNT"));
        excludedOrganizationData.setChargeGlAccount(chargeLGAccount);

        String releaseEmpName = getValidString(oldOrganizationMap.get("RELEASEEMPNAME"));
        excludedOrganizationData.setReleaseEmpName(releaseEmpName);

        String onceAuth = getValidString(oldOrganizationMap.get("ONCE_AUTH"));
        excludedOrganizationData.setOnceAuth(onceAuth);

        String checkerDtStampString = getValidString(oldOrganizationMap.get("CHECKER_DT_STAMP"));
        Date checkerDtStamp = null;
        if (!StringUtils.equals(checkerDtStampString, NULL_STRING))
        {
          checkerDtStamp = convertStringToDate("dd-MM-yyyy", checkerDtStampString);
        }
        excludedOrganizationData.setCheckerDtStamp(checkerDtStamp);

        String checkerId = getValidString(oldOrganizationMap.get("CHECKER_ID"));
        excludedOrganizationData.setCheckerId(checkerId);

        String makerDtStampString = getValidString(oldOrganizationMap.get("MAKER_DT_STAMP"));
        Date makerDtStamp = null;
        if (!StringUtils.equals(makerDtStampString, NULL_STRING))
        {
          makerDtStamp = convertStringToDate("dd-MM-yyyy", makerDtStampString);
        }
        excludedOrganizationData.setMakerDtStamp(makerDtStamp);

        String makerId = getValidString(oldOrganizationMap.get("MAKER_ID"));
        excludedOrganizationData.setMakerId(makerId);

        String modNo = getValidString(oldOrganizationMap.get("MOD_NO"));
        excludedOrganizationData.setModNo(!StringUtils.equals(modNo, NULL_STRING) ? Integer.valueOf(modNo) : null);

        String authStat = getValidString(oldOrganizationMap.get("AUTH_STAT"));
        excludedOrganizationData.setAuthStat(authStat);

        String eRateMax = getValidString(oldOrganizationMap.get("ERATE_MAX"));
        excludedOrganizationData.seteRateMax(!StringUtils.equals(eRateMax, NULL_STRING) ? Double.valueOf(eRateMax) : null);

        String cCreatedDateString = getValidString(oldOrganizationMap.get("CCREATED_DATE"));
        Date cCreatedDate = null;
        if (!StringUtils.equals(cCreatedDateString, NULL_STRING))
        {
          cCreatedDate = convertStringToDate("dd-MM-yyyy", cCreatedDateString);
        }
        excludedOrganizationData.setcCreatedDate(cCreatedDate);

        String extensionDtString = getValidString(oldOrganizationMap.get("EXTENSION_DT"));
        Date extensionDt = null;
        if (!StringUtils.equals(extensionDtString, NULL_STRING))
        {
          extensionDt = convertStringToDate("dd-MM-yyyy", extensionDtString);
        }
        excludedOrganizationData.setExtensionDtString(extensionDt);

        //######## heavily used data
        excludedOrganizationData.setContractNumber(contractNumber);
        excludedOrganizationData.setCifNumber(cifNumber);

        String fcName = getValidString(oldOrganizationMap.get("FCNAME"));
        excludedOrganizationData.setFcName(fcName);

        excludedOrganizationData.setcCreateDate(contractCreatedDate);

        Date createdAt = null;
        if (!StringUtils.equals(createdAtString, NULL_STRING))
        {
          createdAt = convertStringToDate("dd-MM-yyyy", createdAtString);
        }
        excludedOrganizationData.setCreatedAt(createdAt);

        excludedOrganizationData.setCountryRegNumber(countryRegNumber);

        String additionInfo = getValidString(oldOrganizationMap.get("ADDITION_INFO"));
        excludedOrganizationData.setAdditionInfo(additionInfo);

        organizationSalaryRepository.updateSalaryOrganizationExcluded(excludedOrganizationData, contractId);
      }
    }

    return RestResponse.success();
  }

  @GetMapping(value = "/leasing")
  public ResponseEntity<RestResult> getLeasingOrgRequests(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
      @RequestParam(required = true) String branchId) throws UseCaseException, ParseException
  {
    /* Convert to date from date string*/
    Date startValidDate = convertDateString(startDate);
    Date endValidDate = convertDateString(endDate);

    GetAllOrganizationRequestInput allOrganizationRequestInput = new GetAllOrganizationRequestInput(startValidDate, endValidDate);
    GetLeasingOrganizationRequests getLeasingOrganizationRequests = new GetLeasingOrganizationRequests(aimServiceRegistry.getAuthenticationService(),
        aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getOrganizationLeasingRepository());

    GetAllOrganizationRequestsOutput output = getLeasingOrganizationRequests.execute(allOrganizationRequestInput);

    Collection<OrganizationRequest> organizationRequests = output.getOrganizationRequests();

    List<OrganizationRequest> selectedOrganizationRequests = new ArrayList<>();

    if (branchId.equals("all"))
    {
      selectedOrganizationRequests = (List<OrganizationRequest>) organizationRequests;
    }

    if (!branchId.isEmpty())
    {
      String[] split = branchId.split(",");

      for (String branchIds : split)
      {
        for (OrganizationRequest organizationRequest : organizationRequests)
        {
          if (organizationRequest.getBranchId().equals(branchIds))
          {
            selectedOrganizationRequests.add(organizationRequest);
          }
        }
      }
    }

    return RestResponse.success(selectedOrganizationRequests);
  }

  @ApiOperation("Organization registration cancellation")
  @PostMapping("/updateStateOrg")
  public ResponseEntity<RestResult> updateStateOrg(@RequestBody Map<String, Object> request) throws UseCaseException
  {
    String contractId = (String) request.get("contractId");
    String type = (String) request.get("type");

    Map<String, Object> input = new HashMap<>();
    String actionType = String.valueOf(request.get(ACTION_TYPE));
    input.put(CONTRACT_Id, contractId);
    input.put(ACTION_TYPE, actionType);
    input.put("makerId", String.valueOf(request.get("makerId")));
    input.put(CASE_INSTANCE_ID, request.get(CASE_INSTANCE_ID));
    if (type.equals(SALARY_REQUEST))
    {
      UpdateStateOrgSalary updateStateOrgSalary = new UpdateStateOrgSalary(aimServiceRegistry.getAuthenticationService(),
          aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getOrganizationSalaryRepository(), bpmsServiceRegistry, environment);
      return RestResponse.success(updateStateOrgSalary.execute(input));
    }
    else
    {
      UpdateStateOrgLeasing updateStateOrgLeasing = new UpdateStateOrgLeasing(aimServiceRegistry.getAuthenticationService(),
          aimServiceRegistry.getAuthorizationService(), bpmsRepositoryRegistry.getOrganizationLeasingRepository(), bpmsServiceRegistry, environment);
      return RestResponse.success(updateStateOrgLeasing.execute(input));
    }
  }

  @GetMapping(value = "/orgRegNumber/{regNumber}/{type}")
  public ResponseEntity getLeasingOrgRegNumber(@PathVariable String regNumber, @PathVariable String type) throws UseCaseException
  {
    if (type.equals(SALARY_REQUEST))
    {
      GetSalaryOrganizationRegNumber getSalaryOrganizationRegNumber = new GetSalaryOrganizationRegNumber(
          bpmsRepositoryRegistry.getOrganizationSalaryRepository());

      Collection<OrganizationRequest> organizationRequests = getSalaryOrganizationRegNumber.execute(regNumber);

      return RestResponse.success(organizationRequests);
    }
    else
    {
      GetLeasingOrganizationRegNumber getLeasingOrganizationRegNumber = new GetLeasingOrganizationRegNumber(
          bpmsRepositoryRegistry.getOrganizationLeasingRepository());

      Collection<OrganizationRequest> organizationRequests = getLeasingOrganizationRegNumber.execute(regNumber);

      return RestResponse.success(organizationRequests);
    }
  }

  @ApiOperation("Saves organization registration completed form variables.")
  @PostMapping("/save/creation-form")
  public ResponseEntity<RestResult> saveFormValues(@RequestBody RestCreateForm restCreateForm) throws BpmInvalidArgumentException, UseCaseException
  {
    if (null == restCreateForm)
    {
      throw new BpmInvalidArgumentException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    String processType = restCreateForm.getProcessType();
    String requestId = restCreateForm.getRequestId();
    String taskId = restCreateForm.getTaskId();
    String taskDefinitionKey = restCreateForm.getTaskDefinitionKey();
    String caseInstanceId = restCreateForm.getCaseInstanceId();
    Map<String, Object> parameters = restCreateForm.getParameters();

    validateFormInput(processType, requestId, taskId, taskDefinitionKey, caseInstanceId);

    SaveCreateOrganizationFormDataInput input = new SaveCreateOrganizationFormDataInput(processType, requestId, parameters);
    SaveCreateOrganizationFormData saveCreateOrganizationFormData = new SaveCreateOrganizationFormData(organizationSalaryRepository,
        organizationLeasingRepository);

    SaveCreateOrganizationFormDataOutput output = saveCreateOrganizationFormData.execute(input);
    LOGGER.info("########  Updated {} columns of organization registration request. ", output.getNumberOfColsUpdated());

    SubmitFormInput submitFormInput = new SubmitFormInput(taskId, caseInstanceId, parameters);

    CaseService caseService = bpmsServiceRegistry.getCaseService();
    TaskFormService taskFormService = bpmsServiceRegistry.getTaskFormService();

    SubmitForm submitForm = new SubmitForm(taskFormService, caseService);
    SubmitFormOutput submitFormOutput = submitForm.execute(submitFormInput);

    if (submitFormOutput.isSubmitted())
    {
      LOGGER.info("########  Submitted organization registration creation form. ");
    }

    return RestResponse.success();
  }

  @ApiOperation("Gets Organization requests as an excel file")
  @PostMapping("/report/{topHeader}/{searchKey}/{groupId}")
  public ResponseEntity getExcelReport(@PathVariable String topHeader, @PathVariable String searchKey, @PathVariable String groupId)
      throws BpmInvalidArgumentException, UnsupportedEncodingException, UseCaseException
  {
    String header = ORGANIZATION_TYPE_SALARY;
    if (StringUtils.isBlank(topHeader) || StringUtils.isBlank(groupId))
    {
      LOGGER.error("######## Could not convert input stream to byte array.");
      throw new BpmInvalidArgumentException(MessageConstants.NULL_GROUP_ID_CODE, MessageConstants.NULL_GROUP_ID);
    }

    if (isValidISOString(searchKey))
    {
      searchKey = new String(searchKey.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
      LOGGER.info("####################  DECODED SEARCH KEY VALUE FOR EXCEL EXPORTATION DECODED FROM ISO_8859_1 : [{}]", searchKey);
    }
    else
    {
      searchKey = URLDecoder.decode(searchKey, StandardCharsets.UTF_8.name());
    }

    LocalDateTime dateTime = LocalDateTime.now();
    String formattedDate = dateTime.format(dateFormatter);

    List<OrganizationRequestExcel> requests;

    if (topHeader.equals("salary"))
    {
      GetSalaryOrganizationRequestsExcel getSalaryOrganizationRequests = new GetSalaryOrganizationRequestsExcel(
          bpmsRepositoryRegistry.getOrganizationSalaryRepository());
      GetAllOrganizationRequestsOutputExcel output = getSalaryOrganizationRequests.execute(null);
      requests = (List<OrganizationRequestExcel>) output.getOrganizationRequests();
    }
    else
    {
      GetLeasingOrganizationRequestsExcel getLeasingOrganizationRequests = new GetLeasingOrganizationRequestsExcel(
          bpmsRepositoryRegistry.getOrganizationLeasingRepository());
      GetAllOrganizationRequestsOutputExcel output = getLeasingOrganizationRequests.execute(null);
      requests = (List<OrganizationRequestExcel>) output.getOrganizationRequests();
      header = ORGANIZATION_TYPE_LEASING;
    }
    //    if (StringUtils.isNotBlank(searchKey) && !searchKey.equals("undefined"))
    //    {
    //      String finalSearchKey = searchKey;
    //      requests = requests.stream().filter(
    //          orgReq -> orgReq.getCname().contains(finalSearchKey) ||
    //              orgReq.getCif().contains(finalSearchKey) ||
    //              orgReq.getCreatedUserid().contains(finalSearchKey) ||
    //              orgReq.getContactname().contains(finalSearchKey) ||
    //              orgReq.getRegisternumber().contains(finalSearchKey)).collect(Collectors.toList());
    //    }
    CreateOrganizationRequestsExcel createOrganizationRequestsExcel = new CreateOrganizationRequestsExcel(aimServiceRegistry.getAuthorizationService(),
        aimServiceRegistry.getAuthenticationService());
    CreateOrganizationRequestsExcelInput input = new CreateOrganizationRequestsExcelInput(topHeader, searchKey, groupId, formattedDate, requests);

    byte[] excelReportAsByte = createOrganizationRequestsExcel.execute(input);

    if (null == excelReportAsByte)
    {
      RestResponse.notFound(MessageConstants.NOT_FOUND);
    }

    ByteArrayResource resource = new ByteArrayResource(excelReportAsByte);

    String requestTypeName = BpmModuleConstants.ORGANIZATION_REQUEST_TYPE.get(header);
    String fileName = URLEncoder.encode(requestTypeName + "_", StandardCharsets.UTF_8.name());

    return ResponseEntity.ok().contentLength(resource.contentLength()).contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
        .header("Content-Disposition", "attachment; charset=utf-8; filename=\"" + fileName + formattedDate + ".xlsx\"").body(resource);
  }

  private void throwParameterMissingException(String parameterName)
  {
    throw new IllegalArgumentException(parameterName + " is missing!");
  }

  /**
   * Verifies basic authorization otherwise is authenticate user.
   *
   * @param authString basic auth string.
   * @return {@link ResponseEntity} if not authenticated user otherwise null.
   */
  private ResponseEntity<RestResult> verifyBasicAuthorization(String authString) throws BpmInvalidArgumentException
  {
    if (null != authString)
    {
      boolean isAuthenticated = isAuthenticatedUser(aimServiceRegistry.getAuthenticationService(), authString);

      if (!isAuthenticated)
      {
        throw new BpmInvalidArgumentException(INVALID_BASIC_AUTHORIZATION_CODE, INVALID_BASIC_AUTHORIZATION_MESSAGE);
      }
    }
    return null;
  }

  private static boolean isValidISOString(String input)
  {
    byte[] bytes = input.getBytes(StandardCharsets.ISO_8859_1);

    if (bytes.length != 0)
    {
      String valueFromBytes = new String(bytes, StandardCharsets.ISO_8859_1);

      return input.equalsIgnoreCase(valueFromBytes);
    }
    return false;
  }

  private void validateInput(RestOrganizationRequest request)
  {
    if (StringUtils.isBlank(request.getRegistrationNumber()))
    {
      throwParameterMissingException(OrganizationRequestConstants.REGISTER_NUMBER);
    }
    if (StringUtils.isBlank(request.getBranchNumber()))
    {
      throwParameterMissingException(OrganizationRequestConstants.BRANCH_NUMBER);
    }
  }

  private static void validateFormInput(String processType, String requestId, String taskId, String taskDefinitionKey, String caseInstanceId)
      throws BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(processType))
    {
      throw new BpmInvalidArgumentException(PROCESS_TYPE_ID_NULL_CODE, PROCESS_TYPE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(requestId))
    {
      throw new BpmInvalidArgumentException(CONTRACT_REQUEST_ID_IS_NULL_CODE, CONTRACT_REQUEST_ID_IS_NULL_MESSAGE);
    }
  }

  private Date convertDateString(String dateString) throws ParseException
  {
    String dateFormat = "E MMM dd yyyy";
    return new SimpleDateFormat(dateFormat).parse(dateString);
  }

  private static String getValidString(Object value)
  {
    if (null == value || NULL_STRING.contentEquals(String.valueOf(value)) || StringUtils.isBlank(String.valueOf(value)))
    {
      return NULL_STRING;
    }

    return String.valueOf(value).trim();
  }
}
