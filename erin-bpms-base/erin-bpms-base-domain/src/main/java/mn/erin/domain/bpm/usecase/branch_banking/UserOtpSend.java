package mn.erin.domain.bpm.usecase.branch_banking;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.provider.OTPProvider;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_CHANNEL_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_CHANNEL_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_CONTENT_MESSAGE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_CONTENT_MESSAGE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DESTINATION_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_DESTINATION_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

public class UserOtpSend extends AbstractUseCase<UserOtpSendInput, Object>
{
  private final BranchBankingService branchBankingService;
  private final AuthenticationService authenticationService;
  private final OTPProvider otpProvider;
  private static final Logger LOGGER = LoggerFactory.getLogger(UserOtpSend.class);

  public UserOtpSend(BranchBankingService branchBankingService, AuthenticationService authenticationService, OTPProvider otpProvider)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService, "Branch banking service cannot be null!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required.");
    this.otpProvider = Objects.requireNonNull(otpProvider, "OTP Provider cannot be null!");
  }

  @Override
  public Boolean execute(UserOtpSendInput input) throws UseCaseException
  {
    LOGGER.info("##### USSSD USER OTP SEND USECASE, INSTANCE ID = [{}]", input.getInstanceId());
    validateInput(input);

    String destination = input.getDestination();
    String instanceId = input.getInstanceId();
    String channel = input.getChannel();

    final String OTP;

    try
    {
      OTP = generateOTP();
      LOGGER.info("##### USSD USER OTP SEND USECASE, OTP = [{}] \nINSTANCE ID = [{}]", OTP, instanceId);
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new UseCaseException("Generate otp is failed for user " + authenticationService.getCurrentUserId() + ". \n process instance id = " + instanceId);
    }
    if (StringUtils.isBlank(OTP))
    {
      throw new UseCaseException(BB_CONTENT_MESSAGE_NULL_CODE, BB_CONTENT_MESSAGE_NULL_MESSAGE);
    }
    try
    {
      return (this.branchBankingService.userOtpSend(channel, destination, OTP, instanceId));
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    catch (ParseException parseException)
    {
      throw new UseCaseException(parseException.getMessage());
    }
  }

  private void validateInput(UserOtpSendInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getChannel()))
    {
      throw new UseCaseException(BB_CHANNEL_NULL_CODE, BB_CHANNEL_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getDestination()))
    {
      throw new UseCaseException(BB_DESTINATION_NULL_CODE, BB_DESTINATION_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }
  }

  private String generateOTP() throws NoSuchAlgorithmException, UseCaseException
  {
    LOGGER.info("######### UserOtpSend before call GenerateOTP");
    GenerateOTP generateOTP = new GenerateOTP(otpProvider);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("######### UserOtpSend before call GenerateOTP with userid {}", userId);
    validateNotBlank(userId, "User id is missing when generate one time password!");

    return generateOTP.execute(userId);
  }
}
