package mn.erin.bpm.repository.jdbc.interfaces;

import java.time.LocalDateTime;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcUnionFields;

public interface JdbcUnionFieldsRepository extends CrudRepository<JdbcUnionFields, String>
{
  @Modifying
  @Query(value = "INSERT INTO ERIN_OL_UNION(CUSTOMER_NUMBER,REGISTER_ID, KEY_FIELD, TRACK_NUMBER, PRODUCT_CATEGORY, PROCESS_REQUEST_ID, PROCESS_TYPE_ID, REQUEST_TYPE, DATE_TIME) VALUES(:customerNumber, :registerId, :keyField, :trackNumber, :productCategory, :processRequestId, :processTypeId, :requestType, :dateTime)")
  int insert(@Param("customerNumber") String customerNumber,
      @Param("registerId") String registerId,
      @Param("keyField") String keyField,
      @Param("trackNumber") String trackNumber,
      @Param("productCategory") String productCategory,
      @Param("processRequestId") String processRequestId,
      @Param("processTypeId") String processTypeId,
      @Param("requestType") String requestType,
      @Param("dateTime") LocalDateTime dateTime);
}
