package mn.erin.domain.bpm.usecase.bnpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.aim.model.user.UserId;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.AccountId;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.process.ProcessDefinitionType;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;
import mn.erin.domain.bpm.model.process.ProcessRequestState;
import mn.erin.domain.bpm.model.process.ProcessType;
import mn.erin.domain.bpm.model.process.ProcessTypeId;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.ProcessRepository;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.repository.ProcessTypeRepository;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;

import static mn.erin.domain.bpm.BpmModuleConstants.DIRECT_ONLINE_PROCESS_TYPE_CATEGORY;

public class GetBnplRequestStatusTest
{
  public static final String CIF_NUMBER = "00068933";
  private DefaultParameterRepository defaultParameterRepository;
  private BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private BpmsServiceRegistry bpmsServiceRegistry;
  private GetBnplRequestStatus useCase;
  private ProcessRequestRepository processRequestRepository;
  private CoreBankingService coreBankingService;
  private ProductRepository productRepository;
  private ProcessRepository processRepository;
  private Environment environment;
  private GetProcessTypesByCategory getProcessTypesByCategory;
  private ProcessTypeRepository processTypeRepository;
  private CaseService caseService;
  private RuntimeService runtimeService;

  private NewCoreBankingService newCoreBankingService;

  @Before
  public void setUp() throws UseCaseException, BpmRepositoryException
  {
    defaultParameterRepository = Mockito.mock(DefaultParameterRepository.class);
    bpmsRepositoryRegistry = Mockito.mock(BpmsRepositoryRegistry.class);
    bpmsServiceRegistry = Mockito.mock(BpmsServiceRegistry.class);
    processRequestRepository = Mockito.mock(ProcessRequestRepository.class);
    environment = Mockito.mock(Environment.class);
    coreBankingService = Mockito.mock(CoreBankingService.class);
    productRepository = Mockito.mock(ProductRepository.class);
    processRepository = Mockito.mock(ProcessRepository.class);
    caseService = Mockito.mock(CaseService.class);
    runtimeService = Mockito.mock(RuntimeService.class);
    processTypeRepository = Mockito.mock(ProcessTypeRepository.class);
    getProcessTypesByCategory = Mockito.mock(GetProcessTypesByCategory.class);
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    useCase = new GetBnplRequestStatus(bpmsServiceRegistry, bpmsRepositoryRegistry, environment, newCoreBankingService);
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository()).thenReturn(processRequestRepository);
    Mockito.when(bpmsServiceRegistry.getCoreBankingService()).thenReturn(coreBankingService);
    Mockito.when(bpmsRepositoryRegistry.getProductRepository()).thenReturn(productRepository);
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository()).thenReturn(processRepository);
    Mockito.when(bpmsServiceRegistry.getCaseService()).thenReturn(caseService);
    Mockito.when(bpmsServiceRegistry.getRuntimeService()).thenReturn(runtimeService);
    Mockito.when(bpmsServiceRegistry.getNewCoreBankingService()).thenReturn(newCoreBankingService);
    Mockito.when(bpmsRepositoryRegistry.getProcessTypeRepository()).thenReturn(processTypeRepository);
    Mockito.when(environment.getProperty(DIRECT_ONLINE_PROCESS_TYPE_CATEGORY)).thenReturn("DIRECT_ONLINE");
    Mockito.when(environment.getProperty("laa.type")).thenReturn("LAA");
  }

  @Test
  public void when_newCoreBankingService_is_null()
  {
    new GetBnplRequestStatus(bpmsServiceRegistry, bpmsRepositoryRegistry, environment, null);
  }

  @Test
  public void when_bpms_Service_Registry_is_null()
  {
    new GetBnplRequestStatus(null, bpmsRepositoryRegistry, environment, newCoreBankingService);
  }

  @Test
  public void when_bpms_repository_registry_is_null()
  {
    new GetBnplRequestStatus(bpmsServiceRegistry, null, environment, newCoreBankingService);
  }

  @Test(expected = UseCaseException.class)
  public void when_process_request_repository_throws_exception() throws UseCaseException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString())).thenThrow(BpmRepositoryException.class);
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    useCase.execute(CIF_NUMBER);
  }

  @Test
  public void when_process_exist_type_not_null() throws UseCaseException, BpmRepositoryException
  {
    mockRegisterNumber();
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.COMPLETED));
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    useCase.execute("");
    Assert.assertNotNull(this.getProcessInstance());
  }

  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_status_default_parameter_throws_exception() throws UseCaseException, BpmRepositoryException
  {
    mockRegisterNumber();
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.CONFIRMED));
    Mockito.when(bpmsRepositoryRegistry.getProductRepository().findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString()))
        .thenThrow(BpmRepositoryException.class);
    useCase.execute(CIF_NUMBER);
  }

  @Ignore
  @Test
  public void when_status_confirmed() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    mockRegisterNumber();
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.CONFIRMED));
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getProcessInstance());
    Mockito.when(bpmsRepositoryRegistry.getProductRepository().findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);

    Map<String, Object> result = useCase.execute(CIF_NUMBER);
    Assert.assertEquals("CONFIRMED", result.get("Status"));
    Assert.assertNotNull(result.get("processTypeId"));
    Assert.assertNotNull(result.get("expireDate"));
  }

  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_core_banking_service_throws_exception() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    mockRegisterNumber();
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.CONFIRMED));
    Mockito.when(bpmsRepositoryRegistry.getProductRepository().findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());

    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenThrow(BpmServiceException.class);
    useCase.execute(CIF_NUMBER);
  }

  @Ignore
  @Test
  public void when_status_new() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString())).thenReturn(getDummyProcessList(ProcessRequestState.NEW));
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    Map<String, Object> result = useCase.execute("00068933");
    Assert.assertEquals("NEW", result.get("Status"));
    Assert.assertNull(result.get("MaxAmount"));
    Assert.assertNull(result.get("Interest"));
    Assert.assertNull(result.get("Charge"));
    Assert.assertNull(result.get("Term"));
  }

  @Test
  public void when_status_rejected() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.ORG_REJECTED));
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    Map<String, Object> result = useCase.execute("");
    Assert.assertEquals("ORG_REJECTED", result.get("Status"));
  }

  @Ignore
  @Test
  public void when_status_transaction_failed() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.TRANSACTION_FAILED));
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    Map<String, Object> result = useCase.execute("");
    Assert.assertEquals("TRANSACTION_FAILED", result.get("Status"));
  }

  @Test
  public void when_status_overdue() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    mockRegisterNumber();
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.OVERDUE));
    Mockito.when(bpmsRepositoryRegistry.getProductRepository().findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    //    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());
    Mockito.when(bpmsServiceRegistry.getRuntimeService().getVariableById(Mockito.anyString(), Mockito.anyString()))
        .thenReturn("1000");
    Mockito.when(newCoreBankingService.getAccountsList(Mockito.anyMap()))
        .thenReturn(getDummyAccountList());
    Mockito.when(newCoreBankingService.findCustomerByCifNumber(Mockito.anyMap()))
        .thenReturn(getDummyCustomer());
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);

    Map<String, Object> result = useCase.execute(CIF_NUMBER);
    Assert.assertEquals(ProcessRequestState.OVERDUE, result.get("Status"));
    Assert.assertNotNull(result.get("accounts"));
  }

  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_product_repo_throws_exception() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    mockRegisterNumber();
    List<XacAccount> xacAccounts = new ArrayList<>();
    xacAccounts.add(new XacAccount(new AccountId("id"), "Casa", "", "", true, "", false,
        "", "name", "1000000.0", "mine", "their", "EE-81 Иргэний зээл", "bgo",
        "money"));
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.CONFIRMED));
    Mockito.when(bpmsRepositoryRegistry.getProductRepository().findByAppCategory(Mockito.anyString())).thenThrow(BpmRepositoryException.class);
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(xacAccounts);
    useCase.execute(CIF_NUMBER);
  }

  private void mockRegisterNumber()
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getParameterByName(Mockito.anyString(), Mockito.anyString())).thenReturn("УУ96121988");
  }

  private Collection<ProcessRequest> getDummyProcessList(ProcessRequestState state)
  {
    List<ProcessRequest> processRequestList = new ArrayList<>();

    ProcessRequestId processRequestId = new ProcessRequestId("1");
    ProcessTypeId processTypeId = new ProcessTypeId("Process Type 1");
    GroupId groupId = new GroupId("Erin Group");
    String requestedUserId = "Requested User";
    LocalDateTime createdTime = LocalDateTime.now();
    UserId assignedUserId = new UserId("User 1");
    ProcessRequest processRequest = new ProcessRequest(processRequestId, processTypeId, groupId, requestedUserId, createdTime, state, new HashMap<>());
    processRequest.setProcessInstanceId("instance");
    processRequest.setAssignedUserId(assignedUserId);
    processRequestList.add(processRequest);
    return processRequestList;
  }

  private Map<String, Object> getDummyTerms()
  {
    Map<String, Object> terms = new HashMap<>();
    terms.put("grantLoanAmountString", "100000.3");
    terms.put("interestRate", "100");
    terms.put("term", "termo");
    terms.put("branch", "108");
    terms.put("danRegister", "1");
    return terms;
  }

  private Map<String, Object> getCharge()
  {
    Map<String, Object> loanTerms = new HashMap<>();
    loanTerms.put("Charge", "nani");
    loanTerms.put("Term", "nani");
    return loanTerms;
  }

  private List<Product> getDummyProducts()
  {
    List<Product> products = new ArrayList<>();
    products.add(new Product(new ProductId("EB50"), "ONLINE_SALARY", "KAT", "",
        "", new BigDecimal(0), false, false));
    products.add(new Product(new ProductId("EG50"), "SOME_OTHER", "KITTY", "",
        "", new BigDecimal(0), false, false));
    return products;
  }

  private List<XacAccount> getDummyAccountList()
  {
    List<XacAccount> xacAccounts = new ArrayList<>();
    XacAccount xacAccount = new XacAccount(AccountId.valueOf("id"), "prodId", "br", "no", true, "class", false, "clss", "name", "100000", "owner", "pg", "pn",
        "no", "mnt");
    xacAccount.setSchemaType("LAA");
    xacAccounts.add(xacAccount);
    return xacAccounts;
  }

  private Map<String, Object> getProcessInstance()
  {
    Map<String, Object> map = new HashMap<>();
    return map;
  }

  private GetProcessTypesByCategoryOutput getDummyOutput() throws UseCaseException
  {
    ProcessTypeRepository processTypeRepository = bpmsRepositoryRegistry.getProcessTypeRepository();
    GetProcessTypesByCategory getProcessTypesByCategory = new GetProcessTypesByCategory(processTypeRepository);
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(environment.getProperty(DIRECT_ONLINE_PROCESS_TYPE_CATEGORY));
    return output;
  }

  private List<ProcessType> getDummyProcessTypes()
  {
    List<ProcessType> processTypes = new ArrayList<>();
    processTypes.add(new ProcessType(ProcessTypeId.valueOf("id"), "key", ProcessDefinitionType.PROCESS));
    return processTypes;
  }

  private Map<String, Object> getDummyCustomer()
  {
    Map<String, Object> customerMap = new HashMap<>();
    customerMap.put("registerNumber", "УУ96121988");
    return customerMap;
  }
}
