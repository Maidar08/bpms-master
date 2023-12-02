package mn.erin.bpms.webapp.exception;

import org.camunda.bpm.engine.ProcessEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import mn.erin.bpms.process.base.ProcessTaskException;
import mn.erin.domain.base.usecase.ErinException;
import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.service.BpmServiceException;
import mn.erin.infrastucture.rest.common.response.RestError;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(value = { ProcessEngineException.class })
  public final ResponseEntity<Object> handleException(ProcessEngineException e, WebRequest request)
  {
    LOG.error(e.getMessage(), e);
    Throwable cause = e.getCause();
    // 1. use case exception
    if (cause instanceof UseCaseException || cause instanceof ProcessTaskException || cause instanceof BpmInvalidArgumentException)
    {
      RestError restError = RestError.ofErrorMessage(((ErinException) cause).getErrorMessage(), ((ErinException) cause).getCode());
      return handleExceptionInternal((Exception) cause, restError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
    // 2. other exception
    return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(value = { UseCaseException.class })
  public final ResponseEntity<Object> handleException(UseCaseException e, WebRequest request)
  {
    LOG.error(e.getMessage(), e);
    Throwable cause = e.getCause();
    RestError restError = RestError.ofErrorMessage(e.getErrorMessage(), e.getCode());
    return handleExceptionInternal((Exception) cause, restError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = { BpmServiceException.class })
  public final ResponseEntity<Object> handleException(BpmServiceException e, WebRequest request)
  {
    LOG.error(e.getMessage(), e);
    Throwable cause = e.getCause();
    RestError restError = RestError.ofErrorMessage(e.getErrorMessage(), e.getCode());
    return handleExceptionInternal((Exception) cause, restError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {BpmInvalidArgumentException.class})
  public final ResponseEntity<Object> handleException(BpmInvalidArgumentException e, WebRequest request)
  {
    LOG.error(e.getMessage(), e);
    Throwable cause = e.getCause();
    RestError restError = RestError.ofErrorMessage(e.getErrorMessage(), e.getCode());
    return handleExceptionInternal((Exception) cause, restError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = { RuntimeException.class })
  public final ResponseEntity<Object> handleException(RuntimeException e, WebRequest request)
  {
    LOG.error(e.getMessage(), e);
    return handleExceptionInternal(e, e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }
}
