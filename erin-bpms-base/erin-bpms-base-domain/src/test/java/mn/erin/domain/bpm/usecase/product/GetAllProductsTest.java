/*
 * Copyright (C) ERIN SYSTEMS LLC, 2020. All rights reserved.
 *
 * The source code is protected by copyright laws and international copyright treaties, as well as
 * other intellectual property laws and treaties.
 */

package mn.erin.domain.bpm.usecase.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.ProductRepository;

/**
 * @author Zorig
 */
public class GetAllProductsTest
{
  private GetAllProducts useCase;
  private ProductRepository productRepository;

  @Before
  public void setUp()
  {
    productRepository = Mockito.mock(ProductRepository.class);
    useCase = new GetAllProducts(productRepository);
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_null()
  {
    new GetProduct(null);
  }

  @Test
  public void when_no_products_exist() throws UseCaseException
  {
    Mockito.when(productRepository.findAll()).thenReturn(Collections.emptyList());

    GetAllProductsOutput output = useCase.execute(null);

    Assert.assertTrue(output.getProducts().isEmpty());
  }

  @Test
  public void when_products_exist() throws UseCaseException
  {
    Product product = new Product(ProductId.valueOf("123"), "applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true);
    Product product1 = new Product(ProductId.valueOf("456"), "applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true);
    Product product2 = new Product(ProductId.valueOf("789"), "applicationCategory", "categoryDescription", "productDescription", "type", BigDecimal.valueOf(.45), true, true);

    Collection<Product> allProducts = new ArrayList<>();

    allProducts.add(product);
    allProducts.add(product1);
    allProducts.add(product2);

    Mockito.when(productRepository.findAll()).thenReturn(allProducts);

    GetAllProductsOutput output = useCase.execute(null);

    Collection<Product> allReturnedProducts = output.getProducts();
    Iterator<Product> productIterator = allReturnedProducts.iterator();

    Assert.assertFalse(allReturnedProducts.isEmpty());

    Assert.assertEquals(allReturnedProducts.size(), 3);

    Assert.assertEquals(productIterator.next().getId().getId(), "123");
    Assert.assertEquals(productIterator.next().getId().getId(), "456");
    Assert.assertEquals(productIterator.next().getId().getId(), "789");
  }
}
