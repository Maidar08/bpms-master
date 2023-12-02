package mn.erin.domain.bpm.usecase.directOnline;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.account.AccountId;
import mn.erin.domain.bpm.model.account.XacAccount;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfo;
import mn.erin.domain.bpm.usecase.direct_online.GetLoanInfoOutput;

import static mn.erin.domain.bpm.BpmModuleConstants.CIF_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CODE;
import static mn.erin.domain.bpm.BpmModuleConstants.REGISTER_NUMBER;

public class GetLoanInfoTest
{
  private NewCoreBankingService newCoreBankingService;
  private DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private ProductRepository productRepository;
  private GetLoanInfo usecase;


  @Before
  public void setUp()
  {
    newCoreBankingService = Mockito.mock(NewCoreBankingService.class);
    directOnlineCoreBankingService = Mockito.mock(DirectOnlineCoreBankingService.class);
    productRepository = Mockito.mock(ProductRepository.class);
    usecase = new GetLoanInfo(newCoreBankingService, directOnlineCoreBankingService, productRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_new_core_banking_service_is_null()
  {
    new GetLoanInfo(null, directOnlineCoreBankingService, productRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_direct_online_new_core_banking_service_is_null()
  {
    new GetLoanInfo(newCoreBankingService, null, productRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_product_repository_is_null()
  {
    new GetLoanInfo(newCoreBankingService, directOnlineCoreBankingService, null);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null_throws_exception() throws UseCaseException, BpmRepositoryException{
   usecase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throw_bpm_reposiroty_exception() throws UseCaseException, BpmRepositoryException{
    Mockito.when(productRepository.findByAppCategory(Mockito.anyString())).thenThrow(BpmRepositoryException.class);
    usecase.execute(null);
  }

  @Test
  public void when_response_is_empty() throws BpmServiceException, BpmRepositoryException, UseCaseException
  {
//    GetLoanInfoInput input = new GetLoanInfoInput("reg", "123", "onlineSalary");
    Mockito.when(newCoreBankingService.getAccountsList(Mockito.anyMap())).thenReturn(getDummyAccountList());
    Mockito.when(productRepository.findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(directOnlineCoreBankingService.getAccountLoanInfo(Mockito.anyMap())).thenReturn(Collections.emptyMap());
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PRODUCT_CODE, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PHONE_NUMBER, "phoneNumber");
    input.put(REGISTER_NUMBER, "phoneNumber");
    input.put(CIF_NUMBER, "phoneNumber");
    GetLoanInfoOutput output = usecase.execute(input);
    Assert.assertFalse(output.isHasActiveLoanAccount());
    Assert.assertEquals(output.getMappedAccount(), Collections.emptyMap());
    Assert.assertEquals(output.getTotalBalance(), new BigDecimal(0));
    Assert.assertEquals(output.getTotalClosingAmount(), new BigDecimal(0));
  }

  @Test
  public void when_has_no_active_loan() throws BpmServiceException, BpmRepositoryException, UseCaseException
  {
//    GetLoanInfoInput input = new GetLoanInfoInput("reg", "123", "onlineSalary");
    Mockito.when(newCoreBankingService.getAccountsList(Mockito.anyMap())).thenReturn(getDummyAccountList());
    Mockito.when(productRepository.findByAppCategory(Mockito.anyString())).thenReturn(getDummyProducts());
    Mockito.when(directOnlineCoreBankingService.getAccountLoanInfo(Mockito.anyMap())).thenReturn(getDummyAccountInfo());
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PRODUCT_CODE, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PHONE_NUMBER, "phoneNumber");
    input.put(REGISTER_NUMBER, "phoneNumber");
    input.put(CIF_NUMBER, "phoneNumber");
    GetLoanInfoOutput output = usecase.execute(input);
    Assert.assertFalse(output.isHasActiveLoanAccount());
    Assert.assertEquals(output.getMappedAccount().size(), 0);
    Assert.assertEquals(output.getTotalBalance(), new BigDecimal(0));
    Assert.assertEquals(output.getTotalClosingAmount(), new BigDecimal(0));
  }
  private List<XacAccount> getDummyAccountList(){
    List<XacAccount>  xacAccounts = new ArrayList<>();
    XacAccount xacAccount = new XacAccount(AccountId.valueOf("id"), "prodId", "br", "no", true, "class", false, "clss", "name", "100000", "owner", "pg", "pn", "no", "mnt");
    xacAccounts.add(xacAccount);
    return xacAccounts;
  }

  private List<Product> getDummyProducts(){
    Product product = new Product(ProductId.valueOf("prodId"), "cate", "desc", "pdesc", "type", new BigDecimal(1), true, true);
     List<Product> products = new ArrayList<>();
     products.add(product);
     return products;
  }
   private Map<String, Object> getDummyAccountInfo(){

    Map<String, Object> dummyInfo = new HashMap<>();
    dummyInfo.put("amount", new BigDecimal(10000));
    dummyInfo.put("amount2", new BigDecimal(5000));
    return dummyInfo;
   }

}
