package mn.erin.bpms.loan.consumption.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.alfresco.connector.model.RestTextValueField;
import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.collateral.CollateralInfo;

import static mn.erin.bpms.loan.consumption.utils.NumberUtils.getThousandSeparatedString;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_INFO_NOT_FOUND_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_INFO_NOT_FOUND_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.BLANK;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_LIST;
import static mn.erin.domain.bpm.BpmModuleConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_ASSESSMENT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_PRODUCT_DESCRIPTION_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_TOTAL_AMOUNT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COLL_TOTAL_ASSESSMENT_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.COUNTRY_REGISTER_NUMBER_FIELD;
import static mn.erin.domain.bpm.BpmTemplateFieldConstants.OWNER_NAME_FIELD;

/**
 * @author Tamir
 */
public final class CollateralUtils
{
  private CollateralUtils()
  {

  }

  private static final Logger LOGGER = LoggerFactory.getLogger(CollateralUtils.class);

  public static Map<String, Serializable> getCollInfoFromVariable(String collateralId, DelegateExecution execution) throws ProcessTaskException
  {
    //get from execution collateral object
    Collateral collateral = (Collateral) execution.getVariable(collateralId);

    if (null != collateral)
    {
      CollateralInfo collateralInfo = collateral.getCollateralInfo();

      if (null != collateralInfo)
      {
        return getCollateralInfoMap(collateralInfo);
      }
    }
    else
    {
      String caseInstanceId = String.valueOf(execution.getVariable(CASE_INSTANCE_ID));

      Map<String, Object> caseVariables = execution.getProcessEngine().getCaseService().getVariables(caseInstanceId);

      Collateral collFromCaseVariable = (Collateral) caseVariables.get(collateralId);

      if (null != collFromCaseVariable)
      {
        CollateralInfo collateralInfo = collFromCaseVariable.getCollateralInfo();

        if (null != collateralInfo)
        {
          return getCollateralInfoMap(collateralInfo);
        }
      }
    }

    String collNotFoundMessage = String.format(COLLATERAL_INFO_NOT_FOUND_MESSAGE, collateralId);
    throw new ProcessTaskException(COLLATERAL_INFO_NOT_FOUND_CODE, collNotFoundMessage);
  }

  public static List<RestTextValueField> getCollateralTextFields(List<RestTextValueField> textFields, DelegateExecution execution)
  {
    if (!execution.hasVariable(COLLATERAL_LIST) || null == execution.getVariable(COLLATERAL_LIST) || execution.getVariable(COLLATERAL_LIST) instanceof Map)
    {
      return addEmptyCollateralInfos(textFields);
    }
    @SuppressWarnings("unchecked")
    List<Map<String, Serializable>> collaterals = (List<Map<String, Serializable>>) execution.getVariable(COLLATERAL_LIST);
    if (null == collaterals || collaterals.isEmpty())
    {
      return addEmptyCollateralInfos(textFields);
    }

    Map<String, Serializable> selectedCollaterals = collaterals.get(0);
    int index = 1;
    BigDecimal totalAssessmentAmount = BigDecimal.ZERO;
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (Map.Entry<String, Serializable> entry : selectedCollaterals.entrySet())
    {
      @SuppressWarnings("unchecked")
      Map<String, Boolean> checkedMap = (Map<String, Boolean>) entry.getValue();
      if (checkedMap.get("checked").equals(true))
      {
        String colId = entry.getKey();
        Collateral collateral = (Collateral) execution.getVariable(colId);
        String amountOfAssessment = String.valueOf(collateral.getAmountOfAssessment());
        CollateralInfo colSubInfo = collateral.getCollateralInfo();
        String amount = String.valueOf(colSubInfo.getAvailableAmount());
        List<String> ownerNames = collateral.getOwnerNames();
        String ownerName = null == ownerNames ? BLANK : StringUtils.join(ownerNames, ",");
        String stateRegistrationNumber = BLANK;
        if (null != colSubInfo.getStateRegistrationNumber() && !colSubInfo.getStateRegistrationNumber().equals("null"))
        {
          stateRegistrationNumber = colSubInfo.getStateRegistrationNumber();
        }

        setCollateralFieldWithIndex(textFields, COLL_PRODUCT_DESCRIPTION_FIELD, index, colId);
        setCollateralFieldWithIndex(textFields, COUNTRY_REGISTER_NUMBER_FIELD, index, stateRegistrationNumber);
        setCollateralFieldWithIndex(textFields, COLL_ASSESSMENT_FIELD, index, getThousandSeparatedString(amountOfAssessment));
        setCollateralFieldWithIndex(textFields, COLL_AMOUNT_FIELD, index, getThousandSeparatedString(amount));
        setCollateralFieldWithIndex(textFields, OWNER_NAME_FIELD, index, ownerName);

        try
        {
          totalAssessmentAmount = totalAssessmentAmount.add(BigDecimal.valueOf(Long.parseLong(amountOfAssessment)));
          totalAmount = totalAmount.add(BigDecimal.valueOf(Long.parseLong(amount)));
        }
        catch (NumberFormatException e)
        {
          totalAssessmentAmount = totalAssessmentAmount.add(new BigDecimal(amountOfAssessment));
          totalAmount = totalAmount.add(new BigDecimal(amount));
          LOGGER.info("NumberFormatException - COLL ID = [{}]", colId);
        }

        index++;
      }
    }

    while (index <= 10)
    {
      setBlankCollateralField(textFields, index);
      index++;
    }

    textFields.add(new RestTextValueField(COLL_TOTAL_ASSESSMENT_FIELD, getThousandSeparatedString(String.valueOf(totalAssessmentAmount))));
    textFields.add(new RestTextValueField(COLL_TOTAL_AMOUNT_FIELD, getThousandSeparatedString(String.valueOf(totalAmount))));

    return textFields;
  }

  private static Map<String, Serializable> getCollateralInfoMap(CollateralInfo collateralInfo)
  {
    Map<String, Serializable> collateralMapToReturn = new HashMap<>();

    String description = collateralInfo.getDescription();
    String haircut = collateralInfo.getHairCut().toPlainString();

    collateralMapToReturn.put(COLLATERAL_DESCRIPTION, StringUtils.isBlank(description) ? "" : description);
    collateralMapToReturn.put(DEDUCTION_RATE, haircut);

    return collateralMapToReturn;
  }

  private static List<RestTextValueField> addEmptyCollateralInfos(List<RestTextValueField> collTextFields)
  {
    for (int index = 1; index <= 10; index++)
    {
      collTextFields.add(new RestTextValueField("coll_prod_desc_" + index, BLANK));
      collTextFields.add(new RestTextValueField("country_reg_number_" + index, BLANK));
      collTextFields.add(new RestTextValueField("coll_assessment_" + index, BLANK));
      collTextFields.add(new RestTextValueField("coll_amount_" + index, BLANK));
      collTextFields.add(new RestTextValueField("owner_name_" + index, BLANK));
      collTextFields.add(new RestTextValueField(String.valueOf(index), BLANK));
    }

    collTextFields.add(new RestTextValueField(COLL_TOTAL_ASSESSMENT_FIELD, BLANK));
    collTextFields.add(new RestTextValueField(COLL_TOTAL_AMOUNT_FIELD, BLANK));

    return collTextFields;
  }

  private static void setCollateralFieldWithIndex(List<RestTextValueField> textFields, String fieldName, int index, String fieldValue)
  {
    textFields.add(new RestTextValueField(fieldName + "_" + index, fieldValue));
  }

  private static void setBlankCollateralField(List<RestTextValueField> textFields, int index)
  {
    setCollateralFieldWithIndex(textFields, COLL_PRODUCT_DESCRIPTION_FIELD, index, BLANK);
    setCollateralFieldWithIndex(textFields, COUNTRY_REGISTER_NUMBER_FIELD, index, BLANK);
    setCollateralFieldWithIndex(textFields, COLL_ASSESSMENT_FIELD, index, BLANK);
    setCollateralFieldWithIndex(textFields, COLL_AMOUNT_FIELD, index, BLANK);
    setCollateralFieldWithIndex(textFields, OWNER_NAME_FIELD, index, BLANK);
  }
}
