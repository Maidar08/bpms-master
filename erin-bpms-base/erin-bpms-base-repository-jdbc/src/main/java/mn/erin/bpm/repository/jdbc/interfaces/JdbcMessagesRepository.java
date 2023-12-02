package mn.erin.bpm.repository.jdbc.interfaces;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mn.erin.bpm.repository.jdbc.model.JdbcMessage;

/**
 * @author Lkhagvadorj
 */

public interface JdbcMessagesRepository extends CrudRepository<JdbcMessage, String>
{
  @Query("SELECT * FROM MESSAGES")
  List<JdbcMessage> getAllMessages();

  @Modifying
  @Query("INSERT INTO MESSAGES (ID, KEY, LOCALE, TEXT) VALUES (:id, :key, :locale, :text)")
  int insert(@Param(value = "id") String id, @Param(value = "key") String key, @Param(value = "locale") String locale, @Param("text") String text);

  @Query("SELECT * FROM MESSAGES WHERE KEY = :key AND LOCALE = :locale")
  JdbcMessage getMessagesByKeyAndLocale(@Param(value = "key") String key, @Param(value = "locale") String locale);

  @Query("SELECT * FROM MESSAGES WHERE KEY = :key")
  JdbcMessage getMessagesByKey(@Param(value = "key") String key);
}
