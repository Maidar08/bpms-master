package mn.erin.domain.bpm.model.contract;

public class BiPath
{
  private String folder;
  private String path;

  public BiPath(String folder, String path) {
    this.folder = folder;
    this.path = path;
  }
  public String getFolder() {
    return folder;
  }
  public void setFolder(String folder) {
    this.folder = folder;
  }
  public String getPath() {
    return path;
  }
  public void setPath(String path) {
    this.path = path;
  }
}
