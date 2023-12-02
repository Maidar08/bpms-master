/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.cases.get_cases;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Case;
import mn.erin.domain.bpm.service.CaseService;

/**
 * @author Tamir
 */
public class GetCases implements UseCase<Void, GetCasesOutput>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetCases.class);

  private static final String ERR_MSG_DOES_NOT_EXIST = "Cases does not exist!";

  private final CaseService caseService;

  public GetCases(CaseService caseService)
  {
    this.caseService = Objects.requireNonNull(caseService, "case service is null!");
  }

  @Override
  public GetCasesOutput execute(Void input) throws UseCaseException
  {
    List<Case> cases = caseService.getUserCases();

    if (null == cases || cases.isEmpty())
    {
      LOGGER.error(ERR_MSG_DOES_NOT_EXIST);
      throw new UseCaseException(ERR_MSG_DOES_NOT_EXIST);
    }

    
    return new GetCasesOutput(cases);
  }
}
