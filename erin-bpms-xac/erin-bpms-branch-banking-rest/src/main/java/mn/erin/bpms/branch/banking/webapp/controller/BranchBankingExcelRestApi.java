package mn.erin.bpms.branch.banking.webapp.controller;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.file.ExcelHeader;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.ExcelService;
import mn.erin.domain.bpm.usecase.file.ReadFromExcel;
import mn.erin.domain.bpm.usecase.file.ReadFromExcelInput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.domain.bpm.BpmBranchBankingConstants.HEADERS_ENVIRONMENT_PATH;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_MISSING_TABLE_HEADER_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.BB_MISSING_TABLE_HEADER_MESSAGE;

/**
 * @author Bilguunbor
 **/

@RestController
@RequestMapping(value = "/excel", name = "Provides branch banking excel reading API.")
public class BranchBankingExcelRestApi
{
  private final ExcelService excelService;
  private final Environment environment;

  @Inject
  public BranchBankingExcelRestApi(ExcelService excelService, Environment environment)
  {
    this.excelService = excelService;
    this.environment = environment;
  }

  @ApiOperation("Reads table content from excel file.")
  @PostMapping("/read")
  public ResponseEntity<RestResult> readFromExcel(@RequestBody String contentAsBase64)
      throws UseCaseException, BpmInvalidArgumentException
  {
    List<ExcelHeader> excelHeaders = getExcelHeaders(environment);
    ReadFromExcelInput input = new ReadFromExcelInput(contentAsBase64, excelHeaders);
    ReadFromExcel useCase = new ReadFromExcel(excelService, environment);

    JSONObject output = useCase.execute(input);
    return RestResponse.success(output.toMap());
  }

  @ApiOperation("Reads table content from excel file.")
  @PostMapping("/readTemporary")
  public ResponseEntity<RestResult> readFromExcelOrganization(@RequestBody String contentAsBase64)
      throws BpmServiceException
  {
    String temporaryJsonHeaderString = "[\n"
        + " {\n"
        + "  \"key\": \"CONTRACTID\",\n"
        + "  \"headerName\": \"CONTRACTID\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CONTRACTNUMBER\",\n"
        + "  \"headerName\": \"CONTRACTNUMBER\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CIF\",\n"
        + "  \"headerName\": \"CIF\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CONTRACTBRANCH\",\n"
        + "  \"headerName\": \"CONTRACTBRANCH\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CNAME\",\n"
        + "  \"headerName\": \"CNAME\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"FCNAME\",\n"
        + "  \"headerName\": \"FCNAME\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"LOVNUMBER\",\n"
        + "  \"headerName\": \"LOVNUMBER\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CACCOUNTID\",\n"
        + "  \"headerName\": \"CACCOUNTID\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"EXPOSURECATEGORY_CODE\",\n"
        + "  \"headerName\": \"EXPOSURECATEGORY_CODE\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"EXPOSURECATEGORY_DESCRIPTION\",\n"
        + "  \"headerName\": \"EXPOSURECATEGORY_DESCRIPTION\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CCREATEDT\",\n"
        + "  \"headerName\": \"CCREATEDT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"HRCNT\",\n"
        + "  \"headerName\": \"HRCNT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"EMPNAME\",\n"
        + "  \"headerName\": \"EMPNAME\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"EMPPHONE\",\n"
        + "  \"headerName\": \"EMPPHONE\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"FORM4001\",\n"
        + "  \"headerName\": \"FORM4001\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CONTRACTDT\",\n"
        + "  \"headerName\": \"CONTRACTDT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"EXPIREDT\",\n"
        + "  \"headerName\": \"EXPIREDT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"MSTARTSALARY\",\n"
        + "  \"headerName\": \"MSTARTSALARY\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"MENDSALARY\",\n"
        + "  \"headerName\": \"MENDSALARY\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"ARATE\",\n"
        + "  \"headerName\": \"ARATE\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"ERATE\",\n"
        + "  \"headerName\": \"ERATE\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CREATED_USERID\",\n"
        + "  \"headerName\": \"CREATED_USERID\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"LAST_UPDATED_BY\",\n"
        + "  \"headerName\": \"LAST_UPDATED_BY\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CREATED_AT\",\n"
        + "  \"headerName\": \"CREATED_AT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"UPDATED_AT\",\n"
        + "  \"headerName\": \"UPDATED_AT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"COUNTRYREGNUMBER\",\n"
        + "  \"headerName\": \"COUNTRYREGNUMBER\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"REGISTERNUMBER\",\n"
        + "  \"headerName\": \"REGISTERNUMBER\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"EXTENSION_DT\",\n"
        + "  \"headerName\": \"EXTENSION_DT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"LEAKAGE\",\n"
        + "  \"headerName\": \"LEAKAGE\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CORPORATE_TYPE\",\n"
        + "  \"headerName\": \"CORPORATE_TYPE\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"LASTCONTRACTNO\",\n"
        + "  \"headerName\": \"LASTCONTRACTNO\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"SALARYTRANFEE\",\n"
        + "  \"headerName\": \"SALARYTRANFEE\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CHARGEGLACCOUNT\",\n"
        + "  \"headerName\": \"CHARGEGLACCOUNT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"IS_SALARY_LOAN\",\n"
        + "  \"headerName\": \"IS_SALARY_LOAN\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"RELEASEEMPNAME\",\n"
        + "  \"headerName\": \"RELEASEEMPNAME\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"ADDITION_INFO\",\n"
        + "  \"headerName\": \"ADDITION_INFO\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CORPORATERANK\",\n"
        + "  \"headerName\": \"CORPORATERANK\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"RECORD_STAT\",\n"
        + "  \"headerName\": \"RECORD_STAT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"AUTH_STAT\",\n"
        + "  \"headerName\": \"AUTH_STAT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"MOD_NO\",\n"
        + "  \"headerName\": \"MOD_NO\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"MAKER_ID\",\n"
        + "  \"headerName\": \"MAKER_ID\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"MAKER_DT_STAMP\",\n"
        + "  \"headerName\": \"MAKER_DT_STAMP\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CHECKER_ID\",\n"
        + "  \"headerName\": \"CHECKER_ID\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CHECKER_DT_STAMP\",\n"
        + "  \"headerName\": \"CHECKER_DT_STAMP\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"ONCE_AUTH\",\n"
        + "  \"headerName\": \"ONCE_AUTH\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"INTCOND\",\n"
        + "  \"headerName\": \"INTCOND\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"ERATE_MAX\",\n"
        + "  \"headerName\": \"ERATE_MAX\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"SDAY1\",\n"
        + "  \"headerName\": \"SDAY1\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"SDAY2\",\n"
        + "  \"headerName\": \"SDAY2\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"STIME\",\n"
        + "  \"headerName\": \"STIME\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CYEAR\",\n"
        + "  \"headerName\": \"CYEAR\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CEXTENDED\",\n"
        + "  \"headerName\": \"CEXTENDED\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CEXTENDED_DATE\",\n"
        + "  \"headerName\": \"CEXTENDED_DATE\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CEXTENDYEAR\",\n"
        + "  \"headerName\": \"CEXTENDYEAR\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"CCREATED_DATE\",\n"
        + "  \"headerName\": \"CCREATED_DATE\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"ADDITIONAL_INFO\",\n"
        + "  \"headerName\": \"ADDITIONAL_INFO\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"DANREGNUMBER\",\n"
        + "  \"headerName\": \"DANREGNUMBER\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"DISTRICT\",\n"
        + "  \"headerName\": \"DISTRICT\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"ONLINESAL\",\n"
        + "  \"headerName\": \"ONLINESAL\"\n"
        + " },\n"
        + " {\n"
        + "  \"key\": \"PROCESS_INSTANCE_ID\",\n"
        + "  \"headerName\": \"PROCESS_INSTANCE_ID\"\n"
        + " }\n"
        + "]";

    JSONArray headerValues = new JSONArray(temporaryJsonHeaderString);
    JSONArray output = excelService.readFromExcel(contentAsBase64, headerValues);
    return RestResponse.success(output.toList());
  }

  private List<ExcelHeader> getExcelHeaders(Environment environment) throws BpmInvalidArgumentException
  {
    List<ExcelHeader> excelHeaderList = new ArrayList<>();
    String stringExcelTableHeaders = environment.getProperty(HEADERS_ENVIRONMENT_PATH);

    if (StringUtils.isBlank(stringExcelTableHeaders))
    {
      throw new BpmInvalidArgumentException(BB_MISSING_TABLE_HEADER_CODE, BB_MISSING_TABLE_HEADER_MESSAGE);
    }

    String[] splitHeaders = stringExcelTableHeaders.split(";");

    for (String header : splitHeaders)
    {
      ExcelHeader excelHeader = getExcelHeader(header);
      excelHeaderList.add(excelHeader);
    }

    return excelHeaderList;
  }

  private static ExcelHeader getExcelHeader(String header)
  {
    String[] split = header.split("=");

    return new ExcelHeader(split[0], split[1]);
  }
}
