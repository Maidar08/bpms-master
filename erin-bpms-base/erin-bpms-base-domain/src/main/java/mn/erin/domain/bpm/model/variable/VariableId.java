/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.variable;

import mn.erin.domain.base.model.EntityId;

/**
 * @author Tamir
 */
public class VariableId extends EntityId
{
  public VariableId(String id)
  {
    super(id);
  }

  public static VariableId valueOf(String id)
  {
    return new VariableId(id);
  }
}
