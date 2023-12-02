package mn.erin.domain.bpm.usecase;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.BPMS_PARAMETER_NOT_FOUND_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

public class GetGeneralInfo extends AbstractUseCase<GetGeneralInfoInput, Map<String, Object>>
{
  private final DefaultParameterRepository defaultParameterRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(GetGeneralInfo.class);

  public GetGeneralInfo(DefaultParameterRepository defaultParameterRepository)
  {
    this.defaultParameterRepository = Objects.requireNonNull(defaultParameterRepository, "Default parameter repository is required!");
  }

  @Override
  public Map<String, Object> execute(GetGeneralInfoInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    try
    {
      Map<String, Object> defaultParameters;
      String processType = input.getProcessType();
      String entity = input.getEntity();

      defaultParameters = defaultParameterRepository.getDefaultParametersByProcessType(processType, entity);

      if (defaultParameters.isEmpty())
      {
        LOGGER.error("######### PARAMETER VALUES NOT FOUND WITH PROCESS TYPE = [{}] AND ENTITY = [{}] ", processType, entity);
        throw new UseCaseException(BPMS_PARAMETER_NOT_FOUND_CODE, "Parameters not found with  " + processType + " process type and " + entity + " entity");
      }
      return defaultParameters;
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
