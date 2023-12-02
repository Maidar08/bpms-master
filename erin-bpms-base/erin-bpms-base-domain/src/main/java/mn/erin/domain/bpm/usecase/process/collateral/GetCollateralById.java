package mn.erin.domain.bpm.usecase.process.collateral;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.usecase.process.process_parameter.GetProcessParameterByNameAndNameValueAndEntity;
import mn.erin.domain.bpm.usecase.process.process_parameter.ProcessParameterByNameAndEntityInput;

/**
 * @author Lkhagvadorj
 */
public class GetCollateralById extends AbstractUseCase<GetCollateralByIdInput, Process>
{
  private final ProcessRepository processRepository;

  public GetCollateralById(ProcessRepository processRepository)
  {
    this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
  }
  @Override
  public Process execute(GetCollateralByIdInput input) throws UseCaseException
  {
    try
    {
      Validate.notNull(input);
      Validate.notBlank(input.getCollateralId());

      ProcessParameterByNameAndEntityInput processParameterByNameAndEntityInput = new ProcessParameterByNameAndEntityInput("collateralId", input.getCollateralId(),
          ParameterEntityType.COLLATERAL);
      GetProcessParameterByNameAndNameValueAndEntity useCase = new GetProcessParameterByNameAndNameValueAndEntity(processRepository);
      List<Process> filteredProcess = useCase.execute(processParameterByNameAndEntityInput);

      if (filteredProcess.isEmpty())
      {
        return null;
      }
      return filteredProcess.get(0);
    }
    catch (NullPointerException | IllegalArgumentException e)
    {
      throw new UseCaseException(e.getMessage());
    }
  }
}
