package mn.erin.domain.bpm.model.account;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.model.Entity;

/**
 * @author Zorig
 */
public class UDField implements Entity<UDField>
{
  private static final Logger LOG = LoggerFactory.getLogger(UDField.class);

  private final UDFieldId id;
  private final String fieldDescription;
  private final String fieldType;
  private final String fieldNumber;
  private final boolean mandatory;
  private final String fixedLength;
  private final String defaultValue;
  private final boolean updateAllowed;
  private final boolean uniqueField;
  private List<UDFieldValue> values;

  public UDField(UDFieldId id, String fieldDescription, String fieldType, String fieldNumber, boolean mandatory, String fixedLength,
      String defaultValue, boolean updateAllowed, boolean uniqueField)
  {
    this.id = id;
    this.fieldDescription = fieldDescription;
    this.fieldType = fieldType;
    this.fieldNumber = fieldNumber;
    this.mandatory = mandatory;
    this.fixedLength = fixedLength;
    this.defaultValue = defaultValue;
    this.updateAllowed = updateAllowed;
    this.uniqueField = uniqueField;
    this.values = new ArrayList<>();
  }

  @Override
  public boolean sameIdentityAs(UDField other)
  {
    return other != null && (this.id.equals(other.id));
  }

  public UDFieldId getId()
  {
    return id;
  }

  public String getFieldDescription()
  {
    return fieldDescription;
  }

  public String getFieldType()
  {
    return fieldType;
  }

  public String getFieldNumber()
  {
    return fieldNumber;
  }

  public boolean isMandatory()
  {
    return mandatory;
  }

  public String getFixedLength()
  {
    return fixedLength;
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }

  public boolean isUpdateAllowed()
  {
    return updateAllowed;
  }

  public boolean isUniqueField()
  {
    return uniqueField;
  }

  public List<UDFieldValue> getValues()
  {
    return values;
  }

  public void addValues(UDFieldValue value)
  {
    values.add(value);
  }

  public void setValues(List<UDFieldValue> valueList)
  {
    this.values = valueList;
  }

  public String getFieldValueIdByDescription(String fieldDescription)
  {
    for (UDFieldValue fieldValue : values)
    {
      if (null == fieldValue.getItemDescription())
      {
        LOG.info("############# Field description is null : " + fieldValue.getItemId());
        continue;
      }
      if (null != fieldValue.getItemDescription() && fieldValue.getItemDescription().equals(fieldDescription))
      {
        return fieldValue.getItemId();
      }
    }
    return null;
  }
}
