(ns nlp.text.tokenize
  (:require [clojure.string :as str]))

(defn tokenize
  "Split text into tokens by whitespace."
  [text]
  (str/split text #"\s+"))

(defn ngrams
  "Generate ngrams (seqs of tokens) from tokens."
  [tokens n]
  (partition n 1 tokens))

(defn char-ngrams
  "Generate character ngrams from text."
  [text n]
  (partition n 1 text))

(defn tokenize-sentences
  "Naive sentence tokenizer by punctuation."
  [text]
  (->> (str/split text #"[.!?]")
       (map str/trim)
       (remove empty?)))
