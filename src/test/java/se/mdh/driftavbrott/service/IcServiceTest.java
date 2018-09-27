package se.mdh.driftavbrott.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import se.mdh.driftavbrott.modell.Driftavbrott;
import se.mdh.driftavbrott.adapter.DriftavbrottAdapter;
import se.mdh.driftavbrott.repository.DriftavbrottpostRepository;
import se.mdh.driftavbrott.repository.DriftavbrottpostRepositoryException;
import se.mdh.driftavbrott.repository.DriftavbrottpostRepositoryProperties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IcServiceTest {

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
    DateTimeUtils.setCurrentMillisSystem();
  }

  @Test
  public void getPagaendeDriftavbrottUtanMarginal() {
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    DateTime dt = formatter.parseDateTime("2017-10-04 10:58:00");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 0);
      assertTrue(pagaendeDriftavbrott.isPresent());
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void getIckePagaendeDriftavbrottUtanMarginal() {
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    DateTime dt = formatter.parseDateTime("2017-10-04 11:58:00");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 0);
      assertFalse(pagaendeDriftavbrott.isPresent());
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void getPagaendeDriftavbrottMedSlutMarginal() {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    DateTime dt = formatter.parseDateTime("2017-10-04 11:05:00");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 10);
      assertTrue(pagaendeDriftavbrott.isPresent());

      pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 1);
      assertFalse(pagaendeDriftavbrott.isPresent());
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void getPagaendeDriftavbrottMedStartMarginal() {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    DateTime dt = formatter.parseDateTime("2017-10-03 15:58:00");
    DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
    IcService icService = new IcService(repository, new DriftavbrottAdapter());
    try {
      Optional<Driftavbrott> pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 5);
      assertTrue(pagaendeDriftavbrott.isPresent());

      pagaendeDriftavbrott = icService.getPagaendeDriftavbrott(kanaler, 1);
      assertFalse(pagaendeDriftavbrott.isPresent());
    }
    catch(DriftavbrottpostRepositoryException e) {
      e.printStackTrace();
    }
  }
}