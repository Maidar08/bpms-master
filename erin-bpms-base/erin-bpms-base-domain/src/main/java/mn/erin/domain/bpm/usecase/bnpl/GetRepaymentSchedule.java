package mn.erin.domain.bpm.usecase.bnpl;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.ACCOUNT_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ACCOUNT_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REPAYMENT_SCHEDULE_TYPE_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.REPAYMENT_SCHEDULE_TYPE_NULL_MESSAGE;

public class GetRepaymentSchedule extends AbstractUseCase<Map<String, String>, Map<String, Object>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRepaymentSchedule.class);
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;

  public GetRepaymentSchedule(DirectOnlineCoreBankingService directOnlineCoreBankingService)
  {
    this.directOnlineCoreBankingService = Objects.requireNonNull(directOnlineCoreBankingService);
  }
@Override
  public Map<String, Object> execute(Map<String, String> input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    String acid = input.get("acid");
    String project = input.get("project");

    Map<String, Object> repaymentSchedule;

    try{
      validate(acid, project);
      repaymentSchedule = directOnlineCoreBankingService.getRepaymentSchedule(input);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return repaymentSchedule;
  }
  private void validate(String acid, String project) throws UseCaseException
  {
    if (StringUtils.isBlank(acid))
    {
      throw new UseCaseException(ACCOUNT_NUMBER_NULL_CODE, ACCOUNT_NUMBER_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(project))
    {
      throw new UseCaseException(REPAYMENT_SCHEDULE_TYPE_IS_NULL_CODE, REPAYMENT_SCHEDULE_TYPE_NULL_MESSAGE);
    }
  }
}
