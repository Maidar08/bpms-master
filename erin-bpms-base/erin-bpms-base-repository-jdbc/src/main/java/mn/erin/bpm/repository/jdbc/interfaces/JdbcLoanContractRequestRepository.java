package mn.erin.bpm.repository.jdbc.interfaces;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcLoanContractRequest;

/**
 * @author Temuulen Naranbold
 */
public interface JdbcLoanContractRequestRepository extends CrudRepository<JdbcLoanContractRequest, String>
{
  @Modifying
  @Query(value =
      "INSERT INTO LOAN_CONTRACT_PROCESS_REQUEST(PROCESS_INSTANCE_ID, LOAN_CONTRACT_ID, LOAN_CONTRACT_TYPE, LOAN_ACCOUNT, LOAN_AMOUNT, CREATED_DATE, ASSIGNED_USER_ID, GROUP_ID, TENANT_ID, STATE, CIF_NUMBER, PRODUCT_DESCRIPTION)"
          + "VALUES (:instanceId, :id, :type, :account, :amount, :createdDate, :userId, :groupId, :tenantId, :state, :cif, :product)")
  int insert(@Param("instanceId") String instanceId, @Param("id") String id, @Param("type") String type, @Param("account") String account,
      @Param("amount") BigDecimal amount,
      @Param("createdDate") Date createdDate, @Param("userId") String userId, @Param("groupId") String groupId, @Param("tenantId") String tenantId,
      @Param("state") String state, @Param("cif") String cif, @Param("product") String product);

  @NotNull
  @Query(value = "Select * FROM LOAN_CONTRACT_PROCESS_REQUEST WHERE LOAN_CONTRACT_ID = :loanContractId")
  Optional<JdbcLoanContractRequest> findById(@NotNull @Param("loanContractId") String loanContractId);

  @NotNull
  @Query(value = "SELECT * FROM LOAN_CONTRACT_PROCESS_REQUEST")
  List<JdbcLoanContractRequest> findAll();

  @NotNull
  @Query(value = "Select * FROM LOAN_CONTRACT_PROCESS_REQUEST WHERE PROCESS_INSTANCE_ID = :instanceId")
  Optional<JdbcLoanContractRequest> findByProcessInstanceId(@NotNull @Param("instanceId") String instanceId);

  @NotNull
  @Query(value = "Select * FROM LOAN_CONTRACT_PROCESS_REQUEST WHERE GROUP_ID = :groupId AND CREATED_DATE >= :startDate AND CREATED_DATE < :endDate + 1")
  List<JdbcLoanContractRequest> findByGroupId(@NotNull @Param("groupId") String groupId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value =  "Select * FROM LOAN_CONTRACT_PROCESS_REQUEST WHERE LOAN_CONTRACT_ID LIKE :loanAccount")
  List<JdbcLoanContractRequest> findByAccountId(@NotNull @Param("loanAccount") String loanAccount);

  @Modifying
  @Query(value = "UPDATE LOAN_CONTRACT_PROCESS_REQUEST SET ASSIGNED_USER_ID = :modifiedUser WHERE PROCESS_INSTANCE_ID = :processInstanceId AND LOAN_CONTRACT_ID = :processRequestId")
  int update(@Param("processInstanceId") String processInstanceId, @Param("processRequestId") String processRequestId , @Param("modifiedUser") String modifiedUser);

  @Modifying
  @Query(value = "UPDATE LOAN_CONTRACT_PROCESS_REQUEST SET ASSIGNED_USER_ID = :modifiedUser WHERE PROCESS_INSTANCE_ID = :processInstanceId")
  int update(@Param("processInstanceId") String processInstanceId, @Param("modifiedUser") String modifiedUser);

  @NotNull
  @Query(value = "Select * FROM LOAN_CONTRACT_PROCESS_REQUEST WHERE CREATED_DATE >= :startDate AND CREATED_DATE < :endDate + 1")
  List<JdbcLoanContractRequest> findAllByGivenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
