package mn.erin.domain.bpm.usecase.process;

import java.util.List;

import mn.erin.domain.bpm.model.process.ProcessType;

public class GetProcessTypesByCategoryOutput {
    private List<ProcessType> processTypes;

    public GetProcessTypesByCategoryOutput(List<ProcessType> processTypes) {
        this.processTypes = processTypes;

    }

    public List<ProcessType> getProcessTypes() {
        return processTypes;
    }

    public void setProcessTypes(List<ProcessType> processTypes) {
        this.processTypes = processTypes;
    }
}
