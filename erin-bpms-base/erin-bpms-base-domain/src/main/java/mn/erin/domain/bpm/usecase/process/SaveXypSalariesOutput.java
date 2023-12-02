package mn.erin.domain.bpm.usecase.process;

public class SaveXypSalariesOutput {
    private final boolean saved;

    public SaveXypSalariesOutput(boolean saved)
    {
        this.saved = saved;
    }

    public boolean isSaved()
    {
        return saved;
    }
}
