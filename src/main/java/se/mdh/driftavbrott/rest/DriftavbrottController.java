package se.mdh.driftavbrott.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.mdh.driftavbrott.repository.DriftavbrottpostRepositoryException;
import se.mdh.driftavbrott.service.IcService;
import se.mdh.driftavbrott.modell.Driftavbrott;

@RestController
@RequestMapping("/v1/driftavbrott")
public class DriftavbrottController {
  private static final Log log = LogFactory.getLog(DriftavbrottController.class);

  private IcService service;

  public DriftavbrottController(final IcService service) {
    this.service = service;
  }

  @GetMapping(value = "/pagaende", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Driftavbrott> getPagaende(@RequestParam(value = "kanal", required = false) final String[] kanaler,
                                                  @RequestParam(value = "system") final String system,
                                                  final HttpServletRequest request) {
    String logInfo = system + " -> " + request.getMethod() + " /pagaende med kanaler=" + Arrays.toString(kanaler);
    log.info(logInfo);
    try {
      List<String> kanalList;
      if(kanaler == null) {
        kanalList = Collections.emptyList();
      }
      else {
        kanalList = Arrays.asList(kanaler);
      }

      Optional<Driftavbrott> driftavbrott = service.getPagaendeDriftavbrott(kanalList);
      if(driftavbrott.isPresent()) {
        return ResponseEntity.ok(driftavbrott.get());
      }
      else {
        return ResponseEntity.notFound().build();
      }
    }
    catch(DriftavbrottpostRepositoryException e) {
      log.error(logInfo + ": Kunde inte hämta pågående driftavbrott.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    catch(Exception e) {
      log.error("Oväntat fel", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
