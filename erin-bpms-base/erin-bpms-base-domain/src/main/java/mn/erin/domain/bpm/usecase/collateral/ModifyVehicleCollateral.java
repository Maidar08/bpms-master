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
import mn.erin.domain.bpm.util.process.CollateralUtils;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.util.process.BpmUtils.convertVariableListToMap;

public class ModifyVehicleCollateral extends AbstractUseCase<ModifyCollateralInput, String>
{
  public final NewCoreBankingService newCoreBankingService;
  public final CaseService caseService;
  private static final Logger LOG = LoggerFactory.getLogger(ModifyVehicleCollateral.class);

  public ModifyVehicleCollateral(NewCoreBankingService newCoreBankingService, CaseService caseService)
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
      List<Variable> caseVariables = caseService.getVariables(input.getInstanceId());
      String collateralId = input.getCollateralId();
      Map<String, Object> properties = input.getProperties();

      CreateCollateralInput collateralInput = CollateralUtils.toVehicleCollateralMap(properties, convertVariableListToMap(caseVariables));

      Map<String, Object> genericInfo = collateralInput.getGenericInfo();
      Map<String, Object> collateralInfo = collateralInput.getCollateralInfo();
      Map<String, Object> inspectionInfo = collateralInput.getInspectionInfo();
      Map<String, Object> ownershipInfo = collateralInput.getOwnershipInfo();

      String colId = newCoreBankingService.modifyVehicleCollateral(collateralId, genericInfo, collateralInfo, inspectionInfo, ownershipInfo);
      LOG.info("###### SUCCESSFUL MODIFIED VEHICLE PROPERTY COLLATERAL = [{}]", colId);
      return colId;
    }
    catch (BpmServiceException | ParseException e)
    {
      e.printStackTrace();
    }
    return null;
  }
}
