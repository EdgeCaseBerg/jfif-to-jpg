package space.peetseater.rename;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InputTest {

    @Test
    void getInExtensionOnValidInput() {
        Optional<String> parsed = Input.getInExtension(new String[]{
            "--out", "outvalue", "--in", "invalue"
        });
        assertTrue(parsed.isPresent());
        String value = parsed.get();
        assertEquals("invalue", value, "Expected invalue");
    }

    @Test
    void getInExtensionOnInvalidInput() {
        Optional<String> parsed = Input.getInExtension(new String[]{
            "--out", "outvalue", "--in",
        });
        assertTrue(parsed.isEmpty(), "Expected empty option but got %s".formatted(parsed));
    }

    @Test
    void getInExtensionOnMissingInput() {
        Optional<String> parsed = Input.getInExtension(new String[]{
            "--in", "--out", "outvalue"
        });
        assertTrue(parsed.isEmpty(), "Expected empty option but got %s".formatted(parsed));
    }

    @Test
    void getOutExtensionOnValidInput() {
        Optional<String> parsed = Input.getOutExtension(new String[]{
                "--out", "outvalue", "--in", "invalue"
        });
        assertTrue(parsed.isPresent());
        String value = parsed.get();
        assertEquals("outvalue", value, "Expected outvalue");
    }

    @Test
    void getOutExtensionOnInvalidInput() {
        Optional<String> parsed = Input.getOutExtension(new String[]{
                "--out"
        });
        assertTrue(parsed.isEmpty(), "Expected empty option but got %s".formatted(parsed));
    }

    @Test
    void getOutExtensionOnMissingInput() {
        Optional<String> parsed = Input.getOutExtension(new String[]{
                "--out", "--in", "invalue"
        });
        assertTrue(parsed.isEmpty(), "Expected empty option but got %s".formatted(parsed));
    }

    @Test
    void getRecursiveTrue() {
        Optional<Boolean> parsed = Input.getRecursive(new String[]{
                "--recursive", "true"
        });
        assertTrue(parsed.isPresent());
        boolean value = parsed.get();
        assertTrue(value, "Expected true");
    }

    @Test
    void getRecursiveFalse() {
        Optional<Boolean> parsed = Input.getRecursive(new String[]{
                "--recursive", "false"
        });
        assertTrue(parsed.isPresent());
        boolean value = parsed.get();
        assertFalse(value, "Expected false");
    }

    @Test
    void getRecursiveMissingValue() {
        Optional<Boolean> parsed = Input.getRecursive(new String[]{
                "--out", "?", "--in", "?", "--recursive"
        });
        assertTrue(parsed.isEmpty());
    }

    @Test
    void getRecursiveMissingFlag() {
        Optional<Boolean> parsed = Input.getRecursive(new String[]{
                "--out", "?", "--in", "?"
        });
        assertTrue(parsed.isEmpty());
    }

    @Test
    void getPathNoSpaces() {
        Optional<String> parsed = Input.getPath(new String[]{
                "--path", "\\my\\path\\in\\windowsworld"
        });
        assertTrue(parsed.isPresent());
        String value = parsed.get();
        assertEquals("\\my\\path\\in\\windowsworld", value);
    }

    @Test
    void getPathSpacesFromWindows() {
        Optional<String> parsed = Input.getPath(new String[]{
                "--path", "windows", "will", "do", "this", "with", "args"
        });
        assertTrue(parsed.isPresent());
        String value = parsed.get();
        assertEquals("windows will do this with args", value);
    }

    @Test
    void getPathBetweenOtherArgs() {
        Optional<String> parsed = Input.getPath(new String[]{
                "--in", "extension1", "--path", "windows", "will", "do", "this", "with", "args", "--out", "extension2"
        });
        assertTrue(parsed.isPresent());
        String value = parsed.get();
        assertEquals("windows will do this with args", value);
    }

    @Test
    void getPathEmptyWhenNotProvided() {
        Optional<String> parsed = Input.getPath(new String[]{
                "--in", "no", "--out", "pathdefined"
        });
        assertTrue(parsed.isEmpty());
    }
}