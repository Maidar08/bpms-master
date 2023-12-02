package mn.erin.domain.bpm.usecase.process.collateral;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.process_parameter.GetProcessParameterByNameAndNameValueAndEntity;
import mn.erin.domain.bpm.usecase.process.process_parameter.ProcessParameterByNameAndEntityInput;

import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID_PREFIX;

/**
 * @author Lkhagvadorj.A
 */

public class GetGeneratedCollateralId extends AbstractUseCase<ProcessParameterByNameAndEntityInput, String>
{
    private final ProcessRepository processRepository;

    public GetGeneratedCollateralId(ProcessRepository processRepository)
    {
        this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
    }

    @Override
    public String execute(ProcessParameterByNameAndEntityInput input) throws UseCaseException
    {
        String parameterNameValue = input.getParameterNameValue();
        String parameterName = input.getParameterName();
        ParameterEntityType entityType = input.getEntityType();

        ProcessParameterByNameAndEntityInput processParameterInput = new ProcessParameterByNameAndEntityInput(parameterName, parameterNameValue, entityType);
        GetProcessParameterByNameAndNameValueAndEntity useCase = new GetProcessParameterByNameAndNameValueAndEntity(processRepository);


        List<Process> processes = useCase.execute(processParameterInput);
        return generateId(processes, parameterNameValue);
    }

    private String generateId(List<Process> processes, String cifNumber)
    {
        String newCollateralId;
        if (processes.isEmpty())
        {
            newCollateralId = COLLATERAL_ID_PREFIX + cifNumber + COLLATERAL_ID_NUMBER;
        }
        else
        {
            List<String> collateralIdList = new ArrayList<>();
            for (Process process :processes)
            {
                Map<ParameterEntityType, Map<String, Serializable>> processParameters = process.getProcessParameters();
                if (processParameters.containsKey(ParameterEntityType.COLLATERAL))
                {
                    processParameters.get(ParameterEntityType.COLLATERAL).forEach( (key, value) ->
                    {
                        String collateralId = (String) new JSONObject(value.toString()).get(COLLATERAL_ID);
                        if (!StringUtils.isBlank(collateralId))
                        {
                            collateralIdList.add(collateralId);
                        }
                    });
                }
            }
            if (collateralIdList.isEmpty())
            {
                newCollateralId = COLLATERAL_ID_PREFIX + cifNumber + COLLATERAL_ID_NUMBER;
            }
            else
                {
                Collections.sort(collateralIdList);
                String lastGeneratedCollateralId = collateralIdList.get( collateralIdList.size()-1 );
                newCollateralId = generateCollateralId(lastGeneratedCollateralId, cifNumber);
            }
        }
        return newCollateralId;
    }

    private String generateCollateralId(String lastGeneratedCollateralId, String cifNumber)
    {
        int beginIndex = lastGeneratedCollateralId.length() - 3;
        int endIndex = lastGeneratedCollateralId.length();
        int index = Integer.parseInt(lastGeneratedCollateralId.substring(beginIndex, endIndex)) + 1;
        String stringIndex = "";
        if (index < 10)
        {
            stringIndex = "00"+index;
        }
        else if (index < 100)
        {
            stringIndex = "0"+index;
        }
        else
        {
            stringIndex = String.valueOf(index);
        }
        return COLLATERAL_ID_PREFIX + cifNumber + stringIndex;
    }
}
