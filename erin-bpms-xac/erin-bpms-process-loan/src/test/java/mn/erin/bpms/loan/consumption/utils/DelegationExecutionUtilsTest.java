/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.utils;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Tamir
 */
public class DelegationExecutionUtilsTest
{
  public static final String VALUE_STRING = "Base64String";
  private DelegateExecution delegateExecution;
  private static final String PARAMETER_NAME = "fingerPrint";

  @Before
  public void setUp()
  {
    delegateExecution = Mockito.mock(DelegateExecution.class);
  }

  @Test
  public void when_execution_null()
  {
    Assert.assertNull(DelegationExecutionUtils.getExecutionParameterStringValue(null, PARAMETER_NAME));
  }

  @Test
  public void when_has_variable_false()
  {
    Mockito.when(delegateExecution.hasVariable(PARAMETER_NAME)).thenReturn(true);
    Assert.assertNull(DelegationExecutionUtils.getExecutionParameterStringValue(delegateExecution, PARAMETER_NAME));
  }

  @Test
  public void when_another_instance_from_string()
  {
    Mockito.when(delegateExecution.hasVariable(PARAMETER_NAME)).thenReturn(true);
    Mockito.when(delegateExecution.getVariable(PARAMETER_NAME)).thenReturn(10);

    Assert.assertNull(DelegationExecutionUtils.getExecutionParameterStringValue(delegateExecution, PARAMETER_NAME));
  }

  @Test
  public void when_successful_get_parameter_value()
  {
    Mockito.when(delegateExecution.hasVariable(PARAMETER_NAME)).thenReturn(true);
    Mockito.when(delegateExecution.getVariable(PARAMETER_NAME)).thenReturn(VALUE_STRING);

    String value = DelegationExecutionUtils.getExecutionParameterStringValue(delegateExecution, PARAMETER_NAME);
    Assert.assertEquals(VALUE_STRING, value);
  }
}
