package space.peetseater.rename;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;

public class PathVisitor extends SimpleFileVisitor<Path> {
    private final Path startPath;
    private final boolean isRecursive;
    private final Consumer<Path> consumer;

    public PathVisitor(Consumer<Path> pathConsumer, Path startPath, boolean recurse
    ) {
        this.consumer = pathConsumer;
        this.startPath = startPath;
        this.isRecursive = recurse;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (startPath.equals(dir)) return FileVisitResult.CONTINUE;
        if (!isRecursive) {
            return FileVisitResult.SKIP_SUBTREE;
        }
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile()) {
            this.consumer.accept(file);
        }
        return FileVisitResult.CONTINUE;
    }
}