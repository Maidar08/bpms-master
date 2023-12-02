package mn.erin.bpms.loan.consumption.service_task.co_borrower;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.variable.Variable;
import mn.erin.domain.bpm.model.variable.VariableId;

import static mn.erin.domain.bpm.BpmModuleConstants.CASE_INSTANCE_ID;

public class UpdateCoBorrowerVariablesTask implements JavaDelegate
{
  private static final String INDEXES = "indexes";
  private static final String INDEX_CO_BORROWER = "indexCoBorrower";
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCoBorrowerVariablesTask.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception
  {
    Map<String, Object> allVariables = execution.getVariables();
    CaseService caseService = execution.getProcessEngineServices().getCaseService();

    String caseInstanceId = (String) execution.getVariable(CASE_INSTANCE_ID);
    Map<String, Object> caseVariables = caseService.getVariables(caseInstanceId);

    Object indexes = allVariables.get(INDEXES);

    if (null == indexes)
    {
      return;
    }

    if (allVariables.containsKey(INDEXES) && caseVariables.containsKey(INDEX_CO_BORROWER))
    {
      int sizeOfIndexes = ((List<String>) allVariables.get(INDEXES)).size();
      int indexCoBorrower = (int) caseVariables.get(INDEX_CO_BORROWER);
      int newIndexCoBorrower = indexCoBorrower - sizeOfIndexes;
      List<String> selectedIndexes = (List<String>) allVariables.get(INDEXES);
      List<String> unselectedIndexes = getUnselectedIndexes(indexCoBorrower, selectedIndexes);
      Map<String, List<Variable>> selectedCoBorrower = getCoBorrowerVariablesByIndex(allVariables, selectedIndexes);
      Map<String, List<Variable>> unselectedCoBorrower = getCoBorrowerVariablesByIndex(allVariables, unselectedIndexes);

      switchVariableMapsByIndexes(selectedCoBorrower, unselectedCoBorrower, selectedIndexes, unselectedIndexes);

      // add selectedVariables
      List<Variable> updateVariables = convertMapToList(selectedCoBorrower);

      // add unselected variables
      updateVariables.addAll(convertMapToList(unselectedCoBorrower));

      updateCoBorrowerVariables(execution, updateVariables);
      execution.setVariable(INDEXES, selectedIndexes);
      execution.setVariable(INDEX_CO_BORROWER, newIndexCoBorrower);
    }
    else
    {
      LOGGER.error("No selected co borrower variables! with request id = [{}]", execution.getVariable(BpmModuleConstants.PROCESS_REQUEST_ID));
      throw new ProcessTaskException("No selected co borrower variables!");
    }
  }

  private List<String> getUnselectedIndexes(int indexCoBorrower, List<String> selectedIndexes)
  {
    List<String> unselectedIndexes = new ArrayList<>();
    for (int index = 1; index <= indexCoBorrower; index++)
    {
      boolean isSelectedIndex = selectedIndexes.contains(Integer.toString(index));
      if (!isSelectedIndex)
      {
        unselectedIndexes.add(Integer.toString(index));
      }
    }
    return unselectedIndexes;
  }

  private Map<String, List<Variable>> getCoBorrowerVariablesByIndex(Map<String, Object> allVariables, List<String> indexes)
  {
    Map<String, List<Variable>> returnVariables = new HashMap<>();

    indexes.forEach(index -> {
      returnVariables.put(index, new ArrayList<>());
    });

    allVariables.forEach((key, value) -> {
      for (String index : indexes)
      {
        if (key.contains("-" + index))
        {
          Variable variable = new Variable(new VariableId(key), (Serializable) value);
          returnVariables.get(index).add(variable);
        }
      }
    });

    return returnVariables;
  }

  private void switchMapsByIndexes(Map<String, List<Variable>> selectedCoBorrower, Map<String, List<Variable>> unselectedCoBorrower, String selectedIndex,
      String unselectedIndex)
  {
    List<Variable> variables = new ArrayList<>();
    variables = unselectedCoBorrower.remove(unselectedIndex);
    unselectedCoBorrower.put(selectedIndex, variables);

    variables = new ArrayList<>();
    ;
    variables = selectedCoBorrower.remove(selectedIndex);
    selectedCoBorrower.put(unselectedIndex, variables);
  }

  private void switchListElements(List<String> selectedIndexes, List<String> unselectedIndexes, String selectedIndex, String unselectedIndex,
      int indexOfSelected, int indexOfUnselected)
  {
    selectedIndexes.set(indexOfSelected, unselectedIndex);
    unselectedIndexes.set(indexOfUnselected, selectedIndex);
  }

  private void switchVariableMapsByIndexes(Map<String, List<Variable>> selectedCoBorrower, Map<String, List<Variable>> unselectedCoBorrower,
      List<String> selectedIndexes, List<String> unselectedIndexes)
  {
    for (int selectedIndex = 0; selectedIndex < selectedIndexes.size(); selectedIndex++)
    {
      for (int unselectedIndex = 0; unselectedIndex < unselectedIndexes.size(); unselectedIndex++)
      {
        if (Integer.parseInt(unselectedIndexes.get(unselectedIndex)) > Integer.parseInt(selectedIndexes.get(selectedIndex)))
        {
          switchMapsByIndexes(selectedCoBorrower, unselectedCoBorrower, selectedIndexes.get(selectedIndex), unselectedIndexes.get(unselectedIndex));
          switchListElements(selectedIndexes, unselectedIndexes, selectedIndexes.get(selectedIndex), unselectedIndexes.get(unselectedIndex), selectedIndex,
              unselectedIndex);
        }
      }
    }
  }

  private List<Variable> convertMapToList(Map<String, List<Variable>> variables)
  {
    List<Variable> returnVariables = new ArrayList<>();
    variables.forEach((key, value) -> {
      for (Variable variable : value)
      {
        String variableId = variable.getId().getId().split("-")[0];
        String updatedVariableId = variableId + "-" + key;
        Variable newVariable = new Variable(new VariableId(updatedVariableId), variable.getValue());
        returnVariables.add(newVariable);
      }
    });

    return returnVariables;
  }

  private void updateCoBorrowerVariables(DelegateExecution execution, List<Variable> updateVariables)
  {
    updateVariables.forEach(variable -> {
      execution.setVariable(variable.getId().getId(), variable.getValue());
    });
  }
}
