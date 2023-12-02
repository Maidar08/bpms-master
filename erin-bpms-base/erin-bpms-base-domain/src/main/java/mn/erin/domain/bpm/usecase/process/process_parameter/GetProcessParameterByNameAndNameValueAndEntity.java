package mn.erin.domain.bpm.usecase.process.process_parameter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.process.ParameterEntityType;
import mn.erin.domain.bpm.model.process.Process;
import mn.erin.domain.bpm.repository.ProcessRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_ENTITY_TYPE_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_ENTITY_TYPE_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NAME_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PARAMETER_NAME_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 */

public class GetProcessParameterByNameAndNameValueAndEntity extends AbstractUseCase<ProcessParameterByNameAndEntityInput, List<Process>> {
    private final ProcessRepository processRepository;

    public GetProcessParameterByNameAndNameValueAndEntity(ProcessRepository processRepository)
    {
        this.processRepository = Objects.requireNonNull(processRepository, "ProcessRepository is required!");
    }

    @Override
    public List<Process> execute(ProcessParameterByNameAndEntityInput input) throws UseCaseException {
        if (null == input)
        {
            throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
        }
        String parameterName = input.getParameterName();
        String parameterNameValue = input.getParameterNameValue();
        ParameterEntityType entityType = input.getEntityType();

        if (null == entityType)
        {
            throw new UseCaseException(PARAMETER_ENTITY_TYPE_NULL_CODE, PARAMETER_ENTITY_TYPE_NULL_MESSAGE);
        }

        if (StringUtils.isBlank(parameterName) || StringUtils.isBlank(parameterNameValue))
        {
            throw new UseCaseException(PARAMETER_NAME_NULL_CODE, PARAMETER_NAME_NULL_MESSAGE);
        }

        List<Process> parameters = processRepository.filterByParameterNameAndNameValueAndEntityType(entityType, parameterName, parameterNameValue);
        if (parameters.isEmpty())
        {
            return Collections.emptyList();
        }
        return parameters;
    }
}
