/*
 * (C)opyright, 2020, ERIN SYSTEMS LLC
 * The source code is protected by copyright laws and international copyright
 * treaties, as well as other intellectual property laws and treaties.
 * All rights reserved.
 */

package mn.erin.domain.bpm.usecase.form.get_form_by_form_id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.UseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmModuleConstants;
import mn.erin.domain.bpm.model.form.FieldProperty;
import mn.erin.domain.bpm.model.form.TaskForm;
import mn.erin.domain.bpm.model.form.TaskFormField;
import mn.erin.domain.bpm.model.form.TaskFormId;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.ProductRepository;
import mn.erin.domain.bpm.repository.TaskFormRepository;
import mn.erin.domain.bpm.usecase.product.GetAllProducts;

/**
 * @author Tamir
 */
public class GetFormByFormId implements UseCase<GetFormByFormIdInput, GetFormByFormIdOutput>
{
  private static final String ERR_MSG_INPUT_NULL = "Form id input cannot be null!";
  private final TaskFormRepository taskFormRepository;
  private final ProductRepository productRepository;

  public GetFormByFormId(TaskFormRepository taskFormRepository, ProductRepository productRepository)
  {
    this.taskFormRepository = Objects.requireNonNull(taskFormRepository, "Task form repository is required!");
    this.productRepository = Objects.requireNonNull(productRepository, "Product repository is required!");
  }

  @Override
  public GetFormByFormIdOutput execute(GetFormByFormIdInput input) throws UseCaseException
  {
    if (null == input)
    {
      throw new UseCaseException(ERR_MSG_INPUT_NULL);
    }
    String formId = input.getFormId();

    TaskForm taskForm = taskFormRepository.findById(TaskFormId.valueOf(formId));

    if (null == taskForm)
    {
      String errorCode = "CamundaTasKFormService002";
      throw new UseCaseException(errorCode, "Form does not exist with id: " + formId);
    }

    TaskFormId taskFormId = taskForm.getTaskFormId();

    if (taskFormId.getId().equals(BpmModuleConstants.CONSUMPTION_LOAN))
    {
      setProducts(taskForm.getTaskFormFields(), BpmModuleConstants.APPLICATION_TYPE_CONSUMPTION_LOAN);
    }
    else if (taskFormId.getId().equals(BpmModuleConstants.MICRO_LOAN))
    {
      setProducts(taskForm.getTaskFormFields(), BpmModuleConstants.APPLICATION_TYPE_MICRO_LOAN);
    }

    return new GetFormByFormIdOutput(taskForm);
  }

  private void setProducts(Collection<TaskFormField> taskFormFields, String applicationCategory) throws UseCaseException
  {
    for (TaskFormField taskFormField : taskFormFields)
    {
      if (taskFormField.getId().sameValueAsString(BpmModuleConstants.LOAN_PRODUCT))
      {
        GetAllProducts getAllProducts = new GetAllProducts(productRepository);
        Collection<Product> products = getAllProducts.execute(null).getProducts();
        taskFormField.setFieldProperties(getProductFieldProperties(products, applicationCategory));
      }
    }
  }

  private List<FieldProperty> getProductFieldProperties(Collection<Product> products, String appCategory)
  {
    List<FieldProperty> productProperties = new ArrayList<>();

    for (Product product : products)
    {
      String applicationCategory = product.getApplicationCategory();

      if (applicationCategory.equals(appCategory))
      {
        productProperties.add(new FieldProperty(product.getId().getId(), product.getProductDescription()));
      }
    }

    return productProperties;
  }
}
