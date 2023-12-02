package mn.erin.domain.bpm.util.process;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.holidays.LoanCyclePlan;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;

import static mn.erin.domain.bpm.BpmModuleConstants.ISO_DATE_FORMAT;

/**
 * @author Lkhagvadorj.A
 **/

public class BpmHolidayUtils
{
  private BpmHolidayUtils()
  {
  }

  public static LoanCyclePlan addHolidays(LoanCyclePlan loanCyclePlan, DefaultParameterRepository defaultParameterRepository) throws UseCaseException, ParseException
  {
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
    GetGeneralInfoInput input = new GetGeneralInfoInput("LoanCalculation", "Holidays");
    Map<String, Object> defaultParameters = getGeneralInfo.execute(input);

    Map<String, List<String>> holidays = (Map<String, List<String>>) defaultParameters.get("Holidays");
    Calendar calendar = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
    for (Map.Entry<String, List<String>> entry : holidays.entrySet())
    {
      for (String holiday : entry.getValue())
      {
        calendar.setTime(dateFormat.parse(holiday));
        loanCyclePlan.addHoliday(calendar.getTime());
      }
    }

    return loanCyclePlan;
  }
}
