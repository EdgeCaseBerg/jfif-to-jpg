package space.peetseater.rename;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.function.Consumer;

public class UpdateExtensionAction implements Consumer<Path> {

    private final String newExtension;
    private final String oldExtension;

    public UpdateExtensionAction(String oldExtension, String newExtension) {
        this.oldExtension = oldExtension;
        this.newExtension = newExtension;
    }

    @Override
    public String toString() {
        return "rename %s to %s action".formatted(oldExtension, newExtension);
    }

    public boolean shouldTakeAction(Path path) {
        return path.getFileName().toString().endsWith(oldExtension);
    }

    public Optional<String> getNewPath(Path path) {
        if (path.getFileName().toString().endsWith(oldExtension)) {
            String stringPath = path.toAbsolutePath().toString();
            String pathless = stringPath.substring(0, stringPath.length() - oldExtension.length());
            return Optional.of(pathless + newExtension);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void accept(Path path) {
        if (shouldTakeAction(path)) {
            getNewPath(path).ifPresent(s -> {
                Path newPath = Paths.get(s);
                try {
                    Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
