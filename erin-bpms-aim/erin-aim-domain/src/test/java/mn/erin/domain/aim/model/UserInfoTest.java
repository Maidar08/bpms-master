package mn.erin.domain.aim.model;

import org.junit.Assert;
import org.junit.Test;

import mn.erin.domain.aim.model.user.UserInfo;

public class UserInfoTest
{

  @Test
  public void getUserNameReturnsFirstNameWhenLastNameNull()
  {
    UserInfo info = new UserInfo("Лхагва", null);
    Assert.assertEquals("Лхагва", info.getUserName());
  }

  @Test
  public void getUserNameReturnsFirstNameWhenLastNameEmpty()
  {
    UserInfo info = new UserInfo("Лхагва", "");
    Assert.assertEquals("Лхагва", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenLastNameNotBlank()
  {
    UserInfo info = new UserInfo("Лхагва", "Амар");
    Assert.assertEquals("А.Лхагва", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenFirstNameNull()
  {
    UserInfo info = new UserInfo(null, "Амар");
    Assert.assertEquals("(empty name)", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenFirstNameEmpty()
  {
    UserInfo info = new UserInfo("", "Амар");
    Assert.assertEquals("(empty name)", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenFirstNameNotBlank()
  {
    UserInfo info = new UserInfo("Лхагва", "Амар");
    Assert.assertEquals("А.Лхагва", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenNull()
  {
    UserInfo info = new UserInfo(null, null);
    Assert.assertEquals("(empty name)", info.getUserName());
  }

  @Test
  public void getUserNameReturnsCorrectNameWhenEmpty()
  {
    UserInfo info = new UserInfo("", "");
    Assert.assertEquals("(empty name)", info.getUserName());
  }
}
