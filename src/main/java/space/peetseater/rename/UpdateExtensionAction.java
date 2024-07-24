package space.peetseater.rename;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class UpdateExtensionAction implements Consumer<Path> {

    private final String newExtension;
    private final String oldExtension;

    public UpdateExtensionAction(String oldExtension, String newExtension) {
        this.oldExtension = oldExtension;
        this.newExtension = newExtension;
    }

    @Override
    public void accept(Path path) {
        if (path.getFileName().toString().endsWith(oldExtension)) {
            String[] parts = path.toAbsolutePath().toString().split(oldExtension);
            Path newPath = Paths.get(parts[0]  + newExtension);
            try {
                Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
