/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.rest.controller;

import java.util.Collection;
import java.util.Objects;
import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.bpm.rest.model.RestProcessType;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.process.CreateProcessType;
import mn.erin.domain.bpm.usecase.process.CreateProcessTypeInput;
import mn.erin.domain.bpm.usecase.process.GetProcessTypes;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.util.BpmRestUtils.toRestProcessType;
import static mn.erin.bpm.rest.util.BpmRestUtils.toRestProcessTypes;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_TYPE_CATEGORY_IS_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PROCESS_TYPE_CATEGORY_IS_NULL_MESSAGE;

/**
 * @author Tamir
 */
@Api
@RestController
@RequestMapping(value = "bpm/process-types", name = "Provides BPM process type APIs.")
public class ProcessTypeRestApi extends BaseBpmsRestApi
{
  private final ProcessTypeRepository processTypeRepository;
  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  @Inject
  public ProcessTypeRestApi(
      BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry,
      AuthenticationService authenticationService,
      AuthorizationService authorizationService)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);
    this.processTypeRepository = Objects.requireNonNull(bpmsRepositoryRegistry.getProcessTypeRepository(), "Process type repository is required!");
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
  }

  @ApiOperation("Creates a process type")
  @PostMapping
  public ResponseEntity<RestResult> create(@RequestBody RestProcessType restProcessType) throws UseCaseException, BpmInvalidArgumentException
  {
    if (null == restProcessType)
    {
      String errorCode = "BPMS049";
      throw new BpmInvalidArgumentException(errorCode, "Rest process type is required!");
    }

    String processTypeId = restProcessType.getId();
    String definitionKey = restProcessType.getDefinitionKey();

    String name = restProcessType.getName();
    String version = restProcessType.getVersion();
    String definitionType = restProcessType.getProcessDefinitionType();
    CreateProcessTypeInput input = new CreateProcessTypeInput(processTypeId, definitionKey, name, version, definitionType);
    CreateProcessType createProcessType = new CreateProcessType(authenticationService, authorizationService, processTypeRepository);

    ProcessType output = createProcessType.execute(input);
    return RestResponse.success(toRestProcessType(output));
  }

  @ApiOperation("Lists all process types")
  @GetMapping
  public ResponseEntity<RestResult> getAll() throws UseCaseException
  {

    GetProcessTypes getProcessTypes = new GetProcessTypes(authenticationService, authorizationService, processTypeRepository);

    try
    {
      Collection<ProcessType> processTypes = getProcessTypes.execute(null);
      return RestResponse.success(toRestProcessTypes(processTypes));
    }
    catch (RuntimeException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
  @ApiOperation("Get process types by category")
  @GetMapping("/{processTypeCategory}")
  public ResponseEntity<RestResult> getProcessTypesByCategory(@PathVariable String processTypeCategory) throws UseCaseException, BpmInvalidArgumentException {

    if (StringUtils.isBlank(processTypeCategory)){
      throw new BpmInvalidArgumentException(PROCESS_TYPE_CATEGORY_IS_NULL_CODE, PROCESS_TYPE_CATEGORY_IS_NULL_MESSAGE);
    }

    GetProcessTypesByCategory getProcessTypesByCategory= new GetProcessTypesByCategory(processTypeRepository);

    try
    {
      GetProcessTypesByCategoryOutput getProcessTypesByCategoryOutput = getProcessTypesByCategory.execute(processTypeCategory);
      Collection<ProcessType> processTypes = getProcessTypesByCategoryOutput.getProcessTypes();
      return RestResponse.success(toRestProcessTypes(processTypes));
    }
    catch (RuntimeException e)
    {
      return RestResponse.internalError(e.getMessage());
    }
  }
}
