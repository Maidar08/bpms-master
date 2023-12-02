/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.document;

import mn.erin.domain.base.model.Entity;

/**
 * @author Tamir
 */
public class DocumentInfo implements Entity<DocumentInfo>
{
  private DocumentInfoId id;
  private String parentId;

  private String name;
  private String type;

  public DocumentInfo(DocumentInfoId id, String parentId, String name, String type)
  {
    this.id = id;
    this.parentId = parentId;
    this.name = name;
    this.type = type;
  }

  public DocumentInfoId getId()
  {
    return id;
  }

  public void setId(DocumentInfoId id)
  {
    this.id = id;
  }

  public String getParentId()
  {
    return parentId;
  }

  public void setParentId(String parentId)
  {
    this.parentId = parentId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  @Override
  public boolean sameIdentityAs(DocumentInfo other)
  {
    return null != other && other.id.equals(this.id);
  }
}
