/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.aim.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import mn.erin.domain.aim.model.tenant.TenantId;
import mn.erin.domain.aim.model.user.User;

import static org.junit.Assert.assertEquals;

/**
 * @author Tamir
 */
@Ignore
public class LdapUserRepositoryTest
{
  private static final String PROPERTIES_FILE_NAME = "test-ldap.properties";
  private static final String XAC = "xac";

  private static LdapConfig ldapConfig;
  private LdapUserRepository ldapUserRepository;

  @BeforeClass
  public static void beforeClass() throws Exception
  {
    InputStream inputStream = LdapUserRepositoryTest.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
    ldapConfig = LdapConfig.load(inputStream);
  }

  @Before
  public void setUp()
  {
    ldapUserRepository = new LdapUserRepository();
    ldapUserRepository.setLdapConfig(ldapConfig);
  }

  @Test
  public void verify_get_all_users() throws IOException
  {
    long start = System.currentTimeMillis();
    List<User> users = ldapUserRepository.getAllUsers(TenantId.valueOf(XAC));
    assertEquals(1365, users.size());
    System.out.println("### Duration: " + (System.currentTimeMillis() - start));
  }

  private static final String[] TEST_USERS = {
      "RoszkoL",
      "JalaieL",
      "MatthewM",
      "MyreK",
      "GoresY",
      "CsenarC",
      "EastonC",
      "BottingZ",
      "ValleeI",
      "McNamarE",
      "FrankenS",
      "ShumanE",
      "GiescheL",
      "DALEH",
      "DeZoeteA",
      "VanderhZ",
      "HinkleE",
      "JoachimD",
      "GirouxF",
      "HerreraY",
      "EdmondsL",
      "WsadminM",
      "HovingaC",
      "OuelletT",
      "DunningC",
      "AchilleS",
      "CourtadC",
      "FoubertZ",
      "MohA",
      "MaracleM",
      "RahmaniG",
      "SchackZ",
      "FoldesM",
      "VolkS",
      "RaghunaM",
      "BasuD",
      "RochonN",
      "GadouchM",
      "ChristeF",
      "WarkR",
      "KryskoM",
      "SchachaB",
      "BirksC",
      "NimmoT",
      "ArcherB",
      "SlunderS",
      "McVayT",
      "KikuchiT",
      "DMSDBJ",
      "GottlieC",
      "KempffeF",
      "BatchelV",
      "HoralekR",
      "BalmerB",
      "ThurstoB",
      "__TEST_UNKNOW_USER",
      "BrooksC",
      "MerizziC",
      "GrisoniP",
      "AguilarB",
      "TassyJ",
      "WaighJ",
      "ParyagC",
      "FransP",
      "MasalesS",
      "KotykJ",
      "FranzkyD",
      "SalamonR",
      "PringleL",
      "PreoR",
      "DanielaL",
      "BourletS",
      "SandersE",
      "HawryshN",
      "PetrunkF",
      "GrelckR",
      "GoodF",
      "MedlockB",
      "SavaryeB",
      "WalzJ",
      "KrautleT",
      "Abou-EzK",
      "RombougS",
      "DuflothD",
      "LeinenM",
      "SpolarK",
      "HineM",
      "HershbeH",
      "SwinsonE",
      "GodardL",
      "BorzicC",
      "MaheuE",
      "KomaromW",
      "SigdaC",
      "CharestN",
      "NewsC",
      "CassonA",
      "DragertT",
      "GregoriG",
      "ProkopeT",
      "WalchliI",
      "McElroyL",
      "EavesR",
      "VilisL",
      "DenterM",
      "CottingS"
  };
}
