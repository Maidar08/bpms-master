/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.model.property;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import mn.erin.domain.base.model.person.Person;

/**
 * @author Zorig
 */
public class PropertyProcess
{
  private final PropertyServiceId serviceID;
  private final String serviceName;

  private final Date processDate;
  private final List<Person> ownerList;

  public PropertyProcess(PropertyServiceId serviceID, String serviceName, Date processDate, List<Person> ownerList)
  {
    this.serviceID = serviceID;
    this.serviceName = serviceName;
    this.processDate = processDate;
    this.ownerList = ownerList;
  }

  public Date getProcessDate()
  {
    return processDate;
  }

  public List<Person> getOwnerList()
  {
    return Collections.unmodifiableList(ownerList);
  }

  public PropertyServiceId getServiceID()
  {
    return serviceID;
  }

  public String getServiceName()
  {
    return serviceName;
  }
}
