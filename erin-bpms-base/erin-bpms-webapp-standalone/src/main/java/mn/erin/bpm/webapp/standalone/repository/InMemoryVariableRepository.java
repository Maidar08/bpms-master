package mn.erin.bpm.webapp.standalone.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.repository.VariableRepository;

@Repository
public class InMemoryVariableRepository implements VariableRepository
{
  @Override
  public Collection<Variable> findByContext(String context)
  {
    return null;
  }

  @Override
  public Variable findById(EntityId entityId)
  {
    return null;
  }

  @Override
  public Collection<Variable> findAll()
  {
    return null;
  }
}
