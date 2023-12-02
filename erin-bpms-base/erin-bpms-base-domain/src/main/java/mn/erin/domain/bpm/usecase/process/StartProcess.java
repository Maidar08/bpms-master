/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.time.LocalDateTime;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.ProcessTypeService;

import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_START_PROCESS_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.COULD_NOT_START_PROCESS_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_CATEGORY_BRANCH_BANKING;

/**
 * @author EBazarragchaa
 */
public class StartProcess extends AuthorizedUseCase<StartProcessInput, StartProcessOutput>
{
  private static final Logger LOG = LoggerFactory.getLogger(StartProcess.class);

  private static final Permission permission = new BpmModulePermission("StartProcess");
  private final ProcessRequestRepository processRequestRepository;
  private final ProcessTypeService processTypeService;
  private final ProcessRepository processRepository;
  private final CaseService caseService;

  public StartProcess(AuthenticationService authenticationService,
      AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository,
      ProcessTypeService processTypeService, ProcessRepository processRepository, CaseService caseService)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = processRequestRepository;
    this.processTypeService = processTypeService;
    this.processRepository = processRepository;
    this.caseService = caseService;
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected StartProcessOutput executeImpl(StartProcessInput input) throws UseCaseException
  {
    validateInput(input);

    if (!StringUtils.isBlank(input.getProcessCategory()) && !StringUtils.isBlank(input.getProcessType()))
    {
      String processCategory = input.getProcessCategory();
      if (processCategory.equals(PROCESS_CATEGORY_BRANCH_BANKING))
      {
        return startBranchBankingProcess(input.getProcessType(), input.getCreatedUser(), input.getProcessCategory());
      }
    }

    ProcessRequestId processRequestId = ProcessRequestId.valueOf(input.getProcessRequestId());
    ProcessRequest processRequest = processRequestRepository.findById(processRequestId);
    if (null == processRequest)
    {
      throw new UseCaseException(BpmMessagesConstants.PROCESS_REQUEST_NOT_EXISTS_CODE, BpmMessagesConstants.PROCESS_REQUEST_NOT_EXISTS_MESSAGE);
    }
    try
    {
      ProcessTypeId processTypeId = processRequest.getProcessTypeId();
      String processInstanceId = "";
      if (input.getParameters() != null && !input.getParameters().isEmpty())
      {
        processInstanceId = processTypeService.startProcessWithVariables(processTypeId.getId(), processRequestId.getId(), input.getParameters());
      }
      else
      {
        processInstanceId = processTypeService.startProcess(processTypeId.getId(), processRequestId.getId());
      }

      CreateProcessInput createProcessInput = new CreateProcessInput(processInstanceId, LocalDateTime.now(), Collections.emptyMap());

      CreateProcess createProcess = new CreateProcess(authenticationService, authorizationService, processRepository);

      createProcess.execute(createProcessInput);

      if (null == processInstanceId)
      {
        String errorCode = "BPMS012";
        throw new UseCaseException(errorCode, "ProcessRequest with id [" + input.getProcessRequestId() + "] couldn't start new process!");
      }

      processRequest.setProcessInstanceId(processInstanceId);

      LOG.info("########### Updates PROCESS INSTANCE ID = [{}] WITH PROCESS REQUEST ID = [{}] WHEN START CASE.", processInstanceId, processRequestId.getId());
      processRequestRepository.updateProcessInstanceId(processRequestId.getId(), processInstanceId);
      LOG.info("########### Successful updated PROCESS INSTANCE ID = [{}] WITH PROCESS REQUEST ID = [{}] WHEN START CASE.", processInstanceId,
          processRequestId.getId());
      return new StartProcessOutput(processInstanceId);
    }
    catch (Exception e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void validateInput(StartProcessInput input) throws UseCaseException
  {
    if (StringUtils.isBlank(input.getProcessRequestId()))
    {
      throw new UseCaseException(BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_CODE, BpmMessagesConstants.PROCESS_REQUEST_ID_NULL_MESSAGE);
    }
  }

  private StartProcessOutput startBranchBankingProcess(String defKey, String createdUser, String processTypeCategory)
      throws UseCaseException
  {
    String previousCaseInstanceId = getPreviousCaseInstance(createdUser, PROCESS_CATEGORY_BRANCH_BANKING);
    caseService.terminateCase(previousCaseInstanceId);
    caseService.closeCases(previousCaseInstanceId);

    if (previousCaseInstanceId != null)
    {
      processRepository.deleteProcesses(createdUser, processTypeCategory);
    }

    String processInstanceId = processTypeService.startProcess(defKey);
    if (processInstanceId == null)
    {
      throw new UseCaseException(COULD_NOT_START_PROCESS_ERROR_CODE, COULD_NOT_START_PROCESS_ERROR_MESSAGE);
    }

    CreateProcessInput createProcessInput = new CreateProcessInput(processInstanceId, LocalDateTime.now(), createdUser, processTypeCategory,
        Collections.emptyMap());
    CreateProcess createProcess = new CreateProcess(authenticationService, authorizationService, processRepository);
    createProcess.execute(createProcessInput);
    return new StartProcessOutput(processInstanceId);
  }

  private String getPreviousCaseInstance(String createdUser, String processTypeCategory)
  {
    Process process = processRepository.findProcessByUserAndType(createdUser, processTypeCategory);
    if (process != null)
    {
      return process.getId().getId();
    }
    return null;
  }
}
