package mn.erin.bpm.repository.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Tamir
 */
@Table("ERIN_CONTRACT_INFO_LEASING")
public class JdbcContractInfoLeasing
{
  @Id
  String orgReqId;
  String contractNum;

  String termYear;
  String customerCif;
  String accountNumber;

  String paymentType;
  BigDecimal paymentAmount;
  BigDecimal paymentPercent;

  String suppPayTermDay;
  BigDecimal suppPayPercent;

  String isZeroRate;
  BigDecimal fee;

  LocalDateTime createdDate;
  LocalDateTime endDate;

  String createdUserId;
  String createdUserName;

  String lastUpdatedUserId;
  String lastUpdatedUserName;

  String approvedUserId;
  String approvedUserName;

  String relEmpName;
  String relEmpPhone;

  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  BigDecimal modifiedNum;
  String isLatest;

}
