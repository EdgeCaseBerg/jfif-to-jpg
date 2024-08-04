package space.peetseater.rename;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.StandardCopyOption;

public class OriginalRenameCode {

    static final String oldExtension = ".jfif";
    static final String newExtension = ".jpg";

    public static void fixFile(Path path) throws IOException {
        if (path.getFileName().toString().endsWith(oldExtension)) {
            String[] parts = path.toAbsolutePath().toString().split(oldExtension);
            Path newPath = Paths.get(parts[0]  + newExtension);
            Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    static class FileRenameVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (attrs.isRegularFile()) {
                fixFile(file);
            }
            return FileVisitResult.CONTINUE;
        }
    }

    public static void main(String[] args) throws IOException {
        for (String filename : args) {
            Path originalPath = Paths.get(filename);
            File originalFile = originalPath.toFile();
            if (!originalFile.exists()) {
                System.out.printf("File does not exist: %s%n", filename);
                continue;
            }

            if (originalPath.toFile().isDirectory()) {
                Files.walkFileTree(originalPath, new FileRenameVisitor());
            } else {
                fixFile(originalPath);
            }
        }
    }
}