package se.mdh.driftavbrott.repository;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Enhetstester för {@link DriftavbrottpostRepository}.
 *
 * @author Dennis Lundberg
 */
public class DriftavbrottpostRepositoryPropertiesTestCase {
  private static final Log log = LogFactory.getLog(DriftavbrottpostRepositoryPropertiesTestCase.class);

  private DriftavbrottpostRepository repository = new DriftavbrottpostRepositoryProperties("driftavbrott.properties");

  @Test
  public void testListaPoster() throws DriftavbrottpostRepositoryException {
    LocalDate now = LocalDate.now();
    List<Driftavbrottpost> driftavbrottposter = repository.listaPoster(now);
    assertEquals(3, driftavbrottposter.size());
    assertEquals(1, driftavbrottposter.stream().filter(d -> d.getKanal().equals("ladok.backup")).count());
    assertEquals(1, driftavbrottposter.stream().filter(d -> d.getKanal().equals("ladok.produktionssattning")).count());
    assertEquals(1, driftavbrottposter.stream().filter(d -> d.getKanal().equals("ladok.uppgradering")).count());
    for(Driftavbrottpost post : driftavbrottposter) {
      log.info(post);
    }
  }
}
