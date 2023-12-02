/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.model.document;

import mn.erin.domain.base.model.Entity;
import mn.erin.domain.bpm.model.process.ProcessInstanceId;

/**
 * @author Tamir
 */
public class Document implements Entity<Document>
{
  private DocumentId documentId;
  private DocumentInfoId documentInfoId;
  private ProcessInstanceId processInstanceId;

  private String name;
  private String category;
  private String subCategory;
  private String reference;
  private String source;

  public Document(DocumentId documentId, DocumentInfoId documentInfoId, ProcessInstanceId processInstanceId, String name, String category, String subCategory,
      String reference, String source)
  {
    this.documentId = documentId;
    this.documentInfoId = documentInfoId;
    this.processInstanceId = processInstanceId;
    this.name = name;
    this.category = category;
    this.subCategory = subCategory;
    this.reference = reference;
    this.source = source;
  }

  public DocumentId getDocumentId()
  {
    return documentId;
  }

  public void setDocumentId(DocumentId documentId)
  {
    this.documentId = documentId;
  }

  public DocumentInfoId getDocumentInfoId()
  {
    return documentInfoId;
  }

  public void setDocumentInfoId(DocumentInfoId documentInfoId)
  {
    this.documentInfoId = documentInfoId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getReference()
  {
    return reference;
  }

  public void setReference(String reference)
  {
    this.reference = reference;
  }

  public String getCategory()
  {
    return category;
  }

  public void setCategory(String category)
  {
    this.category = category;
  }

  public String getSubCategory()
  {
    return subCategory;
  }

  public void setSubCategory(String subCategory)
  {
    this.subCategory = subCategory;
  }

  public ProcessInstanceId getProcessInstanceId()
  {
    return processInstanceId;
  }

  public void setProcessInstanceId(ProcessInstanceId processInstanceId)
  {
    this.processInstanceId = processInstanceId;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  @Override
  public boolean sameIdentityAs(Document other)
  {
    return null != other && other.documentId.equals(this.documentId);
  }
}
