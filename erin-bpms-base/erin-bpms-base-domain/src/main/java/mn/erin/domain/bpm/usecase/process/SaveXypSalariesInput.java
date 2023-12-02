package mn.erin.domain.bpm.usecase.process;

import java.util.List;

import mn.erin.domain.bpm.model.salary.SalaryInfo;

public class SaveXypSalariesInput {
    private List<SalaryInfo> salaries;
    private String processInstanceId;
    public SaveXypSalariesInput(List<SalaryInfo> salaries, String processInstanceId)
    {
        this.salaries = salaries;
        this.processInstanceId = processInstanceId;
    }

    public void setSalaries(List<SalaryInfo> salaries) {
        this.salaries = salaries;
    }

    public List<SalaryInfo> getSalaries() {
        return salaries;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }
}
