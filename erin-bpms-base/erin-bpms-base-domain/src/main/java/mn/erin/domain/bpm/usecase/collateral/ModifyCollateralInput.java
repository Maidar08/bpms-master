package mn.erin.domain.bpm.usecase.collateral;

import java.util.Map;

/**
 * @author Lkhagvadorj.A
 **/

public class ModifyCollateralInput
{
    private String instanceId;
    private String collateralId;
    private Map<String, Object> properties;

    public ModifyCollateralInput(String instanceId, String collateralId, Map<String, Object> properties)
    {
        this.instanceId = instanceId;
        this.collateralId = collateralId;
        this.properties = properties;
    }

    public String getCollateralId()
    {
        return collateralId;
    }

    public void setCollateralId(String collateralId)
    {
        this.collateralId = collateralId;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    public String getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(String instanceId)
    {
        this.instanceId = instanceId;
    }
}
