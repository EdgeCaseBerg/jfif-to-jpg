package space.peetseater.rename;

import java.util.Optional;

public class Input {
    private static final String IN_FLAG  = "--in";
    private static final String OUT_FLAG = "--out";
    private static final String RECURSE_FLAG = "--recursive";
    private static final String PATH_FLAG = "--path";

    static Optional<String> getInExtension(String[] args) {
        return flagFromArgs(args, IN_FLAG);
    }

    static Optional<String> getOutExtension(String[] args) {
        return flagFromArgs(args, OUT_FLAG);
    }

    static Optional<Boolean> getRecursive(String[] args) {
        Optional<String> possibleValue = flagFromArgs(args, RECURSE_FLAG);
        return possibleValue.map("true"::equals);
    }

    static Optional<String> getPath(String[] args) {
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

    static Optional<String> flagFromArgs(String[] args, String flag) {
        for (int i = 0, a = 1; a < args.length; i++, a++) {
            boolean hasArgument = args[i].startsWith(flag);
            if (hasArgument && !args[a].startsWith("--")) {
                return Optional.of(args[a]);
            }
        }
        return Optional.empty();
    }

}