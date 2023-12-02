package mn.erin.domain.bpm.usecase.customer;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.vehicle.VehicleOwner;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

import static mn.erin.domain.bpm.BpmMessagesConstants.DOWNLOAD_VEHICLE_REFENCE_INFO_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.VEHICLE_PLATE_NUMBER_BLANK_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.VEHICLE_PLATE_NUMBER_CODE;

/**
 * @author Tamir
 */
public class GetCustomerVehicleOwners extends AbstractUseCase<GetCustomerVehicleOwnersInput, GetCustomerVehicleOwnersOutput>
{
  private final CustomerService customerService;

  public GetCustomerVehicleOwners(CustomerService customerService)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");
  }

  @Override
  public GetCustomerVehicleOwnersOutput execute(GetCustomerVehicleOwnersInput input) throws UseCaseException
  {

    String regNumber = input.getRegNumber();
    String plateNumber = input.getPlateNumber();

    if (StringUtils.isBlank(plateNumber))
    {
      String message = String.format(VEHICLE_PLATE_NUMBER_BLANK_MESSAGE, regNumber);
      throw new UseCaseException(VEHICLE_PLATE_NUMBER_CODE, message);
    }

    try
    {
      List<VehicleOwner> owners = customerService.getCustomerVehicleOwners(regNumber, input.getEmployeeRegNumber(), plateNumber);

      return new GetCustomerVehicleOwnersOutput(owners);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(DOWNLOAD_VEHICLE_REFENCE_INFO_CODE, e.getMessage());
    }
  }
}
