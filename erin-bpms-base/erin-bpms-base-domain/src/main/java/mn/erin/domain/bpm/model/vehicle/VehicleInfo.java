package mn.erin.domain.bpm.model.vehicle;

import java.util.Date;
import java.util.List;

import mn.erin.domain.base.model.ValueObject;
import mn.erin.domain.base.model.person.Person;

/**
 * @author Tamir
 */
public class VehicleInfo implements ValueObject<VehicleInfo>
{
  private static final long serialVersionUID = 2133088127597130277L;

  private String plateNumber;
  private String cabinNumber;
  private String certificateNumber;

  private Integer yearOfMade;
  private Date importedDate;
  private String fuelType;

  private String mark;
  private String model;

  private String color;
  private Person currentOwner;
  private List<VehicleOwner> owners;

  public VehicleInfo()
  {

  }

  public VehicleInfo(String plateNumber, String cabinNumber, String certificateNumber, Integer yearOfMade, Date importedDate, String fuelType)
  {
    this.plateNumber = plateNumber;
    this.cabinNumber = cabinNumber;
    this.certificateNumber = certificateNumber;
    this.yearOfMade = yearOfMade;
    this.importedDate = importedDate;
    this.fuelType = fuelType;
  }

  public String getPlateNumber()
  {
    return plateNumber;
  }

  public void setPlateNumber(String plateNumber)
  {
    this.plateNumber = plateNumber;
  }

  public String getCabinNumber()
  {
    return cabinNumber;
  }

  public void setCabinNumber(String cabinNumber)
  {
    this.cabinNumber = cabinNumber;
  }

  public String getCertificateNumber()
  {
    return certificateNumber;
  }

  public void setCertificateNumber(String certificateNumber)
  {
    this.certificateNumber = certificateNumber;
  }

  public Integer getYearOfMade()
  {
    return yearOfMade;
  }

  public void setYearOfMade(Integer yearOfMade)
  {
    this.yearOfMade = yearOfMade;
  }

  public Date getImportedDate()
  {
    return importedDate;
  }

  public void setImportedDate(Date importedDate)
  {
    this.importedDate = importedDate;
  }

  public String getFuelType()
  {
    return fuelType;
  }

  public void setFuelType(String fuelType)
  {
    this.fuelType = fuelType;
  }

  public String getMark()
  {
    return mark;
  }

  public void setMark(String mark)
  {
    this.mark = mark;
  }

  public String getModel()
  {
    return model;
  }

  public void setModel(String model)
  {
    this.model = model;
  }

  public String getColor()
  {
    return color;
  }

  public void setColor(String color)
  {
    this.color = color;
  }

  public Person getCurrentOwner()
  {
    return currentOwner;
  }

  public void setCurrentOwner(Person currentOwner)
  {
    this.currentOwner = currentOwner;
  }

  public List<VehicleOwner> getOwners()
  {
    return owners;
  }

  public void setOwners(List<VehicleOwner> owners)
  {
    this.owners = owners;
  }

  @Override
  public boolean sameValueAs(VehicleInfo other)
  {
    return null != other && other.plateNumber.equalsIgnoreCase(this.plateNumber);
  }
}
