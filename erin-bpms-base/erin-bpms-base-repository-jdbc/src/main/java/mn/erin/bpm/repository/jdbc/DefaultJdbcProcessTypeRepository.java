package mn.erin.bpm.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcProcessTypeRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcProcessType;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.process.ProcessDefinitionType;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;

/**
 * @author EBazarragchaa
 */
@Repository
public class DefaultJdbcProcessTypeRepository implements ProcessTypeRepository
{
  private final JdbcProcessTypeRepository jdbcProcessTypeRepository;

  public DefaultJdbcProcessTypeRepository(JdbcProcessTypeRepository jdbcProcessTypeRepository)
  {
    this.jdbcProcessTypeRepository = jdbcProcessTypeRepository;
  }

  @Override
  public ProcessType create(String processTypeId, String processDefinitionKey, String name, ProcessDefinitionType processDefinitionType)
      throws BpmRepositoryException
  {
    try
    {
      Validate.notBlank(processTypeId, "Process Type Id is required!");
      Validate.notBlank(processDefinitionKey, "Process Definition Key is required!");
      Validate.notNull(processDefinitionType, "Process Definition Type is required!");
      Validate.notBlank(name, "Name of process type is required!");
      jdbcProcessTypeRepository.insert(processTypeId, processDefinitionKey, ProcessDefinitionType.fromEnumToString(processDefinitionType), name);

      ProcessType processTypeToReturn = new ProcessType(new ProcessTypeId(processTypeId), processDefinitionKey, processDefinitionType);
      processTypeToReturn.setName(name);
      return processTypeToReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }



  @Override
  public List<ProcessType> findByProcessTypeCategory(String processTypeCategory) {
    Validate.notNull(processTypeCategory, "Process Type Category is required!");
    List<JdbcProcessType> jdbcProcessTypes = jdbcProcessTypeRepository.findByProcessTypeCategory(processTypeCategory);
    return convertToProcessTypes(jdbcProcessTypes);
  }

  @Override
  public ProcessType findById(EntityId entityId)
  {
    Validate.notNull(entityId, "Entity Id is required!");
    Optional<JdbcProcessType> jdbcProcessType = jdbcProcessTypeRepository.findById(entityId.getId());

    if (jdbcProcessType.isPresent())
    {
      return convertToProcessType(jdbcProcessType.get());
    }
    return null;
  }

  @Override
  public Collection<ProcessType> findAll()
  {
    List<ProcessType> processTypeListToReturn = new ArrayList<>();
    Iterator<JdbcProcessType> allJdbcProcessTypeIterator = jdbcProcessTypeRepository.findAll().iterator();
    while (allJdbcProcessTypeIterator.hasNext())
    {
      processTypeListToReturn.add(convertToProcessType(allJdbcProcessTypeIterator.next()));
    }

    return processTypeListToReturn;
  }


    private List<ProcessType> convertToProcessTypes(List<JdbcProcessType> jdbcProcessTypes)
    {
        List<ProcessType> processTypes = new ArrayList<>();
        for (JdbcProcessType jdbcProcessType : jdbcProcessTypes) {
            processTypes.add(convertToProcessType(jdbcProcessType));
        }

        return processTypes;
    }

  private ProcessType convertToProcessType(JdbcProcessType jdbcProcessType)
  {
    ProcessTypeId processTypeId = new ProcessTypeId(jdbcProcessType.getProcessTypeId());
    ProcessDefinitionType processDefinitionType = ProcessDefinitionType.fromStringToEnum(jdbcProcessType.getProcessDefinitionType());
    ProcessType processTypeToReturn = new ProcessType(processTypeId, jdbcProcessType.getDefinitionKey(), processDefinitionType);
    processTypeToReturn.setVersion(jdbcProcessType.getVersion());
    processTypeToReturn.setName(jdbcProcessType.getName());
    processTypeToReturn.setProcessTypeCategory(jdbcProcessType.getProcessTypeCategory());
    return processTypeToReturn;
  }

  private boolean saveProcessType(JdbcProcessType jdbcProcessType)
  {
    String processTypeId = jdbcProcessType.getProcessTypeId();
    String processDefinitionType = jdbcProcessType.getProcessDefinitionType();
    String definitionKey = jdbcProcessType.getDefinitionKey();
    String name = jdbcProcessType.getName();

    int rowsAffected = jdbcProcessTypeRepository.insert(processTypeId, processDefinitionType, definitionKey, name);

    return rowsAffected == 1;
  }
}
