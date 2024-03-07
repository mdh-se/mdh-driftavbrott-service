package se.mdh.driftavbrott.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.mdh.driftavbrott.TimeMachine;
import se.mdh.driftavbrott.adapter.DriftavbrottAdapter;
import se.mdh.driftavbrott.modell.Driftavbrott;
import se.mdh.driftavbrott.modell.NivaType;
import se.mdh.driftavbrott.repository.DriftavbrottpostRepository;
import se.mdh.driftavbrott.repository.DriftavbrottpostRepositoryException;
import se.mdh.driftavbrott.repository.DriftavbrottpostRepositoryProperties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @todo Skriv om på samma sätt som DriftavbrottAdapterTestCase
 */
public class IcServiceTestCase {

  private static DriftavbrottpostRepository repository;
  private static List<String> kanaler = Arrays.asList("ladok.backup","ladok.produktionssattning","ladok.uppgradering");


  @BeforeClass
  public static void setupClass() {
    repository = new DriftavbrottpostRepositoryProperties("driftavbrott.properties");
  }

  @Before
  public void setupTest() {

  }

  @After
  public void cleanupTest() {
    TimeMachine.useSystemClock();
  }

  @Test
  public void getPagaendeDriftavbrottUtanMarginal() {
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime dt = LocalDateTime.parse("2017-10-04 10:58:00", formatter);
    TimeMachine.setFixedClockAt(dt);
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 0);
      assertTrue(pagaendeDriftavbrott.isPresent());
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void getIckePagaendeDriftavbrottUtanMarginal() {
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime dt = LocalDateTime.parse("2017-10-04 11:58:00", formatter);
    TimeMachine.setFixedClockAt(dt);
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 0);
      assertFalse(pagaendeDriftavbrott.isPresent());
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void getPagaendeDriftavbrottMedSlutMarginal() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime dt = LocalDateTime.parse("2017-10-04 11:05:00", formatter);
    TimeMachine.setFixedClockAt(dt);
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 10);
      assertTrue(pagaendeDriftavbrott.isPresent());

      pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 1);
      assertFalse(pagaendeDriftavbrott.isPresent());
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void getPagaendeDriftavbrottMedStartMarginal() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime dt = LocalDateTime.parse("2017-10-03 15:58:00", formatter);
    TimeMachine.setFixedClockAt(dt);
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 5);
      assertTrue(pagaendeDriftavbrott.isPresent());

      pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 1);
      assertFalse(pagaendeDriftavbrott.isPresent());
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void getPagaendeDriftavbrottMedSlutMarginalPaInfoNiva() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime dt = LocalDateTime.parse("2018-09-28 10:05:00", formatter);
    TimeMachine.setFixedClockAt(dt);
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(Arrays.asList("mdu.systemunderhall.info"), 1);
      assertTrue(pagaendeDriftavbrott.isPresent());
      assertTrue(pagaendeDriftavbrott.get().getKanal().endsWith(NivaType.INFO.value().toLowerCase()));
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  @Test
  public void getPagaendeDriftavbrottMedSlutMarginalValjErrorInnanInfo() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime dt = LocalDateTime.parse("2018-09-28 10:05:00", formatter);
    TimeMachine.setFixedClockAt(dt);
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(Arrays.asList("mdu.systemunderhall","mdu.systemunderhall.info"), 1);
      assertTrue(pagaendeDriftavbrott.isPresent());
      assertFalse(pagaendeDriftavbrott.get().getKanal().endsWith(NivaType.INFO.value().toLowerCase()));
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }
}