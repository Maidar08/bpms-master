/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_forms_by_definition_id;

import java.util.Objects;

/**
 * @author Tamir
 */
public class GetFormsByDefinitionIdInput
{
  private final String definitionId;

  public GetFormsByDefinitionIdInput(String definitionId)
  {
    this.definitionId = Objects.requireNonNull(definitionId, "Definition id is required!");
  }

  public String getDefinitionId()
  {
    return definitionId;
  }
}
