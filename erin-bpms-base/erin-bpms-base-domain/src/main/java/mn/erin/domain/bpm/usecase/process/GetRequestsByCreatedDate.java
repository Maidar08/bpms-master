/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.process;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.aim.model.permission.Permission;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.aim.usecase.AuthorizedUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.BpmModulePermission;
import mn.erin.domain.bpm.model.process.ProcessRequest;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRequestRepository;
import mn.erin.domain.bpm.util.process.ProcessRequestUtils;

/**
 * @author Tamir
 */
public class GetRequestsByCreatedDate extends AuthorizedUseCase<GetRequestsByCreatedDateInput, Collection<ProcessRequest>>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GetRequestsByCreatedDate.class);
  private static final Permission permission = new BpmModulePermission("GetRequestsByCreatedDate");

  private final ProcessRequestRepository processRequestRepository;

  public GetRequestsByCreatedDate(AuthenticationService authenticationService,
                                  AuthorizationService authorizationService, ProcessRequestRepository processRequestRepository)
  {
    super(authenticationService, authorizationService);
    this.processRequestRepository = Objects.requireNonNull(processRequestRepository, "Process request repository is required!");
  }

  @Override
  public Permission getPermission()
  {
    return permission;
  }

  @Override
  protected Collection<ProcessRequest> executeImpl(GetRequestsByCreatedDateInput input) throws UseCaseException
  {
    validateInput(input);

    String parameterValue = input.getParameterValue();
    String startCreatedDate = input.getStartCreatedDate();
    String endCreatedDate = input.getEndCreatedDate();

    Date startDate = toDate(startCreatedDate, true);
    Date endDate = toDate(endCreatedDate, false);

    try
    {
      Collection<ProcessRequest> processRequests = processRequestRepository.findAllByCreatedDateInterval(parameterValue, startDate, endDate);
      return ProcessRequestUtils.filterProcessRequestByState(processRequests);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getMessage(), e);
    }
  }

  private void validateInput(GetRequestsByCreatedDateInput input) throws UseCaseException
  {
    if (null == input)
    {
      String errorCode = "BPMS020";
      throw new UseCaseException(errorCode, "Input cannot be null!");
    }

    if (StringUtils.isBlank(input.getParameterValue()))
    {
      throw new UseCaseException(BpmMessagesConstants.PARAMETER_NULL_CODE, BpmMessagesConstants.PARAMETER_NULL_MESSAGE);
    }

    if (StringUtils.isBlank(input.getStartCreatedDate()))
    {
      String errorCode = "BPMS041";
      throw new UseCaseException(errorCode, "Start created date cannot be blank!");
    }

    if (StringUtils.isBlank(input.getEndCreatedDate()))
    {
      String errorCode = "BPMS042";
      throw new UseCaseException(errorCode, "End created date cannot be blank!");
    }
  }

  private Date toDate(String dateValue, boolean isStartDate) throws UseCaseException
  {
    try
    {
      Date date = new Date(Long.parseLong(dateValue));
      if (isStartDate)
      {
        return new Date( date.getYear(), date.getMonth(), date.getDate(), 00, 00, 00 );
      }
      else
      {
        return new Date( date.getYear(), date.getMonth(), date.getDate(), 23, 59, 59 );
      }
    }
    catch (NumberFormatException e)
    {
      String errorCode = "BPMS003";
      throw new UseCaseException(errorCode, "Date must be timestamp string value : " + e.getMessage(), e);
    }
  }
}
