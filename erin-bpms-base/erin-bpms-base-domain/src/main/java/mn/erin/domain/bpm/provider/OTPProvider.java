package mn.erin.domain.bpm.provider;

/**
 * @author Bilguunbor
 **/

public interface OTPProvider
{
  /**
   * Saves generated one time password in a bean.
   *
   * @param userId       Unique identifier used in storing one time password.
   * @param generatedOTP Generated one time password.
   * @return returns boolean value based on if it is successfully saved or not.
   */
  void setOTP(String userId, String generatedOTP);

  /**
   * Gets generated one time password from bean. Checks time interval and returns nothing if the requested time is outside the time interval.
   * Checks OTP.
   *
   * @param userId Unique identifier used in getting one time password from saved bean.
   * @return Saved one time password identified by current user id.
   */
  String removeIfEqual(String userId, String verificationCode);
}
