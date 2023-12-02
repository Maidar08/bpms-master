package mn.erin.domain.bpm.usecase.directOnline;

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
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.usecase.direct_online.GetRequestStatus;

/**
 * @author Oyungerel Chuluunsukh
 **/
public class GetRequestStatusTest
{
  public static final String CIF_NUMBER = "10003";
  private DefaultParameterRepository defaultParameterRepository;
  private BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private BpmsServiceRegistry bpmsServiceRegistry;
  private GetRequestStatus useCase;
  private ProcessRequestRepository processRequestRepository;
  private CoreBankingService coreBankingService;
  private ProductRepository productRepository;
  private ProcessRepository processRepository;
  private Environment environment;

  @Before
  public void setUp()
  {
    defaultParameterRepository = Mockito.mock(DefaultParameterRepository.class);
    bpmsRepositoryRegistry = Mockito.mock(BpmsRepositoryRegistry.class);
    bpmsServiceRegistry = Mockito.mock(BpmsServiceRegistry.class);
    processRequestRepository = Mockito.mock(ProcessRequestRepository.class);
    coreBankingService = Mockito.mock(CoreBankingService.class);
    productRepository = Mockito.mock(ProductRepository.class);
    processRepository = Mockito.mock(ProcessRepository.class);
    environment = Mockito.mock(Environment.class);
    useCase = new GetRequestStatus(defaultParameterRepository, bpmsServiceRegistry, bpmsRepositoryRegistry, environment);
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository()).thenReturn(processRequestRepository);
    Mockito.when(bpmsServiceRegistry.getCoreBankingService()).thenReturn(coreBankingService);
    Mockito.when(bpmsRepositoryRegistry.getProductRepository()).thenReturn(productRepository);
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository()).thenReturn(processRepository);
  }


  @Test(expected = NullPointerException.class)
  public void when_default_parameter_Repository_is_null()
  {
    new GetRequestStatus(null, bpmsServiceRegistry, bpmsRepositoryRegistry, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_bpms_Service_Registry_is_null()
  {
    new GetRequestStatus(null, null, bpmsRepositoryRegistry, environment);
  }

  @Test(expected = NullPointerException.class)
  public void when_bpms_repository_registry_is_null()
  {
    new GetRequestStatus(null, bpmsServiceRegistry, null, environment);
  }
 //TODO: must to fix all tests after
  @Ignore
  @Test(expected = UseCaseException.class)
  public void when_process_request_repository_throws_exception() throws UseCaseException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString())).thenThrow(BpmRepositoryException.class);
    useCase.execute(CIF_NUMBER);
  }

  @Ignore
  @Test
  public void when_process_exist_type_not_null() throws UseCaseException, BpmRepositoryException, BpmServiceException
  {
    mockRegisterNumber();
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.COMPLETED));
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Map<String, Object> result = useCase.execute("");
    Assert.assertNotNull(result.get("ProcessTypeId"));
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
    Mockito.when(bpmsRepositoryRegistry.getProductRepository().findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());

    Map<String, Object> result = useCase.execute(CIF_NUMBER);
    Assert.assertEquals("CONFIRMED", result.get("Status"));
    Assert.assertNotNull(result.get("MaxAmount"));
    Assert.assertNotNull(result.get("Interest"));
    Assert.assertNotNull(result.get("Charge"));
    Assert.assertNotNull(result.get("Term"));
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
    Mockito.when(bpmsRepositoryRegistry.getProcessTypeRepository().findByProcessTypeCategory("value")).thenReturn(getDummyProcessType());

    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getGivenTimePassedProcessRequests("val", "val", null, null)).thenReturn(Collections.emptyList());
    Map<String, Object> result = useCase.execute("");
    Assert.assertEquals("NEW", result.get("Status"));
    Assert.assertNull(result.get("MaxAmount"));
    Assert.assertNull(result.get("Interest"));
    Assert.assertNull(result.get("Charge"));
    Assert.assertNull(result.get("Term"));
  }

  @Ignore
  @Test
  public void when_status_rejected() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.ORG_REJECTED));
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Map<String, Object> result = useCase.execute("");
    Assert.assertEquals("ORG_REJECTED", result.get("Status"));
  }

  @Ignore
  @Test
  public void when_status_disbursed() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.DISBURSED));
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Map<String, Object> result = useCase.execute("");
    Assert.assertEquals("DISBURSED", result.get("Status"));
  }

  @Ignore
  @Test
  public void when_has_account_list() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    mockRegisterNumber();
    List<XacAccount> xacAccounts = new ArrayList<>();
    xacAccounts.add(new XacAccount(new AccountId("id"), "Casa", "", "", true, "", false,
        "", "name", "1000000.0", "mine", "their", "EB50 - some zeel", "bgo",
        "money"));
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.CONFIRMED));
    Mockito.when(bpmsRepositoryRegistry.getProductRepository().findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(xacAccounts);
    Map<String, Object> result = useCase.execute(CIF_NUMBER);
    Assert.assertEquals("CONFIRMED", result.get("Status"));
    Assert.assertNotNull(result.get("PreviousLoanRequests"));
  }

  @Ignore
  @Test
  public void when_account_list_not_correct_product() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    mockRegisterNumber();
    List<XacAccount> xacAccounts = new ArrayList<>();
    xacAccounts.add(new XacAccount(new AccountId("id"), "Casa", "", "", true, "", false,
        "", "name", "1000000.0", "mine", "their", "EE-81 Иргэний зээл", "bgo",
        "money"));
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getDirectOnlineProcessRequests(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.CONFIRMED));
    Mockito.when(bpmsRepositoryRegistry.getProductRepository().findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(xacAccounts);
    Map<String, Object> result = useCase.execute(CIF_NUMBER);
    Assert.assertEquals("CONFIRMED", result.get("Status"));
    Assert.assertEquals(new BigDecimal(0), result.get("PreviousLoanRequests"));
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
    return terms;
  }

  private Map<String, Object> getCharge()
  {
    Map<String, Object> charge = new HashMap<>();
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
   private List<ProcessType> getDummyProcessType(){
    List<ProcessType> processTypes = new ArrayList<>();
    processTypes.add(new ProcessType(ProcessTypeId.valueOf("id"), "key", ProcessDefinitionType.CASE));
    return processTypes;
   }
}
