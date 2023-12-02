/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;

/**
 * @author Zorig
 */
public class CreateProductTest
{
  private CreateProduct useCase;
  private ProductRepository productRepository;

  @Before
  public void setUp()
  {
    productRepository = Mockito.mock(ProductRepository.class);
    useCase = new CreateProduct(productRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new CreateProduct(null);
  }

  @Test
  public void when_successful_creation() throws BpmRepositoryException, UseCaseException
  {
    Product product = new Product(ProductId.valueOf("123"), "applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true);

    Mockito.when(productRepository.create("123", "applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true)).thenReturn(product);

    CreateProductInput createProductInput = new CreateProductInput( "123","applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true);

    Product createdProduct = useCase.execute(createProductInput);

    Assert.assertNotNull(createdProduct);
    Assert.assertEquals(createdProduct.getId().getId(), "123");
  }

  @Test(expected = UseCaseException.class)
  public void when_not_successful_creation() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(productRepository.create("123", "applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true)).thenReturn(null);

    CreateProductInput createProductInput = new CreateProductInput( "123","applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true);

    Product createdProduct = useCase.execute(createProductInput);
  }

  @Test(expected = NullPointerException.class)
  public void when_product_id_null() throws BpmRepositoryException, UseCaseException
  {
    CreateProductInput createProductInput = new CreateProductInput( null,"applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true);

    Product createdProduct = useCase.execute(createProductInput);
  }

  @Test(expected = NullPointerException.class)
  public void when_app_category_null() throws BpmRepositoryException, UseCaseException
  {
    CreateProductInput createProductInput = new CreateProductInput( "123",null, "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true);

    Product createdProduct = useCase.execute(createProductInput);
  }

  @Test(expected = NullPointerException.class)
  public void when_category_description_null() throws BpmRepositoryException, UseCaseException
  {
    CreateProductInput createProductInput = new CreateProductInput( "123","applicationCategory", null, "productDescription", "type", BigDecimal.valueOf(.45), true, true);

    Product createdProduct = useCase.execute(createProductInput);
  }

  @Test(expected = NullPointerException.class)
  public void when_product_description_null() throws BpmRepositoryException, UseCaseException
  {
    CreateProductInput createProductInput = new CreateProductInput( "123","applicationCategory", "categoryDescription", null, "type", BigDecimal.valueOf(.45), true, true);

    Product createdProduct = useCase.execute(createProductInput);
  }

  @Test(expected = NullPointerException.class)
  public void when_type_null() throws BpmRepositoryException, UseCaseException
  {
    CreateProductInput createProductInput = new CreateProductInput( "123","applicationCategory", "categoryDescription", "productDescription", null, BigDecimal.valueOf(.45), true, true);

    Product createdProduct = useCase.execute(createProductInput);
  }

  @Test(expected = NullPointerException.class)
  public void when_loan_value_ratio_null() throws BpmRepositoryException, UseCaseException
  {
    CreateProductInput createProductInput = new CreateProductInput( "123","applicationCategory", "categoryDescription", "productDescription", "type", null, true, true);

    Product createdProduct = useCase.execute(createProductInput);
  }

}
