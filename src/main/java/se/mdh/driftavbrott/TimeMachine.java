package se.mdh.driftavbrott;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * En klass som underlättar hantering tid där man behöver göra tester som kräver
 * att man ställer in "nu" på en given tid och ett givet datum. Det kräver att
 * både koden och testkoden använder denna klass varje gång de ska hämta "nu".
 * <p>
 * Klassen funkar lite som <code>DateTimeUtils.setCurrentMillisFixed()</code>
 * och <code>DateTimeUtils.setCurrentMillisSystem()</code> i Joda Time.
 * <p>
 * https://stackoverflow.com/questions/24491260/mocking-time-in-java-8s-java-time-api/29360514#29360514
 */
public class TimeMachine {
  private static Clock clock = Clock.systemDefaultZone();
  private static ZoneId zoneId = ZoneId.systemDefault();

  public static LocalDateTime now() {
    return LocalDateTime.now(getClock());
  }

  public static void setFixedClockAt(LocalDateTime date){
    clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId);
  }

  public static void useSystemClock(){
    clock = Clock.systemDefaultZone();
  }

  private static Clock getClock() {
    return clock ;
  }}
