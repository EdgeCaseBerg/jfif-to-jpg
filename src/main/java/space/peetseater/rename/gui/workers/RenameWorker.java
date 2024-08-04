package space.peetseater.rename.gui.workers;

import space.peetseater.rename.ConditionalConsumer;
import space.peetseater.rename.UpdateExtensionAction;
import space.peetseater.rename.gui.RenameOptionsBuilder;

import java.nio.file.Path;

public class RenameWorker extends ConditionalConsumerWorker {
    @Override
    public String getWorkerName() {
        return "Renaming Worker";
    }

    public RenameWorker(RenameOptionsBuilder optionsBuilder, int numberOfFilesToProcess) {
        super(optionsBuilder, numberOfFilesToProcess);
    }

    @Override
    public ConditionalConsumer<Path> getAction() {
        String inExt = optionsBuilder.getInputExtension();
        String outExt = optionsBuilder.getOutputExtension();
        return new UpdateExtensionAction(inExt, outExt);
    }
}
