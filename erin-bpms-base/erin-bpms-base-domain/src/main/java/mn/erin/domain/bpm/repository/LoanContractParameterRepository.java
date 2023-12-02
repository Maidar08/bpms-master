package mn.erin.domain.bpm.repository;

import java.util.List;
import java.util.Map;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.contract.LoanContractParameter;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;

/**
 * @author Temuulen Naranbold
 */
public interface LoanContractParameterRepository extends Repository<LoanContractParameter>
{
  /**
   * If loan contract parameter already exists it will update, otherwise will create
   *
   * @param instanceId The unique id of process instance
   * @param taskName The name of task
   * @param parameterValue Values of loan contract
   * @param parameterEntityType The entity type
   * @throws BpmRepositoryException Throws exception when converting parameter value to json string
   */
  void update(ProcessInstanceId instanceId, String taskName, Map<String, Object> parameterValue, String parameterEntityType)
      throws BpmRepositoryException;

  /**
   * @param instanceId Instance ID
   * @param defKey Definition key of task
   * @return Loan contract parameter
   */
  LoanContractParameter getByInstanceIdAndDefKey(String instanceId, String defKey) throws BpmRepositoryException;

  List<LoanContractParameter> getByInstanceId(String processInstanceId);
}
