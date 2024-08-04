package space.peetseater.rename.gui;

import space.peetseater.rename.gui.workers.ConditionalConsumerWorker;
import space.peetseater.rename.gui.workers.WorkerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class WorkerStatusFrame implements WorkerListener<String>, PropertyChangeListener, ActionListener {
    private final ConditionalConsumerWorker worker;
    private final JFrame jFrame;
    private JProgressBar jProgressBar;
    private JButton closeButton;
    private DefaultListModel<String> renamedFilesListing;
    private JScrollPane jScrollPane;

    public WorkerStatusFrame(ConditionalConsumerWorker worker) {
        this.worker = worker;
        jFrame = new JFrame("Status: %s".formatted(worker.getWorkerName()));
        setupFrame();
        jFrame.setVisible(true);
    }

    protected void setupFrame() {
        jFrame.setLayout(new BorderLayout());
        jFrame.setPreferredSize(new Dimension(680, 420));
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        jProgressBar = new JProgressBar(0, 100);
        jProgressBar.setStringPainted(true);

        renamedFilesListing = new DefaultListModel<>();
        JList<String> jList = new JList<String>(renamedFilesListing);
        jList.setEnabled(false);
        jScrollPane = new JScrollPane(jList);

        closeButton = new JButton("Close");

        jFrame.add(jProgressBar, BorderLayout.PAGE_START);
        jFrame.add(jScrollPane, BorderLayout.CENTER);
        jFrame.add(closeButton, BorderLayout.PAGE_END);
        jFrame.pack();

        worker.addListener(this);
        worker.addPropertyChangeListener(this);
        worker.getPropertyChangeSupport().addPropertyChangeListener("progress", this);
        closeButton.addActionListener(this);
    }

    @Override
    public void onChunk(List<String> chunk) {
        renamedFilesListing.addAll(chunk);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            jProgressBar.setValue((int)evt.getNewValue());
        }

        if ("state".equals(evt.getPropertyName())) {
            if (SwingWorker.StateValue.DONE.equals(evt.getNewValue())) {
                SwingUtilities.invokeLater(() -> {
                    jProgressBar.setValue(100);
                    jScrollPane.getHorizontalScrollBar().setValue(jScrollPane.getHorizontalScrollBar().getMaximum());
                });
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!e.getSource().equals(closeButton)) {
            return;
        }

        jFrame.setVisible(false);
        worker.cancel(true);
    }

    public void run() {
        if (worker.canExecute()) {
            worker.execute();
        }
    }

}
