/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.salary.Organization;
import mn.erin.domain.bpm.model.salary.OrganizationId;
import mn.erin.domain.bpm.model.salary.SalaryInfo;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.CustomerService;

/**
 * @author Tamir
 */
public class GetCustomerSalaryInfoTest
{
  private static final String REG_1 = "reg1";
  private static final String REG_2 = "reg2";

  private static final int MONTH_36 = 36;

  public static final String ORGANIZATION_ID = "1";
  public static final String ORGANIZATION_NAME = "testCompany";
  public static final String ORGANIZATION_REGION = "region";

  private CustomerService customerService;
  private GetCustomerSalaryInfoInput input;
  private GetCustomerSalaryInfo useCase;

  @Before
  public void setUp()
  {
    customerService = Mockito.mock(CustomerService.class);

    input = new GetCustomerSalaryInfoInput(REG_1, REG_2, MONTH_36);
    useCase = new GetCustomerSalaryInfo(customerService);
  }

  // test is correctly, but following refers current date time that is why ignoring test.
  @Ignore
  @Test
  public void verify_36_month_data() throws UseCaseException, BpmServiceException
  {
    Mockito.when(customerService.getCustomerSalaryInfos(REG_1, REG_2, MONTH_36)).thenReturn(getSalaryInfos());
    List<SalaryInfo> salaryInfos = useCase.execute(input);

    Assert.assertEquals(MONTH_36, salaryInfos.size());

    List<SalaryInfo> salaryInfos2017 = getSalaryByYear(2017, salaryInfos);
    List<SalaryInfo> salaryInfos2018 = getSalaryByYear(2018, salaryInfos);

    List<SalaryInfo> salaryInfos2019 = getSalaryByYear(2019, salaryInfos);
    List<SalaryInfo> salaryInfos2020 = getSalaryByYear(2020, salaryInfos);

    // total salary info size will be 36.
    Assert.assertEquals(36, salaryInfos.size());

    // 2017 salary info size will be9.
    Assert.assertEquals(8, salaryInfos2017.size());

    //    // 2018, 2019 salary info size will be 12.
    Assert.assertEquals(12, salaryInfos2018.size());
    Assert.assertEquals(12, salaryInfos2019.size());

    // 2020 salary info size will be 3.
    Assert.assertEquals(4, salaryInfos2020.size());

    for (SalaryInfo salaryInfo : salaryInfos2017)
    {
      if (salaryInfo.getMonth() % 2 == 0)
      {
        // empty salary infos, that is  why assert with amount 0.
        Assert.assertEquals(BigDecimal.valueOf(0), salaryInfo.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(0), salaryInfo.getSalaryFee());
      }
    }
  }

  private List<SalaryInfo> getSalaryByYear(Integer year, List<SalaryInfo> salaryInfos)
  {
    return salaryInfos.stream()
        .filter(salaryInfo -> salaryInfo.getYear().equals(year))
        .collect(Collectors.toList());
  }

  private List<SalaryInfo> getSalaryInfos()
  {
    List<SalaryInfo> salaryInfos = new ArrayList<>();
    Organization organization = new Organization(OrganizationId.valueOf(ORGANIZATION_ID), ORGANIZATION_NAME, ORGANIZATION_REGION);

    Integer startYear = 2017;
    Integer endYear = 2020;

    for (Integer index = 1; index < 13; index++)
    {
      if (index % 2 == 0)
      {
        continue;
      }
      SalaryInfo salaryInfo2017 = new SalaryInfo(BigDecimal.valueOf(2017000), BigDecimal.valueOf(270000), organization, true, startYear, index);
      SalaryInfo salaryInfo2018 = new SalaryInfo(BigDecimal.valueOf(2018000), BigDecimal.valueOf(280000), organization, true, 2018, index);
      SalaryInfo salaryInfo2019 = new SalaryInfo(BigDecimal.valueOf(2019000), BigDecimal.valueOf(290000), organization, true, 2019, index);

      salaryInfos.add(salaryInfo2017);
      salaryInfos.add(salaryInfo2018);
      salaryInfos.add(salaryInfo2019);
    }

    for (Integer index = 1; index < 4; index++)
    {
      SalaryInfo salaryInfo = new SalaryInfo(BigDecimal.valueOf(2020000), BigDecimal.valueOf(200000), organization, true, endYear, index);
      salaryInfos.add(salaryInfo);
    }

    return salaryInfos;
  }
}
