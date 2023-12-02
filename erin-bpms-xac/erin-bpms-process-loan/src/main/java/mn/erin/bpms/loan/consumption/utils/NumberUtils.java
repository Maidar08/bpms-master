package mn.erin.bpms.loan.consumption.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Tamir
 */
public final class NumberUtils
{
  private static final String BLANK_STRING = "  ";
  private static final String COMMA_STRING_FORMAT = "#,###";
  private static final String COMMA_DECIMAL_STRING_FORMAT = "#,###.#";

  private NumberUtils()
  {

  }

  public static String getRoundedNumStr(String numberStr)
  {
    if (StringUtils.isBlank(numberStr))
    {
      return BLANK_STRING;
    }
    double numberParsed = Double.parseDouble(numberStr);

    DecimalFormat formatter = new DecimalFormat(COMMA_STRING_FORMAT);
    return formatter.format(numberParsed);
  }

  public static String getThousandSeparatedString(String numberString)
  {
    if (StringUtils.isBlank(numberString))
    {
      return BLANK_STRING;
    }
    double numberParsed = Double.parseDouble(numberString);
    return new DecimalFormat(COMMA_DECIMAL_STRING_FORMAT).format(numberParsed);
  }

  public static String getThousandSeparatedString(double number)
  {
    return new DecimalFormat(COMMA_DECIMAL_STRING_FORMAT).format(number);
  }

  public static double roundWithDecimalPlace(double number, int decimalPlace)
  {
    return BigDecimal.valueOf(number).setScale(decimalPlace, RoundingMode.HALF_UP).doubleValue();
  }

  public static double convertToInt(long number)
  {
    return BigDecimal.valueOf(number).setScale(0, RoundingMode.HALF_UP).intValue();
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

  public static double getDoubleAndRemoveComma(String numString)
  {
    if (StringUtils.isBlank(numString))
    {
      return 0;
    }

    return Double.parseDouble( removeComma(numString) );
  }
}
