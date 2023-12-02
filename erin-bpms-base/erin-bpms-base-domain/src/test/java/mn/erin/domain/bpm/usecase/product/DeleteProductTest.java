package mn.erin.domain.bpm.usecase.product;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;

public class DeleteProductTest
{
  private ProductRepository productRepository;
  private DeleteProduct useCase;
  private UniqueProductInput input;

  @Before
  public void setUp()
  {
    productRepository = Mockito.mock(ProductRepository.class);
    useCase = new DeleteProduct(productRepository);
    input = new UniqueProductInput("123", "123");
  }

  @Test(expected = NullPointerException.class)
  public void when_product_repository_is_null()
  {
    new DeleteProduct(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_service_exception() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(productRepository.deleteByProductIdAndApplicationCategory("123", "123")).thenThrow(BpmRepositoryException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(productRepository.deleteByProductIdAndApplicationCategory("123", "123")).thenReturn(1);
    Boolean result = useCase.execute(input);
    Assert.assertTrue(result);
  }
}
