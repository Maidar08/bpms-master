package mn.erin.bpm.rest.model;

import java.util.List;
import java.util.Map;

/**
 * @author Tamir
 */
public class RestTaskFormRelation
{
  private String taskDefinitionId;
  private Map<String, List<RestFormFieldRelation>> fieldRelations;

  public RestTaskFormRelation()
  {

  }


  public RestTaskFormRelation(String taskDefinitionId,
      Map<String, List<RestFormFieldRelation>> fieldRelations)
  {
    this.taskDefinitionId = taskDefinitionId;
    this.fieldRelations = fieldRelations;
  }

  public String getTaskDefinitionId()
  {
    return taskDefinitionId;
  }

  public void setTaskDefinitionId(String taskDefinitionId)
  {
    this.taskDefinitionId = taskDefinitionId;
  }

  public Map<String, List<RestFormFieldRelation>> getFieldRelations()
  {
    return fieldRelations;
  }

  public void setFieldRelations(Map<String, List<RestFormFieldRelation>> fieldRelations)
  {
    this.fieldRelations = fieldRelations;
  }
}
