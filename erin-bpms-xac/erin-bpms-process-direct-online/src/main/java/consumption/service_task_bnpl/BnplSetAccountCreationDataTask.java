package consumption.service_task_bnpl;

import java.util.Date;

import org.camunda.bpm.engine.delegate.BpmnError;
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
import static mn.erin.domain.bpm.BpmMessagesConstants.BNPL_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FIRST_PAYMENT_DATE;
import static mn.erin.domain.bpm.BpmModuleConstants.FREQUENCY;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.util.process.BpmUtils.getPlusDateDays;

public class BnplSetAccountCreationDataTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(BnplSetAccountCreationDataTask.class);
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public BnplSetAccountCreationDataTask(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    try
    {
      Object term = execution.getVariable(TERM);
      String termDays = null;
      if (null != term)
      {
        termDays = String.valueOf(term).substring(0, 2);
      }
      execution.setVariable(TERM, termDays);

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
      execution.setVariable("businessTypeReason", businessTypeReason);

      String frequency = getFrequency(String.valueOf(execution.getVariable(LOAN_PRODUCT)));
      execution.setVariable(FREQUENCY, frequency);

      Date date = new Date();
      Date firstPaymentDate = getPlusDateDays(date, 15);
      execution.setVariable(FIRST_PAYMENT_DATE, firstPaymentDate);

      LOGGER.info(BNPL_LOG + " Set account creation data before creation.");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      if (!execution.hasVariable(ERROR_CAUSE))
      {
        execution.setVariable(ERROR_CAUSE, e.getMessage());
      }
      throw new BpmnError("Account Creation", e.getMessage());
    }
  }

  private String getFrequency(String productCode) throws UseCaseException
  {
    GetProduct getProduct = new GetProduct(bpmsRepositoryRegistry.getProductRepository());
    UniqueProductInput input = new UniqueProductInput(productCode, "BNPL");
    Product product = getProduct.execute(input);
    if (null == product)
    {
      return null;
    }
    return String.valueOf(product.getFrequency());
  }
}

