package mn.erin.domain.bpm.usecase.product;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.product.CollateralProduct;
import mn.erin.domain.bpm.model.product.ProductId;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.CollateralProductRepository;
import mn.erin.domain.bpm.service.BpmServiceException;

public class CreateCollateralProductTest
{
  private CollateralProductRepository collateralProductRepository;
  private CreateCollateralProduct useCase;
  private CreateCollateralProductInput input;

  @Before
  public void setUp()
  {
    collateralProductRepository = Mockito.mock(CollateralProductRepository.class);
    useCase = new CreateCollateralProduct(collateralProductRepository);
    input = new CreateCollateralProductInput("123", "123", "123", "123", "123");
  }

  @Test(expected = NullPointerException.class)
  public void when_repo_is_null()
  {
    new CreateCollateralProduct(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_use_case_exception() throws UseCaseException, BpmServiceException
  {
    useCase.execute(null);
  }

  @Test(expected = UseCaseException.class)
  public void when_throws_repo_exception() throws BpmRepositoryException, UseCaseException
  {
    Mockito.when(collateralProductRepository.create("123", "123", "123", "123", "123")).thenThrow(BpmRepositoryException.class);
    useCase.execute(input);
  }

  @Test
  public void when_works_correctly() throws BpmRepositoryException, UseCaseException
  {
    CollateralProduct collateralProduct = new CollateralProduct(ProductId.valueOf("123"), "123", "123", "123", "123");
    Mockito.when(collateralProductRepository.create("123", "123", "123", "123", "123")).thenReturn(collateralProduct);
    useCase.execute(input);
    Assert.assertEquals("123", 123, 123);
  }
}
