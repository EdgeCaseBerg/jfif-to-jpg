package space.peetseater.rename;

import space.peetseater.rename.gui.GraphicalUserInterfaceRunner;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    public static Logger logger = Logger.getLogger(Main.class.toString());

    public static void main(String[] args) throws IOException {
        boolean useGUI = Input.getGUIEnabled(args).orElse(false);
        LoggingHelper.configureLogLevelsByInput(args, logger);

        if(useGUI) {
            logger.fine("Starting program in GUI mode, skipping CLI processing");
            SwingUtilities.invokeLater(new GraphicalUserInterfaceRunner(args));
        } else {
            logger.fine("Starting program in CLI mode");
            CommandLineInterface.run(args);
        }
    }
}