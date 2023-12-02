package consumption.case_listener;

import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_LEVEL_RISK;
import static mn.erin.domain.bpm.BpmModuleConstants.SCORING_SCORE;

/**
 * @author Lkhagvadorj.A
 **/

public class OnlineSalaryScoringLogListener implements CaseExecutionListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(OnlineSalaryScoringLogListener.class);
  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    final Object cif = caseExecution.getVariable(CIF_NUMBER);
    final Object requestId = caseExecution.getVariable(PROCESS_REQUEST_ID);
    final Object scoringLevel = caseExecution.getVariable(SCORING_LEVEL_RISK);
    final Object score = caseExecution.getVariable(SCORING_SCORE);
    final Object economicLevel = caseExecution.getVariable("economic_level");
    final Object incomeType = caseExecution.getVariable("income_type");
    final Object xacspan = caseExecution.getVariable("xacspan");
    final Object workspan = caseExecution.getVariable("workspan");
    final Object address = caseExecution.getVariable("address");
    final Object gender = caseExecution.getVariable("gender");
    final Object familyIncome = caseExecution.getVariable("family_income");
    final Object joblessMembers = caseExecution.getVariable("jobless_members");
    LOGGER.info("############# SCORING HAS CALCULATED FOR CIF = [{}] REQUEST ID = [{}]. "
        + "\n SCORING LEVEL:{} SCORE:{}"
        + " \n ECONOMIC LEVEL:{} INCOME TYPE:{} XACSPAN:{} WORKSPAN:{} ADDRESS:{} GENDER:{} FAMILY INCOME:{} JOBLESS MEMBERS:{}",
        cif, requestId, scoringLevel, score, economicLevel, incomeType, xacspan, workspan, address, gender, familyIncome, joblessMembers);
  }
}
