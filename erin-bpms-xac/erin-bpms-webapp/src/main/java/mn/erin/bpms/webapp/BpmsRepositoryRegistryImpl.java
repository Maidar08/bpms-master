package mn.erin.bpms.webapp;

import javax.inject.Inject;

import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.CollateralProductRepository;
import mn.erin.domain.bpm.repository.DocumentInfoRepository;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.repository.ErrorMessageRepository;
import mn.erin.domain.bpm.repository.FormRelationRepository;
import mn.erin.domain.bpm.repository.LoanContractParameterRepository;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.repository.TaskFormRepository;
import mn.erin.domain.bpm.repository.VariableRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;

/**
 * @author Tamir
 */
public class BpmsRepositoryRegistryImpl implements BpmsRepositoryRegistry
{
  private DocumentInfoRepository documentInfoRepository;
  private DocumentRepository documentRepository;

  private ErrorMessageRepository errorMessageRepository;
  private ProcessRepository processRepository;

  private ProcessRequestRepository processRequestRepository;
  private ProcessTypeRepository processTypeRepository;

  private TaskFormRepository taskFormRepository;
  private VariableRepository variableRepository;

  private ProductRepository productRepository;
  private FormRelationRepository formRelationRepository;

  private CollateralProductRepository collateralProductRepository;
  private OrganizationSalaryRepository organizationSalaryRepository;
  private OrganizationLeasingRepository organizationLeasingRepository;
  private LoanContractRequestRepository loanContractRequestRepository;
  private LoanContractParameterRepository loanContractParameterRepository;
  private DefaultParameterRepository defaultParameterRepository;

  @Inject
  public void setDocumentInfoRepository(DocumentInfoRepository documentInfoRepository)
  {
    this.documentInfoRepository = documentInfoRepository;
  }

  @Inject
  public void setDocumentRepository(DocumentRepository documentRepository)
  {
    this.documentRepository = documentRepository;
  }

  @Inject
  public void setErrorMessageRepository(ErrorMessageRepository errorMessageRepository)
  {
    this.errorMessageRepository = errorMessageRepository;
  }

  @Inject
  public void setProcessRepository(ProcessRepository processRepository)
  {
    this.processRepository = processRepository;
  }

  @Inject
  public void setProcessRequestRepository(ProcessRequestRepository processRequestRepository)
  {
    this.processRequestRepository = processRequestRepository;
  }

  @Inject
  public void setProcessTypeRepository(ProcessTypeRepository processTypeRepository)
  {
    this.processTypeRepository = processTypeRepository;
  }

  @Inject
  public void setTaskFormRepository(TaskFormRepository taskFormRepository)
  {
    this.taskFormRepository = taskFormRepository;
  }

  @Inject
  public void setVariableRepository(VariableRepository variableRepository)
  {
    this.variableRepository = variableRepository;
  }

  @Inject
  public void setProductRepository(ProductRepository productRepository)
  {
    this.productRepository = productRepository;
  }

  @Inject
  public void setFormRelationRepository(FormRelationRepository formRelationRepository)
  {
    this.formRelationRepository = formRelationRepository;
  }

  @Inject
  public void setCollateralProductRepository(CollateralProductRepository collateralProductRepository)
  {
    this.collateralProductRepository = collateralProductRepository;
  }

  @Inject
  public void setOrganizationSalaryRepository(OrganizationSalaryRepository organizationSalaryRepository)
  {
    this.organizationSalaryRepository = organizationSalaryRepository;
  }

  @Inject
  public void setOrganizationLeasingRepository(OrganizationLeasingRepository organizationLeasingRepository)
  {
    this.organizationLeasingRepository = organizationLeasingRepository;
  }

  @Inject
  public void setDefaultParameterRepository(DefaultParameterRepository defaultParameterRepository){
    this.defaultParameterRepository = defaultParameterRepository;
  }

  @Override
  public DocumentInfoRepository getDocumentInfoRepository()
  {
    return this.documentInfoRepository;
  }

  @Override
  public DocumentRepository getDocumentRepository()
  {
    return this.documentRepository;
  }

  @Override
  public ErrorMessageRepository getErrorMessageRepository()
  {
    return this.errorMessageRepository;
  }

  @Override
  public ProcessRepository getProcessRepository()
  {
    return this.processRepository;
  }

  @Override
  public ProcessRequestRepository getProcessRequestRepository()
  {
    return this.processRequestRepository;
  }

  @Override
  public ProcessTypeRepository getProcessTypeRepository()
  {
    return this.processTypeRepository;
  }

  @Override
  public TaskFormRepository getTaskFormRepository()
  {
    return this.taskFormRepository;
  }

  @Override
  public VariableRepository getVariableRepository()
  {
    return this.variableRepository;
  }

  @Override
  public ProductRepository getProductRepository()
  {
    return this.productRepository;
  }

  @Override
  public CollateralProductRepository getCollateralProductRepository()
  {
    return this.collateralProductRepository;
  }

  @Override
  public OrganizationSalaryRepository getOrganizationSalaryRepository()
  {
    return this.organizationSalaryRepository;
  }

  @Override
  public OrganizationLeasingRepository getOrganizationLeasingRepository()
  {
    return this.organizationLeasingRepository;
  }

  @Override
  public FormRelationRepository getFormRelationRepository()
  {
    return this.formRelationRepository;
  }

  @Override
  public LoanContractRequestRepository getLoanContractRequestRepository()
  {
    return this.loanContractRequestRepository;
  }

  @Override
  public LoanContractParameterRepository getLoanContractParameterRepository()
  {
    return this.loanContractParameterRepository;
  }

  @Override
  public DefaultParameterRepository getDefaultParameterRepository()
  {
    return this.defaultParameterRepository;
  }
}
