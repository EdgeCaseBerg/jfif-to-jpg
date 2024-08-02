package space.peetseater.rename;

import java.util.Optional;
import java.util.logging.Level;

public class Input {
    public  static final String IN_FLAG      = "--in";
    public  static final String OUT_FLAG     = "--out";
    public  static final String RECURSE_FLAG = "--recursive";
    public  static final String PATH_FLAG    = "--path";
    public  static final String HELP_FLAG    = "--help";
    public  static final String VERBOSE_FLAG = "--verbose";
    public  static final String DRY_RUN_FLAG = "--dry-run";
    public  static final String SHOW_GUI     = "--gui";
    private static final String HELP_TEXT = helpText();

    public static Optional<String> getInExtension(String[] args) {
        return flaggedValueFromArgs(args, IN_FLAG);
    }

    public static Optional<String> getOutExtension(String[] args) {
        return flaggedValueFromArgs(args, OUT_FLAG);
    }

    public static Optional<Boolean> getRecursive(String[] args) {
        Optional<String> possibleValue = flaggedValueFromArgs(args, RECURSE_FLAG);
        return possibleValue.map("true"::equals);
    }

    public static Optional<Boolean> getDryRunEnabled(String[] args) {
        Optional<String> possibleValue = flaggedValueFromArgs(args, DRY_RUN_FLAG);
        return possibleValue.map("true"::equals);
    }

    static Optional<Boolean> getGUIEnabled(String[] args) {
        Optional<String> possibleValue = flaggedValueFromArgs(args, SHOW_GUI);
        return possibleValue.map("true"::equals);
    }

    public static Optional<String> getPath(String[] args) {
        int pathStart = 0;
        int pathEnd = 0;

        // Find where --path starts
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(PATH_FLAG)) {
                pathStart = i + 1;
                break;
            }
        }

        // pathStart will always be +1 from where we found --path, never 0
        if (pathStart == 0) {
            return Optional.empty();
        }

        // Avoid consuming another argument as part of the path
        for (int i = pathStart; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                pathEnd = i;
                break;
            }
        }

        // When no arguments are found ahead of us, the end of the args is the end of the path
        if (pathEnd == 0) {
            pathEnd = args.length;
        }

        // From --path until the end or the next --flag, join the args to one string
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = pathStart; i < pathEnd; i++) {
            if (i != pathStart) stringBuilder.append(" ");
            stringBuilder.append(args[i]);
        }
        String trimmed = stringBuilder.toString();
        if (trimmed.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(trimmed);
        }
    }

    static Optional<String> flaggedValueFromArgs(String[] args, String flag) {
        for (int i = 0, a = 1; a < args.length; i++, a++) {
            boolean hasArgument = args[i].startsWith(flag);
            if (hasArgument && !args[a].startsWith("--")) {
                return Optional.of(args[a]);
            }
        }
        return Optional.empty();
    }

    public static Optional<Level> getLogLevel(String[] args) {
        for (String arg : args) {
            if (VERBOSE_FLAG.equals(arg.trim())) {
                return Optional.of(Level.FINE);
            }
        }
        return Optional.empty();
    }

    public static Optional<String> getHelp(String[] args) {
        for (String arg : args) {
            if (HELP_FLAG.equals(arg.trim())) {
                return Optional.of(HELP_TEXT);
            }
        }
        return Optional.empty();
    }

    private static String helpText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Bulk Extension Change Tool").append(System.lineSeparator());
        stringBuilder.append(System.lineSeparator());
        String[][] directions = new String[][]{
            {IN_FLAG, "extension to change, without the dot. Defaults to jfif"},
            {OUT_FLAG, "extension the changed files will have after processing, without the dot. Defaults to jpg"},
            {RECURSE_FLAG, "whether or not all files in the given path should be modified, false by default"},
            {PATH_FLAG, "A path to a directory or single file, if directory then all files in subdirectories will be walked if the recursive flag is passed"},
            {DRY_RUN_FLAG, "Do not actually rename the files, just list what would be changed"},
            {VERBOSE_FLAG, "Increase logging level to FINE for application"},
            {SHOW_GUI, "Display a window to select and rename extensions instead of immediately executing"},
            {HELP_FLAG, "display this help text"}
        };
        for (String[] direction : directions) {
            stringBuilder.append("%-15s%s%s".formatted(direction[0], direction[1], System.lineSeparator()));
        }
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Change a single file from txt to markdown").append(System.lineSeparator());
        stringBuilder.append("\textbulkrename %s txt %s md %s path\\tofile.txt".formatted(IN_FLAG, OUT_FLAG, PATH_FLAG));
        stringBuilder.append(System.lineSeparator()).append(System.lineSeparator());
        stringBuilder.append("Change all files within a folder and its subfolders").append(System.lineSeparator());
        stringBuilder.append("\textbulkrename %s txt %s md %s path\\todirectory %s true".formatted(IN_FLAG, OUT_FLAG, PATH_FLAG, RECURSE_FLAG));
        stringBuilder.append(System.lineSeparator()).append(System.lineSeparator());
        stringBuilder.append("Change all files within a folder without changing files in subfolders").append(System.lineSeparator());
        stringBuilder.append("\textbulkrename %s jfif %s jpg %s path\\todirectory".formatted(IN_FLAG, OUT_FLAG, PATH_FLAG));
        stringBuilder.append(System.lineSeparator());

        return stringBuilder.toString();
    }
}