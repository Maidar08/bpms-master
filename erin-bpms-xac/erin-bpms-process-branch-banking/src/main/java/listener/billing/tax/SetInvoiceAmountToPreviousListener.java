package listener.billing.tax;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

/**
 * @author Lkhagvadorj.A
 **/

public class SetInvoiceAmountToPreviousListener implements TaskListener
{

  @Override
  public void notify(DelegateTask delegateTask)
  {
    DelegateExecution execution = delegateTask.getExecution();
    if(execution.getVariable("action").equals("toChildTask")){
      double previousInvoiceAmount = Double.parseDouble(String.valueOf(execution.getVariable("invoiceAmount")));
      execution.setVariable("previousInvoiceAmount", previousInvoiceAmount);
      execution.setVariable("invoiceAmount", previousInvoiceAmount);
    }
  }
}
