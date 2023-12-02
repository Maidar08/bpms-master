package mn.erin.domain.bpm.usecase.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.cases.Execution;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;

/**
 * @author Lkhagvadorj.A
 **/

public class GroupEnabledExecutionsByParentId extends AbstractUseCase<GroupEnableExecutionsByParentIdInput, Map<String, List<Execution>>>
{

  @Override
  public Map<String, List<Execution>> execute(GroupEnableExecutionsByParentIdInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    List<Execution> activeExecutionList = input.getActiveExecutionList();
    List<Execution> enabledExecutionList = input.getEnabledExecutionList();

    Map<String, List<Execution>> groupedExecution = new HashMap<>();
    filterEnableExecutions(activeExecutionList, enabledExecutionList, groupedExecution);
    filterActiveExecutions(activeExecutionList, groupedExecution);
    clearEmptyMap(groupedExecution);
    setUngroupedExecutions(activeExecutionList, enabledExecutionList, groupedExecution);
    return groupedExecution;
  }

  private void clearEmptyMap(Map<String, List<Execution>> groupedExecution)
  {
    groupedExecution.values().removeIf(List::isEmpty);
  }

  private void filterEnableExecutions(List<Execution> activeExecutionList, List<Execution> enabledExecutionList, Map<String, List<Execution>> groupedExecution)
  {
    for (Execution activeExecution : activeExecutionList)
    {
      String id = activeExecution.getId().getId();
      String activityName = activeExecution.getActivityName();
      groupedExecution.put(activityName, new ArrayList<>());
      for (Execution enableExecution : enabledExecutionList)
      {
        String parentId = enableExecution.getParentId();
        if (parentId.equals(id))
        {
          groupedExecution.get(activityName).add(enableExecution);
        }
      }
    }
  }

  private void filterActiveExecutions(List<Execution> activeExecutionList, Map<String, List<Execution>> groupedExecution)
  {
    for (Execution activeExecution : activeExecutionList)
    {
      String parentId = activeExecution.getParentId();
      if (!activeExecution.getActivityType().equals("stage"))
      {
        continue;
      }
      for (Execution execution : activeExecutionList)
      {
        if (execution.getId().getId().equals(parentId))
        {
          String activityName = execution.getActivityName();
          groupedExecution.get(activityName).add(activeExecution);
          break;
        }
      }
    }
  }

  private void setUngroupedExecutions(List<Execution> activeExecutionList, List<Execution> enabledExecutionList, Map<String, List<Execution>> groupedExecution)
  {
    List<Execution> ungroupedExecutions = new ArrayList<>();
    for (Execution enableExecution : enabledExecutionList)
    {
      final String id = enableExecution.getId().getId();
      final String parentId = enableExecution.getParentId();
      final Optional<Execution> first = activeExecutionList.stream().filter(exec -> exec.getId().getId().equals(parentId)).findFirst();
      if (!first.isPresent() || null == first.get())
      {
        ungroupedExecutions.add(enableExecution);
      }
    }
    if (!ungroupedExecutions.isEmpty())
    {
      groupedExecution.put("ungrouped", ungroupedExecutions);
    }
  }
}
