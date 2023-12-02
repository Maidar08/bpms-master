/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.base.repository.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

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
public class InMemoryProcessTypeRepository implements ProcessTypeRepository
{
  private Collection<ProcessType> processTypes = new ArrayList<>();
  private String application_form = "\u0410\u0436\u043b\u044b\u043d \u0430\u043d\u043a\u0435\u0442";

  public InMemoryProcessTypeRepository()
  {
    this.processTypes.add(create("applicationForm", "erin_cms_application_form", application_form, ProcessDefinitionType.CASE));
  }

  @Override
  public ProcessType create(String processTypeId, String processDefinitionKey, String name, ProcessDefinitionType processDefinitionType)
  {
    ProcessType processType = new ProcessType(ProcessTypeId.valueOf(processTypeId), processDefinitionKey, processDefinitionType);
    processType.setName(name);

    return processType;
  }

  @Override
  public ProcessType findById(EntityId entityId)
  {
    for (ProcessType processType : processTypes)
    {
      String id = processType.getId().getId();

      if (id.equals(entityId.getId()))
      {
        return processType;
      }
    }
    return null;
  }

  @Override
  public Collection<ProcessType> findAll()
  {
    return this.processTypes;
  }

  @Override
  public List<ProcessType> findByProcessTypeCategory(String processTypeCategory) throws BpmRepositoryException {
    throw  new UnsupportedOperationException();
  }
}
