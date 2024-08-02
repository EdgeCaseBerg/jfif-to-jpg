package space.peetseater.rename.gui;

import space.peetseater.rename.gui.workers.InputExtensionsListWorker;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ExtensionsList implements PropertyChangeListener {
    private final JList<String> jList;
    private final DefaultListModel<String> jModel;
    private final JLabel jLabel;
    private final String labelFormat = "%d Files Checked";
    private final JPanel jPanel;
    private InputExtensionsListWorker worker;
    int filesSeen = 0;

    public ExtensionsList(List<String> initialExtensions) {
        // TODO: consider making a list model that sorts itself as things as added
        this.jModel = new DefaultListModel<String>();
        this.jModel.addAll(initialExtensions);
        this.jList = new JList<String>(jModel);
        this.jLabel = new JLabel(labelFormat.formatted(0));
        this.jPanel = new JPanel();
        setupPanel();
    }

    private void setupPanel() {
        JScrollPane jScrollPane = new JScrollPane(jList);
        jScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));
        jPanel.add(jScrollPane);
        jPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        jPanel.add(jLabel);
    }

    public JPanel getPanel() {
        return this.jPanel;
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        jList.addListSelectionListener(listener);
    }

    public String getSelectedListItem() {
        return jList.getSelectedValue();
    }

    public void calculateExtensions(Path path) {
        if (worker != null) {
            worker.cancel(true);
        }
        worker = new InputExtensionsListWorker(path);
        worker.addPropertyChangeListener(this);
        worker.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!evt.getSource().equals(worker)) {
            return;
        }

        if (InputExtensionsListWorker.PROGRESS_PROPERTY_NAME.equals(evt.getPropertyName())) {
            updateLabelsFromProgressUpdate((InputExtensionsListWorker.PartialResult) evt.getNewValue());
        }

        if ("state".equals(evt.getPropertyName())) {
            SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
            updateLabelsIfDone(state);
        }
    }

    private void updateLabelsIfDone(SwingWorker.StateValue state) {
        if (!state.equals(SwingWorker.StateValue.DONE)) {
            return;
        }
        HashSet<String> extensionsSeen;
        try {
            extensionsSeen = worker.get();
            List<String> data = extensionsSeen.stream().sorted().toList();
            jModel.clear();
            jModel.addAll(data);
            jLabel.setText("Finished loading all extension.\nChecked %d files".formatted(filesSeen));
            worker = null;
        } catch (InterruptedException | ExecutionException e) {
            jLabel.setText(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void updateLabelsFromProgressUpdate(InputExtensionsListWorker.PartialResult partial) {
        filesSeen = partial.totalFilesChecked();
        jLabel.setText(labelFormat.formatted(partial.totalFilesChecked()));
        for (String ext : partial.extensionsSeen()) {
            if(!jModel.contains(ext)) {
                jModel.addElement(ext);
            }
        }
    }


}
