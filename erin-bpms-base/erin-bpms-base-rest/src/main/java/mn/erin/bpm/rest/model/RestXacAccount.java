package mn.erin.bpm.rest.model;

import mn.erin.domain.bpm.model.account.XacAccount;

/**
 * @author Zorig
 */
public class RestXacAccount
{
  private String accountId;
  private String type;
  private String branchId;
  private String whereAboutsUnknown;
  private boolean deceased;
  private String classType;
  private boolean frozen;
  private String classification;
  private String accountName;
  private String balance;
  private String ownerType;
  private String productGroup;
  private String productName;
  private String hamtran;
  private String currencyId;

  public RestXacAccount()
  {
    // no needed to do something

  }

  public String getAccountId()
  {
    return accountId;
  }

  public void setAccountId(String accountId)
  {
    this.accountId = accountId;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getBranchId()
  {
    return branchId;
  }

  public void setBranchId(String branchId)
  {
    this.branchId = branchId;
  }

  public String getWhereAboutsUnknown()
  {
    return whereAboutsUnknown;
  }

  public void setWhereAboutsUnknown(String whereAboutsUnknown)
  {
    this.whereAboutsUnknown = whereAboutsUnknown;
  }

  public boolean isDeceased()
  {
    return deceased;
  }

  public void setDeceased(boolean deceased)
  {
    this.deceased = deceased;
  }

  public String getClassType()
  {
    return classType;
  }

  public void setClassType(String classType)
  {
    this.classType = classType;
  }

  public boolean isFrozen()
  {
    return frozen;
  }

  public void setFrozen(boolean frozen)
  {
    this.frozen = frozen;
  }

  public String getClassification()
  {
    return classification;
  }

  public void setClassification(String classification)
  {
    this.classification = classification;
  }

  public String getAccountName()
  {
    return accountName;
  }

  public void setAccountName(String accountName)
  {
    this.accountName = accountName;
  }

  public String getBalance()
  {
    return balance;
  }

  public void setBalance(String balance)
  {
    this.balance = balance;
  }

  public String getOwnerType()
  {
    return ownerType;
  }

  public void setOwnerType(String ownerType)
  {
    this.ownerType = ownerType;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public void setProductGroup(String productGroup)
  {
    this.productGroup = productGroup;
  }

  public String getProductName()
  {
    return productName;
  }

  public void setProductName(String productName)
  {
    this.productName = productName;
  }

  public String getHamtran()
  {
    return hamtran;
  }

  public void setHamtran(String hamtran)
  {
    this.hamtran = hamtran;
  }

  public String getCurrencyId()
  {
    return currencyId;
  }

  public void setCurrencyId(String currencyId)
  {
    this.currencyId = currencyId;
  }

  public static RestXacAccount of(XacAccount xacAccount)
  {
    RestXacAccount restXacAccount = new RestXacAccount();
    restXacAccount.setAccountId(xacAccount.getId().getId());
    restXacAccount.setType(xacAccount.getType());
    restXacAccount.setBranchId(xacAccount.getBranchId());
    restXacAccount.setWhereAboutsUnknown(xacAccount.getWhereAboutsUnknown());
    restXacAccount.setDeceased(xacAccount.isDeceased());
    restXacAccount.setClassType(xacAccount.getClassType());
    restXacAccount.setFrozen(xacAccount.isFrozen());
    restXacAccount.setClassification(xacAccount.getClassification());
    restXacAccount.setAccountName(xacAccount.getAccountName());
    restXacAccount.setBalance(xacAccount.getBalance());
    restXacAccount.setOwnerType(xacAccount.getOwnerType());
    restXacAccount.setProductGroup(xacAccount.getProductGroup());
    restXacAccount.setProductName(xacAccount.getProductName());
    restXacAccount.setHamtran(xacAccount.getHamtran());
    restXacAccount.setCurrencyId(xacAccount.getCurrencyId());

    return restXacAccount;
  }
}
