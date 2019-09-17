package se.mdh.driftavbrott.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * En implementation av {@link DriftavbrottpostRepository} som hämtar {@link Driftavbrottpost}er från
 * en properties-fil.
 */
@Repository
public class DriftavbrottpostRepositoryProperties implements DriftavbrottpostRepository {
  private static final Log log = LogFactory.getLog(DriftavbrottpostRepositoryProperties.class);
  private static final String KANAL_DEFAULT = "default";

  private String propertiesfil;

  private ResourceBundle driftavbrottBundleSv;
  private ResourceBundle driftavbrottBundleEn;

  public DriftavbrottpostRepositoryProperties(@Qualifier("se.mdh.driftavbrott.repository.propertiesfil") final String propertiesfil) {
    this.propertiesfil = propertiesfil;
    driftavbrottBundleSv = ResourceBundle.getBundle("se.mdh.driftavbrott.Driftavbrott", new Locale("sv"));
    driftavbrottBundleEn = ResourceBundle.getBundle("se.mdh.driftavbrott.Driftavbrott", new Locale("en"));
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
        if(splitted.length >= 3) {
          post.setMeddelandeSv(splitted[2]);
        }

        if(splitted.length >= 4) {
          post.setMeddelandeEn(splitted[3]);
        }

        // Sätt defaultvärde om inget är angivet i driftavbrottet
        if(StringUtils.isEmpty(post.getMeddelandeSv())) {
          String defaultMeddelandeSv = "";

          try {
            defaultMeddelandeSv = driftavbrottBundleSv.getString(post.getKanal());
          }
          catch(MissingResourceException e1) {
            try {
              defaultMeddelandeSv = driftavbrottBundleSv.getString(KANAL_DEFAULT);
              log.debug("Inget defaultmeddelande konfigurerat för kanalen "
                            + post.getKanal()
                            + " på svenska. Använder ett generellt defaultmeddelande.");
            }
            catch(MissingResourceException e2) {
              log.debug("Inget defaultmeddelande konfigurerat för kanalen "
                            + post.getKanal()
                            + " på svenska. Använder ett tomt meddelande.");
            }
          }

          post.setMeddelandeSv(defaultMeddelandeSv);
        }

        if(StringUtils.isEmpty(post.getMeddelandeEn())) {
          String defaultMeddelandeEn = "";

          try {
            defaultMeddelandeEn = driftavbrottBundleEn.getString(post.getKanal());
          }
          catch(MissingResourceException e1) {
            try {
              defaultMeddelandeEn = driftavbrottBundleEn.getString(KANAL_DEFAULT);
              log.debug("Inget defaultmeddelande konfigurerat för kanalen "
                            + post.getKanal()
                            + " på engelska. Använder ett generellt defaultmeddelande.");
            }
            catch(MissingResourceException e2) {
              log.debug("Inget defaultmeddelande konfigurerat för kanalen "
                            + post.getKanal()
                            + " på engelska. Använder ett tomt meddelande.");
            }
          }

          post.setMeddelandeEn(defaultMeddelandeEn);
        }

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
