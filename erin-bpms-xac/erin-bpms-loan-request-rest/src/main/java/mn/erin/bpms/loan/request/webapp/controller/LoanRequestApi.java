/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.request.webapp.controller;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpms.loan.request.webapp.LoanRequestConstants;
import mn.erin.bpms.loan.request.webapp.util.LoanRequestRestUtil;
import mn.erin.common.mail.EmailService;
import mn.erin.common.utils.ValidationUtils;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.group.Group;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.model.role.Role;
import mn.erin.domain.aim.model.role.RoleId;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.ContactInfo;
import mn.erin.domain.aim.model.user.User;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.GroupRepository;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.repository.RoleRepository;
import mn.erin.domain.aim.repository.UserRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.base.MessageConstants;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.SendEmailAsAssignedRequest;
import mn.erin.domain.bpm.usecase.SendEmailAsAssignedRequestInput;
import mn.erin.domain.bpm.usecase.direct_online.Delete14daysPassedRequests;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequest;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestInput;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestOutput;
import mn.erin.domain.bpm.usecase.process.GetGroupProcessRequests;
import mn.erin.domain.bpm.usecase.process.GetGroupProcessRequestsByProcessType;
import mn.erin.domain.bpm.usecase.process.GetGroupProcessRequestsByProcessTypeInput;
import mn.erin.domain.bpm.usecase.process.GetProcessRequest;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestByProcessInstanceId;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestBySearchKey;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestBySearchKeyInput;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestsByAssignedUserId;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestsByAssignedUserIdInput;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestsByDateInput;
import mn.erin.domain.bpm.usecase.process.GetProcessRequestsOutput;
import mn.erin.domain.bpm.usecase.process.GetProcessType;
import mn.erin.domain.bpm.usecase.process.GetRequestsByCreatedDate;
import mn.erin.domain.bpm.usecase.process.GetRequestsByCreatedDateInput;
import mn.erin.domain.bpm.usecase.process.GetSubGroupProcessRequests;
import mn.erin.domain.bpm.usecase.process.GetUnassignedRequestsByChannelWithFilter;
import mn.erin.domain.bpm.usecase.process.GetUnassignedRequestsByChannelWithFilterInput;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUser;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUserInput;
import mn.erin.domain.bpm.usecase.process.UpdateAssignedUserOutput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParameters;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersInput;
import mn.erin.domain.bpm.usecase.process.UpdateRequestParametersOutput;
import mn.erin.domain.bpm.usecase.process.export_file.GetProcessRequestExcelInput;
import mn.erin.domain.bpm.usecase.process.export_file.GetProcessRequestsAsExcel;
import mn.erin.infrastucture.rest.common.response.RestEntity;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpms.loan.request.webapp.LoanRequestConstants.AMOUNT;
import static mn.erin.bpms.loan.request.webapp.LoanRequestConstants.ANNUAL_INTEREST_RATE;
import static mn.erin.bpms.loan.request.webapp.LoanRequestConstants.FIRST_PAYMENT_DATE;
import static mn.erin.bpms.loan.request.webapp.LoanRequestConstants.HAS_MORTGAGE;
import static mn.erin.bpms.loan.request.webapp.LoanRequestConstants.INCOME_BEFORE_TAX;
import static mn.erin.bpms.loan.request.webapp.LoanRequestConstants.INCOME_TYPE;
import static mn.erin.bpms.loan.request.webapp.LoanRequestConstants.MONTHLY_REPAYMENT;
import static mn.erin.bpms.loan.request.webapp.LoanRequestConstants.PURPOSE;
import static mn.erin.bpms.loan.request.webapp.LoanRequestConstants.TERM;
import static mn.erin.bpms.loan.request.webapp.util.LoanRequestRestUtil.isAuthenticatedUser;
import static mn.erin.bpms.loan.request.webapp.util.LoanRequestRestUtil.isDigits;
import static mn.erin.bpms.loan.request.webapp.util.LoanRequestRestUtil.validateOrganizationRegNumber;
import static mn.erin.domain.bpm.BpmMessagesConstants.AIM_USER_BLANK_MEMBERSHIP_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.AIM_USER_BLANK_MEMBERSHIP_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_REQUEST_BLANK_GROUP_ID_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_REQUEST_BLANK_GROUP_ID_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.AUTHORIZATION;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.BNPL_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.CHANNEL;
import static mn.erin.domain.bpm.BpmModuleConstants.CHO_BRANCH;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.DATE_FORMAT2;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.EMAIL;
import static mn.erin.domain.bpm.BpmModuleConstants.EXCEL_MEDIA_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.FULL_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANT_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INTERNET_BANK_USER_PROPERTY;
import static mn.erin.domain.bpm.BpmModuleConstants.INVOICE_AMOUNT_75;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.NULL_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PREVIUOS_EBANK_USER_NAME;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.REPAYMENT_TYPE;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertStringToDate;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Tamir
 */
@RestController
@RequestMapping(value = "/loan-requests", name = "Provides BPMS loan request API.")
public class LoanRequestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LoanRequestApi.class);
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final String ERR_MSG_INVALID_BASIC_AUTHORIZATION = "Invalid basic authorization!";
  private static final String ERR_MSG_NEGATIVE_AMOUNT = "Amount cannot be negative value!";

  private final UserRepository userRepository;
  private final ProcessRequestRepository processRequestRepository;
  private final ProcessRepository processRepository;
  private final GroupRepository groupRepository;
  private final ProcessTypeRepository processTypeRepository;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;
  private final TenantIdProvider tenantIdProvider;
  private final MembershipRepository membershipRepository;
  private final LoanContractRequestRepository loanContractRequestRepository;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final RoleRepository roleRepository;

  private final EmailService emailService;
  private final CaseService caseService;
  private final Environment environment;

  @Inject
  public LoanRequestApi(UserRepository userRepository, ProcessRequestRepository processRequestRepository, ProcessRepository processRepository,
      GroupRepository groupRepository, ProcessTypeRepository processTypeRepository, AuthenticationService authenticationService,
      AuthorizationService authorizationService, TenantIdProvider tenantIdProvider, MembershipRepository membershipRepository,
      LoanContractRequestRepository loanContractRequestRepository, BpmsRepositoryRegistry bpmsRepositoryRegistry, RoleRepository roleRepository,
      EmailService emailService, CaseService caseService, Environment environment)
  {
    this.userRepository = userRepository;
    this.processRequestRepository = processRequestRepository;
    this.processRepository = processRepository;
    this.groupRepository = groupRepository;
    this.processTypeRepository = processTypeRepository;
    this.authenticationService = authenticationService;
    this.authorizationService = authorizationService;
    this.tenantIdProvider = tenantIdProvider;
    this.membershipRepository = membershipRepository;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.roleRepository = roleRepository;
    this.emailService = emailService;
    this.environment = environment;
    this.loanContractRequestRepository = loanContractRequestRepository;
    this.caseService = caseService;
  }

  @GetMapping(value = "/cifNumber/{cifNumber}")
  public ResponseEntity<RestResult> getLoanRequestsByCifNumber(@PathVariable String cifNumber, @RequestParam String startDate, @RequestParam String endDate,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);
    if (StringUtils.isBlank(cifNumber))
    {
      throwParameterMissingException(LoanRequestConstants.CIF_NUMBER);
    }

    GetRequestsByCreatedDateInput input = new GetRequestsByCreatedDateInput(cifNumber, startDate, endDate);
    GetRequestsByCreatedDate getRequestsByCreatedDate = new GetRequestsByCreatedDate(authenticationService, authorizationService, processRequestRepository);

    Collection<ProcessRequest> processRequests = getRequestsByCreatedDate.execute(input);
    Collection<RestLoanRequest> restLoanRequests = new ArrayList<>();

    for (ProcessRequest processRequest : processRequests)
    {
      restLoanRequests.add(toRestLoanRequest(processRequest));
    }

    return RestResponse.success(LoanRequestRestUtil.descSortByCreatedDate(restLoanRequests));
  }

  @GetMapping(value = "/{requestId}")
  public ResponseEntity<RestResult> getLoanRequest(@PathVariable String requestId, @RequestHeader(value = AUTHORIZATION, required = false) String authString)
      throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);
    try
    {
      GetProcessRequest getProcessRequest = new GetProcessRequest(authenticationService, authorizationService, processRequestRepository);
      ProcessRequest processRequest = getProcessRequest.execute(requestId);

      RestLoanRequest restLoanRequest = toRestLoanRequest(processRequest);

      return ResponseEntity.ok(RestEntity.of(restLoanRequest));
    }
    catch (RuntimeException e)

    {
      return RestResponse.internalError(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<RestResult> getLoanRequests(@RequestParam(required = true) String startDate, @RequestParam(required = true) String endDate,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    try
    {
      GetGroupProcessRequests getGroupProcessRequests = new GetGroupProcessRequests(authenticationService, authorizationService, processRequestRepository,
          groupRepository);

      // load all requests
      GetProcessRequestsByDateInput input = new GetProcessRequestsByDateInput("", convertDateString(startDate), convertDateString(endDate));
      Collection<ProcessRequest> processRequests = getGroupProcessRequests.execute(input);
      Collection<RestLoanRequest> loanRequests = new ArrayList<>();

      List<String> permissions = getPermissions();

      if (!permissions.isEmpty() && permissions.contains("bpms.bpm.GetAllLoanRequestsExceptCHO"))
      {
        processRequests = filterRequestsExcludingByGroupId(processRequests, CHO_BRANCH);
      }

      for (ProcessRequest processRequest : processRequests)
      {
        loanRequests.add(toRestLoanRequest(processRequest));
      }
      return ResponseEntity.ok(RestEntity.of(LoanRequestRestUtil.descSortByCreatedDate(loanRequests)));
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
    catch (ParseException e)
    {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(value = "/branch/{branchNumber}/processType/{processType}")
  public ResponseEntity<RestResult> getBranchLoanRequestsByProcessType(@PathVariable String branchNumber, @PathVariable String processType,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    if (StringUtils.isBlank(branchNumber))
    {
      throwParameterMissingException(LoanRequestConstants.BRANCH_NUMBER);
    }

    String tenantId = tenantIdProvider.getCurrentUserTenantId();
    try
    {
      Group group = groupRepository.findByNumberAndTenantId(branchNumber, TenantId.valueOf(tenantId));

      GetGroupProcessRequestsByProcessType getGroupProcessRequestsByProcessType = new GetGroupProcessRequestsByProcessType(authenticationService,
          authorizationService, processRequestRepository, groupRepository);
      GetGroupProcessRequestsByProcessTypeInput input = new GetGroupProcessRequestsByProcessTypeInput(group.getId().getId(), processType);
      Collection<ProcessRequest> processRequests = getGroupProcessRequestsByProcessType.execute(input);
      Collection<RestLoanRequest> loanRequests = new ArrayList<>();
      for (ProcessRequest processRequest : processRequests)
      {
        loanRequests.add(toRestLoanRequest(processRequest));
      }
      return ResponseEntity.ok(RestEntity.of(LoanRequestRestUtil.descSortByCreatedDate(loanRequests)));
    }
    catch (AimRepositoryException | RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @GetMapping(value = "/branch/{branchNumber}")
  public ResponseEntity<RestResult> getBranchLoanRequests(@PathVariable String branchNumber,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    if (StringUtils.isBlank(branchNumber))
    {
      throwParameterMissingException(LoanRequestConstants.BRANCH_NUMBER);
    }

    String tenantId = tenantIdProvider.getCurrentUserTenantId();
    try
    {

      Group group = groupRepository.findByNumberAndTenantId(branchNumber, TenantId.valueOf(tenantId));

      GetGroupProcessRequests getGroupProcessRequests = new GetGroupProcessRequests(authenticationService, authorizationService, processRequestRepository,
          groupRepository);
      GetProcessRequestsByDateInput input = new GetProcessRequestsByDateInput(group.getId().getId(), null, null);

      Collection<ProcessRequest> processRequests = getGroupProcessRequests.execute(input);
      Collection<RestLoanRequest> loanRequests = new ArrayList<>();
      for (ProcessRequest processRequest : processRequests)
      {
        loanRequests.add(toRestLoanRequest(processRequest));
      }

      return ResponseEntity.ok(RestEntity.of(LoanRequestRestUtil.descSortByCreatedDate(loanRequests)));
    }
    catch (AimRepositoryException | RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @GetMapping(value = "/sub-branches/{branchNumber}")
  public ResponseEntity<RestResult> getSubBranchLoanRequests(@RequestParam(required = true) String startDate, @RequestParam(required = true) String endDate,
      @PathVariable String branchNumber, @RequestHeader(value = AUTHORIZATION, required = false) String authString)
      throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    if (StringUtils.isBlank(branchNumber))
    {
      throwParameterMissingException(LoanRequestConstants.BRANCH_NUMBER);
    }
    try
    {
      GetSubGroupProcessRequests subGroupProcessRequests = new GetSubGroupProcessRequests(groupRepository, authorizationService, authenticationService,
          processRequestRepository);
      GetProcessRequestsByDateInput input = new GetProcessRequestsByDateInput(branchNumber, convertStringToDate(DATE_FORMAT2, startDate),
          convertStringToDate(DATE_FORMAT2, endDate));

      Collection<ProcessRequest> processRequests = subGroupProcessRequests.execute(input);
      Collection<RestLoanRequest> loanRequests = new ArrayList<>();
      for (ProcessRequest processRequest : processRequests)
      {
        loanRequests.add(toRestLoanRequest(processRequest));
      }
      return ResponseEntity.ok(RestEntity.of(LoanRequestRestUtil.descSortByCreatedDate(loanRequests)));
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
    catch (ParseException e)
    {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(value = "/channel/{channelType}/group/{groupId}")
  public ResponseEntity<RestResult> getUnassignedLoanRequestsByChannel(@PathVariable String channelType, @PathVariable String groupId,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString,
      @RequestParam String startDate, @RequestParam String endDate) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    if (StringUtils.isBlank(groupId))
    {
      throw new BpmInvalidArgumentException(MessageConstants.NULL_GROUP_ID_CODE, MessageConstants.NULL_GROUP_ID);
    }

    if (StringUtils.isBlank(channelType))
    {
      throw new BpmInvalidArgumentException(MessageConstants.NULL_CHANNEL_CODE, MessageConstants.NULL_CHANNEL);
    }

    try
    {
      Delete14daysPassedRequests delete14daysPassedRequests = new Delete14daysPassedRequests(bpmsRepositoryRegistry, caseService, environment);
      delete14daysPassedRequests.execute(null);
      GetUnassignedRequestsByChannelWithFilter getUnassignedRequestsByChannelWithFilter = new GetUnassignedRequestsByChannelWithFilter(authenticationService, authorizationService,
          processRequestRepository);
      GetUnassignedRequestsByChannelWithFilterInput getUnassignedRequestsByChannelWithFilterInput = new GetUnassignedRequestsByChannelWithFilterInput(channelType, groupId, convertDateString(startDate), convertDateString(endDate));
      Collection<ProcessRequest> processRequests = getUnassignedRequestsByChannelWithFilter.execute(getUnassignedRequestsByChannelWithFilterInput);

      Collection<RestLoanRequest> loanRequests = new ArrayList<>();
      for (ProcessRequest processRequest : processRequests)
      {
        loanRequests.add(toRestLoanRequest(processRequest));
      }

      return ResponseEntity.ok(RestEntity.of(LoanRequestRestUtil.descSortByCreatedDate(loanRequests)));
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
    catch (ParseException e)
    {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(value = "/group/{groupId}")
  public ResponseEntity<RestResult> getGroupLoanRequests(@RequestParam(required = true) String startDate, @RequestParam(required = true) String endDate,
      @PathVariable String groupId, @RequestHeader(value = AUTHORIZATION, required = false) String authString)
      throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    if (StringUtils.isBlank(groupId))
    {
      throwParameterMissingException("GroupId");
    }

    try
    {
      GetGroupProcessRequests getGroupProcessRequests = new GetGroupProcessRequests(authenticationService, authorizationService, processRequestRepository,
          groupRepository);

      GetProcessRequestsByDateInput input = new GetProcessRequestsByDateInput(groupId, convertDateString(startDate), convertDateString(endDate));

      Collection<ProcessRequest> processRequests = getGroupProcessRequests.execute(input);
      Collection<RestLoanRequest> loanRequests = new ArrayList<>();
      for (ProcessRequest processRequest : processRequests)
      {
        loanRequests.add(toRestLoanRequest(processRequest));
      }

      return ResponseEntity.ok(RestEntity.of(LoanRequestRestUtil.descSortByCreatedDate(loanRequests)));
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
    catch (ParseException e)
    {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(value = "/processInstanceId/{processInstanceId}")
  public ResponseEntity<RestResult> getRequestByProcessInstanceId(@PathVariable String processInstanceId) throws UseCaseException
  {
    if (StringUtils.isBlank(processInstanceId))
    {
      throwParameterMissingException(processInstanceId);
    }
    GetProcessRequestByProcessInstanceId useCase = new GetProcessRequestByProcessInstanceId(authenticationService, authorizationService,
        processRequestRepository, loanContractRequestRepository);
    ProcessRequest processRequest = useCase.execute(processInstanceId);

    if (null == processRequest)
    {
      return ResponseEntity.ok((RestResult) Collections.emptyList());
    }
    RestLoanRequest restLoanRequest = new RestLoanRequest();
    if (processRequest.getId() == null)
    {
      restLoanRequest.setProductCategory(processRequest.getProcessTypeId().getId());
    }
    else
    {
      restLoanRequest = toRestLoanRequest(processRequest);
    }

    return RestResponse.success(Collections.singletonList(restLoanRequest));
  }

  @PostMapping
  public ResponseEntity<RestResult> createLoanRequest(@RequestBody RestLoanRequest request,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    try
    {
      if (request.getProductCategory().contains("Loan"))
      {
        validateInput(request);
      }
    }
    catch (IllegalArgumentException e)
    {
      return RestResponse.badRequest(e.getMessage());
    }

    try
    {
      CreateProcessRequest createProcessRequest = new CreateProcessRequest(authenticationService, authorizationService, tenantIdProvider,
          processRequestRepository, groupRepository, processTypeRepository);

      CreateProcessRequestInput input = new CreateProcessRequestInput(request.getBranchNumber(), request.getUserId(), request.getProductCategory());

      Map<String, Serializable> parameters = new HashMap<>();

      parameters.put(LoanRequestConstants.REGISTER_NUMBER, request.getRegisterNumber());
      parameters.put(LoanRequestConstants.CIF_NUMBER, request.getCifNumber());
      parameters.put(LoanRequestConstants.CHANNEL, request.getChannel());
      parameters.put(LoanRequestConstants.INCOME_TYPE, request.getIncomeType());
      parameters.put(LoanRequestConstants.INCOME_BEFORE_TAX, request.getIncomeBeforeTax());
      parameters.put(LoanRequestConstants.AMOUNT, request.getAmount());
      parameters.put(LoanRequestConstants.ANNUAL_INTEREST_RATE, request.getAnnualInterestRate());
      parameters.put(LoanRequestConstants.TERM, request.getTerm());
      parameters.put(LoanRequestConstants.PHONE_NUMBER, request.getPhoneNumber());
      parameters.put(LoanRequestConstants.EMAIL, request.getEmail());
      parameters.put(LoanRequestConstants.PURPOSE, request.getPurpose());
      parameters.put(LoanRequestConstants.HAS_MORTGAGE, request.getHasMortgage());
      parameters.put(LoanRequestConstants.FIRST_PAYMENT_DATE, request.getFirstPaymentDate());
      parameters.put(LoanRequestConstants.REPAYMENT_TYPE, request.getRepaymentType());
      parameters.put(LoanRequestConstants.MONTHLY_REPAYMENT, request.getMonthlyRepayment());
      parameters.put(LoanRequestConstants.BRANCH_NUMBER, request.getBranchNumber());
      parameters.put(LoanRequestConstants.FULL_NAME, request.getFullName());
      parameters.put(LoanRequestConstants.BORROWER_TYPE, request.getBorrowerType());
      parameters.put(BpmModuleConstants.LOAN_PRODUCT, request.getLoanProduct());
      parameters.put(BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION, request.getLoanProductDescription());

      input.setParameters(parameters);

      CreateProcessRequestOutput output = createProcessRequest.execute(input);
      String internetBankUserName = environment.getProperty(INTERNET_BANK_USER_PROPERTY);

      String userId = request.getUserId();

      if (!userId.equalsIgnoreCase(internetBankUserName) && !userId.equals(PREVIUOS_EBANK_USER_NAME))
      {
        UpdateAssignedUserInput updateAssignedUserInput = new UpdateAssignedUserInput(output.getProcessRequestId(), authenticationService.getCurrentUserId());
        UpdateAssignedUser updateAssignedUser = new UpdateAssignedUser(authenticationService, authorizationService, processRequestRepository);
        updateAssignedUser.execute(updateAssignedUserInput);
      }

      return RestResponse.success(output);
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @PostMapping("/contract")
  public ResponseEntity<RestResult> createLoanContractRequest(@RequestBody RestLoanRequest request,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    try
    {
      CreateProcessRequest createProcessRequest = new CreateProcessRequest(authenticationService, authorizationService, tenantIdProvider,
          processRequestRepository, groupRepository, processTypeRepository);

      CreateProcessRequestInput input = new CreateProcessRequestInput(request.getBranchNumber(), request.getUserId(), request.getProductCategory());

      input.setParameters(new HashMap<>());

      CreateProcessRequestOutput output = createProcessRequest.execute(input);

      return RestResponse.success(output);
    }
    catch (RuntimeException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(e.getMessage());
    }
  }

  @ApiOperation("Update process request parameters")
  @PatchMapping("/parameters/{requestId}")
  public ResponseEntity<RestResult> updateParameters(@PathVariable String requestId, @RequestBody Map<String, Serializable> parameters,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    if (null == parameters)
    {
      throw new BpmInvalidArgumentException(REQUEST_BODY_NULL_CODE, REQUEST_BODY_NULL_MESSAGE);
    }
    if (requestId == null || StringUtils.isBlank(requestId))
    {
      throw new BpmInvalidArgumentException(PROCESS_REQUEST_ID_NULL_CODE, PROCESS_REQUEST_ID_NULL_MESSAGE);
    }

    UpdateRequestParametersInput input = new UpdateRequestParametersInput(requestId, parameters);
    UpdateRequestParameters updateRequestParameters = new UpdateRequestParameters(authenticationService, authorizationService, processRequestRepository);
    UpdateRequestParametersOutput output = updateRequestParameters.execute(input);

    return RestResponse.success(output);
  }

  @ApiOperation("Update assigned user id")
  @PatchMapping("{requestId}/assigned-user/{userId}")
  public ResponseEntity<RestResult> updateAssignedUser(@PathVariable String requestId, @PathVariable String userId,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    if (StringUtils.isBlank(requestId) || StringUtils.isBlank(userId))
    {
      String errorCode = "BPMS040";
      throw new BpmInvalidArgumentException(errorCode, "Invalid process request id or user id!");
    }

    UpdateAssignedUser updateAssignedUser = new UpdateAssignedUser(authenticationService, authorizationService, processRequestRepository);
    UpdateAssignedUserInput input = new UpdateAssignedUserInput(requestId, userId);
    UpdateAssignedUserOutput output = updateAssignedUser.execute(input);

    GetProcessRequest getProcessRequest = new GetProcessRequest(authenticationService, authorizationService, processRequestRepository);
    ProcessRequest processRequest = getProcessRequest.execute(requestId);

    try
    {
      //if process request is ebank channel, then send email
      if (processRequest.getParameters().get("channel").equals("Internet bank"))
      {
        LOGGER.info("#########  Sending Email , Request Number = [{}]", requestId);

        SendEmailAsAssignedRequest sendEmailAsAssignedRequest = new SendEmailAsAssignedRequest(emailService);

        String productName = getProductName(processRequest.getProcessTypeId().getId());
        String customerName = (String) processRequest.getParameters().get("fullName");

        String subject = productName + " - " + requestId + " - " + customerName;
        String reason = "";
        String explanation = "";
        String state = getStateOfProcess(processRequest.getState().toString());
        String emailRecipient = getEmailAddressByUserId(userId);
        String nameOfSender = "Internet Bank";
        String sentFrom = getEmailAddressByUserId(authenticationService.getCurrentUserId());

        String templateName = "send-email-internet-bank.ftl";

        LOGGER.info("#########  Sending Email To: [{}] ", emailRecipient);

        sendEmailAsAssignedRequest.execute(
            new SendEmailAsAssignedRequestInput(subject, emailRecipient, nameOfSender, state, reason, sentFrom, explanation, templateName));

        LOGGER.info("#########  Finished Sending Email..");
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Email was not sent = [{}]", e.getMessage());
    }

    return RestResponse.success(output);
  }

  @ApiOperation("Searching loan requests by depending on person number.")
  @PostMapping("/person-number/")
  public ResponseEntity<RestResult> getByPersonNumber(@RequestBody String personNumber,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString) throws UseCaseException, BpmInvalidArgumentException
  {
    verifyBasicAuthorization(authString);

    try
    {
      personNumber = URLDecoder.decode(personNumber, StandardCharsets.UTF_8.name());
    }
    catch (UnsupportedEncodingException e)
    {
      LOGGER.error(e.getMessage(), e);
    }

    if (StringUtils.isBlank(personNumber))
    {
      String errorCode = "BPMS048";
      throw new BpmInvalidArgumentException(errorCode, "Invalid customer number!");
    }

    GetProcessRequestBySearchKey getProcessRequestBySearchKey = new GetProcessRequestBySearchKey(authenticationService, authorizationService,
        processRequestRepository, groupRepository);

    GetProcessRequestBySearchKeyInput getProcessRequestByPersonNumberInput = new GetProcessRequestBySearchKeyInput(personNumber,
        BpmModuleConstants.ALL_LOAN_REQUEST, "", "");
    GetProcessRequestsOutput output = getProcessRequestBySearchKey.execute(getProcessRequestByPersonNumberInput);
    Collection<ProcessRequest> processRequests = output.getProcessRequests();

    Collection<RestLoanRequest> loanRequests = new ArrayList<>();

    for (ProcessRequest processRequest : processRequests)
    {
      loanRequests.add(toRestLoanRequest(processRequest));
    }
    return RestResponse.success(loanRequests);
  }

  @ApiOperation("Lists all process requests by assigned user id")
  @GetMapping("/users/{assignedUserId:.+}")
  public ResponseEntity<RestResult> getByAssignedUser(@PathVariable String assignedUserId,
      @RequestHeader(value = AUTHORIZATION, required = false) String authString, @RequestParam() String startDate, @RequestParam() String endDate)
      throws UseCaseException, BpmInvalidArgumentException, ParseException
  {
    verifyBasicAuthorization(authString);

    if (StringUtils.isBlank(assignedUserId))
    {
      String errorCode = "BPMS040";
      throw new BpmInvalidArgumentException(errorCode, "Invalid process request id!");
    }

    GetProcessRequestsByAssignedUserId getProcessRequestsByAssignedUserId = new GetProcessRequestsByAssignedUserId(authenticationService, authorizationService,
        processRequestRepository);
    GetProcessRequestsByAssignedUserIdInput input = new GetProcessRequestsByAssignedUserIdInput(assignedUserId, convertDateString(startDate),
        convertDateString(endDate));
    GetProcessRequestsOutput output = getProcessRequestsByAssignedUserId.execute(input);
    Collection<ProcessRequest> processRequests = output.getProcessRequests();

    Collection<RestLoanRequest> loanRequests = new ArrayList<>();
    for (ProcessRequest processRequest : processRequests)
    {
      loanRequests.add(toRestLoanRequest(processRequest));
    }

    return RestResponse.success(loanRequests);
  }

  @ApiOperation("Gets loan requests as excel file")
  @PostMapping("/report/{topHeader}/{searchKey}/{groupId}")
  public ResponseEntity getExcelReport(@PathVariable String topHeader, @PathVariable String searchKey, @PathVariable String groupId,
      @RequestBody Collection<RestLoanRequest> loanRequest) throws UseCaseException, BpmInvalidArgumentException, UnsupportedEncodingException
  {
    if (loanRequest.isEmpty() || StringUtils.isBlank(topHeader) || StringUtils.isBlank(groupId))
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

    // Convert Rest Loan Request into Process Request
    Collection<ProcessRequest> processRequests = new ArrayList<>();
    loanRequest.forEach(request -> processRequests.add(toProcessRequest(request)));

    GetProcessRequestsAsExcel getProcessRequestExcel = new GetProcessRequestsAsExcel(authorizationService, authenticationService);
    GetProcessRequestExcelInput getProcessRequestExcelInput = new GetProcessRequestExcelInput(topHeader, searchKey, groupId, formattedDate, processRequests);

    byte[] excelReportAsByte = getProcessRequestExcel.execute(getProcessRequestExcelInput);

    if (null == excelReportAsByte)
    {
      RestResponse.notFound(MessageConstants.NOT_FOUND);
    }

    ByteArrayResource resource = new ByteArrayResource(excelReportAsByte);

    String processTypeName = BpmModuleConstants.PROCESS_REQUEST_TYPE.get(topHeader).replaceAll("\\s", "_");
    String fileName = URLEncoder.encode(processTypeName + "_", StandardCharsets.UTF_8.name());

    return ResponseEntity.ok().contentLength(resource.contentLength()).contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
        .header("Content-Disposition", "attachment; charset=utf-8; filename=\"" + fileName + formattedDate + ".xlsx\"").body(resource);
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

      Map<String, Object> authenticatedUser = isAuthenticatedUser(authenticationService, authString);
      boolean isAuthenticated = (boolean) authenticatedUser.get("isAuthenticated");

      if (!isAuthenticated)
      {
        String message = authenticatedUser.containsKey("message") ? String.valueOf(authenticatedUser.get("message")) : "";
        String errorCode = "BPMS039";
        throw new BpmInvalidArgumentException(errorCode, ERR_MSG_INVALID_BASIC_AUTHORIZATION + " " + message);
      }
    }
    return null;
  }

  private String getEmailAddressByUserId(String userId) throws UseCaseException
  {
    User user = userRepository.findById(UserId.valueOf(userId));

    if (user == null)
    {
      throw new UseCaseException("User not found!");
    }

    ContactInfo contactInfo = user.getContactInfo();

    if (contactInfo == null)
    {
      throw new UseCaseException("User" + userId + "does not have contact information!");
    }

    String email = contactInfo.getEmail();

    if (StringUtils.isBlank(email))
    {
      throw new UseCaseException(userId + "'s email is blank!");
    }

    return email;
  }

  private String getStateOfProcess(String state)
  {
    Map<String, String> stateMap = new HashMap<>();

    stateMap.put("NEW", "ШИНЭ");
    stateMap.put("STARTED", "СУДЛАГДАЖ БАЙНА");
    stateMap.put("ORG_REJECTED", "БАНК ТАТГАЛЗСАН");
    stateMap.put("CUST_REJECTED", "ХАРИЛЦАГЧ ТАТГАЛЗСАН");
    stateMap.put("CONFIRMED", "БАТЛАГДСАН");
    stateMap.put("RETURNED", "БУЦААСАН");
    stateMap.put("REJECTED", "ЗАХИРАЛ-БАНК ТАТГАЛЗСАН");
    stateMap.put("COMPLETED", "ДУУССАН");

    String stateMnValue = stateMap.get(state);
    if (StringUtils.isBlank(stateMnValue))
    {
      return state;
    }
    return stateMnValue;
  }

  private RestLoanRequest toRestLoanRequest(ProcessRequest processRequest) throws UseCaseException
  {RestLoanRequest restLoanRequest = new RestLoanRequest();
    restLoanRequest.setCreatedDate(toDateString(processRequest.getCreatedTime()));
    restLoanRequest.setId(processRequest.getId().getId());
    restLoanRequest.setInstanceId(processRequest.getProcessInstanceId());
    restLoanRequest.setProductCategory(processRequest.getProcessTypeId().getId());
    restLoanRequest.setUserId(processRequest.getRequestedUserId());
    restLoanRequest.setState(processRequest.getState().toString());
    restLoanRequest.setLoanProduct((String) processRequest.getParameterValue(LOAN_PRODUCT));
    restLoanRequest.setLoanProductDescription((String) processRequest.getParameterValue(LOAN_PRODUCT_DESCRIPTION));

    restLoanRequest.setRegisterNumber((String) processRequest.getParameterValue(LoanRequestConstants.REGISTER_NUMBER));
    restLoanRequest.setCifNumber((String) processRequest.getParameterValue(LoanRequestConstants.CIF_NUMBER));
    restLoanRequest.setChannel((String) processRequest.getParameterValue(LoanRequestConstants.CHANNEL));
    restLoanRequest.setBranchNumber((String) processRequest.getParameterValue(LoanRequestConstants.BRANCH_NUMBER));
    restLoanRequest.setEmail((String) processRequest.getParameterValue(LoanRequestConstants.EMAIL));
    /* Internet bank channel requests have long data type phone numbers. toString() method is used here.*/
    restLoanRequest.setPhoneNumber(getValidString(processRequest.getParameterValue(LoanRequestConstants.PHONE_NUMBER)));

    restLoanRequest.setIncomeType((String) processRequest.getParameterValue(INCOME_TYPE));
    restLoanRequest.setPurpose((String) processRequest.getParameterValue(LoanRequestConstants.PURPOSE));
    restLoanRequest.setRepaymentType((String) processRequest.getParameterValue(LoanRequestConstants.REPAYMENT_TYPE));

    if (null == processRequest.getParameterValue(LoanRequestConstants.AMOUNT))
    {
      String processTypeId = String.valueOf(processRequest.getProcessTypeId().getId());
      if (processTypeId.equals(BNPL_PROCESS_TYPE_ID))
      {
        if (null != processRequest.getParameters().get(INVOICE_AMOUNT_75))
        {
          restLoanRequest.setAmount(String.valueOf(processRequest.getParameters().get(INVOICE_AMOUNT_75)));
        }
        else {
          restLoanRequest.setAmount(String.valueOf(processRequest.getParameters().get(BNPL_LOAN_AMOUNT)));
        }
      }
      else if (processTypeId.equals(DIRECT_ONLINE_PROCESS_TYPE_ID) || processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID) || processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
          if (null != processRequest.getParameters().get(FIXED_ACCEPTED_LOAN_AMOUNT_STRING))
          {
            restLoanRequest.setAmount(String.valueOf(processRequest.getParameters().get(FIXED_ACCEPTED_LOAN_AMOUNT_STRING)));
          }
          else
          {
            restLoanRequest.setAmount(String.valueOf(processRequest.getParameters().get(GRANT_LOAN_AMOUNT_STRING)));
          }
      }
      else
      {
        restLoanRequest.setAmount(String.valueOf(processRequest.getParameterValue(LoanRequestConstants.AMOUNT)));
      }
    }
    else
    {
      restLoanRequest.setAmount(String.valueOf(processRequest.getParameterValue(LoanRequestConstants.AMOUNT)));
    }

    if (isValidString(processRequest, INCOME_BEFORE_TAX))
    {
      restLoanRequest.setIncomeBeforeTax(new BigDecimal(convertToString(processRequest, INCOME_BEFORE_TAX)));
    }

    if (isValidString(processRequest, ANNUAL_INTEREST_RATE))
    {
      restLoanRequest.setAnnualInterestRate(new BigDecimal(convertToString(processRequest, ANNUAL_INTEREST_RATE)));
    }

    if (isValidString(processRequest, MONTHLY_REPAYMENT))
    {
      restLoanRequest.setMonthlyRepayment(new BigDecimal(convertToString(processRequest, MONTHLY_REPAYMENT)));
    }

    if (isValidString(processRequest, HAS_MORTGAGE))
    {
      restLoanRequest.setHasMortgage(Boolean.parseBoolean(convertToString(processRequest, HAS_MORTGAGE)));
    }

    restLoanRequest.setFirstPaymentDate(String.valueOf(processRequest.getParameterValue(LoanRequestConstants.FIRST_PAYMENT_DATE)));

    if (isValidString(processRequest, TERM))
    {
      restLoanRequest.setTerm(Integer.parseInt(convertToString(processRequest, TERM)));
    }

    if (isValidString(processRequest, FULL_NAME))
    {
      restLoanRequest.setFullName(String.valueOf(processRequest.getParameterValue(LoanRequestConstants.FULL_NAME)));
    }

    if (null != processRequest.getParameterValue(LoanRequestConstants.INCOME_TYPE))
    {
      restLoanRequest.setIncomeType(String.valueOf(processRequest.getParameterValue(LoanRequestConstants.INCOME_TYPE)));
    }

    if (null != processRequest.getParameterValue(LoanRequestConstants.BORROWER_TYPE))
    {
      restLoanRequest.setBorrowerType(String.valueOf(processRequest.getParameterValue(LoanRequestConstants.BORROWER_TYPE)));
    }

    restLoanRequest.setProcessTypeId(getProcessTypeName(processRequest.getProcessTypeId().getId()));

    return restLoanRequest;
  }

  private String getProcessTypeName(String processTypeId) throws UseCaseException
  {
    GetProcessType getProcessType = new GetProcessType(authenticationService, authorizationService, processTypeRepository);
    ProcessType processType = getProcessType.execute(processTypeId);
    return processType.getName();
  }

  private ProcessRequest toProcessRequest(RestLoanRequest restLoanRequest)
  {
    ProcessRequestId processRequestId = new ProcessRequestId(restLoanRequest.getId());
    ProcessTypeId processTypeId = new ProcessTypeId(restLoanRequest.getProductCategory());
    GroupId groupId = new GroupId(restLoanRequest.getBranchNumber());
    final String userId = restLoanRequest.getUserId();
    final String createdDate = restLoanRequest.getCreatedDate();
    final String state = restLoanRequest.getState();

    LocalDateTime localDateTime = null;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    if (!StringUtils.isBlank(createdDate))
    {
      localDateTime = LocalDateTime.parse(createdDate, formatter);
    }

    Map<String, Serializable> parameters = convertToProcessRequestParameters(restLoanRequest);

    return new ProcessRequest(processRequestId, processTypeId, groupId, userId, localDateTime, ProcessRequestState.valueOf(state), parameters);
  }

  private Map<String, Serializable> convertToProcessRequestParameters(RestLoanRequest restLoanRequest)
  {
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.put(LOAN_PRODUCT, restLoanRequest.getLoanProduct());
    parameters.put(LOAN_PRODUCT_DESCRIPTION, restLoanRequest.getLoanProductDescription());
    parameters.put(REGISTER_NUMBER, restLoanRequest.getRegisterNumber());
    parameters.put(CIF_NUMBER, restLoanRequest.getCifNumber());
    parameters.put(CHANNEL, restLoanRequest.getChannel());
    parameters.put(BRANCH_NUMBER, BRANCH_NUMBER);
    parameters.put(EMAIL, restLoanRequest.getEmail());
    parameters.put(PHONE_NUMBER, restLoanRequest.getPhoneNumber());
    parameters.put(INCOME_TYPE, restLoanRequest.getIncomeType());
    parameters.put(PURPOSE, restLoanRequest.getPurpose());
    parameters.put(REPAYMENT_TYPE, restLoanRequest.getRepaymentType());
    parameters.put(AMOUNT, restLoanRequest.getAmount());
    parameters.put(INCOME_BEFORE_TAX, restLoanRequest.getIncomeBeforeTax());
    parameters.put(ANNUAL_INTEREST_RATE, restLoanRequest.getAnnualInterestRate());
    parameters.put(MONTHLY_REPAYMENT, restLoanRequest.getMonthlyRepayment());
    parameters.put(HAS_MORTGAGE, restLoanRequest.getHasMortgage());
    parameters.put(FIRST_PAYMENT_DATE, restLoanRequest.getFirstPaymentDate());
    parameters.put(TERM, restLoanRequest.getTerm());
    parameters.put(FULL_NAME, restLoanRequest.getFullName());
    return parameters;
  }

  private Collection<ProcessRequest> filterRequestsExcludingByGroupId(Collection<ProcessRequest> processRequests, String group) throws UseCaseException
  {
    Collection<ProcessRequest> filteredProcessRequests = new ArrayList<>();

    for (ProcessRequest processRequest : processRequests)
    {
      if (processRequest.getGroupId() == null)
      {
        throw new UseCaseException(PROCESS_REQUEST_BLANK_GROUP_ID_ERROR_CODE, PROCESS_REQUEST_BLANK_GROUP_ID_ERROR_MESSAGE);
      }

      if (!processRequest.getGroupId().getId().equals(group))
      {
        filteredProcessRequests.add(processRequest);
      }
    }

    return filteredProcessRequests;
  }

  private List<String> getPermissions() throws UseCaseException
  {
    try
    {
      String userId = authenticationService.getCurrentUserId();

      List<Membership> memberships = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(userId));

      if (memberships.isEmpty())
      {
        throw new UseCaseException(AIM_USER_BLANK_MEMBERSHIP_ERROR_CODE, AIM_USER_BLANK_MEMBERSHIP_ERROR_MESSAGE);
      }

      List<String> permissions = new ArrayList<>();

      for (Membership membership : memberships)
      {
        RoleId roleId = membership.getRoleId();
        Role role = roleRepository.findById(roleId);

        for (Permission permission : role.getPermissions())
        {
          String permissionString = permission.getPermissionString();

          if (!permissions.contains(permissionString))
          {
            permissions.add(permissionString);
          }
        }
      }
      return permissions;
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private String getProductName(String processTypeId)
  {
    ProcessType processType = processTypeRepository.findById(ProcessTypeId.valueOf(processTypeId));
    return processType.getName();
  }

  private String toDateString(LocalDateTime createdTime)
  {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(createdTime);
  }

  private void validateInput(RestLoanRequest request)
  {
    if (StringUtils.isBlank(request.getChannel()))
    {
      throwParameterMissingException(LoanRequestConstants.CHANNEL);
    }

    if (StringUtils.isBlank(request.getUserId()))
    {
      throwParameterMissingException(LoanRequestConstants.USER_ID);
    }

    if (StringUtils.isBlank(request.getRegisterNumber()))
    {
      throwParameterMissingException(LoanRequestConstants.REGISTER_NUMBER);
    }

    if (isDigits(request.getRegisterNumber()))
    {
      validateOrganizationRegNumber(request.getRegisterNumber());
    }
    else
    {
      ValidationUtils.validateRegisterNumber(request.getRegisterNumber());
    }

    if (StringUtils.isBlank(request.getProductCategory()))
    {
      throwParameterMissingException(LoanRequestConstants.PRODUCT_CATEGORY);
    }

    if (StringUtils.isBlank(request.getBranchNumber()))
    {
      throwParameterMissingException(LoanRequestConstants.BRANCH_NUMBER);
    }

    if (StringUtils.isBlank(request.getIncomeType()) && StringUtils.isBlank(request.getBorrowerType()))
    {
      String message = LoanRequestConstants.INCOME_TYPE + " OR " + LoanRequestConstants.BORROWER_TYPE;
      throwParameterMissingException(message);
    }

    if (null == request.getAmount())
    {
      throwParameterMissingException(LoanRequestConstants.AMOUNT);
    }

    if (request.getAmount().compareTo(String.valueOf(BigDecimal.ZERO)) < 0)
    {
      throw new IllegalArgumentException(ERR_MSG_NEGATIVE_AMOUNT);
    }
  }

  private void throwParameterMissingException(String parameterName)
  {
    throw new IllegalArgumentException(parameterName + " is missing!");
  }

  private int stringToInt(Serializable value)
  {
    if (null == value || value.equals("null"))
    {
      return 0;
    }
    return Integer.parseInt(String.valueOf(value));
  }

  private BigDecimal stringToBigDecimal(Serializable value)
  {
    if (null == value || value.equals("null"))
    {
      return BigDecimal.ZERO;
    }
    String stringValue = String.valueOf(value);
    return new BigDecimal(stringValue.replace(",", ""));
  }

  private Boolean stringToBoolean(Serializable value)
  {
    if (null == value || value.equals("null"))
    {
      return false;
    }
    String stringValue = String.valueOf(value);
    return stringValue.equals("1");
  }

  private boolean isValidString(ProcessRequest processRequest, String parameterName)
  {
    final String value = String.valueOf(processRequest.getParameterValue(parameterName));
    return !StringUtils.isBlank(value) && !value.equals(NULL_STRING);
  }

  private String convertToString(ProcessRequest processRequest, String parameterName)
  {
    final String stringValue = String.valueOf(processRequest.getParameterValue(parameterName));
    return stringValue.replace(",", "");
  }

  private Date convertDateString(String dateString) throws ParseException
  {
    String dateFormat = "E MMM dd yyyy";
    return new SimpleDateFormat(dateFormat).parse(dateString);
  }
}
