package mn.erin.bpm.repository.jdbc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author EBazarragchaa
 */
@Table("PROCESS_TYPE")
public class JdbcProcessType
{
  @Id
  String processTypeId;
  String definitionKey;
  String version;
  String name;
  String processDefinitionType;
  String processTypeCategory;

  public String getProcessTypeId()
  {
    return processTypeId;
  }

  public void setProcessTypeId(String processTypeId)
  {
    this.processTypeId = processTypeId;
  }

  public String getDefinitionKey()
  {
    return definitionKey;
  }

  public void setDefinitionKey(String definitionKey)
  {
    this.definitionKey = definitionKey;
  }

  public String getVersion()
  {
    return version;
  }

  public void setVersion(String version)
  {
    this.version = version;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getProcessDefinitionType()
  {
    return processDefinitionType;
  }

  public void setProcessDefinitionType(String processDefinitionType)
  {
    this.processDefinitionType = processDefinitionType;
  }
  public String getProcessTypeCategory() {
    return processTypeCategory;
  }

  public void setProcessTypeCategory(String processTypeCategory) {
    this.processTypeCategory = processTypeCategory;
  }
}
