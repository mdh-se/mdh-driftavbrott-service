package se.mdh.driftavbrott.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
  @Bean("se.mdh.driftavbrott.repository.propertiesfil")
  public String getRepositoryPropertiesFil() {
    return "driftavbrott.properties";
  }
}
