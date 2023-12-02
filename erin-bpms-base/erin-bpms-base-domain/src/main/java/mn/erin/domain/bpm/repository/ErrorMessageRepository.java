package mn.erin.domain.bpm.repository;

import mn.erin.domain.base.usecase.UseCaseException;

/**
 * @author Lkhagvadorj
 * */
public interface ErrorMessageRepository
{
  String getMessage(String code);
  void checkRegisterNumber(String registerNumber) throws UseCaseException;
}
