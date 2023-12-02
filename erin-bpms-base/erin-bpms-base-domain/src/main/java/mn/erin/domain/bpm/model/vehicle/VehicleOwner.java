package mn.erin.domain.bpm.model.vehicle;

import java.util.Date;

import mn.erin.domain.base.model.person.Person;
import mn.erin.domain.base.model.person.PersonId;

/**
 * @author Tamir
 */
public class VehicleOwner extends Person
{
  private static final long serialVersionUID = -15392896949080103L;

  private Date startOwnerDate;
  private Date endOwnerDate;

  public VehicleOwner(PersonId id)
  {
    super(id);
  }

  public Date getStartOwnerDate()
  {
    return startOwnerDate;
  }

  public void setStartOwnerDate(Date startOwnerDate)
  {
    this.startOwnerDate = startOwnerDate;
  }

  public Date getEndOwnerDate()
  {
    return endOwnerDate;
  }

  public void setEndOwnerDate(Date endOwnerDate)
  {
    this.endOwnerDate = endOwnerDate;
  }
}
