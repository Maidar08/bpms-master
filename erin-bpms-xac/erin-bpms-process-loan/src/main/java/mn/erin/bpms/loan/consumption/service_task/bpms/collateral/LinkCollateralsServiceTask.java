package mn.erin.bpms.loan.consumption.service_task.bpms.collateral;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.collateral.LinkCollaterals;
import mn.erin.domain.bpm.usecase.collateral.LinkCollateralsInput;
import mn.erin.domain.bpm.usecase.collateral.LinkCollateralsOutput;
import mn.erin.domain.bpm.usecase.process.collateral.GetImmovableCollInfoMap;
import mn.erin.domain.bpm.usecase.process.collateral.GetMachineryCollInfoMap;
import mn.erin.domain.bpm.usecase.process.collateral.GetOtherCollInfoMap;
import mn.erin.domain.bpm.usecase.process.collateral.GetVehicleCollInfoMap;

import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_COLLATERAL_APPORTIONING_FLAG;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_COLLATERAL_CURRENCY;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_COLLATERAL_LINKAGE_TYPE;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_COLLATERAL_NATURE_IND;
import static mn.erin.bpm.domain.ohs.xac.XacConstants.PROPERTY_KEY_COLLATERAL_SELF_FLAG;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLL_LINKAGE_RESPONSE_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLL_LINKAGE_RESPONSE_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_CODE_FORM_FIELD_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.constants.CollateralConstants.IMMOVABLE_COLLATERAL_TYPE;
import static mn.erin.domain.bpm.constants.CollateralConstants.MACHINERY_COLLATERAL_TYPE;
import static mn.erin.domain.bpm.constants.CollateralConstants.OTHER_COLLATERAL_TYPE;
import static mn.erin.domain.bpm.constants.CollateralConstants.VEHICLE_COLLATERAL_TYPE;

/**
 * @author Tamir
 */
public class LinkCollateralsServiceTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(LinkCollateralsServiceTask.class);

  private static final String SRL_NUM = "SrlNum";
  private static final String COLTRL_TYPE = "ColtrlType";

  private static final String COLTRL_AMT_VAL = "ColtrlAmtVal";
  private static final String APPORT_AMT_VAL = "ApportAmtVal";

  private static final String MARGIN_PCNT = "MarginPcnt";
  private static final String COLTRL_AMT_CCY = "ColtrlAmtCCY";

  private static final String CRNCY = "Crncy";
  private static final String SEL_FLG = "SelFlg";
  private static final String COLTRL_CODE = "ColtrlCode";

  private static final String APPORT_AMT_CCY = "ApportAmtCCY";
  private static final String COLTRL_NATURE_IND = "ColtrlNatureInd";
  private static final String APPARTNG_METHOD = "AppartngMethod";

  private static final String AMOUNT_OF_ASSESSMENT = "amountOfAssessment";
  public static final String LOAN_AMOUNT = "loanAmount";
  public static final String COLLATERAL_CONNECTION_RATE = "collateralConnectionRate";
  public static final String ORDER = "order";

  private final Environment environment;
  private final NewCoreBankingService newCoreBankingService;

  public LinkCollateralsServiceTask(Environment environment, NewCoreBankingService newCoreBankingService)
  {
    this.environment = environment;
    this.newCoreBankingService = newCoreBankingService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String accountNumber = String.valueOf(execution.getVariable(LOAN_ACCOUNT_NUMBER));

    String linkageType = environment.getProperty(PROPERTY_KEY_COLLATERAL_LINKAGE_TYPE);
    String collCurrency = environment.getProperty(PROPERTY_KEY_COLLATERAL_CURRENCY);

    String natureInd = environment.getProperty(PROPERTY_KEY_COLLATERAL_NATURE_IND);
    String apportioningFlag = environment.getProperty(PROPERTY_KEY_COLLATERAL_APPORTIONING_FLAG);
    String selfFlag = environment.getProperty(PROPERTY_KEY_COLLATERAL_SELF_FLAG);

    Map<String, Object> executionVariables = execution.getVariables();

    Map<String, Object> collateralsMap = new HashMap<>();

    Map<String, Map<String, Object>> collaterals = (Map) execution.getVariable(COLLATERAL_LIST);

    for (Map.Entry<String, Map<String, Object>> collateral : collaterals.entrySet())
    {
      String collateralId = collateral.getKey();

      LOG.info("############ LINKAGE COLLATERAL ID = [{}]", collateralId);

      Map<String, Object> collInfoFromBPMS = collateral.getValue();
      Map<String, String> collDetails = new HashMap<>();

      Collateral collateralObj = (Collateral) executionVariables.get(collateralId);

      if (executionVariables.get(collateralId) instanceof Collateral)
      {
        String type = collateralObj.getType();
        LOG.info("############ LINKAGE COLLATERAL TYPE = [{}]", type);

        setCollateralCode(type, collateralId, collDetails);

        collDetails.put(SRL_NUM, String.valueOf(collInfoFromBPMS.get(ORDER)));
        collDetails.put(COLTRL_TYPE, type);

        // Барьцаа хөрөнгийн мөнгөн дүн
        collDetails.put(COLTRL_AMT_VAL, String.valueOf(collInfoFromBPMS.get(AMOUNT_OF_ASSESSMENT)));
        // Хуваалт хийсэн дүн
        collDetails.put(APPORT_AMT_VAL, String.valueOf(collInfoFromBPMS.get(LOAN_AMOUNT)));
        // Маржингийн хувь
        collDetails.put(MARGIN_PCNT, String.valueOf(collInfoFromBPMS.get(COLLATERAL_CONNECTION_RATE)));

        // Default 'MNT' value from properties
        collDetails.put(COLTRL_AMT_CCY, collCurrency);
        collDetails.put(CRNCY, collCurrency);
        collDetails.put(APPORT_AMT_CCY, collCurrency);

        // Default 'P' value from properties
        collDetails.put(COLTRL_NATURE_IND, natureInd);

        // Default 'Y' value from properties
        collDetails.put(APPARTNG_METHOD, apportioningFlag);
        collDetails.put(SEL_FLG, selfFlag);

        collateralsMap.put(collateralId, collDetails);
      }
    }

    LOG.info("########### LINKS COLLATERALS TO LOAN ACCOUNT WITH REQUEST ID = [{}], ACCOUNT NUMBER = [{}]", requestId, accountNumber);

    if (!linkCollaterals(accountNumber, linkageType, collateralsMap))
    {
      String errorMessage = String.format(COLL_LINKAGE_RESPONSE_ERROR_MESSAGE, requestId, accountNumber);
      throw new BpmServiceException(COLL_LINKAGE_RESPONSE_ERROR_CODE, errorMessage);
    }
    execution.setVariable("isLinked", true);
  }

  private void setCollateralCode(String collType, String collateralId, Map<String, String> collDetails) throws UseCaseException
  {
    if (IMMOVABLE_COLLATERAL_TYPE.equalsIgnoreCase(collType))
    {
      LOG.info("######### GETS IMMOVABLE COLLATERAL BY COLL ID = [{}]", collateralId);
      setImmovableCollCode(collateralId, collDetails);
    }
    else if (MACHINERY_COLLATERAL_TYPE.equalsIgnoreCase(collType))
    {
      LOG.info("######### GETS MACHINERY COLLATERAL BY COLL ID = [{}]", collateralId);
      setMachineryCollCode(collateralId, collDetails);
    }
    else if (VEHICLE_COLLATERAL_TYPE.equalsIgnoreCase(collType))
    {
      LOG.info("######### GETS VEHICLE COLLATERAL BY COLL ID = [{}]", collateralId);
      setVehicleCollCode(collateralId, collDetails);
    }
    else if (OTHER_COLLATERAL_TYPE.equalsIgnoreCase(collType))
    {
      LOG.info("######### GETS OTHER COLLATERAL BY COLL ID = [{}]", collateralId);
      setOtherCollCode(collateralId, collDetails);
    }
  }

  private void setImmovableCollCode(String collateralId, Map<String, String> collDetails) throws UseCaseException
  {
    GetImmovableCollInfoMap getImmovableCollInfoMap = new GetImmovableCollInfoMap(newCoreBankingService);
    Map<String, Object> colInfos = getImmovableCollInfoMap.execute(collateralId);

    collDetails.put(COLTRL_CODE, String.valueOf(colInfos.get(COLLATERAL_CODE_FORM_FIELD_ID)));
  }

  private void setMachineryCollCode(String collateralId, Map<String, String> collDetails) throws UseCaseException
  {
    GetMachineryCollInfoMap getMachineryCollInfoMap = new GetMachineryCollInfoMap(newCoreBankingService);
    Map<String, Object> colInfos = getMachineryCollInfoMap.execute(collateralId);

    collDetails.put(COLTRL_CODE, String.valueOf(colInfos.get(COLLATERAL_CODE_FORM_FIELD_ID)));
  }

  private void setVehicleCollCode(String collateralId, Map<String, String> collDetails) throws UseCaseException
  {
    GetVehicleCollInfoMap getVehicleCollInfoMap = new GetVehicleCollInfoMap(newCoreBankingService);
    Map<String, Object> colInfos = getVehicleCollInfoMap.execute(collateralId);

    collDetails.put(COLTRL_CODE, String.valueOf(colInfos.get(COLLATERAL_CODE_FORM_FIELD_ID)));
  }

  private void setOtherCollCode(String collateralId, Map<String, String> collDetails) throws UseCaseException
  {
    GetOtherCollInfoMap getOtherCollInfoMap = new GetOtherCollInfoMap(newCoreBankingService);
    Map<String, Object> colInfos = getOtherCollInfoMap.execute(collateralId);

    collDetails.put(COLTRL_CODE, String.valueOf(colInfos.get(COLLATERAL_CODE_FORM_FIELD_ID)));
  }

  private boolean linkCollaterals(String accountNumber, String linkageType, Map<String, Object> collateralsMap) throws UseCaseException
  {
    LinkCollateralsInput input = new LinkCollateralsInput(accountNumber, linkageType, collateralsMap);
    LinkCollaterals linkCollaterals = new LinkCollaterals(newCoreBankingService);

    LinkCollateralsOutput output = linkCollaterals.execute(input);

    return output.isLinked();
  }
}
