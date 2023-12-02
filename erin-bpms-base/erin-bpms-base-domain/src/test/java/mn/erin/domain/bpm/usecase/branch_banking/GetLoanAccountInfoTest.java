package mn.erin.domain.bpm.usecase.branch_banking;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;

public class GetLoanAccountInfoTest
{
    private BranchBankingService service;

    @Before
    public void setUp()
    {
        service = Mockito.mock(BranchBankingService.class);
    }

    @Test(expected = UseCaseException.class)
    public void whenAccIdIsNull() throws UseCaseException
    {
        GetLoanAccountInfo useCase = new GetLoanAccountInfo(service, "instanceId");
        useCase.execute(null);
    }

    @Test(expected = UseCaseException.class)
    public void whenBpmsServiceException() throws UseCaseException, BpmServiceException
    {
        GetLoanAccountInfo useCase = new GetLoanAccountInfo(service, "instanceId");
        Mockito.when(service.getLoanAccountInfo("accId", "instanceId")).thenThrow(new BpmServiceException("BB01", "Unable to get loan account info"));
        useCase.execute("accId");
    }

    @Test
    public void when_works_successful() throws UseCaseException, BpmServiceException
    {
        Map<String, Object> resp = new HashMap<>();
        resp.put("loanAccountInfo", "loanAccountInfo");
        GetLoanAccountInfo useCase = new GetLoanAccountInfo(service, "instanceId");
        Mockito.when(service.getLoanAccountInfo("accId", "instanceId")).thenReturn(resp);
        Assert.assertEquals(resp, useCase.execute("accId"));
    }
}