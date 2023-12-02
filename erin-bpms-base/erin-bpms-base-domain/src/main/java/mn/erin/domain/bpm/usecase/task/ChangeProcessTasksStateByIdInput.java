package mn.erin.domain.bpm.usecase.task;

import java.util.List;

/**
 * @author Lkhagvadorj.A
 **/

public class ChangeProcessTasksStateByIdInput
{
    private List<String> activeTaskIds;
    private List<String> enableProcessIds;
    private List<String> terminateProcessIds;
    private String instanceId;

    public ChangeProcessTasksStateByIdInput(List<String> activeTaskId, List<String> enableProcessId, List<String> terminateProcessId, String instanceId)
    {
        this.activeTaskIds = activeTaskId;
        this.enableProcessIds = enableProcessId;
        this.terminateProcessIds = terminateProcessId;
        this.instanceId = instanceId;
    }

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }

    public List<String> getActiveTaskIds()
    {
        return activeTaskIds;
    }

    public void setActiveTaskIds(List<String> activeTaskIds)
    {
        this.activeTaskIds = activeTaskIds;
    }

    public List<String> getEnableProcessIds()
    {
        return enableProcessIds;
    }

    public void setEnableProcessIds(List<String> enableProcessIds)
    {
        this.enableProcessIds = enableProcessIds;
    }

    public List<String> getTerminateProcessIds()
    {
        return terminateProcessIds;
    }

    public void setTerminateProcessIds(List<String> terminateProcessIds)
    {
        this.terminateProcessIds = terminateProcessIds;
    }
}
