/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.bpms.loan.consumption.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import mn.erin.domain.bpm.model.loan.Loan;
import mn.erin.domain.bpm.model.loan.LoanClass;
import mn.erin.domain.bpm.model.loan.LoanId;

/**
 * @author Tamir
 */
public class MongolBankServiceUtilsTest
{
  public static final String UNSATISFACTORY = "Муу";
  public static final String INSECURE = "Эргэлзээтэй";

  public static final String UNCERTAIN = "Хэвийн бус";
  public static final String ATTENTIONAL = "Анхаарал хандуулах";
  public static final String NORMAL = "Хэвийн";

  @Test
  public void verify_get_lowest_loan_class()
  {
    List<Loan> loanList = getLoanList();
    LoanClass lowestLoanClass = MongolBankServiceUtils.getLowestLoanClass(loanList);

    int rank = lowestLoanClass.getRank();

    Assert.assertEquals(1, rank);
    Assert.assertEquals(UNSATISFACTORY, lowestLoanClass.getName());
  }

  private List<Loan> getLoanList()
  {
    List<Loan> loanList = new ArrayList<>();

    loanList.add(getLoan("loan1", 1, UNSATISFACTORY));
    loanList.add(getLoan("loan2", 2, INSECURE));

    loanList.add(getLoan("loan3", 3, UNCERTAIN));
    loanList.add(getLoan("loan4", 4, ATTENTIONAL));

    loanList.add(getLoan("loan5", 5, NORMAL));

    return loanList;
  }

  private Loan getLoan(String loanId, Integer rank, String loanClassName)
  {
    return new Loan(LoanId.valueOf(loanId), new LoanClass(rank, loanClassName));
  }
}
