package se.mdh.driftavbrott.adapter;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import se.mdh.driftavbrott.modell.Driftavbrott;
import se.mdh.driftavbrott.modell.NivaType;
import se.mdh.driftavbrott.repository.Driftavbrottpost;

/**
 * Adapterar mellan Driftavbrottpost-objekt och Driftavbrott-objekt. Det första
 * är något som har hämtats från ett DriftavbrottRepository och det andra är det
 * som skickas från denna service.
 */
@Component
public class DriftavbrottAdapter {
  private static final NivaType DEFAULT_NIVA = NivaType.ERROR;
  private static final String DEFAULT_MEDDELANDE_SV = "";
  private static final String DEFAULT_MEDDELANDE_EN = "";

  static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");
  static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");

  /**
   * Konverterar en {@link Driftavbrottpost} till en {@link Driftavbrott}.
   *
   * Om driftavbrottposten endast innehåller start- och sluttider så konverteras
   * dessa till ett fullständigt datum och tid, där datumet är morgondagens
   * datum om sluttiden har passerats, annars dagens datum.
   *
   * Konverteringen använder följande default-värden:
   * {@link Driftavbrott#niva} = {@link #DEFAULT_NIVA}
   * {@link Driftavbrott#meddelandeSv} = {@link #DEFAULT_MEDDELANDE_SV}
   * {@link Driftavbrott#meddelandeEn} = {@link #DEFAULT_MEDDELANDE_EN}
   *
   * @param post Posten som ska konverteras
   * @return Det konverterade driftavbrottet
   */
  public Driftavbrott konvertera(final Driftavbrottpost post) {
    Driftavbrott driftavbrott = new Driftavbrott();

    driftavbrott.setKanal(post.getKanal());
    driftavbrott.setNiva(DEFAULT_NIVA);

    if(post.getStart().contains("T")) { // Adapteras som datetime
      driftavbrott.setStart(LocalDateTime.parse(post.getStart(), DATE_TIME_FORMATTER));
      driftavbrott.setSlut(LocalDateTime.parse(post.getSlut(), DATE_TIME_FORMATTER));
    }
    else { // Adapteras som en tid som upprepas varje dag - bestäm dagar
      LocalTime startTime = LocalTime.parse(post.getStart(), TIME_FORMATTER);
      LocalTime slutTime = LocalTime.parse(post.getSlut(), TIME_FORMATTER);
      LocalDate today = LocalDate.now();
      LocalDate tomorrow = today.plusDays(1);
      LocalDate yesterday = today.plusDays(-1);
      LocalDate startDate;
      LocalDate slutDate;
      LocalTime nowTime = LocalTime.now();
      if(nowTime.isAfter(slutTime)) {
        slutDate = tomorrow;
        if(slutTime.isAfter(startTime)) {
          startDate = slutDate;
        }
        else {
          startDate = today;
        }
      }
      else if(nowTime.isBefore(startTime)) {
        startDate = today;
        if(slutTime.isAfter(startTime)) {
          slutDate = startDate;
        }
        else {
          slutDate = tomorrow;
        }
      }
      else {
        startDate = today;
        if(slutTime.isAfter(startTime)) {
          slutDate = startDate;
        }
        else {
          if(today.equals(startDate)) {
            // Vi är på dag 1
            slutDate = tomorrow;
          }
          else {
            startDate = yesterday;
            slutDate = today;
          }
        }
      }

      driftavbrott.setStart(startDate.toLocalDateTime(startTime));
      driftavbrott.setSlut(slutDate.toLocalDateTime(slutTime));

      String defaultMeddelandeSv = replaceStringWithPlaceholder(post.getDefaultMeddelandeSv(), driftavbrott.getStart(), "${start}");
      defaultMeddelandeSv = replaceStringWithPlaceholder(defaultMeddelandeSv, driftavbrott.getSlut(), "${slut}");

      String defaultMeddelandeEn = replaceStringWithPlaceholder(post.getDefaultMeddelandeEn(), driftavbrott.getStart(), "${start}");
      defaultMeddelandeEn = replaceStringWithPlaceholder(defaultMeddelandeEn, driftavbrott.getSlut(), "${slut}");

      driftavbrott.setMeddelandeSv(defaultMeddelandeSv);
      driftavbrott.setMeddelandeEn(defaultMeddelandeEn);
    }

    return driftavbrott;
  }

  private String replaceStringWithPlaceholder(String stringWithPlaceholder, LocalDateTime localDateTime, String placeholder) {
    if(stringWithPlaceholder.contains(placeholder)) {
      return stringWithPlaceholder.replace(placeholder, localDateTime.toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")));
    }
    else {
      return stringWithPlaceholder;
    }
  }
}
