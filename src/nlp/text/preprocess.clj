(ns nlp.text.preprocess
  (:require [clojure.string :as str]
            [nlp.text.tokenize :as tokenize-ns]))

; Stopwords: example English subset
(def stopwords #{"a" "an" "the" "and" "or" "but" "if" "while" "of" "at" "by" "for" "with" "about" "against" "between" "into" "through" "during" "before" "after" "above" "below" "to" "from" "up" "down" "in" "out" "on" "off" "over" "under" "again" "further" "then" "once"})


(defn normalize
  "Lowercase and remove punctuation, extra spaces."
  [text]
  (-> text
      str/lower-case
      (str/replace #"[^a-z\s]" "")
      (str/replace #"\s+" " ")
      str/trim))


(defn remove-stopwords
  "Remove stopwords from tokens."
  [tokens]
  (remove stopwords tokens))


;; basic preprocess pipeline
(defn preprocess
  "Normalize, tokenize, remove stopwords"
  [text]
  (->> text
       (normalize)
       (tokenize-ns/tokenize)
       (remove-stopwords)
       (remove empty?)))