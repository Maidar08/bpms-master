package mn.erin.domain.bpm.provider;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.bpm.model.branch_banking.OTP;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.FAILED;
import static mn.erin.domain.bpm.BpmBranchBankingConstants.TIME_OUT;

/**
 * @author Bilguunbor
 **/

public class DefaultOTPProvider implements OTPProvider
{
  private final Environment environment;
  private final Map<String, OTP> storedOTPs;
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOTPProvider.class);

  public DefaultOTPProvider(Environment environment)
  {
    this.environment = environment;
    this.storedOTPs = new ConcurrentHashMap<>();
  }

  @Override
  public void setOTP(String userId, String generatedOTP)
  {
    LOGGER.info("########### DefaultOTPProvider before set otp with userid {} and otp {}", userId, generatedOTP);
    Instant instantTime = Instant.now();
    LOGGER.info("########### DefaultOTPProvider before set otp with userid {} and otp {} instantTime {}", userId, generatedOTP, instantTime);
    OTP otp = new OTP(generatedOTP, instantTime);

    this.storedOTPs.put(userId, otp);
    LOGGER.info("########### DefaultOTPProvider after set otp with userid {} and otp {}", userId, generatedOTP);
  }

  @Override
  public String removeIfEqual(String userId, String verificationCode)
  {
    OTP storedOTP = this.storedOTPs.get(userId);

    if (storedOTP == null)
    {
      return FAILED;
    }

    Instant instant = Instant.now();
    Duration duration = Duration.between(storedOTP.getInstantTime(), instant);

    String xacOTPtimeOut = environment.getProperty("xacOTPtimeOut", "60");


    if (duration.getSeconds() <= Integer.parseInt(xacOTPtimeOut))
    {
      if (storedOTP.getGeneratedOTP().equals(verificationCode))
      {
        this.storedOTPs.remove(userId);
        return "Success";
      }
    }
    else
    {
      storedOTPs.remove(userId);
      return TIME_OUT;
    }

    return FAILED;
  }
}
