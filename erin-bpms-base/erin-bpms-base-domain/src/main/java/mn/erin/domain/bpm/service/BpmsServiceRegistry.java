package mn.erin.domain.bpm.service;

/**
 * @author Tamir
 */
public interface BpmsServiceRegistry
{
  CaseService getCaseService();

  CoreBankingService getCoreBankingService();

  NewCoreBankingService getNewCoreBankingService();

  CustomerService getCustomerService();

  DocumentService getDocumentService();

  ExecutionService getExecutionService();

  LoanService getLoanService();

  OrganizationService getOrganizationService();

  ProcessTypeService getProcessTypeService();

  TaskFormService getTaskFormService();

  TaskService getTaskService();

  RuntimeService getRuntimeService();

  DirectOnlineCoreBankingService getDirectOnlineCoreBankingService();

  BnplCoreBankingService getBnplCoreBankingService();
}
