package space.peetseater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.StandardCopyOption;

public class Main {
    public static void fixFile(Path path) throws IOException {
        if (path.getFileName().toString().endsWith(".jfif")) {
            String[] parts = path.toAbsolutePath().toString().split(".jfif");
            Path newPath = Paths.get(parts[0]  + ".jpg");
            Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            String filename = args[i];
            Path originalFile = Paths.get(filename);
            if (originalFile.toFile().exists()) {
                if (originalFile.toFile().isDirectory()) {
                    Files.walkFileTree(originalFile, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (attrs.isRegularFile()) {
                                fixFile(file);
                            };
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } else {
                    fixFile(originalFile);
                }
            } else {
                System.out.printf("File does not exist: %s%n", filename);
            }
        }
    }
}