/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_definition_key;

import java.util.Objects;

/**
 * @author Tamir
 */
public class GetFormsByDefinitionKeyInput
{
  private final String definitionKey;

  public GetFormsByDefinitionKeyInput(String definitionKey)
  {
    this.definitionKey = Objects.requireNonNull(definitionKey, "Definition id is required!");
  }

  public String getDefinitionKey()
  {
    return definitionKey;
  }
}
