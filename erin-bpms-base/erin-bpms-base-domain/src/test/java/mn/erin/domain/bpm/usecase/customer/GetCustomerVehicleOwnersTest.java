package mn.erin.domain.bpm.usecase.customer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.vehicle.VehicleOwner;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;


/**
 * @author Bilguunbor
 */
public class GetCustomerVehicleOwnersTest
{
  private static final String REG_1 = "УП94051512";
  private static final String REG_2 = "УП94051513";
  private static final String FINGER_PRINT_1 = "finger1";
  private static final String FINGER_PRINT_2 = "finger2";
  private static final String PLATE_NUM_1 = "1234";

  VehicleOwner vehicleOwner1 = new VehicleOwner(PersonId.valueOf("УП94051515"));
  VehicleOwner vehicleOwner2 = new VehicleOwner(PersonId.valueOf("УП94051516"));
  VehicleOwner vehicleOwner3 = new VehicleOwner(PersonId.valueOf("УП94051517"));

  private CustomerService customerService;
  private GetCustomerVehicleOwners useCase;
  private GetCustomerVehicleOwnersInput input;

  @Before
  public void setUp()
  {
    customerService = Mockito.mock(CustomerService.class);
    useCase = new GetCustomerVehicleOwners(customerService);
    input = new GetCustomerVehicleOwnersInput(REG_1, REG_2, PLATE_NUM_1);
  }

  @Test(expected = NullPointerException.class)
  public void when_service_is_null()
  {
    new GetCustomerVehicleOwners(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_plate_number_is_blank() throws UseCaseException
  {
    useCase.execute(new GetCustomerVehicleOwnersInput(REG_1, REG_2, " "));
  }

  @Test(expected = UseCaseException.class)
  public void when_plate_number_is_null() throws UseCaseException
  {
    useCase.execute(new GetCustomerVehicleOwnersInput(REG_1, REG_2, null));
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmServiceException, UseCaseException
  {
    Mockito.when(customerService.getCustomerVehicleOwners(REG_1, REG_2, PLATE_NUM_1)).thenThrow(BpmServiceException.class);
    useCase.execute(input);
  }

  @Test
  public void when_vehicle_owners_found() throws BpmServiceException, UseCaseException
  {
    Mockito.when(customerService.getCustomerVehicleOwners(REG_1, REG_2, PLATE_NUM_1)).thenReturn(getVehicleOwnersList());
    GetCustomerVehicleOwnersOutput vehicleOwners = useCase.execute(input);
    List<VehicleOwner> owners = vehicleOwners.getOwners();
    List<VehicleOwner> vehicleOwnersList = new ArrayList<>();

    vehicleOwnersList.add(vehicleOwner1);
    vehicleOwnersList.add(vehicleOwner2);
    vehicleOwnersList.add(vehicleOwner3);

    Assert.assertEquals(vehicleOwnersList, owners);
  }

  public List<VehicleOwner> getVehicleOwnersList()
  {
    List<VehicleOwner> vehicleOwners = new ArrayList<>();

    vehicleOwners.add(vehicleOwner1);
    vehicleOwners.add(vehicleOwner2);
    vehicleOwners.add(vehicleOwner3);

    return vehicleOwners;
  }
}
