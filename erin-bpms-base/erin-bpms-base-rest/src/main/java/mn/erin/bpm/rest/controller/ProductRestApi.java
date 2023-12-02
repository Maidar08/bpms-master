/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.bpm.rest.controller;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpm.rest.model.RestCollateral;
import mn.erin.bpm.rest.model.RestCollateralProduct;
import mn.erin.bpm.rest.model.RestCompletedForm;
import mn.erin.bpm.rest.model.RestCompletedFormField;
import mn.erin.bpm.rest.model.RestProduct;
import mn.erin.bpm.rest.util.BpmRestUtils;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.model.form.CompletedFormField;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.product.CollateralProduct;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.CollateralProductRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.collateral.GetBpmCollateralByCustNumber;
import mn.erin.domain.bpm.usecase.collateral.GetCollateralsByCustNumber;
import mn.erin.domain.bpm.usecase.collateral.GetCollateralsByCustNumberOutput;
import mn.erin.domain.bpm.usecase.collateral.ModifyCollateralInput;
import mn.erin.domain.bpm.usecase.collateral.ModifyImmovablePropCollateral;
import mn.erin.domain.bpm.usecase.collateral.ModifyMachineryCollateral;
import mn.erin.domain.bpm.usecase.collateral.ModifyOtherCollateral;
import mn.erin.domain.bpm.usecase.collateral.ModifyVehicleCollateral;
import mn.erin.domain.bpm.usecase.form.case_variable.SetCaseVariableById;
import mn.erin.domain.bpm.usecase.form.case_variable.SetCaseVariableByIdInput;
import mn.erin.domain.bpm.usecase.loan.get_loan_account_info.GetLoanAccountInfo;
import mn.erin.domain.bpm.usecase.process.GetProcessLargeParameter;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterInput;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterOutput;
import mn.erin.domain.bpm.usecase.process.UpdateProcessLargeParameters;
import mn.erin.domain.bpm.usecase.process.UpdateProcessParametersInput;
import mn.erin.domain.bpm.usecase.process.collateral.GetImmobavlePropertyCollateralInfo;
import mn.erin.domain.bpm.usecase.process.collateral.GetMachineryCollateralInfo;
import mn.erin.domain.bpm.usecase.process.collateral.GetOtherCollateralInfo;
import mn.erin.domain.bpm.usecase.process.collateral.GetVehicleCollateralInfo;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesById;
import mn.erin.domain.bpm.usecase.process.get_variables.GetVariablesByIdOutput;
import mn.erin.domain.bpm.usecase.product.GetAllCollateralProducts;
import mn.erin.domain.bpm.usecase.product.GetAllCollateralProductsOutput;
import mn.erin.domain.bpm.usecase.product.GetAllProducts;
import mn.erin.domain.bpm.usecase.product.GetAllProductsOutput;
import mn.erin.domain.bpm.usecase.product.GetCollateralProduct;
import mn.erin.domain.bpm.usecase.product.GetProduct;
import mn.erin.domain.bpm.usecase.product.GetProductsByAppCategoryAndBorrowerType;
import mn.erin.domain.bpm.usecase.product.GetProductsById;
import mn.erin.domain.bpm.usecase.product.UniqueProductInput;
import mn.erin.domain.bpm.usecase.task.GetCompletedTaskByDefKey;
import mn.erin.domain.bpm.usecase.task.GetCompletedTaskByDefKeyInput;
import mn.erin.domain.bpm.usecase.task.GetCompletedTaskByDefKeyOutput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.constant.BpmsControllerConstants.INTERNAL_SERVER_ERROR;
import static mn.erin.bpm.rest.util.BpmRestUtils.decodeStringToUtf8;
import static mn.erin.bpm.rest.util.BpmRestUtils.jsonToObject;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestTaskForm;
import static mn.erin.domain.bpm.BpmLoanContractConstants.ACCOUNT_NUMBER;
import static mn.erin.domain.bpm.BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_CHECKED;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.COLLATERAL_TABLE_SPECIAL_FORM_KEY;
import static mn.erin.domain.bpm.BpmModuleConstants.FORM_FIELDS;
import static mn.erin.domain.bpm.BpmModuleConstants.FORM_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.SPECIAL_FORMS;
import static mn.erin.domain.bpm.BpmModuleConstants.TASK_ID;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.COMPLETED_FORM;

/**
 * @author Zorig
 */
@Api("Product")
@RestController
@RequestMapping(value = "bpm/products", name = "Products")
public class ProductRestApi extends BaseBpmsRestApi
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestApi.class);

  private final AuthorizationService authorizationService;
  private final AuthenticationService authenticationService;
  private final ProductRepository productRepository;
  private final CollateralProductRepository collateralProductRepository;
  private final ProcessRepository processRepository;
  private final NewCoreBankingService newCoreBankingService;
  private final CaseService caseService;

  @Inject
  public ProductRestApi(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry, ProcessRepository processRepository,
      AuthorizationService authorizationService, AuthenticationService authenticationService,
      NewCoreBankingService newCoreBankingService, CaseService caseService)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);
    this.productRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProductRepository(), "Product repository is required!");
    this.processRepository = processRepository;
    this.authorizationService = authorizationService;
    this.authenticationService = authenticationService;
    this.collateralProductRepository = Objects
        .requireNonNull(bpmsRepositoryRegistry.getCollateralProductRepository(), "Collateral Product Repository is required!");
    this.newCoreBankingService = newCoreBankingService;
    this.caseService = caseService;
  }

  @ApiOperation("Get Products by id")
  @GetMapping("/{productId}")
  public ResponseEntity<RestResult> getProductsById(@PathVariable String productId)
  {
    if (StringUtils.isBlank(productId))
    {
      LOGGER.error(INTERNAL_SERVER_ERROR + PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }

    GetProductsById getProductsById = new GetProductsById(productRepository);

    try
    {
      List<Product> products = getProductsById.execute(productId).getProductsList();

      List<RestProduct> restProducts = new ArrayList<>();

      for (Product product : products)
      {
        restProducts.add(toRestProduct(product));
      }

      return RestResponse.success(restProducts);
    }
    catch (IllegalArgumentException | NullPointerException | UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Get Product by id and application category")
  @GetMapping("/productId/{productId}/applicationCategory/{applicationCategory}")

  public ResponseEntity<RestResult> getProductByIdAndCategory(@PathVariable String productId, @PathVariable String applicationCategory)
  {

    if (StringUtils.isBlank(productId))
    {
      LOGGER.error(INTERNAL_SERVER_ERROR + PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE);
    }

    GetProduct getProduct = new GetProduct(productRepository);

    try
    {
      Product product = getProduct.execute(new UniqueProductInput(productId, applicationCategory));
      return RestResponse.success(product);
    }
    catch (IllegalArgumentException | NullPointerException | UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Get Product by application category and borrower type")
  @GetMapping("/applicationCategory/{applicationCategory}/borrowerType/{borrowerType}")
  public ResponseEntity<RestResult> getProductsByAppCategoryAndBorrowerType(@PathVariable String applicationCategory, @PathVariable String borrowerType)
      throws UseCaseException, UnsupportedEncodingException
  {
    if (StringUtils.isBlank(applicationCategory))
    {
      LOGGER.error(INTERNAL_SERVER_ERROR + "Application Category is required!");
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "Application Category is required!");
    }

    borrowerType = decodeStringToUtf8(borrowerType);

    GetProductsByAppCategoryAndBorrowerType useCase = new GetProductsByAppCategoryAndBorrowerType(productRepository);
    UniqueProductInput input = new UniqueProductInput(null, applicationCategory);
    input.setBorrowerType(borrowerType);

    GetAllProductsOutput productList = useCase.execute(input);
    if (null != productList)
    {
      Collection<RestProduct> restProducts = productList.getProducts()
          .stream()
          .map(this::toRestProduct).collect(Collectors.toList());
      return RestResponse.success(restProducts);
    }

    return RestResponse.success(null);
  }

  @ApiOperation("Get All Products")
  @GetMapping()
  public ResponseEntity<RestResult> getAllProducts()
  {

    GetAllProducts getAllProducts = new GetAllProducts(productRepository);

    try
    {
      Collection<Product> products = getAllProducts.execute(null).getProducts();

      Collection<RestProduct> restProducts = new ArrayList<>();

      for (Product product : products)
      {
        restProducts.add(toRestProduct(product));
      }

      return RestResponse.success(restProducts);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Gets Collateral Product by id")
  @GetMapping("/collateral-products/{productId}")
  public ResponseEntity<RestResult> getCollateralProductById(@PathVariable String productId)
  {
    if (StringUtils.isBlank(productId))
    {
      LOGGER.error(INTERNAL_SERVER_ERROR + "PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE");
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "PRODUCT_BLANK_PRODUCT_ID_ERROR_MESSAGE");
    }

    GetCollateralProduct getProduct = new GetCollateralProduct(collateralProductRepository);

    try
    {
      CollateralProduct product = getProduct.execute(productId);

      return RestResponse.success(toRestCollateralProduct(product));
    }
    catch (IllegalArgumentException | NullPointerException | UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Gets all collateral products.")
  @GetMapping("/collateral-products")
  public ResponseEntity<RestResult> getAllCollateralProducts()
  {
    GetAllCollateralProducts useCase = new GetAllCollateralProducts(collateralProductRepository);

    try
    {
      GetAllCollateralProductsOutput getAllCollateralProductsOutput = useCase.execute(null);
      Collection<CollateralProduct> collateralProducts = getAllCollateralProductsOutput.getCollateralProducts();
      Collection<RestCollateralProduct> restCollateralProducts = new ArrayList<>();

      Iterator<CollateralProduct> collateralProductIterator = collateralProducts.iterator();

      while (collateralProductIterator.hasNext())
      {
        restCollateralProducts.add(toRestCollateralProduct(collateralProductIterator.next()));
      }

      return RestResponse.success(restCollateralProducts);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }

  @ApiOperation("Gets collateral info depending on customer CIF number.")
  @GetMapping("/loan/collaterals/{cifNumber}/{caseInstanceId}")
  public ResponseEntity<RestResult> getCollaterals(@PathVariable String cifNumber, @PathVariable String caseInstanceId) throws UseCaseException
  {
    if (StringUtils.isBlank(cifNumber))
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "Customer CIF number is blank!");
    }

    GetBpmCollateralByCustNumber getBpmCollateralByCustNumber = new GetBpmCollateralByCustNumber(processRepository);
    List<RestCollateral> restCollaterals = new ArrayList<>();
    try
    {
      addRestCollateralsFromVariable(caseInstanceId, restCollaterals);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return checkBpmCollateralsWhenCbsThrowException(getBpmCollateralByCustNumber, cifNumber, e, restCollaterals);
    }
    try
    {
      addRestCollateralsFromBpmCollaterals(getBpmCollateralByCustNumber, cifNumber, restCollaterals, true);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return checkReturnRestValue(restCollaterals, e);
    }
    return RestResponse.success(restCollaterals);
  }

  @ApiOperation("Refresh collateral info depending on customer CIF number.")
  @GetMapping("/loan/collaterals/refresh/{cifNumber}/{caseInstanceId}")
  public ResponseEntity<RestResult> refreshCollaterals(@PathVariable String cifNumber, @PathVariable String caseInstanceId) throws UseCaseException
  {
    if (StringUtils.isBlank(cifNumber))
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "Customer CIF number is blank!");
    }

    GetBpmCollateralByCustNumber getBpmCollateralByCustNumber = new GetBpmCollateralByCustNumber(processRepository);
    List<RestCollateral> restCollaterals = new ArrayList<>();
    try
    {
      setDownloadedCollListToCaseVariable(cifNumber, caseInstanceId);

      addRestCollateralsFromVariable(caseInstanceId, restCollaterals);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return checkBpmCollateralsWhenCbsThrowException(getBpmCollateralByCustNumber, cifNumber, e, restCollaterals);
    }
    try
    {
      addRestCollateralsFromBpmCollaterals(getBpmCollateralByCustNumber, cifNumber, restCollaterals, true);
    }
    catch (UseCaseException e)
    {
      LOGGER.error(e.getMessage(), e);
      return checkReturnRestValue(restCollaterals, e);
    }
    return RestResponse.success(restCollaterals);
  }

  @ApiOperation("Gets selected collaterals by case instance id.")
  @GetMapping("/loan/selected-collaterals/{caseInstanceId}")
  public ResponseEntity<RestResult> getSelectedCollaterals(@PathVariable String caseInstanceId) throws UseCaseException, BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(caseInstanceId))
    {
      return RestResponse.badRequest("Case instance is required!");
    }

    GetCompletedTaskByDefKeyOutput output = getCollateralListCompletedTask(caseInstanceId);
    List<RestCollateral> restCollaterals = new ArrayList<>();

    if (null == output)
    {
      return RestResponse.success(restCollaterals);
    }

    Map<String, Object> specialForms = getCompletedForm(caseInstanceId, output.getCompletedTask()).getSpecialForms();

    if (specialForms.containsKey(COLLATERAL_TABLE_SPECIAL_FORM_KEY))
    {
      List<Object> collaterals = (List<Object>) specialForms.get(COLLATERAL_TABLE_SPECIAL_FORM_KEY);
      return RestResponse.success(BpmRestUtils.getSelectedRestCollaterals(COLLATERAL_CHECKED, collaterals));
    }
    return RestResponse.success(restCollaterals);
  }

  @ApiOperation("get collateral task form which updates form values")
  @GetMapping("/collateral-task-form/collateralType/{collateralType}/collateralId/{collateralId}")
  public ResponseEntity<RestResult> getUpdateCollateralByCollateralId(@PathVariable String collateralType, @PathVariable String collateralId)
      throws UseCaseException
  {
    if (StringUtils.isBlank(collateralType))
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "collateral type id is blank!");
    }

    if (StringUtils.isBlank(collateralType))
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "collateral id is blank!");
    }

    if (collateralType.equals("M"))
    {
      GetMachineryCollateralInfo getMachineryCollateralInfo = new GetMachineryCollateralInfo(newCoreBankingService);
      TaskForm taskForm = getMachineryCollateralInfo.execute(collateralId);
      return RestResponse.success(toRestTaskForm(taskForm));
    }
    if (collateralType.equals("V"))
    {
      GetVehicleCollateralInfo getVehicleCollateralInfo = new GetVehicleCollateralInfo(newCoreBankingService);
      TaskForm taskForm = getVehicleCollateralInfo.execute(collateralId);
      return RestResponse.success(toRestTaskForm(taskForm));
    }

    if (collateralType.equals("I"))
    {
      GetImmobavlePropertyCollateralInfo getImmobavlePropertyCollateralInfo = new GetImmobavlePropertyCollateralInfo(newCoreBankingService);
      TaskForm taskForm = getImmobavlePropertyCollateralInfo.execute(collateralId);
      return RestResponse.success(toRestTaskForm(taskForm));
    }

    if (collateralType.equals("O"))
    {
      GetOtherCollateralInfo getOtherCollateralInfo = new GetOtherCollateralInfo(newCoreBankingService);
      TaskForm taskForm = getOtherCollateralInfo.execute(collateralId);
      return RestResponse.success(toRestTaskForm(taskForm));
    }

    return RestResponse.notFound("collateral Type is invalid");
  }

  @ApiOperation("Update collateral form value")
  @PostMapping("/update/collateral/instanceId/{instanceId}/collateralId/{collateralId}/type/{type}/cif/{cif}")
  public ResponseEntity<RestResult> updateCollateral(@PathVariable String instanceId, @PathVariable String collateralId, @PathVariable String type,
      @PathVariable String cif, @RequestBody Map<String, Object> properties)
      throws UseCaseException
  {
    if (properties == null)
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "task form is null!");
    }

    if (StringUtils.isBlank(instanceId))
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "instance id is blank!");
    }

    if (StringUtils.isBlank(collateralId))
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "collateral id is blank!");
    }

    if (StringUtils.isBlank(type))
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "collateral type is blank!");
    }

    if (StringUtils.isBlank(cif))
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + "cif number is blank!");
    }

    ModifyCollateralInput modifyInput = new ModifyCollateralInput(instanceId, collateralId, properties);

    if (type.equals("M"))
    {
      ModifyCollateralInput input = new ModifyCollateralInput(instanceId, collateralId, properties);
      ModifyMachineryCollateral usecase = new ModifyMachineryCollateral(newCoreBankingService, caseService);
      return RestResponse.success(usecase.execute(input));
    }

    if (type.equals("I"))
    {
      properties.put(CIF_NUMBER, cif);
      ModifyCollateralInput input = new ModifyCollateralInput(instanceId, collateralId, properties);
      ModifyImmovablePropCollateral usecase = new ModifyImmovablePropCollateral(newCoreBankingService, caseService);
      return RestResponse.success(usecase.execute(input));
    }

    if (type.equals("V"))
    {
      properties.put(CIF_NUMBER, cif);
      ModifyCollateralInput input = new ModifyCollateralInput(instanceId, collateralId, properties);
      ModifyVehicleCollateral usecase = new ModifyVehicleCollateral(newCoreBankingService, caseService);
      return RestResponse.success(usecase.execute(input));
    }

    if (type.equals(("O")))
    {
      properties.put(CIF_NUMBER, cif);
      ModifyOtherCollateral usecase = new ModifyOtherCollateral(newCoreBankingService, caseService);
      return RestResponse.success(usecase.execute(modifyInput));
    }

    return RestResponse.notFound("collateral Type is invalid");
  }

  @GetMapping("/account-info/{accountNumber}")
  public ResponseEntity<RestResult> getLoanAccountInfo(@PathVariable String accountNumber)
      throws UseCaseException, RuntimeException
  {
    GetLoanAccountInfo getLoanAccountInfo = new GetLoanAccountInfo(newCoreBankingService);
    Map<String, String> input = new HashMap<>();
    input.put(ACCOUNT_NUMBER, accountNumber);
    input.put(PROCESS_TYPE_ID, PROCESS_TYPE_ID);
    return RestResponse.success(getLoanAccountInfo.execute(input));
  }

  @ApiOperation("Save updated collateral from Account")
  @PostMapping("/save/collateral/account/caseInstanceId/{caseInstanceId}")
  public ResponseEntity<RestResult> saveCollateralFromAccount(@PathVariable String caseInstanceId, @RequestBody RestCompletedForm restTaskForm)
      throws UseCaseException, BpmInvalidArgumentException, JsonProcessingException
  {
    if (StringUtils.isBlank(caseInstanceId))
    {
      return RestResponse.badRequest("Case instance is required!");
    }

    GetCompletedTaskByDefKeyOutput output = getCollateralListCompletedTask(caseInstanceId);
    List<RestCollateral> restCollaterals = new ArrayList<>();

    if (null == output)
    {
      return RestResponse.success(restCollaterals);
    }

    RestCompletedForm completedCollateralList = getCompletedForm(caseInstanceId, output.getCompletedTask());

    // Set collateral array
    List<String> updatedColArray = (List<String>) restTaskForm.getSpecialForms().get(COLLATERAL_TABLE_SPECIAL_FORM_KEY);
    Map<String, Object> specialForms = completedCollateralList.getSpecialForms();
    List<Map<String, Object>> collateralArray = (List<Map<String, Object>>) specialForms.get(COLLATERAL_TABLE_SPECIAL_FORM_KEY);

    for (Map<String, Object> collateralMap : collateralArray)
    {
      String collateralId = (String) collateralMap.get(COLLATERAL_ID);
      Optional<String> first = updatedColArray.stream().filter(col -> col.equals(collateralId)).findFirst();
      if (first.isPresent() && !StringUtils.isBlank(first.get()))
      {
        collateralMap.put(COLLATERAL_CHECKED, true);
      }
      else
      {
        collateralMap.put(COLLATERAL_CHECKED, false);
      }
    }

    // update
    Map<String, Object> colParam = new HashMap<>();
    colParam.put(FORM_ID, completedCollateralList.getFormId());
    colParam.put(CASE_INSTANCE_ID, completedCollateralList.getCaseInstanceId());
    colParam.put(FORM_FIELDS, completedCollateralList.getFormFields());
    colParam.put(SPECIAL_FORMS, completedCollateralList.getSpecialForms());
    colParam.put(TASK_ID, completedCollateralList.getTaskId());

    Map<String, Serializable> param = new HashMap<>();
    param.put(output.getCompletedTask().getId().getId(), new ObjectMapper().writeValueAsString(colParam));

    Map<ParameterEntityType, Map<String, Serializable>> updateParameters = new HashMap<>();
    updateParameters.put(COMPLETED_FORM, param);

    UpdateProcessParametersInput input = new UpdateProcessParametersInput(caseInstanceId, updateParameters);
    UpdateProcessLargeParameters updateProcessLargeParameters = new UpdateProcessLargeParameters(authenticationService, authorizationService,
        processRepository);
    updateProcessLargeParameters.execute(input);

    return RestResponse.success();
  }

  private void setDownloadedCollListToCaseVariable(String cifNumber, String caseInstanceId) throws UseCaseException
  {
    GetCollateralsByCustNumber getCollateralsByCustNumber = new GetCollateralsByCustNumber(bpmsServiceRegistry.getNewCoreBankingService());
    GetCollateralsByCustNumberOutput collateralOutput = getCollateralsByCustNumber.execute(cifNumber);
    List<Collateral> refreshedCollaterals = collateralOutput.getCollaterals();

    SetCaseVariableByIdInput input = new SetCaseVariableByIdInput(caseInstanceId, BpmModuleConstants.COLLATERAL_LIST_FORM_XAC_BANK, refreshedCollaterals);
    SetCaseVariableById setCaseVariableById = new SetCaseVariableById(bpmsServiceRegistry.getCaseService());

    setCaseVariableById.execute(input);

    for (Collateral refreshedCollateral : refreshedCollaterals)
    {
      String collId = refreshedCollateral.getId().getId();

      SetCaseVariableByIdInput collVariableInput = new SetCaseVariableByIdInput(caseInstanceId, collId, refreshedCollateral);
      SetCaseVariableById setCollVariable = new SetCaseVariableById(bpmsServiceRegistry.getCaseService());

      setCollVariable.execute(collVariableInput);
    }
  }

  private void addRestCollateralsFromVariable(String caseInstanceId, List<RestCollateral> restCollaterals) throws UseCaseException
  {
    List<Variable> variables = getVariablesByCaseInstanceId(caseInstanceId);

    for (Variable variable : variables)
    {
      String variableId = variable.getId().getId();

      if (BpmModuleConstants.COLLATERAL_LIST_FORM_XAC_BANK.equals(variableId) && null != variable.getValue())
      {
        List<Collateral> downloadedCollaterals = (List<Collateral>) variable.getValue();

        for (Collateral collateral : downloadedCollaterals)
        {
          String verifyCollId = collateral.getId().getId();
          boolean collateralDuplicated = isCollateralDuplicated(restCollaterals, verifyCollId, collateral, true);

          if (!collateralDuplicated)
          {
            restCollaterals.add(BpmRestUtils.toRestCollateral(collateral, false));
          }
        }
      }
    }
  }

  private RestCompletedForm getCompletedForm(String caseInstanceId, Task completedTask) throws UseCaseException, BpmInvalidArgumentException
  {
    String collateralListTaskId = completedTask.getId().getId();

    GetProcessParameterInput parameterInput = new GetProcessParameterInput(caseInstanceId, collateralListTaskId, COMPLETED_FORM);
    GetProcessLargeParameter getProcessLargeParameter = new GetProcessLargeParameter(bpmsRepositoryRegistry.getProcessRepository());
    GetProcessParameterOutput parameterOutput = getProcessLargeParameter.execute(parameterInput);

    Serializable parameterValue = parameterOutput.getParameterValue();
    RestCompletedForm restCompletedForm = jsonToObject((String) parameterValue, RestCompletedForm.class);

    return restCompletedForm;
  }

  private GetCompletedTaskByDefKeyOutput getCollateralListCompletedTask(String caseInstanceId) throws UseCaseException
  {
    GetCompletedTaskByDefKeyInput input = new GetCompletedTaskByDefKeyInput(caseInstanceId, BpmModuleConstants.COLLATERAL_LIST_USER_TASK_DEF_KEY);

    GetCompletedTaskByDefKey getCompletedTaskByDefKey = new GetCompletedTaskByDefKey(bpmsServiceRegistry);

    return getCompletedTaskByDefKey.execute(input);
  }

  private Collection<CompletedFormField> convertTaskFormField(Collection<RestCompletedFormField> restFormFields)
  {
    Collection<CompletedFormField> taskFormFields = new ArrayList<>();
    restFormFields.forEach(formField ->
        taskFormFields.add(new CompletedFormField(
            formField.getId(), formField.getFormFieldValue(), formField.getLabel(), formField.getType(), formField.getValidations(),
            formField.getOptions(), formField.getContext(), formField.getDisabled(), formField.getRequired(), formField.getColumnIndex()
        ))
    );
    return taskFormFields;
  }

  private List<Variable> getVariablesByCaseInstanceId(String caseInstanceId) throws UseCaseException
  {
    GetVariablesById getVariablesById = new GetVariablesById(authenticationService, authorizationService, bpmsServiceRegistry.getCaseService());

    GetVariablesByIdOutput variablesOutput = getVariablesById.execute(caseInstanceId);

    return variablesOutput.getVariables();
  }

  private void addRestCollateralsFromBpmCollaterals(AbstractUseCase useCase, String cifNumber, List<RestCollateral> restCollaterals, boolean isCreatedOnBpms)
      throws UseCaseException
  {
    GetCollateralsByCustNumberOutput output = (GetCollateralsByCustNumberOutput) useCase.execute(cifNumber);

    List<Collateral> bpmCollaterals = output.getCollaterals();

    for (Collateral collateral : bpmCollaterals)
    {
      String verifyCollId = collateral.getId().getId();
      boolean collateralDuplicated = isCollateralDuplicated(restCollaterals, verifyCollId, null, false);

      if (!collateralDuplicated)
      {
        restCollaterals.add(BpmRestUtils.toRestCollateral(collateral, isCreatedOnBpms));
      }
    }
  }

  private boolean isCollateralDuplicated(List<RestCollateral> restCollaterals, String verifyCollId, Collateral collateral, boolean fromCbs)
  {
    for (RestCollateral restCollateral : restCollaterals)
    {
      String collateralId = restCollateral.getCollateralId();

      if (collateralId.equals(verifyCollId))
      {
        addAccountNumberToCollateral(restCollateral, collateral, fromCbs);
        return true;
      }
    }
    return false;
  }

  private void addAccountNumberToCollateral(RestCollateral restCollateral, Collateral collateral, boolean fromCbs)
  {
    if (fromCbs)
    {
      String newAccountId = collateral.getAccountId().getId();
      String currentAccountId = restCollateral.getAccountId();
      restCollateral.setAccountId(currentAccountId + ", " + newAccountId);
    }
  }

  private ResponseEntity<RestResult> checkBpmCollateralsWhenCbsThrowException(AbstractUseCase useCase, String cifNumber, Exception e,
      List<RestCollateral> restCollaterals) throws UseCaseException
  {

    GetCollateralsByCustNumberOutput output = (GetCollateralsByCustNumberOutput) useCase.execute(cifNumber);
    if (null == output || null == output.getCollaterals() || output.getCollaterals().isEmpty())
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }

    List<Collateral> collaterals = output.getCollaterals();

    for (Collateral collateral : collaterals)
    {
      String verifyCollId = collateral.getId().getId();
      boolean collateralDuplicated = isCollateralDuplicated(restCollaterals, verifyCollId, null, false);

      if (!collateralDuplicated)
      {
        restCollaterals.add(BpmRestUtils.toRestCollateral(collateral, true));
      }
    }

    return RestResponse.success(restCollaterals);
  }

  private ResponseEntity<RestResult> checkReturnRestValue(List<?> restList, Exception e)
  {
    if (restList.isEmpty())
    {
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
    return RestResponse.success(restList);
  }

  private RestProduct toRestProduct(Product product)
  {
    RestProduct restProduct = new RestProduct();

    restProduct.setId(product.getId().getId());
    restProduct.setApplicationCategory(product.getApplicationCategory());
    restProduct.setCategoryDescription(product.getCategoryDescription());
    restProduct.setProductDescription(product.getProductDescription());
    restProduct.setType(product.getType());
    restProduct.setLoanToValueRatio(product.getLoanToValueRatio());
    restProduct.setHasCollateral(product.isHasCollateral());
    restProduct.setHasInsurance(product.isHasInsurance());

    return restProduct;
  }

  private RestCollateralProduct toRestCollateralProduct(CollateralProduct collateralProduct)
  {
    RestCollateralProduct restCollateralProduct = new RestCollateralProduct();

    restCollateralProduct.setId(collateralProduct.getId().getId());
    restCollateralProduct.setType(collateralProduct.getType());
    restCollateralProduct.setSubType(collateralProduct.getSubType());
    restCollateralProduct.setDescription(collateralProduct.getDescription());
    restCollateralProduct.setMoreInformation(collateralProduct.getMoreInformation());

    return restCollateralProduct;
  }
}
