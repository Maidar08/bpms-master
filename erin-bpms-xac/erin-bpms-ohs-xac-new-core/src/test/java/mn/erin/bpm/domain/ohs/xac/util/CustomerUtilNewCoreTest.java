package mn.erin.bpm.domain.ohs.xac.util;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import mn.erin.domain.base.model.person.PersonInfo;
import mn.erin.domain.bpm.model.customer.Customer;

import static mn.erin.domain.bpm.BpmModuleConstants.CUSTOMER;

@Ignore
public class CustomerUtilNewCoreTest
{
  private static final String registerRetailResponseString = "{\n"
      + "    \"Source\": \"COR\",\n"
      + "    \"Function\": \"GetCustList\",\n"
      + "    \"RequestId\": \"e4bf8903-df5d-e40f-1b48-ac126871b6a7\",\n"
      + "    \"UserId\": \"GONSULO\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"SecurityCode\": \"CD7F5F4B1C777FA203585E79664748F9\",\n"
      + "    \"Response\": {\n"
      + "        \"Customers\": [\n"
      + "            {\n"
      + "                \"CustId\": \"R00000375\",\n"
      + "                \"CustType\": \"Retail\",\n"
      + "                \"RegistrationNumber\": \"СЩ97082714\",\n"
      + "                \"BranchID\": \"101\",\n"
      + "                \"ShortName\": \"БАТ\",\n"
      + "                \"Name\": \"БОЛД БАТ\",\n"
      + "                \"LastName\": \"БАТ\",\n"
      + "                \"Firstname\": \"БОЛД\",\n"
      + "                \"MiddleName\": null,\n"
      + "                \"Address\": \"11000  14000  14000ГУДАМЖ  null\",\n"
      + "                \"Nationality\": \"MN\",\n"
      + "                \"BirthDt\": \"1988-08-16T00:00:00.000\",\n"
      + "                \"Phone\": 88888888,\n"
      + "                \"Mail\": null\n"
      + "            }\n"
      + "        ]\n"
      + "    }\n"
      + "}";

  private static final String registerRetailResponseStringInvalid = "{\n"
      + "    \"Source\": \"COR\",\n"
      + "    \"Function\": \"GetCustList\",\n"
      + "    \"RequestId\": \"e4bf8903-df5d-e40f-1b48-ac126871b6a7\",\n"
      + "    \"UserId\": \"GONSULO\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"SecurityCode\": \"CD7F5F4B1C777FA203585E79664748F9\",\n"
      + "    \"Response\": {\n"
      + "        \"Customers\": [\n"
      + "            {\n"
      + "                \"CustId\": \"R00000375\",\n"
      + "                \"CustType\": \"Retail\",\n"
      + "                \"RegistrationNumber\": \"СЩ97082714\",\n"
      + "                \"BranchID\": \"101\",\n"
      + "                \"ShortName\": null,\n"
      + "                \"Name\": null,\n"
      + "                \"LastName\": null,\n"
      + "                \"Firstname\": null,\n"
      + "                \"MiddleName\": null,\n"
      + "                \"Address\": \"11000  14000  14000ГУДАМЖ  null\",\n"
      + "                \"Nationality\": \"MN\",\n"
      + "                \"BirthDt\": \"1988-08-16T00:00:00.000\",\n"
      + "                \"Phone\": 88888888,\n"
      + "                \"Mail\": null\n"
      + "            }\n"
      + "        ]\n"
      + "    }\n"
      + "}";

  private static final String registerCorporateResponseString = "{\n"
      + "    \"Source\": \"COR\",\n"
      + "    \"Function\": \"GetCustList\",\n"
      + "    \"RequestId\": \"e4bf8903-df5d-e40f-1b48-ac126871b6a7\",\n"
      + "    \"UserId\": \"GONSULO\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"SecurityCode\": \"CD7F5F4B1C777FA203585E79664748F9\",\n"
      + "    \"Response\": {\n"
      + "        \"Customers\": [\n"
      + "            {\n"
      + "                \"CustId\": \"C00000208\",\n"
      + "                \"CustType\": \"Corporate\",\n"
      + "                \"RegistrationNumber\": 6043534,\n"
      + "                \"BranchID\": \"101\",\n"
      + "                \"ShortName\": 6043534,\n"
      + "                \"LastName\": \"ERIN SYSTMEMS LLC\",\n"
      + "                \"Firstname\": null,\n"
      + "                \"MiddleName\": null,\n"
      + "                \"Address\": \"11000  17000  17000KHAN-UUL DISTRICT, 15TH KHOROO  null\",\n"
      + "                \"Nationality\": null,\n"
      + "                \"BirthDt\": \"2017-08-16T00:00:00.000\",\n"
      + "                \"Phone\": 88970850,\n"
      + "                \"Mail\": \"TAMIR.BATMAGNAI@ERIN.SYSTEMS\"\n"
      + "            }\n"
      + "        ]\n"
      + "    }\n"
      + "}";

  private static final String registerCorporateResponseStringInvalid = "{\n"
      + "    \"Source\": \"COR\",\n"
      + "    \"Function\": \"GetCustList\",\n"
      + "    \"RequestId\": \"e4bf8903-df5d-e40f-1b48-ac126871b6a7\",\n"
      + "    \"UserId\": \"GONSULO\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"SecurityCode\": \"CD7F5F4B1C777FA203585E79664748F9\",\n"
      + "    \"Response\": {\n"
      + "        \"Customers\": [\n"
      + "            {\n"
      + "                \"CustId\": \"C00000208\",\n"
      + "                \"CustType\": \"Corporate\",\n"
      + "                \"RegistrationNumber\": 6043534,\n"
      + "                \"BranchID\": \"101\",\n"
      + "                \"ShortName\": 6043534,\n"
      + "                \"LastName\": null,\n"
      + "                \"Firstname\": null,\n"
      + "                \"MiddleName\": null,\n"
      + "                \"Address\": \"11000  17000  17000KHAN-UUL DISTRICT, 15TH KHOROO  null\",\n"
      + "                \"Nationality\": null,\n"
      + "                \"BirthDt\": \"2017-08-16T00:00:00.000\",\n"
      + "                \"Phone\": 88970850,\n"
      + "                \"Mail\": \"TAMIR.BATMAGNAI@ERIN.SYSTEMS\"\n"
      + "            }\n"
      + "        ]\n"
      + "    }\n"
      + "}";

  private static final String cifResponseString = "{\n"
      + "    \"Source\": \"COR\",\n"
      + "    \"Function\": \"getCustCorpInfo\",\n"
      + "    \"RequestId\": \"e4bf8903-df5d-e40f-1b48-ac126871b6a7\",\n"
      + "    \"UserId\": \"GONSULO\",\n"
      + "    \"RequestType\": \"A\",\n"
      + "    \"Status\": \"SUCCESS\",\n"
      + "    \"SecurityCode\": \"8D849AAA147D171F83815F6483D1A840\",\n"
      + "    \"Response\": {\n"
      + "        \"CustomerID\": \"C00000021\",\n"
      + "        \"CustomerType\": \"Corporate\",\n"
      + "        \"BranchID\": \"BRANCH1\",\n"
      + "        \"CustomerName\": \"ЧИНГЭЛТЭЙ ДҮҮРГИЙН ЗАСАГ ДАРГЫН ТАМГЫН ГАЗАР\",\n"
      + "        \"AddressRegistered\": \"63000  63070  DDM JDJAJALL 1233456  null\",\n"
      + "        \"AddressCurrent\": \"\",\n"
      + "        \"Telephone\": \"2.5210635E7\",\n"
      + "        \"Work1\": \"\",\n"
      + "        \"Work2\": \"\",\n"
      + "        \"COM1\": \"\",\n"
      + "        \"Fax\": \"\",\n"
      + "        \"Email\": \"CHINGELTEI@GOV.MN\",\n"
      + "        \"Segment\": \"C10\",\n"
      + "        \"SubSegment\": \"null\",\n"
      + "        \"RegisterID\": \"9063527\",\n"
      + "        \"IncorpDt\": \"2010-10-10T00:00:00.000\",\n"
      + "        \"KYCStatus\": \"A\",\n"
      + "        \"LegalEntityType\": \"TB01\",\n"
      + "        \"ShortName\": \"null\",\n"
      + "        \"RecordStatus\": \"\",\n"
      + "        \"UBG\": \"9.01173974E8\",\n"
      + "        \"TotalFundBase\": \"\",\n"
      + "        \"Health_Desc\": \"null\",\n"
      + "        \"CustHealthCode\": \"null\",\n"
      + "        \"SwiftCodeInd\": \"null\",\n"
      + "        \"Cust_Swift_Code_Desc\": \"null\",\n"
      + "        \"IndustryType\": \"BOC\",\n"
      + "        \"CustNetWorth\": \"\",\n"
      + "        \"CurCode\": \"MNT\",\n"
      + "        \"CountryOfIncorp\": \"null\",\n"
      + "        \"Priority\": \"null\",\n"
      + "        \"NativeLangCode\": \"null\",\n"
      + "        \"InlandTradeAllowed\": \"\",\n"
      + "        \"Currency\": [\n"
      + "            {\n"
      + "                \"MiscellaneousID\": 1516,\n"
      + "                \"DelFlag\": null,\n"
      + "                \"CurCode\": \"MNT\",\n"
      + "                \"CreditDiscountPcnt\": 0.00,\n"
      + "                \"DebitDiscountPcnt\": 0.00,\n"
      + "                \"WithholdingTaxPcnt\": 0.00,\n"
      + "                \"WithHoldingTaxFlrLimit\": 0.00,\n"
      + "                \"PrefExpDt\": \"2099-12-31T00:00:00.000\",\n"
      + "                \"PrefType\": {\n"
      + "                    \"Operation\": null,\n"
      + "                    \"Value\": \"CURRENCY\"\n"
      + "                }\n"
      + "            },\n"
      + "            {\n"
      + "                \"MiscellaneousID\": 1517,\n"
      + "                \"DelFlag\": null,\n"
      + "                \"CurCode\": \"USD\",\n"
      + "                \"CreditDiscountPcnt\": 0.00,\n"
      + "                \"DebitDiscountPcnt\": 0.00,\n"
      + "                \"WithholdingTaxPcnt\": 0.00,\n"
      + "                \"WithHoldingTaxFlrLimit\": 0.00,\n"
      + "                \"PrefExpDt\": \"2099-12-31T00:00:00.000\",\n"
      + "                \"PrefType\": {\n"
      + "                    \"Operation\": null,\n"
      + "                    \"Value\": \"CURRENCY\"\n"
      + "                }\n"
      + "            },\n"
      + "            {\n"
      + "                \"MiscellaneousID\": 1518,\n"
      + "                \"DelFlag\": null,\n"
      + "                \"CurCode\": \"AUD\",\n"
      + "                \"CreditDiscountPcnt\": 0.00,\n"
      + "                \"DebitDiscountPcnt\": 0.00,\n"
      + "                \"WithholdingTaxPcnt\": 0.00,\n"
      + "                \"WithHoldingTaxFlrLimit\": 0.00,\n"
      + "                \"PrefExpDt\": \"2099-12-31T00:00:00.000\",\n"
      + "                \"PrefType\": {\n"
      + "                    \"Operation\": null,\n"
      + "                    \"Value\": \"CURRENCY\"\n"
      + "                }\n"
      + "            }\n"
      + "        ]\n"
      + "    }\n"
      + "}";

  @Test
  public void verify_retail_customer_info_by_reg()
  {
    JSONObject responseJson = new JSONObject(registerRetailResponseString);
    JSONObject customerJson = (JSONObject) responseJson.get("Response");

    JSONArray customerInfoArray = customerJson.getJSONArray("Customers");
    JSONObject customerInfo = customerInfoArray.getJSONObject(0);

    Customer customer = CustomerUtilNewCore.toRetailCustomer(customerInfo);

    Assert.assertNotNull(customer);

    PersonInfo personInfo = customer.getPersonInfo();

    String firstName = personInfo.getFirstName();
    String lastName = personInfo.getLastName();

    Assert.assertEquals(customerInfo.getString("Firstname"), firstName);
    Assert.assertEquals(customerInfo.getString("LastName"), lastName);

    String cifNumber = customer.getCustomerNumber();
    String regNumber = customer.getId().getId();

    Assert.assertEquals(customerInfo.getString("CustId"), cifNumber);
    Assert.assertNotNull("СЩ97082714", regNumber);
  }

  @Test
  public void verify_corporate_customer_info_by_reg()
  {
    JSONObject responseJson = new JSONObject(registerCorporateResponseString);
    JSONObject customerJson = (JSONObject) responseJson.get("Response");

    JSONArray customerInfoArray = customerJson.getJSONArray("Customers");
    JSONObject customerInfo = customerInfoArray.getJSONObject(0);

    Customer customer = CustomerUtilNewCore.toCorporateCustomer(customerInfo);

    Assert.assertNotNull(customer);

    PersonInfo personInfo = customer.getPersonInfo();

    String displayName = personInfo.getFirstName();
    String regNumber = customer.getId().getId();

    Assert.assertNotNull("ERIN SYSTMEMS LLC", displayName);
    Assert.assertNotNull("6043534", regNumber);

    String cifNumber = customer.getCustomerNumber();

    Assert.assertEquals(customerInfo.getString("CustId"), cifNumber);
  }

  @Test
  public void when_retails_throws_bpm_service_exception()
  {
    JSONObject responseJson = new JSONObject(registerRetailResponseStringInvalid);
    JSONObject customerJson = (JSONObject) responseJson.get("Response");

    JSONArray customerInfoArray = customerJson.getJSONArray("Customers");
    JSONObject customerInfo = customerInfoArray.getJSONObject(0);

    Customer customer = CustomerUtilNewCore.toCorporateCustomer(customerInfo);

    Assert.assertNull(customer);
  }

  @Test
  public void when_corporate_throws_bpm_service_exception()
  {
    JSONObject responseJson = new JSONObject(registerCorporateResponseStringInvalid);
    JSONObject customerJson = (JSONObject) responseJson.get("Response");

    JSONArray customerInfoArray = customerJson.getJSONArray("Customers");
    JSONObject customerInfo = customerInfoArray.getJSONObject(0);

    Customer customer = CustomerUtilNewCore.toCorporateCustomer(customerInfo);

    Assert.assertNull(customer);
  }

  @Test
  public void verify_customer_info_by_cif()
  {
    JSONObject responseJson = new JSONObject(cifResponseString);
    JSONObject customerJson = (JSONObject) responseJson.get("Response");

    String registerNumber = customerJson.getString("RegisterID");

    final Map<String, Object> customerInfoMap = CustomerUtilNewCore.toCustomerInfo(customerJson, registerNumber);
    Customer customer = (Customer) customerInfoMap.get(CUSTOMER);

    Assert.assertNotNull(customer.getCustomerNumber());
    Assert.assertEquals(customerJson.getString("RegisterID"), customer.getId().getId());
  }
}
