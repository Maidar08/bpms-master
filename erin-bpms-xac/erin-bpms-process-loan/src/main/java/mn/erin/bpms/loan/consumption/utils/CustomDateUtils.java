package mn.erin.bpms.loan.consumption.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mn.erin.domain.bpm.BpmModuleConstants;

import static mn.erin.bpms.loan.consumption.constant.CollateralConstants.XAC_SERVICE_DATE_FORMATTER;

/**
 * @author Tamir
 */
public final class CustomDateUtils
{

  public static final SimpleDateFormat dateFormatter = new SimpleDateFormat(XAC_SERVICE_DATE_FORMATTER);

  private CustomDateUtils()
  {

  }

  public static final String getISOFutureDateByYear(Integer addYear)
  {
    Calendar calendar = Calendar.getInstance();

    calendar.setTime(new Date());
    calendar.add(Calendar.YEAR, addYear);

    SimpleDateFormat formatter = new SimpleDateFormat(BpmModuleConstants.ISO_DATE_FORMAT);

    return formatter.format(calendar.getTime());
  }
}
