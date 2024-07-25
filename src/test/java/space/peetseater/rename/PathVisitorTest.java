package space.peetseater.rename;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.HashSet;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class PathVisitorTest {

    Path tmpDir;
    private Path tmpTxtInRootFolder;
    private Path recFolder;
    private Path tmpTxtInSubFolder;

    @BeforeEach
    void setUp() throws IOException {
        Path testDir = Paths.get(".");
        tmpDir = Files.createTempDirectory(testDir, "pathVisitorTest");
        tmpTxtInRootFolder = Files.createFile(tmpDir.resolve("tmp.txt"));
        recFolder =  Files.createTempDirectory(tmpDir, "recursiveFolder");
        tmpTxtInSubFolder = Files.createFile(recFolder.resolve("sub.txt"));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.delete(tmpTxtInSubFolder);
        Files.delete(recFolder);
        Files.delete(tmpTxtInRootFolder);
        Files.delete(tmpDir);
    }

    @Test
    void visitorDoesNotVisitFilesIfNotRecursing() throws IOException {
        HashSet<Path> seen = new HashSet<>();
        Consumer<Path> consumer = new Consumer<Path>() {
            @Override
            public void accept(Path path) {
                seen.add(path);
            }
        };
        PathVisitor pathVisitor = new PathVisitor(consumer, tmpDir, false);
        Files.walkFileTree(tmpDir, pathVisitor);
        assertTrue(seen.contains(tmpTxtInRootFolder));
        assertFalse(seen.contains(tmpTxtInSubFolder));
    }

    @Test
    void visitorVisitsFilesIfRecursing() throws IOException {
        HashSet<Path> seen = new HashSet<>();
        Consumer<Path> consumer = new Consumer<Path>() {
            @Override
            public void accept(Path path) {
                seen.add(path);
            }
        };
        PathVisitor pathVisitor = new PathVisitor(consumer, tmpDir, true);
        Files.walkFileTree(tmpDir, pathVisitor);
        assertTrue(seen.contains(tmpTxtInRootFolder));
        assertTrue(seen.contains(tmpTxtInSubFolder));
    }
}