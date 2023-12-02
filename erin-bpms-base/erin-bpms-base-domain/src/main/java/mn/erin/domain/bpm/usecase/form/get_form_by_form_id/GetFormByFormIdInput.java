/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_form_by_form_id;

/**
 * @author Tamir
 */
public class GetFormByFormIdInput
{
  private final String formId;

  public GetFormByFormIdInput(String formId)
  {
    this.formId = formId;
  }

  public String getFormId()
  {
    return formId;
  }
}
