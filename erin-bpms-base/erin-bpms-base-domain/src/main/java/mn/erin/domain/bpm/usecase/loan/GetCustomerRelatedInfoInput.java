package mn.erin.domain.bpm.usecase.loan;

/**
 * @author Lkhagvadorj.A
 **/

public class GetCustomerRelatedInfoInput
{
    private String customerCid;
    private String relation;

    public GetCustomerRelatedInfoInput(String customerCid, String relation)
    {
        this.customerCid = customerCid;
        this.relation = relation;
    }

    public String getCustomerCid()
    {
        return customerCid;
    }

    public void setCustomerCid(String customerCid)
    {
        this.customerCid = customerCid;
    }

    public String getRelation()
    {
        return relation;
    }

    public void setRelation(String relation)
    {
        this.relation = relation;
    }
}
