package mn.erin.domain.bpm.usecase.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.property.PropertyId;
import mn.erin.domain.bpm.model.property.PropertyInfo;
import mn.erin.domain.bpm.model.property.PropertyProcess;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

public class GetPropertyInfoTest
{
  private CustomerService customerService;
  private GetPropertyInfo useCase;
  private GetPropertyInfoInput input;

  @Before
  public void setUp()
  {
    customerService = Mockito.mock(CustomerService.class);
    useCase = new GetPropertyInfo(customerService);
    input = new GetPropertyInfoInput(generateMap(), generateMap(), "123");
  }

  @Test(expected = NullPointerException.class)
  public void when_customer_service_is_null()
  {
    new GetPropertyInfo(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(customerService.getPropertyInfo(input.getOperatorInfo(), input.getCitizenInfo(), "123")).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws BpmServiceException, UseCaseException
  {
    Mockito.when(customerService.getPropertyInfo(input.getOperatorInfo(), input.getCitizenInfo(), "123")).thenReturn(getPropertyInfo());
    useCase.execute(input);
    Assert.assertEquals("123", "123", "123");
  }

  private PropertyInfo getPropertyInfo()
  {
    List<PropertyProcess> propertyProcessList = new ArrayList<>();
    return new PropertyInfo(PropertyId.valueOf("123"), "123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123", "123",
        propertyProcessList);
  }

  private static Map<String, String> generateMap()
  {
    Map<String, String> map = new HashMap<>();
    map.put("operatorInfo", "citizenInfo");
    map.put("operatorInfo", "citizenInfo");

    return map;
  }
}
