package mn.erin.domain.bpm.usecase.online_leasing;

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
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.RuntimeService;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfo;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfoInput;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfoOutput;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategory;
import mn.erin.domain.bpm.usecase.process.GetProcessTypesByCategoryOutput;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_CONFIRM_TIME_RANGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_DELETE_TIME_RANGE;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

public class GetOnlineLeasingRequestStatusTest
{
  public static final String CIF_NUMBER = "00068933";
  private DefaultParameterRepository defaultParameterRepository;
  private BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private BpmsServiceRegistry bpmsServiceRegistry;
  private GetOnlineLeasingRequestStatus useCase;
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
  private DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private GetLoanInfo getLoanInfo;

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
    directOnlineCoreBankingService = Mockito.mock(DirectOnlineCoreBankingService.class);
    useCase = new GetOnlineLeasingRequestStatus(bpmsServiceRegistry, bpmsRepositoryRegistry, environment);
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository()).thenReturn(processRequestRepository);
    Mockito.when(bpmsServiceRegistry.getCoreBankingService()).thenReturn(coreBankingService);
    Mockito.when(bpmsRepositoryRegistry.getProductRepository()).thenReturn(productRepository);
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository()).thenReturn(processRepository);
    Mockito.when(bpmsServiceRegistry.getCaseService()).thenReturn(caseService);
    Mockito.when(bpmsServiceRegistry.getRuntimeService()).thenReturn(runtimeService);
    Mockito.when(bpmsRepositoryRegistry.getProcessTypeRepository()).thenReturn(processTypeRepository);
    Mockito.when(bpmsServiceRegistry.getNewCoreBankingService()).thenReturn(newCoreBankingService);
    Mockito.when(bpmsServiceRegistry.getDirectOnlineCoreBankingService()).thenReturn(directOnlineCoreBankingService);
    Mockito.when(environment.getProperty(ONLINE_LEASING_PROCESS_TYPE_CATEGORY)).thenReturn("ONLINE_LEASING");
    Mockito.when(environment.getProperty(ONLINE_LEASING_CONFIRM_TIME_RANGE)).thenReturn("24");
    Mockito.when(environment.getProperty(ONLINE_LEASING_DELETE_TIME_RANGE)).thenReturn("24");
    Mockito.when(environment.getProperty("laa.type")).thenReturn("LAA");
    Mockito.when(environment.getProperty("norm.classification")).thenReturn("NORM");
  }

  @Test
  public void when_bpms_Service_Registry_is_null()
  {
    new GetOnlineLeasingRequestStatus(null, bpmsRepositoryRegistry, environment);
  }

  @Test
  public void when_bpms_repository_registry_is_null()
  {
    new GetOnlineLeasingRequestStatus(bpmsServiceRegistry, null, environment);
  }

  @Test(expected = UseCaseException.class)
  public void when_process_request_repository_throws_exception() throws UseCaseException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getProcessRequestsOnlineLeasing(Mockito.anyString(),
        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString(), Mockito.anyString())).thenThrow(BpmRepositoryException.class);
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    useCase.execute(setInput());
  }

  @Test(expected = BpmRepositoryException.class)
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
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());
    useCase.execute(setInput());
  }

  @Test
  public void when_status_confirmed() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    mockRegisterNumber();
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getProcessRequestsOnlineLeasing(Mockito.anyString(),
            Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.CONFIRMED));
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getProcessInstance());
    Mockito.when(bpmsRepositoryRegistry.getProductRepository().findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    Mockito.when(bpmsServiceRegistry.getRuntimeService()
        .getVariableById(Mockito.anyString(), Mockito.anyString())).thenReturn(getDummyLoanAccountList());
    Mockito.when(bpmsServiceRegistry.getRuntimeService().getVariableById(Mockito.anyString(), Mockito.anyString()))
        .thenReturn("1000");

    Map<String, Object> result = useCase.execute(setInput());
    Assert.assertEquals("CONFIRMED", result.get("Status"));
//    Assert.assertNotNull(result.get("processTypeId"));
  }


  @Test
  public void when_status_new() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRepository().getProcessParametersByInstanceId(Mockito.anyString())).thenReturn(getDummyTerms());
    Mockito.when(defaultParameterRepository.getDefaultParametersByProcessType(Mockito.anyString(), Mockito.anyString())).thenReturn(getCharge());
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    Map<String, Object> result = useCase.execute(setInput());
    Assert.assertEquals("NEW", result.get("Status"));
  }

  @Test
  public void when_status_rejected() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getProcessRequestsOnlineLeasing(Mockito.anyString(),
            Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.ORG_REJECTED));
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    Map<String, Object> result = useCase.execute(setInput());
    Assert.assertEquals("ORG_REJECTED", result.get("Status"));
  }

  @Test
  public void when_status_transaction_failed() throws UseCaseException, BpmServiceException, BpmRepositoryException
  {
    Mockito.when(bpmsRepositoryRegistry.getProcessRequestRepository().getProcessRequestsOnlineLeasing(Mockito.anyString(),
            Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(getDummyProcessList(ProcessRequestState.TRANSACTION_FAILED));
    Mockito.when(bpmsServiceRegistry.getCoreBankingService().getAccountsListFC(Mockito.anyString(), Mockito.anyString())).thenReturn(Collections.emptyList());
    Mockito.when(processTypeRepository.findByProcessTypeCategory(Mockito.anyString())).thenReturn(getDummyProcessTypes());
    GetProcessTypesByCategoryOutput output = getDummyOutput();
    Mockito.when(getProcessTypesByCategory.execute(Mockito.anyString())).thenReturn(output);
    Map<String, Object> result = useCase.execute(setInput());
    Assert.assertEquals("TRANSACTION_FAILED", result.get("Status"));
  }

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
    getLoanInfo = new GetLoanInfo(newCoreBankingService, directOnlineCoreBankingService, productRepository);
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PRODUCT_CODE, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PHONE_NUMBER, "phoneNumber");
    input.put(REGISTER_NUMBER, REGISTER_NUMBER);
    input.put(CIF_NUMBER, CIF_NUMBER);
    input.put(PRODUCT_CATEGORY, PRODUCT_CATEGORY);
    Mockito.when(getLoanInfo.execute(input)).thenReturn(getLoanInfoOutput());
    useCase.execute(setInput());
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
    Mockito.when(directOnlineCoreBankingService.getAccountLoanInfo(Mockito.anyMap())).thenReturn(getDummyAccountInfo());
    Map<String, Object> result = useCase.execute(setInput());
    Assert.assertEquals(ProcessRequestState.OVERDUE, result.get("Status"));
    Assert.assertNotNull(result.get("accounts"));
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
    terms.put("Interest", "25");
    terms.put("term", "termo");
    terms.put("branch", "108");
    terms.put("danRegister", "1");
    terms.put("grantMinimumAmount", "50000");
    terms.put("PreviousLoanRequests", "[{\"id\": \"file\"}]");
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
    products.add(new Product(new ProductId("EB81"), "OnlineLeasing1", "Үндсэн төлбөр тэнцүү", "Онлайн лизинг",
        "", new BigDecimal(0), false, false));
    return products;
  }

  private List<XacAccount> getDummyAccountList()
  {
    List<XacAccount> xacAccounts = new ArrayList<>();
    XacAccount xacAccount = new XacAccount(AccountId.valueOf("id"), "prodId", "br", "no", true, "class", false, "clss", "name", "100000", "owner", "pg", "pn",
        "no", "mnt");
    xacAccount.setSchemaType("LAA");
    XacAccount xacAccount2 = new XacAccount(AccountId.valueOf("id"), "EB81", "br", "no", true, "class", false, "clss", "name", "100000", "owner", "pg", "pn",
        "no", "mnt");
    xacAccount2.setSchemaType("LAA");
    xacAccounts.add(xacAccount);
    xacAccounts.add(xacAccount2);
    return xacAccounts;
  }

  private Map<String, Map<String, Object>> getDummyLoanAccountList()
  {
    Map<String, Map<String, Object>> dummyList = new HashMap<>();
    Map<String, Object> dummy = new HashMap<>();
    Object accountId = "1970083013";
    dummy.put("AccountID", accountId);
    Object balance = "10000";
    dummy.put("Balance", balance);
    dummy.put("ClearBalance", balance);
    Object closingLoanAmount = "10000";
    dummy.put("ClosingLoanAmount", closingLoanAmount);
    Object productID = "EB81";
    dummy.put("ProductID", productID);
    dummyList.put("1970083013", dummy);
    return dummyList;
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
    GetProcessTypesByCategoryOutput output = getProcessTypesByCategory.execute(environment.getProperty(ONLINE_LEASING_PROCESS_TYPE_CATEGORY));
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
  private GetLoanInfoOutput getLoanInfoOutput (){
    return new GetLoanInfoOutput(true, getDummyLoanAccountList(), new BigDecimal(10000), new BigDecimal(10000));
  }
  private Map<String, Object> getDummyAccountInfo(){

    Map<String, Object> dummyInfo = new HashMap<>();
    dummyInfo.put("amount", new BigDecimal(10000));
    dummyInfo.put("XAC_CLOSING_LOAN_AMOUNT", new BigDecimal(10000));
    dummyInfo.put("ClosingLoanAmount", new BigDecimal(5000));
    dummyInfo.put("ClearBalance", new BigDecimal(5000));
    return dummyInfo;
  }
  private Map<String, Object> setInput(){
    Map<String, Object> dummyInfo = new HashMap<>();
    dummyInfo.put(CIF_NUMBER, "12345678");
    dummyInfo.put(PHONE_NUMBER, "12345678");
    dummyInfo.put(PRODUCT_CATEGORY, ONLINE_LEASING_PROCESS_TYPE_ID);
    dummyInfo.put(PRODUCT_CODE, ONLINE_LEASING_PROCESS_TYPE_ID);
    Map<String, String> unionFields= new HashMap<>();
    unionFields.put(PRODUCT_CATEGORY, ONLINE_LEASING_PROCESS_TYPE_ID);
    unionFields.put(PRODUCT_CODE, ONLINE_LEASING_PROCESS_TYPE_ID);
    dummyInfo.put("unionFields",unionFields);

    return dummyInfo;
  }
}
