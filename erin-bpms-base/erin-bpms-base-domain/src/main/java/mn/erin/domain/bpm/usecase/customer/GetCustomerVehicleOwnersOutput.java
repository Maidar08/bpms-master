package mn.erin.domain.bpm.usecase.customer;

import java.util.List;

import mn.erin.domain.bpm.model.vehicle.VehicleOwner;

/**
 * @author Tamir
 */
public class GetCustomerVehicleOwnersOutput
{
  private List<VehicleOwner> owners;

  public GetCustomerVehicleOwnersOutput(List<VehicleOwner> owners)
  {
    this.owners = owners;
  }

  public List<VehicleOwner> getOwners()
  {
    return owners;
  }

  public void setOwners(List<VehicleOwner> owners)
  {
    this.owners = owners;
  }
}
