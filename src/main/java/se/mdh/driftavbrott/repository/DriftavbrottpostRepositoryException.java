package se.mdh.driftavbrott.repository;

public class DriftavbrottpostRepositoryException extends Exception {
  /**
   * Skapa ett tomt exception.
   */
  public DriftavbrottpostRepositoryException() {
    super();
  }

  /**
   * Skapa ett exception med ett meddelande.
   *
   * @param meddelande Det meddelande som ska skickas.
   */
  public DriftavbrottpostRepositoryException(final String meddelande) {
    super(meddelande);
  }

  /**
   * Skapa ett nytt exception.
   *
   * @param orsak Det <code>Exception</code> som orsakade
   *  detta Exception
   */
  public DriftavbrottpostRepositoryException(final Throwable orsak) {
    super(orsak);
  }

  /**
   * Skapa ett nytt exception.
   *
   * @param meddelande Det meddelande som ska skickas.
   * @param orsak Det <code>Exception</code> som orsakade
   *  detta Exception
   */
  public DriftavbrottpostRepositoryException(final String meddelande, final Throwable orsak) {
    super(meddelande, orsak);
  }
}
