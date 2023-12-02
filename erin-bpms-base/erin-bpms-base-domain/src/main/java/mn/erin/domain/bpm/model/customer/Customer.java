package mn.erin.domain.bpm.model.customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mn.erin.domain.base.model.person.PersonId;
import mn.erin.domain.bpm.model.account.Account;
import mn.erin.domain.bpm.model.citizen.Citizen;

/**
 * @author EBazarragchaaC
 */
public class Customer extends Citizen
{
  private String customerNumber;
  private List<Account> accountList = new ArrayList<>();

  private String occupancy;

  public Customer(PersonId id)
  {
    super(id);
  }

  public Customer(PersonId id, String customerNumber)
  {
    super(id);
    this.customerNumber = customerNumber;
  }

  public List<Account> getAccountList()
  {
    return Collections.unmodifiableList(accountList);
  }

  public void setAccountList(List<Account> accountList)
  {
    this.accountList = accountList;
  }

  public String getCustomerNumber()
  {
    return customerNumber;
  }

  public void setCustomerNumber(String customerNumber)
  {
    this.customerNumber = customerNumber;
  }

  public String getOccupancy()
  {
    return occupancy;
  }

  public void setOccupancy(String occupancy)
  {
    this.occupancy = occupancy;
  }
}
