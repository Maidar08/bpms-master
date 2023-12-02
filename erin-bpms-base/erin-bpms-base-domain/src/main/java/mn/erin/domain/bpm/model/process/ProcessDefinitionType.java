/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.process;

/**
 * @author EBazarragchaa
 */
public enum ProcessDefinitionType
{
  CASE, PROCESS;

  public static String fromEnumToString(ProcessDefinitionType processDefinitionType)
  {
    switch(processDefinitionType){
    case CASE:
      return "CASE";
    case PROCESS:
      return "PROCESS";
    default:
      throw new IllegalArgumentException("Incompatible Definition Type!");
    }
  }

  public static ProcessDefinitionType fromStringToEnum(String processDefinitionStr)
  {
    switch(processDefinitionStr){
    case "CASE":
      return ProcessDefinitionType.CASE;
    case "PROCESS":
      return ProcessDefinitionType.PROCESS;
    default:
      throw new IllegalArgumentException("Incompatible Definition Type!");
    }
  }
}
