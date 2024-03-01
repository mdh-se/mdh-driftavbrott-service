package se.mdh.driftavbrott.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by johan on 2017-02-08.
 *
 * @author Johan Nilsson
 */
@Configuration
public class DocConfig {
  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("mdh-driftavbrott-service")
                  .description("API för mdh-driftavbrott-service.")
                  .version("v1"));
  }
}
