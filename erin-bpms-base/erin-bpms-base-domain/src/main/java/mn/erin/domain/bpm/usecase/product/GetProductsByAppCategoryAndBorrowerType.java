package mn.erin.domain.bpm.usecase.product;

import java.util.List;
import java.util.Objects;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.BpmMessagesConstants;
import mn.erin.domain.bpm.model.product.Product;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.ProductRepository;

/**
 * @author Lkhagvadorj.A
 **/

public class GetProductsByAppCategoryAndBorrowerType extends AbstractUseCase<UniqueProductInput, GetAllProductsOutput>
{
  private final ProductRepository productRepository;

  public GetProductsByAppCategoryAndBorrowerType(ProductRepository productRepository)
  {
    this.productRepository = Objects.requireNonNull(productRepository, "Product repository is required!");
  }

  @Override
  public GetAllProductsOutput execute(UniqueProductInput input) throws UseCaseException
  {
    if (input == null)
    {
      throw new UseCaseException(BpmMessagesConstants.INPUT_NULL_CODE, BpmMessagesConstants.INPUT_NULL_MESSAGE);
    }
    try
    {
      List<Product> products = productRepository.findByAppCategoryAndBorrowerType(input.getApplicationCategory(), input.getBorrowerType());
      return new GetAllProductsOutput(products);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
