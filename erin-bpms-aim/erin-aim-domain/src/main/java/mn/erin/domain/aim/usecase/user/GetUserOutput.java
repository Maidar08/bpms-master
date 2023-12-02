package mn.erin.domain.aim.usecase.user;

/**
 * @author Bat-Erdene Tsogoo.
 */
public class GetUserOutput
{
  private String id;
  private String tenantId;
  private String firstName;
  private String lastName;
  private String displayName;
  private String email;
  private String phoneNumber;

  private GetUserOutput()
  {
  }

  public String getId()
  {
    return id;
  }

  public String getTenantId()
  {
    return tenantId;
  }

  public String getFirstName()
  {
    return firstName;
  }

  public String getLastName()
  {
    return lastName;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public String getEmail()
  {
    return email;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public static class Builder
  {
    private String id;
    private String tenantId;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String phoneNumber;

    public Builder(String id)
    {
      this.id = id;
    }

    public Builder withTenant(String tenantId)
    {
      this.tenantId = tenantId;
      return this;
    }

    public Builder withFirstName(String firstName)
    {
      this.firstName = firstName;
      return this;
    }

    public Builder withLastName(String lastName)
    {
      this.lastName = lastName;
      return this;
    }

    public Builder withDisplayName(String displayName)
    {
      this.displayName = displayName;
      return this;
    }

    public Builder withEmail(String email)
    {
      this.email = email;
      return this;
    }

    public Builder withPhoneNumber(String phoneNumber)
    {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public GetUserOutput build()
    {
      GetUserOutput output = new GetUserOutput();
      output.id = this.id;
      output.tenantId = this.tenantId;
      output.firstName = this.firstName;
      output.lastName = this.lastName;
      output.displayName = this.displayName;
      output.email = this.email;
      output.phoneNumber = this.phoneNumber;
      return output;
    }
  }
}
