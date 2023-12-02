package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import mn.erin.domain.bpm.service.BpmServiceException;

public class BranchBankingUtilTest
{
  private final String responseJson = "{\n"
      + "    \"Source\": \"AGN\",\n"
      + "    \"Function\": \"getAccountReference\",\n"
      + "    \"UserId\": \"devtest\",\n"
      + "    \"SecurityCode\": \"4219B932329C90D5D7C3C3514CFCE0D2\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"RequestId\": \"739afc83-58f4-b13f-d701-12fa0f173c17\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"LanguageId\": \"\",\n"
      + "    \"Response\": {\n"
      + "        \"fee\": \"3000\",\n"
      + "        \"currency\": \"\",\n"
      + "        \"branch_code\": \"\",\n"
      + "        \"toBranchCode\": \"\",\n"
      + "        \"toAccountNo\": \"\",\n"
      + "        \"toAccountCurrency\": \"\"\n"
      + "    }\n"
      + "}" ;


  @Test
  public void when_reference_is_json_object()
  {
    JSONObject fullResponse = new JSONObject(responseJson);
    JSONObject response = fullResponse.getJSONObject("Response");

    Map<String, Object> accountRef= BranchBankingUtil.toAccountReference(response);

    Assert.assertEquals(6, accountRef.size());
  }

  @Test
  public void when_search_response_is_success() throws BpmServiceException
  {
    JSONObject fullResponse = new JSONObject(searchResponceJson);
    JSONArray response = fullResponse.getJSONArray("Response");

    Map<String, Object> ussdInfo = BranchBankingUtil.toUSSDInfo(response.getJSONObject(1));
    Assert.assertEquals(16, ussdInfo.size());
  }

  private final String searchResponceJson = "{\n" +
          "    \"Source\": \"AGN\",\n" +
          "    \"Function\": \"ussdSearchUser\",\n" +
          "    \"RequestId\": \"132\",\n" +
          "    \"UserId\": \"ider\",\n" +
          "    \"RequestType\": \"A\",\n" +
          "    \"Status\": \"SUCCESS\",\n" +
          "    \"SecurityCode\": \"9B8C90A772CC58B58396F564A0554903\",\n" +
          "    \"Response\": [\n" +
          "        {\n" +
          "            \"Code\": \"1\",\n" +
          "            \"Status\": \"SUCCESS\",\n" +
          "            \"cif\": \"80000003\",\n" +
          "            \"customerName\": \"БАТ ГӨРӨӨЧ ХХК\",\n" +
          "            \"registerNo\": null,\n" +
          "            \"phoneUssd\": null,\n" +
          "            \"phoneCore\": null,\n" +
          "            \"failureLoginCnt\": null,\n" +
          "            \"lastLoggedDate\": null,\n" +
          "            \"userStatus\": null,\n" +
          "            \"regBrn\": null,\n" +
          "            \"ID\": \"10280\",\n" +
          "            \"makerDtStamp\": null,\n" +
          "            \"makerId\": null,\n" +
          "            \"Accounts\": {\n" +
          "                \"Account\": [\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002417\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002390\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002761\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002416\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002346\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002350\",\n" +
          "                        \"CurrencyCode\": \"CNY\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002354\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002410\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BH100\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002337\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002400\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002560\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"EE31\",\n" +
          "                        \"AccountType\": \"LAA\",\n" +
          "                        \"AccountNumber\": \"1020000045\",\n" +
          "                        \"CurrencyCode\": \"USD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002391\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002355\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002413\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA110\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000002486\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA200\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001844\",\n" +
          "                        \"CurrencyCode\": \"CAD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA300\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001702\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002529\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002491\",\n" +
          "                        \"CurrencyCode\": \"USD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002347\",\n" +
          "                        \"CurrencyCode\": \"CNY\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002348\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA270\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001843\",\n" +
          "                        \"CurrencyCode\": \"AUD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002561\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002402\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA290\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001703\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002762\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002394\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002401\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002352\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002511\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA310\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001701\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002526\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002528\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002487\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002488\",\n" +
          "                        \"CurrencyCode\": \"USD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002351\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002506\",\n" +
          "                        \"CurrencyCode\": \"USD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002490\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002415\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002353\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AB100\",\n" +
          "                        \"AccountType\": \"SBA\",\n" +
          "                        \"AccountNumber\": \"5000002689\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002520\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002630\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002412\",\n" +
          "                        \"CurrencyCode\": \"CNY\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002414\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002389\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA200\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001991\",\n" +
          "                        \"CurrencyCode\": \"JPY\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002500\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002411\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002349\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002519\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    }\n" +
          "                ]\n" +
          "            }\n" +
          "        },\n" +
          "        {\n" +
          "            \"Code\": \"1\",\n" +
          "            \"Status\": \"SUCCESS\",\n" +
          "            \"cif\": \"80000003\",\n" +
          "            \"customerName\": \"БАТ ГӨРӨӨЧ ХХК\",\n" +
          "            \"registerNo\": \"6616453\",\n" +
          "            \"phoneUssd\": null,\n" +
          "            \"phoneCore\": null,\n" +
          "            \"failureLoginCnt\": null,\n" +
          "            \"lastLoggedDate\": null,\n" +
          "            \"userStatus\": null,\n" +
          "            \"regBrn\": null,\n" +
          "            \"ID\": \"10280\",\n" +
          "            \"makerDtStamp\": null,\n" +
          "            \"makerId\": null,\n" +
          "            \"Accounts\": {\n" +
          "                \"Account\": [\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002417\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002390\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002761\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002416\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002346\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002350\",\n" +
          "                        \"CurrencyCode\": \"CNY\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002354\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002410\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BH100\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002337\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002400\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002560\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"EE31\",\n" +
          "                        \"AccountType\": \"LAA\",\n" +
          "                        \"AccountNumber\": \"1020000045\",\n" +
          "                        \"CurrencyCode\": \"USD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002391\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002355\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002413\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA110\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000002486\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA200\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001844\",\n" +
          "                        \"CurrencyCode\": \"CAD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA300\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001702\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002529\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002491\",\n" +
          "                        \"CurrencyCode\": \"USD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002347\",\n" +
          "                        \"CurrencyCode\": \"CNY\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002348\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA270\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001843\",\n" +
          "                        \"CurrencyCode\": \"AUD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002561\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002402\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA290\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001703\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002762\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002394\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002401\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002352\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002511\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA310\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001701\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002526\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002528\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002487\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002488\",\n" +
          "                        \"CurrencyCode\": \"USD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002351\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002506\",\n" +
          "                        \"CurrencyCode\": \"USD\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002490\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002415\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002353\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AB100\",\n" +
          "                        \"AccountType\": \"SBA\",\n" +
          "                        \"AccountNumber\": \"5000002689\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002520\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA230\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002630\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002412\",\n" +
          "                        \"CurrencyCode\": \"CNY\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA220\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002414\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BG200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002389\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"AA200\",\n" +
          "                        \"AccountType\": \"CAA\",\n" +
          "                        \"AccountNumber\": \"5000001991\",\n" +
          "                        \"CurrencyCode\": \"JPY\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002500\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002411\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"BA200\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002349\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    },\n" +
          "                    {\n" +
          "                        \"IsRegistered\": \"N\",\n" +
          "                        \"IsPrimaryAccount\": \"N\",\n" +
          "                        \"ProductCode\": \"CF240\",\n" +
          "                        \"AccountType\": \"TUA\",\n" +
          "                        \"AccountNumber\": \"5000002519\",\n" +
          "                        \"CurrencyCode\": \"MNT\"\n" +
          "                    }\n" +
          "                ]\n" +
          "            }\n" +
          "        }\n" +
          "    ]\n" +
          "}";
}
