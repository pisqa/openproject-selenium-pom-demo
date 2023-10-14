package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

    Logger logger = LogManager.getLogger(Log.class);

    public void info(String message) {
        logger.info(message);
    }


}
