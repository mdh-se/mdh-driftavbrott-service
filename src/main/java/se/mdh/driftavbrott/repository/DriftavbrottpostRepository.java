package se.mdh.driftavbrott.repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface för att hämta {@link Driftavbrottpost}er från en datakälla.
 *
 * @author Dennis Lundberg
 */
public interface DriftavbrottpostRepository {
  List<Driftavbrottpost> listaPoster(LocalDate efter) throws DriftavbrottpostRepositoryException;
}
