package mn.erin.bpm.repository.jdbc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcOrganizationLeasingRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcOrganizationLeasing;
import mn.erin.bpm.repository.jdbc.model.JdbcOrganizationLeasingExcel;
import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.bpm.model.organization.FormDataOrganizationLeasing;
import mn.erin.domain.bpm.model.organization.OrganizationLeasing;
import mn.erin.domain.bpm.model.organization.OrganizationLeasingExcel;
import mn.erin.domain.bpm.model.organization.OrganizationRequestId;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;

/**
 * @author Bilguunbor
 */
@Repository
public class DefaultJdbcOrganizationLeasingRepository implements OrganizationLeasingRepository
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJdbcOrganizationLeasingRepository.class);
  private final JdbcOrganizationLeasingRepository jdbcOrganizationLeasingRepository;

  public DefaultJdbcOrganizationLeasingRepository(JdbcOrganizationLeasingRepository jdbcOrganizationLeasingRepository)
  {
    this.jdbcOrganizationLeasingRepository = jdbcOrganizationLeasingRepository;
  }

  @Override
  public OrganizationLeasing create(String orgRequestId, String organizationName, String regNumber, String cif,
      LocalDateTime contractDt, LocalDate establishedDt, String state, String branchNumber, String assignee, String instanceId, Date createdDate)
      throws BpmRepositoryException
  {

    jdbcOrganizationLeasingRepository
        .insert(orgRequestId, organizationName, regNumber, cif, contractDt, establishedDt, state, branchNumber, assignee, instanceId, createdDate);

    return new OrganizationLeasing(new OrganizationRequestId(orgRequestId));
  }

  @Override
  public Collection<OrganizationLeasing> findByState(String firstState, String secondState)
  {
    Collection<JdbcOrganizationLeasing> jdbcOrgLeasingCollection = jdbcOrganizationLeasingRepository.getJdbcLeasingOrganizations();

    return toOrgLeasingCollection(jdbcOrgLeasingCollection);
  }

  @Override
  public Collection<OrganizationLeasing> findByGroupId(GroupId groupId) throws BpmRepositoryException
  {
    try
    {
      Validate.notNull(groupId, "Group id can not be null");
      List<OrganizationLeasing> leasingOrganizationRequestsReturn = new ArrayList<>();
      List<JdbcOrganizationLeasing> jdbcLeasingOrganizations = jdbcOrganizationLeasingRepository
          .getJdbcLeasingOrganizationsByGroupId(groupId.getId());

      for (JdbcOrganizationLeasing jdbcOrganizationLeasing : jdbcLeasingOrganizations)
      {
        leasingOrganizationRequestsReturn.add(toOrgLeasing(jdbcOrganizationLeasing));
      }
      return leasingOrganizationRequestsReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public boolean update(String contractId, String state) throws BpmRepositoryException
  {
    return 1 == jdbcOrganizationLeasingRepository.updateParameter(contractId, state);
  }

  @Override
  public OrganizationLeasing findById(EntityId entityId)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<OrganizationLeasing> findAll()
  {
    Collection<JdbcOrganizationLeasing> jdbcOrganizationLeasingCollection = jdbcOrganizationLeasingRepository.getJdbcLeasingOrganizations();

    return toOrgLeasingCollection(jdbcOrganizationLeasingCollection);
  }


  @Override
  public Collection<OrganizationLeasing> findAllByGivenDate(Date startDate, Date endDate)
  {
    Collection<JdbcOrganizationLeasing> jdbcOrganizationLeasingCollection = jdbcOrganizationLeasingRepository.findAllByGivenDate(startDate, endDate);
    return toOrgLeasingCollection(jdbcOrganizationLeasingCollection);
  }

  @Override
  public Collection<OrganizationLeasingExcel> findAllExcel()
  {
    Collection<JdbcOrganizationLeasingExcel> jdbcOrganizationLeasingCollection = jdbcOrganizationLeasingRepository.getJdbcLeasingOrganizationsExcel();

    return toOrgLeasingCollectionExcel(jdbcOrganizationLeasingCollection);
  }

  @Override
  public int updateLeasingOrganizationRequest(FormDataOrganizationLeasing formDataOrganizationLeasing)
  {
     int numberOfColumnsUpdated =  jdbcOrganizationLeasingRepository.updateLeasingRequest(formDataOrganizationLeasing.getContractPeriod(),
        formDataOrganizationLeasing.getContractFee(),
        formDataOrganizationLeasing.getPartnerType(), formDataOrganizationLeasing.getPartnerDirection(), formDataOrganizationLeasing.getProductSuppliedType(),
        formDataOrganizationLeasing.getProductSuppliedDescription(), formDataOrganizationLeasing.getContactName(),
        formDataOrganizationLeasing.getContactPhoneNumber(), formDataOrganizationLeasing.getContactEmail(),
        formDataOrganizationLeasing.getContactDescription(), formDataOrganizationLeasing.getFeeSupplierDueDate(),
        formDataOrganizationLeasing.getFeeSupplierAmountPercent(),
        formDataOrganizationLeasing.getFeeAccountNumber(), formDataOrganizationLeasing.getFeeType(), formDataOrganizationLeasing.getFeeAmountPercent(),
        formDataOrganizationLeasing.getFeeLoanOriginationFee(), formDataOrganizationLeasing.getFeeOnlineBnpl(), formDataOrganizationLeasing.getFeeTerminalId(),
        formDataOrganizationLeasing.getContractExtensionYear(), formDataOrganizationLeasing.getContractHasExtension(),
        formDataOrganizationLeasing.getContractExtensionNewDate(), formDataOrganizationLeasing.getFeePaymentType(),
        formDataOrganizationLeasing.getFeePercentAmount(), formDataOrganizationLeasing.getFeeLoanAmount(),
        formDataOrganizationLeasing.getContractId(), formDataOrganizationLeasing.getContractDate(), formDataOrganizationLeasing.getContractEndDate(),
        formDataOrganizationLeasing.getContractOldNumber(),
        formDataOrganizationLeasing.getPartnerCif(), formDataOrganizationLeasing.getPartnerRegistryId(),
        formDataOrganizationLeasing.getPartnerEstablishedDate(), formDataOrganizationLeasing.getPartnerAddress(),
        formDataOrganizationLeasing.getPartnerPhoneNumber(), formDataOrganizationLeasing.getPartnerEmail(), formDataOrganizationLeasing.getCreatedDate()
    );

    LOGGER.info("#### SUCCESSFULLY UPDATED ORGANIZATION REQUEST REGISTRATION FORM DATA.");
    return numberOfColumnsUpdated;
  }

  @Override
  public boolean updateToConfirmLeasing(String contractId, String state, String confirmedUser, Date confirmedDate)
  {
    if (jdbcOrganizationLeasingRepository.updateParameter(contractId, state) == 1)
    {
      LOGGER.info("#### SUCCESSFULLY UPDATED ORGANIZATION REQUEST STATE TO {}, WITH CONTRACT ID = {}", state, contractId);
    }
    return 1 == jdbcOrganizationLeasingRepository.updateToConfirm(contractId, state, confirmedUser, confirmedDate);
  }

  @Override
  public boolean updateToConfirmByDirector(String contractId, String makerId, Date makerDate, String updateState)
  {
    return 1 == jdbcOrganizationLeasingRepository.updateToConfirmByDirector(contractId, makerId, makerDate, updateState);
  }

  @Override
  public String getNameByTerminalId(String terminalId)
  {
    return jdbcOrganizationLeasingRepository.getNameByTerminalId(terminalId);
  }

  @Override
  public Collection<OrganizationLeasing> findByRegNumber(String regNumber) throws BpmRepositoryException
  {
    try
    {
      Collection<OrganizationLeasing> leasingOrganizationReturn = new ArrayList<>();
      Collection<JdbcOrganizationLeasing> jdbcLeasingOrganizations = jdbcOrganizationLeasingRepository.findByRequestId(regNumber);

      for (JdbcOrganizationLeasing jdbcLeasingOrganization : jdbcLeasingOrganizations)
      {
        leasingOrganizationReturn.add(toOrgLeasing(jdbcLeasingOrganization));
      }
      return leasingOrganizationReturn;
    }
    catch (Exception e)
    {
      throw new BpmRepositoryException(e.getMessage(), e);
    }
  }

  @Override
  public String getJdbcLeasingOrgLastContractId()
  {
    String contractId = jdbcOrganizationLeasingRepository.getJdbcLeasingOrgLastContractId();

    return contractId;
  }

  private Collection<OrganizationLeasing> toOrgLeasingCollection(Collection<JdbcOrganizationLeasing> jdbcOrgLeasings)
  {
    Collection<OrganizationLeasing> organizationLeasings = new ArrayList<>();

    for (JdbcOrganizationLeasing jdbcOrgLeasing : jdbcOrgLeasings)
    {
      organizationLeasings.add(toOrgLeasing(jdbcOrgLeasing));
    }
    return organizationLeasings;
  }

  private OrganizationLeasing toOrgLeasing(JdbcOrganizationLeasing jdbcOrgLeasing)
  {
    String id = jdbcOrgLeasing.getContractnumber();
    String branchId = jdbcOrgLeasing.getContractbranch();

    String organizationName = jdbcOrgLeasing.getName();
    String organizationNumber = jdbcOrgLeasing.getOrganizationNumber();

    String cifNumber = jdbcOrgLeasing.getCif();
    String state = jdbcOrgLeasing.getRecordStat();

    LocalDateTime contractDate = jdbcOrgLeasing.getContractdt();

    String assignee = jdbcOrgLeasing.getMakerId();
    String confirmedUser = jdbcOrgLeasing.getCheckerId();
    String instanceId = jdbcOrgLeasing.getProcessInstanceId();

    OrganizationLeasing orgLeasing = new OrganizationLeasing(OrganizationRequestId.valueOf(id));

    orgLeasing.setAssignee(assignee);
    orgLeasing.setOrganizationName(organizationName);
    orgLeasing.setOrganizationNumber(organizationNumber);

    orgLeasing.setContractDate(contractDate);
    orgLeasing.setCifNumber(cifNumber);

    orgLeasing.setBranchId(branchId);
    orgLeasing.setState(state);

    orgLeasing.setConfirmedUser(confirmedUser);
    orgLeasing.setInstanceId(ProcessInstanceId.valueOf(instanceId));

    return orgLeasing;
  }

  private Collection<OrganizationLeasingExcel> toOrgLeasingCollectionExcel(Collection<JdbcOrganizationLeasingExcel> jdbcOrgLeasings)
  {
    Collection<OrganizationLeasingExcel> organizationLeasings = new ArrayList<>();

    for (JdbcOrganizationLeasingExcel jdbcOrgLeasing : jdbcOrgLeasings)
    {
      organizationLeasings.add(toOrgLeasingExcel(jdbcOrgLeasing));
    }
    return organizationLeasings;
  }

  private OrganizationLeasingExcel toOrgLeasingExcel(JdbcOrganizationLeasingExcel jdbcOrgLeasing)
  {
    OrganizationLeasingExcel orgLeasing = new OrganizationLeasingExcel(OrganizationRequestId.valueOf(jdbcOrgLeasing.getRegisternumber()));
    orgLeasing.setContractbranch(jdbcOrgLeasing.getContractbranch());
    orgLeasing.setRegisternumber(jdbcOrgLeasing.getRegisternumber());
    orgLeasing.setName(jdbcOrgLeasing.getName());
    orgLeasing.setContractid(jdbcOrgLeasing.getContractid());
    orgLeasing.setContractdt(jdbcOrgLeasing.getContractdt());
    orgLeasing.setCyear(jdbcOrgLeasing.getCyear());
    orgLeasing.setExpiredt(jdbcOrgLeasing.getExpiredt());
    orgLeasing.setFee(jdbcOrgLeasing.getFee());
    orgLeasing.setLastcontractno(jdbcOrgLeasing.getLastcontractno());
    orgLeasing.setCusttype(jdbcOrgLeasing.getCusttype());
    orgLeasing.setExposurecategoryCode(jdbcOrgLeasing.getExposurecategoryCode());
    orgLeasing.setExposurecategoryDescription(jdbcOrgLeasing.getExposurecategoryDescription());
    orgLeasing.setCif(jdbcOrgLeasing.getCif());
    orgLeasing.setCountryregnumber(jdbcOrgLeasing.getCountryregnumber());
    orgLeasing.setBirthdt(jdbcOrgLeasing.getBirthdt());
    orgLeasing.setAddress(jdbcOrgLeasing.getAddress());
    orgLeasing.setPhone(jdbcOrgLeasing.getPhone());
    orgLeasing.setMail(jdbcOrgLeasing.getMail());
    orgLeasing.setProductcat(jdbcOrgLeasing.getProductcat());
    orgLeasing.setProductdesc(jdbcOrgLeasing.getProductdesc());
    orgLeasing.setContactname(jdbcOrgLeasing.getContactname());
    orgLeasing.setContactphone(jdbcOrgLeasing.getContactphone());
    orgLeasing.setContactemail(jdbcOrgLeasing.getContactemail());
    orgLeasing.setContactdesc(jdbcOrgLeasing.getContactdesc());
    orgLeasing.setChargetype(jdbcOrgLeasing.getChargetype());
    orgLeasing.setChargeamount(jdbcOrgLeasing.getChargeamount());
    orgLeasing.setLoanamount(jdbcOrgLeasing.getLoanamount());
    orgLeasing.setSettlementdate(jdbcOrgLeasing.getSettlementdate());
    orgLeasing.setSettlementpercent(jdbcOrgLeasing.getSettlementpercent());
    orgLeasing.setSettlementaccount(jdbcOrgLeasing.getSettlementaccount());
    orgLeasing.setCondition(jdbcOrgLeasing.getCondition());
    orgLeasing.setRate(jdbcOrgLeasing.getRate());
    orgLeasing.setDischarge(jdbcOrgLeasing.getDischarge());
    orgLeasing.setLeasing(jdbcOrgLeasing.getLeasing());
    orgLeasing.setBnpl(jdbcOrgLeasing.getBnpl());
    orgLeasing.setTerminalid(jdbcOrgLeasing.getTerminalid());
    orgLeasing.setCextendyear(jdbcOrgLeasing.getCextendyear());
    orgLeasing.setCextended(jdbcOrgLeasing.getCextended());
    orgLeasing.setCextendedDate(jdbcOrgLeasing.getCextendedDate());
    orgLeasing.setRecordStat(jdbcOrgLeasing.getRecordStat());
    orgLeasing.setCreatedUserid(jdbcOrgLeasing.getCreatedUserid());
    orgLeasing.setCreatedAt(jdbcOrgLeasing.getCreatedAt());
    orgLeasing.setMakerId(jdbcOrgLeasing.getMakerId());
    orgLeasing.setMakerDtStamp(jdbcOrgLeasing.getMakerDtStamp());
    orgLeasing.setCheckerId(jdbcOrgLeasing.getCheckerId());
    orgLeasing.setCheckerDtStamp(jdbcOrgLeasing.getCheckerDtStamp());
    orgLeasing.setLastUpdatedBy(jdbcOrgLeasing.getLastUpdatedBy());
    orgLeasing.setUpdatedAt(jdbcOrgLeasing.getUpdatedAt());
    orgLeasing.setModNo(jdbcOrgLeasing.getModNo());
    return orgLeasing;
  }
}
