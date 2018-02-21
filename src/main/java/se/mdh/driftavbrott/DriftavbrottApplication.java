package se.mdh.driftavbrott;

import javax.servlet.ServletContextListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DriftavbrottApplication {
  public static void main(final String[] args) {
    SpringApplication.run(DriftavbrottApplication.class, args);
  }

  @Bean
  ServletListenerRegistrationBean<ServletContextListener> myServletListener() {
    ServletListenerRegistrationBean<ServletContextListener> servletListenerRegistrationBean = new ServletListenerRegistrationBean<>();
    servletListenerRegistrationBean.setListener(new ApplicationListener());
    return servletListenerRegistrationBean;
  }
}
