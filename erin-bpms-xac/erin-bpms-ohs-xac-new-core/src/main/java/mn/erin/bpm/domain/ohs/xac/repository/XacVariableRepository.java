/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpm.domain.ohs.xac.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import mn.erin.common.file.FileUtil;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;
import mn.erin.domain.bpm.repository.VariableRepository;

/**
 * @author Tamir
 */
public class XacVariableRepository implements VariableRepository
{
  private static final String VARIABLES_JSON = "/xac-variables.json";

  @Override
  public Variable findById(EntityId entityId)
  {
    return null;
  }

  @Override
  public Collection<Variable> findAll()
  {
    return readVariablesFromFile();
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

  private Collection<Variable> readVariablesFromFile()
  {
    Collection<Variable> variables = new ArrayList<>();

    try (InputStream inputStream = XacVariableRepository.class.getResourceAsStream(VARIABLES_JSON))
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
}
