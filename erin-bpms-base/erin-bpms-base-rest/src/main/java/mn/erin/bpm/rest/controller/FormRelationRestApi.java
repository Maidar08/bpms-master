package mn.erin.bpm.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mn.erin.domain.base.usecase.UseCaseException;
import mn.erin.domain.bpm.exception.BpmInvalidArgumentException;
import mn.erin.domain.bpm.model.form.TaskFormRelation;
import mn.erin.domain.bpm.repository.BpmsRepositoryRegistry;
import mn.erin.domain.bpm.repository.FormRelationRepository;
import mn.erin.domain.bpm.service.BpmsServiceRegistry;
import mn.erin.domain.bpm.usecase.form.relation.GetFormRelation;
import mn.erin.domain.bpm.usecase.form.relation.GetFormRelationOutput;
import mn.erin.infrastucture.rest.common.response.RestResponse;
import mn.erin.infrastucture.rest.common.response.RestResult;

import static mn.erin.bpm.rest.constant.BpmsControllerConstants.INTERNAL_SERVER_ERROR;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_DEF_KEY_NULL_ERROR_CODE;
import static mn.erin.domain.bpm.BpmMessagesConstants.TASK_DEF_KEY_NULL_ERROR_MESSAGE;

/**
 * @author Tamir
 */
@Api
@RestController
@RequestMapping(value = "bpm/form-relation", name = "Form relation API.")
public class FormRelationRestApi extends BaseBpmsRestApi
{
  private static final Logger LOG = LoggerFactory.getLogger(FormRelationRestApi.class);

  public FormRelationRestApi(BpmsServiceRegistry bpmsServiceRegistry,
      BpmsRepositoryRegistry bpmsRepositoryRegistry)
  {
    super(bpmsServiceRegistry, bpmsRepositoryRegistry);
  }

  @ApiOperation("Gets task form relation information depending on task definition key")
  @GetMapping("/{taskDefKey}")
  public ResponseEntity<RestResult> getFormRelationInfo(@PathVariable String taskDefKey) throws BpmInvalidArgumentException
  {
    if (StringUtils.isBlank(taskDefKey))
    {
      throw new BpmInvalidArgumentException(TASK_DEF_KEY_NULL_ERROR_CODE, TASK_DEF_KEY_NULL_ERROR_MESSAGE);
    }

    FormRelationRepository formRelationRepository = bpmsRepositoryRegistry.getFormRelationRepository();
    GetFormRelation getFormRelation = new GetFormRelation(formRelationRepository);

    try
    {
      GetFormRelationOutput output = getFormRelation.execute(taskDefKey);

      if (null == output)
      {
        return RestResponse.success(null);
      }

      TaskFormRelation taskFormRelation = output.getTaskFormRelation();

      return RestResponse.success(taskFormRelation);
    }
    catch (UseCaseException e)
    {
      LOG.error(e.getMessage());
      return RestResponse.internalError(INTERNAL_SERVER_ERROR + e.getMessage());
    }
  }
}
