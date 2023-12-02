package mn.erin.domain.bpm.usecase.customer;

/**
 * @author Tamir
 */
public class GetCustomerVehicleInfoInput
{
  private String regNumber;
  private String employeeRegNumber;

  private String plateNumber;

  public GetCustomerVehicleInfoInput()
  {
  }

  public GetCustomerVehicleInfoInput(String regNumber, String employeeRegNumber, String plateNumber)
  {
    this.regNumber = regNumber;
    this.employeeRegNumber = employeeRegNumber;
    this.plateNumber = plateNumber;
  }

  public String getRegNumber()
  {
    return regNumber;
  }

  public void setRegNumber(String regNumber)
  {
    this.regNumber = regNumber;
  }

  public String getEmployeeRegNumber()
  {
    return employeeRegNumber;
  }

  public void setEmployeeRegNumber(String employeeRegNumber)
  {
    this.employeeRegNumber = employeeRegNumber;
  }

  public String getPlateNumber()
  {
    return plateNumber;
  }

  public void setPlateNumber(String plateNumber)
  {
    this.plateNumber = plateNumber;
  }
}
