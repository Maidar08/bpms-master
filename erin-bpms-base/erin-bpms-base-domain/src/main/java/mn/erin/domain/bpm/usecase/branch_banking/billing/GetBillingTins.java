package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.BB_REGISTER_NUMBER_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_REGISTER_NUMBER_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CASE_INSTANCE_ID_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

public class GetBillingTins extends AbstractUseCase<GetBillingTinsInput, List<FieldProperty>>
{
  private final BranchBankingService branchBankingService;
  private static final Logger LOGGER = LoggerFactory.getLogger(GetBillingTins.class);

  public GetBillingTins(BranchBankingService branchBankingService)
  {
    this.branchBankingService = Objects.requireNonNull(branchBankingService);
  }

  @Override
  public List<FieldProperty> execute(GetBillingTinsInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getRegisterNumber()))
    {
      throw new UseCaseException(BB_REGISTER_NUMBER_NULL_CODE, BB_REGISTER_NUMBER_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getInstanceId()))
    {
      throw new UseCaseException(CASE_INSTANCE_ID_NULL_CODE, CASE_INSTANCE_ID_NULL_MESSAGE);
    }

    try
    {
      String instanceId = input.getInstanceId();
      String registerNumber = input.getRegisterNumber();
      Map<String, String> taxInvoices = branchBankingService.getTinList(registerNumber, instanceId);

      List<FieldProperty> taxInvoiceOptions = new ArrayList<>();
      taxInvoices.forEach( (k,v) -> taxInvoiceOptions.add(new FieldProperty(k, k)) );
      //TODO: remove later
      LOGGER.info("############### TAX INVOICE OPTIONS [{}] WITH INSTANCE ID [{}]",  taxInvoiceOptions, instanceId);

      return taxInvoiceOptions;
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
