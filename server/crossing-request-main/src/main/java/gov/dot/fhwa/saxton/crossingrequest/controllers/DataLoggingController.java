package gov.dot.fhwa.saxton.crossingrequest.controllers;

import gov.dot.fhwa.saxton.crossingrequest.logger.CsvLogger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Rest controller for stopping, starting, and rolling data log files
 */
@RestController
public class DataLoggingController {

    protected Logger log = LogManager.getLogger(getClass());

    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");

    @Value("${logging.datapath}")
    protected String logFilePath;

    protected final String eventPostfix = "EVENTS";
    protected final String dataPostfix = "DATA";
    protected final String fileExtension = ".csv";

    @Autowired
    protected CsvLogger logger;

    protected String curBaseName = "";

    /**
     * Return the current log file name base component
     *
     * @return The string that makes up the base (before EVENT/DATA) of the log file name
     */
    @RequestMapping(value = "logs/basefilename")
    public String getCurrentLogFileBase() {
        return curBaseName;
    }

    /**
     * Begin a new set of log files, one for data and one for events, with the current timestamp.
     *
     * Should be called before each experimental run where data is to be collected
     */
    @RequestMapping(value = "logs/start")
    public void triggerLogRolling() {
        String baseName = generateNewLogFileBaseName();
        curBaseName = baseName;
        String eventFileName = logFilePath + baseName + eventPostfix + fileExtension;
        String dataFileName = logFilePath + baseName + dataPostfix + fileExtension;
        logger.open(eventFileName,
                dataFileName);

        log.info("Rolling data log files to events=" + eventFileName + " and data=" + dataFileName);
    }

    /**
     * Stop logging to the configured files and force the buffer to flush.
     *
     * Ensures that log messages of interest during an experiment or data collection are not lost
     */
    @RequestMapping(value = "logs/stop")
    public void finalizeLogging() {
        log.info("Closing logs.");
        curBaseName = "";
        logger.close();
    }

    /**
     * Generate the base name for new logging files
     * @return A base name for log files based on the date
     */
    protected String generateNewLogFileBaseName() {
        return simpleDateFormat.format(new Date());
    }
}
