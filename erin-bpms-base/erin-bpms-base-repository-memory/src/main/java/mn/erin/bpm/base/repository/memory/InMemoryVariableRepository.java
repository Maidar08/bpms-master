package mn.erin.bpm.base.repository.memory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import mn.erin.common.file.FileUtil;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.repository.VariableRepository;

@Repository
public class InMemoryVariableRepository implements VariableRepository
{
  private static final String VARIABLES_JSON = "/memory-variables.json";
  @Override
  public Collection<Variable> findByContext(String context)
  {
    Collection<Variable> foundVariables = new ArrayList<>();
    Collection<Variable> variables = readVariablesFromFile();

    for (Variable variable : variables)
    {
      if (variable.getContext().equalsIgnoreCase(context))
      {
        foundVariables.add(variable);
      }
    }
    return foundVariables;
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

  private Collection<Variable> readVariablesFromFile()
  {
    Collection<Variable> variables = new ArrayList<>();

    try (InputStream inputStream = InMemoryVariableRepository.class.getResourceAsStream(VARIABLES_JSON))
    {
      JSONArray variableArray = FileUtil.readInputStream(inputStream);

      for (int index = 0; index < variableArray.length(); index++)
      {
        JSONObject variableJson = variableArray.getJSONObject(index);

        variables.add(toVariable(variableJson));
      }
      return variables;
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  private Variable toVariable(JSONObject variableJson)
  {
    String id = variableJson.getString("id");
    String type = variableJson.getString("type");

    String label = variableJson.getString("label");
    String context = variableJson.getString("context");
    boolean isLocalVariable = variableJson.getBoolean("isLocalVariable");

    return new Variable(VariableId.valueOf(id), type, label, context, isLocalVariable);
  }
}
