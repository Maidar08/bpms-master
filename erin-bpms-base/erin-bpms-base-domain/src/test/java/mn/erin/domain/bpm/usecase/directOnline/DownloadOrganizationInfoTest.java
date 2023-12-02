package mn.erin.domain.bpm.usecase.directOnline;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.directOnline.DefaultParameterRepository;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.usecase.direct_online.DownloadOrganizationInfo;
import mn.erin.domain.bpm.usecase.direct_online.DownloadOrganizationInfoInput;

import static mn.erin.domain.bpm.BpmModuleConstants.DAN_REGISTER;
import static mn.erin.domain.bpm.BpmModuleConstants.ONLINE_LEASING_PROCESS_TYPE_ID;
import static mn.erin.domain.bpm.BpmModuleConstants.PHONE_NUMBER;
import static mn.erin.domain.bpm.BpmModuleConstants.PROCESS_TYPE_ID;

public class DownloadOrganizationInfoTest
{
  private DefaultParameterRepository defaultParameterRepository;
  private DirectOnlineCoreBankingService directOnlineCoreBankingService;
  private DownloadOrganizationInfo usecase;

  @Before
  public void setUp()
  {
    directOnlineCoreBankingService = Mockito.mock(DirectOnlineCoreBankingService.class);
    defaultParameterRepository = Mockito.mock(DefaultParameterRepository.class);
    usecase = new DownloadOrganizationInfo(directOnlineCoreBankingService, defaultParameterRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_default_parameter_repo_is_null()
  {
    new DownloadOrganizationInfo(directOnlineCoreBankingService, null);
  }

  @Test(expected = NullPointerException.class)
  public void when_direct_online_new_core_banking_service_is_null()
  {
    new DownloadOrganizationInfo(null, defaultParameterRepository);
  }

  @Test(expected = UseCaseException.class)
  public void when_input_is_null_throws_exception() throws UseCaseException
  {
    usecase.execute(null);
  }

  @Test
  public void when_return_success() throws BpmServiceException, UseCaseException, BpmRepositoryException
  {
    Mockito.when(directOnlineCoreBankingService.findOrganizationInfo(Mockito.anyMap())).thenReturn(getDummyOrgInfo());
    Map<String, String> input = new HashMap<>();
    input.put(PROCESS_TYPE_ID, ONLINE_LEASING_PROCESS_TYPE_ID);
    input.put(PHONE_NUMBER, "phoneNumber");
    input.put(DAN_REGISTER, "phoneNumber");
    input.put("district", "phoneNumber");
    Map<String, Object> infoMap = usecase.execute(input);
    Assert.assertEquals(infoMap.size(), 4);
    Assert.assertEquals(infoMap.get("isSalaryOrganization"), true);
    Assert.assertEquals(infoMap.get("branchNumber"), "100");

  }

  private Map<String, Object> getDummyOrgInfo()
  {
    Map<String, Object> orgInfo = new HashMap<>();
    orgInfo.put("branchNumber", "100");
    orgInfo.put("isSalaryOrganization", true);
    orgInfo.put("erate", "0");
    orgInfo.put("erate_max", "1");
    return orgInfo;
  }

  private Map<String, Object> getDummyOrgInfoNoBr()
  {
    Map<String, Object> orgInfo = new HashMap<>();
    orgInfo.put("isSalaryOrganization", true);
    orgInfo.put("erate", "0");
    orgInfo.put("erate_max", "1");
    return orgInfo;
  }

  private Map<String, Object> getDummyDefaultBranch()
  {
    Map<String, Object> dummyMap = new HashMap<>();
    dummyMap.put("defaultBranch", "DEG");
    return dummyMap;
  }
}