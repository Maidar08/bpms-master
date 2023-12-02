package listener;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

/**
 * @author Lkhagvadorj
 */
public class SetParentTaskIdTaskListener implements TaskListener
{
  @Override
  public void notify(DelegateTask delegateTask)
  {
    ExtensionElements extensionElements = delegateTask.getBpmnModelElementInstance().getExtensionElements();

    CamundaProperties camundaProperties = extensionElements.getElementsQuery()
        .filterByType(CamundaProperties.class)
        .singleResult();

    if (null == camundaProperties)
    {
      return;
    }

    Collection<CamundaProperty> propertiesCollection = camundaProperties.getCamundaProperties();

    if (!propertiesCollection.isEmpty())
    {
      String value = propertiesCollection.iterator().next().getCamundaValue();
      if (!StringUtils.isBlank(value))
      {
        delegateTask.setDescription(value);
      }
    }
  }
}
