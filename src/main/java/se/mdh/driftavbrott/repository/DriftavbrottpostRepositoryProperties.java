package se.mdh.driftavbrott.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * En implementation av {@link DriftavbrottpostRepository} som hämtar {@link Driftavbrottpost}er från
 * en properties-fil.
 */
@Repository
public class DriftavbrottpostRepositoryProperties implements DriftavbrottpostRepository {
  private String propertiesfil;

  public DriftavbrottpostRepositoryProperties(@Qualifier("se.mdh.driftavbrott.repository.propertiesfil") final String propertiesfil) {
    this.propertiesfil = propertiesfil;
  }

  @Override
  public List<Driftavbrottpost> listaPoster(final LocalDate efter) throws DriftavbrottpostRepositoryException {
    InputStream inputStream = null;
    try {
      List<Driftavbrottpost> poster = new ArrayList<>();
      inputStream = getClass().getClassLoader().getResourceAsStream(propertiesfil);
      if(inputStream == null) {
        throw new FileNotFoundException("Hittade inte properties-filen '" + propertiesfil + "' på classpath.");
      }
      Properties properties = new Properties();
      properties.load(inputStream);
      for(Object key : properties.keySet()) {
        String value = properties.getProperty((String) key);
        String[] splitted = StringUtils.split(value, ";");

        Driftavbrottpost post = new Driftavbrottpost();
        post.setKanal((String) key);
        post.setStart(splitted[0]);
        post.setSlut(splitted[1]);
        post.setDefaultMeddelandeSv(splitted[2]);
        post.setDefaultMeddelandeEn(splitted[3]);

        poster.add(post);
      }

      return poster;
    }
    catch(IOException e) {
      throw new DriftavbrottpostRepositoryException("Det gick inte att läsa properties-filen: '" + propertiesfil + "'", e);
    }
    finally {
      IOUtils.closeQuietly(inputStream);
    }
  }
}
