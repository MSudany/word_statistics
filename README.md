# WordStatisticsGUI Documentation

## Table of Contents
1. [Overview](#overview)
2. [Class Structure](#ClassStructure)
3. [GUI Components](#GUIComponents)
4. [Thread Management](#ThreadManagement)
5. [Error Handling](#ErrorHandling)
6. [Main Method](#MainMethod)
7. [Usage](#Usage)

---
## Overview<a name="Overview"></a>

WordStatisticsGUI is a Java Swing-based application developed for analyzing word statistics within text files located in a specified directory and its subdirectories. The program utilizes a multithreaded approach to process files concurrently and presents the results in a table with various word-related metrics. This documentation provides insights into the structure, components, and functionalities of the program.

## Class Structure<a name="ClassStructure"></a>

The main class, `WordStatisticsGUI`, extends `JFrame` and encapsulates the entire application. It consists of the following key components:

- **Fields:**
  - `directoryField`: JTextField for entering the directory path.
  - `includeSubdirectoriesCheckbox`: JCheckBox for including subdirectories in the analysis.
  - `resultTable`: JTable for displaying file-specific and overall word statistics.
  - `executorService`: ExecutorService for managing threads.
  - `lock`: Object used for synchronization.
  - `overallLongestWord` and `overallShortestWord`: Strings to store overall word statistics.

- **Methods:**
  - `initUI()`: Initializes the graphical user interface, including buttons, labels, and layout.
  - `browseButtonClicked()`: Displays a file chooser dialog to select a directory.
  - `processButtonClicked()`: Initiates file processing based on the selected directory.
  - `readContentFromFile(Path filePath)`: Reads the content of a file and returns it as a string.
  - Other utility methods for word processing and updating GUI components.

- **Inner Class:**
  - `FileProcessor`: Implements the `Runnable` interface for processing individual files concurrently. Calculates word statistics and updates the overall statistics and Swing components.

## GUI Components<a name="GUIComponents"></a>

The graphical user interface consists of text fields, buttons, labels, and a table. Key components include:
- `directoryField`: For entering the directory path.
- `includeSubdirectoriesCheckbox`: Checkbox to include subdirectories in the analysis.
- `resultTable`: Table for displaying file-specific and overall word statistics.
- `overallLongestLabel` and `overallShortestLabel`: Labels displaying overall longest and shortest words.

## Thread Management<a name="ThreadManagement"></a>

The application uses an `ExecutorService` to manage threads for concurrent file processing. Each file is processed by an instance of the `FileProcessor` class, which calculates word statistics and updates the overall statistics and Swing components.

## Error Handling<a name="ErrorHandling"></a>

The program includes error handling to display informative error dialogs in case of issues during file processing, such as file reading errors.

## Main Method<a name="MainMethod"></a>

The `main()` method serves as the entry point for the application. It launches the GUI on the Event Dispatch Thread using `SwingUtilities.invokeLater()`.

## Usage<a name="Usage"></a>

### 1. Run the program.
   
   ![image](https://github.com/MSudany/word_statistics/assets/93383768/25c5b332-ca40-422e-8701-562edb48a714)

### 2. Enter the directory path.

   ![image](https://github.com/MSudany/word_statistics/assets/93383768/3fc97d4b-7364-4586-8b7b-0d65fc6e6b41)

### 3. Optionally, check the "Include Subdirectories" checkbox.

   ![image](https://github.com/MSudany/word_statistics/assets/93383768/051c9e50-ce40-4342-a476-9fa3f88b5441)

### 4. Click "Start Processing" to analyze files in the specified directory.

   ![image](https://github.com/MSudany/word_statistics/assets/93383768/c26e0791-239f-407b-b884-29e0e7bc6048)

### 5. View the word statistics in the table, including the overall longest and shortest words.

   ![image](https://github.com/MSudany/word_statistics/assets/93383768/7da97161-f281-4ec3-ba98-a7736433494d)

## Note: Ensure that the Java Runtime Environment (JRE) is installed to execute the program successfully.
