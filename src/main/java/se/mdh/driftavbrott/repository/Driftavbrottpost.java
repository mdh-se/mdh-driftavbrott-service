package se.mdh.driftavbrott.repository;

/**
 * Representerar en driftavbrottspost.
 */
public class Driftavbrottpost {
  private String kanal;
  private String start;
  private String slut;

  public String getSlut() {
    return slut;
  }

  public void setSlut(final String slut) {
    this.slut = slut;
  }

  public String getKanal() {
    return kanal;
  }

  public void setKanal(final String kanal) {
    this.kanal = kanal;
  }

  public String getStart() {
    return start;
  }

  public void setStart(final String start) {
    this.start = start;
  }

  @Override
  public String toString() {
    return kanal + "=" + start + ";" + slut;
  }
}
