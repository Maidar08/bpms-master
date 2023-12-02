package mn.erin.domain.bpm.model.online_leasing;

import java.time.LocalDateTime;

/**
 * @author Sukhbaatar
 */
public class UnionFields
{
  private String customerNumber;
  private String registerId;
  private String keyField;
  private String trackNumber;
  private String productCategory;
  private String processRequestId;
  private String processTypeId;
  private String requestType;
  private LocalDateTime dateTime;


  public UnionFields(String customerNumber, String registerId, String keyField, String trackNumber, String productCategory, String processRequestId, String processTypeId, String requestType, LocalDateTime dateTime)
  {
    this.customerNumber = customerNumber;
    this.registerId = registerId;
    this.keyField = keyField;
    this.trackNumber = trackNumber;
    this.productCategory = productCategory;
    this.processRequestId = processRequestId;
    this.processTypeId = processTypeId;
    this.requestType = requestType;
    this.dateTime = dateTime;
  }

  public String getCustomerNumber()
  {
    return customerNumber;
  }

  public void setCustomerNumber(String customerNumber)
  {
    this.customerNumber = customerNumber;
  }

  public String getRegisterId()
  {
    return registerId;
  }

  public void setRegisterId(String registerId)
  {
    this.registerId = registerId;
  }

  public String getKeyField()
  {
    return keyField;
  }

  public void setKeyField(String keyField)
  {
    this.keyField = keyField;
  }

  public String getTrackNumber()
  {
    return trackNumber;
  }

  public void setTrackNumber(String trackNumber)
  {
    this.trackNumber = trackNumber;
  }

  public String getProductCategory()
  {
    return productCategory;
  }

  public void setProductCategory(String productCategory)
  {
    this.productCategory = productCategory;
  }

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public void setProcessRequestId(String processRequestId)
  {
    this.processRequestId = processRequestId;
  }

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public void setProcessTypeId(String processTypeId)
  {
    this.processTypeId = processTypeId;
  }

  public String getRequestType()
  {
    return requestType;
  }

  public void setRequestType(String requestType)
  {
    this.requestType = requestType;
  }

  public LocalDateTime getDateTime()
  {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime)
  {
    this.dateTime = dateTime;
  }
}
