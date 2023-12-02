package mn.erin.domain.bpm.service;

import java.util.Map;

/**
 * @author Lkhagvadorj.A
 **/

public interface RuntimeService {
    /**
     * Suspends process by process instance id
     * @param processInstanceId Unique process instance id
     */
    void suspendProcess(String processInstanceId);

    /**
     *  Gracefully cancelling a process instance
     *
     * @param processInstanceId Unique process instance id
     */
    void closeProcess(String processInstanceId);

    void setVariable(String processInstanceId, String variableName, Object value);

    void setVariables(String executionId, Map<String, Object> variables);

    Object getVariableById(String instanceId, String name);

    Map<String, Object>  getRuntimeVariables(String instanceId) throws BpmServiceException;

}
