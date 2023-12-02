package mn.erin.domain.bpm.usecase.collateral;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

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
import static mn.erin.domain.bpm.util.process.BpmUtils.convertVariableListToMap;
import static mn.erin.domain.bpm.util.process.CollateralUtils.toOtherCollateralInfoMap;

public class ModifyOtherCollateral extends AbstractUseCase<ModifyCollateralInput, String>
{
  private static final Logger LOG = LoggerFactory.getLogger(ModifyOtherCollateral.class);
  public final NewCoreBankingService newCoreBankingService;
  public final CaseService caseService;

  public ModifyOtherCollateral(NewCoreBankingService newCoreBankingService, CaseService caseService)
  {
    this.newCoreBankingService = newCoreBankingService;
    this.caseService = caseService;
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
      String collateralId = input.getCollateralId();
      Map<String, Object> properties = input.getProperties();
      List<Variable> caseVariables = caseService.getVariables(input.getInstanceId());

      CreateCollateralInput collateralInput = toOtherCollateralInfoMap(properties, convertVariableListToMap(caseVariables));
      Map<String, Object> genericInfo = collateralInput.getGenericInfo();
      Map<String, Object> collateralInfo = collateralInput.getCollateralInfo();
      Map<String, Object> inspectionInfo = collateralInput.getInspectionInfo();
      Map<String, Object> ownershipInfo = collateralInput.getOwnershipInfo();

      String colId = newCoreBankingService.modifyOtherCollateral(collateralId, genericInfo, collateralInfo, inspectionInfo, ownershipInfo);
      LOG.info("###### SUCCESSFUL MODIFIED OTHER PROPERTY COLLATERAL = [{}]", colId);
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
}
