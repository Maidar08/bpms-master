package mn.erin.domain.bpm.model.currency;

public enum CurrencyType
{
  MNT("MNT"),
  USD("USD");

  private final String value;

  CurrencyType(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }

  public static CurrencyType toCurrencyType(String value)
  {
    switch (value)
    {
    case "MNT":
      return CurrencyType.MNT;
    case "USD":
      return CurrencyType.USD;
    default:
      throw new IllegalArgumentException("Incompatible currency type!");
    }
  }
}
