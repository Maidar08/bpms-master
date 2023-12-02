/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

import java.util.List;

/**
 * @author Tamir
 */
public class RestUploadDocuments
{
  private String type;
  private String subType;
  private List<RestUploadDocument> documents;

  public RestUploadDocuments()
  {

  }

  public RestUploadDocuments(String type, String subType, List<RestUploadDocument> documents)
  {
    this.type = type;
    this.subType = subType;
    this.documents = documents;
  }

  public List<RestUploadDocument> getDocuments()
  {
    return documents;
  }

  public void setDocuments(List<RestUploadDocument> documents)
  {
    this.documents = documents;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getSubType()
  {
    return subType;
  }

  public void setSubType(String subType)
  {
    this.subType = subType;
  }
}
