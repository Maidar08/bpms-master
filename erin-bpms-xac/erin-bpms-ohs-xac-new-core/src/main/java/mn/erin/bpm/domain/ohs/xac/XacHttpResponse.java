/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac;

import org.json.JSONObject;

/**
 * @author Tamir
 */
public class XacHttpResponse
{
  private JSONObject response;

  public XacHttpResponse(JSONObject response)
  {
    this.response = response;
  }

  public JSONObject getResponse()
  {
    return response;
  }

  public void setResponse(JSONObject response)
  {
    this.response = response;
  }
}
