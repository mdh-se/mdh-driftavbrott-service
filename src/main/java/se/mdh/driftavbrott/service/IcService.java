package se.mdh.driftavbrott.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import se.mdh.driftavbrott.TimeMachine;
import se.mdh.driftavbrott.adapter.DriftavbrottAdapter;
import se.mdh.driftavbrott.modell.Driftavbrott;
import se.mdh.driftavbrott.repository.Driftavbrottpost;
import se.mdh.driftavbrott.repository.DriftavbrottpostRepository;
import se.mdh.driftavbrott.repository.DriftavbrottpostRepositoryException;

/**
 * En service som hanterar uthämtning av {@link Driftavbrott}-objekt.
 */
@Service
public class IcService {
  private static final Log log = LogFactory.getLog(IcService.class);

  private DriftavbrottpostRepository driftavbrottpostRepository;
  private DriftavbrottAdapter driftavbrottAdapter;

  public IcService(final DriftavbrottpostRepository driftavbrottpostRepository,
                   final DriftavbrottAdapter driftavbrottAdapter) {
    this.driftavbrottpostRepository = driftavbrottpostRepository;
    this.driftavbrottAdapter = driftavbrottAdapter;
  }

  /**
   * Hämtar det pågående driftavbrottet på de kanaler som anges.
   * Det pågående driftavbrottet är en hopslagning av inlagda poster.
   *
   * @param kanaler Hämta driftavbrott för dessa kanaler
   * @param marginal Marginal
   * @return Ett driftavbrott, som är <code>Optional</code>
   * @throws DriftavbrottpostRepositoryException om det inte går att hämta driftavbrott från repositoryt
   */
  public Optional<Driftavbrott> getPagaendeDriftavbrott(final Collection<String> kanaler, final int marginal) throws DriftavbrottpostRepositoryException {

    List<Driftavbrottpost> poster = driftavbrottpostRepository.listaPoster(TimeMachine.now().toLocalDate());
    for(Driftavbrottpost post : poster) {
      log.debug(post);
    }

    Stream<Driftavbrottpost> listStream = poster.stream();
    // filtrera bort poster som inte finns i listan över kanaler
    if(!kanaler.isEmpty()) {
      listStream = listStream.filter(p -> kanaler.contains(p.getKanal()));
    }

    List<Driftavbrott> driftavbrotts = listStream
        .map(driftavbrottAdapter::konvertera)
        // Filtrera ned samlingen till driftavbrott som pågår
        .filter(d ->
          TimeMachine.now().isAfter(d.getStart().minusMinutes(marginal))
              && TimeMachine.now().isBefore(d.getSlut().plusMinutes(marginal)))
        .sorted(Comparator.comparing(Driftavbrott::getSlut))
        .collect(Collectors.toList());

    // Sortera driftavbrott enligt slut (ascending) så att vi får de som slutade först först i samlingen

    // Returnera den sista gällande
    return driftavbrotts.stream().reduce((first, second) -> second);
  }
}
