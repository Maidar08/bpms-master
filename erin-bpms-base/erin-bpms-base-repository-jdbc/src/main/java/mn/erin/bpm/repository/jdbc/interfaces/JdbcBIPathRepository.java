package mn.erin.bpm.repository.jdbc.interfaces;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcBiPath;

public interface JdbcBIPathRepository extends CrudRepository<JdbcBiPath, String>
{
  @Query("SELECT * FROM BI_PATH WHERE PROCESS_TYPE_ID = :processTypeId AND PRODUCT_CODE = :productCode")
  JdbcBiPath getBiPath(@Param(value = "processTypeId") String processTypeId, @Param(value = "productCode") String productCode);
}
