/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.request.webapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.bpms.loan.request.webapp.controller.RestLoanRequest;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;

import static mn.erin.domain.bpm.BpmModuleConstants.ISO_SIMPLE_DATE_FORMATTER;
import static mn.erin.domain.bpm.BpmModuleConstants.ORGANIZATION_REGISTER_NUMBER_REGEX;

/**
 * @author Tamir
 */
public final class LoanRequestRestUtil
{
  private static final Logger LOGGER = LoggerFactory.getLogger(LoanRequestRestUtil.class);

  public static final String XAC_TENANT_ID = "xac";
  public static final String DECODED_AUTH_SEPARATOR = ":";
  public static final String ENCODED_AUTH_SEPARATOR = "\\s+";

  private LoanRequestRestUtil()
  {

  }

  public static void validateOrganizationRegNumber(String organizationRegNumber)
  {
    Validate.matchesPattern(organizationRegNumber, ORGANIZATION_REGISTER_NUMBER_REGEX, "Invalid organization registerNumber format!");
  }

  public static boolean isDigits(String value)
  {
    if (null == value)
    {
      return false;
    }

    return value.chars().allMatch(Character::isDigit);
  }

  public static Map<String, Object> isAuthenticatedUser(AuthenticationService authenticationService, String authString)
  {
    String[] authParts = authString.split(ENCODED_AUTH_SEPARATOR);
    String authInfo = authParts[1];

    byte[] bytes = null;

    Base64.Decoder decoder = Base64.getDecoder();
    bytes = decoder.decode(authInfo);

    String decodedAuth = new String(bytes);
    String[] userAuthentication = decodedAuth.split(DECODED_AUTH_SEPARATOR);

    String userName = userAuthentication[0];
    String password = userAuthentication[1];

    String token = null;

    try
    {
      token = authenticationService.authenticate(XAC_TENANT_ID, userName, password, false);
    }
    catch (Exception e)
    {
      LOGGER.error(e.getMessage(), e);
      Map<String, Object> response = new HashMap<>();
      response.put("isAuthenticated", false);
      response.put("message", e.getMessage());
      return response;
    }
    Map<String, Object> response = new HashMap<>();
    response.put("isAuthenticated", null != token);
    return response;
  }

  public static Collection<RestLoanRequest> descSortByCreatedDate(Collection<RestLoanRequest> restLoanRequests) throws BpmInvalidArgumentException
  {
    if (null == restLoanRequests)
    {
      return Collections.emptyList();
    }

    List<RestLoanRequest> sortedRestLoanRequests = new ArrayList<>(restLoanRequests);
    SimpleDateFormat df = new SimpleDateFormat(ISO_SIMPLE_DATE_FORMATTER);

    if (restLoanRequests.size() == 1)
    {
      try
      {
        RestLoanRequest singleElement = sortedRestLoanRequests.get(0);
        df.parse(singleElement.getCreatedDate());
      }
      catch (ParseException e)
      {
        throw new BpmInvalidArgumentException(BpmMessagesConstants.INVALID_DATE_FORMAT_CODE, BpmMessagesConstants.INVALID_DATE_FORMAT_MESSAGE);
      }
    }
    else
    {
      try
      {
        sortedRestLoanRequests.sort((d1, d2) -> {
          try
          {
            return df.parse(d2.getCreatedDate()).compareTo(df.parse(d1.getCreatedDate()));
          }
          catch (ParseException e)
          {
            throw new IllegalArgumentException(e);
          }
        });
      }
      catch (IllegalArgumentException e)
      {

        throw new BpmInvalidArgumentException(BpmMessagesConstants.INVALID_DATE_FORMAT_CODE, BpmMessagesConstants.INVALID_DATE_FORMAT_MESSAGE);
      }
    }

    return sortedRestLoanRequests;
  }
}
