package se.mdh.driftavbrott.repository;

/**
 * Representerar en driftavbrottspost.
 */
public class Driftavbrottpost {
  private String kanal;
  private String start;
  private String slut;
  private String meddelandeSv;
  private String meddelandeEn;

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

  public String getMeddelandeSv() {
    return meddelandeSv;
  }

  public void setMeddelandeSv(final String meddelandeSv) {
    this.meddelandeSv = meddelandeSv;
  }

  public String getMeddelandeEn() {
    return meddelandeEn;
  }

  public void setMeddelandeEn(final String meddelandeEn) {
    this.meddelandeEn = meddelandeEn;
  }

  @Override
  public String toString() {
    return "Driftavbrottpost{" +
        "kanal='" + kanal + '\'' +
        ", start='" + start + '\'' +
        ", slut='" + slut + '\'' +
        ", meddelandeSv='" + meddelandeSv + '\'' +
        ", meddelandeEn='" + meddelandeEn + '\'' +
        '}';
  }
}
