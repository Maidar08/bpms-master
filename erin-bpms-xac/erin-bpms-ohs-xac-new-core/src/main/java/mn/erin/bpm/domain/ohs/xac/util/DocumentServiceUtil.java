package mn.erin.bpm.domain.ohs.xac.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mn.erin.domain.bpm.service.BpmServiceException;

/**
 * @author Tamir
 */
public final class DocumentServiceUtil
{
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceUtil.class);

  public static final String OK = "OK";
  public static final String DOCUMENT_PATH_TAG = "a:DocumentPath";

  public static final String END_POINT_KEY = "sharePoint.endpoint";
  public static final String ACCESS_CODE_KEY = "sharePoint.accessCode";

  public static final String UPLOAD_SOAP_ACTION = "sharePoint.header.soap.upload.action";
  public static final String GET_DOCUMENT_SOAP_ACTION = "sharePoint.header.soap.getDocument.action";

  public static final String RESPONSE_TYPE_TAG = "a:ResponseType";
  public static final String REPORT_BYTES_TAG = "reportBytes";

  private static final String FUNCTION = "sharePoint.function";
  private static final String REPORT_ERROR_TEXT = "soapenv:Text";
  private static final String DOCUMENT_UPLOAD_USER_ID = "sharePoint.userId";

  private DocumentServiceUtil()
  {
  }

  public static String getDmsEndPoint(Environment environment)
  {
    return environment.getProperty(END_POINT_KEY);
  }

  public static String getAccessCode(Environment environment)
  {
    return environment.getProperty(ACCESS_CODE_KEY);
  }

  public static String getDocumentUploadUserId(Environment environment)
  {
    return environment.getProperty(DOCUMENT_UPLOAD_USER_ID);
  }

  public static String getUploadSoapAction(Environment environment)
  {
    return environment.getProperty(UPLOAD_SOAP_ACTION);
  }

  public static String getDocumentSoapAction(Environment environment)
  {
    return environment.getProperty(GET_DOCUMENT_SOAP_ACTION);
  }

  public static String getFunction(Environment environment)
  {
    return environment.getProperty(FUNCTION);
  }

  public static String simpleDataStructuresToXML(Map<?, Object> map, String ancientObjectName)
  {
    StringBuilder mapAsString = new StringBuilder().append("<").append(ancientObjectName).append(">");
    Set<? extends Map.Entry<?, Object>> entries = map.entrySet();
    for (Map.Entry<?, Object> ent : entries)
    {
      Object value = ent.getValue();
      if (value instanceof Map)
      {
        mapAsString.append(simpleDataStructuresToXML((Map<?, Object>) ent.getValue(), ent.getKey().toString()));
      }
      else if (value instanceof List)
      {
        String parentKey = ent.getKey().toString();
        mapAsString.append(
            ((List<Map<?, Object>>) ent.getValue()).stream().map(el -> simpleDataStructuresToXML(el, parentKey)).collect(Collectors.joining("")));
      }
      else
      {
        mapAsString.append("<").append(ent.getKey()).append(">").append(value).append("</").append(ent.getKey()).append(">");
      }
    }
    return mapAsString.append("</").append(ancientObjectName).append(">").toString();
  }

  public static String getContractBase64Str(String responseStr) throws BpmServiceException
  {
    SOAPBody body = getSoapBody(responseStr, SOAPConstants.SOAP_1_2_PROTOCOL);

    if (null != body.getElementsByTagName(REPORT_ERROR_TEXT))
    {
      NodeList errorTextNodes = body.getElementsByTagName(REPORT_ERROR_TEXT);
      Node errorText = errorTextNodes.item(0);

      if (null != errorText)
      {
        LOGGER.error("######## DOWNLOAD LOAN CONTRACT RESPONSE ERROR MESSAGE = [{}]", errorText.getTextContent());
        throw new BpmServiceException(errorText.getTextContent());
      }
    }

    NodeList reportBytes = body.getElementsByTagName(REPORT_BYTES_TAG);
    Node reportBytesNode = reportBytes.item(0);

    if (null != reportBytesNode)
    {
      // returns contract as base 64 string.
      return reportBytesNode.getTextContent();
    }
    return null;
  }

  public static String getDocRefResult(String responseStr) throws BpmServiceException
  {
    SOAPBody body = getSoapBody(responseStr, SOAPConstants.DEFAULT_SOAP_PROTOCOL);

    NodeList resultNodes = body.getElementsByTagName(RESPONSE_TYPE_TAG);
    NodeList documentRefNodes = body.getElementsByTagName(DOCUMENT_PATH_TAG);

    for (int index = 0; index < resultNodes.getLength(); index++)
    {
      String resultMessage = resultNodes.item(index).getTextContent();

      if (resultMessage.equalsIgnoreCase(OK))
      {
        return documentRefNodes.item(0).getTextContent();
      }
    }
    return null;
  }

  public static String getReportResult(String responseStr) throws BpmServiceException
  {
    SOAPBody body = getSoapBody(responseStr, SOAPConstants.DEFAULT_SOAP_PROTOCOL);

    NodeList report = body.getElementsByTagName(REPORT_BYTES_TAG);

    if (report.getLength() == 0)
    {
      report = body.getElementsByTagName("faultstring");

      if (report.getLength() == 0)
      {
        throw new BpmServiceException("Error Receiving Report Result!");
      }
      throw new BpmServiceException(report.item(0).getTextContent());
    }

    return report.item(0).getTextContent();
  }

  public static String getUploadResult(String responseStr) throws BpmServiceException
  {
    SOAPBody body = getSoapBody(responseStr, SOAPConstants.DEFAULT_SOAP_PROTOCOL);
    NodeList returnList = body.getElementsByTagName(RESPONSE_TYPE_TAG);

    for (int index = 0; index < returnList.getLength(); index++)
    {

      NodeList innerResultList = returnList.item(index).getChildNodes();
      for (int childIndex = 0; childIndex < innerResultList.getLength(); childIndex++)
      {
        if (innerResultList.item(childIndex).getNodeName().equalsIgnoreCase(RESPONSE_TYPE_TAG))
        {
          return innerResultList.item(childIndex).getTextContent();
        }
      }
    }
    return null;
  }

  public static SOAPBody getSoapBody(String responseStr, String soapProtocol) throws BpmServiceException
  {
    try
    {
      LOGGER.info("###### SOAP RESPONSE STRING = [{}]", responseStr);
      LOGGER.info("###### Message factory PROTOCOL = [{}]", soapProtocol);

      MessageFactory factory = MessageFactory.newInstance(soapProtocol);
      SOAPMessage message = factory.createMessage(
          new MimeHeaders(),
          new ByteArrayInputStream(responseStr.getBytes(StandardCharsets.UTF_8)));

      return message.getSOAPBody();
    }
    catch (SOAPException | IOException e)
    {
      LOGGER.error(e.getMessage(), e);
      String errorCode = "DMS017";
      throw new BpmServiceException(errorCode, "Could not get SOAP body!, Reason for that: " + e.getMessage());
    }
  }

  public static String getRequestBodyDownloadContract(String absolutePath, String fileFormat, String accountNumber, String paymentType, String userId,
      String userPassword, String dataSizeForChunkDownload)
  {
    return
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v2=\"http://xmlns.oracle.com/oxp/service/v2\">\n"
            + "    <soapenv:Header/>\n"
            + "    <soapenv:Body>\n"
            + "        <v2:runReport>\n"
            + "            <v2:reportRequest>\n"
            + "                <v2:attributeFormat>" + fileFormat + "</v2:attributeFormat>\n"
            + "                <v2:attributeLocale>English</v2:attributeLocale>\n"
            + "                <v2:attributeTemplate>English</v2:attributeTemplate>\n"
            + "                <v2:attributeTemplate>2</v2:attributeTemplate>\n"
            + "                <v2:parameterNameValues>\n"
            + "                    <v2:listOfParamNameValues>\n"
            + getAccountXmlBody(accountNumber)
            + "                        <v2:item>\n"
            + "                            <v2:name>P_PAYMENTTYPE</v2:name>\n"
            + "                            <v2:values>\n"
            + "                                <v2:item>" + paymentType + "</v2:item>\n"
            + "                            </v2:values>\n"
            + "                        </v2:item>\n"
            + "                    </v2:listOfParamNameValues>\n"
            + "                </v2:parameterNameValues>\n"
            + "                <v2:reportAbsolutePath>" + absolutePath + "</v2:reportAbsolutePath>\n"
            + "                <v2:sizeOfDataChunkDownload>" + dataSizeForChunkDownload + "</v2:sizeOfDataChunkDownload>\n"
            + "            </v2:reportRequest>\n"
            + "            <v2:userID>" + userId + "</v2:userID>\n"
            + "            <v2:password>" + userPassword + "</v2:password>\n"
            + "        </v2:runReport>\n"
            + "    </soapenv:Body>\n"
            + "</soapenv:Envelope>";
  }

  public static String getReqBodyOnlineSalaryLoan(String absolutePath, String fileFormat, String accountNumber, String requestId, String userId,
      String userPassword, String dataSizeForChunkDownload)
  {
    return "<soap:Envelope\n"
        + "xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"\n"
        + "xmlns:pub=\"http://xmlns.oracle.com/oxp/service/PublicReportService\">\n"
        + "<soap:Header/>\n"
        + "<soap:Body>\n"
        + "<pub:runReport>\n"
        + "<pub:reportRequest>\n"
        + "<pub:attributeFormat>" + fileFormat + "</pub:attributeFormat>\n"
        + "<pub:parameterNameValues>\n"
        + "\t<pub:item>\n"
        + "\t\t<pub:name>P_Req</pub:name>\n"
        + "\t\t<pub:values>\n"
        + "\t\t\t<pub:item>" + requestId + "</pub:item>\n"
        + "\t\t</pub:values>\n"
        + "\t</pub:item>\n"
        + "\t<pub:item>\n"
        + "\t\t<pub:name>P_ACC</pub:name>\n"
        + "\t\t<pub:values>\n"
        + "\t\t\t<pub:item>" + accountNumber + "</pub:item>\n"
        + "\t\t</pub:values>\n"
        + "\t</pub:item>\n"
        + "</pub:parameterNameValues>\n"
        + "<pub:reportAbsolutePath>" + absolutePath + "</pub:reportAbsolutePath>\n"
        + "<pub:sizeOfDataChunkDownload>" + dataSizeForChunkDownload + "</pub:sizeOfDataChunkDownload>\n"
        + "</pub:reportRequest>\n"
        + "<pub:userID>" + userId + "</pub:userID>\n"
        + "<pub:password>" + userPassword + "</pub:password>\n"
        + "</pub:runReport>\n"
        + "</soap:Body>\n"
        + "</soap:Envelope>";
  }

  public static String getRequestBodyDownloadReport(String absolutePath, String fileFormat, String accountNumber, String userId,
      String userPassword, String dataSizeForChunkDownload)
  {
    return
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v2=\"http://xmlns.oracle.com/oxp/service/v2\">\n"
            + "    <soapenv:Header/>\n"
            + "    <soapenv:Body>\n"
            + "        <v2:runReport>\n"
            + "            <v2:reportRequest>\n"
            + "                <v2:attributeFormat>" + fileFormat + "</v2:attributeFormat>\n"
            + "                <v2:attributeLocale>English</v2:attributeLocale>\n"
            + "                <v2:attributeTemplate>English</v2:attributeTemplate>\n"
            + "                <v2:attributeTemplate>2</v2:attributeTemplate>\n"
            + "                <v2:parameterNameValues>\n"
            + "                    <v2:listOfParamNameValues>\n"
            + getAccountXmlBody(accountNumber)
            + "                    </v2:listOfParamNameValues>\n"
            + "                </v2:parameterNameValues>\n"
            + "                <v2:reportAbsolutePath>" + absolutePath + "</v2:reportAbsolutePath>\n"
            + "                <v2:sizeOfDataChunkDownload>" + dataSizeForChunkDownload + "</v2:sizeOfDataChunkDownload>\n"
            + "            </v2:reportRequest>\n"
            + "            <v2:userID>" + userId + "</v2:userID>\n"
            + "            <v2:password>" + userPassword + "</v2:password>\n"
            + "        </v2:runReport>\n"
            + "    </soapenv:Body>\n"
            + "</soapenv:Envelope>";
  }

  public static String getRequestBodyDownloadPaymentSchedule(String absolutePath, String fileFormat, String accountNumber, String grace, String userId,
      String userPassword, String dataSizeForChunkDownload)
  {
    return
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:v2=\"http://xmlns.oracle.com/oxp/service/v2\">\n"
            + "    <soapenv:Header/>\n"
            + "    <soapenv:Body>\n"
            + "        <v2:runReport>\n"
            + "            <v2:reportRequest>\n"
            + "                <v2:attributeFormat>" + fileFormat + "</v2:attributeFormat>\n"
            + "                <v2:attributeLocale>English</v2:attributeLocale>\n"
            + "                <v2:attributeTemplate>English</v2:attributeTemplate>\n"
            + "                <v2:attributeTemplate>2</v2:attributeTemplate>\n"
            + "                <v2:parameterNameValues>\n"
            + "                    <v2:listOfParamNameValues>\n"
            + getAccountXmlBody(accountNumber)
            + "                          <v2:item>\n"
            + "                            <v2:name>P_GRACE</v2:name>\n"
            + "                            <v2:values>\n"
            + "                                <v2:item>" + grace + "</v2:item>\n"
            + "                            </v2:values>\n"
            + "                        </v2:item>"
            + "                    </v2:listOfParamNameValues>\n"
            + "                </v2:parameterNameValues>\n"
            + "                <v2:reportAbsolutePath>" + absolutePath + "</v2:reportAbsolutePath>\n"
            + "                <v2:sizeOfDataChunkDownload>" + dataSizeForChunkDownload + "</v2:sizeOfDataChunkDownload>\n"
            + "            </v2:reportRequest>\n"
            + "            <v2:userID>" + userId + "</v2:userID>\n"
            + "            <v2:password>" + userPassword + "</v2:password>\n"
            + "        </v2:runReport>\n"
            + "    </soapenv:Body>\n"
            + "</soapenv:Envelope>";
  }

  public static String getDocumentRequestBody(String accessCode, String documentReference, String userId) throws BpmServiceException
  {
    if (StringUtils.isBlank(accessCode))
    {
      String errorCode = "DMS014";
      throw new BpmServiceException(errorCode, "Access code cannot be null during call get document service!");
    }

    if (StringUtils.isBlank(documentReference))
    {
      String errorCode = "DMS015";
      throw new BpmServiceException(errorCode, "Document reference cannot be null during call get document service!");
    }

    if (StringUtils.isBlank(userId))
    {
      String errorCode = "DMS016";
      throw new BpmServiceException(errorCode, "User ID cannot be null during call get document service!");
    }

    return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n"
        + "   <soapenv:Header/>\n"
        + "   <soapenv:Body>\n"
        + "      <tem:GetDocument>\n"
        + "         <!--Optional:-->\n"
        + "         <tem:accessCode>" + accessCode + "</tem:accessCode>\n"
        + "         <!--Optional:-->\n"
        + "         <tem:documentID>" + documentReference + "</tem:documentID>\n"
        + "         <!--Optional:-->\n"
        + "         <tem:vieweruserId>" + userId + "</tem:vieweruserId>\n"
        + "      </tem:GetDocument>\n"
        + "   </soapenv:Body>\n"
        + "</soapenv:Envelope>";
  }

  public static String getDocumentRequestBody(Map<String, String> documentParam, String reportAbsolutePathDocType, String reportAbsolutePath, String userId,
      String password)
  {
    return "<soapenv:Envelope\n"
        + "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
        + "xmlns:v2=\"http://xmlns.oracle.com/oxp/service/v2\">\n"
        + "<soapenv:Header/>\n"
        + "<soapenv:Body>\n"
        + "\n"
        + getSoapBody(documentParam, reportAbsolutePathDocType, reportAbsolutePath, userId, password) + "\n"
        + "</soapenv:Body>\n"
        + "</soapenv:Envelope>";
  }

  public static void validateParameters(String processRequestId, String processTypeId, String cifNumber, String registerNumber, String fullName)
      throws BpmServiceException
  {
    if (StringUtils.isBlank(processRequestId))
    {
      String errorCode = "DMS009";
      throw new BpmServiceException(errorCode, "Loan request id cannot be blank!");
    }

    if (StringUtils.isBlank(processTypeId))
    {
      String errorCode = "DMS010";
      throw new BpmServiceException(errorCode, "Process type id cannot be blank!");
    }

    if (StringUtils.isBlank(cifNumber))
    {
      String errorCode = "DMS011";
      throw new BpmServiceException(errorCode, "Customer CIF number cannot be blank!");
    }

    if (StringUtils.isBlank(registerNumber))
    {
      String errorCode = "DMS012";
      throw new BpmServiceException(errorCode, "Customer register number cannot be blank!");
    }

    if (StringUtils.isBlank(fullName))
    {
      String errorCode = "DMS013";
      throw new BpmServiceException(errorCode, "Customer full name cannot be blank!");
    }
  }

  private static String getSoapBody(Map<String, String> documentParam, String reportAbsolutePathDocType, String reportAbsolutePath, String userId,
      String password)
  {
    return "<v2:runReport>\n"
        + "<v2:reportRequest>\n"
        + getAttributeFormat() + "\n"
        + "<v2:parameterNameValues>\n"
        + "<v2:listOfParamNameValues>\n"
        + getParamItems(documentParam) + "\n"
        + "</v2:listOfParamNameValues>\n"
        + "</v2:parameterNameValues>\n"
        + "<v2:reportAbsolutePath>/" + reportAbsolutePathDocType + "/" + reportAbsolutePath + ".xdo</v2:reportAbsolutePath>\n"
        + "<v2:sizeOfDataChunkDownload>-1</v2:sizeOfDataChunkDownload>"
        + "</v2:reportRequest>\n"
        + "<v2:userID>" + userId + "</v2:userID>\n"
        + "<v2:password>" + password + "</v2:password>\n"
        + "</v2:runReport>";
  }

  public static String getReqBodyDirectOnlineSalaryLoan(Map<String, String> documentParam, String absolutePath, String fileFormat, String userId,
      String userPassword, String dataSizeForChunkDownload)
  {
    return "<soap:Envelope\n"
        + "xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"\n"
        + "xmlns:pub=\"http://xmlns.oracle.com/oxp/service/PublicReportService\">\n"
        + "<soap:Header/>\n"
        + "<soap:Body>\n"
        + "<pub:runReport>\n"
        + "<pub:reportRequest>\n"
        + "<pub:attributeFormat>" + fileFormat + "</pub:attributeFormat>\n"
        + "<pub:parameterNameValues>\n"
        + getParamItems(documentParam) + "\n"
        + "</pub:parameterNameValues>\n"
        + "<pub:reportAbsolutePath>" + absolutePath + "</pub:reportAbsolutePath>\n"
        + "<pub:sizeOfDataChunkDownload>" + dataSizeForChunkDownload + "</pub:sizeOfDataChunkDownload>\n"
        + "</pub:reportRequest>\n"
        + "<pub:userID>" + userId + "</pub:userID>\n"
        + "<pub:password>" + userPassword + "</pub:password>\n"
        + "</pub:runReport>\n"
        + "</soap:Body>\n"
        + "</soap:Envelope>";
  }

  private static String getAttributeFormat()
  {
    return "<v2:attributeFormat>pdf</v2:attributeFormat>\n"
        + "<v2:attributeLocale>English</v2:attributeLocale>\n"
        + "<v2:attributeTemplate>English</v2:attributeTemplate>\n"
        + "<v2:attributeTemplate>2</v2:attributeTemplate>";
  }

  private static String getParamItems(Map<String, String> documentParam)
  {
    StringBuilder paramItem = new StringBuilder();

    for (Map.Entry<String, String> entry : documentParam.entrySet())
    {
      paramItem.append("<v2:item>\n")
          .append("<v2:name>")
          .append(entry.getKey())
          .append("</v2:name>\n")
          .append("<v2:values>\n")
          .append("<v2:item>")
          .append(entry.getValue())
          .append("</v2:item>")
          .append("</v2:values>")
          .append("</v2:item>");
    }

    return paramItem.toString();
  }

  private static String getAccountXmlBody(String accountNumber)
  {
    return
        "                            <v2:item>\n"
            + "                               <v2:name>P_ACC</v2:name>\n"
            + "                               <v2:values>\n"
            + "                                  <v2:item>" + accountNumber + "</v2:item>\n"
            + "                               </v2:values>\n"
            + "                            </v2:item>\n";
  }
}
