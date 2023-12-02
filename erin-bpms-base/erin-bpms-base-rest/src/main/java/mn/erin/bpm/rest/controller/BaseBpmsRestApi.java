package mn.erin.bpm.rest.controller;

import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;

/**
 * @author Tamir
 */
public abstract class BaseBpmsRestApi
{
  protected BpmsServiceRegistry bpmsServiceRegistry;
  protected BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public BaseBpmsRestApi(BpmsServiceRegistry bpmsServiceRegistry, BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.bpmsServiceRegistry = bpmsServiceRegistry;
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }
}
