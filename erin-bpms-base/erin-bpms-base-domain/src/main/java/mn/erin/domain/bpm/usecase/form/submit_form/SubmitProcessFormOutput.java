package mn.erin.domain.bpm.usecase.form.submit_form;

public class SubmitProcessFormOutput
{
  private boolean isSubmitted;

  public SubmitProcessFormOutput(boolean isSubmitted)
  {
    this.isSubmitted = isSubmitted;
  }

  public boolean isSubmitted()
  {
    return isSubmitted;
  }

  public void setSubmitted(boolean submitted)
  {
    isSubmitted = submitted;
  }
}
