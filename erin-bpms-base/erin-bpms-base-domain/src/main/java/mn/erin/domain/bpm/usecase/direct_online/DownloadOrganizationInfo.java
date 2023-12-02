package mn.erin.domain.bpm.usecase.direct_online;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.usecase.GetGeneralInfo;
import mn.erin.domain.bpm.usecase.GetGeneralInfoInput;
import mn.erin.domain.bpm.util.process.BpmUtils;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmModuleConstants.BRANCH_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.ERATE;
import static mn.erin.domain.bpm.BpmModuleConstants.ERATE_MAX;
import static mn.erin.domain.bpm.BpmModuleConstants.INSTANT_LOAN_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.IS_SALARY_ORGANIZATION;
import static mn.erin.domain.bpm.BpmModuleConstants.PRODUCT_CATEGORY;

/**
 * @author Lkhagvadorj.A
 **/

@SuppressWarnings("unchecked")
public class DownloadOrganizationInfo extends AbstractUseCase<Map<String, String>, Map<String, Object>>
{
  private final DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private final DefaultParameterRepository defaultParameterRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadOrganizationInfo.class);

  public DownloadOrganizationInfo(DirectOnlineCoreBankingService directOnlineCoreBankingService,
      DefaultParameterRepository defaultParameterRepository)
  {
    this.directOnlineCoreBankingService = Objects.requireNonNull(directOnlineCoreBankingService, "Direct Online Core Banking Service is required!");
    this.defaultParameterRepository = Objects.requireNonNull(defaultParameterRepository, "Default Parameter Repository is required!");
  }

  @Override
  public Map<String, Object> execute(Map<String, String> input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    try
    {
      Map<String, Object> orgInfo = directOnlineCoreBankingService.findOrganizationInfo(input);
      boolean isSalaryOrganization = (boolean) orgInfo.get(IS_SALARY_ORGANIZATION);

      if (isSalaryOrganization)
      {
        double minInterestRate = Double.parseDouble(String.valueOf(orgInfo.get(ERATE)));
        double maxInterestRate = Double.parseDouble(String.valueOf(orgInfo.get(ERATE_MAX)));
        LOGGER.info("########## Found organization info with  MIN_INTEREST_RATE [{}], MAX_INTEREST_RATE [{}]", minInterestRate, maxInterestRate);

        if (!orgInfo.containsKey(BpmModuleConstants.BRANCH_NUMBER))
        {
          String defaultBranch = getDefaultBranch(BpmUtils.getValidString(input.get(PRODUCT_CATEGORY)));
          orgInfo.put(BRANCH_NUMBER, defaultBranch);
        }
      }
      else
      {
        String defaultBranch = getDefaultBranch(BpmUtils.getValidString(input.get(PRODUCT_CATEGORY)));
        orgInfo.put(BRANCH_NUMBER, defaultBranch);
      }

      return orgInfo;
    }
    catch (BpmServiceException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }

  private String getDefaultBranch(String productCategory) throws UseCaseException
  {
    GetGeneralInfo getGeneralInfo = new GetGeneralInfo(defaultParameterRepository);
    GetGeneralInfoInput input = new GetGeneralInfoInput(productCategory.equals(INSTANT_LOAN_PROCESS_TYPE_ID) ? INSTANT_LOAN_PROCESS_TYPE_ID : "OnlineSalary",
        "Default");
    Map<String, Object> defaultParameters = getGeneralInfo.execute(input);

    Map<String, Object> defaultParam = (Map<String, Object>) defaultParameters.get("Default");

    return String.valueOf(defaultParam.get("defaultBranch"));
  }
}
