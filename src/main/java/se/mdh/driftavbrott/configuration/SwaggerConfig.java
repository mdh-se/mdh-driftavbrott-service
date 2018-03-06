package se.mdh.driftavbrott.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("Controller API")
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("se.mdh.driftavbrott.rest"))
        .paths(PathSelectors.regex(".*/.*"))
        .build()
        .useDefaultResponseMessages(false);
  }

  @Bean
  public Docket actuatorApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("Spring Actuator")
        .select()
        .apis(RequestHandlerSelectors.basePackage("org.springframework.boot.actuate"))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("MDH Driftavbrott Service")
        .description("API för MDH Driftavbrott Service.")
        .version("1")
        .build();
  }
}
