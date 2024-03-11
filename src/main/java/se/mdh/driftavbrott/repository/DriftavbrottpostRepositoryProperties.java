package se.mdh.driftavbrott.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import se.mdh.driftavbrott.modell.NivaType;

/**
 * En implementation av {@link DriftavbrottpostRepository} som hämtar {@link Driftavbrottpost}er från
 * en properties-fil.
 *
 * @author Dennis Lundberg
 */
@Repository
public class DriftavbrottpostRepositoryProperties implements DriftavbrottpostRepository {
  private static final Log log = LogFactory.getLog(DriftavbrottpostRepositoryProperties.class);
  private static final String KANAL_DEFAULT = "default";
  private static final String KANAL_WARN_DEFAULT = "default.warn";
  private static final String KANAL_INFO_DEFAULT = "default.info";

  private String propertiesfil;

  private ResourceBundle driftavbrottBundleSv;
  private ResourceBundle driftavbrottBundleEn;

  public DriftavbrottpostRepositoryProperties(@Qualifier("se.mdh.driftavbrott.repository.propertiesfil") final String propertiesfil) {
    this.propertiesfil = propertiesfil;
    driftavbrottBundleSv = ResourceBundle.getBundle("se.mdh.driftavbrott.Driftavbrott", new Locale("sv"));
    driftavbrottBundleEn = ResourceBundle.getBundle("se.mdh.driftavbrott.Driftavbrott", new Locale("en"));
  }

  @Override
  public List<Driftavbrottpost> listaPoster() throws DriftavbrottpostRepositoryException {
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

        // Svenska
        // Sätt defaultvärde om inget är angivet i driftavbrottet
        if(StringUtils.isEmpty(post.getMeddelandeSv())) {
          String defaultMeddelandeSv = "";

          if(driftavbrottBundleSv.containsKey(post.getKanal())) {
            defaultMeddelandeSv = driftavbrottBundleSv.getString(post.getKanal());
          }
          else {
            if(post.getKanal().endsWith(NivaType.WARN.value().toLowerCase())) {
              if(driftavbrottBundleSv.containsKey(KANAL_WARN_DEFAULT)) {
                defaultMeddelandeSv = driftavbrottBundleSv.getString(KANAL_WARN_DEFAULT);
              }
              else if(driftavbrottBundleSv.containsKey(KANAL_DEFAULT)) {
                defaultMeddelandeSv = driftavbrottBundleSv.getString(KANAL_DEFAULT);
              }
            }
            else if(post.getKanal().endsWith(NivaType.INFO.value().toLowerCase())) {
              if(driftavbrottBundleSv.containsKey(KANAL_INFO_DEFAULT)) {
                defaultMeddelandeSv = driftavbrottBundleSv.getString(KANAL_INFO_DEFAULT);
              }
              else if(driftavbrottBundleSv.containsKey(KANAL_DEFAULT)) {
                defaultMeddelandeSv = driftavbrottBundleSv.getString(KANAL_DEFAULT);
              }
            }
          }

          post.setMeddelandeSv(defaultMeddelandeSv);
        }

        //Engelska
        // Sätt defaultvärde om inget är angivet i driftavbrottet
        if(StringUtils.isEmpty(post.getMeddelandeEn())) {
          String defaultMeddelandeEn = "";

          if(driftavbrottBundleEn.containsKey(post.getKanal())) {
            defaultMeddelandeEn = driftavbrottBundleEn.getString(post.getKanal());
          }
          else {
            if(post.getKanal().endsWith(NivaType.WARN.value().toLowerCase())) {
              if(driftavbrottBundleEn.containsKey(KANAL_WARN_DEFAULT)) {
                defaultMeddelandeEn = driftavbrottBundleEn.getString(KANAL_WARN_DEFAULT);
              }
              else if(driftavbrottBundleEn.containsKey(KANAL_DEFAULT)) {
                defaultMeddelandeEn = driftavbrottBundleEn.getString(KANAL_DEFAULT);
              }
            }
            else if(post.getKanal().endsWith(NivaType.INFO.value().toLowerCase())) {
              if(driftavbrottBundleEn.containsKey(KANAL_INFO_DEFAULT)) {
                defaultMeddelandeEn = driftavbrottBundleEn.getString(KANAL_INFO_DEFAULT);
              }
              else if(driftavbrottBundleEn.containsKey(KANAL_DEFAULT)) {
                defaultMeddelandeEn = driftavbrottBundleEn.getString(KANAL_DEFAULT);
              }
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
