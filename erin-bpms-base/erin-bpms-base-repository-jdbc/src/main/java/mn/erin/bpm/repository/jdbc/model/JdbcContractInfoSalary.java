package mn.erin.bpm.repository.jdbc.model;

import java.math.BigDecimal;
import java.sql.NClob;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Tamir
 */
@Table("ERIN_CONTRACT_INFO_SALARY")
public class JdbcContractInfoSalary
{
  @Id
  String orgRequestId;
  String contractNum;

  String termYear;
  BigDecimal leakage;
  String orgRank;

  String intRateCond;
  BigDecimal keyEmpIntRateMonthly;

  BigDecimal maxIntRate;
  BigDecimal minIntRate;

  BigDecimal salaryTransFee;
  BigDecimal salaryCountMonthly;

  String salaryFirstDay;
  String salarySecondDay;

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
  NClob specialCondition;
}
