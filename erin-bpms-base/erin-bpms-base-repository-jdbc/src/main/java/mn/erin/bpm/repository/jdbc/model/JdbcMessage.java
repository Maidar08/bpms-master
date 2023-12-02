package mn.erin.bpm.repository.jdbc.model;

import java.util.Locale;

import org.springframework.data.annotation.Id;

/**
 * @author Lkhagvadorj
 * */
public class JdbcMessage
{
  @Id
  private String id;
  private String key;
  private Locale locale;
  private String text;

  public Locale getLocale() { return locale; }

  public void setLocale(Locale locale) { this.locale = locale; }

  public String getId() { return id; }

  public void setId(String id) { this.id = id; }

  public String getKey() { return key; }

  public void setKey(String key) { this.key = key; }

  public String getText() { return text; }

  public void setText(String text) { this.text = text; }
}
