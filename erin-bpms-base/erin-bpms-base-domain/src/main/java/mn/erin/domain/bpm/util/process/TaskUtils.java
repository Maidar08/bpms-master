package mn.erin.domain.bpm.util.process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import mn.erin.domain.bpm.model.task.Task;

/**
 * @author Tamir
 */
public final class TaskUtils
{
  private TaskUtils()
  {

  }

  public static List<Task> getLastFinishedTasks(List<Task> tasks)
  {
    List<Task> filteredCompletedTasks = new ArrayList<>();
    Set<String> definitionKeys = getDefinitionKeys(tasks);

    for (String definitionKey : definitionKeys)
    {
      List<Task> tasksHasSameDifKeys = new ArrayList<>();

      for (Task task : tasks)
      {
        if (task.getDefinitionKey().equals(definitionKey))
        {
          tasksHasSameDifKeys.add(task);
        }
      }

      tasksHasSameDifKeys.sort(Comparator.comparing(Task::getEndDate).reversed());
      filteredCompletedTasks.add(tasksHasSameDifKeys.get(0));
    }
    filteredCompletedTasks.sort(Comparator.comparing(Task::getEndDate).reversed());
    return filteredCompletedTasks;
  }

  public static Set<String> getDefinitionKeys(List<Task> tasks)
  {
    Set<String> definitionKeys = new HashSet<>();

    for (Task completedTask : tasks)
    {
      if (null != completedTask.getDefinitionKey())
      {
        definitionKeys.add(completedTask.getDefinitionKey());
      }
    }
    return definitionKeys;
  }

  public static List<Task> filterByTaskName(List<Task> tasks)
  {
    return tasks.stream().filter(distinctBy(Task::getName)).collect(Collectors.toList());
  }

  public static <T> Predicate<T> distinctBy(Function<? super T, ?> f)
  {
    Set<Object> objects = new HashSet<>();
    return t -> objects.add(f.apply(t));
  }
}
