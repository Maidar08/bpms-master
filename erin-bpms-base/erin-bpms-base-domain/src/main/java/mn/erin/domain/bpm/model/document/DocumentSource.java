/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.document;

/**
 * @author Tamir
 */
public enum DocumentSource
{
  ALFRESCO, LDMS, CAMUNDA;

  public static DocumentSource stringToEnum(String documentSource)
  {
    switch (documentSource)
    {
    case "ALFRESCO":
      return DocumentSource.ALFRESCO;
    case "LDMS":
      return DocumentSource.LDMS;
    case "CAMUNDA":
      return DocumentSource.CAMUNDA;
    default:
      throw new IllegalArgumentException("Incompatible Entity Type!");
    }
  }

  public static String enumToString(DocumentSource documentSource)
  {
    switch (documentSource)
    {
    case ALFRESCO:
      return "ALFRESCO";
    case LDMS:
      return "LDMS";
    case CAMUNDA:
      return "CAMUNDA";
    default:
      throw new IllegalArgumentException("Incompatible Entity Type!");
    }
  }
}
