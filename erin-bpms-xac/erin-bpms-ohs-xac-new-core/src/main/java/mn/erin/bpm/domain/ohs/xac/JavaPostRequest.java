package mn.erin.bpm.domain.ohs.xac;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class JavaPostRequest
{

  private static final Logger LOGGER = LoggerFactory.getLogger(JavaPostRequest.class);

  public static String createLoanAccount(Environment environment, String url, String body, String userId, String branchId)
  {
    byte[] postData = body.getBytes(StandardCharsets.UTF_8);

    String source = getHeaderSource(environment);
    String function = getHeaderFunction(environment, XacConstants.WSO2_HEADER_CREATE_CL_ACCOUNT);

    String requestId = getTimeStamp();
    String securityCode = generateSecurityCode(source, function);

    HttpURLConnection con = null;

    try
    {
      URL myurl = new URL(url);
      con = (HttpURLConnection) myurl.openConnection();

      con.setDoOutput(true);
      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "Java client");
      con.setRequestProperty("Content-Type", "application/json");

      // set application params
      con.setRequestProperty("Source", source);
      con.setRequestProperty("Function", function);
      con.setRequestProperty("UserId", userId);
      con.setRequestProperty("RequestId", requestId);
      con.setRequestProperty("RequestType", "R");
      con.setRequestProperty("SecurityCode", securityCode);
      con.setRequestProperty("Branch", branchId);

      try (DataOutputStream wr = new DataOutputStream(con.getOutputStream()))
      {
        wr.write(postData);
      }
      catch (Exception e)
      {
        LOGGER.error("############# FAILED TO PARSE RESPONSE : ", e);
        return null;
      }
      finally
      {
        LOGGER.error("############# IN FINALLY : ");
      }

      StringBuilder content;

      try (BufferedReader br = new BufferedReader(
          new InputStreamReader(con.getInputStream())))
      {

        String line;
        content = new StringBuilder();

        while ((line = br.readLine()) != null)
        {
          content.append(line);
          content.append(System.lineSeparator());
        }
        return content.toString();
      }
    }
    catch (Exception ex)
    {
      LOGGER.error("######### FAILED TO CREATE LOAN ACCOUNT", ex);
    }
    finally
    {
      if (null != con)
      {
        con.disconnect();
      }
    }
    return null;
  }

  public static String generateSecurityCode(String source, String function)
  {
    String value = source + function;
    return DigestUtils.md5Hex(value).toUpperCase();
  }

  public static String getTimeStamp()
  {
    return new SimpleDateFormat(XacHttpConstants.DATE_FORMATTER).format(new Date());
  }

  private static String getHeaderSource(Environment environment)
  {
    return environment.getProperty(XacConstants.WSO2_HEADER_SOURCE);
  }

  private static String getHeaderFunction(Environment environment, String prefix)
  {
    return environment.getProperty(prefix + ".function");
  }
}
