package mn.erin.domain.bpm.usecase.collateral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.process_parameter.GetProcessParameterByNameAndNameValueAndEntity;
import mn.erin.domain.bpm.usecase.process.process_parameter.ProcessParameterByNameAndEntityInput;

/**
 * @author Lkhagvadorj
 */
public class GetBpmCollateralByCustNumberTest
{
    private ProcessRepository processRepository;
    private GetBpmCollateralByCustNumber useCase;
    private GetProcessParameterByNameAndNameValueAndEntity processParameterByNameAndEntity;
    private static final String CIF_NUMBER = "C12345";

    @Before
    public void setUp()
    {
        processRepository = Mockito.mock(ProcessRepository.class);
        useCase = new GetBpmCollateralByCustNumber(processRepository);
        processParameterByNameAndEntity = new GetProcessParameterByNameAndNameValueAndEntity(processRepository);
    }

    @Test(expected = UseCaseException.class)
    public void when_throws_exception() throws UseCaseException
    {
        Mockito.when(processParameterByNameAndEntity.execute(null)).thenThrow(UseCaseException.class);
        useCase.execute(CIF_NUMBER);
    }

    @Test
    public void when_return_value() throws UseCaseException {
        List<Process> processes = new ArrayList<>();
        processes.add( new Process(null, null, null, null, null, Collections.emptyMap()) );
        ProcessParameterByNameAndEntityInput processParameterInput = new ProcessParameterByNameAndEntityInput(BpmModuleConstants.CIF_NUMBER, CIF_NUMBER , ParameterEntityType.COLLATERAL);
        Mockito.when(processParameterByNameAndEntity.execute(processParameterInput)).thenReturn( processes );
        GetCollateralsByCustNumberOutput output = useCase.execute(CIF_NUMBER);
        Assert.assertNotNull(output.getCollaterals());
    }

    @Test
    public void when_return_null() throws UseCaseException {
        ProcessParameterByNameAndEntityInput processParameterInput = new ProcessParameterByNameAndEntityInput(BpmModuleConstants.CIF_NUMBER, CIF_NUMBER , ParameterEntityType.COLLATERAL);
        Mockito.when(processParameterByNameAndEntity.execute(processParameterInput)).thenReturn( Collections.emptyList() );
        GetCollateralsByCustNumberOutput output = useCase.execute(CIF_NUMBER);
        Assert.assertEquals(Collections.emptyList(), output.getCollaterals());
    }


}
