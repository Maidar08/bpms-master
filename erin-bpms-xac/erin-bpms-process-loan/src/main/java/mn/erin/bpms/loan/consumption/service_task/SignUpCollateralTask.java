/*
 * Copyright (C) ERIN SYSTEMS LLC, 2021. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpms.loan.consumption.service_task;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.exception.AimRepositoryException;
import mn.erin.domain.aim.model.membership.Membership;
import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.aim.repository.MembershipRepository;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.account.UDField;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.product.CollateralProduct;
import mn.erin.domain.bpm.repository.CollateralProductRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByFn;
import mn.erin.domain.bpm.usecase.customer.GetUDFieldsByFnOutput;
import mn.erin.domain.bpm.usecase.process.collateral.GetCollateralById;
import mn.erin.domain.bpm.usecase.process.collateral.GetCollateralByIdInput;
import mn.erin.domain.bpm.usecase.process.collateral.GetSavedCollateralUDFields;
import mn.erin.domain.bpm.usecase.process.collateral.GetSavedCollateralUDFieldsInput;
import mn.erin.domain.bpm.usecase.process.collateral.GetSavedCollateralUDFieldsOutput;
import mn.erin.domain.bpm.usecase.product.GetFilteredCollateralProduct;
import mn.erin.domain.bpm.usecase.product.GetFilteredCollateralProductInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_PRODUCT_NOT_FOUND_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COLLATERAL_PRODUCT_NOT_FOUND_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_BASIC_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_SUB_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.DEDUCTION_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT;

/**
 * @author Zorig
 */
public class SignUpCollateralTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SignUpCollateralTask.class);

  private final AuthenticationService authenticationService;
  private final ProcessRepository processRepository;
  private final CollateralProductRepository collateralProductRepository;
  private final CoreBankingService coreBankingService;
  private final MembershipRepository membershipRepository;

  public SignUpCollateralTask(AuthenticationService authenticationService,
      ProcessRepository processRepository, CollateralProductRepository collateralProductRepository,
      CoreBankingService coreBankingService, MembershipRepository membershipRepository)
  {
    this.authenticationService = authenticationService;
    this.processRepository = processRepository;
    this.collateralProductRepository = collateralProductRepository;
    this.coreBankingService = coreBankingService;
    this.membershipRepository = membershipRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    if (execution.getVariable("isLoanAccountCreate") == null)
    {
      return;
    }
    boolean isLoanAccountCreate = (boolean) execution.getVariable("isLoanAccountCreate");
    if (!isLoanAccountCreate)
    {
      return;
    }

    String registrationNumber = (String) execution.getVariable("registerNumber");
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String userId = authenticationService.getCurrentUserId();
    LOGGER.info("#########  Creating Collateral Task.. Register Number: " + registrationNumber + ", Request ID: " + requestId + " , User ID: " + userId);

    String branch = getBranch();
    execution.setVariable("branch", branch);

    if (execution.getVariable("collateralList") == null)
    {
      LOGGER.info("#########  No Collaterals -- exiting service task");
      return;
    }

    Map<String, Map<String, Object>> collateralsMap = (Map) execution.getVariable("collateralList");

    if (collateralsMap.isEmpty())
    {
      LOGGER.info("#########  No Collaterals -- exiting service task");
      return;
    }

    for (Map.Entry<String, Map<String, Object>> collateral : collateralsMap.entrySet())
    {
      String collateralId = collateral.getKey();

      Map<String, Object> collateralValues = collateral.getValue();

      String cif = (String) execution.getVariable("cifNumber");

      String amountOfAssessment = String.valueOf(collateralValues.get("amountOfAssessment"));
      String collateralConnectionRate = String.valueOf(collateralValues.get("collateralConnectionRate"));
      String loanAmount = String.valueOf(collateralValues.get("loanAmount"));
      String order = String.valueOf(collateralValues.get("order"));

      Date dateNow = new Date();
      Calendar calNow = Calendar.getInstance();
      calNow.setTime(dateNow);

      SimpleDateFormat formatter = new SimpleDateFormat(BpmModuleConstants.ISO_DATE_FORMAT);

      String collateralStartDate = formatter.format(dateNow);
      LOGGER.info("############## COLLATERAL START DATE = [{}]", collateralStartDate);

      String collateralEndDate = "";
      LOGGER.info("############## COLLATERAL END DATE = [{}]", collateralEndDate);

      Map<String, Serializable> collateralMap = getCollateral(collateralId);

      if (collateralMap == null)
      {
        continue;
      }

      String type = String.valueOf(collateralMap.get(COLLATERAL_BASIC_TYPE));
      String subType = String.valueOf(collateralMap.get(COLLATERAL_SUB_TYPE));
      String product = String.valueOf(collateralMap.get(PRODUCT));
      String collateralCategory = getCollateralProductCode(collateralId, type, subType, product);
      String collateralDescription = String.valueOf(collateralMap.get(COLLATERAL_DESCRIPTION));

      //haircut
      String haircut = StringUtils.isBlank(String.valueOf(collateralMap.get(DEDUCTION_RATE))) ? "0" : String.valueOf(collateralMap.get(DEDUCTION_RATE));

      Map<String, String> collateralCreationInfo = new HashMap<>();

      collateralCreationInfo.put("collateralId", collateralId);
      collateralCreationInfo.put("collateralDescription", collateralDescription);

      collateralCreationInfo.put("collateralValue", amountOfAssessment);
      collateralCreationInfo.put("collateralConnectionRate", collateralConnectionRate);
      collateralCreationInfo.put("haircut", haircut);

      collateralCreationInfo.put("loanAmount", loanAmount);
      collateralCreationInfo.put("order", order);
      collateralCreationInfo.put("collateralCategory", collateralCategory);

      collateralCreationInfo.put("collateralCreationStartDate", collateralStartDate);
      collateralCreationInfo.put("collateralCreationEndDate", collateralEndDate);

      Map<String, Serializable> udFieldsWithoutValueIds = getCollateralUDFieldValues(collateralId);
      Map<String, Serializable> udFieldsWithValueIds = getUDFieldValueIDFromDescription(udFieldsWithoutValueIds);

      boolean isCreated = coreBankingService.createCollateral(branch, cif, collateralCreationInfo, udFieldsWithValueIds);

      if (isCreated)
      {
        LOGGER.info("######### Successful signed up single collateral with COLL_ID = [{}]", collateralId);
      }
      else
      {
        LOGGER.info("######### Could not create collateral, Collateral has already been signed up. Collateral Id = [{}]", collateralId);
      }
    }
  }

  private String getCollateralProductCode(String collateralId, String type, String subType, String productType) throws UseCaseException, ProcessTaskException
  {
    GetFilteredCollateralProduct getFilteredCollateralProduct = new GetFilteredCollateralProduct(collateralProductRepository);
    CollateralProduct product = getFilteredCollateralProduct.execute(new GetFilteredCollateralProductInput(type, subType, productType));
    String logMessage;
    if (product == null)
    {
      logMessage = String.format(COLLATERAL_PRODUCT_NOT_FOUND_MESSAGE, collateralId, type, subType, productType);
      LOGGER.error(logMessage);
      throw new ProcessTaskException(COLLATERAL_PRODUCT_NOT_FOUND_CODE, logMessage);
    }

    LOGGER.info("COLLATERAL CATEGORY = [{}] found  with type = [{}], sub type = [{}], description = [{}].",
        product.getId().getId(), type, subType, productType);

    return product.getId().getId();
  }

  private Map<String, Serializable> getCollateralUDFieldValues(String collateralId) throws UseCaseException
  {
    GetSavedCollateralUDFields getSavedCollateralUDFields = new GetSavedCollateralUDFields(processRepository);
    GetSavedCollateralUDFieldsInput getSavedCollateralUDFieldsInput = new GetSavedCollateralUDFieldsInput(collateralId);
    GetSavedCollateralUDFieldsOutput output = getSavedCollateralUDFields.execute(getSavedCollateralUDFieldsInput);

    return output.getUdFields();
  }

  private Map<String, Serializable> getUDFieldValueIDFromDescription(Map<String, Serializable> collateralUDFieldValues) throws UseCaseException
  {
    GetUDFieldsByFn getUDFieldsByFn = new GetUDFieldsByFn(coreBankingService);
    GetUDFieldsByFnOutput output = getUDFieldsByFn.execute("GEDCOLLT");

    Map<String, Serializable> udFieldsWithCorrectValueIds = new HashMap<>();

    Map<String, UDField> allUDFieldsByFn = output.getUdFieldMap();

    for (Map.Entry<String, Serializable> collateralUDFieldValue : collateralUDFieldValues.entrySet())
    {
      String key = collateralUDFieldValue.getKey();
      String value = (String) collateralUDFieldValue.getValue();

      UDField udField = allUDFieldsByFn.get(key);

      String fieldValueId = udField.getFieldValueIdByDescription(value);

      if (value.equals("null"))
      {
        udFieldsWithCorrectValueIds.put(key, null);
      }
      else if (fieldValueId == null)
      {
        udFieldsWithCorrectValueIds.put(key, value);
      }
      else
      {
        udFieldsWithCorrectValueIds.put(key, fieldValueId);
      }
    }
    return udFieldsWithCorrectValueIds;
  }

  private String getBranch() throws UseCaseException
  {
    try
    {
      String currentUserId = authenticationService.getCurrentUserId();
      List<Membership> membershipList = membershipRepository.listAllByUserId(TenantId.valueOf("xac"), UserId.valueOf(currentUserId));
      Membership membership = membershipList.get(0);
      return membership.getGroupId().getId();
    }
    catch (AimRepositoryException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }

  private Map<String, Serializable> getCollateral(String collateralId) throws UseCaseException
  {
    GetCollateralById getCollateralById = new GetCollateralById(processRepository);
    GetCollateralByIdInput getCollateralByIdInput = new GetCollateralByIdInput(collateralId);

    Process process = getCollateralById.execute(getCollateralByIdInput);

    if (process == null)
    {
      return null;
    }

    Map<ParameterEntityType, Map<String, Serializable>> processParameters = process.getProcessParameters();
    Map<String, Serializable> collateralEntityParameters = processParameters.get(ParameterEntityType.COLLATERAL);

    for (Map.Entry<String, Serializable> collateralParameter : collateralEntityParameters.entrySet())
    {
      String collateralsJSONString = String.valueOf(collateralParameter.getValue());
      JSONObject collateralJSON = new JSONObject(collateralsJSONString);

      Map<String, Serializable> collateralMapToReturn = new HashMap<>();

      collateralMapToReturn.put(COLLATERAL_BASIC_TYPE,
          JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_BASIC_TYPE)) ? "" : String.valueOf(collateralJSON.get(COLLATERAL_BASIC_TYPE)));
      collateralMapToReturn
          .put(COLLATERAL_SUB_TYPE,
              JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_SUB_TYPE)) ? "" : String.valueOf(collateralJSON.get(COLLATERAL_SUB_TYPE)));
      collateralMapToReturn.put(PRODUCT, JSONObject.NULL.equals(collateralJSON.get(PRODUCT)) ? "" : String.valueOf(collateralJSON.get(PRODUCT)));
      collateralMapToReturn.put(COLLATERAL_DESCRIPTION,
          JSONObject.NULL.equals(collateralJSON.get(COLLATERAL_DESCRIPTION)) ? "" : String.valueOf(collateralJSON.get(COLLATERAL_DESCRIPTION)));
      collateralMapToReturn
          .put(DEDUCTION_RATE, JSONObject.NULL.equals(collateralJSON.get(DEDUCTION_RATE)) ? "" : String.valueOf(collateralJSON.get(DEDUCTION_RATE)));

      return collateralMapToReturn;
    }
    return process.getProcessParameters().get(ParameterEntityType.COLLATERAL);
  }
}
