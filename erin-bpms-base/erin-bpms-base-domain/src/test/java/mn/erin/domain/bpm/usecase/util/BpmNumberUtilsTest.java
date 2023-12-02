package mn.erin.domain.bpm.usecase.util;

import org.junit.Assert;
import org.junit.Test;

import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getDoubleAndRemoveComma;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getThousandSeparatedString;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.removeComma;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.roundDouble;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.roundWithDecimalPlace;

/**
 * @author Bilguunbor
 */
public class BpmNumberUtilsTest
{
  @Test
  public void round_decimal_up()
  {
    double number = roundWithDecimalPlace(26.75, 0);
    Assert.assertEquals(27.00, number, 1);
  }

  @Test
  public void get_double_remove_comma()
  {
    double number = getDoubleAndRemoveComma("26,75");
    Assert.assertEquals(2675, number, 0);
  }

  @Test
  public void get_double_remove_comma_when_input_is_null()
  {
    double number = getDoubleAndRemoveComma(" ");
    Assert.assertEquals(0, number, 0);
  }

  @Test
  public void remove_comma()
  {
    String numberString = removeComma("26,75%");
    Assert.assertEquals("2675", numberString);
  }

  @Test
  public void remove_comma_when_input_is_null()
  {
    String numberString = removeComma(" ");
    Assert.assertEquals("  ", numberString);
  }

  @Test
  public void thousand_separated_string()
  {
    String number = getThousandSeparatedString(2675.2);
    Assert.assertEquals("2,675.2", number);
  }

  @Test
  public void round_and_return()
  {
    double number = roundDouble(26.75, 1);
    Assert.assertEquals(26.8, number, 0.1);
  }
}
