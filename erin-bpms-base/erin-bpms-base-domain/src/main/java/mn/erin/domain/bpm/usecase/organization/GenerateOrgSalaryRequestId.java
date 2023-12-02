package mn.erin.domain.bpm.usecase.organization;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;

import static mn.erin.domain.bpm.BpmMessagesConstants.BRANCH_ID_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.ORG_REG_NUM_NULL_ERROR_CODE;


/**
 * @author Bilguunbor
 */
public class GenerateOrgSalaryRequestId extends AbstractUseCase<GenerateOrgRequestIdInput, String>
{
  @Override
  public String execute(GenerateOrgRequestIdInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getBranchId()))
    {
      throw new UseCaseException(BRANCH_ID_NULL_ERROR_CODE, "Branch id is missing in the input!");
    }
    else if (StringUtils.isBlank(input.getRegNumber()))
    {
      throw new UseCaseException(ORG_REG_NUM_NULL_ERROR_CODE, "Salary organisation register number is missing in the input!");
    }

    String branchId = input.getBranchId();
    String registrationNumber = input.getRegNumber();

    return 1 + branchId + registrationNumber;
  }
}
