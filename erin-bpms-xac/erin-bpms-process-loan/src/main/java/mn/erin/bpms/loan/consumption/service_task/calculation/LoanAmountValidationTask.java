package mn.erin.bpms.loan.consumption.service_task.calculation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.common.utils.NumberUtils;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.usecase.product.GetProduct;
import mn.erin.domain.bpm.usecase.product.UniqueProductInput;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_SEND_LOAN_REQUEST_DECISION;
import static mn.erin.bpms.loan.consumption.constant.CamundaVariableConstants.GRANT_LOAN_AMOUNT;
import static mn.erin.bpms.loan.consumption.service_task.calculation.CalculateLoanAmountTask.REJECTED_STATE;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.disableAllExecutions;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getCaseVariables;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.getDisabledExecutions;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.suspendAllActiveProcesses;
import static mn.erin.domain.bpm.BpmMessagesConstants.CONSUMPTION_CUSTOMER_MAKE_LOAN_DECISION_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.CONSUMPTION_CUSTOMER_MAKE_LOAN_DECISION_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MUST_CALCULATE_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.MUST_CALCULATE_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_FIELDS_CHANGED_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_FIELDS_CHANGED_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.CONFIRM_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT;
import static mn.erin.domain.bpm.BpmModuleConstants.FIXED_ACCEPTED_LOAN_AMOUNT_STRING;
import static mn.erin.domain.bpm.BpmModuleConstants.LAST_CALCULATED_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT_DESCRIPTION;
import static mn.erin.domain.bpm.BpmModuleConstants.OLD_FIXED_ACCEPTED_LOAN_AMOUNT;

/**
 * @author Tamir
 */
public class LoanAmountValidationTask implements JavaDelegate
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LoanAmountValidationTask.class);

  private final ProductRepository productRepository;

  public LoanAmountValidationTask(ProductRepository productRepository)
  {
    this.productRepository = productRepository;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {

    String confirmLoanAmount = (String) execution.getVariable(CONFIRM_LOAN_AMOUNT);
    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);

    if (execution.getVariable(GRANT_LOAN_AMOUNT) == null)
    {
      throw new ProcessTaskException(MUST_CALCULATE_CODE, MUST_CALCULATE_MESSAGE);
    }

    String loanProductDescription = String.valueOf(execution.getVariable(LOAN_PRODUCT_DESCRIPTION));
    String lastCalculatedLoanProduct = String.valueOf(execution.getVariable(LAST_CALCULATED_PRODUCT));

    if (!lastCalculatedLoanProduct.equals(loanProductDescription))
    {
      throw new ProcessTaskException(TASK_FIELDS_CHANGED_CODE, TASK_FIELDS_CHANGED_MESSAGE);
    }

    if (null != execution.getVariable(OLD_FIXED_ACCEPTED_LOAN_AMOUNT) && null != execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT))
    {
      long oldFixedAcceptedLoanAmount = (long) execution.getVariable(OLD_FIXED_ACCEPTED_LOAN_AMOUNT);
      long fixedAcceptedLoanAmount = (long) execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT);

      if (BigDecimal.valueOf(oldFixedAcceptedLoanAmount).compareTo(BigDecimal.valueOf(fixedAcceptedLoanAmount)) != 0)
      {
        throw new ProcessTaskException(MUST_CALCULATE_CODE, MUST_CALCULATE_MESSAGE);
      }
    }

    if (StringUtils.isBlank(confirmLoanAmount))
    {
      throw new ProcessTaskException(CONSUMPTION_CUSTOMER_MAKE_LOAN_DECISION_CODE, CONSUMPTION_CUSTOMER_MAKE_LOAN_DECISION_MESSAGE);
    }
    if (confirmLoanAmount.equals(REJECTED_STATE))
    {
      disableAndSuspendTasks(caseInstanceId, execution);
    }

    if (null != execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT))
    {
      long fixedAcceptedLoanAmount = (long) execution.getVariable(FIXED_ACCEPTED_LOAN_AMOUNT);
      String fixedAcceptedLoanAmountString = NumberUtils.longToString(fixedAcceptedLoanAmount);
      execution.setVariable(FIXED_ACCEPTED_LOAN_AMOUNT_STRING, fixedAcceptedLoanAmountString);
      execution.getProcessEngine().getCaseService().setVariable(caseInstanceId, FIXED_ACCEPTED_LOAN_AMOUNT_STRING, fixedAcceptedLoanAmountString);
    }

    String loanProduct = String.valueOf(execution.getVariable(LOAN_PRODUCT));
    String productCode = loanProduct.substring(0, 4);

    setRepaymentType(productCode, execution, execution.getProcessEngine().getCaseService(), caseInstanceId);

    // re-enable send to loan decision execution.
    reEnableSendLoanDecision(caseInstanceId, execution);
  }

  private String setRepaymentType(String productCode, DelegateExecution execution, CaseService caseService, String caseInstanceId) throws
      UseCaseException
  {
    GetProduct getProduct = new GetProduct(productRepository);
    Product product = getProduct.execute(new UniqueProductInput(productCode, "CONSUMER"));

    String repaymentType = product.getCategoryDescription();

    if (repaymentType.equals("Үндсэн төлбөр тэнцүү"))
    {
      execution.setVariable("repaymentTypeId", "equalPrinciplePayment");
      caseService.setVariable(caseInstanceId, "repaymentTypeId", "equalPrinciplePayment");
    }
    else
    {
      execution.setVariable("repaymentTypeId", "equatedMonthlyInstallment");
      caseService.setVariable(caseInstanceId, "repaymentTypeId", "equatedMonthlyInstallment");
    }

    execution.setVariable("repaymentType", repaymentType);

    return repaymentType;
  }

  private void reEnableSendLoanDecision(String caseInstanceId, DelegateExecution execution)
  {
    CaseService caseService = execution.getProcessEngine().getCaseService();

    List<CaseExecution> disabledExecutions = getDisabledExecutions(caseInstanceId, execution);
    Map<String, Object> caseVariables = getCaseVariables(caseInstanceId, execution.getProcessEngine());

    if (!disabledExecutions.isEmpty())
    {
      for (CaseExecution disabledExecution : disabledExecutions)
      {
        String activityId = disabledExecution.getActivityId();

        if (activityId.equalsIgnoreCase(ACTIVITY_ID_SEND_LOAN_REQUEST_DECISION) && disabledExecution.isDisabled())
        {
          caseService.reenableCaseExecution(disabledExecution.getId(), caseVariables);
          LOGGER.info("######## Re-enables create loan account execution with variable count = {}", caseVariables.size());
        }
      }
    }
  }

  private void disableAndSuspendTasks(String caseInstanceId, DelegateExecution execution)
  {
    suspendAllActiveProcesses(caseInstanceId, execution);
    disableAllExecutions(caseInstanceId, execution);
  }
}
