(ns nlp.text.tokenize
  (:require [clojure.string :as str]))

(defn tokenize
  "Splits a string into a sequence of lowercase tokens,
   removing non-alphanumeric characters and splitting by whitespace.
   Empty strings are removed from the final sequence."
  [text]
  (let [tokens (-> text
                   str/lower-case
                   (str/replace #"[^a-zA-Z0-9]+" " ")
                   str/trim
                   (str/split #"\s+"))]
    (remove empty? tokens)))

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
