package space.peetseater.rename;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class CommandLineInterface {

    public static final String oldExtensionDefault = ".jfif";
    public static final String newExtensionDefault = ".jpg";
    private final static Logger logger = Logger.getLogger(CommandLineInterface.class.getCanonicalName());

    public static void main(String[] args) throws IOException, IllegalArgumentException {
        displayHelpAndExitIfAsked(args);
        LoggingHelper.configureLogLevelsByInput(args, logger);

        // The only truly required input is the path itself via --help
        String  extensionToChange = Input.getInExtension(args).orElse(oldExtensionDefault);
        String  extensionToBecome = Input.getOutExtension(args).orElse(newExtensionDefault);
        boolean recursive         = Input.getRecursive(args).orElse(false);
        Optional<String> maybePath= Input.getPath(args);
        exitWithMessageIfEmpty(maybePath,  "No Path defined in arguments. Pass %s to view help".formatted(Input.HELP_FLAG));
        assert maybePath.isPresent();
        String  path              = maybePath.get();

        logger.fine("Extension to change: %s".formatted(extensionToChange));
        logger.fine("Will become extension: %s".formatted(extensionToBecome));
        logger.fine("Path is %s".formatted(path));
        logger.fine("Sub-folders will be processed %s".formatted(recursive));

        Path originalPath = Paths.get(path);
        exitIfFileDoesNotExist(originalPath);

        Consumer<Path> action = new UpdateExtensionAction(extensionToChange, extensionToBecome);
        boolean dryRunOn = Input.getDryRunEnabled(args).orElse(false);
        if (dryRunOn) {
            action = new DryRunAction<>(action);
        }

        if (originalPath.toFile().isDirectory()) {
            logger.fine("Walking directory to change all files");
            Files.walkFileTree(originalPath, new PathVisitor(action, originalPath, recursive));
        } else {
            logger.fine("Changing single file");
            action.accept(originalPath);
        }
    }

    public static void exitWithMessageIfEmpty(Optional<?> opt, String messageOnEmpty) {
        if (opt.isPresent()) {
            return;
        }
        logger.warning(messageOnEmpty);
        System.exit(1);
    }

    public static void displayHelpAndExitIfAsked(String[] args) {
        Optional<String> showHelp = Input.getHelp(args);
        showHelp.ifPresent(logger::info);
        if (showHelp.isPresent()) System.exit(0);
    }

    public static void exitIfFileDoesNotExist(Path path) {
        File originalFile = path.toFile();
        if (!originalFile.exists()) {
            logger.warning("File does not exist: %s%n".formatted(path.toAbsolutePath()));
            System.exit(2);
        }
    }

}