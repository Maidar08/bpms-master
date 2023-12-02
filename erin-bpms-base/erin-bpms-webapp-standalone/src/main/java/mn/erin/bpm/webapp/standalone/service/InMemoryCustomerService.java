package mn.erin.bpm.webapp.standalone.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import mn.erin.domain.base.model.person.AddressInfo;
import mn.erin.domain.bpm.model.customer.Customer;
import mn.erin.domain.bpm.model.property.PropertyInfo;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleInfo;
import mn.erin.domain.bpm.model.vehicle.VehicleOwner;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

@Service
public class InMemoryCustomerService implements CustomerService
{
  @Override
  public AddressInfo getCustomerAddress(String regNumber, String employeeRegNumber) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Customer getCustomerInfo(String regNumber, String employeeRegNumber) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<SalaryInfo> getCustomerSalaryInfos(String regNumber, String employeeRegNumber, Integer month)
      throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public PropertyInfo getPropertyInfo(Map<String, String> operatorInfo, Map<String, String> citizenInfo, String propertyId) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public VehicleInfo getCustomerVehicleInfo(String regNumber, String employeeRegNumber, String plateNumber)
      throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<VehicleOwner> getCustomerVehicleOwners(String regNumber, String employeeRegNumber, String plateNumber) throws BpmServiceException
  {
    throw new UnsupportedOperationException();
  }
}
