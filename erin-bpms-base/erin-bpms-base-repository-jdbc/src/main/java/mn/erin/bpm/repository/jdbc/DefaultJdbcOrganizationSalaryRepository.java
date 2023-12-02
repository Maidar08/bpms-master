package mn.erin.bpm.repository.jdbc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcOrganizationSalaryRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcOrganizationSalary;
import mn.erin.bpm.repository.jdbc.model.JdbcOrganizationSalaryExcel;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.organization.ExcludedOrganizationData;
import mn.erin.domain.bpm.model.organization.FormDataOrganizationSalary;
import mn.erin.domain.bpm.model.organization.OrganizationRequestId;
import mn.erin.domain.bpm.model.organization.OrganizationSalary;
import mn.erin.domain.bpm.model.organization.OrganizationSalaryExcel;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;

/**
 * @author Bilguunbor
 */
@Repository
public class DefaultJdbcOrganizationSalaryRepository implements OrganizationSalaryRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJdbcOrganizationSalaryRepository.class);

  private final JdbcOrganizationSalaryRepository jdbcOrganizationSalaryRepository;

  public DefaultJdbcOrganizationSalaryRepository(JdbcOrganizationSalaryRepository jdbcOrganizationSalaryRepository)
  {
    this.jdbcOrganizationSalaryRepository = jdbcOrganizationSalaryRepository;
  }

  @Override
  public OrganizationSalary create(String orgRequestId, String organizationName, String regNumber, String cif,
      LocalDateTime contractDt, LocalDate establishedDt, String state, String branchNumber, String assignee, String instanceId, Date createdDate)
      throws BpmRepositoryException
  {
    jdbcOrganizationSalaryRepository.insert(orgRequestId, organizationName, regNumber, cif, contractDt, establishedDt, state, branchNumber, assignee,
        instanceId, createdDate);
    return new OrganizationSalary(new OrganizationRequestId(orgRequestId));
  }

  @Override
  public Collection<OrganizationSalary> findByState(String firstState, String secondState)
  {
    Collection<JdbcOrganizationSalary> jdbcOrgSalaryCollection = jdbcOrganizationSalaryRepository.getJdbcSalaryOrganizations();

    return toOrgSalaryCollection(jdbcOrgSalaryCollection);
  }

  @Override
  public Collection<OrganizationSalary> findByGroupId(GroupId groupId) throws BpmRepositoryException
  {
    try
    {
      Validate.notNull(groupId, "Group id can not be null");
      Collection<OrganizationSalary> salaryOrganizationRequestsReturn = new ArrayList<>();
      Collection<JdbcOrganizationSalary> jdbcSalaryOrganizations = jdbcOrganizationSalaryRepository.getJdbcSalaryOrganizationsByGroupId(groupId.getId());

      for (JdbcOrganizationSalary jdbcSalaryOrganization : jdbcSalaryOrganizations)
      {
        salaryOrganizationRequestsReturn.add(toOrgSalary(jdbcSalaryOrganization));
      }
      return salaryOrganizationRequestsReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public boolean update(String contractId, String state) throws BpmRepositoryException
  {
    if (jdbcOrganizationSalaryRepository.updateParameter(contractId, state) == 1)
    {
      LOGGER.info("#### SUCCESSFULLY UPDATED ORGANIZATION REQUEST STATE TO {}, WITH CONTRACT ID = {}", state, contractId);
    }
    return 1 == jdbcOrganizationSalaryRepository.updateParameter(contractId, state);
  }

  @Override
  public Collection<OrganizationSalary> findByRegNumber(String regNumber) throws BpmRepositoryException
  {
    try
    {
      Collection<OrganizationSalary> salaryOrganizationReturn = new ArrayList<>();
      Collection<JdbcOrganizationSalary> jdbcSalaryOrganizations = jdbcOrganizationSalaryRepository.findByRequestId(regNumber);

      for (JdbcOrganizationSalary jdbcSalaryOrganization : jdbcSalaryOrganizations)
      {
        salaryOrganizationReturn.add(toOrgSalary(jdbcSalaryOrganization));
      }
      return salaryOrganizationReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public int updateSalaryOrganizationRequest(FormDataOrganizationSalary formDataOrganizationSalary)
  {
    int numberOfColumnsUpdated = jdbcOrganizationSalaryRepository.updateSalaryOrganization(
        formDataOrganizationSalary.getPartnerAccountId(),
        formDataOrganizationSalary.getPartnerDirection(),
        formDataOrganizationSalary.getHrCount(),
        formDataOrganizationSalary.getPartnerContactEmployee(),
        formDataOrganizationSalary.getPartnerPhoneNumber(),
        formDataOrganizationSalary.getContractEndDate(),
        formDataOrganizationSalary.getFeeKeyWorker(),
        formDataOrganizationSalary.getFeeAmountPercent(),
        formDataOrganizationSalary.getPartnerRegistryId(),
        formDataOrganizationSalary.getFeeBankFraud(),
        formDataOrganizationSalary.getPartnerType(),
        formDataOrganizationSalary.getFeeSalaryTransaction(),
        formDataOrganizationSalary.getFeeHasLoan(),
        formDataOrganizationSalary.getFeeOrganizationRating(),
        formDataOrganizationSalary.getFeeType(),
        formDataOrganizationSalary.getFeeSalaryDaysFirst(),
        formDataOrganizationSalary.getFeeSalaryDaysSecond(),
        formDataOrganizationSalary.getFeeSalaryDays(),
        formDataOrganizationSalary.getContractPeriod(),
        formDataOrganizationSalary.getContractHasExtension(),
        formDataOrganizationSalary.getContractExtensionNewDate(),
        formDataOrganizationSalary.getContractExtensionYear(),
        formDataOrganizationSalary.getContractSpecialRemark(),
        formDataOrganizationSalary.getPartnerCodeND(),
        formDataOrganizationSalary.getPartnerNDSubordinate(),
        formDataOrganizationSalary.getFeeHasOnline(),
        formDataOrganizationSalary.getContractDate(),
        formDataOrganizationSalary.getPartnerEstablishedDate(),
        formDataOrganizationSalary.getPartnerCif(),
        formDataOrganizationSalary.getContractId(),
        formDataOrganizationSalary.getCreatedDate(),
        formDataOrganizationSalary.getContractOldNumber());
    LOGGER.info("#### SUCCESSFULLY UPDATED ORGANIZATION REQUEST REGISTRATION FORM DATA.");
    return numberOfColumnsUpdated;
  }

  @Override
  public boolean updateToConfirmByDirector(String contractId, String makerId, Date makerDate, String updateState)
  {
    return 1 == jdbcOrganizationSalaryRepository.updateToConfirmByDirector(contractId, makerId, makerDate, updateState);
  }

  @Override
  public int updateSalaryOrganizationExcluded(ExcludedOrganizationData excludedOrganizationData, String contractId)
  {
    jdbcOrganizationSalaryRepository.updateExclusiveData(
        excludedOrganizationData.getLovNumber(),
        excludedOrganizationData.getExposureCategoryCode(),
        excludedOrganizationData.getForm4001(),
        excludedOrganizationData.getmEndSalary(),
        excludedOrganizationData.getmStartSalary(),
        excludedOrganizationData.getUpdatedAt(),
        excludedOrganizationData.getLastUpdatedBy(),
        excludedOrganizationData.getCreatedUserId(),
        excludedOrganizationData.getChargeGlAccount(),
        excludedOrganizationData.getReleaseEmpName(),
        excludedOrganizationData.getOnceAuth(),
        excludedOrganizationData.getCheckerDtStamp(),
        excludedOrganizationData.getCheckerId(),
        excludedOrganizationData.getMakerDtStamp(),
        excludedOrganizationData.getMakerId(),
        excludedOrganizationData.getModNo(),
        excludedOrganizationData.getAuthStat(),
        excludedOrganizationData.geteRateMax(),
        excludedOrganizationData.getcCreatedDate(),
        excludedOrganizationData.getExtensionDtString(),
        excludedOrganizationData.getContractNumber(),
        excludedOrganizationData.getCifNumber(),
        excludedOrganizationData.getFcName(),
        excludedOrganizationData.getcCreateDate(),
        excludedOrganizationData.getCreatedAt(),
        excludedOrganizationData.getCountryRegNumber(),
        excludedOrganizationData.getAdditionInfo(),
        contractId
    );
    LOGGER.info("#### SUCCESSFULLY UPDATED ORGANIZATION REQUEST EXCLUSIVE DATA TO DATABASE.");
    return 0;
  }

  @Override
  public OrganizationSalary findById(EntityId entityId)
  {
    return null;
  }

  @Override
  public Collection<OrganizationSalary> findAll()
  {

    Collection<JdbcOrganizationSalary> jdbcOrgSalaryCollection = jdbcOrganizationSalaryRepository.getJdbcSalaryOrganizations();

    return toOrgSalaryCollection(jdbcOrgSalaryCollection);
  }

  @Override
  public Collection<OrganizationSalary> findAllByGivenDate(Date startDate, Date endDate)
  {
    Collection<JdbcOrganizationSalary> jdbcOrganizationSalaryCollection = jdbcOrganizationSalaryRepository.findAllByGivenDate(startDate, endDate);
    return toOrgSalaryCollection(jdbcOrganizationSalaryCollection);
  }

  @Override
  public Collection<OrganizationSalaryExcel> findAllExcel()
  {

    Collection<JdbcOrganizationSalaryExcel> jdbcOrgSalaryCollection = jdbcOrganizationSalaryRepository.getJdbcSalaryOrganizationsExcel();

    return toOrgSalaryCollectionExcel(jdbcOrgSalaryCollection);
  }

  @Override
  public boolean updateToConfirmSalary(String contractId, String state, String confirmedUser, Date confirmedDate)
  {
    return 1 == jdbcOrganizationSalaryRepository.updateToConfirm(contractId, state, confirmedUser, confirmedDate);
  }

  @Override
  public String getJdbcSalaryOrgLastContractId()
  {
    return jdbcOrganizationSalaryRepository.getJdbcSalaryOrgLastContractId();
  }

  private Collection<OrganizationSalary> toOrgSalaryCollection(Collection<JdbcOrganizationSalary> jdbcOrgSalaryCollection)
  {
    Collection<OrganizationSalary> orgSalaryCollection = new ArrayList<>();

    for (JdbcOrganizationSalary jdbcOrganizationSalary : jdbcOrgSalaryCollection)
    {
      orgSalaryCollection.add(toOrgSalary(jdbcOrganizationSalary));
    }
    return orgSalaryCollection;
  }

  private OrganizationSalary toOrgSalary(JdbcOrganizationSalary jdbcOrgSalary)
  {
    String id = jdbcOrgSalary.getContractid();
    String branchId = jdbcOrgSalary.getContractbranch();

    String organizationName = jdbcOrgSalary.getCname();
    String organizationNumber = jdbcOrgSalary.getOrganizationNumber();

    String cifNumber = jdbcOrgSalary.getCif();
    String state = jdbcOrgSalary.getRecordStat();

    LocalDateTime contractDate = jdbcOrgSalary.getContractdt();

    String assignee = jdbcOrgSalary.getMakerId();
    String confirmedUser = jdbcOrgSalary.getCheckerId();
    String processInstanceId = jdbcOrgSalary.getProcessInstanceId();

    OrganizationSalary orgSalary = new OrganizationSalary(OrganizationRequestId.valueOf(id));

    orgSalary.setAssignee(assignee);
    orgSalary.setOrganizationName(organizationName);
    orgSalary.setOrganizationNumber(organizationNumber);

    orgSalary.setContractDate(contractDate);
    orgSalary.setCifNumber(cifNumber);

    orgSalary.setBranchId(branchId);
    orgSalary.setState(state);

    orgSalary.setConfirmedUser(confirmedUser);
    orgSalary.setInstanceId(ProcessInstanceId.valueOf(processInstanceId));

    return orgSalary;
  }

  private Collection<OrganizationSalaryExcel> toOrgSalaryCollectionExcel(Collection<JdbcOrganizationSalaryExcel> jdbcOrgSalaryCollection)
  {
    Collection<OrganizationSalaryExcel> orgSalaryCollection = new ArrayList<>();

    for (JdbcOrganizationSalaryExcel jdbcOrganizationSalary : jdbcOrgSalaryCollection)
    {
      orgSalaryCollection.add(toOrgSalaryExcel(jdbcOrganizationSalary));
    }
    return orgSalaryCollection;
  }

  private OrganizationSalaryExcel toOrgSalaryExcel(JdbcOrganizationSalaryExcel jdbcOrgSalary)
  {
    String id = jdbcOrgSalary.getContractid();
    OrganizationSalaryExcel orgSalary = new OrganizationSalaryExcel(OrganizationRequestId.valueOf(id));
    orgSalary.setContractid(jdbcOrgSalary.getContractid());
    orgSalary.setContractnumber(jdbcOrgSalary.getContractnumber());
    orgSalary.setCif(jdbcOrgSalary.getCif());
    orgSalary.setContractbranch(jdbcOrgSalary.getContractbranch());
    orgSalary.setCname(jdbcOrgSalary.getCname());
    orgSalary.setFcname(jdbcOrgSalary.getFcname());
    orgSalary.setLovnumber(jdbcOrgSalary.getLovnumber());
    orgSalary.setCaccountid(jdbcOrgSalary.getCaccountid());
    orgSalary.setExposurecategoryCode(jdbcOrgSalary.getExposurecategoryCode());
    orgSalary.setExposurecategoryDescription(jdbcOrgSalary.getExposurecategoryDescription());
    orgSalary.setCcreatedt(jdbcOrgSalary.getCcreatedt());
    orgSalary.setHrcnt(jdbcOrgSalary.getHrcnt());
    orgSalary.setEmpname(jdbcOrgSalary.getEmpname());
    orgSalary.setEmpphone(jdbcOrgSalary.getEmpphone());
    orgSalary.setForm4001(jdbcOrgSalary.getForm4001());
    orgSalary.setContractdt(jdbcOrgSalary.getContractdt());
    orgSalary.setExpiredt(jdbcOrgSalary.getExpiredt());
    orgSalary.setMstartsalary(jdbcOrgSalary.getMstartsalary());
    orgSalary.setMendsalary(jdbcOrgSalary.getMendsalary());
    orgSalary.setArate(jdbcOrgSalary.getArate());
    orgSalary.setErate(jdbcOrgSalary.getErate());
    orgSalary.setCountryregnumber(jdbcOrgSalary.getCountryregnumber());
    orgSalary.setRegisternumber(jdbcOrgSalary.getRegisternumber());
    orgSalary.setExtensionDt(jdbcOrgSalary.getExtensionDt());
    orgSalary.setLeakage(jdbcOrgSalary.getLeakage());
    orgSalary.setCorporateType(jdbcOrgSalary.getCorporateType());
    orgSalary.setLastcontractno(jdbcOrgSalary.getLastcontractno());
    orgSalary.setSalarytranfee(jdbcOrgSalary.getSalarytranfee());
    orgSalary.setChargeglaccount(jdbcOrgSalary.getChargeglaccount());
    orgSalary.setIsSalaryLoan(jdbcOrgSalary.getIsSalaryLoan());
    orgSalary.setReleaseempname(jdbcOrgSalary.getReleaseempname());
    orgSalary.setAdditionInfo(jdbcOrgSalary.getAdditionInfo());
    orgSalary.setCorporaterank(jdbcOrgSalary.getCorporaterank());
    orgSalary.setRecordStat(jdbcOrgSalary.getRecordStat());
    orgSalary.setAuthStat(jdbcOrgSalary.getAuthStat());
    orgSalary.setOnceAuth(jdbcOrgSalary.getOnceAuth());
    orgSalary.setIntcond(jdbcOrgSalary.getIntcond());
    orgSalary.setErateMax(jdbcOrgSalary.getErateMax());
    orgSalary.setSday1(jdbcOrgSalary.getSday1());
    orgSalary.setSday2(jdbcOrgSalary.getSday2());
    orgSalary.setStime(jdbcOrgSalary.getStime());
    orgSalary.setCyear(jdbcOrgSalary.getCyear());
    orgSalary.setCextended(jdbcOrgSalary.getCextended());
    orgSalary.setCextendedDate(jdbcOrgSalary.getCextendedDate());
    orgSalary.setCextendyear(jdbcOrgSalary.getCextendyear());
    orgSalary.setCcreatedDate(jdbcOrgSalary.getCcreatedDate());
    orgSalary.setAdditionalInfo(jdbcOrgSalary.getAdditionalInfo());
    orgSalary.setDanregnumber(jdbcOrgSalary.getDanregnumber());
    orgSalary.setDistrict(jdbcOrgSalary.getDistrict());
    orgSalary.setOnlinesal(jdbcOrgSalary.getOnlinesal());
    orgSalary.setCreatedUserid(jdbcOrgSalary.getCreatedUserid());
    orgSalary.setCreatedAt(jdbcOrgSalary.getCreatedAt());
    orgSalary.setMakerId(jdbcOrgSalary.getMakerId());
    orgSalary.setMakerDtStamp(jdbcOrgSalary.getMakerDtStamp());
    orgSalary.setCheckerId(jdbcOrgSalary.getCheckerId());
    orgSalary.setCheckerDtStamp(jdbcOrgSalary.getCheckerDtStamp());
    orgSalary.setLastUpdatedBy(jdbcOrgSalary.getLastUpdatedBy());
    orgSalary.setUpdatedAt(jdbcOrgSalary.getUpdatedAt());
    orgSalary.setModNo(jdbcOrgSalary.getModNo());
    return orgSalary;
  }
}
