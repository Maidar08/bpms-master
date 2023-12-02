/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.document;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Tamir
 */
public class UploadDocumentsInput
{
  private String caseInstanceId;
  private String category;
  private String subCategory;
  private String source;
  private Map<String, String> parameters;
  private List<UploadFile> uploadFiles;

  public UploadDocumentsInput(String caseInstanceId, String category, String subCategory, String source, Map<String, String> parameters,
      List<UploadFile> uploadFiles)
  {
    this.caseInstanceId = Objects.requireNonNull(caseInstanceId, "Case instance id is required!");
    this.category = Objects.requireNonNull(category, "Document main type is required!");
    this.subCategory = Objects.requireNonNull(subCategory, "Document sub type is required!");
    this.source = Objects.requireNonNull(source, "Document source is required!");
    this.parameters = Objects.requireNonNull(parameters, "Process type id is required!");
    this.uploadFiles = Objects.requireNonNull(uploadFiles, "Upload files is required!");
  }

  public String getCaseInstanceId()
  {
    return caseInstanceId;
  }

  public void setCaseInstanceId(String caseInstanceId)
  {
    this.caseInstanceId = caseInstanceId;
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

  public List<UploadFile> getUploadFiles()
  {
    return uploadFiles;
  }

  public void setUploadFiles(List<UploadFile> uploadFiles)
  {
    this.uploadFiles = uploadFiles;
  }

  public String getSource()
  {
    return source;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  public Map<String, String> getParameters() { return parameters; }

  public void setParameters(Map<String, String> parameters) { this.parameters = parameters; }
}
