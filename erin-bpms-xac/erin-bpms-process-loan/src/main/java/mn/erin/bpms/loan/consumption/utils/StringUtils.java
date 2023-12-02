package mn.erin.bpms.loan.consumption.utils;

/**
 * @author Lkhagvadorj.A
 **/

public final class StringUtils
{
  private static final String NULL = "null";

  private StringUtils()
  {
    /*no-op*/
  }

  public static String getStringValue(Object objectValue)
  {
    if (null == objectValue || objectValue.toString().equals(NULL))
    {
      return " ";
    }
    return objectValue.toString();
  }

  public static String toWholeNumber(String value)
  {
    return org.apache.commons.lang3.StringUtils.substringBefore(value, ".");
  }
}
