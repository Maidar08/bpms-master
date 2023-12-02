/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_case_instance;

/**
 * @author Tamir
 */
public class GetFormsByCaseInstanceIdInput
{
  private String caseInstanceId;

  public GetFormsByCaseInstanceIdInput(String caseInstanceId)
  {
    this.caseInstanceId = caseInstanceId;
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }

}
