package space.peetseater.rename.gui;

public class RenameOptionsBuilder {
    private boolean isRecusive;
    private boolean isDryRun;
    private String startPath;
    private String inputExtension;
    private String outputExtension;

    public RenameOptionsBuilder() {
        isRecusive = false;
        isDryRun = true;
        startPath = null;
        inputExtension = null;
        outputExtension = null;
    }

    public boolean canRun() {
        return startPath != null && inputExtension != null && outputExtension != null;
    }

    public RenameOptionsBuilder setStartPath(String value) {
        this.startPath = value;
        return this;
    }

    public String getStartPath() {
        return this.startPath;
    }

    public RenameOptionsBuilder setInputExtension(String value) {
        this.inputExtension = value;
        return this;
    }

    public String getInputExtension() {
        return this.inputExtension;
    }

    public RenameOptionsBuilder setOutputExtension(String value) {
        this.outputExtension = value;
        return this;
    }

    public String getOutputExtension() {
        return this.outputExtension;
    }

    public boolean isRecusive() {
        return isRecusive;
    }

    public RenameOptionsBuilder setRecursive(boolean value) {
        this.isRecusive = value;
        return this;
    }

    public boolean isDryRun() {
        return isDryRun;
    }

    public RenameOptionsBuilder setDryRun(boolean dryRun) {
        isDryRun = dryRun;
        return this;
    }
}
