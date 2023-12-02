package mn.erin.domain.bpm.usecase.organization;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.organization.OrganizationLeasingExcel;
import mn.erin.domain.bpm.model.organization.OrganizationRequestExcel;
import mn.erin.domain.bpm.model.organization.OrganizationRequestId;
import mn.erin.domain.bpm.repository.OrganizationLeasingRepository;

/**
 * @author Sukhbaatar
 */
public class GetLeasingOrganizationRequestsExcel extends AbstractUseCase<Void, GetAllOrganizationRequestsOutputExcel>
{
  private final OrganizationLeasingRepository organizationLeasingRepository;

  public GetLeasingOrganizationRequestsExcel(OrganizationLeasingRepository organizationLeasingRepository)
  {
    super();
    this.organizationLeasingRepository = Validate.notNull(organizationLeasingRepository);
  }

  @Override
  public GetAllOrganizationRequestsOutputExcel execute(Void input) throws UseCaseException
  {
    Collection<OrganizationLeasingExcel> allOrgSalary = organizationLeasingRepository.findAllExcel();

    Collection<OrganizationRequestExcel> organizationRequests = new ArrayList<>();

    organizationRequests.addAll(toOrganizationRequests(allOrgSalary));

    return new GetAllOrganizationRequestsOutputExcel(organizationRequests);
  }

  private Collection<? extends OrganizationRequestExcel> toOrganizationRequests(Collection<? extends OrganizationRequestExcel> extendedRequests)
  {
    Collection<OrganizationRequestExcel> organizationRequests = new ArrayList<>();

    for (OrganizationRequestExcel extendedRequest : extendedRequests)
    {
      OrganizationRequestId orgRequestId = extendedRequest.getId();
      OrganizationRequestExcel organizationRequest = new OrganizationRequestExcel(OrganizationRequestId.valueOf(String.valueOf(orgRequestId)));
      organizationRequest.setContractbranch(extendedRequest.getContractbranch());
      organizationRequest.setRegisternumber(extendedRequest.getRegisternumber());
      organizationRequest.setName(extendedRequest.getName());
      organizationRequest.setContractid(extendedRequest.getContractid());
      organizationRequest.setContractdt(extendedRequest.getContractdt());
      organizationRequest.setCyear(extendedRequest.getCyear());
      organizationRequest.setExpiredt(extendedRequest.getExpiredt());
      organizationRequest.setFee(extendedRequest.getFee());
      organizationRequest.setLastcontractno(extendedRequest.getLastcontractno());
      organizationRequest.setCusttype(extendedRequest.getCusttype());
      organizationRequest.setExposurecategoryCode(extendedRequest.getExposurecategoryCode());
      organizationRequest.setExposurecategoryDescription(extendedRequest.getExposurecategoryDescription());
      organizationRequest.setCif(extendedRequest.getCif());
      organizationRequest.setCountryregnumber(extendedRequest.getCountryregnumber());
      organizationRequest.setBirthdt(extendedRequest.getBirthdt());
      organizationRequest.setAddress(extendedRequest.getAddress());
      organizationRequest.setPhone(extendedRequest.getPhone());
      organizationRequest.setMail(extendedRequest.getMail());
      organizationRequest.setProductcat(extendedRequest.getProductcat());
      organizationRequest.setProductdesc(extendedRequest.getProductdesc());
      organizationRequest.setContactname(extendedRequest.getContactname());
      organizationRequest.setContactphone(extendedRequest.getContactphone());
      organizationRequest.setContactemail(extendedRequest.getContactemail());
      organizationRequest.setContactdesc(extendedRequest.getContactdesc());
      organizationRequest.setChargetype(extendedRequest.getChargetype());
      organizationRequest.setChargeamount(extendedRequest.getChargeamount());
      organizationRequest.setLoanamount(extendedRequest.getLoanamount());
      organizationRequest.setSettlementdate(extendedRequest.getSettlementdate());
      organizationRequest.setSettlementpercent(extendedRequest.getSettlementpercent());
      organizationRequest.setSettlementaccount(extendedRequest.getSettlementaccount());
      organizationRequest.setCondition(extendedRequest.getCondition());
      organizationRequest.setRate(extendedRequest.getRate());
      organizationRequest.setDischarge(extendedRequest.getDischarge());
      organizationRequest.setLeasing(extendedRequest.getLeasing());
      organizationRequest.setBnpl(extendedRequest.getBnpl());
      organizationRequest.setTerminalid(extendedRequest.getTerminalid());
      organizationRequest.setCextendyear(extendedRequest.getCextendyear());
      organizationRequest.setCextended(extendedRequest.getCextended());
      organizationRequest.setCextendedDate(extendedRequest.getCextendedDate());
      organizationRequest.setRecordStat(extendedRequest.getRecordStat());
      organizationRequest.setCreatedUserid(extendedRequest.getCreatedUserid());
      organizationRequest.setCreatedAt(extendedRequest.getCreatedAt());
      organizationRequest.setMakerId(extendedRequest.getMakerId());
      organizationRequest.setMakerDtStamp(extendedRequest.getMakerDtStamp());
      organizationRequest.setCheckerId(extendedRequest.getCheckerId());
      organizationRequest.setCheckerDtStamp(extendedRequest.getCheckerDtStamp());
      organizationRequest.setLastUpdatedBy(extendedRequest.getLastUpdatedBy());
      organizationRequest.setUpdatedAt(extendedRequest.getUpdatedAt());
      organizationRequest.setModNo(extendedRequest.getModNo());
      organizationRequests.add(organizationRequest);
    }

    return organizationRequests;
  }
}
