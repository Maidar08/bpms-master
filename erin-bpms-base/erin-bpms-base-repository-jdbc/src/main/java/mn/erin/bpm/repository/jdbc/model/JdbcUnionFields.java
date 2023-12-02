package mn.erin.bpm.repository.jdbc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Sukhbaatar
 */
@Table("ERIN_OL_UNION")
public class JdbcUnionFields
{
  @Id
  String customerNumber;
  String registerId;
  String keyField;
  String trackNumber;
  String productCategory;
  String processRequestId;
  String processTypeId;
  String requestType;
  LocalDateTime dateTime;

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
