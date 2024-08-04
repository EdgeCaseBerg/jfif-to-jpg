package space.peetseater.rename.gui;

import space.peetseater.rename.Input;
import space.peetseater.rename.gui.workers.ConditionalConsumerWorker;
import space.peetseater.rename.gui.workers.DryRunWorker;
import space.peetseater.rename.gui.workers.RenameWorker;
import space.peetseater.rename.gui.workers.WorkerStatusFrame;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.util.*;

public class GraphicalUserInterfaceRunner implements Runnable, ActionListener, ListSelectionListener, CaretListener {

    private final RenameOptionsBuilder options;
    private ExtensionsList extensionsList;
    private JButton cancel;
    private JButton goButton;
    private JLabel instructionText;
    private JPanel rightPanel;
    private JPanel leftPanel;
    private JTextField extensionToBecomeInput;
    private JCheckBox isRecursiveCheckbox;
    private JCheckBox isDryRunCheckbox;

    public GraphicalUserInterfaceRunner(String[] args) {
        this.options = new RenameOptionsBuilder();

        String inPath = Input.getPath(args).orElse(".");
        String startingInputText = Input.getOutExtension(args).orElse("jpg");
        boolean isRecursive = Input.getRecursive(args).orElse(false);
        boolean isDryRun = Input.getDryRunEnabled(args).orElse(true);

        this.options
            .setStartPath(inPath)
            .setOutputExtension(startingInputText)
            .setRecursive(isRecursive)
            .setDryRun(isDryRun);
    }

    @Override
    public void run() {
        setLookAndFeel();
        JFrame frame = setupMainFrame();
        addDataToFrame();
        setupActions();
        frame.setVisible(true);
    }

    private void setupActions() {
        cancel.addActionListener(this);
        isRecursiveCheckbox.addActionListener(this);
        isDryRunCheckbox.addActionListener(this);
        extensionsList.addListSelectionListener(this);
        goButton.addActionListener(this);
        extensionToBecomeInput.addCaretListener(this);
    }

    private void addDataToFrame() {
        extensionsList.calculateExtensions(Paths.get(this.options.getStartPath()));
        extensionToBecomeInput.setText(this.options.getOutputExtension());
        isRecursiveCheckbox.setSelected(this.options.isRecusive());
        isDryRunCheckbox.setSelected(this.options.isDryRun());
    }

    public void setLookAndFeel() {
        LinkedList<String> preference = new LinkedList<>();
        preference.add("Nimbus");
        preference.add("Windows");
        preference.add("Metal");
        PreferredLookAndFeel preferredLookAndFeel = new PreferredLookAndFeel(preference);
        preferredLookAndFeel.apply();
    }

    private JFrame setupMainFrame() {
        JFrame frame = new JFrame("Bulk Extension Rename");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addInstructionsToFrame(frame);
        addLeftPanelContents(frame);
        addRightPanelContents(frame);
        addCancelAndGoButtonToFrame(frame);
        positionComponentsInFrame(frame);
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(new Dimension(640,320));
        frame.pack();
        return frame;
    }

    private void positionComponentsInFrame(JFrame frame) {
        GridBagLayout gridBagLayout = new GridBagLayout();
        frame.setLayout(gridBagLayout);
        // The grid bag is 5 tall, 8 wide.

        // instructions span the whole top part
        GridBagConstraints instructionConstraints = new GridBagConstraints();
        instructionConstraints.anchor = GridBagConstraints.LINE_START;
        instructionConstraints.gridx = 0;
        instructionConstraints.gridy = 0;
        instructionConstraints.gridwidth = 8;
        instructionConstraints.gridheight = 1;
        instructionConstraints.insets = new Insets(10,10,10,10);
        instructionConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagLayout.setConstraints(instructionText, instructionConstraints);

        // Left panel is the extensions list and its status text
        GridBagConstraints leftPanelConstraints = new GridBagConstraints();
        leftPanelConstraints.gridheight = 3;
        leftPanelConstraints.gridwidth = 4;
        leftPanelConstraints.gridx = 0;
        leftPanelConstraints.gridy = 1;
        leftPanelConstraints.insets = new Insets(10,10,10,10);
        gridBagLayout.setConstraints(leftPanel, leftPanelConstraints);

        // Right panel is for all the controls and input
        GridBagConstraints rightPanelConstraints = new GridBagConstraints();
        rightPanelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        rightPanelConstraints.gridx = 4;
        rightPanelConstraints.gridy = 1;
        rightPanelConstraints.gridheight = 2;
        rightPanelConstraints.gridwidth = 4;
        rightPanelConstraints.insets = new Insets(10,10,10,10);
        gridBagLayout.setConstraints(rightPanel, rightPanelConstraints);

        // Cancel Button is on the bottom right
        GridBagConstraints cancelConstraints = new GridBagConstraints();
        cancelConstraints.anchor = GridBagConstraints.LINE_START;
        cancelConstraints.gridx = 4;
        cancelConstraints.gridy = 3;
        cancelConstraints.gridwidth = 2;
        cancelConstraints.gridheight = 1;
        cancelConstraints.insets = new Insets(10,10,10,10);
        gridBagLayout.setConstraints(cancel, cancelConstraints);

        GridBagConstraints goConstraints = new GridBagConstraints();
        goConstraints.anchor = GridBagConstraints.LINE_START;
        goConstraints.gridy = 3;
        goConstraints.gridx = 6;
        goConstraints.gridwidth = 2;
        goConstraints.gridheight = 1;
        goConstraints.insets = new Insets(10,10,10,10);
        gridBagLayout.setConstraints(goButton, goConstraints);
    }

    private void addInstructionsToFrame(JFrame frame) {
        instructionText = new JLabel("Select the extension to be updated from the list below.\nThen choose your options and press go.");
        instructionText.setAlignmentX(Component.LEFT_ALIGNMENT);
        frame.add(instructionText);
    }

    private void addLeftPanelContents(JFrame frame) {
        ArrayList<String> initialExtensions = new ArrayList<>();
        if (this.options.getInputExtension() != null) {
            initialExtensions.add(this.options.getInputExtension());
        }
        extensionsList = new ExtensionsList(initialExtensions);
        leftPanel = extensionsList.getPanel();
        frame.add(leftPanel);
    }

    private void addRightPanelContents(JFrame frame) {
        rightPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS);
        rightPanel.setLayout(boxLayout);
        frame.add(rightPanel);

        JLabel textInputLabel = new JLabel("Extension to become");
        rightPanel.add(textInputLabel);

        extensionToBecomeInput = new JTextField(8);
        rightPanel.add(extensionToBecomeInput);

        isRecursiveCheckbox = new JCheckBox("Rename all files in subfolders");
        rightPanel.add(isRecursiveCheckbox);

        isDryRunCheckbox = new JCheckBox("Dry run, see which files will be affected");
        rightPanel.add(isDryRunCheckbox);
    }

    private void addCancelAndGoButtonToFrame(JFrame frame) {
        cancel = new JButton("Cancel");
        frame.add(cancel);

        goButton = new JButton("Go");
        goButton.setEnabled(this.options.canRun());
        frame.add(goButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (cancel.equals(e.getSource())) {
            System.exit(0);
        }

        if (isDryRunCheckbox.equals(e.getSource())) {
            this.options.setDryRun(isDryRunCheckbox.isSelected());
        }

        if (isRecursiveCheckbox.equals(e.getSource())) {
            this.options.setRecursive(isRecursiveCheckbox.isSelected());
        }

        if (extensionsList.getSelectedListItem() != null) {
            this.options.setInputExtension(extensionsList.getSelectedListItem());
        }

        goButton.setEnabled(this.options.canRun());

        if (e.getSource().equals(goButton)) {
            if (!options.canRun()) {
                return;
            }

            ConditionalConsumerWorker worker = getWorker();
            WorkerStatusFrame workerStatusFrame = new WorkerStatusFrame(worker);
            workerStatusFrame.run();
        }
    }

    protected ConditionalConsumerWorker getWorker() {
        ConditionalConsumerWorker worker = new RenameWorker(options, extensionsList.getFilesSeen());
        if (options.isDryRun()) {
            worker = new DryRunWorker(options, extensionsList.getFilesSeen());
        }
        return worker;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        String inExtension = extensionsList.getSelectedListItem();
        if (inExtension != null && !inExtension.isBlank()) {
            this.options.setInputExtension(inExtension);
        }
        goButton.setEnabled(this.options.canRun());
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        String text = extensionToBecomeInput.getText();
        if (text != null && !text.isBlank()) {
            this.options.setOutputExtension(text);
        } else {
            this.options.setOutputExtension(null);
        }
        goButton.setEnabled(this.options.canRun());
    }
}
