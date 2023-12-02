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
import org.junit.Ignore;
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
@Ignore
public class GetProductTest
{
  private GetProduct useCase;
  private ProductRepository productRepository;

  @Before
  public void setUp()
  {
    productRepository = Mockito.mock(ProductRepository.class);
    useCase = new GetProduct(productRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new GetProduct(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_product_id_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_product_id_blank() throws UseCaseException
  {
    useCase.execute(new UniqueProductInput("", ""));
  }

  @Test(expected = UseCaseException.class)
  public void when_product_not_found() throws UseCaseException
  {
    Mockito.when(productRepository.findById(ProductId.valueOf("123"))).thenReturn(null);
    useCase.execute(new UniqueProductInput("123", "123"));
  }

  @Test
  public void when_product_found() throws UseCaseException, BpmRepositoryException
  {
    Product product = new Product(ProductId.valueOf("123"), "applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true);
    Mockito.when(productRepository.findByProductIdAndApplicationCategory("123", "123")).thenReturn(product);
    Product returnedProduct = useCase.execute(new UniqueProductInput("123", "123"));

    Assert.assertNotNull(returnedProduct);

    Assert.assertEquals(product.getId().getId(), returnedProduct.getId().getId(), "123");
    Assert.assertEquals(product.getLoanToValueRatio(), returnedProduct.getLoanToValueRatio());
  }
}
