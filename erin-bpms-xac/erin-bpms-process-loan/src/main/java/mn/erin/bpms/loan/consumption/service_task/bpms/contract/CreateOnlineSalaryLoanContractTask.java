package mn.erin.bpms.loan.consumption.service_task.bpms.contract;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.contract.CreateOnlineSalaryLoanContractDocument;

import static mn.erin.bpms.loan.consumption.task_listener.UpdateContractParamsListener.IS_COMPLETED_CONTRACT_PARAMS_UPDATE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;

/**
 * @author Tamir
 */
public class CreateOnlineSalaryLoanContractTask implements JavaDelegate
{
  private static final Logger LOG = LoggerFactory.getLogger(CreateOnlineSalaryLoanContractTask.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationService authorizationService;

  private final CaseService caseService;
  private final DocumentRepository documentRepository;

  public CreateOnlineSalaryLoanContractTask(AuthenticationService authenticationService, AuthorizationService authorizationService,
      CaseService caseService, DocumentRepository documentRepository)
  {
    this.authenticationService = Objects.requireNonNull(authenticationService, "Authentication service is required!");
    this.authorizationService = Objects.requireNonNull(authorizationService, "Authorization service is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.documentRepository = Objects.requireNonNull(documentRepository, "Document repository is required!");
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    String requestId = (String) execution.getVariable(PROCESS_REQUEST_ID);

    Object isCompleteParamUpdate = execution.getVariable(IS_COMPLETED_CONTRACT_PARAMS_UPDATE);

    if (null != isCompleteParamUpdate)
    {
      boolean isComplete = (boolean) isCompleteParamUpdate;

      if (!isComplete)
      {
        String errorCode = "PUBLISHER001";
        String errorMessage = String
            .format("Could not update ONLINE SALARY loan contract parameters to PROCESS PARAMETERS TABLE with REQUEST ID =[%s]", requestId);
        LOG.error(errorMessage);
        throw new ProcessTaskException(errorCode, errorMessage);
      }
    }
    else
    {
      String errorCode = "PUBLISHER001";
      LOG.error("###### Could not update ONLINE SALARY loan contract parameters to PROCESS PARAMETERS TABLE with REQUEST ID =[{}]", requestId);
      throw new ProcessTaskException(errorCode, "###### Could not create ONLINE SALARY loan contract document with REQUEST ID =" + requestId);
    }

    LOG.info("##### ONLINE SALARY LOAN CONTRACT DOCUMENT persisting to database.");

    if (null == caseInstanceId)
    {
      String errorCode = "PUBLISHER003";
      LOG.error("######### CASE INSTANCE ID is null during download loan contract! with REQUEST_ID =[{}]", requestId);
      throw new ProcessTaskException(errorCode, "###### CASE INSTANCE ID is null! with REQUEST ID =" + requestId);
    }

    CreateOnlineSalaryLoanContractDocument createContract = new CreateOnlineSalaryLoanContractDocument(authenticationService, authorizationService, caseService,
        documentRepository);
    Map<String, String> inputMap = new HashMap<>();
    inputMap.put("instanceId", caseInstanceId);
    Boolean isCreated = createContract.execute(inputMap);

    if (null != isCreated && isCreated)
    {
      LOG.info("########## Successful created ONLINE SALARY loan contract document with REQUEST ID =[{}]", requestId);
    }
    else
    {
      String errorCode = "PUBLISHER004";
      LOG.error("###### Could not create ONLINE SALARY loan contract document with REQUEST ID =[{}]", requestId);
      throw new ProcessTaskException(errorCode, "###### Could not create ONLINE SALARY loan contract document with REQUEST ID =" + requestId);
    }
  }
}
