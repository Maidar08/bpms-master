package mn.erin.bpm.repository.jdbc.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcOrganizationSalary;
import mn.erin.bpm.repository.jdbc.model.JdbcOrganizationSalaryExcel;

/**
 * @author Tamir
 */
public interface JdbcOrganizationSalaryRepository extends CrudRepository<JdbcOrganizationSalary, String>
{
  @Modifying
  @Query(value = "INSERT INTO ERIN_ORG_SALARY(CONTRACTID,CNAME, REGISTERNUMBER, CIF, CONTRACTDT, CCREATEDT, RECORD_STAT, CONTRACTBRANCH, CREATED_USERID, PROCESS_INSTANCE_ID, CREATED_AT) VALUES(:orgRequestId, :organizationName, :regNumber, :cif, :contractDt, :establishedDt, :state, :branchNumber, :assignee, :instanceId, :createdDate)")
  int insert(@Param("orgRequestId") String orgRequestId,
      @Param("organizationName") String organizationName,
      @Param("regNumber") String regNumber,
      @Param("cif") String cif,
      @Param("contractDt") LocalDateTime contractDt,
      @Param("establishedDt") LocalDate establishedDt,
      @Param("state") String state,
      @Param("branchNumber") String branchNumber,
      @Param("assignee") String assignee,
      @Param("instanceId") String instanceId,
      @Param("createdDate") Date createdDate);

  @Query(value = "SELECT * FROM ERIN_ORG_SALARY WHERE REGISTERNUMBER = :regNumber")
  Collection<JdbcOrganizationSalary> findByRequestId(@Param("regNumber") String regNumber);

  @Query(value = "SELECT * FROM ERIN_ORG_SALARY WHERE STATE = :firstState OR STATE = :secondState")
  Collection<JdbcOrganizationSalary> findByState(@Param("firstState") String firstState, @Param("secondState") String secondState);

  @Query(value = "SELECT * FROM ERIN_ORG_SALARY WHERE BRANCH_ID = :branchId")
  Collection<JdbcOrganizationSalary> getJdbcSalaryOrganizationsByGroupId(@Param("branchId") String branchId);

  @Query(value = "SELECT * FROM ERIN_ORG_SALARY ORDER BY CONTRACTID DESC")
  Collection<JdbcOrganizationSalary> getJdbcSalaryOrganizations();

  @Modifying
  @Query(value = "UPDATE ERIN_ORG_SALARY SET RECORD_STAT = :state WHERE CONTRACTID = :contractId")
  int updateParameter(@Param("contractId") String contractId, @Param("state") String state);

  @Query(value = "SELECT CONTRACTID FROM (SELECT * FROM ERIN_ORG_SALARY ORDER BY CONTRACTID DESC) WHERE ROWNUM = 1")
  String getJdbcSalaryOrgLastContractId();

  @Query(value = "SELECT * FROM ERIN_ORG_SALARY")
  Collection<JdbcOrganizationSalaryExcel> getJdbcSalaryOrganizationsExcel();

  @Modifying
  @Query(value = "UPDATE ERIN_ORG_SALARY SET RECORD_STAT = :state, CHECKER_ID = :confirmedUser, CHECKER_DT_STAMP = :confirmedDate  WHERE CONTRACTID = :contractId")
  int updateToConfirm(@Param("contractId") String contractId, @Param("state") String state, @Param("confirmedUser") String confirmedUser,
      @Param("confirmedDate") Date confirmedDate);

  @Modifying
  @Query(value =
      "UPDATE ERIN_ORG_SALARY SET CACCOUNTID = :accountId,"
          + "EXPOSURECATEGORY_DESCRIPTION = :exposureCategoryDescription, HRCNT = :hrcnt, EMPNAME = :empName, EMPPHONE = :empPhone, EXPIREDT = :expiredt,"
          + "ARATE = :arate, ERATE = :erate, COUNTRYREGNUMBER = :countryRegNumber,LEAKAGE = :leakage, CORPORATE_TYPE = :corporateType,"
          + "SALARYTRANFEE = :salaryTranFee, IS_SALARY_LOAN = :isSalaryLoan, CORPORATERANK = :corporateRank, INTCOND = :intCond, SDAY1 = :sDay1, SDAY2 = :sDay2,"
          + "STIME = :sTime, CYEAR = :cYear, CEXTENDED = :cExtended, CEXTENDED_DATE = :cExtendedDate, CEXTENDYEAR = :cExtendedYear,"
          + "ADDITIONAL_INFO = :additionalInfo, DANREGNUMBER = :danRegNumber, DISTRICT = :district, ONLINESAL = :onlineSal, CONTRACTDT = :contractdt, "
          + "CCREATEDT = :ccreatedt, CIF = :cif, CREATED_AT = :createdDate, LASTCONTRACTNO = :lastContractNo WHERE CONTRACTID = :contractId")
  int updateSalaryOrganization(
      @Param("accountId") String accountId,
      @Param("exposureCategoryDescription") String exposureCategoryDescription,
      @Param("hrcnt") Integer hrcnt,
      @Param("empName") String empName,
      @Param("empPhone") String empPhone,
      @Param("expiredt") Date expiredt,
      @Param("arate") Double arate,
      @Param("erate") Double erate,
      @Param("countryRegNumber") String countryRegNumber,
      @Param("leakage") String leakage,
      @Param("corporateType") Integer corporateType,
      @Param("salaryTranFee") Long salaryTranFee,
      @Param("isSalaryLoan") Integer isSalaryLoan,
      @Param("corporateRank") String corporateRank,
      @Param("intCond") String intCond,
      @Param("sDay1") String sDay1,
      @Param("sDay2") String sDay2,
      @Param("sTime") String sTime,
      @Param("cYear") String cYear,
      @Param("cExtended") String cExtended,
      @Param("cExtendedDate") Date cExtendedDate,
      @Param("cExtendedYear") String cExtendedYear,
      @Param("additionalInfo") String additionalInfo,
      @Param("danRegNumber") String danRegNumber,
      @Param("district") String district,
      @Param("onlineSal") String onlineSal,
      @Param("contractdt") Date contractdt,
      @Param("ccreatedt") Date ccreatedt,
      @Param("cif") String cif,
      @Param("contractId") String contractId,
      @Param("createdDate") Date createdDate,
      @Param("lastContractNo") String lastContractNo
  );

  @Modifying
  @Query(value = "UPDATE ERIN_ORG_SALARY SET MAKER_ID = :makerId, MAKER_DT_STAMP = :makerDate,  RECORD_STAT = :state WHERE CONTRACTID = :contractId")
  int updateToConfirmByDirector(@Param("contractId") String contractId, @Param("makerId") String makerId, @Param("makerDate") Date makerDate,  @Param("state") String state);

  @NotNull
  @Query(value = "Select * FROM ERIN_ORG_SALARY WHERE CONTRACTDT >= :startDate AND CONTRACTDT < :endDate + 1 ORDER BY CONTRACTID DESC")
  Collection<JdbcOrganizationSalary> findAllByGivenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Modifying
  @Query(value = "UPDATE ERIN_ORG_SALARY SET LOVNUMBER = :lovNumber, EXPOSURECATEGORY_CODE = :exposureCategoryCode, FORM4001 = :form4001, MENDSALARY = :mEndSalary, "
      + "MSTARTSALARY = :mStartSalary, UPDATED_AT = :updatedAt, LAST_UPDATED_BY = :lastUpdatedBy, CREATED_USERID = :createdUserId, CHARGEGLACCOUNT = :chargeGlAccount, "
      + "RELEASEEMPNAME = :releaseEmpName, ONCE_AUTH = :onceAuth, CHECKER_DT_STAMP = :checkerDtStamp, CHECKER_ID = :checkerId, MAKER_DT_STAMP = :makerDtStamp, MAKER_ID = :makerId, "
      + "MOD_NO = :modNo,  AUTH_STAT = :authStat, ERATE_MAX = :eRateMax, CCREATED_DATE = :cCreatedDate, EXTENSION_DT = :extensionDt, CONTRACTNUMBER = :contractNumber, "
      + "CIF = :cifNumber, FCNAME = :fcName, CCREATEDT = :cCreateDate, CREATED_AT = :createdAt, COUNTRYREGNUMBER = :countryRegNumber, ADDITION_INFO = :additionInfo WHERE CONTRACTID = :contractId")
  int updateExclusiveData(
      @Param("lovNumber") String lovNumber,
      @Param("exposureCategoryCode") String exposureCategoryCode,
      @Param("form4001") Date form4001,
      @Param("mEndSalary") Date mEndSalary,
      @Param("mStartSalary") Date mStartSalary,
      @Param("updatedAt") Date updatedAt,
      @Param("lastUpdatedBy") String lastUpdatedBy,
      @Param("createdUserId") String createdUserId,
      @Param("chargeGlAccount") String chargeGlAccount,
      @Param("releaseEmpName") String releaseEmpName,
      @Param("onceAuth") String onceAuth,
      @Param("checkerDtStamp") Date checkerDtStamp,
      @Param("checkerId") String checkerId,
      @Param("makerDtStamp") Date makerDtStamp,
      @Param("makerId") String makerId,
      @Param("modNo") Integer modNo,
      @Param("authStat") String authStat,
      @Param("eRateMax") Double eRateMax,
      @Param("cCreatedDate") Date cCreatedDate,
      @Param("extensionDt") Date extensionDt,
      @Param("contractNumber") String contractNumber,
      @Param("cifNumber") String cifNumber,
      @Param("fcName") String fcName,
      @Param("cCreateDate") Date cCreateDate,
      @Param("createdAt") Date createdAt,
      @Param("countryRegNumber") String countryRegNumber,
      @Param("additionInfo") String additionInfo,
      @Param("contractId") String contractId
  );
}
