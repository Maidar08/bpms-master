package mn.erin.bpm.rest.model;

import java.time.LocalDateTime;

import org.jetbrains.annotations.NotNull;

public class RestNote implements Comparable<RestNote>
{
  private String noteText;
  private String username;
  private LocalDateTime date;
  private String formattedDate;
  private boolean isReason;

  public String getFormattedDate()
  {
    return formattedDate;
  }

  public void setFormattedDate(String formattedISOStringDate)
  {
    this.formattedDate = formattedISOStringDate;
  }

  public String getNoteText()
  {
    return noteText;
  }

  public void setNoteText(String noteText)
  {
    this.noteText = noteText;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public LocalDateTime getDate()
  {
    return date;
  }

  public void setDate(LocalDateTime date)
  {
    this.date = date;
  }

  public boolean isReason()
  {
    return isReason;
  }

  public void setReason(boolean reason)
  {
    isReason = reason;
  }

  @Override
  public int compareTo(@NotNull RestNote note)
  {
    if (date == null || note.getDate() == null)
    {
      return 0;
    }
    return note.getDate().compareTo(getDate());
  }
}
