package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import java.util.List;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import mn.erin.domain.bpm.model.branch_banking.TaxInvoice;

public class TaxUtilTest
{
  private final String responseStringInvoiceArray = "{\n"
      + "    \"Source\": \"AGN\",\n"
      + "    \"Function\": \"GETINVOICENOBYASSET\",\n"
      + "    \"UserId\": \"ider\",\n"
      + "    \"SecurityCode\": \"0ADAE5CF4E920E7EEAF95F2EEFB99B6A\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"RequestId\": \"132\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"LanguageId\": \"\",\n"
      + "    \"Response\": {\n"
      + "        \"invoices\": [\n"
      + "            {\n"
      + "                \"amount\": 114048,\n"
      + "                \"invoiceNo\": 1201102074112,\n"
      + "                \"taxTypeCode\": \"04010201\",\n"
      + "                \"taxTypeName\": \"Авто тээвэр, өөрөө явагч хэрэгслийн татвар\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"amount\": 43128,\n"
      + "                \"invoiceNo\": 1210102424698,\n"
      + "                \"taxTypeCode\": \"04010201\",\n"
      + "                \"taxTypeName\": \"Авто тээвэр, өөрөө явагч хэрэгслийн татвар\",\n"
      + "                \"year\": 2021\n"
      + "            }\n"
      + "        ]\n"
      + "    }\n"
      + "}" ;

  private final String responseStringInvoiceObject = "{\n"
      + "    \"Source\": \"AGN\",\n"
      + "    \"Function\": \"GETTAXINVOICEINFO\",\n"
      + "    \"UserId\": \"ider\",\n"
      + "    \"SecurityCode\": \"181A1D6FAD801BC19BAC7C420ED7AC4D\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"RequestId\": \"132\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"LanguageId\": \"\",\n"
      + "    \"Response\": {\n"
      + "        \"invoice\": {\n"
      + "            \"amount\": 114048,\n"
      + "            \"assetDetail\": \"[5]-Тээврийн хэрэгсэл;2802УБЧ;2T2ZK1BA9BC058156;RX 350;LEXUS;3040105\",\n"
      + "            \"branchCode\": 23,\n"
      + "            \"branchName\": \"Хан-Уул\",\n"
      + "            \"invoiceNo\": 1201102074112,\n"
      + "            \"invoiceType\": 1,\n"
      + "            \"payFull\": 1,\n"
      + "            \"payMore\": 0,\n"
      + "            \"pin\": \"ЧЙ86110975\",\n"
      + "            \"stateAccount\": 100200000932,\n"
      + "            \"stateAccountName\": \"НТГ. АТӨЯХАТ\",\n"
      + "            \"subBranchName\": \"1-р хороо\",\n"
      + "            \"taxAccount\": 5000000074,\n"
      + "            \"CCY\": \"MNT\",\n"
      + "            \"taxAccountName\": \"ДЭМБЭРЭЛ ЦЭВЭЭНЖАВ\",\n"
      + "            \"taxPayerName\": \"САРУУЛХҮҮ ОКТЯБРЬ\",\n"
      + "            \"taxTypeName\": \"Автотээвэр, өөрөө явагч хэрэгслийн татвар\",\n"
      + "            \"tin\": 51100946648,\n"
      + "            \"subBranchCode\": 2301,\n"
      + "            \"taxTypeCode\": \"04010201\"\n"
      + "        }\n"
      + "    }\n"
      + "}";

  @Test
  public void when_invoice_is_array()
  {
    JSONObject fullResponse = new JSONObject(responseStringInvoiceArray);
    JSONObject response = fullResponse.getJSONObject("Response");

    List<TaxInvoice> list = TaxUtil.toTaxInfoList(response);

    Assert.assertEquals(2, list.size());
  }

  @Test
  public void when_invoice_is_object()
  {
    JSONObject fullResponse = new JSONObject(responseStringInvoiceObject);
    JSONObject response = fullResponse.getJSONObject("Response");

    List<TaxInvoice> list = TaxUtil.toTaxInfoList(response);

    Assert.assertEquals(1, list.size());
  }
}
