package consumption.service_task_bnpl;

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
import static mn.erin.domain.bpm.BpmMessagesConstants.INSTANT_LOAN_LOG;
import static mn.erin.domain.bpm.BpmMessagesConstants.ONLINE_LEASING_LOG;
import static mn.erin.domain.bpm.BpmModuleConstants.ACTION_TYPE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERROR_CAUSE;
import static mn.erin.domain.bpm.BpmModuleConstants.FREQUENCY;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.INTEREST_RATE;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;
import static mn.erin.domain.bpm.BpmModuleConstants.TERM;
import static mn.erin.domain.bpm.BpmModuleConstants.TRACK_NUMBER;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.INSTANT_LOAN;
import static mn.erin.domain.bpm.model.process.ParameterEntityType.enumToString;
import static mn.erin.domain.bpm.util.process.BpmUtils.getValidString;

public class InstantSetAccountCreationDataTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(InstantSetAccountCreationDataTask.class);
  private final BpmsRepositoryRegistry bpmsRepositoryRegistry;

  public InstantSetAccountCreationDataTask(BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    this.bpmsRepositoryRegistry = bpmsRepositoryRegistry;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    try
    {
      Object term = execution.getVariable(TERM);
      execution.setVariable(TERM, term);

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

      String frequency = getFrequency(String.valueOf(execution.getVariable(LOAN_PRODUCT)), execution);
      execution.setVariable(FREQUENCY, frequency);

      String processTypeId = String.valueOf(execution.getVariable(PROCESS_TYPE_ID));
      String trackNumber = String.valueOf(execution.getVariable(TRACK_NUMBER));

      if (processTypeId.equals(ONLINE_LEASING_PROCESS_TYPE_ID))
      {
        LOGGER.info("{} Set account creation data before creation. Tracknumber : {}", ONLINE_LEASING_LOG, trackNumber + ".");
      }
      else
      {
        LOGGER.info("{} Set account creation data before creation. ActionType : {}", INSTANT_LOAN_LOG, execution.getVariable(ACTION_TYPE) + ".");
      }
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

  private String getFrequency(String productCode, DelegateExecution execution) throws UseCaseException
  {
    String processTypeId = getValidString(execution.getVariable(PROCESS_TYPE_ID));
    String productCategory = getValidString(execution.getVariable(PRODUCT_CATEGORY));
    if (processTypeId.equals(INSTANT_LOAN_PROCESS_TYPE_ID))
    {
      productCategory = enumToString(INSTANT_LOAN);
    }
    GetProduct getProduct = new GetProduct(bpmsRepositoryRegistry.getProductRepository());
    UniqueProductInput input = new UniqueProductInput(productCode, productCategory);
    Product product = getProduct.execute(input);
    if (null == product)
    {
      return null;
    }

    return String.valueOf(product.getFrequency());
  }
}
