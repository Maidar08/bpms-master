/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.ProductRepository;

/**
 * @author Zorig
 */
public class DeleteProductByIdTest
{
  private DeleteProductsById useCase;

  private ProductRepository productRepository;

  @Before
  public void setUp()
  {
    productRepository = Mockito.mock(ProductRepository.class);
    useCase = new DeleteProductsById(productRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new DeleteProductsById(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_product_id_null() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_product_id_blank() throws UseCaseException
  {
    useCase.execute("");
  }

  @Test
  public void when_product_deleted() throws UseCaseException
  {
    Mockito.when(productRepository.findById(ProductId.valueOf("123"))).thenReturn(null);

    boolean isDeleted = useCase.execute("123");

    Assert.assertTrue(isDeleted);
  }
}
