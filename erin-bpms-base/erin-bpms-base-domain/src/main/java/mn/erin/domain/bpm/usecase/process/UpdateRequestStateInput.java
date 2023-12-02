/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.util.Objects;

import mn.erin.domain.bpm.model.process.ProcessRequestState;

/**
 * @author Tamir
 */
public class UpdateRequestStateInput
{
  private final String processRequestId;
  private final ProcessRequestState state;

  public UpdateRequestStateInput(String processRequestId, ProcessRequestState state)
  {
    this.processRequestId = Objects.requireNonNull(processRequestId, "Process request id is required!");
    this.state = Objects.requireNonNull(state, "Process request state is required!");
  }

  public String getProcessRequestId()
  {
    return processRequestId;
  }

  public ProcessRequestState getState()
  {
    return state;
  }
}
