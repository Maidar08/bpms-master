/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.process;

import mn.erin.domain.base.model.EntityId;

/**
 * @author EBazarragchaa
 */
public class ProcessTypeId extends EntityId
{
  public ProcessTypeId(String id)
  {
    super(id);
  }

  public static ProcessTypeId valueOf(String id)
  {
    return new ProcessTypeId(id);
  }
}
