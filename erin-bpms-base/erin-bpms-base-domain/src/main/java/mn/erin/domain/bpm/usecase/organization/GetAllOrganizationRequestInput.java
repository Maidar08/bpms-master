package mn.erin.domain.bpm.usecase.organization;

import java.util.Date;

public class GetAllOrganizationRequestInput {
    private Date startDate;
    private Date endDate;

    public GetAllOrganizationRequestInput(Date startDate, Date endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }
}

