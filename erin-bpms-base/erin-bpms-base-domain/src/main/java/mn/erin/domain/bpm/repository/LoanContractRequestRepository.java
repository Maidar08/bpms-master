package mn.erin.domain.bpm.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.contract.LoanContractRequest;
import mn.erin.domain.bpm.model.process.ProcessRequestId;

/**
 * @author Temuulen Naranbold
 */
public interface LoanContractRequestRepository extends Repository<LoanContractRequest>
{
  /**
   * Creates the loan contract request
   * @param id The unique process request ID
   * @param processInstanceId The unique id of process instance
   * @param type The type of loan contract
   * @param account Loan account
   * @param amount The amount of loan
   * @param createdDate Created date of loan contract
   * @param userId Requesting userId
   * @param groupId Group ID
   * @param tenantId Tenant ID
   * @param state State of loan request
   */
  void create(ProcessRequestId id, String processInstanceId, String type, String account,
      BigDecimal amount, Date createdDate, String userId, String groupId, String tenantId, String state, String cif, String product);

  /**
   * @param instanceId Process instance ID
   * @return Loan contract request
   */
  LoanContractRequest findByInstanceId(String instanceId);

  List<LoanContractRequest> findByGroupId(String groupId, Date startDate, Date endDate);

  /**
   *
   * @param processInstanceId process instance id
   * @param processRequestId
   * @param modifiedUser
   */
  boolean update(String processInstanceId, String processRequestId, String modifiedUser);

  /**
   *
   * @param processInstanceId process instance id
   * @param modifiedUser
   */
  boolean update(String processInstanceId, String modifiedUser);

  /**
   * @param loanAccount  loan account number
   * @return Loan contract requests
   */
  List<LoanContractRequest> findByAccountId(String loanAccount);

  List<LoanContractRequest> findAllByGivenDate(Date startDate, Date endDate);
}
