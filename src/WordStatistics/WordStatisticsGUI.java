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
    private JTextField directoryField;
    private JCheckBox includeSubdirectoriesCheckbox;
    private JTable resultTable;
    private ExecutorService executorService;
    private final Object lock;
    private String overallLongestWord;
    private String overallShortestWord;

    public WordStatisticsGUI() {
        this.overallShortestWord = "";
        this.overallLongestWord = "";
        this.lock = new Object();
        initUI();
    }

    private void initUI() {
        setTitle("Word Statistics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

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

        resultTable = new JTable(new DefaultTableModel(
                new Object[][]{},
                new Object[]{"Files", "Thread ID", "#Words", "#is", "#are", "#you", "Longest", "Shortest"}
        ));

        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        JLabel overallLongestLabel = new JLabel("Longest Overall Word: ");
        JLabel overallShortestLabel = new JLabel("Shortest Overall Word: ");

        JPanel labelsPanel = new JPanel(new FlowLayout());
        labelsPanel.add(overallLongestLabel);
        labelsPanel.add(overallShortestLabel);

        add(labelsPanel, BorderLayout.SOUTH);

        setSize(800, 300);
        setLocationRelativeTo(null);

        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        processButton.setEnabled(false);

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

    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }


    private class FileProcessor implements Runnable {
        private Path filePath;

        public FileProcessor(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            long threadId = Thread.currentThread().threadId();

            try {
                String content = readContentFromFile(filePath);
                String[] words = splitContentIntoWords(content);

                long wordCount = countWords(words);
                String localLongestWord = findLongestWord(words);
                String localShortestWord = findShortestWord(words);

                updateOverallWordStatistics(localLongestWord, localShortestWord);

                updateSwingComponents(filePath, threadId, wordCount, words);
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog("Error reading file: " + e.getMessage());
            }
        }
    }

    private void browseButtonClicked() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            directoryField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void processButtonClicked() {
        String directoryPath = directoryField.getText();
        if (directoryPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid directory path.");
            return;
        }

        resultTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new Object[]{"Files", "Thread ID", "#Words", "#is", "#are", "#you", "Longest", "Shortest"}
        ));

        overallLongestWord = "";
        overallShortestWord = "";

        boolean includeSubdirectories = includeSubdirectoriesCheckbox.isSelected();

        try {
            Files.walk(Paths.get(directoryPath), includeSubdirectories ? Integer.MAX_VALUE : 1)
                    .filter(path -> path.toFile().isFile())
                    .forEach(path -> {
                        executorService.submit(new FileProcessor(path));
                    });
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error reading file: " + e.getMessage());
        }
    }

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

    private String[] splitContentIntoWords(String content) {
        return content.split("\\s+");
    }

    private long countWords(String[] words) {
        return words.length;
    }

    private String findLongestWord(String[] words) {
        return Arrays.stream(words)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

    private String findShortestWord(String[] words) {
        return Arrays.stream(words)
                .min(Comparator.comparingInt(String::length))
                .orElse("");
    }

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

    private void updateSwingComponents(Path filePath, long threadId, long wordCount, String[] words) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
            model.addRow(new Object[]{filePath.getFileName(), threadId,
                    wordCount, countOccurrences(words, "is"), countOccurrences(words, "are"),
                    countOccurrences(words, "you"), findLongestWord(words), findShortestWord(words)});

            updateLabels();
        });
    }

    private int countOccurrences(String[] words, String target) {
        return (int) Arrays.stream(words)
                .filter(word -> word.equals(target))
                .count();
    }

    private void updateLabels() {
        SwingUtilities.invokeLater(() -> {
            JLabel overallLongestLabel = (JLabel) ((JPanel) getContentPane().getComponent(2)).getComponent(0);
            JLabel overallShortestLabel = (JLabel) ((JPanel) getContentPane().getComponent(2)).getComponent(1);

            overallLongestLabel.setText("Longest Overall Word: " + overallLongestWord);
            overallShortestLabel.setText("Shortest Overall Word: " + overallShortestWord);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordStatisticsGUI wordStatisticsGUI = new WordStatisticsGUI();
            wordStatisticsGUI.setVisible(true);
        });
    }
}
