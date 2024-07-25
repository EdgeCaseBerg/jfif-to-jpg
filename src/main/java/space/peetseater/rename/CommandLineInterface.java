package space.peetseater.rename;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandLineInterface {

    public static final String oldExtensionDefault = ".jfif";
    public static final String newExtensionDefault = ".jpg";
    private final static Logger logger = Logger.getLogger(CommandLineInterface.class.getCanonicalName());

    private static void configureLogLevels(String[] args) {
        Optional<Level> loggerLevel = Input.getLogLevel(args);
        loggerLevel.ifPresent(logger::setLevel);

        Logger globalLogger = Logger.getLogger("");
        loggerLevel.ifPresent(globalLogger::setLevel);
        for (Handler handler : globalLogger.getHandlers()) {
            loggerLevel.ifPresent(handler::setLevel);
        }
    }

    public static void main(String[] args) throws IOException, IllegalArgumentException {
        // Help should be checked first so that we can early return
        Optional<String> showHelp = Input.getHelp(args);
        showHelp.ifPresent(logger::info);
        if (showHelp.isPresent()) return;

        configureLogLevels(args);

        // The only truly required input is the path itself via --help
        String  extensionToChange = Input.getInExtension(args).orElse(oldExtensionDefault);
        String  extensionToBecome = Input.getOutExtension(args).orElse(newExtensionDefault);
        boolean recursive         = Input.getRecursive(args).orElse(false);
        String  path              = Input.getPath(args).orElseThrow(new Supplier<IllegalArgumentException>() {
            @Override
            public IllegalArgumentException get() {
                return new IllegalArgumentException("No Path defined in arguments. Pass %s to view help".formatted(Input.HELP_FLAG));
            }
        });

        logger.fine("Extension to change: %s".formatted(extensionToChange));
        logger.fine("Will become extension: %s".formatted(extensionToBecome));
        logger.fine("Path is %s".formatted(path));
        logger.fine("Sub-folders will be processed %s".formatted(recursive));

        Path originalPath = Paths.get(path);
        File originalFile = originalPath.toFile();
        if (!originalFile.exists()) {
            logger.warning("File does not exist: %s%n".formatted(path));
            return;
        }

        // TODO: GUI mode
        // TODO: --dry-run mode?
        UpdateExtensionAction action = new UpdateExtensionAction(extensionToChange, extensionToBecome);
        if (originalPath.toFile().isDirectory()) {
            logger.fine("Walking directory to change all files");
            Files.walkFileTree(originalPath, new PathVisitor(action, originalPath, recursive));
        } else {
            logger.fine("Changing single file");
            action.accept(originalPath);
        }
    }
}