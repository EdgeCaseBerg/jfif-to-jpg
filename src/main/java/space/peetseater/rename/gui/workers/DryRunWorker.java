package space.peetseater.rename.gui.workers;

import space.peetseater.rename.*;
import space.peetseater.rename.gui.RenameOptionsBuilder;

import java.nio.file.Path;

public class DryRunWorker extends ConditionalConsumerWorker {

    public DryRunWorker(RenameOptionsBuilder optionsBuilder) {
        super(optionsBuilder);
    }

    @Override
    public ConditionalConsumer<Path> getAction() {
        String inExt = optionsBuilder.getInputExtension();
        String outExt = optionsBuilder.getOutputExtension();
        return new DryRunAction<>(new UpdateExtensionAction(inExt, outExt));
    }

}
