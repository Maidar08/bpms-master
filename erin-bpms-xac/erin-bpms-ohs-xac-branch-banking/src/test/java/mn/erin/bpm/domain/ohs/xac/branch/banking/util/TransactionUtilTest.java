package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import java.util.List;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import mn.erin.domain.bpm.model.branch_banking.transaction.ETransaction;
import mn.erin.domain.bpm.service.BpmServiceException;

/**
 * @author Bilguunbor
 **/

public class TransactionUtilTest
{
  private static final String RESPONSE_STRING = "{\n"
      + "    \"Source\": \"AGN\",\n"
      + "    \"Function\": \"GetTransactionEbankList\",\n"
      + "    \"RequestId\": \"739afc83-58f4-b13f-d701-12fa0f173c17\",\n"
      + "    \"UserId\": \"devtest\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"SecurityCode\": \"EF650C6D44F338FD1EA94607DC470BD6\",\n"
      + "    \"Response\": {\n"
      + "        \"Transactions\": [\n"
      + "            {\n"
      + "                \"TranID\": \"XB0001853\",\n"
      + "                \"TranDt\": \"2021-01-28T08:00:00.000+08:00\",\n"
      + "                \"TranCcy\": \"MNT\",\n"
      + "                \"TranAmount\": \"50000000\",\n"
      + "                \"Rate\": \"1\",\n"
      + "                \"Fee\": \"500000\",\n"
      + "                \"TranParticulars\": \"Loan Disbursement Debit For 1010000109\",\n"
      + "                \"UserID\": \"USER16\",\n"
      + "                \"AccountID\": \"1010000109\",\n"
      + "                \"AccountName\": \"ДЭМБЭРЭЛ ЦЭВЭЭНЖАВ\",\n"
      + "                \"ToBranchID\": \"101\",\n"
      + "                \"ToAccountID\": \"101150600005\",\n"
      + "                \"ToAccountName\": \"Салбар хоорондын тооцооны авлага, өглөг\",\n"
      + "                \"ToBankName\": \"01\",\n"
      + "                \"ToCcy\": \"MNT\",\n"
      + "                \"ToAmount\": \"500000\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"TranID\": \"XB0001853\",\n"
      + "                \"TranDt\": \"2021-01-28T08:00:00.000+08:00\",\n"
      + "                \"TranCcy\": \"MNT\",\n"
      + "                \"TranAmount\": \"50000000\",\n"
      + "                \"Rate\": \"1\",\n"
      + "                \"Fee\": \"500000\",\n"
      + "                \"TranParticulars\": \"Loan Disbursement Debit For 1010000109\",\n"
      + "                \"UserID\": \"USER16\",\n"
      + "                \"AccountID\": \"1010000109\",\n"
      + "                \"AccountName\": \"ДЭМБЭРЭЛ ЦЭВЭЭНЖАВ\",\n"
      + "                \"ToBranchID\": \"101\",\n"
      + "                \"ToAccountID\": \"5000000074\",\n"
      + "                \"ToAccountName\": \"ДЭМБЭРЭЛ ЦЭВЭЭНЖАВ\",\n"
      + "                \"ToBankName\": \"01\",\n"
      + "                \"ToCcy\": \"MNT\",\n"
      + "                \"ToAmount\": \"49500000\"\n"
      + "            }\n"
      + "        ]\n"
      + "    }\n"
      + "}";

  private static final String RESPONSE_STRING_OBJECT = "{\n"
      + "  \"Source\": \"AGN\",\n"
      + "  \"Function\": \"GetTransactionEbankList\",\n"
      + "  \"RequestId\": \"739afc83-58f4-b13f-d701-12fa0f173c17\",\n"
      + "  \"UserId\": \"devtest\",\n"
      + "  \"RequestType\": \"A\",\n"
      + "  \"Status\": \"SUCCESS\",\n"
      + "  \"SecurityCode\": \"EF650C6D44F338FD1EA94607DC470BD6\",\n"
      + "  \"Response\": {\n"
      + "    \"Transactions\": {\n"
      + "        \"TranID\": \"XB0001853\",\n"
      + "        \"TranDt\": \"2021-01-28T08:00:00.000+08:00\",\n"
      + "        \"TranCcy\": \"MNT\",\n"
      + "        \"TranAmount\": \"50000000\",\n"
      + "        \"Rate\": \"1\",\n"
      + "        \"Fee\": \"500000\",\n"
      + "        \"TranParticulars\": \"Loan Disbursement Debit For 1010000109\",\n"
      + "        \"UserID\": \"USER16\",\n"
      + "        \"AccountID\": \"1010000109\",\n"
      + "        \"AccountName\": \"ДЭМБЭРЭЛ ЦЭВЭЭНЖАВ\",\n"
      + "        \"ToBranchID\": \"101\",\n"
      + "        \"ToAccountID\": \"101150600005\",\n"
      + "        \"ToAccountName\": \"Салбар хоорондын тооцооны авлага, өглөг\",\n"
      + "        \"ToBankName\": \"01\",\n"
      + "        \"ToCcy\": \"MNT\",\n"
      + "        \"ToAmount\": \"500000\"\n"
      + "    }\n"
      + "  }\n"
      + "}";

  @Test
  public void when_transactions_is_json_array() throws BpmServiceException
  {
    JSONObject fullJson = new JSONObject(RESPONSE_STRING);
    JSONObject response = fullJson.getJSONObject("Response");

    List<ETransaction> transactionsList = TransactionUtil.toETransactionList(response, "Channel", "123");
    Assert.assertEquals(2, transactionsList.size());
  }

  @Test
  public void when_transactions_is_json_object() throws BpmServiceException
  {
    JSONObject fullJson = new JSONObject(RESPONSE_STRING_OBJECT);
    JSONObject response = fullJson.getJSONObject("Response");

    List<ETransaction> transactionsList = TransactionUtil.toETransactionList(response,  "Channel", "123");
    Assert.assertEquals(1, transactionsList.size());
  }
}
