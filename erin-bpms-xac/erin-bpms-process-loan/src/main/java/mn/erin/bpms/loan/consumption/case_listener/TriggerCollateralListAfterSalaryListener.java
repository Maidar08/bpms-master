package mn.erin.bpms.loan.consumption.case_listener;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.CaseExecutionListener;
import org.camunda.bpm.engine.delegate.DelegateCaseExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.usecase.product.GetProductsById;

import static mn.erin.bpms.loan.consumption.constant.CamundaActivityIdConstants.ACTIVITY_ID_STAGE_COLLATERAL_LIST;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.manuallyStartExecution;
import static mn.erin.bpms.loan.consumption.utils.CaseExecutionUtils.terminateExecutionByActivityId;
import static mn.erin.domain.bpm.BpmModuleConstants.HAS_COLLATERAL;
import static mn.erin.domain.bpm.BpmModuleConstants.LOAN_PRODUCT;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_REQUEST_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.YES_MN_VALUE;

public class TriggerCollateralListAfterSalaryListener implements CaseExecutionListener
{
  private static final Logger LOG = LoggerFactory.getLogger(TriggerCollateralListAfterSalaryListener.class);

  private final ProductRepository productRepository;

  public TriggerCollateralListAfterSalaryListener(ProductRepository productRepository)
  {
    this.productRepository = Objects.requireNonNull(productRepository, "Product repository is required!");
  }

  @Override
  public void notify(DelegateCaseExecution caseExecution) throws Exception
  {
    String caseInstanceId = caseExecution.getCaseInstanceId();
    ProcessEngine processEngine = caseExecution.getProcessEngine();

    Map<String, Object> executionVariables = caseExecution.getVariables();

    String requestId = (String) caseExecution.getVariable(PROCESS_REQUEST_ID);
    String loanProduct = (String) caseExecution.getVariable(LOAN_PRODUCT);

    if (null == loanProduct)
    {
      LOG.error("Product variable is null with REQUEST ID = [{}]", requestId);
      return;
    }

    String hasCollateralValue = (String) caseExecution.getVariable(HAS_COLLATERAL);

    if (null == hasCollateralValue)
    {
      GetProductsById getProducts = new GetProductsById(productRepository);
      List<Product> products = getProducts.execute(loanProduct).getProductsList();

      boolean hasCollateral = false;

      for (Product product : products)
      {
        if (product.isHasCollateral())
        {
          hasCollateral = true;
        }
      }
      if (hasCollateral)
      {
        manuallyStartExecution(caseInstanceId, ACTIVITY_ID_STAGE_COLLATERAL_LIST, processEngine, executionVariables);
      }
    }
    else
    {
      if (hasCollateralValue.equals(YES_MN_VALUE))
      {
        manuallyStartExecution(caseInstanceId, ACTIVITY_ID_STAGE_COLLATERAL_LIST, processEngine, executionVariables);
      }
      else
      {
        terminateExecutionByActivityId(caseInstanceId, processEngine, ACTIVITY_ID_STAGE_COLLATERAL_LIST, executionVariables);
      }
    }
  }
}
