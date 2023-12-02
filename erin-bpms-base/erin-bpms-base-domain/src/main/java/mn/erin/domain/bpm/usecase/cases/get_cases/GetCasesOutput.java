/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.cases.get_cases;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.bpm.model.cases.Case;

/**
 * @author Tamir
 */
public class GetCasesOutput
{
  private List<Case> cases;

  public GetCasesOutput(List<Case> cases)
  {
    this.cases = Objects.requireNonNull(cases, "Case list cannot be null!");
  }

  public List<Case> getCases()
  {
    return cases;
  }

  public void setCases(List<Case> cases)
  {
    this.cases = cases;
  }
}
