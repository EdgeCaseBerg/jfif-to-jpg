package space.peetseater.rename;

import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingHelper {
    /* By default loggers are at info, so if we want verbose to work then this needs to change */
    static void configureLogLevelsByInput(String[] args, Logger logger) {
        Optional<Level> loggerLevel = Input.getLogLevel(args);
        loggerLevel.ifPresent(logger::setLevel);

        Logger globalLogger = Logger.getLogger("");
        loggerLevel.ifPresent(globalLogger::setLevel);
        for (Handler handler : globalLogger.getHandlers()) {
            loggerLevel.ifPresent(handler::setLevel);
        }
    }
}
