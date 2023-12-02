package mn.erin.domain.bpm.usecase.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mn.erin.domain.bpm.model.cases.CaseInstanceId;
import mn.erin.domain.bpm.model.cases.ExecutionId;
import mn.erin.domain.bpm.model.task.Task;
import mn.erin.domain.bpm.model.task.TaskId;
import mn.erin.domain.bpm.util.process.TaskUtils;

/**
 * @author Tamir
 */
public class TaskUtilsTest
{
  private static final String TEST_TASK_NAME = "Test task";
  private static final String ANOTHER_TASK_NAME = "Another task name";

  private static final String DEF_KEY_1 = "d1";
  private static final String DEF_KEY_2 = "dk2";

  private List<Task> duplicatedTasks;

  @Before
  public void setUp()
  {
    duplicatedTasks = getDuplicatedTasks();
  }

  @Test
  public void verifyLastFinishedTask()
  {
    List<Task> lastFinishedTasks = TaskUtils.getLastFinishedTasks(duplicatedTasks);
    Assert.assertEquals(2, lastFinishedTasks.size());

    for (Task lastFinishedTask : lastFinishedTasks)
    {
      if (lastFinishedTask.getName().equals(TEST_TASK_NAME))
      {
        Date endDate = lastFinishedTask.getEndDate();
        Assert.assertEquals(endDate, new Date(2020, Calendar.SEPTEMBER, 12));
      }
      else if (lastFinishedTask.getName().equals(ANOTHER_TASK_NAME))
      {
        Date endDate = lastFinishedTask.getEndDate();
        Assert.assertEquals(endDate, new Date(2020, Calendar.JULY, 3));
      }
    }
  }

  @Test
  public void verifyFilterByTaskName()
  {
    List<Task> filteredTasks = TaskUtils.filterByTaskName(duplicatedTasks);
    Assert.assertEquals(2, filteredTasks.size());
  }

  private List<Task> getDuplicatedTasks()
  {
    List<Task> duplicatedTasks = new ArrayList<>();

    duplicatedTasks.add(createTask("t1", "e1", "c1", TEST_TASK_NAME, new Date(2020, Calendar.AUGUST, 11), DEF_KEY_1));
    duplicatedTasks.add(createTask("t2", "e2", "c2", TEST_TASK_NAME, new Date(2020, Calendar.SEPTEMBER, 12), DEF_KEY_1));
    duplicatedTasks.add(createTask("t3", "e3", "c3", TEST_TASK_NAME, new Date(2020, Calendar.JULY, 8), DEF_KEY_1));

    duplicatedTasks.add(createTask("t3", "e3", "c3", ANOTHER_TASK_NAME, new Date(2020, Calendar.JULY, 3), DEF_KEY_2));
    duplicatedTasks.add(createTask("t3", "e3", "c3", ANOTHER_TASK_NAME, new Date(2019, Calendar.MARCH, 1), DEF_KEY_2));

    return duplicatedTasks;
  }

  private Task createTask(String taskId, String execId, String instanceId, String name, Date endDate, String defKey)
  {
    Task task = new Task(TaskId.valueOf(taskId), ExecutionId.valueOf(execId), CaseInstanceId.valueOf(instanceId), name);

    task.setEndDate(endDate);
    task.setDefinitionKey(defKey);

    return task;
  }
}
