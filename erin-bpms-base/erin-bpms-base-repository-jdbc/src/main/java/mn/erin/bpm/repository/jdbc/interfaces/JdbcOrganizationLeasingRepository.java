package mn.erin.bpm.repository.jdbc.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcOrganizationLeasing;
import mn.erin.bpm.repository.jdbc.model.JdbcOrganizationLeasingExcel;

/**
 * @author Tamir
 */
public interface JdbcOrganizationLeasingRepository extends CrudRepository<JdbcOrganizationLeasing, String>
{
  @Modifying
  @Query(value = "INSERT INTO ERIN_ORG_LEASING(CONTRACTID, NAME, REGISTERNUMBER, CIF, CONTRACTDT, BIRTHDT, RECORD_STAT, CONTRACTBRANCH, CREATED_USERID, PROCESS_INSTANCE_ID, CREATED_AT) VALUES (:orgRequestId, :organizationName, :regNumber, :cif, :contractDt, :establishedDt, :state, :branchNumber, :assignee, :instanceId, :createdDate)")
  int insert(@Param("orgRequestId") String orgRequestId,
      @Param("organizationName") String organizationName,
      @Param("regNumber") String regNumber,
      @Param("cif") String userId,
      @Param("contractDt") LocalDateTime contractDt,
      @Param("establishedDt") LocalDate establishedDt,
      @Param("state") String state,
      @Param("branchNumber") String branchNumber,
      @Param("assignee") String assignee,
      @Param("instanceId") String instanceId,
      @Param("createdDate") Date createdDate);



  @Query(value = "SELECT * FROM ERIN_ORG_LEASING WHERE REGISTERNUMBER = :regNumber")
  Collection<JdbcOrganizationLeasing> findByRequestId(@Param("regNumber") String regNumber);

  @Query(value = "SELECT * FROM ERIN_ORG_LEASING WHERE STATE = :firstState OR STATE = :secondState")
  Collection<JdbcOrganizationLeasing> findByState(@Param("firstState") String firstState, @Param("secondState") String secondState);

  @Query(value = "SELECT * FROM ERIN_ORG_LEASING WHERE BRANCH_ID=(:branchId)")
  List<JdbcOrganizationLeasing> getJdbcLeasingOrganizationsByGroupId(@Param(value = "branchId") String branchId);

  @Query(value = "SELECT * FROM ERIN_ORG_LEASING ORDER BY CONTRACTID DESC")
  Collection<JdbcOrganizationLeasing> getJdbcLeasingOrganizations();

  @Query(value ="SELECT NAME FROM (SELECT * FROM ERIN_ORG_LEASING WHERE TERMINALID=(:terminalId) ORDER BY CONTRACTID DESC) WHERE ROWNUM = 1")
  String getNameByTerminalId(@Param(value= "terminalId") String terminalId);

  @Modifying
  @Query(value = "UPDATE ERIN_ORG_LEASING SET RECORD_STAT = :state WHERE CONTRACTID = :contractId")
  int updateParameter(@Param("contractId") String contractId, @Param("state") String state);

  @Query(value = "SELECT CONTRACTID FROM (SELECT * FROM ERIN_ORG_LEASING ORDER BY CONTRACTID DESC) WHERE ROWNUM = 1")
  String getJdbcLeasingOrgLastContractId();

  @Query(value = "SELECT * FROM ERIN_ORG_LEASING")
  Collection<JdbcOrganizationLeasingExcel> getJdbcLeasingOrganizationsExcel();

  @Modifying
  @Query(value = "UPDATE ERIN_ORG_LEASING SET RECORD_STAT = :state, CHECKER_ID = :confirmedUser, CHECKER_DT_STAMP = :confirmedDate  WHERE CONTRACTID = :contractId")
  int updateToConfirm(@Param("contractId") String contractId, @Param("state") String state, @Param("confirmedUser") String confirmedUser,
      @Param("confirmedDate") Date confirmedDate);

  @Modifying
  @Query(value = "UPDATE ERIN_ORG_LEASING SET MAKER_ID = :makerId,  MAKER_DT_STAMP = :makerDate, RECORD_STAT = :state  WHERE CONTRACTID = :contractId")
  int updateToConfirmByDirector(@Param("contractId") String contractId, @Param("makerId") String makerId, @Param("makerDate") Date makerDate,
      @Param("state") String state);

  @Modifying
  @Query(value =
      "UPDATE ERIN_ORG_LEASING SET CYEAR = :cYear, FEE = :fee, CUSTTYPE = :custType, EXPOSURECATEGORY_DESCRIPTION = :exposureCategoryDescription, PRODUCTCAT = :productCat,"
          + "PRODUCTDESC = :productDesc, CONTACTNAME = :contactName, CONTACTPHONE = :contactPhone, CONTACTEMAIL = :contactEmail, CONTACTDESC = :contactDesc, SETTLEMENTDATE = :settlementDate, SETTLEMENTPERCENT = :settlementPercent,"
          + "SETTLEMENTACCOUNT = :settlementAccount, CONDITION = :condition, RATE = :rate, DISCHARGE = :discharge, BNPL = :bnpl, TERMINALID = :terminalId, CEXTENDYEAR = :cExtendedYear, CEXTENDED = :cExtended,"
          + "CEXTENDED_DATE = :cExtendedDate, CHARGETYPE = :chargeType, CHARGEAMOUNT = :chargeAmount, LOANAMOUNT = :loanAmount, CONTRACTDT = :contractDt, EXPIREDT = :expireDt, LASTCONTRACTNO = :lastContractNo,  "
          + "CIF = :cif, COUNTRYREGNUMBER = :countryRegNumber, BIRTHDT = :birthDt, ADDRESS = :address, PHONE = :phone, MAIL = :mail, CREATED_AT = :createdDate WHERE CONTRACTID = :contractId")
  int updateLeasingRequest(@Param("cYear") String cYear,
      @Param("fee") Long fee,
      @Param("custType") String custType,
      @Param("exposureCategoryDescription") String exposureCategoryDescription,
      @Param("productCat") String productCat,
      @Param("productDesc") String productDesc,
      @Param("contactName") String contactName,
      @Param("contactPhone") String contactPhone,
      @Param("contactEmail") String contactEmail,
      @Param("contactDesc") String contactDesc,
      @Param("settlementDate") String settlementDate,
      @Param("settlementPercent") String settlementPercent,
      @Param("settlementAccount") String settlementAccount,
      @Param("condition") String condition,
      @Param("rate") Double rate,
      @Param("discharge") Long discharge,
      @Param("bnpl") String bnpl,
      @Param("terminalId") String terminalId,
      @Param("cExtendedYear") String cExtendedYear,
      @Param("cExtended") String cExtended,
      @Param("cExtendedDate") Date cExtendedDate,
      @Param("chargeType") String chargeType,
      @Param("chargeAmount") String chargeAmount,
      @Param("loanAmount") String loanAmount,
      @Param("contractId") String contractId,
      @Param("contractDt") Date contractDt,
      @Param("expireDt") Date expireDt,
      @Param("lastContractNo") String lastContractNo,
      @Param("cif") String cif,
      @Param("countryRegNumber") String countryRegNumber,
      @Param("birthDt") Date birthDt,
      @Param("address") String address,
      @Param("phone") String phone,
      @Param("mail") String mail,
      @Param("createdDate") Date createdDate
  );

  @NotNull
  @Query(value = "Select * FROM ERIN_ORG_LEASING WHERE CONTRACTDT >= :startDate AND CONTRACTDT < :endDate + 1 ORDER BY CONTRACTID DESC")
  Collection<JdbcOrganizationLeasing> findAllByGivenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
