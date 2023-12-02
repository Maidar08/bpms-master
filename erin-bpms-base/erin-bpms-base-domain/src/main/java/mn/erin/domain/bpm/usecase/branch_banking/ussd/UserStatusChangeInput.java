package mn.erin.domain.bpm.usecase.branch_banking.ussd;

public class UserStatusChangeInput
{
    private final String mobileNumber;
    private final String instanceId;
    private final String type;

    public UserStatusChangeInput(String mobileNumber, String instanceId, String type)
    {
        this.mobileNumber = mobileNumber;
        this.instanceId = instanceId;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getMobileNumber()
    {
        return mobileNumber;
    }

    public String getInstanceId()
    {
        return instanceId;
    }
}
