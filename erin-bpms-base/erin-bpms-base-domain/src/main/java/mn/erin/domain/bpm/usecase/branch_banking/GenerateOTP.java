package mn.erin.domain.bpm.usecase.branch_banking;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.provider.OTPProvider;

/**
 * @author Bilguunbor
 **/

public class GenerateOTP extends AbstractUseCase<String, String>
{
  private final OTPProvider otpProvider;
  private final Random rand = SecureRandom.getInstanceStrong();
  private static final Logger LOGGER = LoggerFactory.getLogger(GenerateOTP.class);

  public GenerateOTP(OTPProvider otpProvider) throws NoSuchAlgorithmException
  {
    this.otpProvider = Objects.requireNonNull(otpProvider, "OTP Provider is required.");
  }

  @Override
  public String execute(String userId) throws UseCaseException
  {
    LOGGER.info("######### GenerateOTP beginning of usecase with userid {}", userId);
    validateNotBlank(userId, "User id is missing when generating one time password!");
    LOGGER.info("######### GenerateOTP before generate otp with userid {}", userId);
    final String OTP = generateOTP();
    LOGGER.info("######### GenerateOTP after generate otp with userid {} otp {}", userId, OTP);
    otpProvider.setOTP(userId, OTP);
    LOGGER.info("######### GenerateOTP after set otp with userid {} otp {}", userId, OTP);

    return OTP;
  }

  private String generateOTP()
  {
    int randomPin   =(int)(Math.random()*90000)+10000;
    String otp  =String.valueOf(randomPin);
    return otp;
//    int generatedOTP = this.rand.nextInt(90000) + 10000;
//    return Integer.toString(generatedOTP);
  }
}
