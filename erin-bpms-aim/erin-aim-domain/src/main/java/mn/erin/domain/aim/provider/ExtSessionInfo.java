package mn.erin.domain.aim.provider;

import java.io.Serializable;

/**
 * @author Lkhagvadorj.A
 **/

public class ExtSessionInfo implements Serializable
{

    private static final long serialVersionUID = -6692808658368051593L;
    private String userId;
    private String encryptedPassword;
    private String extSessionId;

    public ExtSessionInfo(String userId, String encryptedPassword, String sessionId)
    {
        this.userId = userId;
        this.encryptedPassword = encryptedPassword;
        this.extSessionId = sessionId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getEncryptedPassword()
    {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword)
    {
        this.encryptedPassword = encryptedPassword;
    }

    public String getExtSessionId()
    {
        return extSessionId;
    }

    public void setExtSessionId(String extSessionId)
    {
        this.extSessionId = extSessionId;
    }
}
