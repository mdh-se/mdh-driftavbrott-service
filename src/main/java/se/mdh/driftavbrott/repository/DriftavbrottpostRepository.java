package se.mdh.driftavbrott.repository;

import java.util.List;
import org.joda.time.LocalDate;

/**
 * Interface för att hämta {@link Driftavbrottpost}er från en datakälla.
 */
public interface DriftavbrottpostRepository {
  List<Driftavbrottpost> listaPoster(LocalDate efter) throws DriftavbrottpostRepositoryException;
}
