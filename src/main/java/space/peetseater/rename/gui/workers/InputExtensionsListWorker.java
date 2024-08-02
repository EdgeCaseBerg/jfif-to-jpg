package space.peetseater.rename.gui;

import space.peetseater.rename.PathVisitor;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class BackgroundFileExtensionWorker extends SwingWorker<HashSet<String>, BackgroundFileExtensionWorker.PartialResult> {

    private final Path searchStart;
    private final HashSet<String> extension;
    private int totalFilesChecked;
    public static final String PROGRESS_PROPERTY_NAME = "progress";

    public BackgroundFileExtensionWorker(Path searchStart) {
        this.searchStart = searchStart;
        this.extension = new HashSet<>();
        this.totalFilesChecked = 0;
    }

    @Override
    protected HashSet<String> doInBackground() throws Exception {
        Consumer<Path> collectExt = new PublishExtensionFromPath();
        PathVisitor pv = new PathVisitor(collectExt, searchStart, true);

        HashSet<FileVisitOption> optionsNoSymbolicLinks = new HashSet<>();
        Files.walkFileTree(searchStart, optionsNoSymbolicLinks, 64, pv);
        return extension;
    }

    @Override
    protected void done() {
        PartialResult newPartialResult = new PartialResult(totalFilesChecked, extension);
        PropertyChangeEvent event = new PropertyChangeEvent(this, PROGRESS_PROPERTY_NAME, null, newPartialResult);
        getPropertyChangeSupport().firePropertyChange(event);
        super.done();
    }

    @Override
    protected void process(List<PartialResult> chunks) {
        int oldAmount = totalFilesChecked;
        PartialResult oldPartialResult = new PartialResult(oldAmount, new HashSet<>(extension));
        for (PartialResult partialResult : chunks) {
            this.totalFilesChecked += partialResult.totalFilesChecked;
            this.extension.addAll(partialResult.extensionsSeen);
        }
        PartialResult newPartialResult = new PartialResult(totalFilesChecked, extension);
        PropertyChangeEvent event = new PropertyChangeEvent(this, PROGRESS_PROPERTY_NAME, oldPartialResult, newPartialResult);
        getPropertyChangeSupport().firePropertyChange(event);
    }

    public record PartialResult(int totalFilesChecked, HashSet<String> extensionsSeen) {};

    private class PublishExtensionFromPath implements Consumer<Path> {
        @Override
        public void accept(Path path) {
            char[] name = path.getFileName().toString().toCharArray();
            StringBuilder extBuilder = new StringBuilder(4);
            boolean foundDot = false;
            for (int i = name.length - 1; i != 0; i--) {
                if (name[i] == '.') {
                    foundDot = true;
                    break;
                }
                extBuilder.append(name[i]);
            }
            if (!extBuilder.isEmpty() && foundDot) {
                HashSet<String> newValues = new HashSet<>(1);
                newValues.add(extBuilder.reverse().toString());
                publish(new PartialResult(1, newValues));
            } else {
                publish(new PartialResult(1, new HashSet<>()));
            }

            // Spin lock for testing purposes, remove later
//            Date now = new Date();
//            Date later = new Date(now.getTime() + 20);
//            while (now.before(later)) {
//                now = new Date();
//            }
        }
    }
}
