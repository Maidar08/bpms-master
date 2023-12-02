/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.camunda;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;

/**
 * @author EBazarragchaa
 */
public class CamundaProcessEngineProvider implements ProcessEngineProvider
{

  @Override
  public ProcessEngine getProcessEngine()
  {
    return BpmPlatform.getDefaultProcessEngine();
  }
}
