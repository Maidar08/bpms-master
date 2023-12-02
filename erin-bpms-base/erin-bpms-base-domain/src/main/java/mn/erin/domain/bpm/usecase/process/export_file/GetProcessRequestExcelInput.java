package mn.erin.domain.bpm.usecase.process.export_file;

import java.util.Collection;
import java.util.Collections;

import mn.erin.domain.bpm.model.process.ProcessRequest;

public class GetProcessRequestExcelInput
{
  private String topHeader;
  private String searchKey;
  private String groupId;
  private String stringDate;
  private Collection<ProcessRequest> processRequests;

  public GetProcessRequestExcelInput(String topHeader, String searchKey, String groupId, String stringDate, Collection<ProcessRequest> processRequests)
  {
    this.topHeader = topHeader;
    this.searchKey = searchKey;
    this.groupId = groupId;
    this.stringDate = stringDate;
    this.processRequests = processRequests;
  }

  public String getSearchKey()
  {
    return searchKey;
  }

  public String getTopHeader()
  {
    return topHeader;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getStringDate()
  {
    return stringDate;
  }

  public Collection<ProcessRequest> getProcessRequests()
  {
    return Collections.unmodifiableCollection(processRequests);
  }
}
