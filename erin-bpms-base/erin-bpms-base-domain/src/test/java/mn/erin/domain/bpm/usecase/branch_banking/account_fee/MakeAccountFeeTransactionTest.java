package mn.erin.domain.bpm.usecase.branch_banking.account_fee;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.branch_banking.MakeAccountFeeTransactionInput;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.usecase.branch_banking.transaction.MakeAccountFeeTransaction;

public class MakeAccountFeeTransactionTest
{
    private MakeAccountFeeTransaction useCase;
    private  BranchBankingService branchBankingService;

    @Before
    public  void setup()
    {
        branchBankingService = Mockito.mock(BranchBankingService.class);
        useCase = new MakeAccountFeeTransaction(branchBankingService);
    }
    @Test(expected = UseCaseException.class)
    public  void when_input_null() throws UseCaseException
    {
        useCase.execute(null);
    }

    @Test(expected = UseCaseException.class)
    public  void  when_instance_id_is_blank()throws UseCaseException
    {
        MakeAccountFeeTransactionInput input = new MakeAccountFeeTransactionInput("", "123","123", "123", Collections.emptyList());
        useCase.execute(input);
    }

    @Test(expected = UseCaseException.class)
    public  void when_throw_BpmServiceException() throws UseCaseException, BpmServiceException {
        Mockito.when(branchBankingService.makeAccountFeeTransactionTask(Collections.emptyList(), "123","123","123","123")).thenThrow(BpmServiceException.class);
        MakeAccountFeeTransactionInput input = new MakeAccountFeeTransactionInput("123", "123","123", "123", Collections.emptyList());
        useCase.execute(input);
    }

    @Test
    public  void when_branch_banking_service_successful() throws UseCaseException, BpmServiceException
    {
        Map<String, String> output = new HashMap<>();
        output.put("testKey", "testValue");
        Mockito.when(branchBankingService.makeAccountFeeTransactionTask(Collections.emptyList(), "123","123","123","123"))
                .thenReturn(output);
        MakeAccountFeeTransactionInput input = new MakeAccountFeeTransactionInput("123", "123","123", "123", Collections.emptyList());
        Map<String, String> actualMap = useCase.execute(input);


        Assert.assertEquals(actualMap.size(), 1);
        Assert.assertEquals(actualMap.get("testKey"), "testValue");
    }
}
