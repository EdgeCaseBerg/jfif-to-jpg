package space.peetseater.rename.gui.workers;

import space.peetseater.rename.*;
import space.peetseater.rename.gui.RenameOptionsBuilder;

import javax.swing.*;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public abstract class ConditionalConsumerWorker extends SwingWorker<Integer, String> {

    protected final Path start;
    protected final boolean isRecursive;
    protected final ConditionalConsumer<Path> action;
    protected int operated = 0;
    protected HashSet<WorkerListener<String>> listeners;
    protected final RenameOptionsBuilder optionsBuilder;

    public ConditionalConsumerWorker(RenameOptionsBuilder optionsBuilder) {
        this(optionsBuilder, 0);
    }

    public abstract String getWorkerName();

    public ConditionalConsumerWorker(RenameOptionsBuilder optionsBuilder, int totalFilesToCheck) {
        this.optionsBuilder = optionsBuilder;
        start = Paths.get(optionsBuilder.getStartPath());
        isRecursive = optionsBuilder.isRecusive();
        listeners = new HashSet<WorkerListener<String>>();
        action = getAction();
    }

    abstract public ConditionalConsumer<Path> getAction();

    public void addListener(WorkerListener<String> workerListener) {
        listeners.add(workerListener);
    }

    @Override
    protected void process(List<String> chunks) {
        super.process(chunks);
        for (WorkerListener<String> workerListener : listeners) {
            workerListener.onChunk(chunks);
        }
    }

    @Override
    protected Integer doInBackground() throws Exception {
        Consumer<Path> reportingConsumer = new Consumer<Path>() {
            @Override
            public void accept(Path path) {
                action.accept(path);
                operated++;
                publish(action.getAudit(path).msg());
            }
        };
        PathVisitor pv = new PathVisitor(reportingConsumer, start, isRecursive);
        HashSet<FileVisitOption> optionsNoSymbolicLinks = new HashSet<>();
        Files.walkFileTree(start, optionsNoSymbolicLinks, 64, pv);
        return operated;
    }
}
