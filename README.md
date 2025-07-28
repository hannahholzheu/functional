# NLP - Text Distance Calculation

Calculate the distance of two texts.

## Current functionalities

* Tokenize input text 
* Preprocess text 
* Vectorize input text (with different functions such as tf-idf or one-hot)
* Calculate distance between text snippets (different distance measures, string based and vector based)
* Calculate similarity between text snippets (e.g. Cosine similarity)
* Classify text using naive bayes classification 
* Cluster words using K-Means algorithm 
* Spellcheck words using Levenshtein distance and a dictionary 
* Summarize text by returning sentences most relevant to the document (based on TF-IDF)

## Installation

Install the Clojure CLI, following the [documentation](https://clojure.org/guides/install_clojure).

Clone the repository:
```bash
git clone https://github.com/hannahholzheu/functional.git
cd functional
```

## Usage

Within a terminal, navigate to the project root directory.

Run:
```bash
clj -M -m nlp.cli --help # display help 
```

### Read files 

Most of the functions support reading files as input instead of passing the texts directly in the command. Usage:

```bash
clj -M -m nlp.cli -f --<function> "file.txt" # works with relative and absolute paths 
```

## Run Tests

Run the tests with the following command:
```bash
clj -M:test
```

## Authors
- Hannah Holzheu
- Lara Blersch
- Lukas Karsch
