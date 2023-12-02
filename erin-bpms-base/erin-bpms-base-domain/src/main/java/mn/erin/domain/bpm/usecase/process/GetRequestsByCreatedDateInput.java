/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

/**
 * @author Tamir
 */
public class GetRequestsByCreatedDateInput
{
  private String parameterValue;
  private String startCreatedDate;
  private String endCreatedDate;

  public GetRequestsByCreatedDateInput(String parameterValue, String startCreatedDate, String endCreatedDate)
  {
    this.parameterValue = parameterValue;
    this.startCreatedDate = startCreatedDate;
    this.endCreatedDate = endCreatedDate;
  }

  public String getParameterValue()
  {
    return parameterValue;
  }

  public void setParameterValue(String parameterValue)
  {
    this.parameterValue = parameterValue;
  }

  public String getStartCreatedDate()
  {
    return startCreatedDate;
  }

  public void setStartCreatedDate(String startCreatedDate)
  {
    this.startCreatedDate = startCreatedDate;
  }

  public String getEndCreatedDate()
  {
    return endCreatedDate;
  }

  public void setEndCreatedDate(String endCreatedDate)
  {
    this.endCreatedDate = endCreatedDate;
  }
}
