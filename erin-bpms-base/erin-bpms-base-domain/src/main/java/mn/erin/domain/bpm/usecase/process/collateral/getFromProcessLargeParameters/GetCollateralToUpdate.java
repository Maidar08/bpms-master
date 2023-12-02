package mn.erin.domain.bpm.usecase.process.collateral.getFromProcessLargeParameters;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.GetProcessParameterOutput;

import static mn.erin.domain.bpm.BpmMessagesConstants.CLOB_CONVERSION_TO_STRING_FAIL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CLOB_CONVERSION_TO_STRING_FAIL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CREATE_COLLATERAL_TASK_NAME;

/**
 * @author Lkhagvadorj
 */
public class GetCollateralToUpdate extends AbstractUseCase<GetCollateralToUpdateInput, GetProcessParameterOutput>
{
  private final ProcessRepository processRepository;

  public GetCollateralToUpdate(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "Process repository is required!");
  }

  @Override
  public GetProcessParameterOutput execute(GetCollateralToUpdateInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }
    try
    {
      Process returnedProcess = processRepository.filterLargeParamBySearchKeyFromValue(input.getCollateralId(), CREATE_COLLATERAL_TASK_NAME, input.getParameterEntityType());
      if (null == returnedProcess)
      {
        return null;
      }

      final Map<String, Serializable> processParameters = returnedProcess.getProcessParameters().get(ParameterEntityType.COMPLETED_FORM);
      for (Map.Entry<String, Serializable> entry : processParameters.entrySet())
      {
        if (null != entry.getValue() && entry.getValue().toString().contains(input.getCollateralId()))
        {
          return new GetProcessParameterOutput(entry.getValue());
        }
      }
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
    catch (SQLException exception)
    {
      throw new UseCaseException(CLOB_CONVERSION_TO_STRING_FAIL_CODE, CLOB_CONVERSION_TO_STRING_FAIL_MESSAGE);
    }
    return null;
  }
}
