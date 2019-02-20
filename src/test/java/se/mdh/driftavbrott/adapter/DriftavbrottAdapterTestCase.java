package se.mdh.driftavbrott.adapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.AfterClass;
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
 * Så här ska logiken fungera.
 * Notera att alla tester körs som om klockan är 21:00.
 *
 * <p>Start- och sluttid avser samma dag
 * <pre>
 * Start  Slut    Resultat
 * -----  ----    --------
 * 21:01  21:02   Före         idag+start    idag+slut     (1)
 * 20:59  21:01   Under        idag+start    idag+slut     (1)
 * 20:58  20:59   Efter        imorgon+start imorgon+slut  (2)
 * </pre>
 *
 * <p>Start- och sluttid avser olika dagar
 * <pre>
 * Start  Slut    Resultat
 * 22:00  01:00   Före         idag+start    imorgon+slut  (3)
 * 19:00  01:00   Under dag 1  idag+start    imorgon+slut  (3)
 * 23:00  22:00   Under dag 2  igår+start    idag+slut     (4)
 * 23:00  19:00   Efter        idag+start    imorgon+slut  (3)
 * </pre>
 */
public class DriftavbrottAdapterTestCase {
  private static final Log log = LogFactory.getLog(DriftavbrottAdapterTestCase.class);

  private DriftavbrottAdapter adapter = new DriftavbrottAdapter();
  private static LocalDateTime now;
  private static final String MEDDELANDE_SV = "Någonting är fel. Felet kommer pågå från ${start} till ${slut}";
  private static final String MEDDELANDE_EN = "Something is wrong.";

  @AfterClass
  public static void afterClass() {
    // Återställ tiden igen när alla tester är klara
    DateTimeUtils.setCurrentMillisSystem();
  }

  @BeforeClass
  public static void beforeClass() {
    // Gör tidsjämförelser som om klockan alltid är 21:00
    // När vi konverterar till Java 8 time API använd DateTimeFormatter.ofPattern()
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    // När vi konverterar till Java 8 time API använd LocalDateTime.parse()
    DateTime dt = formatter.parseDateTime("2019-02-20 21:00:00");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());

    now = LocalDateTime.now();
    log.info("Använder tidpunkten " + now + " som \"nu\" under under testerna.");
  }

  private Driftavbrottpost createDriftavbrottpostOlikaDagar(LocalDateTime runTime, int startOffset, int slutOffset) {
    Driftavbrottpost post = new Driftavbrottpost();
    LocalDateTime start = runTime.plusHours(startOffset);
    post.setStart(start.toLocalTime().toString(DriftavbrottAdapter.TIME_FORMATTER));
    LocalDateTime slut = runTime.plusHours(slutOffset);
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

    log.info("Samma före  " + driftavbrott.getStart() + "  " + driftavbrott.getSlut());
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

    log.info("Samma under " + driftavbrott.getStart() + "  " + driftavbrott.getSlut());
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

    log.info("Samma efter " + driftavbrott.getStart() + "  " + driftavbrott.getSlut());
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

    log.info("Olika före  " + driftavbrott.getStart() + "  " + driftavbrott.getSlut());
    assertEquals("Start idag", now.plusHours(1).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut imorgon", now.plusHours(4).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottpost konverteras till ett Driftavbrott med
   * tidpunkter på formatet HH:mm där starttid har passerats medan sluttid ännu
   * inte har passerats och tidpunkterna hör till olika dagar (start > slut) ska
   * Driftavbrottets tidpunkter höra till olika dagar.
   */
  @Test
  public void testKonverteraPostMedTidOlikaDagarUnderDag1() {
    Driftavbrottpost post = createDriftavbrottpostOlikaDagar(now, -2, 4);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    log.info("Olika dag1  " + driftavbrott.getStart() + "  " + driftavbrott.getSlut());
    // driftavbrott.start resp. slut ska tillhöra olika dagar
    assertEquals("Start idag", now.plusHours(-2).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut imorgon", now.plusHours(4).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
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

    log.info("Olika dag2  " + driftavbrott.getStart() + "  " + driftavbrott.getSlut());
    // driftavbrott.start resp. slut ska tillhöra olika dagar
    assertEquals("Start igår", now.plusHours(-22).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut idag", now.plusHours(1).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
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

    log.info("Olika efter  " + driftavbrott.getStart() + "  " + driftavbrott.getSlut());
    // driftavbrott.start resp. slut ska vara samma som indata i post, men vara imorgon
    assertEquals("Start idag", now.plusDays(1).plusHours(-22).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getStart().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
    assertEquals("Slut imorgon", now.plusDays(1).plusHours(-2).toString(DriftavbrottAdapter.DATE_TIME_FORMATTER), driftavbrott.getSlut().toString(DriftavbrottAdapter.DATE_TIME_FORMATTER));
  }

  /**
   * Testfall: När en Driftavbrottspost konverteras och har en placeholder
   * för start- och sluttid i meddelandet så skall det ersättas med värden för
   * start- och sluttid.
   */
  @Test
  public void testErsattPlaceholderMedStartOchSluttid() {
    Driftavbrottpost post = createDriftavbrottpostOlikaDagar(now, -22, -2);
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
    Driftavbrottpost post = createDriftavbrottpostOlikaDagar(now, -22, -2);
    Driftavbrott driftavbrott = adapter.konvertera(post);

    assertTrue(driftavbrott.getMeddelandeEn().equals(MEDDELANDE_EN));
  }
}
