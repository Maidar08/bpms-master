package mn.erin.domain.bpm.usecase.direct_online;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;

import static mn.erin.domain.bpm.BpmModuleConstants.CONSUMPTION_LOAN;
import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.model.process.ProcessRequestState.DELETED;

/**
 * @author Sukhbat
 */
public class Delete14daysPassedRequests extends AbstractUseCase<String, Void>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(Delete14daysPassedRequests.class);

  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final CaseService caseService;
  private final Environment environment;

  public Delete14daysPassedRequests(BpmsRepositoryRegistry bpmsRepositoryRegistry, CaseService caseService, Environment environment)
  {
    this.bpmsRepositoryRegistry = Objects.requireNonNull(bpmsRepositoryRegistry, "Process request repository is required!");
    this.caseService = Objects.requireNonNull(caseService, "Case service is required!");
    this.environment = environment;
  }

  @Override
  public Void execute(String input) throws UseCaseException
  {
    try
    {
      LocalDateTime endDate = LocalDateTime.now(ZoneId.of("UTC+8"));
      LocalDateTime startDate = endDate.minusDays(14);
      String onlineSalaryProduct = getDefaultProduct();
      String onlineSalaryProcessType = getProcessType();
      String bnplBranch = getDefaultBranch();
      int updatedRequestCount14 = bpmsRepositoryRegistry.getProcessRequestRepository()
          .update14daysPassedProcessState(CONSUMPTION_LOAN, startDate, endDate, onlineSalaryProduct,
              ProcessRequestState.fromEnumToString(DELETED));
      updatedRequestCount14 += bpmsRepositoryRegistry.getProcessRequestRepository()
          .update14daysPassedProcessState(onlineSalaryProcessType, startDate, endDate, onlineSalaryProduct,
              ProcessRequestState.fromEnumToString(DELETED));
      LOGGER.info("[{}] process requests passed 14 day states are updated to DELETED with process type: [{}].", updatedRequestCount14, CONSUMPTION_LOAN);

      Collection<ProcessRequest> get14daysPassedRequests = bpmsRepositoryRegistry.getProcessRequestRepository()
          .get14daysPassedRequestsAndDelete(onlineSalaryProcessType, startDate,
              endDate, onlineSalaryProcessType);
      for (ProcessRequest request : get14daysPassedRequests)
      {
        if (!request.getGroupId().getId().equalsIgnoreCase(bnplBranch))
        {
          String processInstanceId = request.getProcessInstanceId();
          caseService.terminateCase(processInstanceId);
          caseService.closeCases(processInstanceId);
        }
      }
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    return null;
  }

  public String getProcessType() throws UseCaseException
  {
    GetProcessTypesByCategory getProcessTypesByCategory = new GetProcessTypesByCategory(bpmsRepositoryRegistry.getProcessTypeRepository());
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(DIRECT_ONLINE_PROCESS_TYPE_CATEGORY);
    List<ProcessType> processTypes = output.getProcessTypes();
    return String.valueOf(processTypes.get(0).getId().getId());
  }

  public String getDefaultProduct() throws UseCaseException
  {
    GetGeneralInfoInput input = new GetGeneralInfoInput("OnlineSalary", "Default");
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(bpmsRepositoryRegistry.getDefaultParameterRepository());
    Map<String, Object> defaultParams = getGeneralInfo.execute(input);

    Map<String, Object> defaultParam = (Map<String, Object>) defaultParams.get("Default");
    return String.valueOf(defaultParam.get("defaultProduct"));
  }

  public String getDefaultBranch() throws UseCaseException
  {
    GetGeneralInfoInput input = new GetGeneralInfoInput("BnplLoan", "Default");
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(bpmsRepositoryRegistry.getDefaultParameterRepository());
    Map<String, Object> defaultParams = getGeneralInfo.execute(input);

    Map<String, Object> defaultParam = (Map<String, Object>) defaultParams.get("Default");
    return String.valueOf(defaultParam.get("defaultBranch"));
  }
}
