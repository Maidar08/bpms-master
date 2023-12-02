package mn.erin.bpm.domain.ohs.xac.branch.banking.util;

import java.util.List;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import mn.erin.domain.bpm.model.branch_banking.CustomInvoice;

public class CustomInvoiceUtilTest
{
  private String responseStringInvoiceArray = " {\n"
      + "    \"Source\": \"AGN\",\n"
      + "    \"Function\": \"GETINFO\",\n"
      + "    \"UserId\": \"ider\",\n"
      + "    \"SecurityCode\": \"B4BEF9C4A5569295DA028BF98666C2E9\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"RequestId\": \"132\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"LanguageId\": \"\",\n"
      + "    \"Response\": {\n"
      + "        \"invoice\": [\n"
      + "            {\n"
      + "                \"branch_name\": \"Дархан уул аймаг дахь гаалийн газар\",\n"
      + "                \"charge\": 0,\n"
      + "                \"declaration_date\": 20180906,\n"
      + "                \"invoice_num\": \"13214101017I0011902\",\n"
      + "                \"invoice_type_name\": \"Хэсэгчилсэн төлбөр\",\n"
      + "                \"payment_list\": [\n"
      + "                    {\n"
      + "                        \"bank_acc_list\": {\n"
      + "                            \"bank_acc_name\": null,\n"
      + "                            \"bank_acc_number\": 5002108757,\n"
      + "                            \"bank_code\": 80907,\n"
      + "                            \"bank_name\": \"Хас\"\n"
      + "                        },\n"
      + "                        \"payment_Acc_Name\": \"ГЕГ.Импортын барааны НӨАТ\",\n"
      + "                        \"payment_Acc_Number\": 100900000505,\n"
      + "                        \"payment_amount\": 200000\n"
      + "                    },\n"
      + "                    {\n"
      + "                        \"bank_acc_list\": {\n"
      + "                            \"bank_acc_name\": null,\n"
      + "                            \"bank_acc_number\": 5002108762,\n"
      + "                            \"bank_code\": 80907,\n"
      + "                            \"bank_name\": \"Хас\"\n"
      + "                        },\n"
      + "                        \"payment_Acc_Name\": \"ГЕГ.Автобинзен,дизель түлш ОАТ\",\n"
      + "                        \"payment_Acc_Number\": 100900000510,\n"
      + "                        \"payment_amount\": 51648450\n"
      + "                    }\n"
      + "                ],\n"
      + "                \"register_id\": 2075261,\n"
      + "                \"taxpayer_name\": \"НИК ХХК\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"branch_name\": \"Баян-Өлгий дэх гаалийн газар\",\n"
      + "                \"charge\": 0,\n"
      + "                \"declaration_date\": 20150716,\n"
      + "                \"invoice_num\": \"07214100315I3032001\",\n"
      + "                \"invoice_type_name\": \"Мэдүүлгийн төлбөрийн баримт\",\n"
      + "                \"payment_list\": [\n"
      + "                    {\n"
      + "                        \"bank_acc_list\": {\n"
      + "                            \"bank_acc_name\": null,\n"
      + "                            \"bank_acc_number\": 5001403196,\n"
      + "                            \"bank_code\": 80907,\n"
      + "                            \"bank_name\": \"Хас\"\n"
      + "                        },\n"
      + "                        \"payment_Acc_Name\": \"ГЕГ.Гаалийн бусад татвар\",\n"
      + "                        \"payment_Acc_Number\": 100900000502,\n"
      + "                        \"payment_amount\": 7000\n"
      + "                    },\n"
      + "                    {\n"
      + "                        \"bank_acc_list\": {\n"
      + "                            \"bank_acc_name\": null,\n"
      + "                            \"bank_acc_number\": 5001403200,\n"
      + "                            \"bank_code\": 80907,\n"
      + "                            \"bank_name\": \"Хас\"\n"
      + "                        },\n"
      + "                        \"payment_Acc_Name\": \"Гаалийн ерөнхий газар\",\n"
      + "                        \"payment_Acc_Number\": 100900011003,\n"
      + "                        \"payment_amount\": 1200\n"
      + "                    }\n"
      + "                ],\n"
      + "                \"register_id\": 2075261,\n"
      + "                \"taxpayer_name\": \"НИК ХХК\"\n"
      + "            },\n"
      + "            {\n"
      + "                \"branch_name\": \"Увс дахь гаалийн газар\",\n"
      + "                \"charge\": 0,\n"
      + "                \"declaration_date\": 20150615,\n"
      + "                \"invoice_num\": \"09209100515I3029101\",\n"
      + "                \"invoice_type_name\": \"Мэдүүлгийн төлбөрийн баримт\",\n"
      + "                \"payment_list\": {\n"
      + "                    \"bank_acc_list\": {\n"
      + "                        \"bank_acc_name\": null,\n"
      + "                        \"bank_acc_number\": 5001403200,\n"
      + "                        \"bank_code\": 80907,\n"
      + "                        \"bank_name\": \"Хас\"\n"
      + "                    },\n"
      + "                    \"payment_Acc_Name\": \"Гаалийн ерөнхий газар\",\n"
      + "                    \"payment_Acc_Number\": 100900011003,\n"
      + "                    \"payment_amount\": 2400\n"
      + "                },\n"
      + "                \"register_id\": 2075261,\n"
      + "                \"taxpayer_name\": \"НИК ХХК\"\n"
      + "            }\n"
      + "        ]\n"
      + "    }\n"
      + "}";

  private String responseStringInvoiceObject = "{\n"
      + "    \"Source\": \"AGN\",\n"
      + "    \"Function\": \"GETINFO\",\n"
      + "    \"UserId\": \"ider\",\n"
      + "    \"SecurityCode\": \"B4BEF9C4A5569295DA028BF98666C2E9\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"RequestId\": \"132\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"LanguageId\": \"\",\n"
      + "    \"Response\": {\n"
      + "        \"invoice\": {\n"
      + "                \"branch_name\": \"Дархан уул аймаг дахь гаалийн газар\",\n"
      + "                \"charge\": 0,\n"
      + "                \"declaration_date\": 20180906,\n"
      + "                \"invoice_num\": \"13214101017I0011902\",\n"
      + "                \"invoice_type_name\": \"Хэсэгчилсэн төлбөр\",\n"
      + "                \"payment_list\": [\n"
      + "                    {\n"
      + "                        \"bank_acc_list\": {\n"
      + "                            \"bank_acc_name\": null,\n"
      + "                            \"bank_acc_number\": 5002108757,\n"
      + "                            \"bank_code\": 80907,\n"
      + "                            \"bank_name\": \"Хас\"\n"
      + "                        },\n"
      + "                        \"payment_Acc_Name\": \"ГЕГ.Импортын барааны НӨАТ\",\n"
      + "                        \"payment_Acc_Number\": 100900000505,\n"
      + "                        \"payment_amount\": 200000\n"
      + "                    },\n"
      + "                    {\n"
      + "                        \"bank_acc_list\": {\n"
      + "                            \"bank_acc_name\": null,\n"
      + "                            \"bank_acc_number\": 5002108762,\n"
      + "                            \"bank_code\": 80907,\n"
      + "                            \"bank_name\": \"Хас\"\n"
      + "                        },\n"
      + "                        \"payment_Acc_Name\": \"ГЕГ.Автобинзен,дизель түлш ОАТ\",\n"
      + "                        \"payment_Acc_Number\": 100900000510,\n"
      + "                        \"payment_amount\": 51648450\n"
      + "                    }\n"
      + "                ],\n"
      + "                \"register_id\": 2075261,\n"
      + "                \"taxpayer_name\": \"НИК ХХК\"\n"
      + "        }\n"
      + "    }\n"
      + "}";

  @Test
  public void when_invoice_is_array()
  {
    JSONObject fullResponse = new JSONObject(responseStringInvoiceArray);
    JSONObject response = fullResponse.getJSONObject("Response");

    List<CustomInvoice> list = CustomInvoiceUtil.toCustomList(response, "123");

    Assert.assertEquals(3, list.size());
  }

  @Test
  public void when_invoice_is_object()
  {
    JSONObject fullResponse = new JSONObject(responseStringInvoiceObject);
    JSONObject response = fullResponse.getJSONObject("Response");

    List<CustomInvoice> list = CustomInvoiceUtil.toCustomList(response, "123");

    Assert.assertEquals(1, list.size());
  }
}
