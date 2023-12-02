package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import mn.erin.domain.bpm.model.branch_banking.TaxInvoice;
import mn.erin.domain.bpm.util.process.BpmUtils;

import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.EMPTY_STRING;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INVOICE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INVOICES;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INVOICE_NO;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.INVOICE_TYPE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.PAID_DATE;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.TRANSACTION_NUMBER;
import static mn.erin.bpm.domain.ohs.xac.branch.banking.constants.XacBranchBankingServiceConstants.YEAR;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.getThousandSepStrWithDigit;
import static mn.erin.domain.bpm.util.process.BpmNumberUtils.removeComma;

/**
 * @author Bilguunbor
 */
public class TaxUtil
{
  private TaxUtil()
  {
  }

  public static List<TaxInvoice> toTaxInfoList(JSONObject response)
  {

    List<TaxInvoice> taxInfoList = new ArrayList<>();
    if (response.has(INVOICE) && response.get(INVOICE) instanceof JSONObject)
    {
      JSONObject responseObject = (JSONObject) response.get(INVOICE);
      String taxPayerName = responseObject.has("taxPayerName") ? BpmUtils.toString(responseObject.get("taxPayerName")) : null;
      String taxAccountName = responseObject.has("taxAccountName") ? BpmUtils.toString(responseObject.get("taxAccountName")) : null;
      String taxTypeCode = responseObject.has("taxTypeCode") ? BpmUtils.toString(responseObject.get("taxTypeCode")) : null;
      String assetDetail = responseObject.has("assetDetail") ? BpmUtils.toString(responseObject.get("assetDetail")) : null;
      String period = responseObject.has("period") ? BpmUtils.toString(responseObject.get("period")) : null;
      String payFull = responseObject.has("payFull") ? BpmUtils.toString(responseObject.get("payFull")) : null;
      String payMore = responseObject.has("payMore") ? BpmUtils.toString(responseObject.get("payMore")) : null;
      String amountStr = responseObject.has("amount") ? BpmUtils.toString(responseObject.get("amount")) : "0";
      String amount = getThousandSepStrWithDigit(Double.parseDouble(amountStr), 2);
      String branchCode = responseObject.has("branchCode") ? BpmUtils.toString(responseObject.get("branchCode")) : null;
      String branchName = responseObject.has("branchName") ? BpmUtils.toString(responseObject.get("branchName")) : null;
      String invoiceType = responseObject.has("invoiceType") ? BpmUtils.toString(responseObject.get("invoiceType")) : null;
      String periodType = responseObject.has("periodType") ? BpmUtils.toString(responseObject.get("periodType")) : null;
      String subBranchName = responseObject.has("subBranchName") ? BpmUtils.toString(responseObject.get("subBranchName")) : null;
      String CCY = responseObject.has("CCY") ? BpmUtils.toString(responseObject.get("CCY")) : null;
      String taxTypeName = responseObject.has("taxTypeName") ? BpmUtils.toString(responseObject.get("taxTypeName")) : null;
      String year = responseObject.has("year") ? BpmUtils.toString(responseObject.get("year")) : null;
      String subBranchCode = responseObject.has("subBranchCode") ? BpmUtils.toString(responseObject.get("subBranchCode")) : null;

      String tin = responseObject.has("tin") ? BpmUtils.toString(responseObject.get("tin")) : null;
      String taxAccount = responseObject.has("taxAccount") ? BpmUtils.toString(responseObject.get("taxAccount")) : null;
      String pin = responseObject.has("pin") ? BpmUtils.toString(responseObject.get("pin")) : null;
      String stateAccount = responseObject.has("stateAccount") ? BpmUtils.toString(responseObject.get("stateAccount")) : null;
      String stateAccountName = responseObject.has("stateAccountName") ? BpmUtils.toString(responseObject.get("stateAccountName")) : null;

      TaxInvoice taxInvoice = setTaxBasicInfo(responseObject);
      taxInvoice.setTaxPayerName(taxPayerName);
      taxInvoice.setTaxAccountName(taxAccountName);
      taxInvoice.setTaxTypeCode(taxTypeCode);
      taxInvoice.setAssetDetail(assetDetail);
      taxInvoice.setPeriod(period);
      taxInvoice.setPayFull(payFull);
      taxInvoice.setPayMore(payMore);
      taxInvoice.setAmount(amount);
      taxInvoice.setBranchCode(branchCode);
      taxInvoice.setBranchName(branchName);
      taxInvoice.setInvoiceType(invoiceType);
      taxInvoice.setPeriodType(periodType);
      taxInvoice.setSubBranchName(subBranchName);
      taxInvoice.setCcy(CCY);
      taxInvoice.setTaxTypeName(taxTypeName);
      taxInvoice.setYear(year);
      taxInvoice.setSubBranchCode(subBranchCode);



      taxInvoice.setTin(tin);
      taxInvoice.setTaxAccount(taxAccount);
      taxInvoice.setPin(pin);
      taxInvoice.setStateAccount(stateAccount);
      taxInvoice.setStateAccountName(stateAccountName);
      taxInfoList.add(taxInvoice);
    }
    else if (response.has(INVOICES) && response.get(INVOICES) instanceof JSONObject)
    {
      JSONObject responseObject = (JSONObject) response.get(INVOICES);
      taxInfoList.add(setTaxBasicInfo(responseObject));
    }
    else
    {
      JSONArray responseArray = (JSONArray) response.get(INVOICES);
      for (int i = 0; i < responseArray.length(); i++)
      {
        JSONObject jsonObject = (JSONObject) responseArray.get(i);

        taxInfoList.add(setTaxBasicInfo(jsonObject));
      }
    }
    return taxInfoList;
  }

  public static Map<String, String> toTinList(JSONArray response)
  {
    Map<String, String> tinList = new HashMap<>();

    mapToTinList(tinList, response);
    return tinList;
  }

  public static Map<String, String> toTaxTransactionInfo(JSONObject responseObject)
  {
    Map<String, String> transactionInfo = new HashMap<>();
    transactionInfo.put(TRANSACTION_NUMBER, String.valueOf(responseObject.get("bankTransactionNo")));
    transactionInfo.put("creditAccountId", String.valueOf(responseObject.get("CreditAccountID")));
    transactionInfo.put("creditAccountName", String.valueOf(responseObject.get("CreditAccountname")));
    transactionInfo.put("creditAmount", String.valueOf(responseObject.get("CreditAmount")));
    transactionInfo.put("creditBranchId", String.valueOf(responseObject.get("CreditBranchID")));
    transactionInfo.put("creditCurrency", String.valueOf(responseObject.get("CreditCurrency")));
    transactionInfo.put("debitAccountId", String.valueOf(responseObject.get("DebitAccountID")));
    transactionInfo.put("debitAccountName", String.valueOf(responseObject.get("DebitAccountName")));
    transactionInfo.put("debitAmount", String.valueOf(responseObject.get("DebitAmount")));
    transactionInfo.put("debitBranchId", String.valueOf(responseObject.get("DebitBranchID")));
    transactionInfo.put("debitCurrency", String.valueOf(responseObject.get("DebitCurrency")));
    transactionInfo.put(INVOICE_NO, String.valueOf(responseObject.get("invoiceNo")));
    transactionInfo.put(PAID_DATE, String.valueOf(responseObject.get(PAID_DATE)));
    transactionInfo.put("receiveDate", String.valueOf(responseObject.get("receiveDate")));
    transactionInfo.put("statementNo", String.valueOf(responseObject.get("statementNo")));
    transactionInfo.put("trxdescription", String.valueOf(responseObject.get("trxdescription")));
    transactionInfo.put("valueDate", String.valueOf(responseObject.get("valueDate")));
    return transactionInfo;
  }


  private static void mapToTinList(Map<String, String> tinList, JSONArray response)
  {
    for (int index = 0; index < response.length(); index++)
    {
      if (response.get(index) instanceof JSONArray)
      {
        mapToTinList(tinList, (JSONArray) response.get(index));
      }
      else if (response.get(index) instanceof JSONObject)
      {
        JSONObject tinObject = (JSONObject) response.get(index);

        String tin = String.valueOf(tinObject.get("tin"));
        String name = String.valueOf(tinObject.get("name"));

        tinList.put(tin, name);
      }
    }
  }

  private static TaxInvoice setTaxBasicInfo(JSONObject responseObject)
  {
    String invoiceNo = responseObject.has(INVOICE_NO) ? BpmUtils.toString(responseObject.get(INVOICE_NO)) : EMPTY_STRING;
    String invoiceType = responseObject.has(INVOICE_TYPE) ? BpmUtils.toString(responseObject.get(INVOICE_TYPE)) : EMPTY_STRING;
    String amountStr = responseObject.has("amount") ? BpmUtils.toString(responseObject.get("amount")) : EMPTY_STRING;
    String amountRemoveComma = getThousandSepStrWithDigit(Double.parseDouble(amountStr), 2);
    String amount = removeComma(amountRemoveComma);
    String invoiceDate = responseObject.has(YEAR) ? BpmUtils.toString(responseObject.get(YEAR)) : EMPTY_STRING;
    return new TaxInvoice(invoiceNo, invoiceDate, invoiceType, amount);
  }
}
