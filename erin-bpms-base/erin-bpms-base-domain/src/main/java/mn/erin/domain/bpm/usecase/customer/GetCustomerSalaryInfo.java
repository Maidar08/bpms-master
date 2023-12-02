/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.salary.Organization;
import mn.erin.domain.bpm.model.salary.OrganizationId;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

/**
 * @author Tamir
 */
public class GetCustomerSalaryInfo extends AbstractUseCase<GetCustomerSalaryInfoInput, List<SalaryInfo>>
{
  private static final String NO_ORGANIZATION_ID = "NO_ORG_ID";
  private static final String NO_ORGANIZATION_NAME = "NO_ORG_NAME";

  public static final int MONTH_JANUARY = 1;

  private final CustomerService customerService;

  public GetCustomerSalaryInfo(CustomerService customerService)
  {
    this.customerService = Objects.requireNonNull(customerService, "Customer service is required!");
  }

  @Override
  public List<SalaryInfo> execute(GetCustomerSalaryInfoInput input) throws UseCaseException
  {
    if (null == input)
    {
      String errorCode = "BPMS020";
      throw new UseCaseException(errorCode, "Input cannot be null!");
    }

    String regNumber = input.getRegNumber();
    String employeeRegNumber = input.getEmployeeRegNumber();

    Integer month = input.getMonth();

    LocalDateTime currentDate = LocalDateTime.now();

    Integer currentYear = currentDate.getYear();
    Integer startYear = getStartYear(currentYear, month);

    Integer currentMonth = currentDate.getMonth().getValue();
    Integer maxMonth = currentMonth - 1;

    try
    {
      List<SalaryInfo> allSalaryInfos = customerService.getCustomerSalaryInfos(regNumber, employeeRegNumber, month);

      if (allSalaryInfos.isEmpty())
      {
        return allSalaryInfos;
      }

      List<SalaryInfo> salariesForStartYear = allSalaryInfos.stream()
          .filter(salaryInfo -> salaryInfo.getYear().equals(startYear))
          .collect(Collectors.toList());

      // removes needless salaries.
      removeNeedlesSalaries(maxMonth, salariesForStartYear, allSalaryInfos);

      return getFilledSalaryInfo(startYear, currentYear, currentMonth, maxMonth, allSalaryInfos);
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage(), e);
    }
  }

  private void removeNeedlesSalaries(Integer maxMonth, List<SalaryInfo> salariesForStarYear, List<SalaryInfo> allSalaryInfos)
  {
    for (Integer indexMonth = maxMonth; indexMonth > 0; indexMonth--)
    {
      for (SalaryInfo salaryInfo : salariesForStarYear)
      {
        if (indexMonth.equals(salaryInfo.getMonth()))
        {
          allSalaryInfos.remove(salaryInfo);
        }
      }
    }
  }

  private List<SalaryInfo> getFilledSalaryInfo(Integer startYear, Integer currentYear, Integer currentMonth, Integer maxMonth,
      List<SalaryInfo> salaryInfos)
  {
    for (Integer indexYear = startYear; indexYear <= currentYear; indexYear++)
    {
      Integer finalIndexYear = indexYear;

      List<SalaryInfo> salaryForEachYear = salaryInfos.stream()
          .filter(salaryInfo -> salaryInfo.getYear().equals(finalIndexYear))
          .collect(Collectors.toList());

      if (indexYear.equals(startYear))
      {
        fillSalaryByMonth(indexYear, currentMonth, salaryForEachYear, salaryInfos, true, false, false);
      }
      else if (indexYear.equals(currentYear))
      {
        fillSalaryByMonth(indexYear, maxMonth, salaryForEachYear, salaryInfos, false, false, true);
      }
      else
      {
        fillSalaryByMonth(indexYear, MONTH_JANUARY, salaryForEachYear, salaryInfos, false, true, false);
      }
    }

    return salaryInfos;
  }

  private void fillSalaryByMonth(Integer year, Integer startMonth, List<SalaryInfo> salaryForEachYear, List<SalaryInfo> salaryInfos, boolean isStartYear,
      boolean isMiddleYear, boolean isCurrentYear)
  {
    List<Integer> months = new ArrayList<>();

    for (SalaryInfo salaryInfo : salaryForEachYear)
    {
      months.add(salaryInfo.getMonth());
    }

    if (isStartYear || isMiddleYear)
    {
      for (int indexMonth = startMonth; indexMonth <= 12; indexMonth++)
      {
        if (!months.contains(indexMonth))
        {
          addEmptySalary(year, indexMonth, salaryInfos);
        }
      }
    }

    if (isCurrentYear)
    {
      for (Integer indexMonth = startMonth; indexMonth > 0; indexMonth--)
      {
        if (!months.contains(indexMonth))
        {
          addEmptySalary(year, indexMonth, salaryInfos);
        }
      }
    }
  }

  private void addEmptySalary(Integer year, Integer month, List<SalaryInfo> salaryInfos)
  {
    Organization organization = new Organization(OrganizationId.valueOf(NO_ORGANIZATION_ID), NO_ORGANIZATION_NAME);
    SalaryInfo salaryInfo = new SalaryInfo(BigDecimal.valueOf(0), BigDecimal.valueOf(0), organization, false, year, month);

    salaryInfos.add(salaryInfo);
  }

  private Integer getStartYear(Integer currentYear, Integer month)
  {
    switch (month)
    {
    case 12:
      return currentYear - 1;
    case 24:
      return currentYear - 2;
    case 36:
      return currentYear - 3;
    default:
      return currentYear;
    }
  }
}
