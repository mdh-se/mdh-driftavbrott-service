package se.mdh.driftavbrott.adapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;
import se.mdh.driftavbrott.TimeMachine;
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

  static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
  static final DateTimeFormatter DATE_TIME_FORMATTER_MESSAGE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  /**
   * Konverterar en {@link Driftavbrottpost} till en {@link Driftavbrott}.
   *
   * Om driftavbrottposten endast innehåller start- och sluttider så konverteras
   * dessa till ett fullständigt datum och tid, där datumet är morgondagens
   * datum om sluttiden har passerats, annars dagens datum.
   *
   * Konverteringen använder följande default-värden:
   * {@link Driftavbrott#niva} = {@link #DEFAULT_NIVA}
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
      LocalDate today = TimeMachine.now().toLocalDate();
      LocalDate tomorrow = today.plusDays(1);
      LocalDate yesterday = today.plusDays(-1);
      LocalDate startDate;
      LocalDate slutDate;
      LocalTime nowTime = TimeMachine.now().toLocalTime();

      if(startTime.isAfter(slutTime)) {
        // Olika start- och slutdag
        if(nowTime.isBefore(slutTime)) {
          // Dag 2
          startDate = yesterday;
          slutDate = today;
        }
        else if(nowTime.isAfter(startTime)) {
          // Dag 1
          startDate = today;
          slutDate = tomorrow;
        }
        else {
          // Utanför
          startDate = today;
          slutDate = tomorrow;
        }
      }
      else {
        // Samma start- och slutdag
        if(nowTime.isBefore(startTime)) {
          // Vi har inte passerat starttiden ännu, så nästa driftavbrott kommer att vara idag
          startDate = today;
          slutDate = today;
        }
        else if(nowTime.isAfter(slutTime)) {
          // Vi har passerat sluttiden, så nästa driftavbrott kommer att vara imorgon
          startDate = tomorrow;
          slutDate = tomorrow;
        }
        else {
          // Vi ligger mellan start- och sluttid, så driftavbrottet är idag
          startDate = today;
          slutDate = today;
        }
      }

      driftavbrott.setStart(startDate.atTime(startTime));
      driftavbrott.setSlut(slutDate.atTime(slutTime));
    }

    // Hantera meddelanden och injicera datumen i dem
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("start", DATE_TIME_FORMATTER_MESSAGE.format(driftavbrott.getStart()));
    valuesMap.put("slut", DATE_TIME_FORMATTER_MESSAGE.format(driftavbrott.getSlut()));

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    String meddelandeSv = sub.replace(post.getMeddelandeSv());
    String meddelandeEn = sub.replace(post.getMeddelandeEn());

    driftavbrott.setMeddelandeSv(meddelandeSv);
    driftavbrott.setMeddelandeEn(meddelandeEn);

    return driftavbrott;
  }
}
