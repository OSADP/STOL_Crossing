package gov.dot.fhwa.saxton.crossingrequest;

import gov.dot.fhwa.saxton.crossingrequest.geometry.GeofenceFactory;
import gov.dot.fhwa.saxton.crossingrequest.logger.CsvLogger;
import gov.dot.fhwa.saxton.crossingrequest.model.CrossingGeometryModel;
import gov.dot.fhwa.saxton.crossingrequest.model.NotificationStatusModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Entry point for CrossingRequestServer application
 *
 * Also serves as Spring Boot framework configuration file
 */

@EnableAutoConfiguration
@Configuration
@ComponentScan("gov.dot.fhwa.saxton.crossingrequest")
public class CrossingRequestServerMain {

    @Value("${geometry.pedDescriptionFile}")
    protected String pedestrianGeofenceFilepath;

    @Value("${geometry.driverDescriptionFile}")
    protected String driverGeofenceFilepath;

    @Value("${logging.eventFile}")
    protected String eventLogFilepath;

    @Value("${logging.dataFile}")
    protected String dataLogFilepath;

    @Bean
    public NotificationStatusModel getNotificationStatusModel() {
        return new NotificationStatusModel();
    }

    @Bean
    public GeofenceFactory getGeofenceFactory() {
        return new GeofenceFactory();
    }

    @Bean
    public CsvLogger getCsvLogger() {
        CsvLogger dataLogger = new CsvLogger();
        dataLogger.open(eventLogFilepath, dataLogFilepath);
        return dataLogger;
    }

    @Bean
    public CrossingGeometryModel getCrossingGeometryModel() {
        CrossingGeometryModel cgm = new CrossingGeometryModel();
        cgm.loadDriverGeofenceFromFile(driverGeofenceFilepath);
        cgm.loadPedestrianGeofenceFromFile(pedestrianGeofenceFilepath);
        return cgm;
    }

    @Bean
    public NotificationStatusModel notificationStatusModel() {
        return new NotificationStatusModel();
    }

    private static ApplicationContext ctx;

    /**
     * Main entry point for the CrossingRequest server application. Run automatically when the JAR starts.
     *
     * Primary function is to instantiate the SpringBoot environment and let it take over from there
     * @param args Commandline arguments
     */
    public static void main(String[] args) {
        ctx = SpringApplication.run(CrossingRequestServerMain.class);
    }
}
