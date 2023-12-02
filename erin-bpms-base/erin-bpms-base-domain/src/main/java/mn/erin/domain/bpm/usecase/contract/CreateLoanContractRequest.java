package mn.erin.domain.bpm.usecase.contract;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import mn.erin.domain.aim.model.permission.AimModulePermission;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.service.TenantIdProvider;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.ProcessTypeService;
import mn.erin.domain.bpm.usecase.loan.GetAccountInfo;
import mn.erin.domain.bpm.usecase.loan.get_loan_account_info.GetLoanAccountInfo;
import mn.erin.domain.bpm.usecase.process.CreateProcessRequestInput;

import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.BPMS_LOAN_CONTRACT_REQUEST_ID_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BPMS_LOAN_CONTRACT_REQUEST_ID_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.GRANTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;

/**
 * @author Temuulen Naranbold
 */
public class CreateLoanContractRequest extends AuthorizedUseCase<CreateProcessRequestInput, String>
{
  private static final Permission permission = new AimModulePermission("CreateLoanContractRequest");
  private final LoanContractRequestRepository loanContractRequestRepository;
  private final ProcessTypeService processTypeService;
  private final TenantIdProvider tenantIdProvider;
  private final NewCoreBankingService newCoreBankingService;
  private final CaseService caseService;

  public CreateLoanContractRequest(LoanContractRequestRepository loanContractRequestRepository,
      ProcessTypeService processTypeService, TenantIdProvider tenantIdProvider,
      AuthenticationService authenticationService, AuthorizationService authorizationService,
      NewCoreBankingService newCoreBankingService, CaseService caseService)
  {
    super(authenticationService, authorizationService);
    this.loanContractRequestRepository = Objects.requireNonNull(loanContractRequestRepository, "Loan contract repository is required!");
    this.processTypeService = processTypeService;
    this.tenantIdProvider = tenantIdProvider;
    this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New core banking service is required!");
    this.caseService = caseService;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected String executeImpl(CreateProcessRequestInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);

      Map<String, Object> parameters = input.getObjectParameters();
      String processTypeId = input.getProcessTypeId();
      String accountNumber = String.valueOf(parameters.get(ACCOUNT_NUMBER));

      Map<String, Object> param;
      Map<String, String> inputParam = new HashMap<>();
      inputParam.put(PROCESS_TYPE_ID, processTypeId);
      inputParam.put(PHONE_NUMBER, String.valueOf(parameters.get(PHONE_NUMBER)));
      inputParam.put(ACCOUNT_NUMBER, accountNumber);
      param = getLoanAccountInfo(inputParam);
      param.putAll(parameters);

      BigDecimal amount = new BigDecimal(String.valueOf(param.get(GRANTED_LOAN_AMOUNT)));
      param.put(GRANTED_LOAN_AMOUNT, amount.longValue());

      Date createdDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

      List<LoanContractRequest> processRequests = loanContractRequestRepository.findByAccountId(accountNumber);
      String processInstanceId = processTypeService.startProcess(input.getProcessTypeId(), param);

      ProcessRequestId processRequestId = ProcessRequestId.valueOf(getProcessRequestId(accountNumber, processRequests));
      caseService.setCaseVariableById(processInstanceId, PROCESS_REQUEST_ID, processRequestId.getId());

      loanContractRequestRepository.create(
          processRequestId,
          processInstanceId,
          input.getProcessTypeId(),
          accountNumber,
          amount,
          createdDate,
          getUserId(input.getRequestedUserId()),
          input.getGroupNumber(),
          tenantIdProvider.getCurrentUserTenantId(),
          ProcessRequestState.STARTED.name(),
          String.valueOf(param.get(CIF_NUMBER)),
          String.valueOf(param.get("productDescription"))
      );

      return processInstanceId;
    }
    catch (IllegalArgumentException | NullPointerException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private Map<String, Object> getLoanAccountInfo(Map<String, String> input) throws UseCaseException
  {
    if (!StringUtils.equals("creditLineLoanContract", input.get(PROCESS_TYPE_ID)))
    {
      GetLoanAccountInfo getLoanAccountInfo = new GetLoanAccountInfo(newCoreBankingService);
      return getLoanAccountInfo.execute(input);
    }
    else
    {
      GetAccountInfo getAccountInfo = new GetAccountInfo(newCoreBankingService);
      return getAccountInfo.execute(input);
    }
  }

  private String getProcessRequestId(String accountId, List<LoanContractRequest> processRequests) throws UseCaseException
  {
    int requestCount;
    requestCount = processRequests.size();
    return accountId + setRequestIncrement(requestCount);
  }

  private int setRequestIncrement(int requestCounter) throws UseCaseException
  {
    if (requestCounter > 8)
    {
      throw new UseCaseException(BPMS_LOAN_CONTRACT_REQUEST_ID_ERROR_CODE, BPMS_LOAN_CONTRACT_REQUEST_ID_ERROR_MESSAGE);
    }
    else if (requestCounter == 0)
    {
      return 1;
    }
    else
    {
      requestCounter++;
      return requestCounter;
    }
  }

  private String getUserId(String userId)
  {
    return StringUtils.isBlank(userId) ? Objects.requireNonNull(authenticationService).getCurrentUserId() : userId;
  }
}
