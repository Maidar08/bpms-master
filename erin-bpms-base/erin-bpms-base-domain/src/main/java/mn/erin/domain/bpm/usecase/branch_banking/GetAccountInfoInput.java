package mn.erin.domain.bpm.usecase.branch_banking;

/**
 * @author Lkhagvadorj.A
 **/

public class GetAccountInfoInput
{
    private String instanceId;
    private String accountId;
    private boolean hasAccountValidation;

    public GetAccountInfoInput(String instanceId, String accountId, boolean hasAccountValidation)
    {
        this.instanceId = instanceId;
        this.accountId = accountId;
        this.hasAccountValidation = hasAccountValidation;
    }

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public boolean isHasAccountValidation() { return hasAccountValidation; }

    public void setHasAccountValidation(boolean hasAccountValidation) { this.hasAccountValidation = hasAccountValidation; }
}
