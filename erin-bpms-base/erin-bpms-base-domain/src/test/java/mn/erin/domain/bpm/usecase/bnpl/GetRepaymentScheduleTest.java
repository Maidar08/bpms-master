package mn.erin.domain.bpm.usecase.bnpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;

public class GetRepaymentScheduleTest
{
  private GetRepaymentSchedule useCase;
  private DirectOnlineCoreBankingService directOnlineCoreBankingService;

  @Before
  public void setUp()
  {
    directOnlineCoreBankingService = Mockito.mock(DirectOnlineCoreBankingService.class);
    useCase = new GetRepaymentSchedule(directOnlineCoreBankingService);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_null()
  {
    new GetRepaymentSchedule(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null_throws_exception() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test
  public void when_response_is_empty() throws BpmServiceException, UseCaseException
  {
    Map<String, String> input = new HashMap<>();
    input.put("acid", "1000005589");
    input.put("project", "Y");
    Mockito.when(directOnlineCoreBankingService.getRepaymentSchedule(Mockito.anyMap())).thenReturn(Collections.emptyMap());
    Map<String, Object> output = useCase.execute(input);
    Assert.assertEquals(output, Collections.emptyMap());
  }

  @Test
  public void when_response_is_successful() throws BpmServiceException, UseCaseException
  {
    Map<String, String> input = new HashMap<>();
    input.put("acid", "1000005589");
    input.put("project", "Y");
    Mockito.when(directOnlineCoreBankingService.getRepaymentSchedule(Mockito.anyMap())).thenReturn(getDummyResData());
    Map<String, Object> output = useCase.execute(input);
    Assert.assertNotNull(output.get("accountID"));
    Assert.assertNotNull(output.get("productID"));
    Assert.assertNotNull(output.get("branchID"));
    Assert.assertEquals(output.get("currency"), "MNT");
  }

  private Map<String, Object> getDummyResData(){
    Map<String, Object> result = new HashMap<>();
    result.put("accountID", "1000005589");
    result.put("branchID", "100");
    result.put("currency", "MNT");
    result.put("customerID", "00231507");
    result.put("customerName", "1000005589");
    result.put("loanAmount", "88066771.64");
    result.put("productID", "EF21");

    List<Map<String, Object>> scheduleInfoList = new ArrayList<>();
    Map<String, Object> schedule = new HashMap<>();
    schedule.put("capitalAmount", "0.0");
    schedule.put("dueDate", "2016-08-11");
    schedule.put("installmentAmount", 265425);
    schedule.put("installmentID", 1);
    schedule.put("interestAmount", 0);
    schedule.put("lifeAmount", "0.0");
    schedule.put("oSPrincipal", 87801346.64);
    schedule.put("paidInterest", 0);
    schedule.put("paidPrinciple", 265425);
    schedule.put("principalAmount", 265425);
    scheduleInfoList.add(schedule);

    result.put("schedule", scheduleInfoList);
    return result;
  }
}
