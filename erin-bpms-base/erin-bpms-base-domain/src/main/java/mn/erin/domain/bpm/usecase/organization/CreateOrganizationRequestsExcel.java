package mn.erin.domain.bpm.usecase.organization;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.MessageConstants;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.organization.OrganizationRequestExcel;

import static mn.erin.domain.bpm.BpmModuleConstants.LEASING_ORGANIZATION_REQUEST_TABLE_HEADER;
import static mn.erin.domain.bpm.BpmModuleConstants.ORGANIZATION_TYPE_SALARY;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_ORGANIZATION_REQUEST_TABLE_HEADER;

/**
 * @author Bilguunbor
 */
public class CreateOrganizationRequestsExcel extends AuthorizedUseCase<CreateOrganizationRequestsExcelInput, byte[]>
{
  private static final Permission permission = new BpmModulePermission("GetOrganizationRequestsByAssignedUserId");

  public CreateOrganizationRequestsExcel(AuthorizationService authorizationService, AuthenticationService authenticationService)
  {
    super(authenticationService, authorizationService);
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected byte[] executeImpl(CreateOrganizationRequestsExcelInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(MessageConstants.INPUT_REQUIRED_CODE, MessageConstants.INPUT_REQUIRED);
    }

    String topHeader = input.getTopHeader();
    String searchKey = input.getSearchKey();
    String groupId = input.getGroupId();
    String stringDate = input.getStringDate();

    validateInput(topHeader, searchKey, groupId, stringDate);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    String title = BpmModuleConstants.ORGANIZATION_REQUEST_TYPE.get(ORGANIZATION_TYPE_SALARY);

    List<OrganizationRequestExcel> orgRequests = input.getOrganizationRequests();

    if (!orgRequests.isEmpty())
    {
      String sheetTitle = title + " " + stringDate;
      boolean isSalary = topHeader.equals("salary");
      exportToExcelFile(orgRequests, outputStream, sheetTitle, isSalary);
      return outputStream.toByteArray();
    }

    return new byte[0];
  }

  private void validateInput(String topHeader, String searchKey, String groupId, String stringDate) throws UseCaseException
  {
    if (StringUtils.isBlank(topHeader))
    {
      throw new UseCaseException(MessageConstants.NULL_PROCESS_REQUEST_TYPE_CODE, MessageConstants.NULL_PROCESS_REQUEST_TYPE);
    }

    if (StringUtils.isBlank(searchKey))
    {
      throw new UseCaseException(MessageConstants.NULL_PROCESS_REQUEST_TYPE_CODE, MessageConstants.NULL_PROCESS_REQUEST_TYPE);
    }

    if (StringUtils.isBlank(groupId))
    {
      throw new UseCaseException(MessageConstants.NULL_GROUP_ID_CODE, MessageConstants.NULL_GROUP_ID);
    }

    if (StringUtils.isBlank(stringDate))
    {
      throw new UseCaseException(MessageConstants.NULL_DATE_CODE, MessageConstants.NULL_DATE);
    }
  }

  private void exportToExcelFile(List<OrganizationRequestExcel> orgRequests, OutputStream outputStream, String sheetTitle, boolean isSalary)
  {
    Object[][] objects = new Object[orgRequests.size()][];
    int rowIndex = 0;

    if (isSalary)
    {
      for (OrganizationRequestExcel orgRequest : orgRequests)
      {
        List<Object> rowData = new ArrayList<>();

        rowData.add(rowIndex + 1);
        rowData.add(orgRequest.getRegisternumber());
        rowData.add(orgRequest.getContractid());
        rowData.add(orgRequest.getContractnumber());
        rowData.add(orgRequest.getCif());
        rowData.add(orgRequest.getContractbranch());
        rowData.add(orgRequest.getCname());
        rowData.add(orgRequest.getFcname());
        rowData.add(orgRequest.getLovnumber());
        rowData.add(orgRequest.getCaccountid());
        rowData.add(orgRequest.getExposurecategoryCode());
        rowData.add(orgRequest.getExposurecategoryDescription());
        rowData.add(getDateString(orgRequest.getCcreatedt()));
        rowData.add(orgRequest.getHrcnt());
        rowData.add(orgRequest.getEmpname());
        rowData.add(orgRequest.getEmpphone());
        rowData.add(orgRequest.getForm4001());
        rowData.add(getDateString(orgRequest.getContractdt()));
        rowData.add(getDateString(orgRequest.getExpiredt()));
        rowData.add(orgRequest.getMstartsalary());
        rowData.add(orgRequest.getMendsalary());
        rowData.add(orgRequest.getArate());
        rowData.add(orgRequest.getErate());
        rowData.add(orgRequest.getCountryregnumber());
        rowData.add(getDateString(orgRequest.getExtensionDt()));
        rowData.add(orgRequest.getLeakage());
        rowData.add(orgRequest.getCorporateType());
        rowData.add(orgRequest.getLastcontractno());
        rowData.add(orgRequest.getSalarytranfee());
        rowData.add(orgRequest.getChargeglaccount());
        rowData.add(orgRequest.getIsSalaryLoan());
        rowData.add(orgRequest.getReleaseempname());
        rowData.add(orgRequest.getAdditionInfo());
        rowData.add(orgRequest.getCorporaterank());
        rowData.add(orgRequest.getRecordStat());
        rowData.add(orgRequest.getAuthStat());
        rowData.add(orgRequest.getOnceAuth());
        rowData.add(orgRequest.getIntcond());
        rowData.add(orgRequest.getErateMax());
        rowData.add(orgRequest.getSday1());
        rowData.add(orgRequest.getSday2());
        rowData.add(orgRequest.getStime());
        rowData.add(orgRequest.getCyear());
        rowData.add(orgRequest.getCextended());
        rowData.add(getDateString(orgRequest.getCextendedDate()));
        rowData.add(orgRequest.getCextendyear());
        rowData.add(getDateString(orgRequest.getCcreatedDate()));
        rowData.add(orgRequest.getAdditionalInfo());
        rowData.add(orgRequest.getDanregnumber());
        rowData.add(orgRequest.getDistrict());
        rowData.add(orgRequest.getOnlinesal());
        rowData.add(orgRequest.getCreatedUserid());
        rowData.add(getDateString(orgRequest.getCreatedAt()));
        rowData.add(orgRequest.getMakerId());
        rowData.add(getDateString(orgRequest.getMakerDtStamp()));
        rowData.add(orgRequest.getCheckerId());
        rowData.add(getDateString(orgRequest.getCheckerDtStamp()));
        rowData.add(orgRequest.getLastUpdatedBy());
        rowData.add(getDateString(orgRequest.getUpdatedAt()));
        rowData.add(orgRequest.getModNo());
        objects[rowIndex] = rowData.toArray();
        rowIndex++;
      }
    }
    else
    {
      for (OrganizationRequestExcel orgRequest : orgRequests)
      {
        List<Object> rowData = new ArrayList<>();

        rowData.add(rowIndex + 1);
        rowData.add(orgRequest.getRegisternumber());
        rowData.add(orgRequest.getContractbranch());
        rowData.add(orgRequest.getName());
        rowData.add(orgRequest.getContractid());
        rowData.add(getDateString(orgRequest.getContractdt()));
        rowData.add(orgRequest.getCyear());
        rowData.add(getDateString(orgRequest.getExpiredt()));
        rowData.add(orgRequest.getFee());
        rowData.add(orgRequest.getLastcontractno());
        rowData.add(orgRequest.getCusttype());
        rowData.add(orgRequest.getExposurecategoryCode());
        rowData.add(orgRequest.getExposurecategoryDescription());
        rowData.add(orgRequest.getCif());
        rowData.add(orgRequest.getCountryregnumber());
        rowData.add(getDateString(orgRequest.getBirthdt()));
        rowData.add(orgRequest.getAddress());
        rowData.add(orgRequest.getPhone());
        rowData.add(orgRequest.getMail());
        rowData.add(orgRequest.getProductcat());
        rowData.add(orgRequest.getProductdesc());
        rowData.add(orgRequest.getContactname());
        rowData.add(orgRequest.getContactphone());
        rowData.add(orgRequest.getContactemail());
        rowData.add(orgRequest.getContactdesc());
        rowData.add(orgRequest.getChargetype());
        rowData.add(orgRequest.getChargeamount());
        rowData.add(orgRequest.getLoanamount());
        rowData.add(orgRequest.getSettlementdate());
        rowData.add(orgRequest.getSettlementpercent());
        rowData.add(orgRequest.getSettlementaccount());
        rowData.add(orgRequest.getCondition());
        rowData.add(orgRequest.getRate());
        rowData.add(orgRequest.getDischarge());
        rowData.add(orgRequest.getLeasing());
        rowData.add(orgRequest.getBnpl());
        rowData.add(orgRequest.getTerminalid());
        rowData.add(orgRequest.getCextendyear());
        rowData.add(orgRequest.getCextended());
        rowData.add(getDateString(orgRequest.getCextendedDate()));
        rowData.add(orgRequest.getRecordStat());
        rowData.add(orgRequest.getCreatedUserid());
        rowData.add(getDateString(orgRequest.getCreatedAt()));
        rowData.add(orgRequest.getMakerId());
        rowData.add(getDateString(orgRequest.getMakerDtStamp()));
        rowData.add(orgRequest.getCheckerId());
        rowData.add(getDateString(orgRequest.getCheckerDtStamp()));
        rowData.add(orgRequest.getLastUpdatedBy());
        rowData.add(getDateString(orgRequest.getUpdatedAt()));
        rowData.add(orgRequest.getModNo());

        objects[rowIndex] = rowData.toArray();
        rowIndex++;
      }
    }

    ExcelWriterUtil.write(true, sheetTitle, isSalary ? SALARY_ORGANIZATION_REQUEST_TABLE_HEADER : LEASING_ORGANIZATION_REQUEST_TABLE_HEADER, objects,
        outputStream);
  }
  private String getDateString(LocalDateTime localDateTime)
  {
    if (localDateTime == null)
    {
      return "";
    }
    return localDateTime.toLocalDate().toString();
  }
}
