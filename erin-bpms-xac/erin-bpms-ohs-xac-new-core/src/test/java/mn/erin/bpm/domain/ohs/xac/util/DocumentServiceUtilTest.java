package mn.erin.bpm.domain.ohs.xac.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import mn.erin.domain.bpm.service.BpmServiceException;

/**
 * @author Tamir
 */
public class DocumentServiceUtilTest
{
  private static final String OK = "OK";
  private static final String UPLOAD_RESPONSE = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
      + "    <s:Body>\n"
      + "        <UploadFileResponse xmlns=\"http://tempuri.org/\">\n"
      + "            <UploadFileResult xmlns:a=\"http://schemas.datacontract.org/2004/07/SharePointDMS\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
      + "                <a:DocumentPath i:nil=\"true\"/>\n"
      + "                <a:Message>%22SAD_93106.pdf%22+%d0%91%d0%b8%d1%87%d0%b8%d0%b3+%d0%b1%d0%b0%d1%80%d0%b8%d0%bc%d1%82+%d0%bd%d1%8d%d0%bc%d1%8d%d0%b3%d0%b4%d1%81%d1%8d%d0%bd%d0%b3%d2%af%d0%b9%3a+%22123456789%22+%d0%b4%d1%83%d0%b3%d0%b0%d0%b0%d1%80+%d0%b4%d1%8d%d1%8d%d1%80+%2200031530_03._%d0%a1%d0%b0%d0%bd_%d1%85%d0%b0%d0%b0%d1%85_%d1%81%d0%b0%d0%bd%d0%b0%d0%bb_20200517_ZrTEG6iNqUWBKoTnUGxeVg.pdf%22+%d0%bd%d1%8d%d1%80%d1%82%d1%8d%d0%b9+%d0%b1%d0%b8%d1%87%d0%b8%d0%b3+%d0%b1%d0%b0%d1%80%d0%b8%d0%bc%d1%82+%d0%b1%d0%b0%d0%b9%d0%bd%d0%b0.</a:Message>\n"
      + "                <a:ResponseType>\n"
      + "                    <a:ResponseType>OK</a:ResponseType>\n"
      + "                </a:ResponseType>\n"
      + "            </UploadFileResult>\n"
      + "        </UploadFileResponse>\n"
      + "    </s:Body>\n"
      + "</s:Envelope>";

  private static final String DOWNLOAD_CONTRACT_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
      + "    <soapenv:Body>\n"
      + "        <runReportResponse xmlns=\"http://xmlns.oracle.com/oxp/service/PublicReportService\">\n"
      + "            <runReportReturn>\n"
      + "                <reportBytes>base64Value</reportBytes>\n"
      + "                <reportContentType>application/pdf</reportContentType>\n"
      + "                <reportFileID xsi:nil=\"true\"/>\n"
      + "                <reportLocale xsi:nil=\"true\"/>\n"
      + "            </runReportReturn>\n"
      + "        </runReportResponse>\n"
      + "    </soapenv:Body>\n"
      + "</soapenv:Envelope>";

  private static final String DOWNLOAD_CONTRACT_RES_WITH_ERROR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
      + "    <soapenv:Body>\n"
      + "        <soapenv:Fault>\n"
      + "            <soapenv:Code>\n"
      + "                <soapenv:Value>soapenv:Server.userException</soapenv:Value>\n"
      + "            </soapenv:Code>\n"
      + "            <soapenv:Reason>\n"
      + "                <soapenv:Text xml:lang=\"en\">oracle.xdo.webservice.exception.AccessDeniedException: java.lang.SecurityException: Failed to log into BI Publisher: invalid username or password.</soapenv:Text>\n"
      + "            </soapenv:Reason>\n"
      + "            <soapenv:Detail>\n"
      + "                <oracle.xdo.webservice.exception.AccessDeniedException/>\n"
      + "                <ns1:hostname xmlns:ns1=\"http://xml.apache.org/axis/\">obidev.xac0000.mn</ns1:hostname>\n"
      + "            </soapenv:Detail>\n"
      + "        </soapenv:Fault>\n"
      + "    </soapenv:Body>\n"
      + "</soapenv:Envelope>";

  private static final String GET_DOCUMENT_RESPONSE = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body><GetDocumentResponse xmlns=\"http://tempuri.org/\"><GetDocumentResult xmlns:a=\"http://schemas.datacontract.org/2004/07/SharePointDMS\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><a:DocumentPath>http://sharepointtest/sites/dms/pages/default.aspx?docid=Sun May 17 19:59:40 ULAT 2020&amp;m=view</a:DocumentPath><a:Message>00031530_01._%d3%a8%d1%80%d0%b3%d3%a9%d0%b4%d3%a9%d0%bb%2c_%d1%85%d2%af%d1%81%d1%8d%d0%bb%d1%82_20200517_8TK-MjJqW0uPP5iYnaCBkA.pdf</a:Message><a:ResponseType>OK</a:ResponseType></GetDocumentResult></GetDocumentResponse></s:Body></s:Envelope>";

  @Test
  public void mapToXML() {
    HashMap<String, Object> hashMap = new HashMap<>();
    List<HashMap<String, Object>> childList = new ArrayList<>();

    hashMap.put("name","chris");

    HashMap<String, Object> childmap = new HashMap<>();
    childmap.put("name", "faranga");

    HashMap<String, Object> childmap1 = new HashMap<>();
    childmap1.put("name", "faranga");
    hashMap.put("islands", childList);

    childList.add(childmap1);
    childList.add(childmap);

    String result = DocumentServiceUtil.simpleDataStructuresToXML(hashMap, "root");
    Assert.assertEquals("<root><islands><name>faranga</name></islands><islands><name>faranga</name></islands><name>chris</name></root>", result);
  }

  @Test
  public void verify_upload_document_response() throws BpmServiceException
  {

    String expectedResult = DocumentServiceUtil.getUploadResult(UPLOAD_RESPONSE);

    Assert.assertEquals(OK, expectedResult);
  }

  @Test
  public void verify_get_document_response() throws BpmServiceException
  {
    String expectedDocRef = DocumentServiceUtil.getDocRefResult(GET_DOCUMENT_RESPONSE);

    Assert.assertNotNull(expectedDocRef);
  }

  @Test
  public void verify_get_contract_as_base64() throws BpmServiceException
  {
    String contractAsBase64 = DocumentServiceUtil.getContractBase64Str(DOWNLOAD_CONTRACT_RESPONSE);

    Assert.assertNotNull(contractAsBase64);
    Assert.assertEquals("base64Value", contractAsBase64);
  }

  @Test(expected = BpmServiceException.class)
  public void verify_catch_error_text() throws BpmServiceException
  {
    DocumentServiceUtil.getContractBase64Str(DOWNLOAD_CONTRACT_RES_WITH_ERROR);
  }
}
