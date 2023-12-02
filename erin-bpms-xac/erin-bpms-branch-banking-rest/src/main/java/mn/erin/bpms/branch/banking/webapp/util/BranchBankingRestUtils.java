package mn.erin.bpms.branch.banking.webapp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mn.erin.bpms.branch.banking.webapp.model.RestCompletedFormField;
import mn.erin.bpms.branch.banking.webapp.model.RestTransaction;
import mn.erin.domain.bpm.model.branch_banking.transaction.CustomerTransaction;
import mn.erin.domain.bpm.model.branch_banking.transaction.TransactionId;
import mn.erin.domain.bpm.model.form.FieldProperty;

/**
 * @author Tamir
 */
public final class BranchBankingRestUtils
{
  private BranchBankingRestUtils()
  {

  }

  public static Collection<RestTransaction> toRestTransactions(Collection<CustomerTransaction> transactions)
  {
    if (transactions.isEmpty())
    {
      return Collections.emptyList();
    }

    Collection<RestTransaction> restTransactions = new ArrayList<>();

    for (CustomerTransaction transaction : transactions)
    {
      TransactionId transactionId = transaction.getTransactionId();
      String transactionDate = transaction.getTransactionDate();

      String transactionAmount = transaction.getTransactionAmount();
      String transactionCCY = transaction.getTransactionCCY();

      String branchId = transaction.getBranchId();
      String accountId = transaction.getAccountId();

      String userId = transaction.getUserId();
      String type= transaction.getType();

      String subType = transaction.getSubType();
      String status = transaction.getStatus();
      String particulars = transaction.getParticulars();

      restTransactions.add(new RestTransaction(transactionId.getId(), transactionDate, transactionAmount, transactionCCY,
          branchId, accountId, userId, type, subType, status, particulars));
    }

    return restTransactions;
  }

  // tax and custom info util

  public static String getSelectedFieldOptionIdByFieldID(List<RestCompletedFormField> restFormFields, String fieldId)
  {
    RestCompletedFormField searchTypeField = getFormFieldById(restFormFields, fieldId);
    if (null == searchTypeField)
    {
      return null;
    }

    String searchTypeDefaultValue = String.valueOf(searchTypeField.getFormFieldValue().getDefaultValue());
    for (FieldProperty option : searchTypeField.getOptions())
    {
      if (option.getValue().equals(searchTypeDefaultValue))
      {
        return option.getId();
      }
    }
    return null;
  }

  public static  RestCompletedFormField getFormFieldById(List<RestCompletedFormField> restFormFields, String fieldId)
  {
    for (RestCompletedFormField restFormField : restFormFields)
    {
      if (restFormField.getId().equals(fieldId))
      {
        return restFormField;
      }
    }
    return null;
  }


  public static Map<String, Object> getValuesFromMap(Map<String, Object> mapValues, List<String> parametersId){
    Map<String, Object> outputMap = new HashMap<>();
    for ( int i = 0; i < parametersId.size(); i++) {
      if (mapValues.containsKey(parametersId.get(i))){
        outputMap.put(parametersId.get(i), mapValues.get(parametersId.get(i)));
      }
    }
    return  outputMap;
  }
}
