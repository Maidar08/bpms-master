package mn.erin.domain.bpm.usecase.collateral;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.collateral.GetGeneratedCollateralId;
import mn.erin.domain.bpm.usecase.process.process_parameter.ProcessParameterByNameAndEntityInput;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID_PREFIX;

public class GetGeneratedCollateralIdTest {
    private ProcessRepository processRepository;
    private GetGeneratedCollateralId useCase;

    @Before
    public void setUp()
    {
        processRepository = Mockito.mock(ProcessRepository.class);
        useCase = new GetGeneratedCollateralId(processRepository);
    }

    @Test
    public void when_process_parameters_return_null() throws UseCaseException {
        String cifNumber = "001234";
        ParameterEntityType entityType = ParameterEntityType.COLLATERAL;
        ProcessParameterByNameAndEntityInput input = new ProcessParameterByNameAndEntityInput(CIF_NUMBER, cifNumber, entityType);
        Mockito.when(processRepository.filterByParameterNameAndNameValueAndEntityType(entityType, "", "")).thenReturn(null);

        String collateralId = useCase.execute(input);
        Assert.assertEquals("BPMS001234001", collateralId);
    }

    @Test
    public void when_process_parameters_return_value() throws UseCaseException {
        String cifNumber = "001234";
        ParameterEntityType entityType = ParameterEntityType.COLLATERAL;
        ProcessParameterByNameAndEntityInput input = new ProcessParameterByNameAndEntityInput(CIF_NUMBER, cifNumber, entityType);
        List<Process> processes = getProcessList( COLLATERAL_ID_PREFIX + cifNumber + "001" );
        Mockito.doReturn(processes).when(processRepository).filterByParameterNameAndNameValueAndEntityType(entityType, CIF_NUMBER, cifNumber);


        String collateralId = useCase.execute(input);
        Assert.assertEquals("BPMS001234002", collateralId);
    }

    private List<Process> getProcessList(String collateralId)
    {
        Map<ParameterEntityType, Map<String, Serializable>> processParameters = new HashMap<>();
        processParameters.put(ParameterEntityType.COLLATERAL, new HashMap<>());

        JSONObject jsonObject= new JSONObject();
        jsonObject.put(CIF_NUMBER, "001234");
        jsonObject.put(COLLATERAL_ID, collateralId);
        String parameterName = jsonObject.toString();

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put(COLLATERAL_ID, collateralId);
        String parameterValue = jsonObject1.toString();

        processParameters.get(ParameterEntityType.COLLATERAL).put(parameterName, parameterValue);
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(null, null, null, null, null, processParameters));
        return processes;
    }
}



