package mn.erin.domain.bpm.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.organization.FormDataOrganizationLeasing;
import mn.erin.domain.bpm.model.organization.OrganizationLeasing;
import mn.erin.domain.bpm.model.organization.OrganizationLeasingExcel;

/**
 * @author Bilguunbor
 */
public interface OrganizationLeasingRepository extends Repository<OrganizationLeasing>
{
  /**
   * Creates Organization leasing
   *
   * @param orgRequestId     Leasing Organization Request id
   * @param organizationName Registered Organization type
   * @param regNumber        Organization Register number
   * @param cif              Registered Organization cif
   * @param contractDt       Registered contract date
   * @param establishedDt    Registered Organization Established date
   * @param branchNumber     Registering user branch
   * @param assignee         Registering userName
   * @return returns
   * @throws BpmRepositoryException Throws Repository exception
   */
  OrganizationLeasing create(String orgRequestId, String organizationName, String regNumber, String cif, LocalDateTime contractDt, LocalDate establishedDt,
      String state, String branchNumber, String assignee, String instanceId, Date createdDate)
      throws BpmRepositoryException;

  /**
   * Finds by organization state.
   *
   * @param firstState  firstState
   * @param secondState secondState
   * @return found requests.
   */
  Collection<OrganizationLeasing> findByState(String firstState, String secondState);

  /**
   * @param id User group id
   * @return Return all existing leasing organization request to a given user group
   */
  Collection<OrganizationLeasing> findByGroupId(GroupId id) throws BpmRepositoryException;

  /**
   * Finds by organization date.
   *
   * @param startDate  startDate
   * @param endDate endDate
   * @return found requests.
   */
  Collection<OrganizationLeasing> findAllByGivenDate(Date startDate, Date endDate);

  /**
   * @param contractId contractId
   * @param state      state
   * @return Return boolean
   */
  boolean update(String contractId, String state) throws BpmRepositoryException;

  /**
   * Find name by terminal id.
   *
   * @param terminalId terminalId
   * @return found requests.
   */
  String getNameByTerminalId(String terminalId);

  /**
   * @param regNumber regNumber
   * @return Returns all existing leasing organization requests
   */
  Collection<OrganizationLeasing> findByRegNumber(String regNumber) throws BpmRepositoryException;

  /**
   * @return Return salary organization last contractId
   */
  String getJdbcLeasingOrgLastContractId();

  /**
   * @return Return all existing leasing organization request
   */
  Collection<OrganizationLeasingExcel> findAllExcel();

  /**
   * Update registration form parameters into table column.
   *
   * @param formDataOrganizationLeasing Form data from leasing organization registration.
   */
  int updateLeasingOrganizationRequest(FormDataOrganizationLeasing formDataOrganizationLeasing);

  /**
   * @param contractId    contract id
   * @param state         request state
   * @param confirmedUser request confirmed user
   * @param confirmedDate request confirmed date
   * @return
   */
  boolean updateToConfirmLeasing(String contractId, String state, String confirmedUser, Date confirmedDate);

  /**
   * @param contractId  contract id
   * @param makerId     makerId
   * @param updateState updateState
   * @return
   */
  boolean updateToConfirmByDirector(String contractId, String makerId, Date makerDate, String updateState);
}
