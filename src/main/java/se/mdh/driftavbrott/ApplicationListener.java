package se.mdh.driftavbrott;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * En {@link ServletContextListener} som loggar när en
 * webbapplikation startar och avslutas. Lägg till följande konfiguration i
 * web.xml för att aktivera den:
 * <pre>
 *   &lt;listener&gt;
 *     &lt;listener-class&gt;se.mdh.driftavbrott.ApplicationListener&lt;/listener-class&gt;
 *   &lt;/listener&gt;
 * </pre>
 *
 * @author Dennis Lundberg
 */
public class ApplicationListener implements ServletContextListener {
  /**
   * Den log som skall användas.
   */
  private static final Log log = LogFactory.getLog(ApplicationListener.class);

  /**
   * {@inheritDoc}
   */
  @Override
  public void contextInitialized(final ServletContextEvent event) {
    ServletContext context = event.getServletContext();

    log.info("Applikationen '" + context.getServletContextName()
                 + "' startar.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void contextDestroyed(final ServletContextEvent event) {
    ServletContext context = event.getServletContext();

    log.info("Applikationen '" + context.getServletContextName()
                 + "' avslutas.");
  }
}
