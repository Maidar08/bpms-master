package mn.erin.domain.bpm.service;

import java.util.Map;

/**
 * @author Oyungerel Chuluunsukh
 **/
public interface  MessagingService
{
  /**
   * Sends sms message to given phone number
   *
   * @param input phone number message processTypeId
   * @return if sms was sent boolean
   * @throws BpmServiceException when this service is not reachable or usable
   */
  boolean sendSms(Map<String, String> input) throws BpmServiceException;
}
