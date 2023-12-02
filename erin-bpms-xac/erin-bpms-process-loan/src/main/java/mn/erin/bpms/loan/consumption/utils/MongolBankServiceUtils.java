/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;

import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.KEY_REPLACEMENT_HTML_STRING;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.KEY_REPLACEMENT_PATH;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.KEY_TARGET_HTML_STRING;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.KEY_TARGET_PATH;
import static mn.erin.bpms.loan.consumption.constant.CamundaMongolBankConstants.UNCERTAINLY;

/**
 * @author Tamir
 */
public final class MongolBankServiceUtils
{
  private static final Logger LOGGER = LoggerFactory.getLogger(MongolBankServiceUtils.class);

  private MongolBankServiceUtils()
  {

  }

  public static void replaceMongolBankHtmlValues(Environment environment, String bytesAsString, DelegateExecution execution, String variableId)
  {
    if (null == environment)
    {
      LOGGER.error("########## Spring environment is null during download MONGOL BANK ENQUIRE.");
      return;
    }

    LOGGER.info("###### Gets MONGOL BANK replacement values from properties file.");

    String targetPath = environment.getProperty(KEY_TARGET_PATH);
    LOGGER.info("#### TARGET PATH = [{}]", targetPath);

    String replacementPath = environment.getProperty(KEY_REPLACEMENT_PATH);
    LOGGER.info("#### REPLACEMENT PATH = [{}]", replacementPath);

    String targetHtml = environment.getProperty(KEY_TARGET_HTML_STRING);
    LOGGER.info("#### TARGET HTML = [{}]", targetHtml);

    String replacementHtml = environment.getProperty(KEY_REPLACEMENT_HTML_STRING);
    LOGGER.info("#### REPLACEMENT HTML = [{}]", replacementHtml);

    if (null == targetPath || !bytesAsString.contains(targetPath))
    {
      LOGGER.warn("######### MONGOL BANK REPLACEMENT TARGET PATH IS NULL or HTML STRING NOT CONTAINING TARGET VALUE.");
      return;
    }

    if (bytesAsString.contains(targetPath))
    {
      if (null != replacementPath && null != targetHtml && null != replacementHtml)
      {
        String htmlFileAsString = bytesAsString.replace(targetPath, replacementPath).replace(targetHtml, replacementHtml);
        LOGGER.info("######## SETS LOAN ENQUIRE HTML VARIABLE");

        byte[] enquireBytes = htmlFileAsString.getBytes(StandardCharsets.UTF_8);
        execution.setVariable(variableId, enquireBytes);

        LOGGER.info("############# Successful downloaded loan enquire HTML from Mongol bank.");
      }
    }
  }

  public static LoanClass getLowestLoanClass(List<Loan> loanList)
  {
    if (loanList.isEmpty())
  {
      return new LoanClass(10, UNCERTAINLY);
    }

    List<Integer> ranks = new ArrayList<>();

    for (Loan loan : loanList)
    {
      LoanClass loanClass = loan.getLoanClass();
      int rank = loanClass.getRank();
      ranks.add(rank);
    }

    Collections.sort(ranks);

    for (Loan loan : loanList)
    {
      LoanClass loanClass = loan.getLoanClass();

      // verifies minimum rank
      if (ranks.get(0).equals(loanClass.getRank()))
      {
        return loanClass;
      }
    }
    return null;
  }
}
