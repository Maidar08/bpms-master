/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.customer;

import org.json.JSONArray;

/**
 * @author Zorig
 */
public class CreateUDFieldsRequestBodyOutput
{
  private final JSONArray numberUdFields;
  private final JSONArray textUdFields;
  private final JSONArray dateUdFields;

  public CreateUDFieldsRequestBodyOutput(JSONArray numberUdFields, JSONArray textUdFields, JSONArray dateUdFields)
  {
    this.numberUdFields = numberUdFields;
    this.textUdFields = textUdFields;
    this.dateUdFields = dateUdFields;
  }

  public JSONArray getNumberUdFields()
  {
    return numberUdFields;
  }

  public JSONArray getTextUdFields()
  {
    return textUdFields;
  }

  public JSONArray getDateUdFields()
  {
    return dateUdFields;
  }
}
