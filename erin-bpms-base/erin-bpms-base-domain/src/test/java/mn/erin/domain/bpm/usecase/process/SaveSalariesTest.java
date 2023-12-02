package mn.erin.domain.bpm.usecase.process;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.AuthorizationService;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.salary.CalculatedSalaryInfo;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProcessRepository;

/**
 * @author Zorig
 */
public class SaveSalariesTest
{
  private static final String CURRENT_USER = "admin";
  private static final String PERMISSION_STR = "bpms.bpm.SaveSalaries";

  private static final BigDecimal ZERO = new BigDecimal(0);
  private static final BigDecimal NEGATIVE_ONE = new BigDecimal(-1);

  private AuthenticationService authenticationService;
  private AuthorizationService authorizationService;

  private ProcessRepository processRepository;
  private SaveSalaries useCase;

  @Before
  public void setUp()
  {
    authenticationService = Mockito.mock(AuthenticationService.class);
    authorizationService = Mockito.mock(AuthorizationService.class);

    processRepository = Mockito.mock(ProcessRepository.class);
    useCase = new SaveSalaries(authenticationService, authorizationService, processRepository);

    Mockito.when(authenticationService.getCurrentUserId()).thenReturn(CURRENT_USER);
    Mockito.when(authorizationService.hasPermission(CURRENT_USER, PERMISSION_STR)).thenReturn(true);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new SaveSalaries(authenticationService, authorizationService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_authentication_service_null()
  {
    new SaveSalaries(null, authorizationService, processRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_authorization_service_null()
  {
    new SaveSalaries(authenticationService, null, processRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryAfterTax_null() throws UseCaseException
  {
    SaveSalariesInput input = new SaveSalariesInput("123", new HashMap<>(), ZERO, null, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryAfterTax_negative() throws UseCaseException
  {
    SaveSalariesInput input = new SaveSalariesInput("123", new HashMap<>(), ZERO, NEGATIVE_ONE, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryBeforeTax_null() throws UseCaseException
  {
    SaveSalariesInput input = new SaveSalariesInput("123", new HashMap<>(), null, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryBeforeTax_negative() throws UseCaseException
  {
    SaveSalariesInput input = new SaveSalariesInput("123", new HashMap<>(), NEGATIVE_ONE, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoMap_null() throws UseCaseException
  {
    SaveSalariesInput input = new SaveSalariesInput("123", null, ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoMap_empty() throws UseCaseException
  {
    SaveSalariesInput input = new SaveSalariesInput("123", new HashMap<>(), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfo_date_null() throws UseCaseException
  {
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(null, new CalculatedSalaryInfo()), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoObject_null() throws UseCaseException
  {
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), null), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoHoat_null() throws UseCaseException
  {
    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(null);
    calculatedSalaryInfo.setNdsh(ZERO);
    calculatedSalaryInfo.setSalaryBeforeTax(ZERO);
    calculatedSalaryInfo.setSalaryAfterTax(ZERO);
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoHoat_negative() throws UseCaseException
  {
    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(NEGATIVE_ONE);
    calculatedSalaryInfo.setNdsh(ZERO);
    calculatedSalaryInfo.setSalaryBeforeTax(ZERO);
    calculatedSalaryInfo.setSalaryAfterTax(ZERO);
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoNdsh_null() throws UseCaseException
  {
    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(ZERO);
    calculatedSalaryInfo.setNdsh(null);
    calculatedSalaryInfo.setSalaryBeforeTax(ZERO);
    calculatedSalaryInfo.setSalaryAfterTax(ZERO);
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoNdsh_negative() throws UseCaseException
  {
    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(ZERO);
    calculatedSalaryInfo.setNdsh(NEGATIVE_ONE);
    calculatedSalaryInfo.setSalaryBeforeTax(ZERO);
    calculatedSalaryInfo.setSalaryAfterTax(ZERO);
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoSalaryAfterTax_null() throws UseCaseException
  {
    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(ZERO);
    calculatedSalaryInfo.setNdsh(ZERO);
    calculatedSalaryInfo.setSalaryBeforeTax(ZERO);
    calculatedSalaryInfo.setSalaryAfterTax(null);
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoSalaryAfterTax_negative() throws UseCaseException
  {
    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(ZERO);
    calculatedSalaryInfo.setNdsh(ZERO);
    calculatedSalaryInfo.setSalaryBeforeTax(ZERO);
    calculatedSalaryInfo.setSalaryAfterTax(NEGATIVE_ONE);
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoSalaryBeforeTax_null() throws UseCaseException
  {
    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(ZERO);
    calculatedSalaryInfo.setNdsh(ZERO);
    calculatedSalaryInfo.setSalaryBeforeTax(null);
    calculatedSalaryInfo.setSalaryAfterTax(ZERO);
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test(expected = UseCaseException.class)
  public void when_salaryInfoSalaryBeforeTax_negative() throws UseCaseException
  {
    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(ZERO);
    calculatedSalaryInfo.setNdsh(ZERO);
    calculatedSalaryInfo.setSalaryBeforeTax(NEGATIVE_ONE);
    calculatedSalaryInfo.setSalaryAfterTax(ZERO);
    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    useCase.execute(input);
  }

  @Test
  public void when_number_updated_zero() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(processRepository.updateParameters(Mockito.any(), Mockito.any())).thenReturn(0);

    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(ZERO);
    calculatedSalaryInfo.setNdsh(ZERO);
    calculatedSalaryInfo.setSalaryBeforeTax(ZERO);
    calculatedSalaryInfo.setSalaryAfterTax(ZERO);

    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    SaveSalariesOutput output = useCase.execute(input);

    Mockito.verify(processRepository, Mockito.times(1)).updateParameters(Mockito.any(), Mockito.any());

    Assert.assertFalse(output.isSaved());
  }

  @Test
  public void when_number_updated_more_than_zero() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(processRepository.updateParameters(Mockito.any(), Mockito.anyMap())).thenReturn(6);

    CalculatedSalaryInfo calculatedSalaryInfo = new CalculatedSalaryInfo();
    calculatedSalaryInfo.setHhoat(ZERO);
    calculatedSalaryInfo.setNdsh(ZERO);
    calculatedSalaryInfo.setSalaryBeforeTax(ZERO);
    calculatedSalaryInfo.setSalaryAfterTax(ZERO);

    SaveSalariesInput input = new SaveSalariesInput("123", Collections.singletonMap(new Date(), calculatedSalaryInfo), ZERO, ZERO, "", "", "");
    SaveSalariesOutput output = useCase.execute(input);

    Mockito.verify(processRepository, Mockito.times(1)).updateParameters(Mockito.any(), Mockito.any());

    Assert.assertTrue(output.isSaved());
  }
}
