package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static org.hamcrest.Matchers.hasSize;

/**
 * @author Lkhagvadorj.A
 **/

public class GetBillingTinsTest
{
  private BranchBankingService branchBankingService;
  private GetBillingTins useCase;

  @Before
  public void setUp()
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);
    useCase = new GetBillingTins(branchBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void when_register_number_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_register_number_is_blank() throws UseCaseException
  {
    useCase.execute(new GetBillingTinsInput("", ""));
  }

  @Test
  public void when_return_success() throws BpmServiceException, UseCaseException
  {
    Mockito.when(branchBankingService.getTinList("testRegister", "instanceId")).thenReturn(getTestTinMap());

    List<FieldProperty> testTinList = useCase.execute(new GetBillingTinsInput("instanceId", "testRegister"));
    List<FieldProperty> expectedList = getTestFieldProperty();
    Assert.assertThat(testTinList, hasSize(4));
  }

  private Map<String, String> getTestTinMap()
  {
    Map<String, String> testTinMap = new HashMap<>();
    testTinMap.put("k1", "v1");
    testTinMap.put("k2", "v2");
    testTinMap.put("k3", "v3");
    testTinMap.put("k4", "v4");
    return testTinMap;
  }

  private List<FieldProperty> getTestFieldProperty()
  {
    List<FieldProperty> testFieldProperty = new ArrayList<>();
    testFieldProperty.add(new FieldProperty("k1", "v1"));
    testFieldProperty.add(new FieldProperty("k2", "v2"));
    testFieldProperty.add(new FieldProperty("k3", "v3"));
    testFieldProperty.add(new FieldProperty("k4", "v4"));

    return testFieldProperty;
  }

}
