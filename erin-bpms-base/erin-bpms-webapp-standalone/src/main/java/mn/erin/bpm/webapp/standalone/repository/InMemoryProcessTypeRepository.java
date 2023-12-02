/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.webapp.standalone.repository;

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
  private String consumption_loan_name = "\u0425\u044d\u0440\u044d\u0433\u043b\u044d\u044d\u043d\u0438\u0439 \u0437\u044d\u044d\u043b";

  public InMemoryProcessTypeRepository()
  {
    this.processTypes.add(create("consumptionLoan", "bpms_consumption_loan_case", consumption_loan_name, ProcessDefinitionType.CASE));
  }

  @Override
  public ProcessType create(String processTypeId, String processDefinitionKey, String name, ProcessDefinitionType processDefinitionType)
  {
    ProcessType processType = new ProcessType(ProcessTypeId.valueOf(processTypeId), processDefinitionKey, processDefinitionType);
    processType.setName(name);

    return processType;
  }

  @Override
  public List<ProcessType> findByProcessTypeCategory(String processTypeCategory) throws BpmRepositoryException {
    throw new UnsupportedOperationException();
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
}
