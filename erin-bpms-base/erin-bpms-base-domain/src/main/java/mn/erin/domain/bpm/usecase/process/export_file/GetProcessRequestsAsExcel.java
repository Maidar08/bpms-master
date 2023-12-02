package mn.erin.domain.bpm.usecase.process.export_file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.common.excel.ExcelWriterUtil;
import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.MessageConstants;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;

public class GetProcessRequestsAsExcel extends AuthorizedUseCase<GetProcessRequestExcelInput, byte[]>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessRequestsAsExcel.class);
  private static final Permission permission = new BpmModulePermission("GetProcessRequestsByAssignedUserId");

  public GetProcessRequestsAsExcel(AuthorizationService authorizationService, AuthenticationService authenticationService)
  {
    super(authenticationService, authorizationService);
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected byte[] executeImpl(GetProcessRequestExcelInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(MessageConstants.INPUT_REQUIRED_CODE, MessageConstants.INPUT_REQUIRED);
    }

    String topHeader = input.getTopHeader();
    String groupId = input.getGroupId();
    String stringDate = input.getStringDate();
    String searchKey = input.getSearchKey();

    validateInput(topHeader, searchKey, groupId, stringDate);

    try
    {
      searchKey = URLDecoder.decode(input.getSearchKey(), StandardCharsets.UTF_8.name());
    }
    catch (UnsupportedEncodingException e)
    {
      LOGGER.error(e.getMessage(), e);
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    try
    {
      String title = getTitleText(topHeader);
      Collection<ProcessRequest> processRequests = input.getProcessRequests();

      if (null == processRequests || processRequests.isEmpty())
      {
        String sheetTitle = title + "   " + stringDate;
        exportToExcelFile(Collections.emptyList(), outputStream, sheetTitle);
      }
      else
      {
        String sheetTitle = title + "   " + stringDate;
        exportToExcelFile(processRequests, outputStream, sheetTitle);
      }

      return outputStream.toByteArray();
    }

    finally
    {
      try
      {
        outputStream.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
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

  private String toDateString(LocalDateTime createdTime)
  {
    Date date = new GregorianCalendar(createdTime.getYear(), createdTime.getMonth().getValue() - 1, createdTime.getDayOfMonth()).getTime();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    return format.format(date);
  }

  private void exportToExcelFile(Collection<ProcessRequest> processRequests, OutputStream outputStream, String sheetTitle)
  {
    Object[][] objects = new Object[processRequests.size()][];
    int rowIndex = 0;
    for (ProcessRequest processRequest : processRequests)
    {
      List<Object> rowData = new ArrayList<>();
      String processTypeId = processRequest.getProcessTypeId().getId();
      String processRequestState = processRequest.getState().toString();

      rowData.add(rowIndex + 1);
      rowData.add(processRequest.getId().getId());
      rowData.add(processRequest.getParameterValue(BpmModuleConstants.FULL_NAME));
      rowData.add(processRequest.getParameterValue(BpmModuleConstants.REGISTER_NUMBER));
      rowData.add(processRequest.getParameterValue(BpmModuleConstants.CIF_NUMBER));
      rowData.add(BpmModuleConstants.PROCESS_TYPE_IDS.get(processTypeId));
      rowData.add(processRequest.getParameterValue(BpmModuleConstants.LOAN_AMOUNT).toString());
      rowData.add(toDateString(processRequest.getCreatedTime()));
      rowData.add(processRequest.getRequestedUserId());
      rowData.add(processRequest.getGroupId().getId());
      rowData.add(processRequest.getParameterValue(BpmModuleConstants.CHANNEL));
      rowData.add(BpmModuleConstants.PROCESS_REQUEST_STATE_MAP.get(processRequestState));

      objects[rowIndex] = rowData.toArray();
      rowIndex++;
    }

    ExcelWriterUtil.write(true, sheetTitle, BpmModuleConstants.PROCESS_REQUEST_TABLE_HEADER, objects, outputStream);
  }

  private String getTitleText(String topHeader)
  {
    switch (topHeader)
    {
    case BpmModuleConstants.MY_LOAN_REQUEST:
      return BpmModuleConstants.PROCESS_REQUEST_TYPE.get(BpmModuleConstants.MY_LOAN_REQUEST);
    case BpmModuleConstants.BRANCH_LOAN_REQUEST:
      return BpmModuleConstants.PROCESS_REQUEST_TYPE.get(BpmModuleConstants.BRANCH_LOAN_REQUEST);
    case BpmModuleConstants.ALL_LOAN_REQUEST:
      return BpmModuleConstants.PROCESS_REQUEST_TYPE.get(BpmModuleConstants.ALL_LOAN_REQUEST);
    case BpmModuleConstants.INTERNET_BANK_LOAN_REQUEST:
      return BpmModuleConstants.PROCESS_REQUEST_TYPE.get(BpmModuleConstants.INTERNET_BANK_LOAN_REQUEST);
    case BpmModuleConstants.SEARCH_CUSTOMER:
      return BpmModuleConstants.PROCESS_REQUEST_TYPE.get(BpmModuleConstants.SEARCH_CUSTOMER);
    case BpmModuleConstants.SUB_GROUP_REQUEST:
      return BpmModuleConstants.PROCESS_REQUEST_TYPE.get(BpmModuleConstants.SUB_GROUP_REQUEST);
    default:
      return null;
    }
  }

}
