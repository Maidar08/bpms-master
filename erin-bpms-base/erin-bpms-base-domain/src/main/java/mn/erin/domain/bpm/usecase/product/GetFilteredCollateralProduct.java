package mn.erin.domain.bpm.usecase.product;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import mn.erin.domain.base.usecase.AbstractUseCase;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.model.product.CollateralProduct;
import mn.erin.domain.bpm.repository.BpmRepositoryException;
import mn.erin.domain.bpm.repository.CollateralProductRepository;

import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.INPUT_NULL_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_MESSAGE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_SUB_TYPE_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.PRODUCT_BLANK_PRODUCT_SUB_TYPE_ERROR_MESSAGE;

/**
 * @author Lkhagvadorj
 */
public class GetFilteredCollateralProduct extends AbstractUseCase<GetFilteredCollateralProductInput, CollateralProduct>
{
  private final CollateralProductRepository collateralProductRepository;

  public GetFilteredCollateralProduct(CollateralProductRepository collateralProductRepository)
  {
    this.collateralProductRepository = Objects.requireNonNull(collateralProductRepository, "Collateral Product Repository is required!!");
  }

  @Override
  public CollateralProduct execute(GetFilteredCollateralProductInput input) throws UseCaseException
  {
    if (null == input) {
      throw new UseCaseException(INPUT_NULL_CODE, INPUT_NULL_MESSAGE);
    }

    final String type = input.type;
    final String subType = input.subType;
    final String description = input.description;

    if (StringUtils.isBlank( type ))
    {
      throw new UseCaseException(PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_CODE, PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_MESSAGE);
    }

    if (StringUtils.isBlank( subType ))
    {
      throw new UseCaseException(PRODUCT_BLANK_PRODUCT_SUB_TYPE_ERROR_CODE, PRODUCT_BLANK_PRODUCT_SUB_TYPE_ERROR_MESSAGE);
    }

    if (StringUtils.isBlank( description ))
    {
      throw new UseCaseException(PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_CODE, PRODUCT_BLANK_PRODUCT_DESCRIPTION_ERROR_MESSAGE);
    }

    try
    {
      return collateralProductRepository.filterByTypeAndSubTypeAndDesc(type, subType, description);
    }
    catch (BpmRepositoryException e)
    {
      throw new UseCaseException(e.getCode(), e.getMessage());
    }
  }
}
