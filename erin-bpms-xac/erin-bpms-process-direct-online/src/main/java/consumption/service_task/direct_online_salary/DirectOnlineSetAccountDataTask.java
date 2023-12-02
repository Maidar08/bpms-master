package consumption.service_task.direct_online_salary;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.usecase.product.GetProduct;
import mn.erin.domain.bpm.usecase.product.UniqueProductInput;

import static consumption.constant.CamundaVariableConstants.DIGITAL_BANK;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_SALARY_LOG_HASH;
import static mn.erin.domain.bpm.BpmModuleConstants.FREQUENCY;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.NUMBER_OF_PAYMENTS;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;

/**
 * @author Lkhagvadorj.A
 **/

public class DirectOnlineSetAccountDataTask implements JavaDelegate
{
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;
  private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnlineSetAccountDataTask.class);

  public DirectOnlineSetAccountDataTask(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    Object term = execution.getVariable(TERM);
    execution.setVariable(NUMBER_OF_PAYMENTS, term);

    Object interestRate = execution.getVariable(INTEREST_RATE);
    execution.setVariable("interest_rate", interestRate);

    String requestId = String.valueOf(execution.getVariable(PROCESS_REQUEST_ID));
    String sanctionedBy = DIGITAL_BANK + "-" + requestId;
    execution.setVariable("sanctionedBy", sanctionedBy);

    Object attentiveLoan = execution.getVariable("attentiveLoan");
    execution.setVariable("attentiveLoan", attentiveLoan);

    Object loanCycle = execution.getVariable("loanCycle");
    execution.setVariable("loanCycle", loanCycle);

    Object insuranceCompanyInfo = execution.getVariable("insuranceCompanyInfo");
    execution.setVariable("insuranceCompanyInfo", insuranceCompanyInfo);

    Object businessTypeReason = execution.getVariable("businessTypeReason");
    execution.setVariable("businessTypeReason",businessTypeReason);

    String frequency = getFrequency(String.valueOf(execution.getVariable(LOAN_PRODUCT)));
    execution.setVariable(FREQUENCY, frequency);

    LOGGER.info(ONLINE_SALARY_LOG_HASH + " Set account creation data before creation.");

  }

  private String getFrequency(String productCode) throws UseCaseException
  {
    GetProduct getProduct = new GetProduct(bpmsRepositoryRegistry.getProductRepository());
    UniqueProductInput input = new UniqueProductInput(productCode, "ONLINE_SALARY");
    Product product = getProduct.execute(input);
    if (null == product)
    {
      return null;
    }

    return String.valueOf(product.getFrequency());
  }
}
