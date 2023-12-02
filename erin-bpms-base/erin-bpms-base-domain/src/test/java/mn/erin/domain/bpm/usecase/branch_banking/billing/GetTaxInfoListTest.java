package mn.erin.domain.bpm.usecase.branch_banking.billing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.TaxInvoice;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Lkhagvadorj.A
 **/

public class GetTaxInfoListTest
{
  private BranchBankingService branchBankingService;
  private GetTaxInfoList useCase;

  @Before
  public void setUp()
  {
    branchBankingService = Mockito.mock(BranchBankingService.class);
    useCase = new GetTaxInfoList(branchBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_empty() throws UseCaseException
  {
    Map<String, Object> emptyMap = new HashMap<>();
    useCase.execute(emptyMap);
  }

  @Test
  public void when_return_success() throws BpmServiceException, UseCaseException
  {
    Mockito.when(branchBankingService.getTaxInfoList("type", "value", "123")).thenReturn(getTaxInvoiceList());

    Map<String, Object> input = new HashMap<>();
    input.put("type", "type");
    input.put("value", "value");
    input.put(CASE_INSTANCE_ID, "123");
    List<TaxInvoice> taxInvoices = useCase.execute(input);
    Assert.assertThat(taxInvoices, hasSize(1));
  }

  private List<TaxInvoice> getTaxInvoiceList()
  {
    List<TaxInvoice> taxInvoices = new ArrayList<>();
    taxInvoices.add(new TaxInvoice("invoiceNumber", "invoiceDate", "invoiceType", "amount"));

    return taxInvoices;
  }
}
