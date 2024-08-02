package space.peetseater.rename;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UpdateExtensionActionTest {

    @Test
    void shouldTakeAction() {
        UpdateExtensionAction uea = new UpdateExtensionAction("txt", "png");
        boolean willAct = uea.shouldTakeAction(Path.of("file.txt"));
        assertTrue(willAct);
    }

    @Test
    void shouldNotTakeAction() {
        UpdateExtensionAction uea = new UpdateExtensionAction("txt", "png");
        boolean willAct = uea.shouldTakeAction(Path.of("file.png"));
        assertFalse(willAct);
    }

    @Test
    void getNewPath() {
        UpdateExtensionAction uea = new UpdateExtensionAction("txt", "md");
        Optional<String> np = uea.getNewPath(Path.of("txtfile.txt"));
        assertTrue(np.isPresent());
        String expected = Path.of("txtfile.md").toAbsolutePath().toString();
        assertEquals(expected, np.get());
    }
}