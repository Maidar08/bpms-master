/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.document_info;

import java.util.Collection;

import mn.erin.domain.bpm.model.document.DocumentInfo;

/**
 * @author Tamir
 */
public class GetBasicDocumentInfosOutput
{
  private Collection<DocumentInfo> documentInfos;

  public GetBasicDocumentInfosOutput(Collection<DocumentInfo> documentInfos)
  {
    this.documentInfos = documentInfos;
  }

  public Collection<DocumentInfo> getDocumentInfos()
  {
    return documentInfos;
  }

  public void setDocumentInfos(Collection<DocumentInfo> documentInfos)
  {
    this.documentInfos = documentInfos;
  }
}
