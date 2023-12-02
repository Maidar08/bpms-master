/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.task;

import java.util.Date;
import java.util.Objects;

import mn.erin.domain.base.model.Entity;
import mn.erin.domain.bpm.model.cases.CaseInstanceId;
import mn.erin.domain.bpm.model.cases.ExecutionId;

/**
 * @author Tamir
 */
public class Task implements Entity<Task>
{
  private final TaskId id;
  private final ExecutionId executionId;
  private final CaseInstanceId caseInstanceId;
  private String name;

  private String type;
  private String status;

  private String assignee;
  private String definitionKey;

  private Date startDate;
  private Date endDate;

  private String parentTaskId;
  private String processInstanceId;

  public Task(TaskId id, ExecutionId executionId, CaseInstanceId caseInstanceId, String name)
  {
    this.id = Objects.requireNonNull(id, "Task id is required!");
    this.executionId = Objects.requireNonNull(executionId, "Execution id is required!");
    this.caseInstanceId = Objects.requireNonNull(caseInstanceId, "Case instance is required!");
    this.name = Objects.requireNonNull(name, "Task name is required!");
  }

  public TaskId getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public ExecutionId getExecutionId()
  {
    return executionId;
  }

  public CaseInstanceId getCaseInstanceId()
  {
    return caseInstanceId;
  }

  public String getAssignee()
  {
    return assignee;
  }

  public void setAssignee(String assignee)
  {
    this.assignee = assignee;
  }

  public String getDefinitionKey()
  {
    return definitionKey;
  }

  public void setDefinitionKey(String definitionKey)
  {
    this.definitionKey = definitionKey;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public void setStartDate(Date startDate)
  {
    this.startDate = startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public void setEndDate(Date endDate)
  {
    this.endDate = endDate;
  }

  public String getParentTaskId()
  {
    return parentTaskId;
  }

  public void setParentTaskId(String parentTaskId)
  {
    this.parentTaskId = parentTaskId;
  }

  public String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  @Override
  public boolean sameIdentityAs(Task other)
  {
    return other != null && (this.id.equals(other.id));
  }
}
