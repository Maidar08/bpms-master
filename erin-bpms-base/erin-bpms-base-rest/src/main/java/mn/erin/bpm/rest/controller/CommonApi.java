package mn.erin.bpm.rest.controller;

import java.util.Collections;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Munkh-Itgel
 */
@Api
@RestController
@RequestMapping(value = "/common", name = "Common Services")
public class CommonApi
{
  @ApiOperation("Ping API")
  @GetMapping("/ping")
  public ResponseEntity<Map<String, String>> ping()
  {
    return ResponseEntity.ok(Collections.singletonMap("message", "pong!"));
  }
}
