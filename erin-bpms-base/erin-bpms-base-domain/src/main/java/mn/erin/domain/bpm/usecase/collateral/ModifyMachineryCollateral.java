package mn.erin.domain.bpm.usecase.collateral;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.NewCoreBankingService;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARSE_DATE_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARSE_DATE_ERROR_MESSAGE;
import static mn.erin.domain.bpm.util.process.CollateralUtils.toMachineryCollateralInfoMap;

/**
 * @author Lkhagvadorj.A
 **/

public class ModifyMachineryCollateral extends AbstractUseCase<ModifyCollateralInput, String>
{
    private final NewCoreBankingService newCoreBankingService;
    private final CaseService caseService;
    private static final Logger LOG = LoggerFactory.getLogger(ModifyMachineryCollateral.class);

    public ModifyMachineryCollateral(NewCoreBankingService newCoreBankingService, CaseService caseService)
    {
        this.newCoreBankingService = Objects.requireNonNull(newCoreBankingService, "New core banking service is required!");
        this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    }

    @Override
    public String execute(ModifyCollateralInput input) throws UseCaseException
    {
        if (null == input)
        {
            throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
        }

        try
        {
            List<Variable> caseVariables = caseService.getVariables(input.getInstanceId());
            String collateralId = input.getCollateralId();
            Map<String, Object> properties = input.getProperties();

            CreateCollateralInput collateralInput = toMachineryCollateralInfoMap(properties, toMap(caseVariables));
            Map<String, Object> genericInfo = collateralInput.getGenericInfo();
            Map<String, Object> collateralInfo = collateralInput.getCollateralInfo();
            Map<String, Object> inspectionInfo = collateralInput.getInspectionInfo();
            Map<String, Object> ownershipInfo = collateralInput.getOwnershipInfo();

            String colId = newCoreBankingService.modifyMachineryCollateral(collateralId, genericInfo, collateralInfo, inspectionInfo, ownershipInfo);
            LOG.info("###### SUCCESSFUL MODIFIED MACHINERY COLLATERAL = [{}]", colId);
            return colId;
        }
        catch (BpmServiceException e)
        {
            throw new UseCaseException(e.getCode(), e.getMessage());
        }
        catch (ParseException e)
        {
            throw new UseCaseException(PARSE_DATE_ERROR_CODE, PARSE_DATE_ERROR_MESSAGE);
        }
    }

    private Map<String, Object> toMap(List<Variable> variables)
    {
        Map<String, Object> variableMap = new HashMap<>();
        for (Variable variable : variables)
        {
            variableMap.put(variable.getId().getId(), variable.getValue());
        }
        return variableMap;
    }
}
