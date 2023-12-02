package mn.erin.domain.bpm.usecase.direct_online;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.UseCaseException;

import static mn.erin.domain.bpm.BpmMessagesConstants.CIF_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CIF_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_REGISTER_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CUSTOMER_REGISTER_NUMBER_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class GetLoanInfoInput
{
  private final String registerNumber;
  private final String cif;
  private final String processTypeId;

  public GetLoanInfoInput(String registerNumber, String cif, String processTypeId) throws UseCaseException
  {
    this.processTypeId = processTypeId;
    validate(cif, registerNumber);
    this.cif = cif;
    this.registerNumber = registerNumber;
  }

  public String getRegisterNumber()
  {
    return registerNumber;
  }

  public String getCif()
  {
    return cif;
  }

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  private void validate(String cif, String registerNumber) throws UseCaseException
  {
    if (StringUtils.isBlank(cif))
    {
      throw new UseCaseException(CIF_NULL_CODE, CIF_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(registerNumber))
    {
      throw new UseCaseException(CUSTOMER_REGISTER_NUMBER_NULL_CODE, CUSTOMER_REGISTER_NUMBER_NULL_MESSAGE);
    }
  }
}
