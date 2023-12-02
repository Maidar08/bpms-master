package mn.erin.bpms.loan.consumption.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tamir
 */
public class NumberUtilsTest
{

  @Test
  public void verifyRoundedNumberStr()
  {
    String roundedNumberString = NumberUtils.getRoundedNumStr("150000000");
    Assert.assertEquals("150,000,000", roundedNumberString);
  }

  @Test
  public void verifyNullString()
  {
    String roundedNumberString = NumberUtils.getRoundedNumStr(null);
    Assert.assertEquals("  ", roundedNumberString);
  }

  @Test
  public void verifyBlankString()
  {
    String roundedNumberString = NumberUtils.getRoundedNumStr("  ");
    Assert.assertEquals("  ", roundedNumberString);
  }
}
