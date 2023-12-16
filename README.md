# Word Statistics GUI - Documentation for Computer Science Students

## Table of Contents
1. [Introduction](#introduction)
2. [Overview](#overview)
3. [Project Structure](#project-structure)
    - [Main Class](#main-class)
    - [Initialization](#initialization)
    - [Event Handling](#event-handling)
    - [File Processing](#file-processing)
    - [Swing Component Updates](#swing-component-updates)
4. [Understanding Multi-threading](#multithreading)
5. [Conclusion](#conclusion)

---

## Introduction<a name="introduction"></a>

Welcome to the documentation for the Word Statistics GUI, a Java application designed for analyzing text files within a specified directory. This documentation aims to help computer science students understand the structure, functionality, and key components of the program.

## Overview<a name="overview"></a>

The Word Statistics GUI employs a graphical user interface (GUI) built using Java's Swing library to facilitate the analysis of text files. It utilizes a multi-threaded approach to concurrently process files in the specified directory, providing users with detailed statistics such as word counts, occurrences of specific words, and the longest and shortest words.

## Problem Breakdown

### Requirements:

- Main thread identifies text files in directory and its sub-directory.
- Each thread should work on exactly one text files. i.e. There must be 10 different threads for 10 different files.

- Integrate a GUI with the following:
    - **Input:**
        - Directory path (or selection via browse button)
        - Checkbox for including sub-directories.
    - **Output:**
        - **Files:** Filename with extension only.
        - **Thread ID:** ID of the thread working on the file at hand. i.e. 45.
        - **#words:** word count per file.
        - **#is:** "is" count per file.
        - **#are:** "are" count per file.
        - **#you:** "you" count per file.
        - **Longest:** Longest word per directories.
        - **Shortest:** Shortest word per directories.
- Identify the longest overall word.
- Identify the shortest overall word.
- Each thread should send updates to GUI:
    - Send the computed statistics (word count, is count, are count, you count, longest word, shortest word) back to the main GUI thread to update the display.
    - Send (Longest Overall Word, Shortest Overall Word) with those labels back to the main GUI thread to update the displayز

## Project Structure<a name="project-structure"></a>

### Main Class<a name="main-class"></a>

- **WordStatisticsGUI**: This class extends `JFrame` and serves as the main entry point for the application. It initializes the GUI components, handles user input, and orchestrates file processing.

### Initialization<a name="initialization"></a>

- **initUI()**: The method responsible for initializing the GUI components, layout, and event listeners.
- **browseButtonClicked()**: Handles the event when the "Browse" button is clicked, allowing users to select a directory.
- **processButtonClicked()**: Handles the event when the "Start Processing" button is clicked, initiating the file analysis.

### Event Handling<a name="event-handling"></a>

- **DocumentListener**: Monitors changes to the directory input field, enabling or disabling the "Start Processing" button accordingly.

### File Processing<a name="file-processing"></a>

- **processFile(Path filePath)**: Processes a single file, including reading content, counting words, finding the longest and shortest words, and updating overall statistics.
- **readContentFromFile(Path filePath)**: Reads content from a file specified by its path.
- **splitContentIntoWords(String content)**: Splits the content into an array of words.
- **countWords(String[] words)**: Counts the number of words in an array.
- **findLongestWord(String[] words)**: Finds the longest word in an array.
- **findShortestWord(String[] words)**: Finds the shortest word in an array.

### Swing Component Updates<a name="swing-component-updates"></a>

- **updateOverallWordStatistics(String localLongestWord, String localShortestWord)**: Updates the overall longest and shortest words based on local statistics.
- **updateSwingComponents(Path filePath, long threadId, long wordCount, String[] words)**: Updates Swing components, such as the result table and labels, on the Event Dispatch Thread.
- **countOccurrences(String[] words, String target)**: Counts the occurrences of a specific word in an array.
- **updateLabels()**: Updates the overall longest and shortest word labels.

## Multithreading<a name="multithreading"></a>

The Word Statistics GUI utilizes multithreading to process multiple files concurrently, enhancing the program's efficiency. The `ExecutorService` is employed to manage a thread pool, allowing the application to submit tasks for each file. This asynchronous approach ensures responsive user interactions while efficiently utilizing system resources.

## Conclusion<a name="conclusion"></a>

This Word Statistics GUI project provides a comprehensive example of a Java application with a graphical user interface and multithreading capabilities. By exploring the code, computer science students can gain insights into GUI development, event handling, file processing, and the effective use of multithreading for improved performance. The project encourages hands-on exploration and experimentation, making it an ideal learning resource for students interested in Java programming and GUI development.
