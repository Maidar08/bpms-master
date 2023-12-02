package mn.erin.bpm.domain.ohs.xac;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import mn.erin.bpm.domain.ohs.xac.repository.XacErrorMessageRepository;
import mn.erin.bpm.domain.ohs.xac.repository.XacTaskFormRepository;
import mn.erin.bpm.domain.ohs.xac.repository.XacVariableRepository;
import mn.erin.bpm.domain.ohs.xac.service.XacEncryptionService;
import mn.erin.common.mail.EmailService;
import mn.erin.domain.aim.service.AuthenticationService;
import mn.erin.domain.aim.service.EncryptionService;
import mn.erin.domain.bpm.repository.BiPathRepository;
import mn.erin.domain.bpm.repository.ErrorMessageRepository;
import mn.erin.domain.bpm.repository.TaskFormRepository;
import mn.erin.domain.bpm.repository.VariableRepository;
import mn.erin.domain.bpm.service.BnplCoreBankingService;
import mn.erin.domain.bpm.service.CaseService;
import mn.erin.domain.bpm.service.CoreBankingService;
import mn.erin.domain.bpm.service.CustomerService;
import mn.erin.domain.bpm.service.DirectOnlineCoreBankingService;
import mn.erin.domain.bpm.service.NewCoreBankingService;
import mn.erin.domain.bpm.service.OrganizationService;

/**
 * @author EBazarragchaa
 */
@Configuration
public class XacNewCoreBeanConfig
{
  @Bean
  @Inject
  NewCoreBankingService newCoreBankingService(Environment environment, @Autowired AuthenticationService authenticationService)
  {
    return new XacNewCoreBankingService(environment, authenticationService);
  }

  @Bean
  DirectOnlineCoreBankingService directOnlineCoreBankingService(Environment environment, AuthenticationService authenticationService)
  {
    return new XacDirectOnlineCoreBankingService(environment, authenticationService);
  }

  @Bean
  BnplCoreBankingService bnplCoreBankingService(Environment environment, AuthenticationService authenticationService)
  {
    return new XacBnplCoreBankingService(environment, authenticationService);
  }

  @Bean
  @Inject
  CoreBankingService coreBankingService(Environment environment, @Autowired AuthenticationService authenticationService)
  {
    return new XacCoreBankingService(environment, authenticationService);
  }

  @Bean
  @Inject
  XacDocumentService documentService(Environment environment, @Autowired CaseService caseService, @Autowired AuthenticationService authenticationService, BiPathRepository biPathRepository)
  {
    return new XacDocumentService(environment, caseService, authenticationService, biPathRepository);
  }

  @Bean
  @Inject
  XacMongolBankService loanService(Environment environment, @Autowired AuthenticationService authenticationService)
  {
    return new XacMongolBankService(environment, authenticationService);
  }

  @Bean
  @Inject
  CustomerService customerService(Environment environment, @Autowired AuthenticationService authenticationService)
  {
    return new XacXypCustomerService(environment, authenticationService);
  }

  @Bean
  @Inject
  OrganizationService organizationService(Environment environment, @Autowired AuthenticationService authenticationService)
  {
    return new XacOrganizationService(environment, authenticationService);
  }

  @Bean
  TaskFormRepository taskFormRepository()
  {
    return new XacTaskFormRepository();
  }

  @Bean
  VariableRepository variableRepository()
  {
    return new XacVariableRepository();
  }

  @Bean
  ErrorMessageRepository errorMessageRepository()
  {
    return new XacErrorMessageRepository();
  }

  @Bean
  EmailService emailService(Environment environment)
  {
    return new XacEmailService(environment);
  }

  @Bean
  EncryptionService encryptionService(Environment environment)
  {
    return new XacEncryptionService(environment);
  }

  @Bean
  XacMessagingService xacMessagingService(Environment environment, AuthenticationService authenticationService)
  {
    return new XacMessagingService(environment, authenticationService);
  }
}
