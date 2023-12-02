/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.submit_form;

/**
 * @author Tamir
 */
public class SubmitFormOutput
{
  private boolean isSubmitted;
  private String relatedUserTaskId;

  public SubmitFormOutput(boolean isSubmitted)
  {
    this.isSubmitted = isSubmitted;
  }

  public boolean isSubmitted()
  {
    return isSubmitted;
  }

  public String getRelatedUserTaskId()
  {
    return relatedUserTaskId;
  }

  public void setRelatedUserTaskId(String relatedUserTaskId)
  {
    this.relatedUserTaskId = relatedUserTaskId;
  }
}
