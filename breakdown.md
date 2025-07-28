
# Project Breakdown

This document provides a breakdown of the Clojure NLP project, explaining the different modules, their functionalities, and highlighting idiomatic Clojure and functional programming concepts.

## Project Overview

This project is a collection of Natural Language Processing (NLP) tools written in Clojure. It provides a command-line interface (CLI) to access various NLP functionalities, including text preprocessing, vectorization, distance and similarity metrics, classification, clustering, spellchecking, and summarization.

## File Structure

The project is organized into the following main directories:

- **`src/nlp`**: Contains the core source code for the NLP functionalities.
- **`test/nlp`**: Contains tests for the NLP functionalities.

### Core Modules (`src/nlp`)

- **`cli.clj`**: The main entry point for the command-line interface. It uses `clojure.tools.cli` to parse command-line arguments and dispatch to the appropriate NLP functions.
- **`metrics/`**:
    - **`distance.clj`**: Implements various distance metrics for strings and vectors, such as Levenshtein, Hamming, Manhattan, Euclidean, and others.
    - **`similarity.clj`**: Implements similarity metrics like Cosine Similarity, Jaccard Similarity, and Dice Coefficient.
- **`models/`**:
    - **`classification.clj`**: Implements a Naive Bayes classifier.
    - **`clustering.clj`**: Implements a K-Means clustering algorithm.
    - **`spellcheck.clj`**: A simple spellchecker using Levenshtein distance.
    - **`summarize.clj`**: An extractive text summarizer.
- **`text/`**:
    - **`preprocess.clj`**: Functions for text preprocessing, including normalization (lowercase, punctuation removal) and stopword removal.
    - **`tokenize.clj`**: Functions for tokenizing text into words, n-grams, and sentences.
    - **`vectorize.clj`**: Functions for converting text into numerical vectors, such as TF-IDF, one-hot encoding, and count vectors.
- **`utils/`**:
    - **`files.clj`**: Utility functions for file operations, such as reading file content.
    - **`utils.clj`**: General utility functions.

## Idiomatic Clojure and Functional Programming

This project demonstrates several idiomatic features of Clojure and functional programming:

- **Immutability**: All data structures are immutable. Functions do not modify their inputs but instead return new, transformed data structures. This is a core principle of functional programming that leads to more predictable and thread-safe code.
- **Higher-Order Functions**: The code makes extensive use of higher-order functions like `map`, `reduce`, `filter`, and `fn`. These functions take other functions as arguments, allowing for concise and expressive data manipulation. For example, in `cli.clj`, the `get-text` function is a higher-order function that either reads from a file or returns the argument directly.
- **Pure Functions**: Many functions in the project are pure, meaning their output depends only on their input, and they have no side effects. This makes them easier to reason about and test. For example, the distance and similarity metric functions are pure.
- **Data-Oriented Programming**: The project follows a data-oriented approach, where the logic is driven by data transformations. The core data structures are simple maps and vectors, which are manipulated by a series of functions.
- **Namespaces and `require`**: The code is organized into namespaces, which helps to avoid naming conflicts and promotes modularity. The `require` function is used to load and reference other namespaces.
- **The `->` (Thread-First) and `->>`(Thread-Last) Macros**: The thread-first macro is used extensively to create clean and readable data processing pipelines. For example, in `preprocess.clj`:
  ```clojure
  (->> text
       (normalize)
       (tokenize-ns/tokenize)
       (remove-stopwords)
       (remove empty?)))
  ```
  This is equivalent to `(remove-stopwords (tokenize-ns/tokenize (normalize text)))`, but is much easier to read from left-to-right and top-to-bottom.
- **Destructuring**: Destructuring is used to bind names to parts of data structures, making the code more concise. For example, in `cli.clj`:
  ```clojure
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)]
    ...)
  ```
