package mn.erin.domain.bpm.model.account;

/**
 * @author Zorig
 */
public class UDFieldValue
{
  private final String itemId;
  private final String itemDescription;
  private final boolean isDefault;

  public UDFieldValue(String itemId, String itemDescription, boolean isDefault)
  {
    this.itemId = itemId;
    this.itemDescription = itemDescription;
    this.isDefault = isDefault;
  }

  public String getItemId()
  {
    return itemId;
  }

  public String getItemDescription()
  {
    return itemDescription;
  }

  public boolean isDefault()
  {
    return isDefault;
  }
}
