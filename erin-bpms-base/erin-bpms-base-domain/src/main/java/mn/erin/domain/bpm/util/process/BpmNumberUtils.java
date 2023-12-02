package mn.erin.domain.bpm.util.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Lkhagvadorj.A
 **/

public class BpmNumberUtils
{
  private static final String BLANK_STRING = "  ";
  private static final String COMMA_DECIMAL_STRING_FORMAT = "#,###.#";

  public static double roundWithDecimalPlace(double number, int decimalPlace)
  {
    return BigDecimal.valueOf(number).setScale(decimalPlace, RoundingMode.HALF_UP).doubleValue();
  }

  public static double getDoubleAndRemoveComma(String numString)
  {
    if (StringUtils.isBlank(numString))
    {
      return 0;
    }

    return Double.parseDouble(removeComma(numString));
  }

  public static String removeComma(String numString)
  {
    if (StringUtils.isBlank(numString))
    {
      return BLANK_STRING;
    }

    numString = numString.replace(",", "");
    return numString.replace("%", "");
  }

  public static String getThousandSeparatedString(double number)
  {
    return new DecimalFormat(COMMA_DECIMAL_STRING_FORMAT).format(number);
  }

  public static String getThousandSepStrWithDigit(Object number, int fractionDigit)
  {
    DecimalFormat decimalFormat = new DecimalFormat(COMMA_DECIMAL_STRING_FORMAT);
    decimalFormat.setMaximumFractionDigits(fractionDigit);
    return decimalFormat.format(number);
  }

  public static double roundDouble(double value, int scale)
  {
    return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
  }
}
