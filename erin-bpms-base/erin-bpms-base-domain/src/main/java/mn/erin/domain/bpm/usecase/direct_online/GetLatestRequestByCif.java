package mn.erin.domain.bpm.usecase.direct_online;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

/**
 * @author Lkhagvadorj.A
 **/

public class GetLatestRequestByCif extends AbstractUseCase<Map<String, String>, ProcessRequest>
{
  private final ProcessRequestRepository processRequestRepository;
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private final Environment environment;

  public GetLatestRequestByCif(ProcessRequestRepository processRequestRepository, BpmsRepositoryRegistry bpmsRepositoryRegistry, Environment environment)
  {
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "Process request repository is required!");
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
    this.environment = environment;
  }

  @Override
  public ProcessRequest execute(Map<String, String> input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    if (StringUtils.isBlank(input.get(CIF_NUMBER)) || StringUtils.isBlank(input.get(PROCESS_TYPE_ID)))
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    long duration = Long.parseLong(getValidString(input.get("duration")));
    LocalDateTime endDate = LocalDateTime.now(ZoneId.of("UTC+8"));
    LocalDateTime startDate = endDate.minusHours(duration);
    try
    {
      Collection<ProcessRequest> processRequests = this.processRequestRepository
          .getDirectOnlineProcessRequests(input.get(CIF_NUMBER), startDate, endDate, input.get(PROCESS_TYPE_ID));
      return findLatestProcess(processRequests);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }

  private ProcessRequest findLatestProcess(Collection<ProcessRequest> processRequests)
  {
    ProcessRequest latestProcess = null;

    for (ProcessRequest request : processRequests)
    {
      if (null == latestProcess)
      {
        latestProcess = request;
      }
      else
      {
        LocalDateTime currentRequestDate = request.getCreatedTime();
        LocalDateTime latestRequestDate = latestProcess.getCreatedTime();
        if (currentRequestDate.isAfter(latestRequestDate))
        {
          latestProcess = request;
        }
      }
    }

    return latestProcess;
  }
}
