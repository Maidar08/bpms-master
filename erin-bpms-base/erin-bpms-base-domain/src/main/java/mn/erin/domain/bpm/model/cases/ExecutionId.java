/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.cases;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Tamir
 */
public class ExecutionId extends EntityId
{
  public ExecutionId(String id)
  {
    super(id);
  }

  public static ExecutionId valueOf(String id)
  {
    return new ExecutionId(id);
  }
}
