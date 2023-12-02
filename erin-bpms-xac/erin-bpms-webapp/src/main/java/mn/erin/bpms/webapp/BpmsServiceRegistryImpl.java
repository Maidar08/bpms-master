package mn.erin.bpms.webapp;

import javax.inject.Inject;

import mn.erin.domain.bpm.service.BnplCoreBankingService;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.CustomerService;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.DocumentService;
import mn.erin.domain.bpm.service.ExecutionService;
import mn.erin.domain.bpm.service.LoanService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.OrganizationService;
import mn.erin.domain.bpm.service.ProcessTypeService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.service.TaskFormService;
import mn.erin.domain.bpm.service.TaskService;

/**
 * @author Tamir
 */
public class BpmsServiceRegistryImpl implements BpmsServiceRegistry
{
  private CaseService caseService;
  private RuntimeService runtimeService;
  private CoreBankingService coreBankingService;

  private NewCoreBankingService newCoreBankingService;

  private CustomerService customerService;
  private DocumentService documentService;

  private ExecutionService executionService;
  private LoanService loanService;

  private OrganizationService organizationService;
  private ProcessTypeService processTypeService;

  private TaskFormService taskFormService;
  private TaskService taskService;
  private DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private BnplCoreBankingService bnplCoreBankingService;

  @Inject
  public void setRuntimeService(RuntimeService runtimeService)
  {
    this.runtimeService = runtimeService;
  }

  @Inject
  public void setCaseService(CaseService caseService)
  {
    this.caseService = caseService;
  }

  @Inject
  public void setNewCoreBankingService(NewCoreBankingService newCoreBankingService)
  {
    this.newCoreBankingService = newCoreBankingService;
  }

  @Inject
  public void setCoreBankingService(CoreBankingService coreBankingService)
  {
    this.coreBankingService = coreBankingService;
  }

  @Inject
  public void setCustomerService(CustomerService customerService)
  {
    this.customerService = customerService;
  }

  @Inject
  public void setDocumentService(DocumentService documentService)
  {
    this.documentService = documentService;
  }

  @Inject
  public void setExecutionService(ExecutionService executionService)
  {
    this.executionService = executionService;
  }

  @Inject
  public void setLoanService(LoanService loanService)
  {
    this.loanService = loanService;
  }

  @Inject
  public void setOrganizationService(OrganizationService organizationService)
  {
    this.organizationService = organizationService;
  }

  @Inject
  public void setProcessTypeService(ProcessTypeService processTypeService)
  {
    this.processTypeService = processTypeService;
  }

  @Inject
  public void setTaskFormService(TaskFormService taskFormService)
  {
    this.taskFormService = taskFormService;
  }

  @Inject
  public void setTaskService(TaskService taskService)
  {
    this.taskService = taskService;
  }

  @Inject
  public void setDirectOnlineCoreBankingService(DirectOnlineCoreBankingService directOnlineCoreBankingService)
  {
    this.directOnlineCoreBankingService = directOnlineCoreBankingService;
  }

  @Inject
  public void setBnplCoreBankingService(BnplCoreBankingService bnplCoreBankingService)
  {
    this.bnplCoreBankingService = bnplCoreBankingService;
  }

  @Override
  public CaseService getCaseService()
  {
    return this.caseService;
  }

  @Override
  public CoreBankingService getCoreBankingService()
  {
    return this.coreBankingService;
  }

  @Override
  public NewCoreBankingService getNewCoreBankingService()
  {
    return this.newCoreBankingService;
  }

  @Override
  public CustomerService getCustomerService()
  {
    return this.customerService;
  }

  @Override
  public DocumentService getDocumentService()
  {
    return this.documentService;
  }

  @Override
  public ExecutionService getExecutionService()
  {
    return this.executionService;
  }

  @Override
  public LoanService getLoanService()
  {
    return this.loanService;
  }

  @Override
  public OrganizationService getOrganizationService()
  {
    return this.organizationService;
  }

  @Override
  public ProcessTypeService getProcessTypeService()
  {
    return this.processTypeService;
  }

  @Override
  public TaskFormService getTaskFormService()
  {
    return this.taskFormService;
  }

  @Override
  public TaskService getTaskService()
  {
    return this.taskService;
  }

  @Override
  public RuntimeService getRuntimeService()
  {
    return this.runtimeService;
  }

  @Override
  public DirectOnlineCoreBankingService getDirectOnlineCoreBankingService()
  {
    return this.directOnlineCoreBankingService;
  }

  @Override
  public BnplCoreBankingService getBnplCoreBankingService()
  {
    return this.bnplCoreBankingService;
  }
}
