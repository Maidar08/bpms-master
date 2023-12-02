package mn.erin.domain.bpm.usecase.customer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.vehicle.VehicleInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

/**
 * @author Bilguunbor
 */
public class GetCustomerVehicleInfoTest
{
  private static final String REG_1 = "УП94051512";
  private static final String REG_2 = "УП94051513";
  private static final String FINGER_PRINT_1 = "finger1";
  private static final String FINGER_PRINT_2 = "finger2";
  private static final String PLATE_NUM_1 = "1234";

  private CustomerService customerService;
  private GetCustomerVehicleInfo useCase;
  private GetCustomerVehicleInfoInput input;

  @Before
  public void setUp()
  {
    customerService = Mockito.mock(CustomerService.class);
    useCase = new GetCustomerVehicleInfo(customerService);
    input = new GetCustomerVehicleInfoInput(REG_1, REG_2, PLATE_NUM_1);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCustomerVehicleInfo(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_plate_number_is_blank() throws UseCaseException
  {
    useCase.execute(new GetCustomerVehicleInfoInput(REG_1, REG_2, " "));
  }

  @Test(expected = UseCaseException.class)
  public void when_plate_number_is_null() throws UseCaseException
  {
    useCase.execute(new GetCustomerVehicleInfoInput(REG_1, REG_2, null));
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(customerService.getCustomerVehicleInfo(REG_1, REG_2, PLATE_NUM_1)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_vehicle_info_found() throws BpmServiceException, UseCaseException
  {
    Mockito.when(customerService.getCustomerVehicleInfo(REG_1, REG_2, PLATE_NUM_1)).thenReturn(new VehicleInfo());
    useCase.execute(input);
    Mockito.verify(customerService, Mockito.atLeastOnce()).getCustomerVehicleInfo(REG_1, REG_2, PLATE_NUM_1);
  }
}
