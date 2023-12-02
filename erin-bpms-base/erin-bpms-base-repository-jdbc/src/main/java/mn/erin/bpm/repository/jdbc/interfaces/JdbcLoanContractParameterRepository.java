package mn.erin.bpm.repository.jdbc.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcLoanContractParameter;

/**
 * @author Temuulen Naranbold
 */
public interface JdbcLoanContractParameterRepository extends CrudRepository<JdbcLoanContractParameter, String>
{
  @Modifying
  @Query(value = "INSERT INTO LOAN_CONTRACT_PARAMETER(PROCESS_INSTANCE_ID, DEF_KEY, PARAMETER_VALUE, PARAMETER_VALUE_TYPE, PARAMETER_ENTITY_TYPE) "
      + "VALUES (:processInstanceId, :defKey, :parameterValue, :parameterValueType, :parameterEntityType)")
  int insert(@Param("processInstanceId") String processInstanceId, @Param("defKey") String defKey, @Param("parameterValue") Serializable parameterValue,
      @Param("parameterValueType") String parameterValueType, @Param("parameterEntityType") String parameterEntityType);

  @Modifying
  @Query(value = "UPDATE LOAN_CONTRACT_PARAMETER SET PARAMETER_VALUE = :parameterValue WHERE PROCESS_INSTANCE_ID = :processInstanceId AND DEF_KEY = :defKey")
  int update(@Param("processInstanceId") String processInstanceId, @Param("defKey") String defKey, @Param("parameterValue") Serializable parameterValue);

  @Modifying
  @Query(value = "UPDATE LOAN_CONTRACT_PARAMETER SET PARAMETER_VALUE = :parameterValue WHERE PROCESS_INSTANCE_ID = :processInstanceId AND DEF_KEY = :defKey AND PARAMETER_ENTITY_TYPE = :parameterEntityType")
  int update(@Param("processInstanceId") String processInstanceId, @Param("defKey") String defKey, @Param("parameterValue") Serializable parameterValue,
      @Param("parameterEntityType") String parameterEntityType);

  @NotNull
  @Query(value = "SELECT * FROM LOAN_CONTRACT_PARAMETER")
  List<JdbcLoanContractParameter> findAll();

  @NotNull
  @Query(value = "SELECT * FROM LOAN_CONTRACT_PARAMETER WHERE PROCESS_INSTANCE_ID = :id")
  Optional<JdbcLoanContractParameter> findById(@NotNull @Param("id") String id);

  @NotNull
  @Query(value = "SELECT * FROM LOAN_CONTRACT_PARAMETER WHERE PROCESS_INSTANCE_ID = :id AND DEF_KEY = :defKey AND PARAMETER_ENTITY_TYPE = :entity")
  Optional<JdbcLoanContractParameter> findByProcessInstanceIdAndDefKeyAndEntity(@NotNull @Param("id") String id, @Param("defKey") String defKey, @Param("entity") String entity);

  @NotNull
  @Query(value = "SELECT * FROM LOAN_CONTRACT_PARAMETER WHERE PROCESS_INSTANCE_ID = :id AND DEF_KEY = :defKey")
  List<JdbcLoanContractParameter> findByProcessInstanceIdAndDefKey(@NotNull @Param("id") String id, @Param("defKey") String defKey);

  @NotNull
  @Query(value = "SELECT * FROM LOAN_CONTRACT_PARAMETER WHERE PROCESS_INSTANCE_ID = :id")
  Optional<JdbcLoanContractParameter> findByProcessInstanceId(@NotNull @Param("id") String id);

  @Query(value = "SELECT * FROM LOAN_CONTRACT_PARAMETER WHERE PROCESS_INSTANCE_ID = :id")
  List<JdbcLoanContractParameter> findByProcessInstanceId2(@NotNull @Param("id") String id);
}
