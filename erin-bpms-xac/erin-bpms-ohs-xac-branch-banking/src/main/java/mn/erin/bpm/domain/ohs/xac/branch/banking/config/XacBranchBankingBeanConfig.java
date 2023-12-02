package mn.erin.bpm.domain.ohs.xac.branch.banking.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.context.annotation.ApplicationScope;

import mn.erin.bpm.domain.ohs.xac.XacNewCoreBeanConfig;
import mn.erin.bpm.domain.ohs.xac.branch.banking.service.XacBranchBankingDocumentService;
import mn.erin.bpm.domain.ohs.xac.branch.banking.service.XacBranchBankingExcelService;
import mn.erin.bpm.domain.ohs.xac.branch.banking.service.XacBranchBankingNewCoreService;
import mn.erin.bpm.domain.ohs.xac.branch.banking.service.XacBranchBankingService;
import mn.erin.domain.aim.provider.ExtSessionInfoCache;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.bpm.provider.DefaultOTPProvider;
import mn.erin.domain.bpm.provider.OTPProvider;
import mn.erin.domain.bpm.service.BranchBankingDocumentService;
import mn.erin.domain.bpm.service.BranchBankingService;
import mn.erin.domain.bpm.service.ExcelService;
import mn.erin.domain.bpm.service.NewCoreBankingService;

/**
 * @author Tamir
 */
@Configuration
@Import(XacNewCoreBeanConfig.class)
public class XacBranchBankingBeanConfig
{
  @Bean
  @Inject
  BranchBankingService branchBankingService(AuthenticationService authenticationService, Environment environment, ExtSessionInfoCache extSessionInfoCache)
  {
    return new XacBranchBankingService(authenticationService, environment, extSessionInfoCache);
  }

  @Bean
  NewCoreBankingService branchBankingNewCoreService()
  {
    return new XacBranchBankingNewCoreService();
  }

  @Bean
  BranchBankingDocumentService branchBankingDocumentService(Environment environment)
  {
    return new XacBranchBankingDocumentService(environment);
  }

  @Bean
  ExcelService excelService()
  {
    return new XacBranchBankingExcelService();
  }

  @Bean
  @ApplicationScope
  OTPProvider otpProvider(Environment environment)
  {
    return new DefaultOTPProvider(environment);
  }
}
