package mn.erin.bpms.loan.consumption.task_listener;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SaveReasonTaskListenerTest
{
  @Test
  public void saves_current_dateTime_as_json()
  {
    LocalDateTime noteDate = LocalDateTime.parse("2020-10-08T19:14:28.510958100");
    JSONObject json = new JSONObject();
    json.put("noteDate", noteDate);

    assertEquals("{\"noteDate\":\"2020-10-08T19:14:28.510958100\"}", json.toString());
  }

  @Test
  public void gets_date_part_from_json_date()
  {
    String stringDate = "2020-10-08T19:14:28.510958100";
    assertEquals("2020-10-08", stringDate.substring(0, stringDate.indexOf('T')));
  }
}