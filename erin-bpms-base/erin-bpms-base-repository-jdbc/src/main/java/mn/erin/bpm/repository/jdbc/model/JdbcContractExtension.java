package mn.erin.bpm.repository.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Tamir
 */
@Table("ERIN_CONTRACT_EXTENSION")
public class JdbcContractExtension
{
  @Id
  String extensionId;
  String contractNum;
  String isExtended;

  LocalDateTime extendedDate;
  LocalDateTime endDate;

  BigDecimal fee;
  String status;

  String termYear;
  String type;
}
