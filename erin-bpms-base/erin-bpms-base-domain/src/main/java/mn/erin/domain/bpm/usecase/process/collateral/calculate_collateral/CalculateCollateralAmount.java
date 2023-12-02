package mn.erin.domain.bpm.usecase.process.collateral.calculate_collateral;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.bpm.BpmMessagesConstants.FIXED_ACCEPTED_LOAN_AMOUNT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.FIXED_ACCEPTED_LOAN_AMOUNT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_VALUE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_VALUE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.WRONG_COLLATERAL_LOAN_AMOUNT_VALUE_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.WRONG_COLLATERAL_LOAN_AMOUNT_VALUE_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.AMOUNT_OF_ASSESSMENT;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_CONNECTION_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;

/**
 * @author Lkhagvadorj
 */
public class CalculateCollateralAmount extends AbstractUseCase< List<Map<String, Object>>, List<Map<String, Object>> >
{
  @Override
  public List<Map<String, Object>> execute(List<Map<String, Object>> input) throws UseCaseException
  {
    for (Map<String, Object> taskFormFields: input)
    {
      if (!taskFormFields.containsKey(FIXED_ACCEPTED_LOAN_AMOUNT_STRING))
      {
        throw new UseCaseException(FIXED_ACCEPTED_LOAN_AMOUNT_NULL_CODE, FIXED_ACCEPTED_LOAN_AMOUNT_NULL_MESSAGE);
      }
      if (taskFormFields.containsKey(COLLATERAL_LOAN_AMOUNT) && taskFormFields.containsKey(COLLATERAL_CONNECTION_RATE))
      {
        calculateCollateralAmount(taskFormFields);
      }
    }

    return input;
  }


  private void calculateCollateralAmount(Map<String, Object> taskFormFields) throws UseCaseException
  {
    int acceptedLoanAmount;
    int loanAmount;

    final Object acceptedLoanObject = taskFormFields.get(FIXED_ACCEPTED_LOAN_AMOUNT_STRING);
    Object loanAmountObject = taskFormFields.get(COLLATERAL_LOAN_AMOUNT);

    if (null == acceptedLoanObject || null == loanAmountObject)
    {
      throw new UseCaseException(PARAMETER_VALUE_NULL_CODE, PARAMETER_VALUE_NULL_MESSAGE);
    }

    acceptedLoanAmount = Integer.parseInt(String.valueOf(acceptedLoanObject));
    loanAmount = Integer.parseInt(String.valueOf(loanAmountObject));

    if (acceptedLoanAmount < 0)
    {
      throw new UseCaseException(PARAMETER_VALUE_NULL_CODE, PARAMETER_VALUE_NULL_MESSAGE);
    }

    if (acceptedLoanAmount < loanAmount)
    {
      throw new UseCaseException(WRONG_COLLATERAL_LOAN_AMOUNT_VALUE_CODE, WRONG_COLLATERAL_LOAN_AMOUNT_VALUE_MESSAGE);
    }

    if (taskFormFields.containsKey(AMOUNT_OF_ASSESSMENT) && null != taskFormFields.get(AMOUNT_OF_ASSESSMENT))
    {
      int amountOfAssessment = Integer.parseInt(String.valueOf(taskFormFields.get(AMOUNT_OF_ASSESSMENT)));
      if (amountOfAssessment < loanAmount)
      {
        throw new UseCaseException(WRONG_COLLATERAL_LOAN_AMOUNT_VALUE_CODE, WRONG_COLLATERAL_LOAN_AMOUNT_VALUE_MESSAGE);
      }
    }

    double rate = Double.parseDouble(new DecimalFormat("##.00").format((double) loanAmount * 100 / acceptedLoanAmount));
    taskFormFields.put( COLLATERAL_CONNECTION_RATE, rate );

  }

}
