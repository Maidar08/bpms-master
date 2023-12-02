/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.model;

/**
 * @author Tamir
 */
public class RestUploadDocument
{
  private String name;
  private String contentAsBase64;

  public RestUploadDocument()
  {

  }

  public RestUploadDocument(String name, String contentAsBase64)
  {
    this.name = name;
    this.contentAsBase64 = contentAsBase64;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getContentAsBase64()
  {
    return contentAsBase64;
  }

  public void setContentAsBase64(String contentAsBase64)
  {
    this.contentAsBase64 = contentAsBase64;
  }
}
