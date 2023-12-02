/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import mn.erin.bpm.domain.ohs.xac.XacNewCoreBeanConfig;
import mn.erin.bpms.loan.consumption.listener.StartContractProcessTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadAttachmentLoanContractTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadCoOwnerContractTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadCollateralListAttachmentLoanContractTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadFiducContractTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadLoanPermissionContractTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadLoanRepaymentAfterTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadLoanRepaymentBeforeTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadLoanReportPageTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadMainContractTask;
import mn.erin.bpms.loan.consumption.service_task.DownloadPurchaseTradeContractTask;
import mn.erin.bpms.process.base.CamundaBeanConfig;
import mn.erin.domain.bpm.repository.DocumentRepository;
import mn.erin.domain.bpm.repository.LoanContractRequestRepository;
import mn.erin.domain.bpm.service.DocumentService;

/**
 * @author Sukhbat
 */
@Configuration
@Import({ CamundaBeanConfig.class, XacNewCoreBeanConfig.class })
public class CamundaLoanContractBeanConfig
{
  // Loan Contract
  @Bean
  public DownloadMainContractTask downloadMainContract(DocumentRepository documentRepository)
  {
    return new DownloadMainContractTask(documentRepository);
  }
  @Bean
  public DownloadPurchaseTradeContractTask downloadPurchaseTradeContractTask(DocumentRepository documentRepository, DocumentService documentService){
    return new DownloadPurchaseTradeContractTask(documentRepository,documentService);
  }


  @Bean
  public DownloadCoOwnerContractTask downloadCoOwnerContract(LoanContractRequestRepository loanContractRequestRepository, DocumentRepository documentRepository)
  {
    return new DownloadCoOwnerContractTask(loanContractRequestRepository, documentRepository);
  }
  @Bean
  public DownloadFiducContractTask downloadFiducContractTask(LoanContractRequestRepository loanContractRequestRepository, DocumentRepository documentRepository)
  {
    return new DownloadFiducContractTask(loanContractRequestRepository, documentRepository);
  }
  @Bean
  public DownloadAttachmentLoanContractTask downloadAttachmentLoanContractTask(LoanContractRequestRepository loanContractRequestRepository, DocumentRepository documentRepository)
  {
    return new DownloadAttachmentLoanContractTask(loanContractRequestRepository, documentRepository);
  }

  @Bean
  public DownloadLoanRepaymentBeforeTask downloadLoanRepaymentBeforeTask(DocumentRepository documentRepository){
    return  new DownloadLoanRepaymentBeforeTask(documentRepository);
  }
  @Bean
  public DownloadLoanRepaymentAfterTask downloadLoanRepaymentAfterTask(DocumentRepository documentRepository){
    return new DownloadLoanRepaymentAfterTask(documentRepository);
  }
  @Bean
  public DownloadLoanReportPageTask downloadLoanReportPageTask(LoanContractRequestRepository loanContractRequestRepository, DocumentRepository documentRepository)
  {
    return new DownloadLoanReportPageTask(loanContractRequestRepository,documentRepository);
  }
  @Bean
  public DownloadCollateralListAttachmentLoanContractTask downloadCollateralListAttachmentLoanContract(LoanContractRequestRepository loanContractRequestRepository, DocumentRepository documentRepository)
  {
    return new DownloadCollateralListAttachmentLoanContractTask(loanContractRequestRepository,documentRepository);
  }

  @Bean
  public DownloadLoanPermissionContractTask downloadLoanPermissionContractTask(DocumentRepository documentRepository)
  {
    return new DownloadLoanPermissionContractTask(documentRepository);
  }

  @Bean
  public StartContractProcessTask startContractProcessTask()
  {
    return new StartContractProcessTask();
  }
}
