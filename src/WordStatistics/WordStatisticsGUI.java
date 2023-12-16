package WordStatistics;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class WordStatisticsGUI extends JFrame {
    // GUI components
    private JTextField directoryField;
    private JCheckBox includeSubdirectoriesCheckbox;
    private JTable resultTable;
    private ExecutorService executorService;
    private final Object lock;
    private String overallLongestWord;
    private String overallShortestWord;

    public WordStatisticsGUI() {
        // Initialize instance variables
        this.overallShortestWord = "";
        this.overallLongestWord = "";
        this.lock = new Object();
        // Set up the GUI
        initUI();
    }

    private void initUI() {
        // Set basic JFrame properties
        setTitle("Word Statistics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create and configure input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel directoryLabel = new JLabel("Directory Path:");
        inputPanel.add(directoryLabel);

        directoryField = new JTextField(20);
        inputPanel.add(directoryField);

        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseButtonClicked());
        inputPanel.add(browseButton);

        includeSubdirectoriesCheckbox = new JCheckBox("Include Subdirectories");
        inputPanel.add(includeSubdirectoriesCheckbox);

        JButton processButton = new JButton("Start Processing");
        processButton.addActionListener(e -> processButtonClicked());
        inputPanel.add(processButton);

        add(inputPanel, BorderLayout.NORTH);

        // Create and configure result table
        resultTable = new JTable(new DefaultTableModel(
                new Object[][]{},
                new Object[]{"Files", "Thread ID", "#Words", "#is", "#are", "#you", "Longest", "Shortest"}
        ));

        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create and configure labels panel
        JLabel overallLongestLabel = new JLabel("Longest Overall Word: ");
        JLabel overallShortestLabel = new JLabel("Shortest Overall Word: ");

        JPanel labelsPanel = new JPanel(new FlowLayout());
        labelsPanel.add(overallLongestLabel);
        labelsPanel.add(overallShortestLabel);

        add(labelsPanel, BorderLayout.SOUTH);

        // Set JFrame size and location
        setSize(800, 300);
        setLocationRelativeTo(null);

        // Initialize executor service with the number of available processors
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Disable the processButton initially
        processButton.setEnabled(false);

        // Add a document listener to the directoryField for enabling/disabling the processButton
        directoryField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButtonState();
            }

            private void updateButtonState() {
                processButton.setEnabled(!directoryField.getText().trim().isEmpty());
            }
        });
    }

    // Event handler for the browseButton
    private void browseButtonClicked() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            directoryField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    // Event handler for the processButton
    private void processButtonClicked() {
        // Get the directory path from the input field
        String directoryPath = directoryField.getText();
        if (directoryPath.isEmpty()) {
            // Show an error message if the directory path is empty
            JOptionPane.showMessageDialog(this, "Please enter a valid directory path.");
            return;
        }

        // Clear the result table
        resultTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new Object[]{"Files", "Thread ID", "#Words", "#is", "#are", "#you", "Longest", "Shortest"}
        ));

        // Reset overall word statistics
        overallLongestWord = "";
        overallShortestWord = "";

        // Check if subdirectories should be included in the search
        boolean includeSubdirectories = includeSubdirectoriesCheckbox.isSelected();

        try {
            // Walk through the directory and submit tasks for each file to the executor service
            Files.walk(Paths.get(directoryPath), includeSubdirectories ? Integer.MAX_VALUE : 1)
                    .filter(path -> path.toFile().isFile())
                    .forEach(path -> executorService.submit(() -> processFile(path)));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while processing files: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Process a file and update the result table
    private void processFile(Path filePath) {
        long threadId = Thread.currentThread().threadId();

        try {
            // Read content from the file
            String content = readContentFromFile(filePath);
            // Split content into words
            String[] words = splitContentIntoWords(content);

            // Count words and find longest and shortest words
            long wordCount = countWords(words);
            String localLongestWord = findLongestWord(words);
            String localShortestWord = findShortestWord(words);

            // Update overall word statistics
            updateOverallWordStatistics(localLongestWord, localShortestWord);

            // Update Swing components on the Event Dispatch Thread
            updateSwingComponents(filePath, threadId, wordCount, words);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while processing files: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Read content from a file
    private String readContentFromFile(Path filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            return contentBuilder.toString();
        }
    }

    // Split content into words
    private String[] splitContentIntoWords(String content) {
        return content.split("\\s+");
    }

    // Count the number of words
    private long countWords(String[] words) {
        return words.length;
    }

    // Find the longest word in an array of words
    private String findLongestWord(String[] words) {
        return Arrays.stream(words)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

    // Find the shortest word in an array of words
    private String findShortestWord(String[] words) {
        return Arrays.stream(words)
                .min(Comparator.comparingInt(String::length))
                .orElse("");
    }

    // Update overall word statistics with local statistics
    private void updateOverallWordStatistics(String localLongestWord, String localShortestWord) {
        synchronized (lock) {
            if (localLongestWord.length() > overallLongestWord.length()) {
                overallLongestWord = localLongestWord;
            }
            if (overallShortestWord.isEmpty() || localShortestWord.length() < overallShortestWord.length()) {
                overallShortestWord = localShortestWord;
            }
        }
    }

    // Update Swing components with file statistics
    private void updateSwingComponents(Path filePath, long threadId, long wordCount, String[] words) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
            model.addRow(new Object[]{filePath.getFileName(), threadId,
                    wordCount, countOccurrences(words, "is"), countOccurrences(words, "are"),
                    countOccurrences(words, "you"), findLongestWord(words), findShortestWord(words)});

            // Update labels at the bottom of the frame
            updateLabels();
        });
    }

    // Count occurrences of a target word in an array of words
    private int countOccurrences(String[] words, String target) {
        return (int) Arrays.stream(words)
                .filter(word -> word.equals(target))
                .count();
    }

    // Update the overall longest and shortest word labels
    private void updateLabels() {
        SwingUtilities.invokeLater(() -> {
            JLabel overallLongestLabel = (JLabel) ((JPanel) getContentPane().getComponent(2)).getComponent(0);
            JLabel overallShortestLabel = (JLabel) ((JPanel) getContentPane().getComponent(2)).getComponent(1);

            overallLongestLabel.setText("Longest Overall Word: " + overallLongestWord);
            overallShortestLabel.setText("Shortest Overall Word: " + overallShortestWord);
        });
    }

    // Entry point of the application
    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            WordStatisticsGUI wordStatisticsGUI = new WordStatisticsGUI();
            wordStatisticsGUI.setVisible(true);
        });
    }
}