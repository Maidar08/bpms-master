package mn.erin.domain.bpm.repository;

import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;

/**
 * @author Tamir
 */
public interface BpmsRepositoryRegistry
{
  DocumentInfoRepository getDocumentInfoRepository();

  DocumentRepository getDocumentRepository();

  ErrorMessageRepository getErrorMessageRepository();

  ProcessRepository getProcessRepository();

  ProcessRequestRepository getProcessRequestRepository();

  ProcessTypeRepository getProcessTypeRepository();

  TaskFormRepository getTaskFormRepository();

  VariableRepository getVariableRepository();

  ProductRepository getProductRepository();

  FormRelationRepository getFormRelationRepository();

  CollateralProductRepository getCollateralProductRepository();

  OrganizationSalaryRepository getOrganizationSalaryRepository();

  OrganizationLeasingRepository getOrganizationLeasingRepository();

  LoanContractRequestRepository getLoanContractRequestRepository();

  LoanContractParameterRepository getLoanContractParameterRepository();

  DefaultParameterRepository getDefaultParameterRepository();
}
