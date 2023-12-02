package mn.erin.domain.bpm.usecase.customer;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.vehicle.VehicleInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

import static mn.erin.domain.bpm.BpmMessagesConstants.DOWNLOAD_VEHICLE_REFENCE_INFO_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.VEHICLE_PLATE_NUMBER_BLANK_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.VEHICLE_PLATE_NUMBER_CODE;

/**
 * @author Tamir
 */
public class GetCustomerVehicleInfo extends AbstractUseCase<GetCustomerVehicleInfoInput, VehicleInfo>
{
  private final CustomerService customerService;

  public GetCustomerVehicleInfo(CustomerService customerService)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");
  }

  @Override
  public VehicleInfo execute(GetCustomerVehicleInfoInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    String regNumber = input.getRegNumber();
    String plateNumber = input.getPlateNumber();

    if (StringUtils.isBlank(plateNumber))
    {
      String message = String.format(VEHICLE_PLATE_NUMBER_BLANK_MESSAGE, regNumber);
      throw new UseCaseException(VEHICLE_PLATE_NUMBER_CODE, message);
    }

    try
    {
      return customerService.getCustomerVehicleInfo(regNumber, input.getEmployeeRegNumber(), plateNumber);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(DOWNLOAD_VEHICLE_REFENCE_INFO_CODE, e.getMessage());
    }
  }
}
