package mn.erin.domain.bpm.usecase.calculations.salary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;

import static mn.erin.domain.bpm.BpmModuleConstants.CONSUMPTION_LOAN_2;
import static mn.erin.domain.bpm.BpmModuleConstants.SALARY_CALCULATION;

/**
 * @author Zorig
 */
public final class NiigmiinDaatgalCalculation
{
  private NiigmiinDaatgalCalculation()
  {
  }

  private static final Map<Integer, SocialSecurityTaxValues> SOCIAL_SECURITY_MAP = new HashMap<>();
  private static final Map<Integer, Map<Integer, SocialSecurityTaxValues>> SOCIAL_INSURANCE_PREMIUM_MAP = new HashMap<>();
  private static final Map<Integer, BigDecimal> HEALTH_INSURANCE_TAX = new HashMap<>();

  static
  {
    BigDecimal elevenAndHalfPercent = new BigDecimal(.115);
    BigDecimal twelveAndHalfPercent = new BigDecimal(.125);

    SOCIAL_SECURITY_MAP.put(2017, new SocialSecurityTaxValues(new BigDecimal(.10), new BigDecimal(240000)));
    SOCIAL_SECURITY_MAP.put(2018, new SocialSecurityTaxValues(new BigDecimal(.11), new BigDecimal(264000)));
    SOCIAL_SECURITY_MAP.put(2019, new SocialSecurityTaxValues(elevenAndHalfPercent, new BigDecimal(368000)));
    SOCIAL_SECURITY_MAP.put(2020, new SocialSecurityTaxValues(elevenAndHalfPercent, new BigDecimal(420000)));
    SOCIAL_SECURITY_MAP.put(2021, new SocialSecurityTaxValues(twelveAndHalfPercent, new BigDecimal(420000)));
    SOCIAL_SECURITY_MAP.put(2022, new SocialSecurityTaxValues(twelveAndHalfPercent, new BigDecimal(420000)));
    SOCIAL_SECURITY_MAP.put(2023, new SocialSecurityTaxValues(twelveAndHalfPercent, new BigDecimal(420000)));
    SOCIAL_SECURITY_MAP.put(2024, new SocialSecurityTaxValues(twelveAndHalfPercent, new BigDecimal(420000)));
    SOCIAL_SECURITY_MAP.put(2025, new SocialSecurityTaxValues(twelveAndHalfPercent, new BigDecimal(420000)));
    SOCIAL_SECURITY_MAP.put(2026, new SocialSecurityTaxValues(twelveAndHalfPercent, new BigDecimal(420000)));
    SOCIAL_SECURITY_MAP.put(2027, new SocialSecurityTaxValues(twelveAndHalfPercent, new BigDecimal(420000)));
    SOCIAL_SECURITY_MAP.put(2028, new SocialSecurityTaxValues(twelveAndHalfPercent, new BigDecimal(420000)));
    SOCIAL_SECURITY_MAP.put(2029, new SocialSecurityTaxValues(twelveAndHalfPercent, new BigDecimal(420000)));

    BigDecimal twoPercent = new BigDecimal(.02);
    HEALTH_INSURANCE_TAX.put(2017, twoPercent);
    HEALTH_INSURANCE_TAX.put(2018, twoPercent);
    HEALTH_INSURANCE_TAX.put(2019, twoPercent);
    HEALTH_INSURANCE_TAX.put(2020, twoPercent);
    HEALTH_INSURANCE_TAX.put(2021, twoPercent);
    HEALTH_INSURANCE_TAX.put(2022, twoPercent);
    HEALTH_INSURANCE_TAX.put(2023, twoPercent);
    HEALTH_INSURANCE_TAX.put(2024, twoPercent);
    HEALTH_INSURANCE_TAX.put(2025, twoPercent);
    HEALTH_INSURANCE_TAX.put(2026, twoPercent);
    HEALTH_INSURANCE_TAX.put(2027, twoPercent);
    HEALTH_INSURANCE_TAX.put(2028, twoPercent);
    HEALTH_INSURANCE_TAX.put(2029, twoPercent);

  }

  public static BigDecimal determineNdsh(BigDecimal singleMonthSalary, boolean isExcludedNiigmiinDaatgal, boolean isExcludedHealthInsurance, int year, int month,  DefaultParameterRepository defaultParameterRepository)
      throws UseCaseException
  {
    setSocialInsuranceDefaultValue(defaultParameterRepository);
    if (year > 2029 || year < 2017)
    {
      throw new UnsupportedOperationException("Year not supported for the calculation for NDSH. (Supported years: 2017-2029)");
    }

    SocialSecurityTaxValues socialInsurance = getSocialInsurance(year, month  + 1);

    BigDecimal maxTaxable = socialInsurance.maxTaxable;
    BigDecimal taxPercentage = socialInsurance.taxPercentage;
    //    BigDecimal maxTaxable = SOCIAL_SECURITY_MAP.get(year).maxTaxable;
//    BigDecimal taxPercentage = SOCIAL_SECURITY_MAP.get(year).taxPercentage;
    BigDecimal healthInsuranceTax = HEALTH_INSURANCE_TAX.get(year);

    if (isExcludedHealthInsurance && isExcludedNiigmiinDaatgal)
    {
      return new BigDecimal(0);
    }
    else if (!isExcludedHealthInsurance && isExcludedNiigmiinDaatgal)
    {
      return singleMonthSalary.multiply(healthInsuranceTax).setScale(16, RoundingMode.FLOOR);
    }
    else
    {
      if (singleMonthSalary.multiply(taxPercentage).compareTo(maxTaxable) == 1)
      {
        return maxTaxable.setScale(16, RoundingMode.FLOOR);
      }
      else
      {
        return singleMonthSalary.multiply(taxPercentage).setScale(16, RoundingMode.FLOOR);
      }
    }
  }

  private static void setSocialInsuranceDefaultValue(DefaultParameterRepository defaultParameterRepository) throws UseCaseException
  {
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
    GetGeneralInfoInput input = new GetGeneralInfoInput(CONSUMPTION_LOAN_2, SALARY_CALCULATION);
    Map<String, Object> defaultParameters = getGeneralInfo.execute(input);

    Map<String, Map<String, Object>> paramValueMap = (Map<String, Map<String, Object>>) defaultParameters.get(SALARY_CALCULATION);

    for (Map.Entry<String, Map<String, Object>> entry : paramValueMap.entrySet())
    {
      Map<String, Object> valueMap = entry.getValue();

      int year = Integer.parseInt(entry.getKey());

      mapToSocialInsuranceMap(year, entry.getValue());
    }
  }

  private static void mapToSocialInsuranceMap(int year, Map<String, Object> monthNdshMap)
  {
    for (Map.Entry<String, Object> entry : monthNdshMap.entrySet())
    {
      int month = Integer.parseInt(entry.getKey());
      Map<String, Object> value = (Map<String, Object>) entry.getValue();
      String socialInsurancePremiums = String.valueOf(value.get("socialInsurancePremiums"));
      double ndshDouble = Double.parseDouble(socialInsurancePremiums);

      String minimumWage = String.valueOf(value.get("minimumWage"));
      double minimumWageDouble = Double.parseDouble(minimumWage);
      if (SOCIAL_INSURANCE_PREMIUM_MAP.containsKey(year))
      {
        Map<Integer, SocialSecurityTaxValues> valuesMap = SOCIAL_INSURANCE_PREMIUM_MAP.get(year);
        valuesMap.put(month, new SocialSecurityTaxValues(new BigDecimal(ndshDouble), new BigDecimal(minimumWageDouble)));
      }
      else
      {
        Map<Integer, SocialSecurityTaxValues> valuesMap = new HashMap<>();
        valuesMap.put(month, new SocialSecurityTaxValues(new BigDecimal(ndshDouble), new BigDecimal(minimumWageDouble)));
        SOCIAL_INSURANCE_PREMIUM_MAP.put(year, valuesMap);
      }
    }
  }

  private static SocialSecurityTaxValues getSocialInsurance(int year, int month)
  {
    return SOCIAL_INSURANCE_PREMIUM_MAP.get(year).get(month);
  }

  static class SocialSecurityTaxValues
  {
    final BigDecimal taxPercentage;
    final BigDecimal maxTaxable;

    public SocialSecurityTaxValues(BigDecimal taxPercentage, BigDecimal maxTaxable)
    {
      this.taxPercentage = taxPercentage;
      this.maxTaxable = maxTaxable;
    }
  }

}
