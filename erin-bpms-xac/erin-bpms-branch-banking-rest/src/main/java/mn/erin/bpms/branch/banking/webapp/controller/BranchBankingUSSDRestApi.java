package mn.erin.bpms.branch.banking.webapp.controller;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.CHO_BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.FAILED;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.REGISTERED_BRANCH;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TIME_OUT;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ID_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ID_IS_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ONE_OF_CIF_PHONE_IS_REQUIRED_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_ONE_OF_CIF_PHONE_IS_REQUIRED_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_OTP_EXPIRED_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_OTP_EXPIRED_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_OTP_FAILED_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_OTP_FAILED_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REQUEST_BODY_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CHO_BRANCH;
import static mn.erin.domain.bpm.BpmModuleConstants.ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANCE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.provider.OTPProvider;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.UserOtpSend;
import mn.erin.domain.bpm.usecase.branch_banking.UserOtpSendInput;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.GetUserInfoFromUSSD;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.GetUserInfoFromUSSDInput;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.MakeUserCancelOnUSSD;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.MakeUserEndorseOnUSSD;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.UpdateUserInfoUSSD;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.UpdateUserUSSDInput;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.UserStatusChange;
import mn.erin.domain.bpm.usecase.branch_banking.ussd.UserStatusChangeInput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

/**
 * @author Chinbat
 */

@RestController @RequestMapping(value = "/USSD/instanceId/{instanceId}", name = "Provides Branch banking USSD API")
public class BranchBankingUSSDRestApi
{
  private final BranchBankingService branchBankingService;
  private final AuthenticationService authenticationService;
  private final OTPProvider otpProvider;
  private final Environment environment;

  private final MembershipRepository membershipRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(BranchBankingUSSDRestApi.class);

  public BranchBankingUSSDRestApi(BranchBankingService branchBankingService, AuthenticationService authenticationService,
      OTPProvider otpProvider, Environment environment, MembershipRepository membershipRepository)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service cannot be null!");
    this.authenticationService = authenticationService;
    this.otpProvider = otpProvider;
    this.environment = environment;
    this.membershipRepository = membershipRepository;
  }

  @GetMapping(value = "/searchUser")
  public ResponseEntity<RestResult> searchUser(@RequestParam(required = false) String customerId, @RequestParam(required = false) String phoneNumber,
      @PathVariable String instanceId) throws BpmInvalidArgumentException, UseCaseException
  {

    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(customerId) && StringUtils.isBlank(phoneNumber))
    {
      throw new BpmInvalidArgumentException(BB_ONE_OF_CIF_PHONE_IS_REQUIRED_CODE, BB_ONE_OF_CIF_PHONE_IS_REQUIRED_MESSAGE);
    }

    GetUserInfoFromUSSD getUserFromUSSD = new GetUserInfoFromUSSD(branchBankingService);
    return RestResponse.success(getUserFromUSSD.execute(new GetUserInfoFromUSSDInput(customerId, phoneNumber, "", instanceId)));
  }

  @PostMapping(value = "/updateOrCreate")
  public ResponseEntity<RestResult> updateOrCreateUser(@RequestBody Map<String, Object> form, @PathVariable String instanceId)
      throws UseCaseException, BpmInvalidArgumentException, AimRepositoryException
  {
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(getValidString(form.get(REGISTERED_BRANCH))))
    {
      String currentBranch = getCurrentUserBranch(authenticationService, membershipRepository);
      if (currentBranch.equals(CHO_BRANCH))
      {
        form.put(REGISTERED_BRANCH, environment.getProperty(CHO_BRANCH_NUMBER));
      }
      else
      {
        form.put(REGISTERED_BRANCH, Integer.parseInt(currentBranch));
      }
    }
    Objects.requireNonNull(form, "Form cannot be null");
    UpdateUserInfoUSSD updateUserUSSD = new UpdateUserInfoUSSD(branchBankingService);

    return RestResponse.success(updateUserUSSD.execute(new UpdateUserUSSDInput(form, "MN", instanceId)));
  }

  @GetMapping(value = "/userOtpSend/{destination}")
  public ResponseEntity<RestResult> userOtpSend(@PathVariable String destination, @PathVariable String instanceId)
      throws UseCaseException
  {
    LOGGER.info("##### USSD USER OTP SEND REST API, INSTANCE ID = [{}]", instanceId);
    UserOtpSend userOtpSend = new UserOtpSend(branchBankingService, authenticationService, otpProvider);
    Boolean p = userOtpSend.execute(new UserOtpSendInput("P", destination, instanceId));
    LOGGER.info("##### USSD USER OTP SEND REST API, OUTPUT = [{}] \nINSTANCE ID = [{}]", p, instanceId);
    return RestResponse.success(p);
  }

  @GetMapping(value = "/userStatusChange")
  public ResponseEntity<RestResult> userStatusChange(@PathVariable String instanceId, @RequestParam String mobile, @RequestParam String type)
      throws UseCaseException, BpmInvalidArgumentException
  {
    LOGGER.info("##### USSSD USER STATUS CHANGE REST API, INSTANCE ID = [{}]", instanceId);
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    UserStatusChange userStatusChange = new UserStatusChange(branchBankingService);
    Map<String, Object> output = userStatusChange.execute(new UserStatusChangeInput(mobile, instanceId, type));
    LOGGER.info("##### USSSD USER STATUS CHANGE REST API, OUTPUT = [{}] \nINSTANCE ID = [{}]", output, instanceId);
    return RestResponse.success(output);
  }

  @PostMapping(value = "/verifyOtpCode")
  public ResponseEntity<RestResult> verifyOtpCode(@RequestBody String verificationCode, @PathVariable String instanceId)
      throws BpmInvalidArgumentException, BpmServiceException
  {
    LOGGER.info("##### USSD VERIFY OTP CODE REST API, INSTANCE ID = [{}]", instanceId);
    if (StringUtils.isBlank(verificationCode))
    {
      throw new BpmInvalidArgumentException(REQUEST_BODY_NULL_CODE, REQUEST_BODY_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    LOGGER.info("##### VERIFYING OTP CODE WITH THE GIVEN VERIFICATION CODE =[{}]", verificationCode);
    String response = otpProvider.removeIfEqual(authenticationService.getCurrentUserId(), verificationCode.replace("\\s", ""));

    LOGGER.info("##### USSD VERIFY OTP CODE REST API, OUTPUT =[{}] \nINSTANCE ID = [{}]", response, instanceId);
    if (response.equals(TIME_OUT))
    {
      throw new BpmServiceException(BB_OTP_EXPIRED_CODE, BB_OTP_EXPIRED_MESSAGE);
    }
    else if (response.equals(FAILED))
    {
      throw new BpmServiceException(BB_OTP_FAILED_CODE, BB_OTP_FAILED_MESSAGE);
    }

    return RestResponse.success();
  }

  @PostMapping(value = "/ussdUserEndorse")
  public ResponseEntity<RestResult> makeUserEndorse(@RequestBody Map<String, String> input, @PathVariable String instanceId)
      throws BpmInvalidArgumentException, UseCaseException
  {
    if (StringUtils.isBlank(input.get(ID)))
    {
      throw new BpmInvalidArgumentException(BB_ID_IS_NULL_CODE, BB_ID_IS_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
    input.put(INSTANCE_ID, instanceId);
    LOGGER.info("##### MAKE USER ENDORSE ON USSD SEND REST API, INSTANCE ID = [{}]", instanceId);
    MakeUserEndorseOnUSSD makeUserEndorseOnUSSD = new MakeUserEndorseOnUSSD(branchBankingService);
    boolean result = makeUserEndorseOnUSSD.execute(input);
    return RestResponse.success(result);
  }

  @PostMapping(value = "/ussdUserCancel")
  public ResponseEntity<RestResult> makeUserCancel(@RequestBody Map<String, String> input, @PathVariable String instanceId)
      throws BpmInvalidArgumentException, UseCaseException
  {
    if (StringUtils.isBlank(input.get(ID)))
    {
      throw new BpmInvalidArgumentException(BB_ID_IS_NULL_CODE, BB_ID_IS_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(instanceId))
    {
      throw new BpmInvalidArgumentException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
    input.put(INSTANCE_ID, instanceId);
    LOGGER.info("##### MAKE USER CANCEL ON USSD SEND REST API, INSTANCE ID = [{}]", instanceId);
    MakeUserCancelOnUSSD makeUserCancelOnUSSD = new MakeUserCancelOnUSSD(branchBankingService);
    boolean result = makeUserCancelOnUSSD.execute(input);
    return RestResponse.success(result);
  }

  private String getCurrentUserBranch(AuthenticationService authenticationService, MembershipRepository membershipRepository) throws AimRepositoryException
  {
    UserId userId = new UserId(authenticationService.getCurrentUserId());
    return membershipRepository.findByUserId(userId).getGroupId().getId();
  }
}
