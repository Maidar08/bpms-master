package mn.erin.domain.bpm.model.account;

/**
 * @author Zorig
 */
public class XacAccount extends Account
{
  private final String branchId;
  private final String whereAboutsUnknown;
  private final boolean deceased;
  private final String classType;
  private final boolean frozen;
  private final String classification;
  private final String accountName;
  private final String balance;
  private final String ownerType;
  private final String productGroup;
  private final String productName;
  private final String hamtran;
  private final String currencyId;
  private String schemaType;

  public XacAccount(AccountId id, String type, String branchId, String whereAboutsUnknown, boolean deceased, String classType, boolean frozen,
      String classification, String accountName, String balance, String ownerType, String productGroup, String productName, String hamtran,
      String currencyId)
  {
    super(id, type);//this is accountId and productId;
    this.branchId = branchId;
    this.whereAboutsUnknown = whereAboutsUnknown;
    this.deceased = deceased;
    this.classType = classType;
    this.frozen = frozen;
    this.classification = classification;
    this.accountName = accountName;
    this.balance = balance;
    this.ownerType = ownerType;
    this.productGroup = productGroup;
    this.productName = productName;
    this.hamtran = hamtran;
    this.currencyId = currencyId;
  }

  public String getBranchId()
  {
    return branchId;
  }

  public String getWhereAboutsUnknown()
  {
    return whereAboutsUnknown;
  }

  public boolean isDeceased()
  {
    return deceased;
  }

  public String getClassType()
  {
    return classType;
  }

  public boolean isFrozen()
  {
    return frozen;
  }

  public String getClassification()
  {
    return classification;
  }

  public String getAccountName()
  {
    return accountName;
  }

  public String getBalance()
  {
    return balance;
  }

  public String getOwnerType()
  {
    return ownerType;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public String getProductName()
  {
    return productName;
  }

  public String getHamtran()
  {
    return hamtran;
  }

  public String getCurrencyId()
  {
    return currencyId;
  }

  public String getSchemaType()
  {
    return schemaType;
  }

  public void setSchemaType(String schemaType)
  {
    this.schemaType = schemaType;
  }
}
