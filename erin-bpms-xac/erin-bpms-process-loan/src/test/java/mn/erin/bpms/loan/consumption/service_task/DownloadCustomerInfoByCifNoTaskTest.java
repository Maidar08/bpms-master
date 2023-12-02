package mn.erin.bpms.loan.consumption.service_task;

import org.junit.Assert;
import org.junit.Test;

import mn.erin.bpms.loan.consumption.service_task.core_bank.DownloadCustomerInfoByCifNoTask;

/**
 * @author Tamir
 */
public class DownloadCustomerInfoByCifNoTaskTest
{
  private static final String TEST_PHONE_NUMBER = "88776655";

  @Test
  public void verifyAppendPhoneNumbers()
  {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(TEST_PHONE_NUMBER);
    DownloadCustomerInfoByCifNoTask.addPhoneNumberString(stringBuilder, "99999999");

    StringBuilder appendedBuilder = DownloadCustomerInfoByCifNoTask.addPhoneNumberString(stringBuilder, TEST_PHONE_NUMBER);

    String stringValue = appendedBuilder.toString();
    String[] split = stringValue.split(",");
    Assert.assertEquals(2, split.length);
  }


  @Test
  public void verifyLengthZero()
  {
    StringBuilder stringBuilder = new StringBuilder();

    StringBuilder appendedBuilder = DownloadCustomerInfoByCifNoTask.addPhoneNumberString(stringBuilder, TEST_PHONE_NUMBER);

    String stringValue = appendedBuilder.toString();

    Assert.assertEquals(TEST_PHONE_NUMBER, stringValue);
  }
}
