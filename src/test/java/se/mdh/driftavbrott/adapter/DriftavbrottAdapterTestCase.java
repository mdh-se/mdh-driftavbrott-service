package se.mdh.driftavbrott.adapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.LocalDateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import se.mdh.driftavbrott.modell.Driftavbrott;
import se.mdh.driftavbrott.modell.NivaType;
import se.mdh.driftavbrott.repository.Driftavbrottpost;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Enhetstester för {@link DriftavbrottAdapter}.
 *
 * Så här ska logiken fungera:
 *
 * <pre>
 * Start  Slut    Testet körs   Resultat
 *
 * 00:10  01:10   Före  00:01   idag+start    idag+slut     (1)
 *                Under 00:30   idag+start    idag+slut     (1)
 *                Efter 02:10   imorgon+start imorgon+slut  (2)
 * 23:10  00:10   Före  22:00   idag+start    imorgon+slut  (3)
 *                Under 23:30   idag+start    imorgon+slut  (3)
 *                Under 00:01   igår+start    idag+slut     (4)
 *                Efter 01:10   idag+start    imorgon+slut  (3)
 * </pre>
 */
public class DriftavbrottAdapterTestCase {
  private static final Log log = LogFactory.getLog(DriftavbrottAdapterTestCase.class);

  private DriftavbrottAdapter adapter = new DriftavbrottAdapter();
  private static LocalDateTime now;
  private static int hourOffset;
  private static final String MEDDELANDE_SV = "Någonting är fel. Felet kommer pågå från ${start} till ${slut}";
  private static final String MEDDELANDE_EN = "Something is wrong.";

  @BeforeClass
  public static void beforeClass() {
    now = LocalDateTime.now();
    // Gör tidsjämförelser som om klockan alltid är mellan klockan 21 och 22
    hourOffset = 21 - now.getHourOfDay();
    log.info("Använder en offset på " + hourOffset + " timmar.");
  }

  private Driftavbrottpost createDriftavbrottpostOlikaDagar(LocalDateTime runTime, int startOffset, int slutOffset) {
    Driftavbrottpost post = new Driftavbrottpost();
    LocalDateTime start = runTime.plusHours(startOffset + hourOffset);
    post.setStart(start.toLocalTime().toString(DriftavbrottAdapter.TIME_FORMATTER));
    LocalDateTime slut = runTime.plusHours(slutOffset + hourOffset);
    post.setSlut(slut.toLocalTime().toString(DriftavbrottAdapter.TIME_FORMATTER));
    post.setKanal("mdh.test");
    post.setMeddelandeSv(MEDDELANDE_SV);
    post.setMeddelandeEn(MEDDELANDE_EN);
    log.info(post);
    log.info(start);
    log.info(slut);
    return post;
  }

  private Driftavbrottpost createDriftavbrottpostSammaDag(LocalDateTime runTime, int startOffset, int slutOffset) {
    Driftavbrottpost post = new Driftavbrottpost();
    LocalDateTime start = runTime.plusMinutes(startOffset);
    post.setStart(start.toLocalTime().toString(DriftavbrottAdapter.TIME_FORMATTER));
    LocalDateTime slut = runTime.plusMinutes(slutOffset);
    post.setSlut(slut.toLocalTime().toString(DriftavbrottAdapter.TIME_FORMATTER));
    post.setKanal("mdh.test");
    post.setMeddelandeSv(MEDDELANDE_SV);
    post.setMeddelandeEn(MEDDELANDE_EN);
    log.info(post);
    log.info(start);
    log.info(slut);
    return post;
  }

  @Test
  public void testKonverteraPostMedDatum() {
    Driftavbrottpost post = new Driftavbrottpost();
    post.setKanal("mdh.test");
    post.setStart("2017-10-01T16:00");
    post.setSlut("2017-10-01T17:00");
    post.setMeddelandeSv(MEDDELANDE_SV);
    post.setMeddelandeEn(MEDDELANDE_EN);

    Driftavbrott driftavbrott = adapter.konvertera(post);
    assertEquals(post.getKanal(), driftavbrott.getKanal());
    assertEquals(NivaType.ERROR, driftavbrott.getNiva());
    assertEquals(post.getStart(), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals(post.getSlut(), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottpost konverteras till ett Driftavbrott med
   * tidpunkter på formatet HH:mm som inte har passerats ska Driftavbrottet
   * innehålla samma tidpunkt för dagens datum.
   */
  @Test
  public void testKonverteraPostMedTidSammaDagFöre() {
    Driftavbrottpost post = createDriftavbrottpostSammaDag(now, 1, 2);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    // driftavbrott.start resp. slut ska vara samma som indata i post
    assertEquals("Start idag", now.plusMinutes(1).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut idag", now.plusMinutes(2).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottpost konverteras till ett Driftavbrott med
   * tidpunkter på formatet HH:mm där starttid har passerats medan sluttid ännu
   * inte har passerats och tidpunkterna tillhör samma dag (start < slut) ska
   * Driftavbrottets tidpunkter höra till samma datum.
   */
  @Test
  public void testKonverteraPostMedTidSammaDagUnder() {
    Driftavbrottpost post = createDriftavbrottpostSammaDag(now, -1, 1);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    // driftavbrott.start resp. slut ska vara samma som indata i post
    assertEquals("Start idag", now.plusMinutes(-1).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut idag", now.plusMinutes(1).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottpost konverteras till ett Driftavbrott med
   * tidpunkter på formatet HH:mm som har passerats ska Driftavbrottet
   * innehålla samma tidpunkt men nästa dag, dvs. imorgon.
   */
  @Test
  public void testKonverteraPostMedTidSammaDagEfter() {
    Driftavbrottpost post = createDriftavbrottpostSammaDag(now, -2, -1);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    // driftavbrott.start resp. slut ska vara samma som indata i post, men vara imorgon
    assertEquals("Start imorgon", now.plusDays(1).plusMinutes(-2).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut imorgon", now.plusDays(1).plusMinutes(-1).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottpost konverteras till ett Driftavbrott med
   * tidpunkter på formatet HH:mm där starttid har passerats medan sluttid ännu
   * inte har passerats och tidpunkterna hör till olika dagar (start > slut) ska
   * Driftavbrottets tidpunkter höra till olika dagar.
   */
  @Test
  public void testKonverteraPostMedTidOlikaDagarFöre() {
    Driftavbrottpost post = createDriftavbrottpostOlikaDagar(now, 1, 4);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    assertEquals("Start idag", now.plusHours(1 + hourOffset).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut imorgon", now.plusHours(4 + hourOffset).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottpost konverteras till ett Driftavbrott med
   * tidpunkter på formatet HH:mm där starttid har passerats medan sluttid ännu
   * inte har passerats och tidpunkterna hör till olika dagar (start > slut) ska
   * Driftavbrottets tidpunkter höra till olika dagar.
   */
  @Test
  public void testKonverteraPostMedTidOlikaDagarUnderDag1() {
    Driftavbrottpost post = createDriftavbrottpostOlikaDagar(now, -2, 2);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    // driftavbrott.start resp. slut ska tillhöra olika dagar
    assertEquals("Start idag", now.plusHours(-2 + hourOffset).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut imorgon", now.plusHours(2 + hourOffset).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottpost konverteras till ett Driftavbrott med
   * tidpunkter på formatet HH:mm där starttid har passerats medan sluttid ännu
   * inte har passerats och tidpunkterna hör till olika dagar (start > slut) ska
   * Driftavbrottets tidpunkter höra till olika dagar.
   */
  @Test
  public void testKonverteraPostMedTidOlikaDagarUnderDag2() {
    Driftavbrottpost post = createDriftavbrottpostOlikaDagar(now, -22, 1);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    // driftavbrott.start resp. slut ska tillhöra olika dagar
    assertEquals("Start igår", now.plusHours(-22 + hourOffset).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut idag", now.plusHours(1 + hourOffset).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottpost konverteras till ett Driftavbrott med
   * tidpunkter på formatet HH:mm där starttid har passerats medan sluttid ännu
   * inte har passerats och tidpunkterna hör till olika dagar (start > slut) ska
   * Driftavbrottets tidpunkter höra till olika dagar.
   */
  @Test
  public void testKonverteraPostMedTidOlikaDagarEfter() {
    Driftavbrottpost post = createDriftavbrottpostOlikaDagar(now, -22, -2);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    // driftavbrott.start resp. slut ska vara samma som indata i post, men vara imorgon
    assertEquals("Start idag", now.plusDays(1).plusHours(-22 + hourOffset).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut imorgon", now.plusDays(1).plusHours(-2 + hourOffset).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottspost konverteras och har en placeholder
   * för start- och sluttid i meddelandet så skall det ersättas med värden för
   * start- och sluttid.
   */
  @Test
  public void testErsattPlaceholderMedStartOchSluttid() {
    Driftavbrottpost post = createDriftavbrottpostOlikaDagar(now, -24, -2);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    assertFalse(driftavbrott.getMeddelandeSv().equals(MEDDELANDE_SV));
    assertFalse(driftavbrott.getMeddelandeSv().contains("${start}"));
    assertFalse(driftavbrott.getMeddelandeSv().contains("${slut}"));
    assertTrue(driftavbrott.getMeddelandeSv().contains(driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER_MESSAGE)));
    assertTrue(driftavbrott.getMeddelandeSv().contains(driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER_MESSAGE)));
  }

  /**
   * Testfall: När en Driftavbrottpost konverteras och inte har en placeholder
   * för start- och sluttid i meddelandet så skall ingenting ändras i meddelandet.
   */
  @Test
  public void testErsattMeddelandeUtanPlaceholder() {
    Driftavbrottpost post = createDriftavbrottpostOlikaDagar(now, -24, -2);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    assertTrue(driftavbrott.getMeddelandeEn().equals(MEDDELANDE_EN));
  }
}
