package mn.erin.bpm.repository.jdbc;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import org.springframework.stereotype.Repository;

import mn.erin.bpm.repository.jdbc.interfaces.JdbcMessagesRepository;
import mn.erin.bpm.repository.jdbc.model.JdbcMessage;
import mn.erin.domain.base.model.EntityId;
import mn.erin.domain.base.model.message.Message;
import mn.erin.domain.base.model.message.MessageId;
import mn.erin.domain.base.repository.message.MessageRepository;
import mn.erin.domain.base.usecase.MessageUtil;

/**
 * @author Lkhagvadorj
 */

@Repository

public class DefaultJdbcMessageRepository implements MessageRepository
{
  private final JdbcMessagesRepository jdbcMessagesRepository;

  public DefaultJdbcMessageRepository(JdbcMessagesRepository jdbcMessagesRepository)
  {
    this.jdbcMessagesRepository = Objects.requireNonNull(jdbcMessagesRepository, "message repository is required");
    MessageUtil.setMessageRepository(this); // can be moved application class
  }

  @Override
  public void save(Message message)
  {
    String id = String.valueOf(System.currentTimeMillis());
    jdbcMessagesRepository.insert(id, message.getKey(), message.getLocale().getLanguage(), message.getText());
  }

  @Override
  public Message find(String key, Locale locale)
  {
    JdbcMessage jdbcMessage = jdbcMessagesRepository.getMessagesByKeyAndLocale(key, locale.getLanguage());
    if (null != jdbcMessage)
    {
      MessageId messageId = new MessageId(jdbcMessage.getId());
      return new Message(messageId, jdbcMessage.getKey(), jdbcMessage.getLocale(), jdbcMessage.getText());
    }
    return null;
  }

  @Override
  public Message findById(EntityId entityId)
  {
    throw new UnsupportedOperationException("Invalid operation for Usecase exception");
  }

  @Override
  public Collection<Message> findAll()
  {
    throw new UnsupportedOperationException("Invalid operation for Usecase exception");
  }


}
