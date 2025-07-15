
(ns text-distance.preprocess
  (:require [clojure.string :as str]))

(def stopwords #{"the" "is" "in" "at" "of" "and" "a" "to" "on" "for" "with" "this" "that"})

; normalize text (lower case)
(defn normalize [text]
  (-> text
      str/lower-case
      (str/replace #"[^a-z\s]" "")  ; keep only letters + whitespace
      (str/replace #"\s+" " ")
      str/trim))

; tokenize text
(defn tokenize [text]
  (str/split text #"\s+"))

(defn remove-stopwords [tokens]
  (remove stopwords tokens))

(defn preprocess [text]
  (-> text
      normalize
      tokenize
      remove-stopwords))
