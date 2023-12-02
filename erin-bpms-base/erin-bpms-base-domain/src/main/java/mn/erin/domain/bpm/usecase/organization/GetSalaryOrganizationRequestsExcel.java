package mn.erin.domain.bpm.usecase.organization;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.Validate;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.organization.OrganizationRequestExcel;
import mn.erin.domain.bpm.model.organization.OrganizationRequestId;
import mn.erin.domain.bpm.model.organization.OrganizationSalaryExcel;
import mn.erin.domain.bpm.repository.OrganizationSalaryRepository;

/**
 * @author Tamir
 */
public class GetSalaryOrganizationRequestsExcel extends AbstractUseCase<Void, GetAllOrganizationRequestsOutputExcel>
{
  private final OrganizationSalaryRepository organizationSalaryRepository;

  public GetSalaryOrganizationRequestsExcel(OrganizationSalaryRepository organizationSalaryRepository)
  {
    super();
    this.organizationSalaryRepository = Validate.notNull(organizationSalaryRepository);
  }

  @Override
  public GetAllOrganizationRequestsOutputExcel execute(Void input) throws UseCaseException
  {
    Collection<OrganizationSalaryExcel> allOrgSalary = organizationSalaryRepository.findAllExcel();

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
      organizationRequest.setContractid(extendedRequest.getContractid());
      organizationRequest.setContractnumber(extendedRequest.getContractnumber());
      organizationRequest.setCif(extendedRequest.getCif());
      organizationRequest.setContractbranch(extendedRequest.getContractbranch());
      organizationRequest.setCname(extendedRequest.getCname());
      organizationRequest.setFcname(extendedRequest.getFcname());
      organizationRequest.setLovnumber(extendedRequest.getLovnumber());
      organizationRequest.setCaccountid(extendedRequest.getCaccountid());
      organizationRequest.setExposurecategoryCode(extendedRequest.getExposurecategoryCode());
      organizationRequest.setExposurecategoryDescription(extendedRequest.getExposurecategoryDescription());
      organizationRequest.setCcreatedt(extendedRequest.getCcreatedt());
      organizationRequest.setHrcnt(extendedRequest.getHrcnt());
      organizationRequest.setEmpname(extendedRequest.getEmpname());
      organizationRequest.setEmpphone(extendedRequest.getEmpphone());
      organizationRequest.setForm4001(extendedRequest.getForm4001());
      organizationRequest.setContractdt(extendedRequest.getContractdt());
      organizationRequest.setExpiredt(extendedRequest.getExpiredt());
      organizationRequest.setMstartsalary(extendedRequest.getMstartsalary());
      organizationRequest.setMendsalary(extendedRequest.getMendsalary());
      organizationRequest.setArate(extendedRequest.getArate());
      organizationRequest.setErate(extendedRequest.getErate());
      organizationRequest.setCountryregnumber(extendedRequest.getCountryregnumber());
      organizationRequest.setRegisternumber(extendedRequest.getRegisternumber());
      organizationRequest.setExtensionDt(extendedRequest.getExtensionDt());
      organizationRequest.setLeakage(extendedRequest.getLeakage());
      organizationRequest.setCorporateType(extendedRequest.getCorporateType());
      organizationRequest.setLastcontractno(extendedRequest.getLastcontractno());
      organizationRequest.setSalarytranfee(extendedRequest.getSalarytranfee());
      organizationRequest.setChargeglaccount(extendedRequest.getChargeglaccount());
      organizationRequest.setIsSalaryLoan(extendedRequest.getIsSalaryLoan());
      organizationRequest.setReleaseempname(extendedRequest.getReleaseempname());
      organizationRequest.setAdditionInfo(extendedRequest.getAdditionInfo());
      organizationRequest.setCorporaterank(extendedRequest.getCorporaterank());
      organizationRequest.setRecordStat(extendedRequest.getRecordStat());
      organizationRequest.setAuthStat(extendedRequest.getAuthStat());
      organizationRequest.setOnceAuth(extendedRequest.getOnceAuth());
      organizationRequest.setIntcond(extendedRequest.getIntcond());
      organizationRequest.setErateMax(extendedRequest.getErateMax());
      organizationRequest.setSday1(extendedRequest.getSday1());
      organizationRequest.setSday2(extendedRequest.getSday2());
      organizationRequest.setStime(extendedRequest.getStime());
      organizationRequest.setCyear(extendedRequest.getCyear());
      organizationRequest.setCextended(extendedRequest.getCextended());
      organizationRequest.setCextendedDate(extendedRequest.getCextendedDate());
      organizationRequest.setCextendyear(extendedRequest.getCextendyear());
      organizationRequest.setCcreatedDate(extendedRequest.getCcreatedDate());
      organizationRequest.setAdditionalInfo(extendedRequest.getAdditionalInfo());
      organizationRequest.setDanregnumber(extendedRequest.getDanregnumber());
      organizationRequest.setDistrict(extendedRequest.getDistrict());
      organizationRequest.setOnlinesal(extendedRequest.getOnlinesal());
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
