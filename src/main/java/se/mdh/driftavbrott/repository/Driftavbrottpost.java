package se.mdh.driftavbrott.repository;

/**
 * Representerar en driftavbrottspost.
 */
public class Driftavbrottpost {
  private String kanal;
  private String start;
  private String slut;
  private String defaultMeddelandeSv;
  private String defaultMeddelandeEn;

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

  public String getDefaultMeddelandeSv() {
    return defaultMeddelandeSv;
  }

  public void setDefaultMeddelandeSv(String defaultMeddelandeSv) {
    this.defaultMeddelandeSv = defaultMeddelandeSv;
  }

  public String getDefaultMeddelandeEn() {
    return defaultMeddelandeEn;
  }

  public void setDefaultMeddelandeEn(String defaultMeddelandeEn) {
    this.defaultMeddelandeEn = defaultMeddelandeEn;
  }

  @Override
  public String toString() {
    return "Driftavbrottpost{" +
        "kanal='" + kanal + '\'' +
        ", start='" + start + '\'' +
        ", slut='" + slut + '\'' +
        ", defaultMeddelandeSv='" + defaultMeddelandeSv + '\'' +
        ", defaultMeddelandeEn='" + defaultMeddelandeEn + '\'' +
        '}';
  }
}
