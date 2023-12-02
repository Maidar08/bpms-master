package mn.erin.domain.bpm.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import mn.erin.domain.aim.model.group.GroupId;
import mn.erin.domain.base.repository.Repository;
import mn.erin.domain.bpm.model.organization.ExcludedOrganizationData;
import mn.erin.domain.bpm.model.organization.FormDataOrganizationSalary;
import mn.erin.domain.bpm.model.organization.OrganizationSalary;
import mn.erin.domain.bpm.model.organization.OrganizationSalaryExcel;

/**
 * @author Bilguunbor
 */
public interface OrganizationSalaryRepository extends Repository<OrganizationSalary>
{
  /**
   * Creates Organization salary
   *
   * @param orgRequestId  Salary Organization request id
   * @param organizationName Registered Organization name
   * @param regNumber     Organization Register number
   * @param cif Registered Organization cif
   * @param contractDt Registered contract date
   * @param establishedDt Registered Organization Established date
   * @param branchNumber Registering user branch
   * @param assignee Registering userName
   * @return Returns
   * @throws BpmRepositoryException Repository Exception
   */
  OrganizationSalary create(String orgRequestId, String organizationName, String regNumber, String cif, LocalDateTime contractDt, LocalDate establishedDt, String state, String branchNumber, String assignee, String instanceId, Date createdDate)
      throws BpmRepositoryException;

  /**
   * Finds by organization state.
   *
   * @param firstState  firstState
   * @param secondState secondState
   * @return found requests.
   */
  Collection<OrganizationSalary> findByState(String firstState, String secondState);

  /**
   * Finds by organization date.
   *
   * @param startDate  startDate
   * @param endDate endDate
   * @return found requests.
   */
  Collection<OrganizationSalary> findAllByGivenDate(Date startDate, Date endDate);

  /**
   *
   * @param id  user group id
   * @return Returns all existing salary organization requests to a given user group ( Branch number)

   */
  Collection<OrganizationSalary> findByGroupId(GroupId id) throws BpmRepositoryException;

  /**
   *
   * @param regNumber  regNumber
   * @return Returns all existing salary organization requests
   */
  Collection<OrganizationSalary> findByRegNumber(String regNumber) throws BpmRepositoryException;

  /**
   *
   * @return Return salary organization last contractId
   */
  String getJdbcSalaryOrgLastContractId();

  /**
   *
   * @param contractId  contractId
   * @param state  state
   * @return Return boolean
   */
  boolean update(String contractId, String state) throws BpmRepositoryException;
  /**
   *
   * @return Returns all existing salary organization requests
   */
  Collection<OrganizationSalaryExcel> findAllExcel();

  /**
   *
   * @param contractId contract id
   * @param state request state
   * @param confirmedUser request confirmed user
   * @param confirmedDate request confirmed date
   * @return
   */
  boolean updateToConfirmSalary(String contractId, String state, String confirmedUser, Date confirmedDate);



  /**
   * Update registration form parameters into table column.
   *
   * @param formDataOrganizationSalary Form data from salary organization registration.
   */
  int updateSalaryOrganizationRequest(FormDataOrganizationSalary formDataOrganizationSalary);

  /**
   *
   * @param contractId contract id
   * @param makerId makerId
   * @param updateState updateState
   * @return
   */
  boolean updateToConfirmByDirector(String contractId, String makerId, Date makerDate, String updateState);

  /**
   * Updates exclusive data to database.
   *
   * @param excludedOrganizationData Not used data.
   * @return update.
   */
  int updateSalaryOrganizationExcluded(ExcludedOrganizationData excludedOrganizationData, String contractId);
}
