package mn.erin.domain.bpm.usecase.process;

import java.io.Serializable;
import java.util.Map;

public class GetXypSalariesOutput {
    private Map<String, Map<String, Serializable>> xypSalary;
    public GetXypSalariesOutput(Map<String, Map<String, Serializable>> xypSalary)
    {
        this.xypSalary = xypSalary;
    }

    public void setXypSalary(Map<String, Map<String, Serializable>> xypSalary) {
        this.xypSalary = xypSalary;
    }

    public Map<String, Map<String, Serializable>> getXypSalary() {
        return xypSalary;
    }
}
