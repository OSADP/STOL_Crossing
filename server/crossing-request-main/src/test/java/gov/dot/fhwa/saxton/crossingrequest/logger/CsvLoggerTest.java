package gov.dot.fhwa.saxton.crossingrequest.logger;

import gov.dot.fhwa.saxton.crossingrequest.CrossingRequestServerMain;
import gov.dot.fhwa.saxton.crossingrequest.model.messages.EventReport;
import gov.dot.fhwa.saxton.crossingrequest.model.messages.PedestrianDataReport;
import gov.dot.fhwa.saxton.crossingrequest.model.messages.UserRole;
import gov.dot.fhwa.saxton.crossingrequest.model.geometry.Location;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Unit test for CsvLogger, ensures that events are logged correctly and recorded in the file properly
 */
@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from the OrderServiceConfig class
@ContextConfiguration(classes=CrossingRequestServerMain.class, loader=AnnotationConfigContextLoader.class)
public class CsvLoggerTest {

    @Autowired
    CsvLogger log;

    @Test
    public void testRecordEvent() throws Exception {
        for (int i = 0; i < 10; i++) {
            log.recordEvent(new LogEntry(new EventReport(System.currentTimeMillis(), EventReport.EventType.GEOFENCE_ENTERED, UserRole.MOTORIST)));
        }
        log.writeEventLogs();
    }

    @Test
    public void testRecordData() throws Exception {
        for (int i = 0; i < 10; i++) {
            log.recordData(new LogEntry(new PedestrianDataReport(System.currentTimeMillis(), new Location(33.3, 77.7))));
        }
        log.writeDataLogs();
    }
    @Configuration
	@ComponentScan("gov.dot.fhwa.saxton.crossingrequest")
	static class PropConfig {

		// because @PropertySource doesnt work in annotation only land
		@Bean
        PropertyPlaceholderConfigurer propConfig() {
			PropertyPlaceholderConfigurer ppc =  new PropertyPlaceholderConfigurer();
			ppc.setLocation(new ClassPathResource("application.properties"));
			return ppc;
		}
}
}