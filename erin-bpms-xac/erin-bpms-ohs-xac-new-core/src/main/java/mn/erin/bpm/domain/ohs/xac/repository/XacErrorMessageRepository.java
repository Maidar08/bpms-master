package mn.erin.bpm.domain.ohs.xac.repository;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import mn.erin.common.file.FileUtil;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.ErrorMessageRepository;

public class XacErrorMessageRepository implements ErrorMessageRepository
{
  public static final String ERROR_MESSAGE_JSON = "/xac-error-message.json";
  public static final String ERROR_CODE = "errorCode";
  public static final String ERROR_DESC = "errorDesc";

  private static final String REGISTER_REG_EXP = "[ӨөҮүЁёА-Яа-я]{2}[0-9]{8}";
  @Override
  public String getMessage(String code)
  {
    try (InputStream inputStream = XacErrorMessageRepository.class.getResourceAsStream(ERROR_MESSAGE_JSON))
    {
      JSONArray errorArray = FileUtil.readInputStream(inputStream);
      for (int index = 0; index < errorArray.length(); index++)
      {
        JSONObject errorObject = errorArray.getJSONObject(index);
        String errorCode = errorObject.getString(ERROR_CODE);
        String errorDesc = errorObject.getString(ERROR_DESC);
        if (code.equals(errorCode))
        {
          return errorDesc;
        }
      }
      return "An Error description related to the code has not found.";
    }
    catch (IOException e)
    {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  @Override
  public void checkRegisterNumber(String registerNumber) throws UseCaseException
  {
    String errorCode = "XYP001";

    String errorMessage = getMessage(errorCode);

    if (StringUtils.isBlank(registerNumber))
    {
      throw new UseCaseException(errorMessage);
    }
    if (10 != registerNumber.length())
    {
      throw new UseCaseException(errorMessage);
    }
    if (!registerNumber.matches(REGISTER_REG_EXP))
    {
      throw new UseCaseException(errorMessage);
    }
  }
}
