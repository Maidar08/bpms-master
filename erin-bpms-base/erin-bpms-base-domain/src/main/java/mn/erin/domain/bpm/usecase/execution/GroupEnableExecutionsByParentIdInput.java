package mn.erin.domain.bpm.usecase.execution;

import java.util.List;

import mn.erin.domain.bpm.model.cases.Execution;

/**
 * @author Lkhagvadorj.A
 **/

public class GroupEnableExecutionsByParentIdInput
{
    private List<Execution> activeExecutionList;
    private List<Execution> enabledExecutionList;

    public GroupEnableExecutionsByParentIdInput(List<Execution> activeExecutionList,
        List<Execution> enabledExecutionList)
    {
        this.activeExecutionList = activeExecutionList;
        this.enabledExecutionList = enabledExecutionList;
    }

    public List<Execution> getActiveExecutionList()
    {
        return activeExecutionList;
    }

    public void setActiveExecutionList(List<Execution> activeExecutionList)
    {
        this.activeExecutionList = activeExecutionList;
    }

    public List<Execution> getEnabledExecutionList()
    {
        return enabledExecutionList;
    }

    public void setEnabledExecutionList(List<Execution> enabledExecutionList)
    {
        this.enabledExecutionList = enabledExecutionList;
    }
}
