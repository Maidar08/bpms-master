package mn.erin.bpms.loan.consumption.task_listener;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

/**
 * @author Tamir
 */
public class SetRootParentIdTaskListener implements TaskListener
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
      for (CamundaProperty camundaProperty : propertiesCollection)
      {
        if (null != camundaProperty)
        {
          String camundaId = camundaProperty.getCamundaName();

          if (!StringUtils.isBlank(camundaId))
          {
            String lowerCased = camundaId.toLowerCase();

            if (lowerCased.contains("rootparenttaskname"))
            {
              delegateTask.setDescription(camundaProperty.getCamundaValue());
            }
          }
        }
      }
    }
  }
}
