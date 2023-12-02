package mn.erin.bpm.domain.ohs.xac.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import mn.erin.domain.bpm.model.collateral.Collateral;
import mn.erin.domain.bpm.service.BpmServiceException;

import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.COLLATERAL;
import static mn.erin.bpm.domain.ohs.xac.constant.XacServiceConstants.EMPTY_COLLATERAL_ACC_NUMBER;

@Ignore
public class CollateralUtilTest
{
  private static final String VALID_DATE_STR = "11-MAY-18";
  private static final String ISO_DATE_FORMAT = "2020-08-15";

  @Test
  public void whenCollateralJsonArrayNull() throws BpmServiceException
  {
    JSONObject collateralJson = new JSONObject();
    JSONArray collaterals = new JSONArray();

    collateralJson.put(COLLATERAL, collaterals);

    List<Collateral> collateralList = CollateralNewCoreUtil.toCollaterals(collateralJson);
    Assert.assertEquals(0, collateralList.size());
  }

  @Test
  public void whenConvertSuccessful() throws BpmServiceException
  {
    JSONObject collateralJSON = getCollateralJSON();

    List<Collateral> collateralList = CollateralNewCoreUtil.toCollaterals(collateralJSON);

    Assert.assertEquals(2, collateralList.size());

    Collateral collateral1 = collateralList.get(0);

    String collateralId1 = collateral1.getId().getId();
    String name1 = collateral1.getName();
    BigDecimal amountOfAssessment1 = collateral1.getAmountOfAssessment();
    String accountNumber1 = collateral1.getAccountId().getId();

    Assert.assertEquals("COL0006017702", collateralId1);
    Assert.assertEquals("КЛИН-ЭЙР ХХК", name1);

    Assert.assertEquals(new BigDecimal(5400000), amountOfAssessment1);
    Assert.assertEquals(EMPTY_COLLATERAL_ACC_NUMBER, accountNumber1);

    Collateral collateral2 = collateralList.get(1);

    String collateralId2 = collateral2.getId().getId();
    String name2 = collateral2.getName();
    BigDecimal amountOfAssessment2 = collateral2.getAmountOfAssessment();
    String accountNumber2 = collateral2.getAccountId().getId();

    Assert.assertEquals("COL0008798987", collateralId2);
    Assert.assertEquals("БАТ ХҮЛЭГ ХХК", name2);

    Assert.assertEquals(new BigDecimal(9700000), amountOfAssessment2);
    Assert.assertEquals(EMPTY_COLLATERAL_ACC_NUMBER, accountNumber2);
  }

  private JSONObject getCollateralJSON()
  {
    JSONObject collateralJson = new JSONObject();
    JSONArray collaterals = new JSONArray();

    JSONObject collateral1 = createCollateralJson("COL0006017702", "КЛИН-ЭЙР ХХК", "5400000");
    JSONObject collateral2 = createCollateralJson("COL0008798987", "БАТ ХҮЛЭГ ХХК", "9700000");

    collaterals.put(collateral1);
    collaterals.put(collateral2);

    collateralJson.put(COLLATERAL, collaterals);

    return collateralJson;
  }

  private JSONObject createCollateralJson(String code, String name, String value)
  {

    JSONObject collateral = new JSONObject();

    collateral.put("code", code);
    collateral.put("name", name);
    collateral.put("value", value);

    collateral.put("ccy", "MNT");
    collateral.put("haircut", "50");
    collateral.put("avl_amount", "975000");

    collateral.put("custname", "ТВ*T***R_00060*77");
    collateral.put("startdate", "11-MAY-18");
    collateral.put("enddate", "   ");

    collateral.put("revaldate", "  ");
    collateral.put("description", "Тогтмол цалингийн орлого - КЛИН-ЭЙР ХХК    ");
    collateral.put("ownernames", "   ");

    collateral.put("linked_refno", JSONObject.NULL);
    collateral.put("accno", JSONObject.NULL);
    collateral.put("liabno", "00060177");

    return collateral;
  }

  @Test
  public void whenValidStringToLocalDate() throws BpmServiceException
  {
    LocalDate localDate = CollateralNewCoreUtil.toLocalDate(VALID_DATE_STR);

    int year = localDate.getYear();
    int month = localDate.getMonth().getValue();
    int dayOfMonth = localDate.getDayOfMonth();

    Assert.assertNotNull(localDate);
    Assert.assertEquals(2018, year);

    Assert.assertEquals(5, month);
    Assert.assertEquals(11, dayOfMonth);
  }

  @Test(expected = BpmServiceException.class)
  public void whenISODateStringToLocalDate() throws BpmServiceException
  {
    CollateralNewCoreUtil.toLocalDate(ISO_DATE_FORMAT);
  }

  @Test
  public void whenNullDateString() throws BpmServiceException
  {
    LocalDate localDate = CollateralNewCoreUtil.toLocalDate(null);

    Assert.assertNull(localDate);
  }
}
