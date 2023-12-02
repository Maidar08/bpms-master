package mn.erin.domain.bpm.usecase.collateral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Sukhbat
 */

public class GetCollReferenceCodesTest
{
  private NewCoreBankingService newCoreBankingService;
  private GetCollReferenceCodes useCase;
  private GetCollReferenceCodesInput input;

  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    useCase = new GetCollReferenceCodes(newCoreBankingService);
    input = new GetCollReferenceCodesInput(generateList());
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    List<String> list = new ArrayList<>();
    useCase.execute(new GetCollReferenceCodesInput(list));
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws UseCaseException, BpmServiceException
  {
    Mockito.when(newCoreBankingService.getCollReferenceCodes(input.getTypes())).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws BpmServiceException, UseCaseException
  {
    Mockito.when(newCoreBankingService.getCollReferenceCodes(input.getTypes())).thenReturn(generateMap());
    GetCollReferenceCodesOutput output = useCase.execute(input);

    Assert.assertEquals(2, output.getReferenceCodes().size());
  }

  private static List<String> generateList()
  {
    List<String> list = new ArrayList<>();
    list.add("ABC");
    list.add("ABC");
    list.add("ABC");

    return list;
  }

  private static Map<String, List<String>> generateMap()
  {
    Map<String, List<String>> map = new HashMap<>();
    map.put("A", generateList());
    map.put("B", generateList());

    return map;
  }
}
