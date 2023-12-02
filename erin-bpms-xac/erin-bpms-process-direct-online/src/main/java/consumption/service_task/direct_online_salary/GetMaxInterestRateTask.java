package consumption.service_task.direct_online_salary;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.usecase.product.GetProduct;
import mn.erin.domain.bpm.usecase.product.UniqueProductInput;

import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY;

public class GetMaxInterestRateTask implements JavaDelegate
{
  private final ProductRepository productRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(GetMaxInterestRateTask.class);

  public GetMaxInterestRateTask(ProductRepository productRepository)
  {
    this.productRepository = productRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    String processRequestId = (String) execution.getVariable(PROCESS_REQUEST_ID);
    String loanProduct = (String) execution.getVariable(LOAN_PRODUCT);
    String productCode = loanProduct.substring(0, 4);

    LOGGER.info(ONLINE_SALARY_LOG_HASH + "Getting INTEREST RATE from database, request id [{}],  product code {}", processRequestId, productCode);
    GetProduct getProduct = new GetProduct(productRepository);
    Product productInfo = getProduct.execute(new UniqueProductInput(productCode, PRODUCT_APPLICATION_CATEGORY_ONLINE_SALARY));
    execution.setVariable(INTEREST_RATE, productInfo.getRate());
  }
}

