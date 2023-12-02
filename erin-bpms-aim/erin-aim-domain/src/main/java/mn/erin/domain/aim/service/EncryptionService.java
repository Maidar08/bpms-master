package mn.erin.domain.aim.service;

/**
 * @author Lkhagvadorj.A
 **/

public interface EncryptionService
{
    String encrypt(String input);

    String decrypt(String input);
}
